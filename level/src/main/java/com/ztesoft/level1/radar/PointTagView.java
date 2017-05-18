package com.ztesoft.level1.radar;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ztesoft.level1.R;

import com.ztesoft.level1.Level1Util;

public class PointTagView extends LinearLayout {

    private Context mContext;
    private TextView descriptionTxt;
    LinearLayout lyt;
    private CircleImageView alarmImg;
    private int txtColor = 0xFF000000;
    private int txtSize = 14;

    private final int WOREST_COLOR = Color.parseColor("#ff625c");
    private final int MIDDLE_COLOR = Color.parseColor("#f9a546");
    private final int MILD_COLOR = Color.parseColor("#ffe400");

    private int orgLeft, orgTop;
    private int lytW, lytH;

    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

            descriptionTxt.measure(w, h);
            Log.v("xiaomi", "size " + descriptionTxt.getMeasuredWidth() + "  " + descriptionTxt
                    .getMeasuredHeight());

            int viewLeft = orgLeft - lytW / 2;
            int viewTop = orgTop - (descriptionTxt.getMeasuredHeight() + lytH / 2);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(Level1Util.dip2px
                    (mContext, 50), LinearLayout.LayoutParams.WRAP_CONTENT);

            lp.setMargins(viewLeft, viewTop, 0, 0);

            PointTagView.this.setLayoutParams(lp);
        }

    };

    public static enum Level {
        WOREST, MIDDLE, MILD
    }

    ;

    public PointTagView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public PointTagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public PointTagView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    private void init() {

        this.setOrientation(LinearLayout.VERTICAL);
        this.setGravity(Gravity.CENTER);
        this.setBackgroundColor(0x00FFFFFF);
        // this.setBackgroundColor(Color.BLUE);

        descriptionTxt = new TextView(mContext);
        descriptionTxt.setTextColor(txtColor);
        descriptionTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, txtSize);
        descriptionTxt.setGravity(Gravity.CENTER);
        this.addView(descriptionTxt, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lyt = new LinearLayout(mContext);
        lyt.setOrientation(LinearLayout.HORIZONTAL);
        lyt.setGravity(Gravity.CENTER);
        lyt.setPadding(5, 10, 10, 5);
        alarmImg = new CircleImageView(mContext);
        lyt.addView(alarmImg, 40, 40);
        this.addView(lyt, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    public void startAnimation() {
        Animation scaleAnimations = AnimationUtils.loadAnimation(mContext, R.anim.point_anim);
        alarmImg.startAnimation(scaleAnimations);
    }

    public void setPointLevel(Level lev) {
        if (lev == Level.WOREST) {
            alarmImg.setColor(WOREST_COLOR);
        } else if (lev == Level.MIDDLE) {
            alarmImg.setColor(MIDDLE_COLOR);
        } else {
            alarmImg.setColor(MILD_COLOR);
        }
    }

    public void setDescription(String txt) {
        descriptionTxt.setText(txt);
        int ems = 3;
        if (txt.length() == 4) {
            ems = 2;
        }
        descriptionTxt.setEms(ems);
    }

    public void resetMargin(int leftPix, int topPix) {
        orgLeft = leftPix;
        orgTop = topPix;
        ViewTreeObserver vto2 = lyt.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                lyt.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                lytW = lyt.getMeasuredWidth();
                lytH = lyt.getMeasuredHeight();
                Log.v("xiaomi", "size " + lyt.getMeasuredWidth() + "  " + lyt.getMeasuredHeight());
                mHandler.sendEmptyMessage(0);
            }
        });
    }
}
