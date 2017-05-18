package com.ztesoft.level1.hscrollframe;

import java.lang.reflect.Method;

import com.ztesoft.level1.Level1Util;
import com.ztesoft.level1.R;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class HScrollControlView extends LinearLayout implements HScrollFrame
        .OnScreenChangeListener {
    private Context context;
    private String methodName;
    private int dot = R.drawable.dot_1;
    private int dot1 = R.drawable.dot_2;

    /**
     * 左右滑动提示小圆点 一般和左右滑动控件一起使用
     *
     * @param context
     * @param methodName 回调方法名，有且仅有一个int入参，表示当前页码
     */
    public HScrollControlView(Context context, String methodName) {
        super(context);
        this.context = context;
        this.methodName = methodName;
        this.setGravity(Gravity.CENTER);
    }

    /**
     * 左右滑动提示小圆点 一般和左右滑动控件一起使用
     *
     * @param context
     * @param methodName 回调方法名，有且仅有一个int入参，表示当前页码
     * @param dot        选中图标
     * @param dot1       未选中图标
     */
    public HScrollControlView(Context context, String methodName, int dot, int dot1) {
        this(context, methodName);
        this.dot = dot;
        this.dot1 = dot1;
    }

    @Override
    public void screenChange(int currentTab, int totalTab) {
        this.removeAllViews();
        if (!TextUtils.isEmpty(methodName)) {
            try {
                Class<?> yourClass = Class.forName(context.getClass().getName());
                Method method = yourClass.getMethod(methodName, int.class);//有且仅有一个int入参
                method.setAccessible(true);//提高反射速度
                method.invoke(context, currentTab);// 调用方法
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (totalTab > 1) {
            for (int i = 0; i < totalTab; i++) {
                ImageView iv = new ImageView(context);
                if (i == currentTab) {
                    iv.setImageResource(dot);
                } else {
                    iv.setImageResource(dot1);
                }
                this.addView(iv);

                this.addView(new View(context), Level1Util.dip2px(context, 10), 0);

//				Button bb3 = new Button(context);//圆点间空隙
//				bb3.setWidth(8);
//				bb3.setHeight(8);
//				bb3.setBackgroundColor(Color.parseColor("#00000000"));
//				this.addView(bb3);
            }
        }
    }
}
