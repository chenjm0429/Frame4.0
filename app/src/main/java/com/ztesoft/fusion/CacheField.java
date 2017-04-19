package com.ztesoft.fusion;

import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件名称 : CacheField
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 存放系统的缓存文件
 * <p>
 * 创建时间 : 2017/3/23 14:55
 * <p>
 */
public class CacheField {

    /**
     * 图片软引用
     */
    public static Map<String, SoftReference<BitmapDrawable>> mAdvPicCache = new HashMap<String,
            SoftReference<BitmapDrawable>>();

    /**
     * 消息推送使用的imsi号
     */
    public static String sim, userId, staffId;

    /**
     * 清空缓存
     */
    public static void clearCache() {
        if (CacheField.mAdvPicCache != null) {
            CacheField.mAdvPicCache.clear();
        }
    }
}
