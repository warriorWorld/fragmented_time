package com.truthower.suhang.fragmentedtime.business.main;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.truthower.suhang.fragmentedtime.R;
import com.truthower.suhang.fragmentedtime.adapter.OnlineMangaRecyclerListAdapter;
import com.truthower.suhang.fragmentedtime.base.BaseFragment;
import com.truthower.suhang.fragmentedtime.bean.LoginBean;
import com.truthower.suhang.fragmentedtime.bean.MangaBean;
import com.truthower.suhang.fragmentedtime.business.detail.LocalMangaDetailsActivity;
import com.truthower.suhang.fragmentedtime.config.Configure;
import com.truthower.suhang.fragmentedtime.config.ShareKeys;
import com.truthower.suhang.fragmentedtime.listener.OnEditResultListener;
import com.truthower.suhang.fragmentedtime.listener.OnRecycleItemClickListener;
import com.truthower.suhang.fragmentedtime.listener.OnRecycleItemLongClickListener;
import com.truthower.suhang.fragmentedtime.listener.OnSevenFourteenListDialogListener;
import com.truthower.suhang.fragmentedtime.sort.FileComparatorByTime;
import com.truthower.suhang.fragmentedtime.spider.FileSpider;
import com.truthower.suhang.fragmentedtime.utils.DisplayUtil;
import com.truthower.suhang.fragmentedtime.utils.Logger;
import com.truthower.suhang.fragmentedtime.utils.SharedPreferencesUtils;
import com.truthower.suhang.fragmentedtime.widget.bar.TopBar;
import com.truthower.suhang.fragmentedtime.widget.dialog.ListDialog;
import com.truthower.suhang.fragmentedtime.widget.dialog.MangaDialog;
import com.truthower.suhang.fragmentedtime.widget.dialog.MangaEditDialog;
import com.truthower.suhang.fragmentedtime.widget.recyclerview.RecyclerGridDecoration;
import com.truthower.suhang.fragmentedtime.widget.wheelview.wheelselector.WheelSelectorDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class LocalMangaFragment extends BaseFragment implements
        EasyPermissions.PermissionCallbacks, OnRefreshListener, OnLoadMoreListener {
    private View emptyView;
    private View mainView;
    private ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();
    private TopBar topBar;
    private String storagePath;
    private String[] fileNameOptions = {"independent", "story", "other(edit)"};

    private OnlineMangaRecyclerListAdapter adapter;
    private RecyclerView mangaRcv;
    private SwipeToLoadLayout swipeToLoadLayout;

    private enum FileTypeEnum {
        Independent,
        Story
    }

    private WheelSelectorDialog optionsSelector;
    private final String INDEPENDENT_PATH = "Independent", STORY_PATH = "Story";
    private Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {
            switch (Integer.valueOf(msg.obj.toString())) {
                case 0:
                    MangaDialog dialog = new MangaDialog(getActivity());
                    dialog.show();
                    dialog.setTitle("文件移动完成!");
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.collect_manga_list, null);
        initUI(mainView);
        initGridView();

        initFilePath();
        initFile();
        if (!SharedPreferencesUtils.getBooleanSharedPreferencesData(getActivity(), ShareKeys.CLOSE_TUTORIAL, true)) {
            MangaDialog dialog = new MangaDialog(getActivity());
            dialog.show();
            dialog.setTitle("教程");
            dialog.setMessage("1,本应用所有列表页面均支持上下拉刷新" +
                    "\n2,本地漫画可通过长按删除");
        }
        return mainView;
    }

    private void initFilePath() {
        File parentPath = Environment
                .getExternalStorageDirectory();
        storagePath = parentPath.getAbsolutePath() + "/" + Configure.DST_FOLDER_NAME;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        try {
            if (!hidden) {
            }
        } catch (Exception e) {
            //这时候有可能fragment还没绑定上activity
        }
    }


    @AfterPermissionGranted(Configure.PERMISSION_FILE_REQUST_CODE)
    public void initFile() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            // Already have permission, do the thing
            // ...
            try {
                if (null != mangaList) {
                    mangaList.clear();
                }
                mangaList = FileSpider.getInstance().getMangaList(storagePath);
                initGridView();
            } catch (Exception e) {
                Logger.d(e + "");
            }
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "我们需要写入/读取权限",
                    Configure.PERMISSION_FILE_REQUST_CODE, perms);
        }
    }

    private void initGridView() {
        try {
            if (null == mangaList || mangaList.size() <= 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
            if (null == adapter) {
                adapter = new OnlineMangaRecyclerListAdapter(getActivity(), mangaList);
                adapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String currentMangaName = mangaList.get(position).getName();
                        Intent intent = new Intent(getActivity(), LocalMangaDetailsActivity.class);
                        intent.putExtra("filePath", mangaList.get(position).getUrl());
                        intent.putExtra("currentMangaName", currentMangaName);
                        startActivity(intent);
                    }
                });
                adapter.setOnRecycleItemLongClickListener(new OnRecycleItemLongClickListener() {
                    @Override
                    public void onItemLongClick(int position) {
                        showDeleteDialog(position);
                    }
                });
                mangaRcv.setAdapter(adapter);
                ColorDrawable dividerDrawable = new ColorDrawable(0x00000000) {
                    @Override
                    public int getIntrinsicHeight() {
                        return DisplayUtil.dip2px(getActivity(), 8);
                    }

                    @Override
                    public int getIntrinsicWidth() {
                        return DisplayUtil.dip2px(getActivity(), 8);
                    }
                };
                RecyclerGridDecoration itemDecoration = new RecyclerGridDecoration(getActivity(),
                        dividerDrawable, true);
                mangaRcv.addItemDecoration(itemDecoration);
            } else {
                adapter.setList(mangaList);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
        }
        swipeToLoadLayout.setRefreshing(false);
        swipeToLoadLayout.setLoadingMore(false);
    }


    private void initUI(View v) {
        swipeToLoadLayout = (SwipeToLoadLayout) v.findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        mangaRcv = (RecyclerView) v.findViewById(R.id.swipe_target);
        mangaRcv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mangaRcv.setFocusableInTouchMode(false);
        mangaRcv.setFocusable(false);
        mangaRcv.setHasFixedSize(true);
        emptyView = v.findViewById(R.id.empty_view);

        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initFile();
            }
        });

        topBar = (TopBar) v.findViewById(R.id.gradient_bar);
        topBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {

            }

            @Override
            public void onRightClick() {

            }

            @Override
            public void onTitleClick() {
                if (LoginBean.getInstance().isMaster()) {
//                    showOptionsSelector();
                    showOptionsSelectorDialog();
                }
            }
        });
    }


    private void sortAndRenameFile(String manganame) {
        String oldPath = storagePath + "/" + "download";
        String newPath = storagePath + "/" + manganame;
        String gifPath = storagePath + "/" + "GIFs";

        File f = new File(oldPath);
        File[] files = f.listFiles();
        ArrayList<File> fileArrayList = new ArrayList<File>();
        for (int i = 0; i < files.length; i++) {
            fileArrayList.add(files[i]);
        }
        Collections.sort(fileArrayList, new FileComparatorByTime());//通过重写Comparator的实现类

        //如果子目录不存在 建立一个子目录
        File folder = new File(newPath);
        if (!folder.exists()) {
            folder.mkdirs();
        } else {
            baseToast.showToast("该文件夹已存在,请重新命名!");
            return;
        }
        File gifFolder = new File(gifPath);
        if (!gifFolder.exists()) {
            gifFolder.mkdirs();
        }
        for (int i = 0; i < fileArrayList.size(); i++) {
            if (!fileArrayList.get(i).toString().contains("gif")) {
                File to = new File(newPath, manganame + "(" + i + ")" + ".jpg");

                fileArrayList.get(i).renameTo(to);
            } else {
                Long time = new Date().getTime();
                String timeString = time + "";
                timeString = timeString.substring(5);
                File to = new File(gifPath, "gif" + timeString + i + ".gif");

                fileArrayList.get(i).renameTo(to);
            }
        }
        baseToast.showToast("完成");
    }

    private void showOptionsSelector() {
        if (null == fileNameOptions || fileNameOptions.length == 0) {
            baseToast.showToast("没有筛选条件");
            return;
        }
        if (null == optionsSelector) {
            optionsSelector = new WheelSelectorDialog(getActivity());
            optionsSelector.setCancelable(true);
        }
        optionsSelector.setOnSingleSelectedListener(new WheelSelectorDialog.OnSingleSelectedListener() {

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes) {
            }

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes, String selectedTypeRes) {
            }

            @Override
            public void onOkBtnClick(int position) {
                String fileName;
                switch (position) {
                    case 0:
                        fileName = getFileName(FileTypeEnum.Independent);
                        sortAndRenameFile(fileName);
                        moveFolder(INDEPENDENT_PATH, fileName);
                        break;
                    case 1:
                        fileName = getFileName(FileTypeEnum.Story);
                        sortAndRenameFile(fileName);
                        moveFolder(STORY_PATH, fileName);
                        break;
                    case 2:
                        showSortAndRenameFilesDialog();
                        break;
                }
            }
        });
        optionsSelector.show();

        optionsSelector.initOptionsData(fileNameOptions);
    }

    private void showOptionsSelectorDialog() {
        ListDialog listDialog = new ListDialog(getActivity());
        listDialog.setOnSevenFourteenListDialogListener(new OnSevenFourteenListDialogListener() {
            @Override
            public void onItemClick(String selectedRes, String selectedCodeRes) {

            }

            @Override
            public void onItemClick(String selectedRes) {

            }

            @Override
            public void onItemClick(int position) {
                String fileName;
                switch (position) {
                    case 0:
                        fileName = getFileName(FileTypeEnum.Independent);
                        sortAndRenameFile(fileName);
                        moveFolder(INDEPENDENT_PATH, fileName);
                        break;
                    case 1:
                        fileName = getFileName(FileTypeEnum.Story);
                        sortAndRenameFile(fileName);
                        moveFolder(STORY_PATH, fileName);
                        break;
                    case 2:
                        showSortAndRenameFilesDialog();
                        break;
                }
            }
        });
        listDialog.show();
        listDialog.setOptionsList(fileNameOptions);
    }

    private void moveFolder(final String folderName, final String fileName) {
        new Thread() {
            @Override
            public void run() {
                FileSpider.getInstance().moveFile(storagePath + "/" + fileName + "/",
                        storagePath + "/" + folderName + "/" + fileName + "/");

                Message msg = handler2.obtainMessage();
                msg.obj = 0;
                msg.sendToTarget();
            }
        }.start();
    }

    private String getFileName(FileTypeEnum fileType) {
        try {
            String filePath = "";
            switch (fileType) {
                case Independent:
                    filePath = storagePath + "/" + INDEPENDENT_PATH;
                    break;
                case Story:
                    filePath = storagePath + "/" + STORY_PATH;
                    break;
                default:
                    break;
            }
            File f = new File(filePath);
            if (!f.exists()) {
                f.mkdirs();
            }
            File[] files = f.listFiles();
            if (null == files || files.length == 0) {
                switch (fileType) {
                    case Independent:
                        return INDEPENDENT_PATH + "0";
                    case Story:
                        return STORY_PATH + "0";
                    default:
                        return "";
                }
            } else {
                int[] fileNums = new int[files.length];
                String replaceString = "";
                switch (fileType) {
                    case Independent:
                        replaceString = INDEPENDENT_PATH;
                        break;
                    case Story:
                        replaceString = STORY_PATH;
                        break;
                }
                for (int i = 0; i < files.length; i++) {
                    String numString = files[i].getName();
                    numString = numString.replaceAll(replaceString, "");
                    fileNums[i] = Integer.valueOf(numString);
                }
                int fileNum = getMaxNum(fileNums) + 1;
                return replaceString + fileNum;
            }
        } catch (NumberFormatException e) {
            return "";
        }
    }

    private int getMaxNum(int[] nums) {
        int maxNum = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] > maxNum) {
                maxNum = nums[i];
            }
        }
        return maxNum;
    }

    private void showSortAndRenameFilesDialog() {
        MangaEditDialog mangaEditDialog = new MangaEditDialog(getActivity());
        mangaEditDialog.setOnEditResultListener(new OnEditResultListener() {
            @Override
            public void onResult(String text) {
                sortAndRenameFile(text);
            }

            @Override
            public void onCancelClick() {

            }
        });
        mangaEditDialog.show();
        mangaEditDialog.setTitle("是否按修改时间重新排序?");
        mangaEditDialog.setOkText("是的");
        mangaEditDialog.setCancelText("算了");
    }

    private void showDeleteDialog(final int i) {
        MangaDialog deleteDialog = new MangaDialog(getActivity());
        deleteDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                FileSpider.getInstance().deleteFile(mangaList.get(i).getUrl());
                initFile();
            }

            @Override
            public void onCancelClick() {

            }
        });
        deleteDialog.show();

        deleteDialog.setTitle("确定删除?");
        deleteDialog.setOkText("删除");
        deleteDialog.setCancelText("算了");
        deleteDialog.setCancelable(true);
    }


    @Override
    public void onLoadMore() {
        initFile();
    }

    @Override
    public void onRefresh() {
        initFile();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        baseToast.showToast("已获得授权,请继续!");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        baseToast.showToast("没文件读取/写入授权,你让我怎么读取本地漫画?", true);
    }
}
