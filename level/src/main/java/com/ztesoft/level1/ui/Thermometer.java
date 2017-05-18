package com.ztesoft.level1.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.ztesoft.level1.R;
import com.ztesoft.level1.util.NumericalUtil;

/**
 * 文件名称 : Thermometer
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 温度计
 * <p>
 * 创建时间 : 2017/5/2 10:27
 * <p>
 */
public class Thermometer extends View {

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

    public Thermometer(Context context) {
        super(context);
    }

    public Thermometer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Thermometer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void drawChart(Canvas canvas) {
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

        paint = new Paint();
        paint.setColor(Color.BLACK);
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

        // 绘制轴
        canvas.drawLine(xPosition, y_head, xPosition, y_head + height, paint);
        drawScale(canvas);
        // 绘制底图
        paint = new Paint();
        paint.setColor(Color.parseColor("#ff8d00"));
        float value_topY = height * (maxValue - value) / (maxValue - minValue);
        // 高度多往下画5px，防止中间有空隙。
        canvas.drawRect(xPosition + intervalWidth, y_head + value_topY, xPosition + picWidth,
                y_head + height + 5,
                paint);
        Resources r = this.getContext().getResources();
        // 叠加图片
        for (int i = 0; i < nn; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(r, R.drawable.thermometer);
            Bitmap bitmapTemp = Bitmap.createScaledBitmap(bitmap, picWidth, y_center, true);// 拉伸
            // 为了顶部刻度对齐，往上提了y_center/2
            canvas.drawBitmap(bitmapTemp, xPosition + intervalWidth, y_head - y_center / 2 + i *
                    y_center, paint);
        }
        Bitmap bitmap_top = BitmapFactory.decodeResource(r, R.drawable.thermometer_top);
        Bitmap topTmp = Bitmap.createScaledBitmap(bitmap_top, picWidth, y_head, true);// 拉伸
        canvas.drawBitmap(topTmp, xPosition + intervalWidth, 0, paint);
        Bitmap bitmap_bottom = BitmapFactory.decodeResource(r, R.drawable.thermometer_bottom);
        Bitmap bottomTmp = Bitmap.createScaledBitmap(bitmap_bottom, picWidth, y_bottom, true);// 拉伸
        canvas.drawBitmap(bottomTmp, xPosition + intervalWidth, y_head + height, paint);
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
     * 完成值，必须在maxValue和minValue后设置。
     *
     * @param value
     */
    public void setValue(float value) {
        this.value = value;
        invalidate();
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

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawChart(canvas);
    }
}
