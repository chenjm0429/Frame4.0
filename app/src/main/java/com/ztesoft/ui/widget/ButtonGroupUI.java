package com.ztesoft.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.ztesoft.R;
import com.ztesoft.utils.Utils;

import java.lang.reflect.Method;

/**
 * 按钮组合控件
 *
 * @author chenjm
 * @date 2014年8月26日
 */
public class ButtonGroupUI {

    private String methodName = null;
    private View backCls = null;
    private Class<?> subCls = null;
    private Context context;
    private String[] buttonNames;
    private String[] buttonCodes;
    private LinearLayout parentLayout;
    private int currPosition = 0;

    private int textSize = 16;
    private int width; // 控件宽度，默认宽度为屏幕的一半
    private int mm; // 每个按钮增加部分

    /***
     * 回调函数在Activity里
     *
     * @param context      上下文
     * @param parentLayout 父容器
     * @param buttonNames  name数组
     * @param buttonCodes  code数组
     * @param method       回调方法
     */
    public ButtonGroupUI(Context context, LinearLayout parentLayout,
                         String[] buttonNames, String[] buttonCodes, String method) {

        width = Utils.getDeviceWidth(context) / 2;
        drawLayout(context, parentLayout, buttonNames, buttonCodes, method, null);
    }

    public ButtonGroupUI(Activity act, LinearLayout parentLayout, String[] buttonNames, String[]
            buttonCodes, String method, int textSize, int width) {

        this.textSize = textSize;
        this.width = width;

        drawLayout(act, parentLayout, buttonNames, buttonCodes, method, null);
    }

    /***
     * 回调函数在ParentModule里
     *
     * @param context
     * @param parentLayout
     * @param buttonNames
     * @param buttonCodes
     * @param method
     * @param backCls
     */
    public ButtonGroupUI(Context context, LinearLayout parentLayout, String[] buttonNames,
                         String[] buttonCodes, String method, View backCls) {

        width = Utils.getDeviceWidth(context) / 2;

        drawLayout(context, parentLayout, buttonNames, buttonCodes, method, backCls);
    }

    public void drawLayout(Context context, LinearLayout parentLayout, String[] buttonNames,
                           String[] buttonCodes, String method, View backCls) {
        this.parentLayout = parentLayout;
        this.methodName = method;
        this.backCls = backCls;
        this.context = context;
        this.buttonNames = buttonNames;
        this.buttonCodes = buttonCodes;
        try {
            subCls = Class.forName(((Activity) context).getClass().getName());
            if (backCls != null) {
                subCls = Class.forName(backCls.getClass().getName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        TextView childButton;
        parentLayout.setOrientation(LinearLayout.HORIZONTAL);
        String btnName;

        float w = 0;
        for (int i = 0; i < buttonNames.length; i++) {
            String name = buttonNames[i];
            TextView tv = new TextView(context);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            TextPaint textpaint = tv.getPaint();
            float textlength = textpaint.measureText(name) + 20;
            if (textlength < 80) {
                textlength = 80;
            }
            w = w + textlength;
        }
        mm = ((int) (width - w)) / buttonNames.length;

        for (int i = 0; i < buttonNames.length; i++) {

            childButton = new TextView(context);
            childButton.setPadding(0, 3, 0, 3);
            childButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            childButton.setTextColor(Color.WHITE);

            btnName = buttonNames[i];
            childButton.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            childButton.setSingleLine(true);
            childButton.setText(btnName);
            childButton.setBackgroundResource(R.drawable.button_group_mid_normal);
            childButton.setGravity(Gravity.CENTER);
            TextPaint textpaint = childButton.getPaint();
            float textlength = textpaint.measureText(btnName) + 20;

            if (textlength < 80) {
                textlength = 80;
            }

            // 设置每个按钮的宽度
            parentLayout.addView(childButton, (int) textlength + mm, LayoutParams.MATCH_PARENT);

            childButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView onclickButton = (TextView) view;
                    backFunc(onclickButton);
                }
            });
        }

        /**
         * 改变头尾按钮样式
         */
        TextView firstText = (TextView) parentLayout.getChildAt(0);
        firstText.setBackgroundResource(R.drawable.button_group_left_normal);

        TextView lastText = (TextView) parentLayout.getChildAt(parentLayout.getChildCount() - 1);
        lastText.setBackgroundResource(R.drawable.button_group_right_normal);
    }

    public void backFunc(View btn) {
        int num = parentLayout.getChildCount();
        TextView tempButton = null;
        for (int i = 0; i < num; i++) {
            tempButton = (TextView) parentLayout.getChildAt(i);

            if (tempButton == btn) {
                currPosition = i;
                if (i == 0) {
                    tempButton.setBackgroundResource(R.drawable.button_group_left_click);
                } else if (i == num - 1) {
                    tempButton.setBackgroundResource(R.drawable.button_group_right_click);
                } else {
                    tempButton.setBackgroundResource(R.drawable.button_group_mid_click);
                }
                tempButton.setTextColor(Color.WHITE);
            } else {
                if (i == 0) {
                    tempButton.setBackgroundResource(R.drawable.button_group_left_normal);
                } else if (i == num - 1) {
                    tempButton.setBackgroundResource(R.drawable.button_group_right_normal);
                } else {
                    tempButton.setBackgroundResource(R.drawable.button_group_mid_normal);
                }
                tempButton.setTextColor(Color.parseColor("#1e95ff"));
            }
        }
        if (methodName != null && methodName.trim().length() > 0) {
            Object object;
            try {
                object = context;
                Method method = null;
                if (backCls != null) {
                    object = backCls;
                }

                method = subCls.getMethod(this.methodName);
                method.setAccessible(true);
                method.invoke(object);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 获取值
     *
     * @return
     */
    public String getValue() {
        return buttonCodes[currPosition];
    }

    public String getText() {
        return buttonNames[currPosition];
    }

    public void setValue(String code) {
        for (int i = 0; i < buttonCodes.length; i++) {

            TextView tempButton = (TextView) parentLayout.getChildAt(i);

            if (code.equals(buttonCodes[i])) {
                currPosition = i;
                if (i == 0) {
                    tempButton.setBackgroundResource(R.drawable.button_group_left_click);
                } else if (i == buttonCodes.length - 1) {
                    tempButton.setBackgroundResource(R.drawable.button_group_right_click);
                } else {
                    tempButton.setBackgroundResource(R.drawable.button_group_mid_click);
                }
                tempButton.setTextColor(Color.WHITE);
            } else {
                if (i == 0) {
                    tempButton.setBackgroundResource(R.drawable.button_group_left_normal);
                } else if (i == buttonCodes.length - 1) {
                    tempButton.setBackgroundResource(R.drawable.button_group_right_normal);
                } else {
                    tempButton.setBackgroundResource(R.drawable.button_group_mid_normal);
                }
                tempButton.setTextColor(Color.parseColor("#1e95ff"));
            }
        }
    }

    public int getCurrPosition() {
        return currPosition;
    }

    public void setCurrPosition(int currPosition) {
        this.currPosition = currPosition;
    }
}
