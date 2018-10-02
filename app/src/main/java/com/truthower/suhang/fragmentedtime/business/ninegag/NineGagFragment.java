package com.truthower.suhang.fragmentedtime.business.ninegag;

import android.view.ViewGroup;

import com.truthower.suhang.fragmentedtime.R;
import com.truthower.suhang.fragmentedtime.base.BaseRefreshFragment;

/**
 * Created by Administrator on 2018/10/2.
 */

public class NineGagFragment extends BaseRefreshFragment {
    @Override
    protected void onCreateAfterInitUI() {
        hideRefreshTopBar();
    }

    @Override
    protected void onCreateBeforInitUI() {

    }

    @Override
    protected void doGetData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_9gag;
    }

    @Override
    protected void initFrgmentUI(ViewGroup view) {

    }
}
