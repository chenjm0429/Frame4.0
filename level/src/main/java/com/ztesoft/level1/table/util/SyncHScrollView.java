package com.ztesoft.level1.table.util;

import com.ztesoft.level1.Level1Bean;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * 自定义HorizontalScrollView
 *
 * @author fanlei@asiainfo-linkage.com  2012-6-25 下午3:09:07
 */
public class SyncHScrollView extends HorizontalScrollView {

    //HorizontalScrollView滑动时联动的视图
    private View mView;

    public SyncHScrollView(Context context) {
        super(context);
    }

    public SyncHScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SyncHScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 重写onScrollChanged方法
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        //控制越界时由表格滚动改为页面滑动
        if (getScrollX() + getWidth() >= computeHorizontalScrollRange()) {//滑动距离+显示宽度>实际宽度
            Level1Bean.scrollToLeft = false;
            Level1Bean.scrollToRight = true;
        } else if (getScrollX() <= 0) {//滑动距离<=0
            Level1Bean.scrollToLeft = true;
            Level1Bean.scrollToRight = false;
        } else {
            Level1Bean.scrollToLeft = true;
            Level1Bean.scrollToRight = true;
        }
        super.onScrollChanged(l, t, oldl, oldt);
        if (mView != null) {//表头不为空时，表头跟着滑动
            mView.scrollTo(l, t);
        }
    }

    /**
     * HorizontalScrollView滑动时联动的视图
     *
     * @param view
     */
    public void setScrollView(View view) {
        mView = view;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean flag = super.onInterceptTouchEvent(ev);
        return flag;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean flag = super.onTouchEvent(ev);
        return flag;
    }
}