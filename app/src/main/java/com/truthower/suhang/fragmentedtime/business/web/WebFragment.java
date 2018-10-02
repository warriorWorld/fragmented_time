package com.truthower.suhang.fragmentedtime.business.web;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;

import com.google.gson.Gson;
import com.truthower.suhang.fragmentedtime.R;
import com.truthower.suhang.fragmentedtime.base.BaseRefreshFragment;
import com.truthower.suhang.fragmentedtime.bean.ParamBean;
import com.truthower.suhang.fragmentedtime.bean.ParamListBean;
import com.truthower.suhang.fragmentedtime.business.main.MainActivity;
import com.truthower.suhang.fragmentedtime.config.Configure;
import com.truthower.suhang.fragmentedtime.listener.OnAllVersionScrollChangeListener;
import com.truthower.suhang.fragmentedtime.listener.OnEditResultListener;
import com.truthower.suhang.fragmentedtime.listener.OnUrlChangeListener;
import com.truthower.suhang.fragmentedtime.utils.ActivityPoor;
import com.truthower.suhang.fragmentedtime.utils.MatchStringUtil;
import com.truthower.suhang.fragmentedtime.widget.bar.TopBar;
import com.truthower.suhang.fragmentedtime.widget.dialog.DownloadDialog;
import com.truthower.suhang.fragmentedtime.widget.dialog.MangaDialog;
import com.truthower.suhang.fragmentedtime.widget.dialog.MangaEditDialog;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Administrator on 2018/9/4.
 */
