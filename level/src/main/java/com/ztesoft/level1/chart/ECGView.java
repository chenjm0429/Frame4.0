package com.ztesoft.level1.chart;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.View;

import com.ztesoft.level1.R;

/**
 * 文件名称 : ECGView
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 心电图
 * <p>
 * 创建时间 : 2017/4/1 9:47
 * <p>
 */
public class ECGView extends View {

    private int bgRes = R.drawable.ecg_bg;
    private List<Integer> allValue;
    private int showNumber = 500;// 一屏显示个数
    private int rateMillis = 6;// 动画步进毫秒间隔
    private int maxValue = 416, minValue = -416;
    private int lineColor = Color.BLACK;
    private int rateNum = 1;// 一次步进的个数

    private int width;
    private int height;
    private float realViewLength;
    private boolean dataLoadFlag = true;// 已有数据是否已播放完毕，仅实时绘制有效
    private boolean realTimeFlag = false;// 是否实时绘制
    private float unitSpacing;// 两点之间距离
    private Paint paint;

    public static final int PLAYING = 0, PLAYPAUSE = 1, NOTPLAYING = 2, PLAYEND = 9;
    private int playingFlag = NOTPLAYING;// 播放状态，默认未播放
    private float playedX = 0;// 已播放位置

    public ECGView(Context context) {
        super(context);
        allValue = new ArrayList<Integer>();
    }

