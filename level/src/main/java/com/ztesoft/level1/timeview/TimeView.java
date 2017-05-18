package com.ztesoft.level1.timeview;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

public class TimeView extends View {

    private Context mContext;
    private String format = "HH:mm:ss";

    // 格式为{"08:23:24","09:06:25"}
    private String[] values;

    private String checkedValue;

    // 空心圆填充色
    private int STROKE_COLOR = Color.WHITE;

    private int LINE_COLOR = Color.parseColor("#666666");

    private int SELECTED_COLOR = Color.parseColor("#BEE09D");

    // 左右两边的亮点到两边的距离
    private int MARGIN_PAD = 30;

    private int radius = 10;

    //文本字体大小
    private int TIME_SIZE = 30;

    private OnCheckedListener onCheckedListener;

    //最后时间点，如果为null，采用24小时, 格式为23:0:0
    private String endTime;

    private String startTime = "0:0:0";

    private int width = 0;

    //是否已经画图形
    private boolean isDrawable = false;

    public void setValues(String[] values) {
        this.values = timeSort(values);

        if (isDrawable)
            invalidate();
    }

    public String getCheckedValue() {
        return checkedValue;
    }

    public void setCheckedValue(String checkedValue) {
        this.checkedValue = checkedValue;
    }

    public OnCheckedListener getOnCheckedListener() {
        return onCheckedListener;
    }

    public void setOnCheckedListener(OnCheckedListener onCheckedListener) {
        this.onCheckedListener = onCheckedListener;
    }

    public int getLINE_COLOR() {
        return LINE_COLOR;
    }

    public void setLINE_COLOR(int lINE_COLOR) {
        LINE_COLOR = lINE_COLOR;
    }

    public int getSELECTED_COLOR() {
        return SELECTED_COLOR;
    }

    public void setSELECTED_COLOR(int sELECTED_COLOR) {
        SELECTED_COLOR = sELECTED_COLOR;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getTIME_SIZE() {
        return TIME_SIZE;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public TimeView(Context context) {
        super(context);
        this.mContext = context;
        initData();
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initData();
    }

    public TimeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initData();
    }

    private void initData() {
        scroller = new Scroller(mContext);

        MARGIN_PAD = (int) (getFontWidth(format, TIME_SIZE) / 2);
    }

    private List<float[]> pointArray;
    private float startX = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        isDrawable = true;
        pointArray = new ArrayList<float[]>();
        if (width == 0)
            width = getWidth();
        if (Math.abs(newDist - oldDist) > 5f) {
            if (width >= getWidth())
                width = (int) (width + 3 * (newDist - oldDist));
            oldDist = newDist;
            if (width < getWidth())
                width = getWidth();
        }
        if (null == checkedValue && null != values && values.length > 0)
            checkedValue = values[values.length - 1];
        Paint paint = new Paint();
        paint.setStrokeWidth(radius * 0.5f);
        paint.setColor(LINE_COLOR);
        paint.setTextSize(TIME_SIZE);

        if (startX + moveLen < 0 && width > getWidth())
            startX += moveLen;
        else
            startX = 0;

        if (width > getWidth() && Math.abs(startX) > (width - getWidth()))
            startX = getWidth() - width;

        canvas.drawLine(startX, radius * 2.5f, width + startX, radius * 2.5f, paint);

        if (null == values || values.length == 0) {
            return;
        }
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        float hoursNum = 24.0f;
        if (null == endTime)
            hoursNum = getHoursBetTime(startTime, values[values.length - 1]);

        float hours = (width - 2 * MARGIN_PAD) * 1.0f / hoursNum;

        for (int i = 0; i < values.length; i++) {
            int secondes = getSeconds("0:0:0", values[i], format);

            float leng = secondes * 1.0f / 3600.0f;

            float xPos = MARGIN_PAD + leng * hours + startX;

            if (xPos > width - MARGIN_PAD)
                xPos = width - MARGIN_PAD;

            if (null != checkedValue && checkedValue.equals(values[i])) {
                paint.setColor(SELECTED_COLOR);
                canvas.drawText(values[i], xPos - getFontWidth(values[i], TIME_SIZE) / 2, radius
                        * 3.0f + getFontHeight(TIME_SIZE), paint);
            } else {
                paint.setColor(LINE_COLOR);
            }
            canvas.drawCircle(xPos, radius * 2.5f, radius, paint);

            paint.setColor(STROKE_COLOR);
            canvas.drawCircle(xPos, radius * 2.5f, radius - 5, paint);

            pointArray.add(new float[]{xPos, radius * 2.5f});
        }
    }

