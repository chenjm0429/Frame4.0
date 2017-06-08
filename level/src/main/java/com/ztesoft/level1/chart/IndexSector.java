package com.ztesoft.level1.chart;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.ztesoft.level1.R;

/**
 * 文件名称 : CompleteSector
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 指数仪表盘图形
 * <p>
 * 创建时间 : 2017/4/1 9:46
 * <p>
 */
public class IndexSector extends View {

    private float maxValue = 100, minValue = 0, value = minValue;

    //背景色
    private int color = Color.WHITE;

    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();//颜色渐变插值器 

    private int width;
    private int height;
    private float angle = 270;
    private float textSize = 50;
    private int[] bgColors = new int[(int) angle];

    public IndexSector(Context context) {
        super(context);
    }

    public IndexSector(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IndexSector(Context context, AttributeSet attrs, int defStyle) {
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
//        paint_bg.setColor(bgColor2);
//        Shader mShader = new LinearGradient(width / 2, height, width / 2, 0, bgColor1, bgColor2,
//                Shader.TileMode.REPEAT);
//        paint_bg.setShader(mShader);

        RectF rf = new RectF(0, 0, width, height * 2);


        for (int i = 0; i < angle; i++) {
            Integer color = (Integer) argbEvaluator.evaluate(i / angle, Color.parseColor
                    ("#2d2954"), Color.parseColor("#5ce9ff"));

            bgColors[i] = color;

            paint_bg.setColor(color);

            canvas.drawArc(rf, -(90 + angle / 2 - i), 1, true, paint_bg);  //整体圆弧
        }

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);

        float bb = value / (maxValue - minValue);

        RectF rf2 = new RectF(width / 8, height / 4, width / 8 * 7, height / 4 * 7);
        canvas.drawArc(rf2, 0, 360, true, paint);

        // 内环增加1度，防止外环线露出来
        Resources res = this.getContext().getResources();
        Bitmap pointBarBM = BitmapFactory.decodeResource(res, R.drawable.index_arrow);

        // 总弧度为angle
        float cc = bb * angle - angle / 2;// 获取旋转角度
        Matrix matrix = new Matrix();
//        matrix.preTranslate(width / 2 - 4, 0);
//        matrix.preRotate(cc, 4, height);
        matrix.preTranslate(width / 2, 0);
        matrix.preRotate(cc, 0, height);
        canvas.drawBitmap(pointBarBM, matrix, paint);

        TextView textView = new TextView(getContext());
        int position = (int) (bb * angle);
        textView.setTextColor(bgColors[position]);
        textView.setTextSize(textSize);

        textView.setText(value + "");
        textView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
        textView.buildDrawingCache();
        Bitmap map = textView.getDrawingCache();

        Matrix ttt = new Matrix();
        float xx = (float) width / map.getWidth();
        float yy = (float) height / 2 / map.getHeight();
        if (xx > yy) {
            ttt.postScale(yy, yy);
        } else {
            ttt.postScale(xx, xx);
        }
        Bitmap resizedBitmap = Bitmap.createBitmap(map, 0, 0, map.getWidth(), map.getHeight(),
                ttt, true);

        canvas.drawBitmap(resizedBitmap, (width - resizedBitmap.getWidth()) / 2, height -
                resizedBitmap.getHeight() / 2, paint);
    }

    @Override
    public void setBackgroundColor(int color) {
        this.color = color;
    }


    /**
     * 完成值，必须在maxValue和minValue后设置。
     *
     * @param value
     */
    public void setValue(float value) {
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
        this.angle = angle;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawChart(canvas);
    }
}