    public void create() {
        paint = new Paint();
        paint.setColor(lineColor);
        paint.setAntiAlias(true);

        if (rateMillis < 10) {// 步进周期太小时，一次步进10个点
            rateMillis = rateMillis * 15;
            rateNum = 15;
        }
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), bgRes);
        BitmapDrawable drawable = new BitmapDrawable(bitmap);
        drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
        drawable.setDither(true);
        this.setBackgroundDrawable(drawable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = this.getWidth();
        height = this.getHeight();
        unitSpacing = (float) width / (showNumber - 1);
        realViewLength = unitSpacing * (allValue.size() - 1);// 实际图形总宽度
        drawLine(canvas);
    }

    /**
     * 绘制线
     *
     * @param canvas
     */
    private void drawLine(Canvas canvas) {
        float oldX = 0, oldY = 0;
        for (int i = 0; i < allValue.size(); i++) {
            float x, y;
            float ff = (float) (maxValue - allValue.get(i)) / (maxValue - minValue);
            y = ff * height;
            if (!realTimeFlag && playingFlag == PLAYEND) {//非实时，且播放结束，则停留在最后一屏
                x = realViewLength - i * unitSpacing;
            } else {
                x = playedX - i * unitSpacing;
            }
            // canvas.drawCircle(x, y, 2, paint);
            if (i > 0) {// 超过一个点才画线
                canvas.drawLine(oldX, oldY, x, y, paint);
            }
            oldX = x;
            oldY = y;
        }
        if (realTimeFlag) {// 如果是实时，则回到播放进度
            if (playingFlag != PLAYING && !dataLoadFlag) {// 如果有数据，但播放停止，则继续播放
                handler.postDelayed(runnable, 0);
            }
        } else if (playingFlag == PLAYEND || playingFlag == NOTPLAYING) {// 如果非实时，且播放结束/未播放，则将播放进度清零
            playedX = 0;
            if (allValue.size() > 0) {
                ((ECGLayout) getParent()).setStartIcon();
            } else {
                ((ECGLayout) getParent()).setNullIcon();
            }
        }
    }

    /**
     * 设置播放状态
     *
     * @return 返回操作后的播放状态
     */
    public int setPlayingFlag() {
        if (playingFlag == PLAYING) {
            playingFlag = PLAYPAUSE;
            handler.removeCallbacks(runnable);
        } else {
            playingFlag = PLAYING;
            handler.postDelayed(runnable, 0);
        }
        return playingFlag;
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int rateSpacing = (int) (unitSpacing * rateNum);
            float surplusWidth = realViewLength - playedX;// 剩余宽度
            if (surplusWidth <= 0) {// 播放结束
                handler.removeCallbacks(runnable);
                playingFlag = PLAYEND;
                dataLoadFlag = true;
                if (!realTimeFlag) {
                    playedX = 0;// 非实时绘制，播放结束后要从头播放，播放位置重置为0
                    ((ECGLayout) getParent()).setStartIcon();
                } else {
                    playedX = realViewLength;
                }
            } else {
                playingFlag = PLAYING;
                playedX += rateSpacing;
                invalidate();
                handler.postDelayed(this, rateMillis); // 每隔rateMillis毫秒执行
            }
        }
    };

    /**
     * 一次传入全量数据，仅非实时状态可用
     *
     * @param allValue
     */
    public void setAllValue(List<Integer> allValue) {
        if (allValue == null || allValue.size() == 0) {
            allValue = new ArrayList<Integer>();
        }
        if (!realTimeFlag) {
            this.allValue = allValue;

            playingFlag = NOTPLAYING;
            handler.removeCallbacks(runnable);
            invalidate();
        }
    }

    public void setValue(int value) {
        if (realTimeFlag) {// 必须是实时数据
            this.allValue.add(value);
            dataLoadFlag = false;
            if (playingFlag != PLAYING) {// 如果正在播放，则不需要触发重绘
                invalidate();
            }
        }
    }

    /**
     * 一次传入部分数据，仅实时状态可用
     *
     * @param values
     */
    public void setValue(List<Integer> values) {
        if (realTimeFlag) {// 必须是实时数据
            for (int i = 0; i < values.size(); i++) {
                this.allValue.add(values.get(i));
            }
            dataLoadFlag = false;
            if (playingFlag != PLAYING) {// 如果正在播放，则不需要触发重绘
//				Log.d(Tag, "setValue =");
                invalidate();
            }
        }
    }

    /**
     * 设置最大值，默认416
     *
     * @param maxValue
     */
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * 设置最小值，默认-416
     *
     * @param minValue
     */
    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    /**
     * 一次设置，maxValue = value minValue = -value
     *
     * @param value
     */
    public void setThresholdValue(int value) {
        this.maxValue = value;
        this.minValue = -this.maxValue;
    }

    /**
     * 实时数据传输结束
     */
    public void setStop() {
        this.realTimeFlag = false;
        invalidate();
    }

    /**
     * 获取心电图bitmap
     *
     * @return
     */
    public Bitmap getBitmap() {
        int width = (int) realViewLength;
        if (width <= 0) {
            width = this.width;
        }
        Bitmap tableView = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(tableView);
        // 绘制背景
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), bgRes);
        int widthCount = ((int) realViewLength + bitmap.getWidth() - 1) / bitmap.getWidth();
        int heightCount = (height + bitmap.getHeight() - 1) / bitmap.getHeight();
        for (int idy = 0; idy < heightCount; ++idy) {
            for (int idx = 0; idx < widthCount; ++idx) {
                canvas.drawBitmap(bitmap, idx * bitmap.getWidth(), idy * bitmap.getHeight(), null);
            }
        }
        // 绘制线
        float oldX = 0, oldY = 0;
        for (int i = 0; i < allValue.size(); i++) {
            float x, y;
            float ff = (float) (maxValue - allValue.get(i)) / (maxValue - minValue);
            y = ff * height;
            x = realViewLength - i * unitSpacing;
            if (i > 0) {// 超过一个点才画线
                canvas.drawLine(oldX, oldY, x, y, paint);
            }
            oldX = x;
            oldY = y;
        }
        return tableView;
    }

    /**
     * 设置一屏显示的个数，必须大于1
     *
     * @param showNumber
     */
    public void setScreenShowNumber(int showNumber) {
        if (showNumber > 1) {
            this.showNumber = showNumber;
        }
    }

    /**
     * 设置步进间隔（毫秒）
     *
     * @param rateMillis
     */
    public void setRateMillis(int rateMillis) {
        this.rateMillis = rateMillis;
    }

    /**
     * 设置是否实时模式，默认非实时
     *
     * @param realTimeFlag
     */
    public void setRealTimeFlag(boolean realTimeFlag) {
        this.realTimeFlag = realTimeFlag;
    }

    public boolean isRealTimeFlag() {
        return realTimeFlag;
    }

    public int isPlayingFlag() {
        return playingFlag;
    }

    /**
     * 设置线的颜色
     *
     * @param lineColor
     */
    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    /**
     * 设置背景图片编号
     *
     * @param bgRes
     */
    public void setBgRes(int bgRes) {
        this.bgRes = bgRes;
    }

    public int getValueSize() {
        return allValue.size();
    }
}
