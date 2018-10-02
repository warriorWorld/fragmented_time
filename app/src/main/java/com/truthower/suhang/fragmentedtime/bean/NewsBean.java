package com.truthower.suhang.fragmentedtime.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/10/2.
 */

public class NewsBean extends BaseBean {
    private List<NewsListBean> news_list;

    public List<NewsListBean> getNews_list() {
        return news_list;
    }

    public void setNews_list(List<NewsListBean> news_list) {
        this.news_list = news_list;
    }
}
