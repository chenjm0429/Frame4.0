package com.ztesoft.ui.widget.cycleview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 文件名称 : CustomViewPager
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : CycleView控件使用，存放轮播视图
 * <p>
 * 创建时间 : 2017/3/24 14:46
 * <p>
 */
public class CustomViewPager extends ViewPager {


    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }
}
