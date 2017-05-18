package com.steema.teechart;

import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.view.MotionEvent;
import android.view.View;

import com.steema.teechart.styles.Series;
import com.steema.teechart.styles.SeriesCollection;
import com.steema.teechart.styles.StringList;
import com.ztesoft.level1.R;

/***
 * 遮罩滑动
 * @author wangsq
 *
 */
public class MaskView extends View {
    private Context context;
    private int maskSplitNum = 4;
    private int moveMask = -1;
    private Chart chart;
    private Bitmap bg_left, bg_right;
    private int showNum = 90;
    private int hdWidth = 20;
    private Region leftRegion = null;
    private Region middleRegion = null;
    private Region rightRegion = null;
    int firstView = 0;
    int lastView = 0;
    int moveX = 0;
    private String maskMethod = null;
    private int mLastMotionX, mLastMotionY;

    public MaskView(Context context, Chart chart) {
        super(context);
        this.context = context;
        this.chart = chart;
    }

    /***
     * 画遮罩层
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        SeriesCollection series = chart.getSeries();
        if (series.size() == 0) {
            return;
        }

        //获取显示图形的坐标
        int tickPos[] = new int[0];
        if (series.size() > 0) {
            Series series1 = series.getSeries(0);
            int first = series1.getFirstVisible();
            int last = series1.getLastVisible();
            tickPos = new int[last - first + 1];
            for (int x = first, j = 0; x < last + 1; x++, j++) {
                tickPos[j] = chart.getAxes().getBottom().calcXPosValue(x);
            }
        }
        //画分割线
        if (tickPos.length > this.maskSplitNum) {
            int avg = tickPos.length / this.maskSplitNum;
            Paint pt = new Paint();
            pt.setColor(android.graphics.Color.BLACK);
            pt.setTextSize(13);
            pt.setAntiAlias(true);
            for (int i = 0; i < series.size(); i++) {
                Series series1 = series.getSeries(i);
                StringList lbs = series1.getLabels();
                if (series1.getActive()) {
                    int first = series1.getFirstVisible();
                    int last = series1.getLastVisible();
                    for (int x = first, j = 0; x < last + 1; x++, j++) {
                        if (x % avg == 0) {
                            int xPoint = chart.getAxes().getBottom().calcXPosValue(x);
                            canvas.drawLine(xPoint, chart.getChartRect().getTop(), xPoint, chart
                                    .getChartRect().getBottom(), pt);
                            canvas.drawText(lbs.getString(x), xPoint, chart.getChartRect()
                                    .getBottom() - 3, pt);
                        }
                    }
                    break;
                }
            }
        }
        if (moveMask == -1) {
            bg_left = BitmapFactory.decodeResource(getResources(), R.drawable.range_left);
            bg_right = BitmapFactory.decodeResource(getResources(), R.drawable.range_right);
            hdWidth = bg_right.getWidth();
            canvas.save();
            int leftOff = 0;
            if (tickPos.length > showNum) {
                leftOff = tickPos[tickPos.length - showNum];
                firstView = series.getSeries(0).getLastVisible() - showNum;
            } else {
                leftOff = tickPos[0];
                firstView = series.getSeries(0).getLastVisible() - tickPos.length;
            }
            if ((leftOff + hdWidth) >= chart.getChartRect().getRight() - hdWidth)
                leftOff = chart.getChartRect().getRight() - 2 * hdWidth + (int) (hdWidth * 0.2);
            lastView = series.getSeries(0).getLastVisible();
            canvas.clipRect(chart.getChartRect().getLeft(), chart.getChartRect().getTop(), chart
                    .getChartRect().getRight(), chart.getChartRect().getBottom());
            canvas.clipRect(leftOff, chart.getChartRect().getTop() + 2, chart.getChartRect()
                    .getRight(), chart.getChartRect().getBottom() - 2, Region.Op.DIFFERENCE);
            Rect rectx = new Rect(chart.getChartRect().getLeft(), chart.getChartRect().getTop(),
                    chart.getChartRect().getRight(), chart.getChartRect().getBottom());
            Paint pt = new Paint();
            pt.setColor(android.graphics.Color.BLACK);
            pt.setAlpha(150);
            pt.setStyle(Paint.Style.FILL);
            canvas.drawRect(rectx, pt);
            pt.setStyle(Paint.Style.STROKE);
            pt.setStrokeWidth(5);
            pt.setColor(android.graphics.Color.GRAY);
            canvas.drawRect(rectx, pt);
            canvas.restore();
            Path path = new Path();
            path.moveTo(leftOff, chart.getChartRect().getTop());
            path.lineTo(chart.getChartRect().getRight(), chart.getChartRect().getTop());
            path.lineTo(chart.getChartRect().getRight(), chart.getChartRect().getBottom());
            path.lineTo(leftOff, chart.getChartRect().getBottom());
            path.lineTo(leftOff, chart.getChartRect().getTop());

            //画区域
            RectF r = new RectF();
            path.computeBounds(r, true);
            middleRegion = new Region();
            middleRegion.setPath(path, new Region((int) r.left, (int) r.top, (int) r.right, (int)
                    r.bottom));

            //右边
            pt.setStyle(Paint.Style.STROKE);
            pt.setStrokeWidth(1);
            pt.setColor(android.graphics.Color.GRAY);

            path = new Path();
            path.moveTo(chart.getChartRect().getRight() - hdWidth, chart.getChartRect().getTop()
                    + 10);
            path.lineTo(chart.getChartRect().getRight(), chart.getChartRect().getTop() + 10);
            path.lineTo(chart.getChartRect().getRight(), chart.getChartRect().getBottom() - 10);
            path.lineTo(chart.getChartRect().getRight() - hdWidth, chart.getChartRect().getBottom
                    () - 10);
            path.lineTo(chart.getChartRect().getRight() - hdWidth, chart.getChartRect().getTop()
                    + 10);
            canvas.drawBitmap(bg_right, chart.getChartRect().getRight() - hdWidth, chart
                    .getChartRect().getTop(), pt);
            r = new RectF();
            path.computeBounds(r, true);
            rightRegion = new Region();
            rightRegion.setPath(path, new Region((int) r.left, (int) r.top, (int) r.right, (int)
                    r.bottom));

            //左边
            pt.setStyle(Paint.Style.STROKE);
            pt.setStrokeWidth(1);
            pt.setColor(android.graphics.Color.GRAY);
            path = new Path();
            path.moveTo(leftOff, chart.getChartRect().getTop());
            path.lineTo(leftOff, chart.getChartRect().getTop() + 10);
            canvas.drawPath(path, pt);

            //path=new Path();  
            path.moveTo(leftOff, chart.getChartRect().getBottom());
            path.lineTo(leftOff, chart.getChartRect().getBottom() - 10);
            canvas.drawPath(path, pt);

            path.moveTo(leftOff, chart.getChartRect().getTop() + 10);
            path.lineTo(leftOff + hdWidth, chart.getChartRect().getTop() + 10);
            path.lineTo(leftOff + hdWidth, chart.getChartRect().getBottom() - 10);
            path.lineTo(leftOff, chart.getChartRect().getBottom() - 10);
            path.lineTo(leftOff, chart.getChartRect().getTop() + 10);
            canvas.drawBitmap(bg_left, leftOff, chart.getChartRect().getTop(), pt);
            r = new RectF();
            path.computeBounds(r, true);
            leftRegion = new Region();
            leftRegion.setPath(path, new Region((int) r.left, (int) r.top, (int) r.right, (int) r
                    .bottom));
        }
        if (moveMask != -1) {
            canvas.save();
            Rect midleRec = middleRegion.getBounds();
            Rect leftRec = leftRegion.getBounds();
            Rect rightRec = rightRegion.getBounds();
            canvas.clipRect(chart.getChartRect().getLeft(), chart.getChartRect().getTop(), chart
                    .getChartRect().getRight(), chart.getChartRect().getBottom());
            //左滑块
            int leftMove = 0;
            int rightMove = 0;
            if (moveMask == 1) {
                leftMove = moveX;
            } else if (moveMask == 2) {
                rightMove = leftMove = moveX;
            } else if (moveMask == 3) {
                rightMove = moveX;
            } else if (moveMask == 99) {
                leftMove = rightMove = 0;
            }

            if (leftRec.right + leftMove - (int) (hdWidth * 0.2) > rightRec.left + rightMove) {
                if (moveMask == 1) {
                    leftMove = 0;
                } else if (moveMask == 3) {
                    rightMove = 0;
                } else {
                    leftMove = rightMove = 0;
                }
            }

            if (leftRec.left + leftMove < chart.getChartRect().getLeft()) {
                leftMove = chart.getChartRect().getLeft() - leftRec.left;
                //如果滑动的是中间部位，当向左向右时，移动到最左，最右时不动
                if (moveMask == 2) {
                    rightMove = 0;
                }
            }

            if (rightRec.right + rightMove > chart.getChartRect().getRight()) {
                rightMove = chart.getChartRect().getRight() - rightRec.right;
                //如果滑动的是中间部位，当向左向右时，移动到最左，最右时不动
                if (moveMask == 2) {
                    leftMove = 0;
                }
            }
            canvas.clipRect(midleRec.left + leftMove, chart.getChartRect().getTop() + 2, midleRec
                    .right + rightMove, chart.getChartRect().getBottom() - 2, Region.Op
                    .DIFFERENCE);
            Rect rectx = new Rect(chart.getChartRect().getLeft(), chart.getChartRect().getTop(),
                    chart.getChartRect().getRight(), chart.getChartRect().getBottom());
            Paint pt = new Paint();
            pt.setColor(android.graphics.Color.BLACK);
            pt.setAlpha(150);
            pt.setStyle(Paint.Style.FILL);
            canvas.drawRect(rectx, pt);
            pt.setStyle(Paint.Style.STROKE);
            pt.setStrokeWidth(5);
            pt.setColor(android.graphics.Color.GRAY);
            canvas.drawRect(rectx, pt);
            canvas.restore();

            Path path = new Path();
            path.moveTo(midleRec.left + leftMove, chart.getChartRect().getTop());
            path.lineTo(midleRec.right + rightMove, chart.getChartRect().getTop());
            path.lineTo(midleRec.right + rightMove, chart.getChartRect().getBottom());
            path.lineTo(midleRec.left + leftMove, chart.getChartRect().getBottom());
            path.lineTo(midleRec.left + leftMove, chart.getChartRect().getTop());
            //画区域
            RectF r = new RectF();
            path.computeBounds(r, true);
            middleRegion = new Region();
            middleRegion.setPath(path, new Region((int) r.left, (int) r.top, (int) r.right, (int)
                    r.bottom));

            //右滑块
            path = new Path();
            path.moveTo(rightRec.left + rightMove, chart.getChartRect().getTop());
            path.lineTo(rightRec.right + rightMove, chart.getChartRect().getTop());
            path.lineTo(rightRec.right + rightMove, chart.getChartRect().getBottom());
            path.lineTo(rightRec.left + rightMove, chart.getChartRect().getBottom());
            path.lineTo(rightRec.left + rightMove, chart.getChartRect().getTop());
            pt.setAlpha(225);

            //画区域
            r = new RectF();
            path.computeBounds(r, true);
            rightRegion = new Region();
            rightRegion.setPath(path, new Region((int) r.left, (int) r.top, (int) r.right, (int)
                    r.bottom));

            //左滑块
            path = new Path();
            path.moveTo(leftRec.left + leftMove, chart.getChartRect().getTop());
            path.lineTo(leftRec.right + leftMove, chart.getChartRect().getTop());
            path.lineTo(leftRec.right + leftMove, chart.getChartRect().getBottom());
            path.lineTo(leftRec.left + leftMove, chart.getChartRect().getBottom());
            path.lineTo(leftRec.left + leftMove, chart.getChartRect().getTop());

            //画区域
            r = new RectF();
            path.computeBounds(r, true);
            if (leftRec.left - chart.getChartRect().getLeft() < hdWidth) {
                canvas.drawBitmap(bg_left, leftRec.left + leftMove, chart.getChartRect().getTop()
                        , pt);
                canvas.drawBitmap(bg_right, rightRec.left + rightMove, chart.getChartRect()
                        .getTop(), pt);
            } else {
                canvas.drawBitmap(bg_right, rightRec.left + rightMove, chart.getChartRect()
                        .getTop(), pt);
                canvas.drawBitmap(bg_left, leftRec.left + leftMove, chart.getChartRect().getTop()
                        , pt);
            }

            leftRegion = new Region();
            leftRegion.setPath(path, new Region((int) r.left, (int) r.top, (int) r.right, (int) r
                    .bottom));
            midleRec = middleRegion.getBounds();
            leftRec = leftRegion.getBounds();
            rightRec = rightRegion.getBounds();
            boolean has = false;
            for (int k = 0; k < tickPos.length; k++) {
                if ((tickPos[k] >= midleRec.left && tickPos[k] <= midleRec.right)) {
                    Series series1 = series.getSeries(0);
                    if (series1.getActive()) {
                        if (!has) {
                            firstView = k;
                            has = true;
                        }
                        lastView = k;
                    }
                }
            }
        }
        canvas.save();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                if (leftRegion != null && leftRegion.contains(x, y) && rightRegion != null &&
                        rightRegion.contains(x, y)) {
                    moveMask = 2;
                } else if (leftRegion != null && leftRegion.contains(x, y)) {
                    moveMask = 1;
                } else if (rightRegion != null && rightRegion.contains(x, y)) {
                    moveMask = 3;
                } else if (middleRegion != null && middleRegion.contains(x, y)) {
                    moveMask = 2;
                } else {
                    moveMask = 99;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (moveMask != -1) {
                    moveX = x - mLastMotionX;
                    mLastMotionX = x;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (moveMask != -1) {
                    moveMask = 99;
                    callMaskBack(firstView, lastView);
                }
        }
        return true;
    }

    public void setMaskMethod(String maskMethod) {
        this.maskMethod = maskMethod;
    }

    public void setShowNum(int showNum) {
        this.showNum = showNum;
    }

    private void callMaskBack(int first, int last) {
        if (maskMethod != null && maskMethod.trim().length() > 0) {
            try {
                Class yourClass = Class.forName(context.getClass().getName());
                //假设你要动态加载的类为YourClass   
                Class[] parameterTypes = new Class[2];//这里你要调用的方法只有一个参数   
                parameterTypes[0] = Integer.class;//参数类型为String 
                parameterTypes[1] = Integer.class;//参数类型为String
                Method method = yourClass.getMethod(maskMethod, parameterTypes);
                //这里假设你的类为YourClass，而要调用的方法是methodName   
                method.invoke(context, first, last);//调用方法   
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}