package com.ztesoft.level1.chart;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.ztesoft.level1.R;
import com.ztesoft.level1.util.NumericalUtil;

/**
 * 文件名称 : CompleteSector
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 仪表盘图形
 * <p>
 * 创建时间 : 2017/4/1 9:46
 * <p>
 */
public class CompleteSector extends View {

    private float maxValue = 100, minValue = 0, value = minValue;
    private int color = Color.WHITE;
    private int bgColor1 = Color.parseColor("#898989"), fgColor1 = Color.parseColor("#5db030");
    private int bgColor2 = Color.parseColor("#b9b9b9"), fgColor2 = Color.parseColor("#9cda50");
    private int width;
    private int height;
    private float angle = 130;
    private float textSize = 50;
    private int textColor = Color.WHITE;
    private int decimalNum = -1; //小数点位数，-1表示没有强制设定

    //仪表盘上显示的数值，与value有区别，value的值与指针指示的值同步
    private float showValue = value;

    public CompleteSector(Context context) {
        super(context);
    }

    public CompleteSector(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompleteSector(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void drawChart(Canvas canvas) {
        width = this.getWidth();
        height = this.getHeight();
        if (width > height * 2) {// 按小值 ，取正方形
            width = height * 2;
        } else {
            height = width / 2;
        }
        Paint paint_bg = new Paint();
        paint_bg.setAntiAlias(true);
        paint_bg.setColor(bgColor2);
        Shader mShader = new LinearGradient(width / 2, height, width / 2, 0, bgColor1, bgColor2,
                Shader.TileMode.REPEAT);
        paint_bg.setShader(mShader);
        Paint paint_fg = new Paint();
        paint_fg.setAntiAlias(true);
        paint_fg.setColor(fgColor2);
        Shader mShader2 = new LinearGradient(width / 2, height, width / 2, 0, fgColor1, fgColor2,
                Shader.TileMode.REPEAT);
        paint_fg.setShader(mShader2);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);

        RectF rf = new RectF(0, 2, width, height * 2 + 2);// 往下偏移2px，保证顶端圆弧效果
        canvas.drawArc(rf, -(90 + angle / 2), angle, true, paint_bg);
        float bb = value / (maxValue - minValue);
        if (bb > 0) {// 大于0时才绘制完成部分
            // 总弧度为angle
            canvas.drawArc(rf, -(90 + angle / 2), bb * angle, true, paint_fg);
        }

        RectF rf2 = new RectF(width / 4, height / 2 + 2, width / 4 * 3, height / 2 * 3 + 2);
        canvas.drawArc(rf2, -(float) (90 + 0.5 + angle / 2), angle + 1, true, paint);// 
        // 内环增加1度，防止外环线露出来

        Resources res = this.getContext().getResources();
        Bitmap pointBarBM = BitmapFactory.decodeResource(res, R.drawable.point_bar);
        Bitmap mBitmap = Bitmap.createScaledBitmap(pointBarBM, pointBarBM.getWidth(), height,
                true);// 拉伸
        // 总弧度为angle
        float cc = bb * angle - angle / 2;// 获取旋转角度
        Matrix matrix = new Matrix();
        matrix.preTranslate(width / 2 - 4, 0);
        matrix.preRotate(cc, 4, height);
        canvas.drawBitmap(mBitmap, matrix, paint);

        TextView textView = new TextView(getContext());
        textView.setTextColor(textColor);
        textView.setTextSize(textSize);
        int num;
        if (-1 == decimalNum) {
            // 调整显示的小数位数，确保跟传入值一致
            num = ("" + value).length() - ("" + value).indexOf(".") - 1;
        } else {
            num = decimalNum;
        }
        textView.setText(NumericalUtil.getInstance().setDecimalPlace("" + showValue, num) + "%");
        textView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
        textView.buildDrawingCache();
        Bitmap map = textView.getDrawingCache();

        Matrix ttt = new Matrix();
        float xx = (float) width / 2 / map.getWidth();
        float yy = (float) height / 4 / map.getHeight();
        if (xx > yy) {
            ttt.postScale(yy, yy);
        } else {
            ttt.postScale(xx, xx);
        }
        Bitmap resizedBitmap = Bitmap.createBitmap(map, 0, 0, map.getWidth(), map.getHeight(),
                ttt, true);

        canvas.drawBitmap(resizedBitmap, (width - resizedBitmap.getWidth()) / 2, height / 4 -
                resizedBitmap.getHeight()
                        / 2, paint);
    }

    @Override
    public void setBackgroundColor(int color) {
        this.color = color;
    }

    /**
     * 未完成颜色，从1渐变到2
     *
     * @param bgColor1
     * @param bgColor2
     */
    public void setBgColor(int bgColor1, int bgColor2) {
        this.bgColor1 = bgColor1;
        this.bgColor2 = bgColor2;
    }

    /**
     * 未完成颜色
     *
     * @param bgColor
     */
    public void setBgColor(int bgColor) {
        this.bgColor2 = bgColor;
    }

    /**
     * 完成颜色，从1渐变到2
     *
     * @param fgColor1
     * @param fgColor2
     */
    public void setFgColor(int fgColor1, int fgColor2) {
        this.fgColor1 = fgColor1;
        this.fgColor2 = fgColor2;
    }

    /**
     * 完成颜色
     *
     * @param fgColor
     */
    public void setFgColor(int fgColor) {
        this.fgColor2 = fgColor;
    }

    /**
     * 完成值，必须在maxValue和minValue后设置。
     *
     * @param value
     */
    public void setValue(float value) {
        this.showValue = value;
        if (value < minValue) {
            value = minValue;
        } else if (value > maxValue) {
            value = maxValue;
        }
        this.value = value;
    }

    /**
     * 最大值，默认100
     *
     * @param maxValue
     */
    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * 最小值，默认0
     *
     * @param minValue
     */
    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    /**
     * 设置扇形角度，0-180之间
     *
     * @param angle
     */
    public void setAngle(float angle) {
        if (angle > 180)
            angle = 180;
        this.angle = angle;
    }

    /**
     * 设值小数点位数
     *
     * @param decimalNum
     */
    public void setDecimalNum(int decimalNum) {
        if (decimalNum < 0) {
            return;
        }
        this.decimalNum = decimalNum;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawChart(canvas);
    }
}
