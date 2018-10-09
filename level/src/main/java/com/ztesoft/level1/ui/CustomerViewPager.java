package com.ztesoft.level1.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 文件名称 : CustomerViewPager
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 解决内部子控件有多点触控时，导致父类触控报错的问题
 * <p>
 * 创建时间 : 2017/12/12 15:16
 * <p>
 */
public class CustomerViewPager extends ViewPager {

    private boolean mIsDisallowIntercept = false;

    public CustomerViewPager(Context context) {
        super(context);
    }

    public CustomerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // keep the info about if the innerViews do  
        // requestDisallowInterceptTouchEvent  
        mIsDisallowIntercept = disallowIntercept;
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // the incorrect array size will only happen in the multi-touch  
        // scenario.  
        if (ev.getPointerCount() > 1 && mIsDisallowIntercept) {
            requestDisallowInterceptTouchEvent(false);
            boolean handled = super.dispatchTouchEvent(ev);
            requestDisallowInterceptTouchEvent(true);
            return handled;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }
}