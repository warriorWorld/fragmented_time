package com.truthower.suhang.fragmentedtime.widget.viewgroup;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.truthower.suhang.fragmentedtime.R;
import com.truthower.suhang.fragmentedtime.adapter.DrawerAdapter;
import com.truthower.suhang.fragmentedtime.bean.DrawerBean;
import com.truthower.suhang.fragmentedtime.listener.OnRecycleItemClickListener;

import java.util.ArrayList;


/**
 * Created by Administrator on 2017/8/18.
 */

public class MainNavigationView extends RelativeLayout {
    private Context context;
    private RecyclerView drawerRcv;

    private DrawerAdapter mAdapter;

    public MainNavigationView(Context context) {
        this(context, null);
    }

    public MainNavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_navigation, this);
        drawerRcv = (RecyclerView) findViewById(R.id.drawer_rcv);
        drawerRcv.setLayoutManager
                (new LinearLayoutManager
                        (context, LinearLayoutManager.VERTICAL, false) {
                    @Override
                    public boolean canScrollVertically() {
                        return true;
                    }
                });
        drawerRcv.setFocusableInTouchMode(false);
        drawerRcv.setFocusable(false);
        drawerRcv.setHasFixedSize(true);
    }


    public void initLoanAppRec(final ArrayList<DrawerBean> drawers) {
        try {
            if (null == mAdapter) {
                mAdapter = new DrawerAdapter(context);
                mAdapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                    }
                });

                mAdapter.setList(drawers);
                drawerRcv.setAdapter(mAdapter);
                drawerRcv.setItemAnimator(new DefaultItemAnimator());
            } else {
                mAdapter.setList(drawers);
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
