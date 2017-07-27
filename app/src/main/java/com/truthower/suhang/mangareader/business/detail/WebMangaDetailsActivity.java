package com.truthower.suhang.mangareader.business.detail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.OnlineMangaDetailAdapter;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.business.download.DownloadActivity;
import com.truthower.suhang.mangareader.business.download.DownloadService;
import com.truthower.suhang.mangareader.business.read.ReadMangaActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.eventbus.DownLoadEvent;
import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.spider.SpiderBase;
import com.truthower.suhang.mangareader.utils.ServiceUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshBase;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshGridView;
import com.truthower.suhang.mangareader.widget.wheelview.wheelselector.WheelSelectorDialog;

import org.greenrobot.eventbus.Subscribe;

public class WebMangaDetailsActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener,
        PullToRefreshBase.OnRefreshListener<GridView> {
    private SpiderBase spider;
    private PullToRefreshGridView pullToRefreshGridView;
    private GridView mangaGV;
    private View collectV;
    private boolean chooseing = false;//判断是否在选择状态
    private boolean firstChoose = true;
    private int downloadStartPoint = 0;
    private MangaBean currentManga;
    private OnlineMangaDetailAdapter adapter;
    private ImageView thumbnailIV, downloadIv;
    private TextView mangaNameTv, mangaAuthorTv, mangaTypeTv, lastUpdateTv, downloadTagTv;
    private String[] optionsList = {"下载全部", "选择起始点下载"};
    private ProgressDialog loadBar;
    private WheelSelectorDialog optionsSelector;
    private String mangaUrl;
    private MangaDialog downloadDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mangaUrl = intent.getStringExtra("mangaUrl");
        if (TextUtils.isEmpty(mangaUrl)) {
            this.finish();
        }
        initSpider();

        initUI();
        initPullGridView();
        initProgressBar();
        initWebManga(mangaUrl);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initSpider() {
        try {
            spider = (SpiderBase) Class.forName
                    ("com.truthower.suhang.mangareader.spider." + Configure.currentWebSite + "Spider").newInstance();
        } catch (ClassNotFoundException e) {
            baseToast.showToast(e + "");
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            baseToast.showToast(e + "");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            baseToast.showToast(e + "");
            e.printStackTrace();
        }
    }

    private void initProgressBar() {
        loadBar = new ProgressDialog(WebMangaDetailsActivity.this);
        loadBar.setCancelable(true);
        loadBar.setMessage("加载中...");
    }

    private void initWebManga(String url) {
        loadBar.show();
        spider.getMangaDetail(url, new JsoupCallBack<MangaBean>() {
            @Override
            public void loadSucceed(final MangaBean result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadBar.dismiss();
                        currentManga = result;
                        refreshUI();
                        toggleDownload();
                    }
                });
            }

            @Override
            public void loadFailed(String error) {
                loadBar.dismiss();
            }
        });
    }

    private void refreshUI() {
        if (null == currentManga) {
            return;
        }
        baseTopBar.setTitle(currentManga.getName());
        ImageLoader.getInstance().displayImage(currentManga.getWebThumbnailUrl(), thumbnailIV, Configure.normalImageOptions);
        mangaNameTv.setText("漫画名称:" + currentManga.getName());
        mangaAuthorTv.setText("作者:" + currentManga.getAuthor());
        //TODO 多类型 可点击
        mangaTypeTv.setText("类型:" + currentManga.getTypes()[0]);
        lastUpdateTv.setText("最后更新:" + currentManga.getLast_update());
        toggleCollect();

        initGridView();
    }

    private void initGridView() {
        if (null == adapter) {
            adapter = new OnlineMangaDetailAdapter(this, currentManga.getChapters());
            mangaGV.setAdapter(adapter);
            mangaGV.setColumnWidth(50);
            mangaGV.setNumColumns(5);
            mangaGV.setVerticalSpacing(10);
            mangaGV.setHorizontalSpacing(3);
            mangaGV.setOnItemClickListener(this);
        } else {
            adapter.setChapters(currentManga.getChapters());
            adapter.notifyDataSetChanged();
        }
        pullToRefreshGridView.onPullDownRefreshComplete();// 动画结束方法
        pullToRefreshGridView.onPullUpRefreshComplete();
    }

    private void initUI() {
        pullToRefreshGridView = (PullToRefreshGridView) findViewById(R.id.ptf_grid_view);
        mangaGV = (GridView) pullToRefreshGridView.getRefreshableView();
        thumbnailIV = (ImageView) findViewById(R.id.thumbnail);
        mangaNameTv = (TextView) findViewById(R.id.manga_name);
        mangaAuthorTv = (TextView) findViewById(R.id.manga_author);
        mangaTypeTv = (TextView) findViewById(R.id.manga_type);
        lastUpdateTv = (TextView) findViewById(R.id.manga_update_date);
        collectV = findViewById(R.id.collect_view);
        downloadIv = (ImageView) findViewById(R.id.download_iv);
        downloadTagTv = (TextView) findViewById(R.id.download_tag_tv);

        collectV.setOnClickListener(this);
        downloadIv.setOnClickListener(this);
        baseTopBar.setRightText("下载");
        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {

            @Override
            public void onRightClick() {
                showOptionsSelector();
            }

            @Override
            public void onTitleClick() {

            }

            @Override
            public void onLeftClick() {
                WebMangaDetailsActivity.this.finish();
            }
        });
    }

    private void initPullGridView() {
        // 上拉加载更多
        pullToRefreshGridView.setPullLoadEnabled(true);
        // 滚到底部自动加载
        pullToRefreshGridView.setScrollLoadEnabled(false);
        pullToRefreshGridView.setOnRefreshListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manga_details_web;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (chooseing) {
            if (firstChoose) {
                downloadStartPoint = position;
                firstChoose = false;
            } else {
                doDownload(downloadStartPoint, position, 1);
            }
        } else {
            Configure.currentMangaName = currentManga.getName() + "(" + currentManga.getChapters().
                    get(position).getChapterPosition() + ")";
            Intent intent = new Intent(WebMangaDetailsActivity.this, ReadMangaActivity.class);
            intent.putExtra("chapterUrl", currentManga.getChapters().get(position).getChapterUrl());
            startActivity(intent);
        }
    }

    private void downloadAll() {
        doDownload(0, currentManga.getChapters().size() - 1, 1);
    }

    private void doDownload(int start, int end, int startPage) {
        //先停掉服务
        Intent stopServiceIntent = new Intent(WebMangaDetailsActivity.this, DownloadService.class);
        stopService(stopServiceIntent);
        //再打开
        Intent intent = new Intent(WebMangaDetailsActivity.this, DownloadService.class);
        Bundle mangaBundle = new Bundle();
        mangaBundle.putSerializable("download_MangaBean", currentManga);
        intent.putExtras(mangaBundle);
        intent.putExtra("download_folderSize", 3);
        intent.putExtra("download_startPage", startPage);
        intent.putExtra("download_currentChapter", start);
        intent.putExtra("download_endChapter", end);
        startService(intent);
        baseToast.showToast("开始下载!");
        showDownloadDialog();
    }

    private void showOptionsSelector() {
        if (null == optionsSelector) {
            optionsSelector = new WheelSelectorDialog(this);
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
                switch (position) {
                    case 0:
                        baseToast.showToast("开始下载!");
                        downloadAll();
                        break;
                    case 1:
                        chooseing = true;
                        firstChoose = true;
                        baseToast.showToast("请先点击起始话然后点击结束话!");
                        break;
                }
            }
        });
        optionsSelector.show();

        optionsSelector.initOptionsData(optionsList);
    }

    @Subscribe
    public void onEventMainThread(final DownLoadEvent event) {
        if (null == event)
            return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (event.getEventType()) {
                    case EventBusEvent.DOWNLOAD_EVENT:

                        break;
                    case EventBusEvent.DOWNLOAD_FINISH_EVENT:
                        stopDownload();
                        showDownloadDialog();
                        break;
                    case EventBusEvent.DOWNLOAD_FAIL_EVENT:
                        baseToast.showToast(event.getDownloadExplain());
                        break;
                }
                //刷新UI放在这里才准确
                refreshDownloadDialogMsg(event);
                toggleDownload();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.collect_view:
                toggleCollect();
                break;
            case R.id.download_iv:
                showDownloadDialog();
                break;
        }
    }

    private void stopDownload() {
        Intent stopIntent = new Intent(WebMangaDetailsActivity.this, DownloadService.class);
        stopService(stopIntent);
        baseToast.showToast("已停止");
    }

    private void showDownloadDialog() {
        if (null == downloadDialog) {
            downloadDialog = new MangaDialog(this);
            downloadDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
                @Override
                public void onOkClick() {
                    if (Configure.isDownloadServiceRunning) {
                        stopDownload();
                    } else {
                        doDownload(SharedPreferencesUtils.getIntSharedPreferencesData
                                        (WebMangaDetailsActivity.this, ShareKeys.CURRENT_DOWNLOAD_EPISODE),
                                SharedPreferencesUtils.getIntSharedPreferencesData
                                        (WebMangaDetailsActivity.this, ShareKeys.DOWNLOAD_END_EPISODE),
                                SharedPreferencesUtils.getIntSharedPreferencesData
                                        (WebMangaDetailsActivity.this, ShareKeys.CURRENT_DOWNLOAD_PAGE));
                    }
                }

                @Override
                public void onCancelClick() {

                }
            });
        }
        downloadDialog.show();
        String downloadMsg = SharedPreferencesUtils.getSharedPreferencesData(this, ShareKeys.DOWNLOAD_EXPLAIN);
        if (TextUtils.isEmpty(downloadMsg)) {
            downloadMsg = "开始下载";
        }
        String downloadingMangaName = SharedPreferencesUtils.getSharedPreferencesData(this, ShareKeys.DOWNLOAD_MANGA_NAME);
        if (TextUtils.isEmpty(downloadingMangaName)) {
            downloadingMangaName = currentManga.getName();
        } else {
            downloadingMangaName = "下载" + downloadingMangaName;
        }
        downloadDialog.setTitle(downloadingMangaName);
        downloadDialog.setMessage(downloadMsg);
        downloadDialog.setCancelText("知道了");
        if (Configure.isDownloadServiceRunning) {
            downloadDialog.setOkText("停止下载");
        } else {
            downloadDialog.setOkText("继续下载");
        }
    }

    private void refreshDownloadDialogMsg(DownLoadEvent event) {
        if (null != downloadDialog) {
            downloadDialog.setMessage(event.getDownloadExplain());
            downloadDialog.setTitle(event.getDownloadMangaName());
            if (Configure.isDownloadServiceRunning) {
                downloadDialog.setOkText("停止下载");
            } else {
                downloadDialog.setOkText("继续下载");
            }
        }
    }

    private void toggleCollect() {
        if (currentManga.isCollected()) {
            collectV.setBackgroundResource(R.drawable.collected);
        } else {
            collectV.setBackgroundResource(R.drawable.collect);
        }
    }

    private void toggleDownload() {
        if (Configure.isDownloadServiceRunning && currentManga.getName().equals
                (SharedPreferencesUtils.getSharedPreferencesData(this, ShareKeys.DOWNLOAD_MANGA_NAME))) {
            downloadTagTv.setVisibility(View.VISIBLE);
        } else {
            downloadTagTv.setVisibility(View.GONE);
        }
        if (currentManga.getName().equals
                (SharedPreferencesUtils.getSharedPreferencesData(this, ShareKeys.DOWNLOAD_MANGA_NAME))) {
            //只有当前下载的漫画才看得到这个
            downloadIv.setVisibility(View.VISIBLE);
        } else {
            downloadIv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
        initWebManga(mangaUrl);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
        pullToRefreshGridView.onPullDownRefreshComplete();// 动画结束方法
        pullToRefreshGridView.onPullUpRefreshComplete();
    }
}
