package com.ztesoft.level1.chart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ztesoft.level1.Level1Util;
import com.ztesoft.level1.R;

import java.lang.reflect.Method;


/**
 * 旋转炫酷饼图
 *
 * @author fanlei@asiainfo-linkage.com 2013-3-22 下午2:00:44
 */
public class RotatePieChart extends View {
    private Context context;

    private static final int ANIMATION_DURATION = 800; // 动画持续时间
    private static final int ANIMATION_STATE_RUNNING = 1; // 动画正在运行
    private static final int ANIMATION_STATE_DOWN = 2; // 动画结束

    private static RectF OVAL;// 饼图绘制区域
    private Point center; // 这个是饼图的中心位置

    private int[] values; // 每部分的大小
    private int[] degrees; // 值转换成角度
    private String[] percents;//各部分所占百分比
    private int[] colors; // 每部分的颜色值

    private Paint paint; // 饼图画笔
    private Paint maskPaint; // 遮罩层画笔
    private Paint textPaint; // 文字画笔

    private Bitmap mask; // 用于遮罩的Bitmap

    private int startDegree = 90; // 让初始的时候，圆饼是从箭头位置开始画出的
    private Point lastEventPoint;// 饼图触摸最后事件
    private int currentTargetIndex = -1;// 饼图各部分序号
    private int eventRadius = 0; // 事件距离饼图中心的距离

    private int animState = ANIMATION_STATE_DOWN;// 动画状态
    private boolean animEnabled = false;// 是否启动动画
    private long animStartTime;// 动画启动时间

    private String methodName = null;// 回调函数名称
    //高度
    private int pieHeight = -1;
    //放大比例
    private float scale = 0.8f;

    public RotatePieChart(Context context, int[] values) {
        super(context);

        this.context = context;
        this.values = values;
    }

    public RotatePieChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotatePieChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void create() {
        // 饼图画笔
        paint = new Paint();
        paint.setAntiAlias(true);

        // 遮罩层画笔
        maskPaint = new Paint();
        maskPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setAlpha(100);
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);

        degrees = getDegrees();

        mask = BitmapFactory.decodeResource(getResources(), R.drawable.mask);
        //调整遮罩层大小
        int width = mask.getWidth();
        int height = mask.getHeight();
        //没有设定高度默认为图片的0.8f;
        if (pieHeight > 0) {
            scale = (float) (pieHeight * 1.00 / width);
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        mask = Bitmap.createBitmap(mask, 0, 0, width, height, matrix, true);
        width = mask.getWidth() - 14;
        height = mask.getHeight() - (int) (11 * scale) - 14;

        textPaint.setTextSize(Level1Util.getSpSize((int) (18 * scale / 0.8)));

        OVAL = new RectF(14, 14, width, height);

        // 启动动画效果
        animEnabled = true;
    }

    /**
     * 计算总和
     */
    private int sum(int[] values) {
        int sum = 0;
        for (int value : values) {
            sum += value;
        }

        return sum;
    }

    /**
     * 根据每部分所占的比例，来计算每个区域在整个圆中所占的角度 但是，有个小细节，就是计算的时候注意，可能并不能整除的情况，这个时候，为了
     * 避免所有的角度和小于360度的情况，姑且将剩余的部分送给某个部分，反正也不影响
     *
     * @return 返回各区域所占角度的数组
     */
    private int[] getDegrees() {
        int sum = this.sum(values);

        int[] degrees = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            degrees[i] = (int) Math.floor((double) values[i] / (double) sum * 360);
        }
        int angleSum = this.sum(degrees);
        if (angleSum != 360) {
            // 上面的计算可能导致和小于360
            int c = 360 - angleSum;
            degrees[values.length - 1] += c; // 姑且让最后一个的值稍大点
        }

