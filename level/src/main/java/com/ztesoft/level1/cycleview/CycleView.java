package com.ztesoft.level1.cycleview;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ztesoft.level1.Level1Util;
import com.ztesoft.level1.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件名称 : CycleView
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 自动轮播控件
 * <p>
 * 创建时间 : 2017/3/24 14:43
 * <p>
 */
public class CycleView extends LinearLayout {

    /**
     * 上下文
     */
    private Context mContext;

    /**
     * 图片轮播视图
     */
    private CustomViewPager mAdvPager = null;

    /**
     * 滚动图片视图适配器
     */
    private CycleAdapter mAdvAdapter;

    /**
     * 图片轮播指示器控件
     */
    private LinearLayout mGroup;

    //轮播指示器的位置，默认居中，left,center,right
    private String position = "center";

    /**
     * 图片轮播指示器-个图
     */
    private ImageView mImageView = null;

    /**
     * 滚动图片指示器-视图列表
     */
    private ImageView[] mImageViews = null;

    /**
     * 图片滚动当前图片下标
     */
    private int mImageIndex = 0;

    /**
     * 是否循环
     */
    private boolean isCycle = false;

    private int dot = R.drawable.dot_1;
    private int dot1 = R.drawable.dot_2;

    public CycleView(Context context) {
        this(context, null);
    }

    /**
     * @param context
     * @param dot     选中状态指示器
     * @param dot1    未选中状态指示器
     */
    public CycleView(Context context, int dot, int dot1) {
        this(context, null);

        this.dot = dot;
        this.dot1 = dot1;
    }

    public CycleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public CycleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.widget_cycle_view, this);
        mAdvPager = (CustomViewPager) findViewById(R.id.adv_pager);
        mAdvPager.setOnPageChangeListener(new GuidePageChangeListener());
        // mAdvPager.setOnTouchListener(new OnTouchListener() {
        //
        // @Override
        // public boolean onTouch(View v, MotionEvent event) {
        // switch (event.getAction()) {
        // case MotionEvent.ACTION_UP:
        // // 开始图片滚动
        // startImageTimerTask();
        // break;
        // default:
        // // 停止图片滚动
        // stopImageTimerTask();
        // break;
        // }
        // return false;
        // }
        // });
        // 滚动图片右下指示器视图
        mGroup = (LinearLayout) findViewById(R.id.viewGroup);
        mGroup.setGravity(Gravity.CENTER);
    }

    /**
     * 装填图片数据
     *
     * @param views
     * @param imageCycleViewListener
     */
    public void setImageResources(List<View> views, CycleViewListener imageCycleViewListener) {
        // 清除所有子视图
        mGroup.removeAllViews();
        // 图片广告数量
        final int viewCount = views.size();
        mImageViews = new ImageView[viewCount];
        for (int i = 0; i < viewCount; i++) {
            mImageView = new ImageView(mContext);
//			mImageView.setPadding(5, 5, 5, 5);
            mImageViews[i] = mImageView;
            if (i == 0) {
                mImageViews[i].setBackgroundResource(dot);
            } else {
                mImageViews[i].setBackgroundResource(dot1);
            }
            mGroup.addView(mImageViews[i]);
            mGroup.addView(new View(mContext), Level1Util.dip2px(mContext, 10), 0);
        }

        if (null == views || views.size() < 2)
            mGroup.setVisibility(View.INVISIBLE);
        else
            mGroup.setVisibility(View.VISIBLE);

        mAdvAdapter = new CycleAdapter(views, imageCycleViewListener);
        mAdvPager.setAdapter(mAdvAdapter);

    }

    /**
     * 开始轮播(手动控制自动轮播与否，便于资源控制)
     */
    public void startImageCycle() {
        isCycle = true;
        startImageTimerTask();
    }

    /**
     * 暂停轮播——用于节省资源
     */
    public void pushImageCycle() {
        isCycle = false;
        stopImageTimerTask();
    }

    /**
     * 开始图片滚动任务
     */
    private void startImageTimerTask() {
        stopImageTimerTask();
        // 图片每3秒滚动一次
        mHandler.postDelayed(mImageTimerTask, 3000);
    }

    /**
     * 停止图片滚动任务
     */
    private void stopImageTimerTask() {
        mHandler.removeCallbacks(mImageTimerTask);
    }

    private Handler mHandler = new Handler();

    /**
     * 图片自动轮播Task
     */
    private Runnable mImageTimerTask = new Runnable() {

        @Override
        public void run() {
            if (mImageViews != null) {
                // 下标等于图片列表长度说明已滚动到最后一张图片,重置下标
                if ((++mImageIndex) == mImageViews.length) {
                    mImageIndex = 0;
                }
                mAdvPager.setCurrentItem(mImageIndex);
            }
        }
    };

    /**
     * 轮播图片状态监听器
     *
     * @author minking
     */
    private final class GuidePageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int state) {
            if (isCycle && state == ViewPager.SCROLL_STATE_IDLE)
                startImageTimerTask(); // 开始下次计时
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int index) {
            // 设置当前显示的图片下标
            mImageIndex = index;
            // 设置图片滚动指示器背景
            mImageViews[index].setBackgroundResource(dot);
            for (int i = 0; i < mImageViews.length; i++) {
                if (index != i) {
                    mImageViews[i].setBackgroundResource(dot1);
                }
            }
        }
    }

    private class CycleAdapter extends PagerAdapter {

        /**
         * 图片视图缓存列表
         */
        private ArrayList<ImageView> mViewCacheList;

        /**
         * 图片资源列表
         */
        private List<View> mAdList = new ArrayList<View>();

        /**
         * 广告图片点击监听器
         */
        private CycleViewListener mCycleViewListener;

        public CycleAdapter(List<View> adList, CycleViewListener imageCycleViewListener) {
            mAdList = adList;
            mCycleViewListener = imageCycleViewListener;
            mViewCacheList = new ArrayList<ImageView>();
        }

        @Override
        public int getCount() {
            return mAdList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = mAdList.get(position);

            if (null != mCycleViewListener) {
                // 设置图片点击监听
                view.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        mCycleViewListener.onViewClick(position, v);
                    }
                });
            }
            container.addView(view);

            if (null != mCycleViewListener) {
                mCycleViewListener.displayView(position, view);
            }

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView view = (ImageView) object;
            container.removeView(view);
            mViewCacheList.add(view);
        }

    }

    /**
     * 轮播控件的监听事件
     *
     * @author minking
     */
    public interface CycleViewListener {

        /**
         * 加载图片资源
         *
         * @param position
         * @param view
         */
        void displayView(int position, View view);

        /**
         * 单击图片事件
         *
         * @param position
         * @param view
         */
        void onViewClick(int position, View view);
    }

    public void setPosition(String position) {
        this.position = position;

        if (position.equals("left")) {
            mGroup.setGravity(Gravity.LEFT);
        } else if (position.equals("center")) {
            mGroup.setGravity(Gravity.CENTER);
        } else if (position.equals("right")) {
            mGroup.setGravity(Gravity.RIGHT);
        }
    }
}
