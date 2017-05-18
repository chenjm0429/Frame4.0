package com.ztesoft.level1.chart;

import com.ztesoft.level1.Level1Util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.view.View;

/**
 * 占比图
 *
 * @author fanlei@asiainfo-linkage.com  2012-3-21 下午12:32:08
 * @ClassName: TyincrRoundBar
 */
public class PercentBarChart extends View {
    private Context context;
    //图形起始X坐标
    private int xPosition;
    //图形起始Y坐标
    private int yPosition;
    //横柱图高度
    private int height;
    //横柱图间距
    private int interval;
    //圆角角度
    private int angle;
    //占比宽度    
    private float[][] barWidth;
    //占比颜色
    private String[] colors = ColorDefault.colors;
    //占比描述 级字体大小和颜色
    private String[] barDesc;
    private int barDescFontSize;
    private String barDescColor = "#000000";

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public PercentBarChart(Context context) {
        super(context);
        this.context = context;
    }

    public void setXPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public void setYPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    public void setHeight(int height) {
        this.height = Level1Util.getDipSize(height);
    }

    public void setInterval(int interval) {
        this.interval = Level1Util.getDipSize(interval);
    }

    public void setBarWidth(float[][] barWidth) {
        this.barWidth = barWidth;
    }

    public void setColors(String[] colors) {
        this.colors = colors;
    }

    public void setBarDesc(String[] barDesc) {
        this.barDesc = barDesc;
    }

    public void setBarDescColor(String barDescColor) {
        this.barDescColor = barDescColor;
    }

    public void setBarDescFontSize(int barDescFontSize) {
        this.barDescFontSize = Level1Util.getDipSize(barDescFontSize);
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    /**
     * 画图
     * <p>Title: drawChart</p>
     *
     * @param canvas
     * @author fanlei@asiainfo-linkage.com  2012-7-24 下午4:04:31
     */
    public void drawChart(Canvas canvas) {
        int yOrigin = yPosition;
        Paint paint = new Paint();
        Paint txtPaint = new Paint();
        txtPaint.setTextSize(barDescFontSize);
        txtPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setTextAlign(Align.CENTER);
        txtPaint.setColor(Color.parseColor(barDescColor));
        for (int i = 0; i < barWidth.length; i++) {
            int tempXPosition = xPosition;
            for (int j = 0; j < barWidth[i].length; j++) {
                if (barWidth[i][j] > 0) {
                    paint.setColor(Color.parseColor(colors[j]));
                    paint.setAntiAlias(true);
                    int percentWidth = Level1Util.getDipSize(barWidth[i][j]);
                    if (j == 0) {
                        //左圆角矩形块
                        RectF leftRoundRect = new RectF(tempXPosition - 10, yOrigin, 
                                tempXPosition + 10, yOrigin + height);
                        canvas.drawRoundRect(leftRoundRect, angle, angle, paint);
                        canvas.drawRect(tempXPosition, yOrigin, tempXPosition + percentWidth, 
                                yOrigin + height, paint);
                    } else if (j == barWidth[i].length - 1) {
                        canvas.drawRect(tempXPosition, yOrigin, tempXPosition + percentWidth,
                                yOrigin + height, paint);
                        //右圆角矩形块
                        RectF rightRoundRect = new RectF(tempXPosition + percentWidth - 10,
                                yOrigin, tempXPosition + percentWidth + 10, yOrigin + height);
                        canvas.drawRoundRect(rightRoundRect, angle, angle, paint);
                    } else {
                        canvas.drawRect(tempXPosition, yOrigin, tempXPosition + percentWidth,
                                yOrigin + height, paint);
                    }
                    //写描述
                    canvas.drawText(barDesc[j], tempXPosition + percentWidth / 2, yOrigin +
                            height / 2 + 5, txtPaint);
                    tempXPosition += percentWidth;
                }
            }
            yOrigin += (height + interval);
        }
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