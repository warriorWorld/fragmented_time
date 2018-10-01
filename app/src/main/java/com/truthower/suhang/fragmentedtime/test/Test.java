package com.truthower.suhang.fragmentedtime.test;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Test {
    private String[] spiderables = {"http://topwebcomics.com/", "http://mangakakalot.com/"};
    private static org.jsoup.nodes.Document doc;

    public static void main(String[] args) {
        try {
            doc = Jsoup.connect("https://www.mangareader.net/one-piece/32/1")
                    .timeout(10000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (null != doc) {
            Elements mangaListElements = doc.select("div.imgholder a");
//            String name = mangaListElements.get(0).getElementsByTag("img").last().attr("src");

            System.out.println(mangaListElements.text());
        }
    }
}
