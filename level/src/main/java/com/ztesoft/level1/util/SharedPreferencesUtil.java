package com.ztesoft.level1.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.ztesoft.level1.util.encrypt.AES256Help;

import java.util.List;
import java.util.Map;

/**
 * 文件名称 : SharedPreferencesUtil
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : SharedPreferences工具类，对String型加密处理
 * <p>
 * 创建时间 : 2017/4/7 9:30
 * <p>
 */
public class SharedPreferencesUtil {
    private final String MAK = "ztesoft-govmrkt-dev";

//    private final int MODE = Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE + Context
//            .MODE_PRIVATE;

    private final int MODE = Context.MODE_PRIVATE;

    private final SharedPreferences sharedpreferences;

    /**
     * 构造方法
     *
     * @param context  上下文
     * @param fileName 存储文件名，取Level1Bean中的SHARE_PREFERENCES_NAME
     */
    public SharedPreferencesUtil(Context context, String fileName) {
        sharedpreferences = context.getSharedPreferences(fileName, MODE);
    }

    /**
     * 存储String，默认加密，加密失败时直接存储
     *
     * @param key   键值
     * @param value 存储内容
     * @return 保存结果
     */
    public boolean putString(String key, String value) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        try {
            editor.putString(key, AES256Help.encrypt(value, MAK));
        } catch (Exception e) {
            editor.putString(key, value);
            e.printStackTrace();
        }
        return editor.commit();
    }

    /**
     * 获取存储的String，返回值不为空时解密
     *
     * @param key      键值
     * @param defValue 默认值
     * @return 存储的String
     */
    public String getString(String key, String defValue) {
        String str = null;
        try {
            str = sharedpreferences.getString(key, defValue);
            if (!TextUtils.isEmpty(str)) {
                str = AES256Help.decrypt(str, MAK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public boolean putInt(String key, int value) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public int getInt(String key) {
        return sharedpreferences.getInt(key, 0);
    }

    public int getInt(String key, int value) {
        return sharedpreferences.getInt(key, value);
    }

    public boolean putFloat(String key, float value) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    public float getFloat(String key) {
        return sharedpreferences.getFloat(key, 0f);
    }

    public boolean putLong(String key, Long value) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    public long getLong(String key) {
        return sharedpreferences.getLong(key, 0l);
    }

    public boolean putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public boolean getBoolean(String key) {
        return sharedpreferences.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean value) {
        return sharedpreferences.getBoolean(key, value);
    }

    public boolean saveAllSharePreference(String keyName, List<?> list) {
        int size = list.size();
        if (size < 1) {
            return false;
        }
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if (list.get(0) instanceof String) {
            for (int i = 0; i < size; i++) {
                editor.putString(keyName + i, (String) list.get(i));
            }
        } else if (list.get(0) instanceof Long) {
            for (int i = 0; i < size; i++) {
                editor.putLong(keyName + i, (Long) list.get(i));
            }
        } else if (list.get(0) instanceof Float) {
            for (int i = 0; i < size; i++) {
                editor.putFloat(keyName + i, (Float) list.get(i));
            }
        } else if (list.get(0) instanceof Integer) {
            for (int i = 0; i < size; i++) {
                editor.putLong(keyName + i, (Integer) list.get(i));
            }
        } else if (list.get(0) instanceof Boolean) {
            for (int i = 0; i < size; i++) {
                editor.putBoolean(keyName + i, (Boolean) list.get(i));
            }
        }
        return editor.commit();
    }

    public Map<String, ?> getAll() {
        return sharedpreferences.getAll();
    }

    public boolean remove(String key) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove(key);
        return editor.commit();
    }

    public boolean clear() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        return editor.commit();
    }

    /**
     * 判断是否存储了该键值
     *
     * @param key
     * @return
     */
    public boolean contains(String key) {
        return sharedpreferences.contains(key);
    }
}
