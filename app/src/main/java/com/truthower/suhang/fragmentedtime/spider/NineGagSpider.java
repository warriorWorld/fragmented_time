package com.truthower.suhang.fragmentedtime.spider;

import com.truthower.suhang.fragmentedtime.listener.JsoupCallBack;

import java.io.IOException;

/**
 * http://www.mangareader.net/
 */
public class NineGagSpider {
    private org.jsoup.nodes.Document doc;
    private String webUrl = "https://9gag.com/";

    public <ResultObj> void get9GagList(final JsoupCallBack<ResultObj> jsoupCallBack) throws IOException {
    }

    public String getWebUrl() {
        return webUrl;
    }
}
