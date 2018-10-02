package com.truthower.suhang.fragmentedtime.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018/9/18.
 */

public class ParamBean implements Serializable{
    private ArrayList<ParamListBean> params;

    public ArrayList<ParamListBean> getParams() {
        return params;
    }

    public void setParams(ArrayList<ParamListBean> params) {
        this.params = params;
    }
}
