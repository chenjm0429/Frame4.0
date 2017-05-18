package com.ztesoft.level1.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.ztesoft.level1.R;
import com.ztesoft.level1.util.BitmapOperateUtil;

/**
 * 文件名称 : MagicView
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 翻牌数字
 * <p>
 * 创建时间 : 2017/5/23 9:24
 * <p>
 */
public class MagicView extends LinearLayout {
    private final String BACE_FORMAT = "0123456789-.,%";
    private int bgImage = R.drawable.split_1;
    private int textColor = Color.BLACK;
    private int rateMillis = 2000;
    private float textSize = 16;

    private int textHeight;

    public MagicView(Context context) {
        super(context);
        this.setOrientation(LinearLayout.HORIZONTAL);
        Resources r = this.getContext().getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(r, bgImage);
        textHeight = bitmap.getHeight();
    }

    /**
     * 拼装横向layout，排除非法字段
     *
     * @param value
     */
    public void setValue(String value) {
        if (TextUtils.isEmpty(value)) {
            this.removeAllViews();
            return;
        }

        TextView tv = new TextView(getContext());
        tv.setTextSize(textSize);
        tv.setText("0");
        Bitmap textMap = BitmapOperateUtil.getBitmapFromSimpleView(tv);
        // Paint paint = new Paint();
        // paint.setTextSize(textSize);
        // int paintHeight = (int)
        // (paint.getFontMetrics().bottom-paint.getFontMetrics().top);
        int paintHeight = textMap.getHeight();
        if (paintHeight > textHeight) {
            textHeight = paintHeight;
        }

        for (int i = 0; i < value.length(); i++) {
            String v = value.substring(i, i + 1);
            if (BACE_FORMAT.indexOf(v) != -1) {
                LinearLayout vvv = new LinearLayout(getContext());
                vvv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
                vvv.setBackgroundResource(bgImage);
                vvv.setTag(v);
                mView mV = new mView(getContext());
                vvv.addView(mV, LayoutParams.FILL_PARENT, BACE_FORMAT.length() * textHeight);
                this.addView(vvv, LayoutParams.WRAP_CONTENT, textHeight);
            } else {
                continue;
            }
        }

        mHandler.sendEmptyMessage(1);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            for (int i = 0; i < MagicView.this.getChildCount(); i++) {
                LinearLayout vvv = (LinearLayout) MagicView.this.getChildAt(i);
                mView mV = (mView) vvv.getChildAt(0);
                // 计算每个需要滚动的距离
                String v = (String) vvv.getTag();
                int height = BACE_FORMAT.indexOf(v) * textHeight;
                mV.smoothScrollBy(0, height);
            }
        }
    };

    /**
     * 全量内容的竖行layout，用于滚动
     */
    class mView extends LinearLayout {
        private Scroller mScroller;

        public mView(Context context) {
            super(context);
            mScroller = new Scroller(context);
            this.setOrientation(LinearLayout.VERTICAL);
            this.setGravity(Gravity.CENTER);

            for (int i = 0; i < BACE_FORMAT.length(); i++) {
                TextView v = new TextView(getContext());
                v.setTextColor(textColor);
                v.setTextSize(textSize);
                v.setText(BACE_FORMAT.substring(i, i + 1));
                addView(v, LayoutParams.WRAP_CONTENT, textHeight);
            }
        }

        /**
         * 调用此方法滚动到目标位置
         */
        private void smoothScrollTo(int fx, int fy) {
            int dx = fx - mScroller.getFinalX();
            int dy = fy - mScroller.getFinalY();
            smoothScrollBy(dx, dy);
        }

        /**
         * 调用此方法设置滚动的相对偏移
         */
        private void smoothScrollBy(int dx, int dy) {

            // 设置mScroller的滚动偏移量
            mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, rateMillis);
            invalidate();// 这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
        }

        @Override
        public void computeScroll() {

            // 先判断mScroller滚动动画是否完成
            if (mScroller.computeScrollOffset()) {

                // 这里调用View的scrollTo()完成实际的滚动
                scrollTo(mScroller.getCurrX(), mScroller.getCurrY());

                // 必须调用该方法，否则不一定能看到滚动效果
                postInvalidate();
            }
            super.computeScroll();
        }
    }

    /**
     * 翻牌背景图，推荐使用.9图片
     *
     * @param bgImage
     */
    public void setBgImage(int bgImage) {
        this.bgImage = bgImage;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    /**
     * 动画时长（毫秒），默认2000
     *
     * @param rateMillis
     */
    public void setRateMillis(int rateMillis) {
        this.rateMillis = rateMillis;
    }
}
