package com.shijiwei.xkit.utility.file;

import android.os.Environment;

import java.io.File;

/**
 * Created by shijiwei on 2017/6/5.
 */
public class FileUtility {

    private static String appName = "shijiwei";

    // 文件根目录
    public static String ROOT;
    public static String APP_CRASH_LOG_PATH;
    public static String APK_SAVE_PATH;
    public static String CACHE_PIC_PATH;
    public static String TEMP_FILE_PATH;

    //文件最大容量
    public static final long MAX_SIZE = 1024 * 1024 * 30;

    static {

        if (Environment.MEDIA_UNMOUNTED.equals(Environment.getExternalStorageState())) {
            ROOT = Environment.getExternalStorageDirectory()
                    .getPath()
                    + File.separator
                    + appName;
        } else {
            ROOT = Environment.getDataDirectory()
                    .getAbsolutePath()
                    + File.separator
                    + appName;
        }

        APP_CRASH_LOG_PATH = ROOT + File.separator + "crash_log";
        APK_SAVE_PATH = ROOT + File.separator + "apk";
        CACHE_PIC_PATH = ROOT + File.separator + "cache_picture";
        TEMP_FILE_PATH = ROOT + File.separator + "temp_file";

        File appCrashLog = new File(APP_CRASH_LOG_PATH);
        if (!appCrashLog.exists())
            appCrashLog.mkdirs();

    }

    /**
     * 删除文件夹
     */
    public static void delete(String path) {
        delete(new File(path));
    }

    public static void delete(File delFile) {

        if (!delFile.exists()) return;
        if (delFile.isDirectory()) {
            File[] files = delFile.listFiles();
            if (files.length == 0 || files == null) {
                delFile.delete();
                return;
            }
            for (int i = 0; i < files.length; i++) {
                delete(files[i]);
            }
            delFile.delete();
        } else {
            delFile.delete();
        }
    }


    /**
     * 返回文件大小
     */
    public static double getFileSize(String path) {
        return getFileSiez(new File(path));
    }

    public static double getFileSiez(File targetFile) {

        if (!targetFile.exists()) return 0.0;
        double size = 0.0;
        if (targetFile.isDirectory()) {
            File[] files = targetFile.listFiles();
            for (int i = 0; i < files.length; i++)
                size += getFileSiez(files[i]);
            return size;
        } else {
            // 以 ‘M’为单位返回
            return (double) targetFile.length() / 1024 / 1024;
        }
    }

}
