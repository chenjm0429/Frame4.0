package com.ztesoft.level1.hscrollframe;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.ztesoft.level1.Level1Bean;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 文件名称 : HScrollFrame
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 多视图左右滑动框架
 * <p>
 * 创建时间 : 2017/5/24 14:00
 * <p>
 */
public class HScrollFrame extends ViewGroup {

    /**
     * 用来平滑过渡各个页面之间的切换
     */
    private Scroller mScroller;
    /**
     * 用来跟踪触摸速度的类
     */
    private VelocityTracker mVelocityTracker;

    private int mCurScreen = 0;// 默认展现第一个界面

    private final int TOUCH_STATE_REST = 0;// 手指离开屏幕状态
    private final int TOUCH_STATE_SCROLLING = 1;// 手指还在滑动状态
    private final int SNAP_VELOCITY = 400;// 滑动速度，在touch_up时获取

    private int mTouchState = TOUCH_STATE_REST;// 手指与屏幕是否接触状态
    private int mTouchSlop;
    /**
     * 按下去横坐标的位置
     */
    private float mLastMotionX;
    /**
     * 按下去纵坐标的位置
     */
    private float mLastMotionY;

    private OnScreenChangeListener onScreenChangeListener;

    /**
     * 自动切换时长
     */
    private long intervalTime = 3000;
    private Timer mTimer;

