package com.truthower.suhang.fragmentedtime.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.fragmentedtime.R;
import com.truthower.suhang.fragmentedtime.base.BaseRecyclerAdapter;
import com.truthower.suhang.fragmentedtime.bean.DrawerBean;
import com.truthower.suhang.fragmentedtime.listener.OnRecycleItemClickListener;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/15.
 * 还款页的还款计划
 */
public class DrawerAdapter extends BaseRecyclerAdapter {
    private ArrayList<DrawerBean> list = null;
    private OnRecycleItemClickListener onRecycleItemClickListener;

    public DrawerAdapter(Context context) {
        super(context);
    }

    @Override
    protected String getEmptyText() {
        return "空";
    }

    @Override
    protected String getListEndText() {
        return "";
    }

    @Override
    protected <T> ArrayList<T> getDatas() {
        return (ArrayList<T>) list;
    }

    @Override
    protected RecyclerView.ViewHolder getNormalViewHolder(ViewGroup viewGroup) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_drawer, viewGroup, false);
        NormalViewHolder vh = new NormalViewHolder(view);
        return vh;
    }

    @Override
    protected void refreshNormalViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final DrawerBean item = list.get(position);
        ((NormalViewHolder) viewHolder).drawerNameTv.setText(item.getTitle());

        ((NormalViewHolder) viewHolder).itemDrawerRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onRecycleItemClickListener) {
                    onRecycleItemClickListener.onItemClick(position);
                }
            }
        });
    }

    public void setList(ArrayList<DrawerBean> list) {
        this.list = list;
    }

    public ArrayList<DrawerBean> getList() {
        return list;
    }

    public void setOnRecycleItemClickListener(OnRecycleItemClickListener onRecycleItemClickListener) {
        this.onRecycleItemClickListener = onRecycleItemClickListener;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout itemDrawerRl;
        private TextView drawerNameTv;

        public NormalViewHolder(View view) {
            super(view);
            itemDrawerRl = (RelativeLayout) view.findViewById(R.id.item_drawer_rl);
            drawerNameTv = (TextView) view.findViewById(R.id.drawer_name_tv);
        }
    }
}
