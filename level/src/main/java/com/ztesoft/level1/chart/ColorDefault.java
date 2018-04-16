package com.ztesoft.level1.chart;

import android.graphics.Color;

public class ColorDefault {

    public static String pieColors[] = {"#D94D4D", "#EBA538", "#87AB66", "#4BB3D2", "#FFF468", 
            "#A186BE", "#008E8E", "#9D080D", "#CC6600", "#ABA000", "#D94D4D", "#EBA538", 
            "#87AB66", "#4BB3D2", "#FFF468", "#A186BE", "#008E8E", "#9D080D", "#CC6600", "#ABA000"};

    public static String[] pieCenterColor = {"#FFFFFF", "#efefef"};//饼图中间渲染色

    public static String colors[] = {"#D94D4C", "#D9824D", "#EBA538", "#A6A538", "#87AB66", 
            "#87ABAD", "#4BB3D2", "#69C7FF", "#CC6600", "#ABA000", "#D94D4C", "#D9824D", 
            "#EBA538", "#A6A538", "#87AB66", "#87ABAD", "#4BB3D2", "#69C7FF", "#CC6600", "#ABA000"};

    /**
     * 渐变色
     */
    public static int applyDark(int color, int i) {
        int j = getRed(color);
        if (j > i)
            j -= i;
        else
            j = 0;
        int k = getGreen(color);
        if (k > i)
            k -= i;
        else
            k = 0;
        int l = getBlue(color);
        if (l > i)
            l -= i;
        else
            l = 0;
        return Color.argb(getAlpha(color), j, k, l);
    }

    public static int getRed(int color) {
        return android.graphics.Color.red(color);
    }

    public static int getGreen(int color) {
        return android.graphics.Color.green(color);
    }

    public static int getBlue(int color) {
        return android.graphics.Color.blue(color);
    }

    public static int getAlpha(int color) {
        return android.graphics.Color.alpha(color);
    }
}
