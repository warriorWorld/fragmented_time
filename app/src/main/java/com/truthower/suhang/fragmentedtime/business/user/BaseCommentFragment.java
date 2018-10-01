package com.truthower.suhang.fragmentedtime.business.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.truthower.suhang.fragmentedtime.R;
import com.truthower.suhang.fragmentedtime.adapter.CommentAdapter;
import com.truthower.suhang.fragmentedtime.base.BaseFragment;
import com.truthower.suhang.fragmentedtime.bean.CommentBean;
import com.truthower.suhang.fragmentedtime.business.comment.CommentActivity;
import com.truthower.suhang.fragmentedtime.listener.OnRecycleItemClickListener;
import com.truthower.suhang.fragmentedtime.widget.recyclerview.LinearLayoutMangerWithoutBug;

import java.util.ArrayList;

/**
 * 个人信息页
 */
public abstract class BaseCommentFragment extends BaseFragment implements View.OnClickListener {
    protected ArrayList<CommentBean> commentList = new ArrayList<>();
    private RecyclerView commentRcv;
    private CommentAdapter adapter;
    protected String owner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.activity_only_recycler, container, false);
        initUI(v);
        doGetData();
        return v;
    }

    protected abstract void doGetData();

    public void setOwner(String owner) {
        this.owner = owner;
    }

    protected void initListView() {
        try {
            if (null == adapter) {
                adapter = new CommentAdapter(getActivity(), commentList);
                adapter.setUserCenter(true);
                adapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(getActivity(), CommentActivity.class);
                        intent.putExtra("mangaName", commentList.get(position).getMangaName());
                        intent.putExtra("mangaUrl", commentList.get(position).getMangaUrl());
                        startActivity(intent);
                    }
                });
                commentRcv.setAdapter(adapter);
            } else {
                adapter.setDatas(commentList);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
        }
    }


    private void initUI(View v) {
        commentRcv = (RecyclerView) v.findViewById(R.id.only_rcv);
        commentRcv.setLayoutManager
                (new LinearLayoutMangerWithoutBug
                        (getActivity(), LinearLayoutManager.VERTICAL, false));
        commentRcv.setFocusableInTouchMode(false);
        commentRcv.setFocusable(false);
        commentRcv.setHasFixedSize(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
