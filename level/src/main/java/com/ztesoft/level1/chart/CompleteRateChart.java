package com.ztesoft.level1.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.ztesoft.level1.Level1Util;

import java.text.DecimalFormat;

/**
 * 完成率图
 *
 * @author fanlei@asiainfo-linkage.com  2012-7-24 上午9:41:09
 * @ClassName: CompleteRateChart
 */
public class CompleteRateChart extends View {

    private int xPoint;
    private int yPoint;
    private int width;
    private int height;
    private float upperLimit = 100;
    private float pointer;
    private float pointer1;
    private int descFontSize;
    private String descColor = "#000000";

    private static DecimalFormat myformat = new DecimalFormat("######.00");

    public String color = ColorDefault.colors[0];

    public CompleteRateChart(Context context) {
        super(context);
    }

    public void setXPoint(int xPoint) {
        this.xPoint = xPoint;
    }

    public void setYPoint(int yPoint) {
        this.yPoint = yPoint;
    }

    public void setWidth(int width) {
        this.width = Level1Util.getDipSize(width);
    }

    public void setHeight(int height) {
        this.height = Level1Util.getDipSize(height);
    }

    public void setPointer(float pointer) {
        this.pointer = Float.parseFloat(myformat.format(pointer));
        this.pointer1 = this.pointer;
    }

    public void setDescFontSize(int descFontSize) {
        this.descFontSize = descFontSize;
    }

    public void setDescColor(String descColor) {
        this.descColor = descColor;
    }

    public void setColor(String color) {
        this.color = color;
    }

    /**
     * 画图
     * <p>Title: drawChart</p>
     *
     * @param canvas
     * @author fanlei@asiainfo-linkage.com  2012-7-25 上午9:10:54
     */
    protected void drawChart(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        //画整条柱
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(0.5f);
        paint.setAlpha(220);
        canvas.drawRect(xPoint, yPoint, xPoint + width, yPoint + height, paint);

        //渲染整条柱背景
//		paint.setStyle(Paint.Style.FILL);
//		paint.setColor(colors[1]);
//		paint.setAlpha(220);
//		canvas.drawRect(0, textHeight, xwidth, xheight, paint);	

        //每份占用的大小
        float unit = width / upperLimit;
        if (pointer > 100) {
            pointer1 = pointer;
            pointer = 100;
        }
        float pointUnit = pointer * unit;

        //画完成柱
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.parseColor(color));
        canvas.drawRect(xPoint, yPoint, xPoint + pointUnit, yPoint + height, paint);

        //画占比文字
        paint.setTextSize(Level1Util.getDipSize(descFontSize));
        paint.setColor(Color.parseColor(descColor));
        String pointerStr = pointer1 + "%";
        canvas.drawText(pointerStr, xPoint + pointUnit, yPoint + height / 2 + Level1Util.getDipSize(5), paint);
    }

    /**
     * 重写Canvas中默认的画图方法，自动调用
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawChart(canvas);
    }
}
