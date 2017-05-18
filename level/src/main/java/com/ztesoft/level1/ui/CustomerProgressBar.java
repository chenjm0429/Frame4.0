package com.ztesoft.level1.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * 文件名称 : CustomerProgressBar
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 自定义进度条
 * <p>
 * 创建时间 : 2017/3/24 15:49
 * <p>
 */
public class CustomerProgressBar extends ProgressBar {

    private int mProgress = 0;

    private int mCurrentProgress = 0;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setValue();
        }

    };

    public CustomerProgressBar(Context context) {
        super(context);
    }

    public CustomerProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomerProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void setValue() {
        this.setMax(100);
        this.setProgress(mCurrentProgress);

        if (mCurrentProgress < mProgress) {
            if (mProgress - mCurrentProgress <= 5) {
                mCurrentProgress = mProgress;
            } else {
                mCurrentProgress = mCurrentProgress + 5;
            }
            mHandler.sendEmptyMessageDelayed(0, 40);
        }
    }

    public void start(int max, int progress) {
        mProgress = progress;

        if (mCurrentProgress < mProgress) {
            mCurrentProgress = mCurrentProgress + 1;
            mHandler.sendEmptyMessage(0);
        }
    }
}