public class WebFragment extends BaseRefreshFragment implements
        EasyPermissions.PermissionCallbacks {
    protected MyWebView myWebView;
    private String url = "";
    private ClipboardManager clip;//复制文本用
    private DownloadDialog downloadDialog;
    private String noRefreshUrl = "";
    private boolean hideTopLeft = false;

    @Override
    protected void onCreateAfterInitUI() {
        try {
            clip = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            initJS();
            if (getActivity() instanceof WebActivity) {
                Intent intent = getActivity().getIntent();
                url = intent.getStringExtra("url");
                if (TextUtils.isEmpty(url)) {
                    getActivity().finish();
                }

                myWebView.loadUrl(url);
            }
        } catch (Exception e) {
            //catch
            if (Configure.isTest) {
                MangaDialog dialog = new MangaDialog(getActivity());
                dialog.show();
                dialog.setTitle(e + "");
            }
        }
    }

    @Override
    protected void onCreateBeforInitUI() {

    }

    @Override
    protected void initFrgmentUI(ViewGroup view) {
        myWebView = (MyWebView) view.findViewById(R.id.peanut_web);
        refreshBaseTopbar.setTitle("读取中");
        if (hideTopLeft) {
            refreshBaseTopbar.hideLeftButton();
        }
        myWebView.setOnPeanutWebViewListener(new MyWebView.OnPeanutWebViewListener() {
            @Override
            public void onReceivedTitle(String title) {
                if (!MatchStringUtil.isChinese(title)) {
                    refreshBaseTopbar.setTitle(getResources().getString(R.string.app_name));
                } else {
                    refreshBaseTopbar.setTitle(title);
                }

            }
        });
        myWebView.setOnUrlChangeListener(new OnUrlChangeListener() {
            @Override
            public void onUrlChange(String url) {
                if (!url.equals(noRefreshUrl)) {
                    baseSwipeLayout.setEnabled(true);
                }
            }
        });
        myWebView.setOnWebViewLongClickListener(new MyWebView.OnWebViewLongClickListener() {
            @Override
            public void onImgLongClick(String imgUrl) {

            }
        });
        refreshBaseTopbar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {
                getActivity().finish();
            }

            @Override
            public void onRightClick() {

            }

            @Override
            public void onTitleClick() {
                if (Configure.isTest) {
                    MangaDialog dialog = new MangaDialog(getActivity());
                    dialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
                        @Override
                        public void onOkClick() {
                            clip.setText(myWebView.getUrl());
                        }

                        @Override
                        public void onCancelClick() {

                        }
                    });
                    dialog.show();
                    dialog.setTitle(myWebView.getUrl());
                    dialog.setOkText("复制地址");
                }
            }
        });
        refreshBaseTopbar.setTopBarLongClickLister(new TopBar.OnTopBarLongClickListener() {
            @Override
            public void onLeftLongClick() {

            }

            @Override
            public void onRightLongClick() {

            }

            @Override
            public void onTitleLongClick() {
                if (Configure.isTest) {
                    MangaEditDialog dialog = new MangaEditDialog(getActivity());
                    dialog.setOnEditResultListener(new OnEditResultListener() {
                        @Override
                        public void onResult(String text) {
                            myWebView.loadUrl(text);
                        }

                        @Override
                        public void onCancelClick() {

                        }
                    });
                    dialog.show();
                }
            }
        });


        myWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                MangaDialog dialog = new MangaDialog(getActivity());
                dialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
                    @Override
                    public void onOkClick() {
                        //目前仅支持apk文件
                        downloadUrl = url;
                        doDownload();
                    }

                    @Override
                    public void onCancelClick() {

                    }
                });
                dialog.show();
                dialog.setTitle("是否下载文件?");
                dialog.setOkText("是");
                dialog.setCancelText("否");
            }
        });
        myWebView.setOnAllVersionScrollChangeListener(new OnAllVersionScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == 0) {
                    baseSwipeLayout.setEnabled(true);
                } else {
                    baseSwipeLayout.setEnabled(false);
                }
            }
        });
        hideLoadMore();
    }

    private String downloadUrl = "";

    @AfterPermissionGranted(333)
    private void doDownload() {
        //不需要处理拒绝后的回调 因为一亿元是强制更新 用户不更新就不让进应用
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            // Already have permission, do the thing
            // ...
            showDownLoadDialog();
            // 下载apk，自动安装
            FinalHttp client = new FinalHttp();
            // url:下载的地址
            // target:保存的地址，包含文件的名称
            // callback 下载时的回调对象
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                client.download(downloadUrl, Environment.getExternalStorageDirectory() + "/other.apk",
                        new AjaxCallBack<File>() {

                            // 下载失败时回调这个方法
                            @Override
                            public void onFailure(Throwable t, String strMsg) {
                                super.onFailure(t, strMsg);
                                if (null != downloadDialog && downloadDialog.isShowing()) {
                                    downloadDialog.dismiss();
                                }
                                if (Configure.isTest) {
                                    baseToast.showToast(t.getMessage() + "\n" + strMsg);
                                } else {
                                    baseToast.showToast("请检查你的网络");
                                }
                            }

                            // 下载时回调这个方法
                            // count ：下载文件需要的总时间，单位是毫秒
                            // current :当前进度,单位是毫秒
                            @Override
                            public void onLoading(long count, long current) {
                                super.onLoading(count, current);
                                String progress = current * 100 / count + "";
                                Integer integer = Integer.parseInt(progress);
                                downloadDialog.setProgress(integer);
                            }

                            // 下载成功时回调这个方法
                            @Override
                            public void onSuccess(File t) {
                                super.onSuccess(t);
                                if (null != downloadDialog && downloadDialog.isShowing()) {
                                    downloadDialog.dismiss();
                                }
                                baseToast.showToast("下载成功,文件保存在" + t.getPath());
                                Intent intent = new Intent();
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setAction("android.intent.action.VIEW");
                                intent.addCategory("android.intent.category.DEFAULT");
                                intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive");
                                startActivity(intent);
                            }
                        });
            }
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "需要文件读写权限才能下载文件",
                    333, perms);
        }
    }

    private void showDownLoadDialog() {
        if (null == downloadDialog) {
            downloadDialog = new DownloadDialog(getActivity());
        }
        downloadDialog.show();
        downloadDialog.setCancelable(false);
    }

    private void initJS() {
        //JavaScript回调接口
        CallBackJavascriptInterface callBackJavascriptInterface = new
                CallBackJavascriptInterface(getActivity(), new JavaScriptCallBackListener() {
            @Override
            public void toPage(String activityName) {
                toCertainPage(activityName, null);
            }

            @Override
            public void toPage(String activityName, String params) {
                Gson gson = new Gson();
                ParamBean paramBean = gson.fromJson(params, ParamBean.class);
                toCertainPage(activityName, paramBean.getParams());
            }

            @Override
            public void goToLogin(String string) {
                if (!TextUtils.isEmpty(string)) {
                    baseToast.showToast(string);
                }
            }

            @Override
            public void closeHtml() {
                getActivity().setResult(getActivity().RESULT_OK);
                getActivity().finish();
            }

            @Override
            public void closePullRefresh() {
                noRefreshUrl = myWebView.getUrl();
                baseSwipeLayout.setEnabled(false);
            }
        });
        myWebView.addJavascriptInterface(callBackJavascriptInterface,
                callBackJavascriptInterface.getInterfaceName());
    }

    private void toCertainPage(String activityName, ArrayList<ParamListBean> params) {
        Intent intent = null;
        try {
            ActivityPoor.finishAllActivityButThis(MainActivity.class);
            intent = new Intent(getActivity(), Class.forName(activityName));
            if (null != params && params.size() > 0) {
                for (int i = 0; i < params.size(); i++) {
                    intent.putExtra(params.get(i).getParam_name(), params.get(i).getParam_value());
                }
            }
            startActivity(intent);
            getActivity().finish();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCurrentUrl() {
        return myWebView.getUrl();
    }

    @Override
    public void doGetData() {
        try {
            if (TextUtils.isEmpty(myWebView.getUrl())) {
                myWebView.loadUrl(url);
            } else {
                myWebView.reload();
            }
            noMoreData();
        } catch (Exception e) {
            //有可能fragment还没加载完就调用所以可能空指针
            e.printStackTrace();
        }
    }

    public void loadUrl(String url) {
        if (null != myWebView) {
            myWebView.loadUrl(url);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        //这个调用不能调用带参数的方法
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        baseToast.showToast("已获得授权,请继续!");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == 333) {
            MangaDialog dialog = new MangaDialog(getActivity());
            dialog.show();
            dialog.setTitle("没有文件读写权限,无法下载!");
            dialog.setOkText("知道了");
        }
    }

    public void setHideTopLeft(boolean hideTopLeft) {
        this.hideTopLeft = hideTopLeft;
    }
}
