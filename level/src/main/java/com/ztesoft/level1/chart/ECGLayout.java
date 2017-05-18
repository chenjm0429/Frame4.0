package com.ztesoft.level1.chart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.ztesoft.level1.R;

/**
 * 文件名称 : ECGLayout
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 心电图附属功能布局
 * <p>
 * 创建时间 : 2017/4/1 9:48
 * <p>
 */
public class ECGLayout extends FrameLayout {
    private int startIcon = R.drawable.video_play;
    private int pauseIcon = R.drawable.video_pause;
    private BitmapDrawable startDraw, pauseDraw;

    public ECGLayout(Context context) {
        super(context);
    }

    public ECGLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ECGLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void create(final com.ztesoft.level1.chart.ECGView th) {
        Bitmap startTemp = BitmapFactory.decodeResource(getResources(), startIcon);
        startDraw = new BitmapDrawable(startTemp);
        startDraw.setGravity(Gravity.CENTER);

        Bitmap pauseTemp = BitmapFactory.decodeResource(getResources(), pauseIcon);
        pauseDraw = new BitmapDrawable(pauseTemp);
        pauseDraw.setGravity(Gravity.CENTER);
        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!th.isRealTimeFlag()) {
                    if (th.getValueSize() > 0) {
                        if (th.setPlayingFlag() == com.ztesoft.level1.chart.ECGView.PLAYING) {
                            setPauseIcon();
                        } else {
                            setStartIcon();
                        }
                    }
                }
            }
        });
        this.addView(th);
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ECGLayout.this.setForeground(null);
        }
    };

    public void setStartIcon() {
        ECGLayout.this.setForeground(startDraw);
        handler.removeCallbacks(runnable);
    }

    public void setPauseIcon() {
        ECGLayout.this.setForeground(pauseDraw);
        handler.postDelayed(runnable, 2000);
    }

    public void setNullIcon() {
        ECGLayout.this.setForeground(null);
    }

    public void setStartIcon(int startIcon) {
        this.startIcon = startIcon;
    }

    public void setPauseIcon(int pauseIcon) {
        this.pauseIcon = pauseIcon;
    }
}
