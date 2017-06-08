package com.ztesoft.level1.hscrollframe;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ztesoft.level1.Level1Util;
import com.ztesoft.level1.R;

/**
 * 文件名称 : HScrollControlView
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 多试图左右滑动控件指示器
 * <p>
 * 创建时间 : 2017/5/24 14:01
 * <p>
 */
public class HScrollControlView extends LinearLayout implements HScrollFrame
        .OnScreenChangeListener {

    private Context context;

    private int dot = R.drawable.dot_1;
    private int dot1 = R.drawable.dot_2;

    private HScrollFrame.OnScreenChangeListener mChangeListener;

    /**
     * 左右滑动提示小圆点，作为监听器传入HScrollFrame中
     *
     * @param context        上下文
     * @param changeListener 状态改变监听器
     * @see HScrollFrame#setOnScreenChangeListener(HScrollFrame.OnScreenChangeListener)
     */
    public HScrollControlView(Context context, HScrollFrame.OnScreenChangeListener changeListener) {
        super(context);
        this.context = context;
        this.mChangeListener = changeListener;
        this.setGravity(Gravity.CENTER);
    }

    /**
     * 左右滑动提示小圆点 一般和左右滑动控件一起使用
     *
     * @param context        上下文
     * @param changeListener 状态改变监听器
     * @param dot            选中图标
     * @param dot1           未选中图标
     */
    public HScrollControlView(Context context, HScrollFrame.OnScreenChangeListener
            changeListener, int dot, int dot1) {
        this(context, changeListener);
        this.dot = dot;
        this.dot1 = dot1;
    }

    @Override
    public void screenChange(int currentTab, int totalTab) {
        this.removeAllViews();

        if (mChangeListener != null) {
            mChangeListener.screenChange(currentTab, totalTab);
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
            }
        }
    }
}
