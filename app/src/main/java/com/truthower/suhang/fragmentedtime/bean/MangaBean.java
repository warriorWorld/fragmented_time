package com.truthower.suhang.fragmentedtime.bean;

import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/3.
 */
public class MangaBean extends BaseBean {
    private boolean isThumbnailLoadFail;
    private boolean isMangaDetailLoadSuccess;
    private String url;
    private String localThumbnailUrl;
    private String userThumbnailUrl;
    private String name;
    private String webThumbnailUrl;
    private String author;
    private String last_update;
    private String[] types, typeCodes;
    private boolean isCollected;
    private ArrayList<ChapterBean> chapters;
    private String description;//漫画介绍
    private boolean isChecked;

    public boolean isThumbnailLoadFail() {
        return isThumbnailLoadFail;
    }

    public void setThumbnailLoadFail(boolean thumbnailLoadFail) {
        isThumbnailLoadFail = thumbnailLoadFail;
    }

    public boolean isMangaDetailLoadSuccess() {
        return isMangaDetailLoadSuccess;
    }

    public void setMangaDetailLoadSuccess(boolean mangaDetailLoadSuccess) {
        isMangaDetailLoadSuccess = mangaDetailLoadSuccess;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    public ArrayList<ChapterBean> getChapters() {
        return chapters;
    }

    public void setChapters(ArrayList<ChapterBean> chapters) {
        this.chapters = chapters;
    }

    public String getLocalThumbnailUrl() {
        if (!TextUtils.isEmpty(localThumbnailUrl) && !localThumbnailUrl.contains("file://")
                && !localThumbnailUrl.contains("http://") && !localThumbnailUrl.contains("https://")) {
            return "file://" + localThumbnailUrl;
        } else {
            return localThumbnailUrl;
        }
    }

    public void setLocalThumbnailUrl(String localThumbnailUrl) {
        this.localThumbnailUrl = localThumbnailUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebThumbnailUrl() {
        return webThumbnailUrl;
    }

    public void setWebThumbnailUrl(String webThumbnailUrl) {
        this.webThumbnailUrl = webThumbnailUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getTypeCodes() {
        return typeCodes;
    }

    public void setTypeCodes(String[] typeCodes) {
        this.typeCodes = typeCodes;
    }

    public String getUserThumbnailUrl() {
        if (!TextUtils.isEmpty(userThumbnailUrl) && !userThumbnailUrl.contains("file://")
                && !userThumbnailUrl.contains("http://") && !userThumbnailUrl.contains("https://")) {
            return "file://" + userThumbnailUrl;
        } else {
            return userThumbnailUrl;
        }
    }

    public void setUserThumbnailUrl(String userThumbnailUrl) {
        this.userThumbnailUrl = userThumbnailUrl;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
