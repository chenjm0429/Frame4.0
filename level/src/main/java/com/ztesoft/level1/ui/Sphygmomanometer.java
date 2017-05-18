package com.ztesoft.level1.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.ztesoft.level1.R;
import com.ztesoft.level1.util.NumericalUtil;

/**
 * 文件名称 : Sphygmomanometer
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 血压计
 * <p>
 * 创建时间 : 2017/5/2 10:27
 * <p>
 */
@SuppressLint("WrongCall")
public class Sphygmomanometer extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder holder;
    private Canvas mCanvas;

    private int xPosition = 10;
    private int y_head = 68;// top图标高度
    private int y_bottom = 90;// bottom图标高度
    private int y_center = 16;// 中间图标高度
    private int picWidth = 75;// 图片宽度
    private int xScaleNum1Width = 10;// 刻度线1的宽度
    private int xScaleNum2Width = 5;// 刻度线2的宽度
    private int intervalWidth = 5;// 轴和图片的间隔
    private int width;// 总宽度
    private int height;// 可用刻度高度
    private Paint paint;
    private String[] showTexts;// 刻度值数组
    private int textSize = 16;

    private float maxValue = 100, minValue = 0, value = minValue;
    private int scaleNum1 = 1, scaleNum2 = 1;

    private float oldV = value;
    private float step = 10;// 每次刷新改变像素
    private int time = 50;// 刷新速度（ms）

    public Sphygmomanometer(Context context) {
        this(context, null);
    }

    public Sphygmomanometer(Context context, AttributeSet attrs) {
        super(context, attrs);
        count = 0;
        holder = this.getHolder();
        holder.addCallback(this);
        paint = new Paint();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        new Thread(this).start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        showTexts = new String[scaleNum1 + 1];
        float bb = (maxValue - minValue) / scaleNum1;
        for (int i = 0; i <= scaleNum1; i++) {
            String showNum = NumericalUtil.getInstance().setDecimalPlace((minValue + i * bb) +
                    "", 2);
            if (showNum.endsWith(".00")) {
                showNum = showNum.substring(0, showNum.indexOf(".00"));
            }
            showTexts[scaleNum1 - i] = showNum;
        }
        // 根据坐标显示文本的长度，计算偏移量
        String maxLongText = "";
        for (int i = 0; i < showTexts.length; i++) {
            if (showTexts[i].length() > maxLongText.length()) {
                maxLongText = showTexts[i];
            }
        }
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);
        xPosition = (int) (paint.measureText(maxLongText) + xScaleNum1Width);// 获得轴偏移量
        xPosition = xPosition + 2;

        width = this.getWidth();
        if (width < xPosition + intervalWidth + picWidth) {// 如果宽度过小，则缩放图片
            int oldWidth = picWidth;
            picWidth = width - xPosition - intervalWidth;
            y_head = picWidth * y_head / oldWidth;
            y_bottom = picWidth * y_bottom / oldWidth;
            y_center = picWidth * y_center / oldWidth;
        }

        height = this.getHeight() - y_head - y_bottom;
        int nn = height / y_center;
        height = nn * y_center + 1;// 重新定义height,确保图片上下刻度能显示完成
        nn++;

        step = step / height * maxValue;

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void drawBgImage() {
        mCanvas = holder.lockCanvas();
        // 绘制轴
        paint.setColor(Color.WHITE);
        mCanvas.drawLine(xPosition, y_head, xPosition, y_head + height, paint);
        drawScale(mCanvas);

        Resources r = this.getContext().getResources();
        Bitmap bitmap_top = BitmapFactory.decodeResource(r, R.drawable.thermometer_top);
        Bitmap topTmp = Bitmap.createScaledBitmap(bitmap_top, picWidth, y_head, true);// 拉伸
        mCanvas.drawBitmap(topTmp, xPosition + intervalWidth, 0, null);
        Bitmap bitmap_bottom = BitmapFactory.decodeResource(r, R.drawable.thermometer_bottom);
        Bitmap bottomTmp = Bitmap.createScaledBitmap(bitmap_bottom, picWidth, y_bottom, true);// 拉伸
        mCanvas.drawBitmap(bottomTmp, xPosition + intervalWidth, y_head + height, null);
        holder.unlockCanvasAndPost(mCanvas);
    }

    /**
     * 绘制刻度
     */
    private void drawScale(Canvas canvas) {
        float textHeight = paint.getFontMetrics().bottom - paint.getFontMetrics().top;
        for (int i = 0; i <= scaleNum1; i++) {
            int tempy = y_head + i * (height / scaleNum1);// 临时y坐标
            if (i == scaleNum1) {
                tempy = y_head + height;// 确保最后一个对齐
            }
            float startX = xPosition - xScaleNum1Width - paint.measureText(showTexts[i]) - 2;
            canvas.drawText(showTexts[i], startX, tempy + textHeight / 4, paint);
            canvas.drawLine(xPosition, tempy, xPosition - xScaleNum1Width, tempy, paint);
            if (scaleNum2 > 1 && i != scaleNum1) {
                for (int j = 1; j < scaleNum2; j++) {
                    canvas.drawLine(xPosition, tempy + j * (height / scaleNum1 / scaleNum2),
                            xPosition
                                    - xScaleNum2Width, tempy + j * (height / scaleNum1 /
                                    scaleNum2), paint);
                }
            }
        }
    }

    int count = 0;

    @Override
    public void run() {
        if (count < 2) {// 双缓冲导致需要必须2个画布都有内容
            drawBgImage();
        }
        count++;

        Resources r = this.getContext().getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(r, R.drawable.thermometer);
        Bitmap bitmapTemp = Bitmap.createScaledBitmap(bitmap, picWidth, y_center, true);
        int nn = height / y_center;
        nn++;

        while (true) {
            try {
                float value_topY = height * (maxValue - oldV) / (maxValue - minValue);
//				 Log.d("----------",
//				 "oldV="+oldV+"---value="+value+"---value_topY="+value_topY);
                mCanvas = holder.lockCanvas(new Rect(xPosition + intervalWidth, y_head, xPosition
                        + intervalWidth
                        + picWidth, y_head + height + 5));
                paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
                mCanvas.drawPaint(paint);
                paint.setXfermode(new PorterDuffXfermode(Mode.SRC));

                paint.setColor(Color.parseColor("#ff8d00"));
                mCanvas.drawRect(xPosition + intervalWidth, y_head + value_topY, xPosition +
                                intervalWidth + picWidth,
                        y_head + height + 5, paint);

                for (int i = 0; i < nn; i++) {
                    mCanvas.drawBitmap(bitmapTemp, xPosition + intervalWidth, y_head - y_center /
                                    2 + i * y_center,
                            null);
                }
                if (oldV > value) {
                    oldV -= step;
                    if (oldV < value)// 不足一个step时，直接到实际值
                        oldV = value;
                } else if (oldV < value) {
                    oldV += step;
                    if (oldV > value)// 不足一个step时，直接到实际值
                        oldV = value;
                } else {// 达到实际值，动画完成
                    break;
                }
            } finally {
                holder.unlockCanvasAndPost(mCanvas);
            }
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setValue(float v) {
        this.value = v;
        new Thread(this).start();
    }

    /**
     * 设置最大值，默认100
     *
     * @param maxValue
     */
    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * 设置最小值，默认0
     *
     * @param minValue
     */
    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    /**
     * 设置大刻度，必须大于1
     *
     * @param scaleNum1
     */
    public void setScaleNum(int scaleNum1) {
        if (scaleNum1 > 1)
            this.scaleNum1 = scaleNum1;
    }

    /**
     * 设置大小刻度，必须大于1
     *
     * @param scaleNum1
     * @param scaleNum2
     */
    public void setScaleNum(int scaleNum1, int scaleNum2) {
        if (scaleNum1 > 1)
            this.scaleNum1 = scaleNum1;
        if (scaleNum2 > 1)
            this.scaleNum2 = scaleNum2;
    }

    /**
     * 每次步进的像素个数，默认10
     *
     * @param step
     */
    public void setStep(float step) {
        this.step = step;
    }

    /**
     * time毫秒步进一次，默认50毫秒
     *
     * @param time
     */
    public void setTime(int time) {
        this.time = time;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

}
