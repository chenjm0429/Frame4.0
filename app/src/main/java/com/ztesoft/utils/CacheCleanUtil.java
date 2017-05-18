package com.ztesoft.utils;

import android.content.Context;
import android.text.format.Formatter;

import com.ztesoft.fusion.CacheField;
import com.ztesoft.fusion.FusionCode;
import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.util.SharedPreferencesUtil;

import java.io.File;

/**
 * 文件名称 : CacheCleanUtil
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 清空缓存工具类，具有特殊性，只适用于本应用
 * <p>
 * 创建时间 : 2017/3/24 9:35
 * <p>
 */
public class CacheCleanUtil {

    /**
     * 获取缓存大小，只统计下载的图片、文件、错误日志等的大小，其它未统计
     *
     * @return
     */
    public static String getCacheSize(Context context) {

        File file = new File(Level1Bean.SD_ROOTPATH + FusionCode.FILE_LOCALPATH);
        long size = DataCleanUtil.getFileSize(file);

        File errorFile = new File(Level1Bean.SD_ROOTPATH + FusionCode.ERROR_LOCALPATH);
        long errorSize = DataCleanUtil.getFileSize(errorFile);

        return Formatter.formatFileSize(context, size + errorSize);
    }

    /**
     * 清空缓存，现在只清空方便清空的文件，其它相对困难的以后增加
     */
    public static void cleanCache(Context context) {

        // 下载的图片、文件等
        DataCleanUtil.cleanCustomCache(Level1Bean.SD_ROOTPATH + FusionCode.IMAGES_LOCALPATH);
        DataCleanUtil.cleanCustomCache(Level1Bean.SD_ROOTPATH + FusionCode.AUTOUPDATE_LOCALPATH);
        DataCleanUtil.cleanCustomCache(Level1Bean.SD_ROOTPATH + FusionCode.MAIL_LOCALPATH);
        DataCleanUtil.cleanCustomCache(Level1Bean.SD_ROOTPATH + FusionCode.FILE_LOCALPATH);

        DataCleanUtil.cleanCustomCache(Level1Bean.SD_ROOTPATH + FusionCode.ERROR_LOCALPATH);

        DataCleanUtil.cleanSharedPreference(context);

        SharedPreferencesUtil mPrefs = new SharedPreferencesUtil(context, "Demo");
        mPrefs.clear();

        new SharedPreferencesUtil(context, Level1Bean.SHARE_PREFERENCES_NAME).clear();

        // 软应用数据
        CacheField.clearCache();
    }
}