        return degrees;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = mask.getHeight();
        int width = mask.getWidth();
        this.setMeasuredDimension(width, height);
    }

    /**
     * 重写这个方法来画出整个界面
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mask == null) return;
        if (animEnabled) {

            //说明是启动的时候，需要旋转着画出饼图
            if (animState == ANIMATION_STATE_DOWN) {
                animStartTime = SystemClock.uptimeMillis();
                animState = ANIMATION_STATE_RUNNING;
            }

            final long currentTimeDiff = SystemClock.uptimeMillis()
                    - animStartTime;
            int currentMaxDegree = (int) ((float) currentTimeDiff
                    / ANIMATION_DURATION * 360f);

            if (currentMaxDegree >= 360) {
                // 动画结束状态,停止绘制
                currentMaxDegree = 360;
                animState = ANIMATION_STATE_DOWN;
                animEnabled = false;
            }

            int[] degrees = getDegrees();
            int startAngle = this.startDegree;

            // 获取当前时刻最大可以旋转的角度所位于的区域
            int maxIndex = getEventPart(currentMaxDegree);

            // 根据不同的颜色画饼图
            for (int i = 0; i <= maxIndex; i++) {
                int currentDegree = degrees[i];

                if (i == maxIndex) {
                    // 对于当前最后一个绘制区域，可能只是一部分，需要获取其偏移量
                    currentDegree = getOffsetOfPartStart(currentMaxDegree,
                            maxIndex);
                }

                if (i > 0) {
                    // 注意，每次画饼图，记得计算startAngle
                    startAngle += degrees[i - 1];
                }

                paint.setColor(colors[i]);
                canvas.drawArc(OVAL, startAngle, currentDegree, true, paint);
            }

            if (animState == ANIMATION_STATE_DOWN) {
                // 如果动画结束了，则调整当前箭头位于所在区域的中心方向
                onStop();
            } else {
                postInvalidate();
            }

        } else {
            int[] degrees = getDegrees();
            int startAngle = this.startDegree;

            //每个区域的颜色不同，但是这里只要控制好每个区域的角度就可以了，整个是个圆
            for (int i = 0; i < values.length; i++) {
                paint.setColor(colors[i]);
                if (i > 0) {
                    startAngle += degrees[i - 1];
                }
                canvas.drawArc(OVAL, startAngle, degrees[i], true, paint);
            }

            //画各个扇区的百分比
            drawArcPercent(canvas);
        }

        //中心区域实时显示的百分比
        if (currentTargetIndex >= 0) {
            computeCenter();
            String percentStr = percents[currentTargetIndex] + "%";
            float perLength = textPaint.measureText(percentStr);
            canvas.drawText(percentStr, center.x - perLength / 2, center.y, textPaint);
        }

        //画出饼图之后，画遮罩图片，这样图片就位于 饼图之上了，形成了遮罩的效果
        canvas.drawBitmap(mask, 0, 0, maskPaint);
    }

    /**
     * <p>Title: drawArcPercent</p>
     * <p>Description: 画各个扇区的占比情况
     *
     * @param canvas 画布
     */
    public void drawArcPercent(Canvas canvas) {
        int[] degree = getDegrees();
        int[] newDegree = new int[degree.length];
        String[] newPercent = new String[degree.length];
        if (currentTargetIndex == 0) {
            newDegree = degree;
            for (int i = 0; i < degree.length; i++) {
                newPercent[i] = percents[i];
            }
        } else {
            int i = 0;
            for (int x = currentTargetIndex; x < degree.length; x++) {
                newDegree[i] = degree[x];
                newPercent[i] = percents[x];
                i++;
            }
            for (int xx = 0; xx < currentTargetIndex; xx++) {
                newDegree[i] = degree[xx];
                newPercent[i] = percents[xx];
                i++;
            }
        }

        computeCenter();

        double tempDegree;
        double invalidateDegree;
        double x, y;

        Paint percentPaint = new Paint();
        percentPaint.setAlpha(100);
        percentPaint.setAntiAlias(true);
        percentPaint.setColor(Color.WHITE);
        percentPaint.setTextSize(Level1Util.getSpSize((int) (15 * scale / 0.8)));

        for (int i = 1; i <= newDegree.length; i++) {

            String percentStr = newPercent[i - 1] + "%";
            double perLength = percentPaint.measureText(percentStr);

            if (i == 1) {
                x = center.x - perLength / 2;
                y = center.y + getRadius() / 2;
            } else {
                tempDegree = getArcCenter(i, newDegree);
                if (tempDegree >= 90 && tempDegree < 180) {
                    invalidateDegree = tempDegree - 90;
                    invalidateDegree = invalidateDegree / 180 * Math.PI;
                    x = center.x - 2 * getRadius() * Math.cos(invalidateDegree) / 3;
                    y = center.y - 2 * getRadius() * Math.sin(invalidateDegree) / 3;
                } else if (tempDegree >= 180 && tempDegree < 270) {
                    invalidateDegree = tempDegree - 180;
                    invalidateDegree = invalidateDegree / 180 * Math.PI;
                    x = center.x + getRadius() * Math.sin(invalidateDegree) / 2;
                    y = center.y - getRadius() * Math.cos(invalidateDegree) / 2;
                } else if (tempDegree >= 270 && tempDegree < 360) {
                    invalidateDegree = 360 - tempDegree;
                    invalidateDegree = invalidateDegree / 180 * Math.PI;
                    x = center.x + getRadius() * Math.sin(invalidateDegree) / 2;
                    y = center.y + getRadius() * Math.cos(invalidateDegree) / 2;
                } else {
                    invalidateDegree = tempDegree;
                    invalidateDegree = invalidateDegree / 180 * Math.PI;
                    x = center.x - 2 * getRadius() * Math.sin(invalidateDegree) / 3;
                    y = center.y + 2 * getRadius() * Math.cos(invalidateDegree) / 3;
                }
            }

            if (Double.parseDouble(newPercent[i - 1]) >= 15) {
                canvas.drawText(newPercent[i - 1] + "%", (float) (x), (float) (y), percentPaint);
            }
        }
    }

    /**
     * <p>Title: getArcCenter</p>
     * <p>Description: 计算各扇区中分角之前已扫描的角度之和
     *
     * @param index
     * @param degrees
     * @return sumDegree
     */
    public double getArcCenter(int index, int[] degrees) {
        double sumDegree = (double) degrees[0] / 2;
        for (int i = 1; i < index; i++) {
            if (i == index - 1) {
                sumDegree += (double) degrees[i] / 2;
            } else {
                sumDegree += (double) degrees[i];
            }
        }
        return sumDegree;
    }

    /**
     * 处理饼图的转动
     */
    public boolean onTouchEvent(MotionEvent event) {

        if (animEnabled && animState == ANIMATION_STATE_RUNNING) {
            return super.onTouchEvent(event);
        }

        Point eventPoint = getEventAbsoluteLocation(event);
        computeCenter(); // 计算中心坐标

        // 计算当前位置相对于x轴正方向的角度
        // 在下面这个方法中计算了eventRadius的
        int newAngle = getEventAngle(eventPoint, center);

        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 记录lastEventPoint
                lastEventPoint = eventPoint;

                //只要当触点在饼图内部时才使滑动生效
                if (eventRadius > getRadius()) {
                    return super.onTouchEvent(event);
                }

                break;
            case MotionEvent.ACTION_MOVE:
                // 这里处理滑动
                rotate(eventPoint, newAngle);

                // 处理之后，记得更新lastEventPoint
                lastEventPoint = eventPoint;
                break;

            case MotionEvent.ACTION_UP:
                onStop();

                // 回调函数
                callBack(currentTargetIndex + "");
                break;

            default:
                break;
        }

        return true;
    }

    /**
     * 当我们停止旋转的时候，如果当前下方箭头位于某个区域的非中心位置，则我们需要计算 偏移量，并且将箭头指向中心位置
     */
    private void onStop() {

        int targetAngle = getTargetDegree();
        currentTargetIndex = getEventPart(targetAngle);

        int offset = getOffsetOfPartCenter(targetAngle, currentTargetIndex);

        /**
         * offset>0,说明当前箭头位于中心位置右边，则所有区域沿着顺时针旋转offset大小的角度 offset<0,正好相反
         */
        startDegree += offset;

        postInvalidateDelayed(200);
    }

    private void rotate(Point eventPoint, int newDegree) {
        // 计算上一个位置相对于x轴正方向的角度
        int lastDegree = getEventAngle(lastEventPoint, center);

        /**
         * 其实转动就是不断的更新画圆弧时候的起始角度，这样，每次从新的起始角度重画圆弧就形成了转动的效果
         */
        startDegree += newDegree - lastDegree;

        // 转多圈的时候，限定startAngle始终在-360-360度之间
        if (startDegree >= 360) {
            startDegree -= 360;
        } else if (startDegree <= -360) {
            startDegree += 360;
        }

        // 获取当前下方箭头所在的区域，这样在onDraw的时候就会转到不同区域显示的是当前区域对应的信息
        int targetDegree = getTargetDegree();
        currentTargetIndex = getEventPart(targetDegree);

        // 请求重新绘制界面，调用onDraw方法
        postInvalidate();
    }

    /**
     * 获取当前事件event相对于屏幕的坐标
     */
    protected Point getEventAbsoluteLocation(MotionEvent event) {
        int[] location = new int[2];
        this.getLocationOnScreen(location); // 当前控件在屏幕上的位置

        int x = (int) event.getX();
        int y = (int) event.getY();

        // x += location[0];
        // y += location[1]; // 这样x,y就代表当前事件相对于整个屏幕的坐标

        Point p = new Point(x, y);

        return p;
    }

    /**
     * 获取当前饼图的中心坐标，相对于屏幕左上角
     */
    protected void computeCenter() {
        if (center == null) {
            int x = (int) OVAL.left + (int) ((OVAL.right - OVAL.left) / 2f);
            int y = (int) OVAL.top + (int) ((OVAL.bottom - OVAL.top) / 2f); // 状态栏的高度是50
            center = new Point(x, y);
        }
    }

    /**
     * 获取半径
     */
    protected int getRadius() {
        int radius = (int) ((OVAL.right - OVAL.left) / 2f);
        return radius;
    }

    /**
     * 获取事件坐标相对于饼图的中心x轴正方向的角度
     * 这里就是坐标系的转换，本例中使用饼图的中心作为坐标中心，就是我们从初中到大学一直使用的"正常"坐标系。
     * 但是涉及到圆的转动，本例中一律相对于x正方向顺时针来计算某个事件在坐标系中的位置
     */
    protected int getEventAngle(Point eventPoint, Point center) {
        int x = eventPoint.x - center.x;// x轴方向的偏移量
        int y = eventPoint.y - center.y; // y轴方向的偏移量

        double z = Math.hypot(Math.abs(x), Math.abs(y)); // 求直角三角形斜边的长度

        eventRadius = (int) z;
        double sinA = (double) Math.abs(y) / z;

        double asin = Math.asin(sinA); // 求反正玄，得到当前点和x轴的角度,是最小的那个

        int degree = (int) (asin / 3.14f * 180f);

        // 下面就需要根据x,y的正负，来判断当前点和x轴的正方向的夹角
        int realDegree;
        if (x <= 0 && y <= 0) {
            // 左上方，返回180+angle
            realDegree = 180 + degree;
        } else if (x >= 0 && y <= 0) {
            // 右上方，返回360-angle
            realDegree = 360 - degree;
        } else if (x <= 0 && y >= 0) {
            // 左下方，返回180-angle
            realDegree = 180 - degree;
        } else {
            // 右下方,直接返回
            realDegree = degree;
        }

        return realDegree;
    }

    /**
     * 获取当前下方箭头位置相对于startDegree的角度值 注意，下方箭头相对于x轴正方向是90度
     */
    protected int getTargetDegree() {
        int targetDegree = -1;

        int tmpStart = startDegree;

        /**
         * 如果当前startAngle为负数，则直接+360，转换为正值
         */
        if (tmpStart < 0) {
            tmpStart += 360;
        }

        if (tmpStart < 90) {
            /**
             * 如果startAngle小于90度（可能为负数）
             */
            targetDegree = 90 - tmpStart;
        } else {
            /**
             * 如果startAngle大于90，由于在每次计算startAngle的时候，限定了其最大为360度，所以 直接可以按照如下公式计算
             */
            targetDegree = 360 + 90 - tmpStart;
        }

        return targetDegree;
    }

    /**
     * 判断角度为degree坐落在饼图的哪个部分 注意，这里的角度一定是正值，而且不是相对于x轴正方向，而是相对于startAngle
     * 返回当前部分的索引
     */
    protected int getEventPart(int degree) {
        int currentSum = 0;

        for (int i = 0; i < degrees.length; i++) {
            currentSum += degrees[i];
            if (currentSum >= degree) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 在已经得知了当前degree位于targetIndex区域的情况下，计算angle相对于区域targetIndex起始位置的偏移量
     */
    protected int getOffsetOfPartStart(int degree, int targetIndex) {
        int currentSum = 0;
        for (int i = 0; i < targetIndex; i++) {
            currentSum += degrees[i];
        }

        int offset = degree - currentSum;

        return offset;
    }

    /**
     * 在已经得知了当前degree位于targetIndex区域的情况下，计算angle相对于区域targetIndex中心位置的偏移量
     * 这个是当我们停止旋转的时候，通过计算偏移量，来使得箭头指向当前区域的中心位置
     */
    protected int getOffsetOfPartCenter(int degree, int targetIndex) {
        int currentSum = 0;
        for (int i = 0; i <= targetIndex; i++) {
            currentSum += degrees[i];
        }

        int offset = degree - (currentSum - degrees[targetIndex] / 2);

        // 超过一半,则offset>0；未超过一半,则offset<0
        return offset;
    }

    /**
     * 回调函数
     * currentIndex为当前箭头所指扇区的序号
     */
    private void callBack(String currentIndex) {
        if (methodName != null && methodName.trim().length() > 0) {
            try {
                Class yourClass = Class.forName(context.getClass().getName());// 
                // 假设你要动态加载的类为YourClass
                Class[] parameterTypes = new Class[1];// 这里你要调用的方法只有一个参数
                parameterTypes[0] = String.class;// 参数类型为String
                Method method = yourClass.getMethod(methodName, parameterTypes);// 
                // 这里假设你的类为YourClass，而要调用的方法是methodName
                method.invoke(context, currentIndex);// 调用方法
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    public void setValues(int[] values) {
        this.values = values;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setPercents(String[] percents) {
        this.percents = percents;
    }

    class Point {

        public int x;
        public int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int[] getPoint() {
            int[] point = new int[2];
            point[0] = x;
            point[1] = y;

            return point;
        }

        public String toString() {
            return new StringBuilder("[").append(x).append(",").append(y).append("]").toString();
        }
    }

    public int getPieHeight() {
        return pieHeight;
    }

    public void setPieHeight(int pieHeight) {
        this.pieHeight = pieHeight;
    }
}