    /**
     * 左右滑动页面框架
     *
     * @param context
     */
    public HScrollFrame(Context context) {
        super(context);
        /** 使用缺省的持续时间和动画插入器创建一个Scroller */
        mScroller = new Scroller(context);
        /** 是一个距离，表示滑动的时候，手的移动要大于这个距离才开始移动控件 */
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /**
     * 为每一个子view指定size和position
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        /** 子view离父view左边的距离 */
        int childLeft = 0;
        /** 获取子view数目 */
        final int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            /** 获取子view */
            final View childView = getChildAt(i);

            if (childView.getVisibility() != View.GONE) {// 如果子view可见的话
                /** 获取子view的宽度 */
                final int childWidth = childView.getMeasuredWidth();
                /** 为子view设置大小和位置 */
                childView.layout(childLeft, 0, childLeft + childWidth, childView
                        .getMeasuredHeight());
                /** 左边距自加子view宽度，从而得到下一个子view的x坐标 */
                childLeft += childWidth;
            }
        }
    }

    /**
     * 指明控件可获得的空间以及关于这个空间描述的元数据
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * int specMode = MeasureSpec.getMode(measureSpec); int specSize =
         * MeasureSpec.getSize(measureSpec); 依据specMode的值，如果是AT_MOST，specSize
         * 代表的是最大可获得的空间； 如果是EXACTLY，specSize 代表的是精确的尺寸；
         * 如果是UNSPECIFIED，对于控件尺寸来说，没有任何参考意义。
         */
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("ScrollLayout only canmCurScreen run at EXACTLY mode!");
        }

        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("ScrollLayout only can run at EXACTLY mode!");
        }

        // 给每一个子view给予相同的空间
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
        /** 滚动到目标坐标 */
        scrollTo(mCurScreen * widthSize, 0);
    }

    /**
     * 慢速滑动，根据滑动距离判断当前页码，并调用snapToScreen进行滑动
     */
    private void snapToDestination() {
        int screenWidth = getWidth();
        int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
        snapToScreen(destScreen);
    }

    /**
     * 根据当前页码，调整页面位置，显示在屏幕中间
     *
     * @param whichScreen
     */
    public void snapToScreen(int whichScreen) {
        // 获取有效页面
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        if (getScrollX() != (whichScreen * getWidth())) {
            int delta = whichScreen * getWidth() - getScrollX();
            mScroller.startScroll(getScrollX(), 0, delta, 0);
            setCurPageNum(whichScreen);
            invalidate();// 使view重画
        }
    }

    /**
     * 由父视图调用，用于通知子视图在必要时更新 mScrollX 和 mScrollY 的值 该操作主要用于子视图使用 Scroller
     * 进行动画滚动时。
     */
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {// 返回true，表示动画仍在进行，还没有停止
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());// 滚动到目标坐标
            postInvalidate(); // 使view重画
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();// 获取mVelocityTracker实例对象
        }

        /** 将当前的移动事件传递给mVelocityTracker对象 */
        mVelocityTracker.addMovement(event);

        /** 获取当前触摸动作 */
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:// 当向下按时
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();// Scrooller停止动画行为
                }
                mLastMotionX = x;
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:// 当手指滑动时
                int deltaX = (int) (mLastMotionX - x);
                int deltaY = (int) (mLastMotionY - y);
                mLastMotionX = x;
                mLastMotionY = y;

                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    scrollBy(deltaX, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
                /** 计算当前速度 */
                mVelocityTracker.computeCurrentVelocity(1000);
                /** 获取当前x方向的速度 */
                int velocityX = (int) mVelocityTracker.getXVelocity();
                int velocityY = (int) mVelocityTracker.getYVelocity();

                // 获取Y轴滑动速度，当大于X轴滑动速度时，不切换页面
                if (Math.abs(velocityY) > Math.abs(velocityX)) {
                    snapToDestination();
                    return true;
                }
                if (velocityX > SNAP_VELOCITY && mCurScreen > 0) { // 向右滑动并且手指滑动速度大于指定的速度(此时速度的方向为正)
                    // Fling enough to move left
                    snapToScreen(mCurScreen - 1);
                } else if (velocityX < -SNAP_VELOCITY && mCurScreen < getChildCount() - 1) {// 
                    // 向左滑动时并且手指滑动的速度也大于指定的速度(此时速度方向为负)
                    // Fling enough to move right
                    snapToScreen(mCurScreen + 1);
                } else {//慢速滑动
                    snapToDestination();
                }
                if (mVelocityTracker != null) {// 释放VelocityTracker对象
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                mTouchState = TOUCH_STATE_REST;
                break;
            case MotionEvent.ACTION_CANCEL:
                mTouchState = TOUCH_STATE_REST;
                mVelocityTracker.recycle();
                break;
        }

        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        int action = ev.getAction();
        // 双指传递给子控件
        if (ev.getPointerCount() > 1) {
            return false;
        }
        // 是否横向滑动------------X轴移动距离大于Y轴移动距离
        boolean xFlag = Math.abs(x - mLastMotionX) > Math.abs(y - mLastMotionY);

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                int xDiff = (int) Math.abs(mLastMotionX - x);
                if (xDiff > mTouchSlop * 2) {//增加范围，当mTouchSlop < xDiff < 
                    // mTouchSlop*2时，内部点击事件解锁给内部滑动事件
                    //当xDiff > mTouchSlop*2时，直接解锁框架滑动事件
                    //效果：缓慢滑动时内部滑动；快速滑动时框架滑动。
                    mTouchState = TOUCH_STATE_SCROLLING; // 视图还在移动状态
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                mLastMotionY = y;
                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
                Level1Bean.scrollToLeft = false;
                Level1Bean.scrollToRight = false;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mTouchState = TOUCH_STATE_REST;
                break;
        }

        if (mTouchState == TOUCH_STATE_REST) {
            return false;
        } else {
            boolean tag = !((x >= mLastMotionX && Level1Bean.scrollToRight) || (x <= mLastMotionX
                    && Level1Bean.scrollToLeft) || (Level1Bean.scrollToRight && Level1Bean
                    .scrollToLeft));
            return xFlag && tag;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    public interface OnScreenChangeListener {
        void screenChange(int currentTab, int totalTab);
    }

    /**
     * 注册监听事件
     *
     * @param onScreenChangeListener
     */
    public void setOnScreenChangeListener(OnScreenChangeListener onScreenChangeListener) {
        this.onScreenChangeListener = onScreenChangeListener;
    }

    /**
     * 显示第viewIndex个子页面，需要先注册监听 如设置监听，则该步骤会调用PageControlView中设置的回调方法
     *
     * @param viewIndex
     */
    public void setCurPageNum(int viewIndex) {
        mCurScreen = Math.max(0, Math.min(viewIndex, getChildCount() - 1));

        if (onScreenChangeListener != null) {
            onScreenChangeListener.screenChange(mCurScreen, getChildCount());
        }
    }

    /**
     * 获取当前页面位置，从0开始
     *
     * @return 当前页面位置，从0开始
     */
    public int getCurPageNum() {
        return mCurScreen;
    }

    public void setIntervalTime(long intervalTime) {
        this.intervalTime = intervalTime;
    }

    /**
     * 启动自动切换
     */
    public void start() {
        mTimer = new Timer();

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = mHandler.obtainMessage();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        }, intervalTime, intervalTime);
    }

    /**
     * 暂停自动切换
     */
    public void stop() {
        if (null != mTimer) {
            mTimer.cancel();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {
                int count = HScrollFrame.this.getChildCount();

                if (mCurScreen < count - 1) {
                    mCurScreen++;
                } else {
                    mCurScreen = 0;
                }
                snapToScreen(mCurScreen);
            }
        }
    };
}
