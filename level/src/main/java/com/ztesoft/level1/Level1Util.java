package com.ztesoft.level1;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 文件名称 : Level1Util
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 工具类
 * <p>
 * 创建时间 : 2017/5/10 15:02
 * <p>
 */
public class Level1Util {

    /**
     * 长度单位从dp转换为px
     *
     * @param context 上下文
     * @param dpValue dp长度
     * @return px长度
     */
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获取屏幕的密度
     *
     * @param context 上下文
     */
    public static float getDeviceDensity(Context context) {
        DisplayMetrics dm = new DisplayMetrics();

        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);

        return dm.density;
    }

    /**
     * 获取屏幕高度
     *
     * @param context 上下文
     */
    public static int getDeviceHeight(Context context) {

        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);

        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);

        return dm.heightPixels;
    }

    /**
     * 获取屏幕的宽
     *
     * @param context 上下文
     */
    public static int getDeviceWidth(Context context) {

        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);

        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);

        return dm.widthPixels;
    }


    /**
     * 百分比宽度
     *
     * @param value 当前值
     * @param width 总宽度
     * @return
     */
    public static int widthPercent(double value, double width) {
        if (width <= 0 || width > Level1Bean.actualWidth)
            width = Level1Bean.actualWidth;
        return (int) (value / 100 * width);
    }

    /**
     * 百分比高度
     *
     * @param value  当前值
     * @param height 总高度
     * @return
     */
    public static int heightPercent(double value, double height) {
        if (height <= 0 || height > Level1Bean.actualHeight)
            height = Level1Bean.actualHeight;
        return (int) (value / 100 * height);
    }

    public static int getDipSize(float size) {
        return getRawSize(null, TypedValue.COMPLEX_UNIT_DIP, size);
    }

    /**
     * 仅限图形控件绘制文字时使用
     *
     * @param size
     * @return
     */
    public static int getSpSize(float size) {
        return getRawSize(null, TypedValue.COMPLEX_UNIT_SP, size);
    }

    /**
     * 获取当前分辨率下指定单位对应的像素大小（根据设备信息）
     * px,dip,sp -> px
     * Paint.setTextSize()单位为px
     *
     * @param unit TypedValue.COMPLEX_UNIT_*
     * @param size
     * @return
     */
    public static int getRawSize(Context context, int unit, float size) {
        Resources resources;
        if (context == null) {
            resources = Resources.getSystem();
        } else {
            resources = context.getResources();
        }
        return (int) TypedValue.applyDimension(unit, size, resources.getDisplayMetrics());
    }

    /**
     * 色块效果
     *
     * @param context
     * @param color   内部填充色
     * @return
     */
    public static GradientDrawable MAIN_RADIO_NOBORD(Context context, int color) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        int radio = Level1Util.dip2px(context, 4);
        gd.setCornerRadii(new float[]{radio, radio, radio, radio, radio, radio, radio, radio});
        gd.setShape(GradientDrawable.RECTANGLE);//设置形状为矩形
        return gd;
    }

    /**
     * 边框效果
     *
     * @param context
     * @param color      内部填充色
     * @param stokecolor 边框色
     * @param stokeWidth 边框宽度
     * @return
     */
    public static GradientDrawable MAIN_RADIO_BORD(Context context, int color, int stokecolor, int
            stokeWidth) {
        int radio = Level1Util.dip2px(context, 4);
        return MAIN_RADIO_BORD(context, color, stokecolor, stokeWidth, radio);
    }

    /**
     * 边框效果
     *
     * @param context
     * @param color      内部填充色
     * @param stokecolor 边框色
     * @param stokeWidth 边框宽度
     * @param radio      0表示直角
     * @return
     */
    public static GradientDrawable MAIN_RADIO_BORD(Context context, int color, int stokecolor, int
            stokeWidth, int radio) {
        GradientDrawable gd = new GradientDrawable();
        gd.setStroke(stokeWidth, stokecolor);
        gd.setColor(color);
        gd.setCornerRadii(new float[]{radio, radio, radio, radio, radio, radio, radio, radio});
        gd.setShape(GradientDrawable.RECTANGLE);//设置形状为矩形
        return gd;
    }

    /**
     * 右侧边框效果
     *
     * @param context
     * @param color      内部填充色
     * @param stokecolor 边框色
     * @param stokeWidth 边框宽度
     * @return
     */
    public static GradientDrawable MAIN_RADIO_BORD_RIGHT(Context context, int color, int stokecolor,
                                                         int stokeWidth, int radio) {
        GradientDrawable gd = new GradientDrawable();
        gd.setStroke(stokeWidth, stokecolor);
        gd.setColor(color);
        gd.setCornerRadii(new float[]{0, 0, radio, radio, radio, radio, 0, 0});
        gd.setShape(GradientDrawable.RECTANGLE);//设置形状为矩形
        return gd;
    }

    /**
     * 左侧边框效果
     *
     * @param context
     * @param color      内部填充色
     * @param stokecolor 边框色
     * @param stokeWidth 边框宽度
     * @return
     */
    public static GradientDrawable MAIN_RADIO_BORD_LEFT(Context context, int color, int stokecolor,
                                                        int stokeWidth, int radio) {
        GradientDrawable gd = new GradientDrawable();
        gd.setStroke(stokeWidth, stokecolor);
        gd.setColor(color);
        gd.setCornerRadii(new float[]{radio, radio, 0, 0, 0, 0, radio, radio});
        gd.setShape(GradientDrawable.RECTANGLE);//设置形状为矩形
        return gd;
    }

    public static String ColorToString(int c) {
        return "#" + Integer.toHexString(c);
    }

    /***
     * 文本框渲染成按钮
     * @param context
     * @param txt
     */
    public static void textToButtonStyle(Context context, TextView txt, int color) {
        int pad = Level1Util.dip2px(context, 10);
        txt.setPadding(pad, pad / 2, pad, pad / 2);
        GradientDrawable gd = MAIN_RADIO_NOBORD(context, color);
        txt.setBackgroundDrawable(gd);
    }
}
