package com.ztesoft.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import com.ztesoft.fusion.FusionCode;
import com.ztesoft.level1.Level1Bean;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 文件名称 : ErrorLogUtil
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 系统错误日志工具类，非Crash错误
 * <p>
 * 创建时间 : 2017/3/23 17:04
 * <p>
 */
public class ErrorLogUtil {

    // ErrorLogUtil实例
    private static ErrorLogUtil INSTANCE = new ErrorLogUtil();
    // 用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    // 用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA);

    /**
     * 保证只有一个ErrorLogUtil实例
     */
    private ErrorLogUtil() {
    }

    /**
     * 获取ErrorLogUtil实例 ,单例模式
     */
    public static ErrorLogUtil getInstance() {
        return INSTANCE;
    }

    /**
     * 写入日志
     *
     * @param context
     */
    public void log(Context context, String message) {

        collectDeviceInfo(context);
        saveCrashInfo2File(message);
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(String message) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }
        sb.append("info" + "=" + message + "\n");
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "log-" + time + "-" + timestamp + ".log";

            String path = Level1Bean.SD_ROOTPATH + FusionCode.ERROR_LOCAL_PATH;
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(path + fileName);
            fos.write(sb.toString().getBytes());
            fos.close();

            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
