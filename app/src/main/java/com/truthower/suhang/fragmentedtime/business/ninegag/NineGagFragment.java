package com.truthower.suhang.fragmentedtime.business.ninegag;

import android.view.ViewGroup;

import com.truthower.suhang.fragmentedtime.R;
import com.truthower.suhang.fragmentedtime.base.BaseRefreshFragment;
import com.truthower.suhang.fragmentedtime.bean.NewsBean;
import com.truthower.suhang.fragmentedtime.bean.NewsListBean;
import com.truthower.suhang.fragmentedtime.listener.JsoupCallBack;
import com.truthower.suhang.fragmentedtime.spider.NineGagSpider;

import java.io.IOException;

/**
 * Created by Administrator on 2018/10/2.
 */

public class NineGagFragment extends BaseRefreshFragment {
    private NineGagSpider mNineGagSpider;

    @Override
    protected void onCreateAfterInitUI() {
        hideRefreshTopBar();
    }

    @Override
    protected void onCreateBeforInitUI() {
        mNineGagSpider = new NineGagSpider();
    }

    @Override
    protected void doGetData() {
        try {
            mNineGagSpider.get9GagList(new JsoupCallBack<NewsBean>() {
                @Override
                public void loadSucceed(NewsBean result) {
                    baseToast.showToast("已刷新");
                    noMoreData();
                }

                @Override
                public void loadFailed(String error) {
                    baseToast.showToast("读取失败");
                    noMoreData();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_9gag;
    }

    @Override
    protected void initFrgmentUI(ViewGroup view) {

    }
}
