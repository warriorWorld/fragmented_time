package com.truthower.suhang.fragmentedtime.sort;

import java.io.File;
import java.util.Comparator;

/**
 * 这个是给文件排序并重命名的
 */
public class FileComparatorByTime implements Comparator<File> {

    @Override
    public int compare(File file, File t1) {
        if (file.lastModified() < t1.lastModified()) {
            return -1;
        } else if (file.lastModified() > t1.lastModified()) {
            return 1;
        } else {
            return 0;
        }
    }
}
