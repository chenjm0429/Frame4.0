package com.ztesoft.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ztesoft.R;
import com.ztesoft.level1.util.PromptUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件名称 : Utils
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 公共方法
 * <p>
 * 创建时间 : 2017/3/24 10:25
 * <p>
 */
public class Utils {

    /**
     * 音频文件
     */
    public static final int MUSIC = 1;

    /**
     * 图片文件
     */
    public static final int PHOTO = 2;

    /**
     * 视频文件
     */
    public static final int VIDEO = 3;

    /**
     * 获取版本名称
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
                    .versionName;
        } catch (NameNotFoundException e) {
            PromptUtils.instance.displayToastId(context, true, R.string.error_version);
            e.printStackTrace();
        }

        return versionName;
    }

    /**
     * 获取版本号
     */
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
                    .versionCode;
        } catch (NameNotFoundException e) {
            PromptUtils.instance.displayToastId(context, true, R.string.error_version);
            e.printStackTrace();
        }

        return versionCode;
    }

    /**
     * 针对view隐藏输入法
     *
     * @param context
     */
    public static void hideInput(Context context, View view) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context
                .INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 针对context隐藏输入法
     *
     * @param context
     */
    public static void hideInput(Context context) {
        View view = ((Activity) context).getCurrentFocus();
        if (null != view) {
            if (null != view.getWindowToken()) {
                ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager
                                .HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 显示输入法
     *
     * @param context
     */
    public static void showInput(Context context, View view) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context
                .INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 遍历指定文件夹下的资源文件
     *
     * @param folder 文件
     */
    public static List<String> simpleScanning(File folder, int fileType) {

        List<String> list = new ArrayList<String>();

        // 指定正则表达式
        Pattern mPattern = Pattern.compile("([^\\.]*)\\.([^\\.]*)");
        // 当前目录下的所有文件
        final String[] filenames = folder.list();
        // 当前目录的名称
        // final String folderName = folder.getName();
        // 当前目录的绝对路径
        // final String folderPath = folder.getAbsolutePath();
        if (filenames != null) {
            // 遍历当前目录下的所有文件
            for (String name : filenames) {
                File file = new File(folder, name);

                if (file.isDirectory()) {// 如果是文件夹则继续递归当前方法
                    simpleScanning(file, fileType);

                } else {// 如果是文件则对文件进行相关操作
                    Matcher matcher = mPattern.matcher(name);
                    if (matcher.matches()) {
                        // 文件名称
                        // String fileName = matcher.group(1);
                        // 文件后缀
                        String fileExtension = matcher.group(2);
                        // 文件路径
                        String filePath = file.getAbsolutePath();

                        if (fileType == Utils.MUSIC) {
                            if (Utils.isMusic(fileExtension)) {
                                list.add(filePath);
                            }

                        } else if (fileType == Utils.PHOTO) {
                            if (Utils.isPhoto(fileExtension)) {
                                list.add(filePath);
                            }

                        } else if (fileType == Utils.VIDEO) {
                            if (Utils.isVideo(fileExtension)) {
                                list.add(filePath);
                            }

                        } else {
                            list.add(filePath);
                        }
                    }
                }
            }
        }

        return list;
    }

    /**
     * 判断是否是音乐文件
     *
     * @param extension 后缀名
     * @return
     */
    public static boolean isMusic(String extension) {
        if (extension == null)
            return false;

        final String ext = extension.toLowerCase(Locale.ENGLISH);
        if (ext.equals("mp3") || ext.equals("m4a") || ext.equals("wav") || ext.equals("amr") ||
                ext.equals("awb") || ext.equals("aac") || ext.equals("flac") || ext.equals("mid")
                || ext.equals("midi") || ext.equals("xmf") || ext.equals("rtttl") || ext.equals
                ("rtx") || ext.equals("ota") || ext.equals("wma") || ext.equals("ra") || ext
                .equals("mka") || ext.equals("m3u") || ext.equals("pls")) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是图像文件
     *
     * @param extension 后缀名
     * @return
     */
    public static boolean isPhoto(String extension) {
        if (extension == null)
            return false;

        final String ext = extension.toLowerCase(Locale.ENGLISH);
        if (ext.endsWith("jpg") || ext.endsWith("jpeg") || ext.endsWith("gif") || ext.endsWith
                ("png") || ext.endsWith("bmp") || ext.endsWith("wbmp")) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是视频文件
     *
     * @param extension 后缀名
     * @return
     */
    public static boolean isVideo(String extension) {
        if (extension == null)
            return false;

        final String ext = extension.toLowerCase(Locale.ENGLISH);
        if (ext.endsWith("mpeg") || ext.endsWith("mp4") || ext.endsWith("mov") || ext.endsWith
                ("m4v") || ext.endsWith("3gp") || ext.endsWith("3gpp") || ext.endsWith("3g2") ||
                ext.endsWith("3gpp2") || ext.endsWith("avi") || ext.endsWith("divx") || ext
                .endsWith("wmv") || ext.endsWith("asf") || ext.endsWith("flv") || ext.endsWith
                ("mkv") || ext.endsWith("mpg") || ext.endsWith("rmvb") || ext.endsWith("rm") ||
                ext.endsWith("vob") || ext.endsWith("f4v")) {
            return true;
        }
        return false;
    }
}
