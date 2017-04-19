package com.ztesoft.ui.widget;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * 文件名称 : MultiSlideMenuLayout
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 自定义滑动菜单控件
 * <p>
 * 创建时间 : 2017/3/24 16:00
 * <p>
 */
public class MultiSlideMenuLayout extends RelativeLayout implements
        OnTouchListener {

    /**
     * 滚动显示和隐藏左侧布局时，手指滑动需要达到的速度。
     */
    public static final int SNAP_VELOCITY = 200;

    /**
     * 滑动状态的一种，表示未进行任何滑动。
     */
    public static final int DO_NOTHING = 0;

    /**
     * 滑动状态的一种，表示正在滑出左侧菜单。
     */
    public static final int SHOW_LEFT_MENU = 1;

    /**
     * 滑动状态的一种，表示正在滑出右侧菜单。
     */
    public static final int SHOW_RIGHT_MENU = 2;

    /**
     * 滑动状态的一种，表示正在隐藏左侧菜单。
     */
    public static final int HIDE_LEFT_MENU = 3;

    /**
     * 滑动状态的一种，表示正在隐藏右侧菜单。
     */
    public static final int HIDE_RIGHT_MENU = 4;

    /**
     * 左菜单
     */
    private static boolean LEFT = false;

    /**
     * 右菜单
     */
    private static boolean RIGHT = false;

    /**
     * 布局数量
     */
    private static int mLayoutCount;

    /**
     * 记录当前的滑动状态
     */
    private int mSlideState;

    /**
     * 屏幕宽度值。
     */
    private int mScreenWidth;

    /**
     * 在被判定为滚动之前用户手指可以移动的最大值。
     */
    private int mTouchSlop;

    /**
     * 记录手指按下时的横坐标。
     */
    private float mXDown;

    /**
     * 记录手指按下时的纵坐标。
     */
    private float mYDown;

    /**
     * 记录手指移动时的横坐标。
     */
    private float mXMove;

    /**
     * 记录手指移动时的纵坐标。
     */
    private float mYMove;

    /**
     * 记录手机抬起时的横坐标。
     */
    private float mXUp;

    /**
     * 左侧菜单当前是显示还是隐藏。只有完全显示或隐藏时才会更改此值，滑动过程中此值无效。
     */
    private boolean mIsLeftMenuVisible;

    /**
     * 右侧菜单当前是显示还是隐藏。只有完全显示或隐藏时才会更改此值，滑动过程中此值无效。
     */
    private boolean mIsRightMenuVisible;

    /**
     * 是否正在滑动。
     */
    private boolean mIsSliding;

    /**
     * 左侧菜单布局对象。
     */
    private View mLeftMenuLayout;

    /**
     * 右侧菜单布局对象。
     */
    private View mRightMenuLayout;

    /**
     * 内容布局对象。
     */
    private View mContentLayout;

    /**
     * 用于监听滑动事件的View。
     */
    private View mBindView;

    /**
     * 左侧菜单布局的参数。
     */
    private MarginLayoutParams mLeftMenuLayoutParams;

    /**
     * 右侧菜单布局的参数。
     */
    private MarginLayoutParams mRightMenuLayoutParams;

    /**
     * 内容布局的参数。
     */
    private RelativeLayout.LayoutParams mContentLayoutParams;

    /**
     * 用于计算手指滑动的速度。
     */
    private VelocityTracker mVelocityTracker;

    /**
     * 重写MultiSlideMenuLayout的构造函数，其中获取了屏幕的宽度和mTouchSlop的值。
     *
     * @param context
     * @param attrs
     */
    public MultiSlideMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = wm.getDefaultDisplay().getWidth();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    /**
     * 绑定监听滑动事件的View。
     *
     * @param bindView 需要绑定的View对象。
     */
    public void setScrollEvent(View bindView) {
        mBindView = bindView;
        mBindView.setOnTouchListener(this);
    }

    /**
     * 绑定监听滑动事件的View
     *
     * @param bindView
     * @param LEFT     左边侧滑，当为true时，要保证布局与之对应
     * @param RIGHT    右边侧滑，当为true时，要保证布局与之对应
     *                 使用此方法时，确保LEFT和RIGHT中只有一个值能为true，当两个值都为true或false时无法实现预想的效果
     */

    public void setScrollEvent(View bindView, boolean LEFT, boolean RIGHT) {
        if (LEFT && RIGHT) {// 两个都是ture不处理

        } else {
            MultiSlideMenuLayout.LEFT = LEFT;
            MultiSlideMenuLayout.RIGHT = RIGHT;
        }

        mBindView = bindView;
        mBindView.setOnTouchListener(this);
    }

    /**
     * 将界面滚动到左侧菜单界面，滚动速度设定为-30.
     */
    public void scrollToLeftMenu() {
        new LeftMenuScrollTask().execute(-30);
    }

    /**
     * 将界面滚动到右侧菜单界面，滚动速度设定为-30.
     */
    public void scrollToRightMenu() {
        new RightMenuScrollTask().execute(-30);
    }

    /**
     * 将界面从左侧菜单滚动到内容界面，滚动速度设定为30.
     */
    public void scrollToContentFromLeftMenu() {
        new LeftMenuScrollTask().execute(30);
    }

    /**
     * 将界面从右侧菜单滚动到内容界面，滚动速度设定为30.
     */
    public void scrollToContentFromRightMenu() {
        new RightMenuScrollTask().execute(30);
    }

    /**
     * 左侧菜单是否完全显示出来，滑动过程中此值无效。
     *
     * @return 左侧菜单完全显示返回true，否则返回false。
     */
    public boolean isLeftLayoutVisible() {
        return mIsLeftMenuVisible;
    }

    /**
     * 右侧菜单是否完全显示出来，滑动过程中此值无效。
     *
     * @return 右侧菜单完全显示返回true，否则返回false。
     */
    public boolean isRightLayoutVisible() {
        return mIsRightMenuVisible;
    }

    /**
     * 显示左菜单
     */
    public void scrollLeftMenu() {
        if (mLayoutCount == 3 || mLayoutCount == 2 && LEFT) {

            if (isLeftLayoutVisible()) {
                scrollToContentFromLeftMenu();
            } else {
                initShowLeftState();
                // 如果用户想要滑动左侧菜单，将左侧菜单显示，右侧菜单隐藏
                if (mLayoutCount == 3) {
                    mRightMenuLayout.setVisibility(View.GONE);
                }
                mLeftMenuLayout.setVisibility(View.VISIBLE);
                scrollToLeftMenu();
            }
        }
    }

    /**
     * 显示右菜单
     */
    public void scrollRightMenu() {
        if (mLayoutCount == 3 || mLayoutCount == 2 && RIGHT) {

            if (isRightLayoutVisible()) {
                scrollToContentFromRightMenu();
            } else {
                initShowRightState();
                // 如果用户想要滑动右侧菜单，将右侧菜单显示，左侧菜单隐藏
                if (mLayoutCount == 3) {
                    mLeftMenuLayout.setVisibility(View.GONE);
                }
                mRightMenuLayout.setVisibility(View.VISIBLE);
                scrollToRightMenu();
            }
        }
    }

    /**
     * 左菜单初始化显示前初始化
     */
    public void initShowLeftState() {
        mContentLayoutParams.rightMargin = 0;
        mContentLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
        mContentLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mContentLayout.setLayoutParams(mContentLayoutParams);
        // 如果用户想要滑动左侧菜单，将左侧菜单显示，右侧菜单隐藏
        /*
         * leftMenuLayout.setVisibility(View.VISIBLE);
		 * rightMenuLayout.setVisibility(View.GONE);
		 */
    }

    /**
     * 右菜单显示前初始化
     */

    public void initShowRightState() {
        mContentLayoutParams.leftMargin = 0;
        mContentLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
        mContentLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        mContentLayout.setLayoutParams(mContentLayoutParams);
        // 如果用户想要滑动右侧菜单，将右侧菜单显示，左侧菜单隐藏
		/*
		 * rightMenuLayout.setVisibility(View.VISIBLE);
		 * leftMenuLayout.setVisibility(View.GONE);
		 */
    }

    /**
     * 在onLayout中重新设定左侧菜单、右侧菜单、以及内容布局的参数。
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            mLayoutCount = getChildCount();
            if (mLayoutCount == 2 && LEFT) {
                // 获取左侧菜单布局对象
                mLeftMenuLayout = getChildAt(0);
                mLeftMenuLayoutParams = (MarginLayoutParams) mLeftMenuLayout.getLayoutParams();
                // 获取内容布局对象
                mContentLayout = getChildAt(1);
                mContentLayoutParams = (RelativeLayout.LayoutParams) mContentLayout
                        .getLayoutParams();
                mContentLayoutParams.width = mScreenWidth;
                mContentLayout.setLayoutParams(mContentLayoutParams);
            } else if (mLayoutCount == 2 && RIGHT) {
                // 获取右侧菜单布局对象
                mRightMenuLayout = getChildAt(0);
                mRightMenuLayoutParams = (MarginLayoutParams) mRightMenuLayout.getLayoutParams();
                // 获取内容布局对象
                mContentLayout = getChildAt(1);
                mContentLayoutParams = (RelativeLayout.LayoutParams) mContentLayout
                        .getLayoutParams();
                mContentLayoutParams.width = mScreenWidth;
                mContentLayout.setLayoutParams(mContentLayoutParams);
            } else {
                // 获取左侧菜单布局对象
                mLeftMenuLayout = getChildAt(0);
                mLeftMenuLayoutParams = (MarginLayoutParams) mLeftMenuLayout.getLayoutParams();
                // 获取右侧菜单布局对象
                mRightMenuLayout = getChildAt(1);
                mRightMenuLayoutParams = (MarginLayoutParams) mRightMenuLayout.getLayoutParams();
                // 获取内容布局对象
                mContentLayout = getChildAt(2);
                mContentLayoutParams = (RelativeLayout.LayoutParams) mContentLayout
                        .getLayoutParams();
                mContentLayoutParams.width = mScreenWidth;
                mContentLayout.setLayoutParams(mContentLayoutParams);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        createVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下时，记录按下时的坐标
                mXDown = event.getRawX();
                mYDown = event.getRawY();
                // 将滑动状态初始化为DO_NOTHING
                mSlideState = DO_NOTHING;
                break;
            case MotionEvent.ACTION_MOVE:
                mXMove = event.getRawX();
                mYMove = event.getRawY();
                // 手指移动时，对比按下时的坐标，计算出移动的距离。
                int moveDistanceX = (int) (mXMove - mXDown);
                int moveDistanceY = (int) (mYMove - mYDown);
                // 检查当前的滑动状态
                checkSlideState(moveDistanceX, moveDistanceY);
                // 根据当前滑动状态决定如何偏移内容布局
                switch (mSlideState) {
                    case SHOW_LEFT_MENU:
                        if (mLayoutCount == 2 && LEFT || mLayoutCount == 3) {
                            mContentLayoutParams.rightMargin = -moveDistanceX;
                            checkLeftMenuBorder();
                            mContentLayout.setLayoutParams(mContentLayoutParams);
                        }
                        break;
                    case HIDE_LEFT_MENU:
                        if (mLayoutCount == 2 && LEFT || mLayoutCount == 3) {
                            mContentLayoutParams.rightMargin = -mLeftMenuLayoutParams.width - 
                                    moveDistanceX;
                            checkLeftMenuBorder();
                            mContentLayout.setLayoutParams(mContentLayoutParams);
                        }
                    case SHOW_RIGHT_MENU:
                        if (mLayoutCount == 2 && RIGHT || mLayoutCount == 3) {
                            mContentLayoutParams.leftMargin = moveDistanceX;
                            checkRightMenuBorder();
                            mContentLayout.setLayoutParams(mContentLayoutParams);
                        }
                        break;
                    case HIDE_RIGHT_MENU:
                        if (mLayoutCount == 2 && RIGHT || mLayoutCount == 3) {
                            mContentLayoutParams.leftMargin = -mRightMenuLayoutParams.width + 
                                    moveDistanceX;
                            checkRightMenuBorder();
                            mContentLayout.setLayoutParams(mContentLayoutParams);
                        }
                    default:
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                mXUp = event.getRawX();
                int upDistanceX = (int) (mXUp - mXDown);
                if (mIsSliding) {
                    // 手指抬起时，进行判断当前手势的意图
                    switch (mSlideState) {
                        case SHOW_LEFT_MENU:
                            if (mLayoutCount == 3 || LEFT) {
                                if (shouldScrollToLeftMenu()) {
                                    scrollToLeftMenu();
                                } else {
                                    scrollToContentFromLeftMenu();
                                }
                            }
                            break;
                        case HIDE_LEFT_MENU:
                            if (mLayoutCount == 3 || LEFT) {
                                if (shouldScrollToContentFromLeftMenu()) {
                                    scrollToContentFromLeftMenu();
                                } else {
                                    scrollToLeftMenu();
                                }
                            }
                            break;
                        case SHOW_RIGHT_MENU:
                            if (mLayoutCount == 3 || RIGHT) {
                                if (shouldScrollToRightMenu()) {
                                    scrollToRightMenu();
                                } else {
                                    scrollToContentFromRightMenu();
                                }
                            }
                            break;
                        case HIDE_RIGHT_MENU:
                            if (mLayoutCount == 3 || RIGHT) {
                                if (shouldScrollToContentFromRightMenu()) {
                                    scrollToContentFromRightMenu();
                                } else {
                                    scrollToRightMenu();
                                }
                            }
                            break;
                        default:
                            break;
                    }
                } else if (upDistanceX < mTouchSlop && mIsLeftMenuVisible) {
                    // 当左侧菜单显示时，如果用户点击一下内容部分，则直接滚动到内容界面
                    scrollToContentFromLeftMenu();
                } else if (upDistanceX < mTouchSlop && mIsRightMenuVisible) {
                    // 当右侧菜单显示时，如果用户点击一下内容部分，则直接滚动到内容界面
                    scrollToContentFromRightMenu();
                }
                recycleVelocityTracker();
                break;
        }
        if (v.isEnabled()) {
            if (mIsSliding) {
                // 正在滑动时让控件得不到焦点
                unFocusBindView();
                return true;
            }
            if (mIsLeftMenuVisible || mIsRightMenuVisible) {
                // 当左侧或右侧布局显示时，将绑定控件的事件屏蔽掉
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * 根据手指移动的距离，判断当前用户的滑动意图，然后给slideState赋值成相应的滑动状态值。
     *
     * @param moveDistanceX 横向移动的距离
     * @param moveDistanceY 纵向移动的距离
     */
    private void checkSlideState(int moveDistanceX, int moveDistanceY) {
        if (mIsLeftMenuVisible) {
            if (mLayoutCount == 3 || LEFT) {
                if (!mIsSliding && Math.abs(moveDistanceX) >= mTouchSlop && moveDistanceX < 0) {
                    mIsSliding = true;
                    mSlideState = HIDE_LEFT_MENU;
                }
            }
        } else if (mIsRightMenuVisible) {
            if (mLayoutCount == 3 || RIGHT) {
                if (!mIsSliding && Math.abs(moveDistanceX) >= mTouchSlop && moveDistanceX > 0) {
                    mIsSliding = true;
                    mSlideState = HIDE_RIGHT_MENU;
                }
            }
        } else {
            if (!mIsSliding && Math.abs(moveDistanceX) >= mTouchSlop && moveDistanceX > 0 && Math
                    .abs(moveDistanceY) < mTouchSlop) {
                if (mLayoutCount == 3 || mLayoutCount == 2 && LEFT) {
                    mIsSliding = true;
                    mSlideState = SHOW_LEFT_MENU;
                    mContentLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                    mContentLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    mContentLayout.setLayoutParams(mContentLayoutParams);
                    // 如果用户想要滑动左侧菜单，将左侧菜单显示，右侧菜单隐藏
                    mLeftMenuLayout.setVisibility(View.VISIBLE);
                    if (mLayoutCount == 3) {
                        mRightMenuLayout.setVisibility(View.GONE);
                    }
                }

            } else if (!mIsSliding && Math.abs(moveDistanceX) >= mTouchSlop && moveDistanceX < 0 
                    && Math.abs(moveDistanceY) < mTouchSlop) {
                if (mLayoutCount == 3 || mLayoutCount == 2 && RIGHT) {
                    mIsSliding = true;
                    mSlideState = SHOW_RIGHT_MENU;
                    mContentLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    mContentLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    mContentLayout.setLayoutParams(mContentLayoutParams);
                    // 如果用户想要滑动右侧菜单，将右侧菜单显示，左侧菜单隐藏
                    mRightMenuLayout.setVisibility(View.VISIBLE);
                    if (mLayoutCount == 3) {
                        mLeftMenuLayout.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    /**
     * 在滑动过程中检查左侧菜单的边界值，防止绑定布局滑出屏幕。
     */
    private void checkLeftMenuBorder() {
        if (mContentLayoutParams.rightMargin > 0) {
            mContentLayoutParams.rightMargin = 0;
        } else if (mContentLayoutParams.rightMargin < -mLeftMenuLayoutParams.width) {
            mContentLayoutParams.rightMargin = -mLeftMenuLayoutParams.width;
        }
    }

    /**
     * 在滑动过程中检查右侧菜单的边界值，防止绑定布局滑出屏幕。
     */
    private void checkRightMenuBorder() {
        if (mContentLayoutParams.leftMargin > 0) {
            mContentLayoutParams.leftMargin = 0;
        } else if (mContentLayoutParams.leftMargin < -mRightMenuLayoutParams.width) {
            mContentLayoutParams.leftMargin = -mRightMenuLayoutParams.width;
        }
    }

    /**
     * 判断是否应该滚动将左侧菜单展示出来。如果手指移动距离大于左侧菜单宽度的1/2，或者手指移动速度大于SNAP_VELOCITY，
     * 就认为应该滚动将左侧菜单展示出来。
     *
     * @return 如果应该将左侧菜单展示出来返回true，否则返回false。
     */
    private boolean shouldScrollToLeftMenu() {
        return mXUp - mXDown > mLeftMenuLayoutParams.width / 2 || getScrollVelocity() > 
                SNAP_VELOCITY;
    }

    /**
     * 判断是否应该滚动将右侧菜单展示出来。如果手指移动距离大于右侧菜单宽度的1/2，或者手指移动速度大于SNAP_VELOCITY，
     * 就认为应该滚动将右侧菜单展示出来。
     *
     * @return 如果应该将右侧菜单展示出来返回true，否则返回false。
     */
    private boolean shouldScrollToRightMenu() {
        return mXDown - mXUp > mRightMenuLayoutParams.width / 2 || getScrollVelocity() > 
                SNAP_VELOCITY;
    }

    /**
     * 判断是否应该从左侧菜单滚动到内容布局，如果手指移动距离大于左侧菜单宽度的1/2，或者手指移动速度大于SNAP_VELOCITY，
     * 就认为应该从左侧菜单滚动到内容布局。
     *
     * @return 如果应该从左侧菜单滚动到内容布局返回true，否则返回false。
     */
    private boolean shouldScrollToContentFromLeftMenu() {
        return mXDown - mXUp > mLeftMenuLayoutParams.width / 2 || getScrollVelocity() > 
                SNAP_VELOCITY;
    }

    /**
     * 判断是否应该从右侧菜单滚动到内容布局，如果手指移动距离大于右侧菜单宽度的1/2，或者手指移动速度大于SNAP_VELOCITY，
     * 就认为应该从右侧菜单滚动到内容布局。
     *
     * @return 如果应该从右侧菜单滚动到内容布局返回true，否则返回false。
     */
    private boolean shouldScrollToContentFromRightMenu() {
        return mXUp - mXDown > mRightMenuLayoutParams.width / 2 || getScrollVelocity() > 
                SNAP_VELOCITY;
    }

    /**
     * 创建VelocityTracker对象，并将触摸事件加入到VelocityTracker当中。
     *
     * @param event 右侧布局监听控件的滑动事件
     */
    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 获取手指在绑定布局上的滑动速度。
     *
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。
     */
    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }

    /**
     * 回收VelocityTracker对象。
     */
    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    /**
     * 使用可以获得焦点的控件在滑动的时候失去焦点。
     */
    private void unFocusBindView() {
        if (mBindView != null) {
            mBindView.setPressed(false);
            mBindView.setFocusable(false);
            mBindView.setFocusableInTouchMode(false);
        }
    }

    class LeftMenuScrollTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... speed) {
            int rightMargin = mContentLayoutParams.rightMargin;
            // 根据传入的速度来滚动界面，当滚动到达边界值时，跳出循环。
            while (true) {
                rightMargin = rightMargin + speed[0];
                if (rightMargin < -mLeftMenuLayoutParams.width) {
                    rightMargin = -mLeftMenuLayoutParams.width;
                    break;
                }
                if (rightMargin > 0) {
                    rightMargin = 0;
                    break;
                }
                publishProgress(rightMargin);
                // 为了要有滚动效果产生，每次循环使线程睡眠一段时间，这样肉眼才能够看到滚动动画。
                sleep(15);
            }
            if (speed[0] > 0) {
                mIsLeftMenuVisible = false;
            } else {
                mIsLeftMenuVisible = true;
            }
            mIsSliding = false;
            return rightMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... rightMargin) {
            mContentLayoutParams.rightMargin = rightMargin[0];
            mContentLayout.setLayoutParams(mContentLayoutParams);
            unFocusBindView();
        }

        @Override
        protected void onPostExecute(Integer rightMargin) {
            mContentLayoutParams.rightMargin = rightMargin;
            mContentLayout.setLayoutParams(mContentLayoutParams);
        }
    }

    class RightMenuScrollTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... speed) {
            int leftMargin = mContentLayoutParams.leftMargin;
            // 根据传入的速度来滚动界面，当滚动到达边界值时，跳出循环。
            while (true) {
                leftMargin = leftMargin + speed[0];
                if (leftMargin < -mRightMenuLayoutParams.width) {
                    leftMargin = -mRightMenuLayoutParams.width;
                    break;
                }
                if (leftMargin > 0) {
                    leftMargin = 0;
                    break;
                }
                publishProgress(leftMargin);
                // 为了要有滚动效果产生，每次循环使线程睡眠一段时间，这样肉眼才能够看到滚动动画。
                sleep(15);
            }
            if (speed[0] > 0) {
                mIsRightMenuVisible = false;
            } else {
                mIsRightMenuVisible = true;
            }
            mIsSliding = false;
            return leftMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... leftMargin) {
            mContentLayoutParams.leftMargin = leftMargin[0];
            mContentLayout.setLayoutParams(mContentLayoutParams);
            unFocusBindView();
        }

        @Override
        protected void onPostExecute(Integer leftMargin) {
            mContentLayoutParams.leftMargin = leftMargin;
            mContentLayout.setLayoutParams(mContentLayoutParams);
        }
    }

    /**
     * 使当前线程睡眠指定的毫秒数。
     *
     * @param millis 指定当前线程睡眠多久，以毫秒为单位
     */
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