    /**
     * 获得字体高度
     *
     * @param fontSize 字体大小
     * @return
     */
    private int getFontHeight(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.ascent) + 2;
    }

    /**
     * 获得字符串的宽度
     *
     * @param content  字符串内容
     * @param fontSize 字体大小
     * @return
     */
    private float getFontWidth(String content, float fontSize) {
        TextPaint FontPaint = new TextPaint();
        FontPaint.setTextSize(fontSize);
        return FontPaint.measureText(content);
    }

    private int getSeconds(String time1, String time2, String format) {
        DateFormat df = new SimpleDateFormat(format); // "hh:mm:ss"
        try {
            java.util.Date d1 = df.parse(time1);
            java.util.Date d2 = df.parse(time2);
            return (int) Math.abs((d1.getTime() - d2.getTime()) / 1000);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    /**
     * 比较两个时间点的大小, 如果time1>time2。返回1，反之返回-1。两个时间点相等，则返回0
     *
     * @param time1
     * @param time2
     * @param format 时间点类型hh:mm:ss
     * @return
     */
    private int compareTime(String time1, String time2, String format) {
        DateFormat df = new SimpleDateFormat(format); // "hh:mm:ss"
        try {
            java.util.Date d1 = df.parse(time1);
            java.util.Date d2 = df.parse(time2);
            if (d1.getTime() > d2.getTime()) {
                return 1;
            } else if (d1.getTime() < d2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    /**
     * 对数据源 从小到大排序
     *
     * @param args 数据源
     * @return
     */
    public String[] timeSort(String[] args) {// 冒泡排序算法
        if (null == args)
            return null;

        for (int i = 0; i < args.length - 1; i++) {
            for (int j = i + 1; j < args.length; j++) {
                if (compareTime(args[i], args[j], format) == 1) {
                    String temp = args[i];
                    args[i] = args[j];
                    args[j] = temp;
                }
            }
        }
        return args;
    }

    public float getHoursBetTime(String time1, String time2) {
        long nh = 1000 * 60 * 60;// 一小时的毫秒数 

        DateFormat df = new SimpleDateFormat(format); // "hh:mm:ss"
        try {
            java.util.Date d1 = df.parse(time1);
            java.util.Date d2 = df.parse(time2);

            long millions = Math.abs(d1.getTime() - d2.getTime());
            return (millions * 1.0f / nh * 1.0f);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    // 滑动动画
    private Scroller scroller;

    float oldDist = 0;
    float newDist = 0;
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;
    // int isFinish=0;//1：开始 2：移动 0:结束
    float mLastMotionX = 0;
    float mLastMotionY = 0;
    boolean signalTouch = true;
    float moveLen = 0;
    boolean moveFlag = false;
    int eventTime = 0;

    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            // 设置拖拉模式
            case MotionEvent.ACTION_DOWN:
                signalTouch = true;
                mode = DRAG;
                mLastMotionX = x;
                mLastMotionY = y;
                return true;
            case MotionEvent.ACTION_UP:
                if (signalTouch) {
                    for (int i = 0; i < pointArray.size(); i++) {
                        float[] xx = (float[]) pointArray.get(i);
                        if (Math.abs(mLastMotionX - pointArray.get(i)[0]) < 2 * radius && Math
                                .abs(mLastMotionY - pointArray.get(i)[1]) < 2 * radius) {
                            checkedValue = values[i];
                            if (null != onCheckedListener)
                                onCheckedListener.onChecked(checkedValue);
                        }
                    }

                    invalidate();
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                // isFinish=0;
                newDist = 0;
                oldDist = 0;
                break;
            // 设置多点触摸模式
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                // isFinish=1;
                mode = ZOOM;
                break;
            // 若为DRAG模式，则点击移动
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG && Math.abs(mLastMotionX - x) > 5 && Math.abs(mLastMotionY - y) 
                        < 3 * radius) {
                    signalTouch = false;
                    moveFlag = true;
                    moveLen = x - mLastMotionX;
                    scrollingOffset += moveLen;
                    mLastMotionX = x;
                    mLastMotionY = y;
                    super.invalidate();
                }
                // 若为ZOOM模式，则多点触摸缩放
                else if (mode == ZOOM) {
                    newDist = spacing(event);
                    signalTouch = false;
                    invalidate();
                }
                break;
        }

//		if (moveFlag && event.getAction() == MotionEvent.ACTION_UP) {
//			moveFlag = false;
//			justify();
//		}
        return true;
    }

    private final int SCROLLING_DURATION = 800;
    private float scrollingOffset;

//	private void justify() {
//		mLastMotionX = 0;
//		float offset = scrollingOffset;
//		if (offset < 0 && width - getWidth() - Math.abs(startX) < Math.abs(offset)) {
//			offset = getWidth() + Math.abs(startX) - width;
//		} else if (offset > 0 && offset > Math.abs(startX)) {
//			offset = Math.abs(startX);
//		}
//		scroller.startScroll(scroller.getFinalX(), 0, (int)-offset, 0, SCROLLING_DURATION);
//		
//		startX += offset;
//		invalidate();
//
//		scrollingOffset = 0;
//	}
//
//	@Override
//	public void computeScroll() {
//
//		// 先判断mScroller滚动是否完成
//		if (scroller.computeScrollOffset()) {
//
//			// 这里调用View的scrollTo()完成实际的滚动
//			scrollTo(scroller.getCurrX(), scroller.getCurrY());
//
//			// 必须调用该方法，否则不一定能看到滚动效果
//			postInvalidate();
//		}
//		super.computeScroll();
//	}

    // 计算移动距离
    private float spacing(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            float x = event.getX(event.getPointerId(0)) - event.getX(event.getPointerId(1));
            return Math.abs(x);
        }
        return 0;
    }

    public interface OnCheckedListener {
        void onChecked(String checkedValue);
    }
}
