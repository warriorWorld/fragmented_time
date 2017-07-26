package com.truthower.suhang.mangareader.spider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Message;
import android.text.TextUtils;

import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.eventbus.DownLoadEvent;
import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
import com.truthower.suhang.mangareader.listener.DownloadCallBack;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.utils.ImageUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * http://www.mangareader.net/
 */
public class FileSpider {
    private String webUrl = "file://";
    private final int TRY_COUNT_LIMIT = 3;
    private int tryCount = 0;
    private DownLoadEvent downLoadEvent = new DownLoadEvent(EventBusEvent.DOWNLOAD_EVENT);

    private FileSpider() {
    }

    private static volatile FileSpider instance = null;

    public static FileSpider getInstance() {
        if (instance == null) {
            //线程锁定
            synchronized (FileSpider.class) {
                //双重锁定
                if (instance == null) {
                    instance = new FileSpider();
                }
            }
        }
        return instance;
    }

    public ArrayList<MangaBean> getMangaList(final String path) {
        File f = new File(path);//第一级目录 reptile
        if (!f.exists()) {
            f.mkdirs();
        }
        int firstFileLength = f.toString().length() + 1;
        File[] files = f.listFiles();//第二级目录 具体漫画们
        if (null != files && files.length > 0) {
            ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();
            for (int i = 0; i < files.length; i++) {
                Bitmap bp = null;
                MangaBean item = new MangaBean();

                String title = files[i].toString();
                title = title.substring(firstFileLength, title.length());

                item.setName(title);
                item.setUrl(files[i].toString());

                File[] files1 = files[i].listFiles();//第三级目录 某具体漫画内部
                if (null != files1 && files1.length > 0 && !files1[0].isDirectory()) {
                    //如果下一级目录就直接是图片文件 则显示第一张图片
                    item.setLocalThumbnailUrl(files1[0].toString());

                } else if (null != files1 && files1.length > 0 && files1[0].isDirectory()) {
                    //如果下一级目录不是图片而是文件夹们  二级文件夹
                    File[] files2 = files1[0].listFiles();//第四级目录 某具体漫画内部的内部
                    item.setLocalThumbnailUrl(files2[0].toString());
                } else if (null == files1 && !files[i].isDirectory()) {
                    //如果files1这一级就已经是单张图片了
                    item.setLocalThumbnailUrl(files[i].toString());
                } else {
                    //空文件夹
                }
                mangaList.add(item);
            }
            return mangaList;
        } else {
            return null;
        }
    }


    public static void deleteFile(String file) {
        if (file.contains("file://")) {
            file = file.substring(7, file.length());
        }
        File f = new File(file);
        deleteFile(f);
    }

