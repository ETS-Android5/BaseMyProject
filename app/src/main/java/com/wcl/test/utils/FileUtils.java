package com.wcl.test.utils;

import java.io.File;

import com.wcl.test.base.BaseApp;

public class FileUtils {

    /**
     * 现在的Android应用将文件放到SD卡上时总是随便创建一个目录，那这样有个问题就是卸载应用时，
     * 这些垃圾还留在用户的SD卡上导致占用存储空间（猎豹清理大师这样的工具由此应用而生）。
     * 其实Android系统已经帮我们提供了相关的API可以将文件缓存到data/data目录下，
     * 当APP卸载时，这些垃圾文件也跟着自动卸载清除了。
     * <p>
     * 2021-05-21 修正补充：
     * 由于安卓11对文件存储有很大限制，导致sdcard/data/data无法正常使用，
     * 所以存储统一改为使用app系统目录
     */
    public static String getDataPath() {
        String cachePath = BaseApp.getApp().getFilesDir().getAbsolutePath();
        return cachePath + File.separator;
    }

    /**
     * 得到文件夹大小
     */
    public static long getFolderSize(java.io.File file) throws Exception {
        long size = 0;
        java.io.File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                size = size + getFolderSize(fileList[i]);
            } else {
                size = size + fileList[i].length();
            }
        }
        return size / (1024 * 1024);
    }

    /**
     * 删文件或者目录
     */
    public static void deleteDirectory(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteDirectory(files[i]);
                }
            }
            file.delete();
        }
    }

    /**
     * 清空文件夹下的文件，而不删除文件夹
     */
    public static boolean clearDirectory(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                clearDirectory(path + File.separator + tempList[i]);
                flag = true;
            }
        }
        return flag;
    }



//    public static void copyDirectory(File fromDir, File toDir) {
//        try {
//            if (!fromDir.isDirectory()) {
//                return;
//            }
//
//            if (!toDir.exists()) {
//                toDir.mkdirs();
//            }
//
//            File[] files = fromDir.listFiles();
//            for (File file : files) {
//                String strFrom = fromDir + File.separator + file.getName();
//                String strTo = toDir + File.separator + file.getName();
//                if (file.isDirectory()) {
//                    copyDirectory(new File(strFrom), new File(strTo));
//                }
//                if (file.isFile()) {
//                    copy(new File(strFrom), new File(strTo));
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("copy Directory error = " + e.toString());
//        }
//
//    }
//
//    public static void copy(File source, File dest) {
//        try {
//            Files.copy(source.toPath(), dest.toPath());
//        } catch (Exception e) {
//            System.out.println("copy file error = " + e.toString());
//        }
//
//    }
}
