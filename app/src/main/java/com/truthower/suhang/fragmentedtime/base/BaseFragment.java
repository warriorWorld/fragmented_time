package com.truthower.suhang.fragmentedtime.base;/**
 * Created by Administrator on 2016/10/26.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.truthower.suhang.fragmentedtime.widget.toast.EasyToast;


/**
 * 作者：苏航 on 2016/10/26 14:46
 * 邮箱：772192594@qq.com
 */
public class BaseFragment extends Fragment {
    protected EasyToast baseToast;

    public String getFragmentTag() {
        return getClass().getSimpleName();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseToast = new EasyToast(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
