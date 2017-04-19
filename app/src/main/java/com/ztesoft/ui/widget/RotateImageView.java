package com.ztesoft.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ztesoft.R;

/**
 * 文件名称 : RotateImageView
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 旋转的图片
 * <p>
 * 创建时间 : 2017/3/24 16:03
 * <p>
 */
public class RotateImageView extends ImageView {

    /**
     * 转动频率
     */
    private static final int INCREMENT = 30;

    /**
     * 延迟时间
     */
    private static final int DELAY_TIME = 80;

    private Handler mHandler;

    /**
     * 是否停止
     */
    private boolean mIsStop;

    private int mMsgCount;

    private int mArc;

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public RotateImageView(Context context) {
        super(context);
        init();
    }

    /**
     * 构造方法
     *
     * @param context 上下文
     * @param attrs   属性
     */
    public RotateImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
    }

    /**
     * 停止
     */
    public void stop() {
        mIsStop = true;
        invalidate();
    }

    /**
     * 开始
     */
    public void start() {
        mIsStop = false;
        if (mMsgCount == 0) {
            mHandler.sendEmptyMessage(0);
            mMsgCount++;
        }
    }

    private void init() {
        if (getDrawable() == null) {
            setImageDrawable(getResources().getDrawable(R.drawable.rotate_image_default));
        }
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mMsgCount--;
                if (!mIsStop) {
                    invalidate();
                    mArc = (mArc + INCREMENT) % 360;
                    sendEmptyMessageDelayed(0, DELAY_TIME);
                    mMsgCount++;
                }
                if (mIsStop) {
                    mArc = 0;
                    invalidate();
                }
            }
        };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(mArc, getWidth() / 2, getHeight() / 2);
        super.onDraw(canvas);
    }
}
