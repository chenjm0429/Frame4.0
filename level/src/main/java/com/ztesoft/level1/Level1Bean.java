package com.ztesoft.level1;

import android.os.Environment;

public class Level1Bean {

    /**
     * SharePreferences文件名称，使用时可修改
     */
    public final static String SHARE_PREFERENCES_NAME = "com.ztesoft.govmrkt.preferences.config";

    //屏幕宽度和高度
    public static int actualWidth = 320;
    public static int actualHeight = 480;

    public static String SERVICE_PATH = "http://10.21.57.21:7002/webservice/bdx/ianglem/";

    public static String AUTOUPDATE_FILENAME = "MobileBI_update.apk";
    //SD卡根目录
    public static final String SD_ROOTPATH = Environment.getExternalStorageDirectory() + "/";

    // 下载状态              0 成功，1 文件已存在，-1 下载失败，2 下载中
    public static final int DOWNLOAD_COMPLETE = 0;
    public static final int DOWNLOAD_ED = 1;
    public static final int DOWNLOAD_FAIL = -1;
    public static final int DOWNLOAD_PROS = 2;

    // 上传状态              0 成功，1 文件已存在，-1 上传失败，2 上传中
    public static final int UPLOAD_COMPLETE = 0;
    public static final int UPLOAD_ED = 1;
    public static final int UPLOAD_FAIL = -1;
    public static final int UPLOAD_PROS = 2;

    //公共信息
    public static String plusColor = "#0A8C17";//正值颜色
    public static String minusColor = "#BA0000";//负值颜色
    public static String zeroColor = "#D18A10";//零值颜色
    public static String plusPicCode = "table_green";//正值箭头
    public static String minusPicCode = "table_red";//负值箭头
    public static String zeroPicCode = "table_yellow";//零值箭头

    //scrollview与linebarchart滚动的条件
    public static boolean scrollToLeft;//是否可以往左边滑动(手指往左边滑动)
    public static boolean scrollToRight;//是否可以往右边滑动(手指往右边滑动)
}