    /**
     * 递归删除文件和文件夹 因为file.delete();只能删除空文件夹或文件 所以需要这么递归循环删除
     *
     * @param file 要删除的根目�?
     */
    public static void deleteFile(File file) {
        if (file.isFile() && file.exists()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                deleteFile(f);
            }
            file.delete();
        }
    }


    /**
     * 下载一整个章节的图片
     *
     * @param mangaName   仅用于给文件命名
     * @param imgs        url列表
     * @param episode     仅用于命名
     * @param endEpisode  仅用于刷新下载页UI
     * @param page        起始页
     * @param folderSize  几话放在同一个文件夹中
     * @param <ResultObj>
     */
    public <ResultObj> void downloadImgs(final String mangaName, final ArrayList<String> imgs,
                                         final int episode, final int endEpisode, final int page, final int folderSize,
                                         final JsoupCallBack<ResultObj> jsoupCallBack) {
        // 将图片下载并保存
        new Thread() {
            public void run() {
                Bitmap bp = null;
                if (!TextUtils.isEmpty(imgs.get(page - 1))) {
                    //从网络上获取到图片
                    try {
                        InputStream is = new URL(imgs.get(page - 1)).openStream();
                        bp = BitmapFactory.decodeStream(is);
                    } catch (IOException e) {
                        tryCount++;
                        if (tryCount <= TRY_COUNT_LIMIT) {
                            downLoadEvent = new DownLoadEvent(EventBusEvent.DOWNLOAD_FAIL_EVENT);
                            downLoadEvent.setCurrentDownloadEpisode(episode);
                            downLoadEvent.setCurrentDownloadPage(page);
                            downLoadEvent.setDownloadExplain("第" + episode + "话" +
                                    "第" + page + "页下载失败!正在第" + (tryCount + 1) + "次尝试");
                            downLoadEvent.setDownloadFolderSize(folderSize);
                            downLoadEvent.setDownloadMangaName(mangaName);
                            downLoadEvent.setDownloadEndEpisode(endEpisode);

                            EventBus.getDefault().post(downLoadEvent);
                            downloadImgs(mangaName, imgs, episode, endEpisode, page, folderSize, jsoupCallBack);
                        } else {
                            tryCount = 0;
                            downloadImgs(mangaName, imgs, episode, endEpisode, page + 1, folderSize, jsoupCallBack);
                        }
                    }
                    if (null != bp) {
                        //把图片保存到本地
                        saveBitmap(bp, mangaName + "_" + episode
                                        + "_" + page + ".jpg",
                                getChildFolderName(episode, folderSize), mangaName);

                        if (page + 1 <= imgs.size()) {
                            //下载完成
                            downLoadEvent = new DownLoadEvent(EventBusEvent.DOWNLOAD_EVENT);
                            downLoadEvent.setCurrentDownloadEpisode(episode);
                            downLoadEvent.setCurrentDownloadPage(page);
                            downLoadEvent.setDownloadExplain("第" + episode + "话" +
                                    "第" + page + "页下载完成!");
                            downLoadEvent.setDownloadFolderSize(folderSize);
                            downLoadEvent.setDownloadMangaName(mangaName);
                            downLoadEvent.setDownloadEndEpisode(endEpisode);
                            EventBus.getDefault().post(downLoadEvent);
                            downloadImgs(mangaName, imgs, episode, endEpisode, page + 1, folderSize, jsoupCallBack);
                        } else {
                            //下载完成
                            downLoadEvent = new DownLoadEvent(EventBusEvent.DOWNLOAD_EVENT);
                            downLoadEvent.setCurrentDownloadEpisode(episode);
                            downLoadEvent.setCurrentDownloadPage(page);
                            downLoadEvent.setDownloadExplain("第" + episode + "话下载完成!");
                            downLoadEvent.setDownloadFolderSize(folderSize);
                            downLoadEvent.setDownloadMangaName(mangaName);
                            downLoadEvent.setDownloadEndEpisode(endEpisode);

                            EventBus.getDefault().post(downLoadEvent);
                            jsoupCallBack.loadSucceed((ResultObj) null);
                        }
                    } else {
                        tryCount++;
                        if (tryCount <= TRY_COUNT_LIMIT) {
                            downLoadEvent = new DownLoadEvent(EventBusEvent.DOWNLOAD_FAIL_EVENT);
                            downLoadEvent.setCurrentDownloadEpisode(episode);
                            downLoadEvent.setCurrentDownloadPage(page);
                            downLoadEvent.setDownloadExplain("第" + episode + "话" +
                                    "第" + page + "页下载失败!正在第" + (tryCount + 1) + "次尝试");
                            downLoadEvent.setDownloadFolderSize(folderSize);
                            downLoadEvent.setDownloadMangaName(mangaName);
                            downLoadEvent.setDownloadEndEpisode(endEpisode);

                            EventBus.getDefault().post(downLoadEvent);
                            downloadImgs(mangaName, imgs, episode, endEpisode, page, folderSize, jsoupCallBack);
                        } else {
                            tryCount = 0;
                            downloadImgs(mangaName, imgs, episode, endEpisode, page + 1, folderSize, jsoupCallBack);
                        }
                    }
                } else {
                    downloadImgs(mangaName, imgs, episode, endEpisode, page + 1, folderSize, jsoupCallBack);
                }
            }
        }.start();
    }


    /**
     * 存图片 TODO
     *
     * @param b
     * @param bmpName
     * @param mangaName
     * @return
     * @childFolder 子文件夹名 因为漫画图片数量太大 所以在多一层子文件夹 自动建立
     */
    public static String saveBitmap(Bitmap b, String bmpName,
                                    String childFolder, String mangaName) {
        b = ImageUtil.imageZoom(b, 480);
        String path = Configure.storagePath + "/" + mangaName;
        String jpegName = path + "/" + childFolder + "/" + bmpName;
        String folderName = path + "/" + childFolder;
        File f = new File(folderName);
        if (!f.exists()) {
            // 如果不存在 就创建
            f.mkdirs();
        }
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jpegName;
    }

    private String getChildFolderName(int episode, int folderSize) {
        String res;
        int start = ((int) (episode / folderSize)) * folderSize;
        int end = start + folderSize - 1;
        res = start + "-" + end;
        return res;
    }
}
