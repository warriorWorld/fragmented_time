package com.truthower.suhang.fragmentedtime.business.web;

import android.os.Bundle;

import com.truthower.suhang.fragmentedtime.R;
import com.truthower.suhang.fragmentedtime.base.BaseFragment;
import com.truthower.suhang.fragmentedtime.base.FragmentContainerActivity;


/**
 * 个人信息页
 */
public class WebActivity extends FragmentContainerActivity {
    private WebFragment mWebFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mWebFragment = new WebFragment();
        super.onCreate(savedInstanceState);
        hideBaseTopBar();
    }

    @Override
    protected BaseFragment getFragment() {
        return mWebFragment;
    }

    @Override
    protected String getTopBarTitle() {
        return getResources().getString(R.string.app_name);
    }
}
