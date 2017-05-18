package com.ztesoft.level1.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ztesoft.level1.R;


/**
 * 文件名称 : ViewPagerUI
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 滑动切换页面控件
 * <p>
 * 创建时间 : 2017/3/24 15:38
 * <p>
 */
public class ViewPagerUI extends LinearLayout {

    private Context context;

    private String[] names;
    private String[] codes;

    private View[] views;

    private OnPagerSrollListener onPagerScrollListener;

    private ViewPager mPager;// 页卡内容

    private ImageView cursor;// 动画图片
    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;// 动画图片宽度

    private LinearLayout topLayout;

    public ViewPagerUI(Context context, String[] names, String[] codes,
                       View[] views, OnPagerSrollListener onPagerScrollListener) {
        super(context);

        this.context = context;

        this.names = names;
        this.codes = codes;

        this.views = views;

        this.onPagerScrollListener = onPagerScrollListener;

        if (names.length != views.length || names.length == 0) {
            return;
        }

        init();
    }

    private void init() {
        
        View.inflate(context, R.layout.widget_view_pager, this);

        initImageView();
        initTextView();
        initViewPager();
    }

    /**
     * 初始化头标
     */
    private void initTextView() {

        LinearLayout.LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1);

        topLayout = (LinearLayout) findViewById(R.id.topLayout);

        for (int i = 0; i < names.length; i++) {
            TextView tv = new TextView(context);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tv.setTextColor(getResources().getColor(R.color.blue));
            tv.setGravity(Gravity.CENTER);
            tv.setText(names[i]);

            topLayout.addView(tv, lp);

//            if (i != names.length - 1) {
//
//                ImageView iv = new ImageView(context);
//                iv.setBackgroundResource(R.drawable.ic_multi_line);
//                topLayout.addView(iv, 2, LayoutParams.MATCH_PARENT);
//            }

            final int index = i;
            tv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    mPager.setCurrentItem(index);

                    changeTextColor(index);

                    if (onPagerScrollListener != null) {
                        onPagerScrollListener.onScroll(codes[index],
                                names[index]);
                    }
                }
            });
        }

        changeTextColor(currIndex);
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPager() {
        mPager = (ViewPager) findViewById(R.id.vPager);
        mPager.setAdapter(new MyPagerAdapter(views));
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    /**
     * 初始化动画
     */
    private void initImageView() {
        cursor = (ImageView) findViewById(R.id.cursor);
        bmpW = BitmapFactory.decodeResource(getResources(),
                R.drawable.view_page_selected_icon).getWidth();// 获取图片宽度
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度

        //设置cursor的宽度
        cursor.setMaxWidth(screenW / names.length);
        MarginLayoutParams margin9 = new MarginLayoutParams(cursor.getLayoutParams());
        LinearLayout.LayoutParams layoutParams9 = new LinearLayout.LayoutParams(margin9);
        layoutParams9.width = screenW / names.length; //设置图片的宽度
        cursor.setLayoutParams(layoutParams9);

        offset = (screenW / names.length - bmpW) / 2;// 计算偏移量
        Matrix matrix = new Matrix();
//        matrix.postTranslate(offset, 0);
        matrix.postTranslate(0, 0);
        cursor.setImageMatrix(matrix);// 设置动画初始位置


    }

    /**
     * 修改文字颜色
     *
     * @param position
     */
    private void changeTextColor(int position) {

//        if (topLayout.getChildCount() == 0) {
//            return;
//        }
//
//        int count = (topLayout.getChildCount() + 1) / 2;
//        for (int i = 0; i < count; i++) {
//            TextView tv = (TextView) topLayout.getChildAt(i * 2);
//
//            if (i == position) {
//                tv.setTextColor(Color.parseColor("#187ac6"));
//            } else {
//                tv.setTextColor(Color.BLACK);
//            }
//        }
    }

    /**
     * 列表跳转对应的选项卡
     *
     * @param target 目标页
     */
    public void forwardCategory(int target) {
        mPager.setCurrentItem(target);
        changeTextColor(target);
        if (onPagerScrollListener != null) {
            onPagerScrollListener.onScroll(codes[target], names[target]);
        }
    }

    /**
     * ViewPager适配器
     */
    public class MyPagerAdapter extends PagerAdapter {
        public View[] views;

        public MyPagerAdapter(View[] views) {
            this.views = views;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(views[arg1]);
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return views.length;
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(views[arg1], 0);
            return views[arg1];
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }

    /**
     * 页卡切换监听
     */
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量

        @Override
        public void onPageSelected(int position) {
            Animation animation = null;

            int first = 0;
            int last = 0;

            if (currIndex > position) {
                first = one * position;
                last = one * (position + 1);
                animation = new TranslateAnimation(last, first, 0, 0);

            } else if (currIndex < position) {
                first = one * (position - 1);
                last = one * position;
                animation = new TranslateAnimation(first, last, 0, 0);
            }

            currIndex = position;
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(200);
            cursor.startAnimation(animation);

            changeTextColor(currIndex);

            if (onPagerScrollListener != null) {
                onPagerScrollListener
                        .onScroll(codes[position], names[position]);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    public interface OnPagerSrollListener {

        void onScroll(String code, String name);
    }
}


