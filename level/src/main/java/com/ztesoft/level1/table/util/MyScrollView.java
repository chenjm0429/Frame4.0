package com.ztesoft.level1.table.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * MyScrollView <li>you can {@link #setOnBorderListener(OnBorderListener)} to
 * set your top and bottom response</li> </ul>
 */
public class MyScrollView extends ScrollView {

    private boolean isLoading = false;
    private OnBorderListener onBorderListener;
    private View contentView;
    private View parentScrollView;// 谨慎使用,只是在有2个scrollView嵌套的时候使用
    private boolean pEventFlag = false;// 父scrollview是否处理事件
    // 与parentScrollView一起启动，优先child
    // scrollView 滚动

    private int currentY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (parentScrollView == null) {
            return super.onInterceptTouchEvent(ev);
        } else {
            if (contentView != null) {
                // 如果内容小于scrollView高度 还是交给父scrollView
                int off = contentView.getMeasuredHeight() - getHeight();
                if (off <= 0) {
                    pEventFlag = true;
                }
            }
            if (!pEventFlag) {
                if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                    // 将父scrollview的滚动事件拦截
                    currentY = (int) ev.getY();
                    setParentScrollAble(false);
                    return super.onInterceptTouchEvent(ev);
                } else if (ev.getAction() == MotionEvent.ACTION_UP) {
                    // 把滚动事件恢复给父Scrollview
                    setParentScrollAble(true);
                } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                }
            }
        }
        return super.onInterceptTouchEvent(ev);

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        View child = getChildAt(0);
        if (parentScrollView != null) {
            if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                int height = child.getMeasuredHeight();
                height = height - getMeasuredHeight();

                // System.out.println("height=" + height);
                int scrollY = getScrollY();
                // System.out.println("scrollY" + scrollY);
                int y = (int) ev.getY();

                // 手指向下滑动
                if (currentY < y) {
                    if (scrollY <= 0) {
                        // 如果向下滑动到头，就把滚动交给父Scrollview
                        setParentScrollAble(true);
//						if (onBorderListener != null) {
//							onBorderListener.onTop();
//						}
                        return false;
                    } else {
                        setParentScrollAble(false);
                    }
                } else if (currentY > y) {
                    if (scrollY >= height) {
                        // 如果向上滑动到头，就把滚动交给父Scrollview
                        setParentScrollAble(true);
                        return false;
                    } else {
                        setParentScrollAble(false);
                    }
                }
                currentY = y;
            } else if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                isLoading = false;
            }
        } else {
            isLoading = false;
        }

        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        doOnBorderListener();
    }

    public void setOnBorderListener(final OnBorderListener onBorderListener) {
        this.onBorderListener = onBorderListener;
        if (onBorderListener == null) {
            return;
        }

        if (contentView == null) {
            contentView = getChildAt(0);
        }
    }

    private void doOnBorderListener() {
        if (onBorderListener != null && !isLoading) {
            if (contentView != null && contentView.getMeasuredHeight() <= getScrollY() + 
                    getHeight()) {
                onBorderListener.onBottom();
                isLoading = true;
            } else if (getScrollY() == 0) {
                onBorderListener.onTop();
                isLoading = true;
            }
            onBorderListener.onTopY(this);
        }
    }

    /**
     * 是否把滚动事件交给父scrollview
     *
     * @param flag
     */
    private void setParentScrollAble(boolean flag) {
        if (parentScrollView instanceof ScrollView) {
            ((ScrollView) parentScrollView).requestDisallowInterceptTouchEvent(!flag);
        } else if (parentScrollView instanceof ListView) {
            ((ListView) parentScrollView).requestDisallowInterceptTouchEvent(!flag);
        }
    }

    /**
     * 滑动阶段的接口事件
     */
    public static interface OnBorderListener {

        /**
         * 滑动到底部触发事件
         */
        void onBottom();

        /**
         * 滑动到顶部触发事件
         */
        void onTop();

        /***
         * 滑动到顶部Y触发事件
         */
        void onTopY(ScrollView sv);
    }

    public boolean ispEventFlag() {
        return pEventFlag;
    }

    public void setpEventFlag(boolean pEventFlag) {
        this.pEventFlag = pEventFlag;
    }

    public View getParentScrollView() {
        return parentScrollView;
    }

    public void setParentScrollView(View parentScrollView) {
        this.parentScrollView = parentScrollView;
    }

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}