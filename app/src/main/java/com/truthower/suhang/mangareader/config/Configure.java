package com.truthower.suhang.mangareader.config;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.truthower.suhang.mangareader.R;

/**
 * Created by Administrator on 2017/7/19.
 */

public class Configure {
    public static String versionName = "";
    public static String versionCode = "";

    public static DisplayImageOptions normalImageOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .showImageOnLoading(R.drawable.back)
            .showImageOnFail(R.drawable.empty_list)
            .build();
}