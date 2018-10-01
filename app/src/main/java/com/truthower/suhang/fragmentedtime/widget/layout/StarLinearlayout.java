package com.truthower.suhang.fragmentedtime.widget.layout;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;


import com.truthower.suhang.fragmentedtime.R;
import com.truthower.suhang.fragmentedtime.adapter.StarAdapter;
import com.truthower.suhang.fragmentedtime.listener.OnRecycleItemClickListener;
import com.truthower.suhang.fragmentedtime.widget.recyclerview.LinearLayoutMangerWithoutBug;

import java.util.ArrayList;

public class StarLinearlayout extends LinearLayout {
    private Context context;
    private RecyclerView starRv;
    private StarAdapter starAdapter;
    private float starNum;
    private int maxStar = 5;
    private ArrayList<StarAdapter.StarState> starStates = new ArrayList<>();
    private OnRecycleItemClickListener onRecycleItemClickListener;

    public StarLinearlayout(Context context) {
        this(context, null);
    }

    public StarLinearlayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StarLinearlayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.view_star_ll, this);
        starRv = (RecyclerView) findViewById(R.id.star_rv);
        starRv.setLayoutManager(new LinearLayoutMangerWithoutBug
                (context, LinearLayoutManager.HORIZONTAL, false));
        initStarRv();
    }

    private void initStarRv() {
        try {
            if (null == starAdapter) {
                starAdapter = new StarAdapter(starStates);
                if (null != onRecycleItemClickListener) {
                    starAdapter.setOnRecycleItemClickListener(onRecycleItemClickListener);
                }
                starRv.setAdapter(starAdapter);

            } else {
                starAdapter.setStarStates(starStates);
                starAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {

        }
    }

    public ArrayList<StarAdapter.StarState> getStarStates() {
        return starStates;
    }

    private void setStarStates(ArrayList<StarAdapter.StarState> starStates) {
        this.starStates = starStates;
        initStarRv();
    }

    public float getStarNum() {
        return starNum;
    }

    public void setStarNum(float starNum) {
        this.starNum = starNum;
        int starInt = (int) starNum;
        float halfF = starNum - starInt;
        boolean halfB = halfF == 0.5;
        ArrayList<StarAdapter.StarState> stars = new ArrayList<>();
        for (int i = 0; i < maxStar; i++) {
            if (i < starInt) {
                stars.add(StarAdapter.StarState.STAR);
            } else if (i == starInt + 1 && halfB) {
                stars.add(StarAdapter.StarState.HALF_STAR);
            } else {
                stars.add(StarAdapter.StarState.UNSTAR);
            }
        }
        setStarStates(stars);
    }

    public int getMaxStar() {
        return maxStar;
    }

    public void setMaxStar(int maxStar) {
        this.maxStar = maxStar;
    }

    public void setOnRecycleItemClickListener(OnRecycleItemClickListener onRecycleItemClickListener) {
        this.onRecycleItemClickListener = onRecycleItemClickListener;
        if (null != starAdapter) {
            starAdapter.setOnRecycleItemClickListener(onRecycleItemClickListener);
        }
    }
}
