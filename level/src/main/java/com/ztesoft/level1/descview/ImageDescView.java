package com.ztesoft.level1.descview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.math.BigDecimal;

/**
 * 圆形显示值、单位、名称组件，根据传入的内容绘制圆圈，值和单位放在圈内，名称放在圈外。
 *
 * @author wx
 */
public class ImageDescView extends View implements OnGestureListener {

    private Context mContext;
    // 该图形的编码
    private String code;

    // 数值、单位、名称
    private String value;
    private String unit;
    private String name;

    // 数值、单位、名称字体大小值
    private int valueSize = 40;
    private int unitSize = 20;
    private int nameSize = 32;

    // 是否需要动画如需要，则第一个值必须是数值型（可能为整数，小数），展现是从0开始递增/递减到目标值（建议动画时间1秒）。
    private boolean isAnimation = false;
    // isAnimation为true时，判断数值类型是否为整形(true为整形，false为浮点型)
    private boolean isValueInt = true;
    private int duration = 1000;
    private int nameHeight = 0;
    // 圈内、圈外字体颜色值
    private int inTextColor = Color.WHITE;
    private int outTextColor = Color.BLACK;

    // 圆圈北京颜色值
    private int bgColor = Color.RED;

    // 值与单位之间的距离
    private final int UNIT_OFFSET = 10;

    // 组件内容与边框的距离
    private final int MARGIN_OFFSET = 6;

    // 手势时间处理(点击、长按处理)
    private GestureDetector detector;

    // 长按、点击事件
    private OnDesViewLongClickListerer onLongClick;
    private OnDesViewClickListener onClick;

    // 控件默认长、宽
    private int defaultWidth = 0;
    private int defaultHeight = 0;

    // 半径
    private int radius = 0;

    BarAnimation anim;

    public ImageDescView(Context context) {
        super(context);
        this.mContext = context;
        detector = new GestureDetector(this);
    }

    public ImageDescView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        detector = new GestureDetector(this);
    }

    public ImageDescView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        detector = new GestureDetector(this);
    }

    private void init() {
        if (isAnimation) {
            anim = new BarAnimation();
            anim.setDuration(duration);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);

        if (null == value)
            return;

        if (defaultWidth == 0) {
            defaultWidth = getWidth() - 2 * MARGIN_OFFSET;
        }
        if (defaultHeight == 0) {
            defaultHeight = getHeight() - 2 * MARGIN_OFFSET;
        }

        // 半径
        radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2;

        Paint paint = new Paint();
        paint.setAntiAlias(true); // 是否抗锯齿
        paint.setColor(bgColor); // 设置颜色，这里Android内部定义的有Color类包含了一些常见颜色定义
        // paint.setTextScaleX(float scaleX) // 设置文本缩放倍数，1.0f为原始
        // paint.setTextSize(float textSize) // 设置字体大小
        // paint.setUnderlineText(booleanunderlineText) // 设置下划线

        // 如果名称为空，则圆形处于控件中心
        if (null == name) {
            nameHeight = 0;
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, paint);
        } else { // 根据名称字体计算圆形位置
            radius = (defaultWidth < defaultHeight - getFontHeight(nameSize) ? defaultWidth :
                    defaultHeight - getFontHeight(nameSize)) / 2;
            drawNameText(canvas);
            canvas.drawCircle(getWidth() / 2, (getHeight() - nameHeight) / 2, radius, paint);
        }

        // 写顶部值以及单位
        drawValueUnitText(canvas);
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

    private String sb = "...";

    /**
     * 画顶部值和单位
     *
     * @param canvas
     */
    private void drawValueUnitText(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(inTextColor);

        String content = mCount;
        // 需要缩小字体
        while (getFontWidth(content, valueSize) > (2 * radius - 2 * MARGIN_OFFSET)) {
            valueSize--;
            // 缩小到最小字体后value值宽度仍然很大，则中间加小数点
            if (valueSize < unitSize) {
                valueSize = unitSize;
                while (getFontWidth(content, valueSize) > (2 * radius - 2 * MARGIN_OFFSET)) {
                    StringBuffer strb = new StringBuffer(content);
                    if (strb.indexOf(sb) != -1) {
                        strb.replace(strb.indexOf(sb) - 1, strb.indexOf(sb) + 1 + sb.length(), sb);
                    } else {
                        strb.replace(strb.length() / 2, strb.length() / 2 + 1, sb);
                    }
                    content = strb.toString();
                }
            }
        }
        paint.setTextSize(valueSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        if (null == unit) {
            canvas.drawText(content, getWidth() / 2 - getFontWidth(content, valueSize) / 2,
                    (getHeight() - nameHeight) / 2 + getFontHeight(valueSize) / 4, paint);
        } else {

            int height = getFontHeight(valueSize) + getFontHeight(unitSize) + UNIT_OFFSET;
            int vHei = height / 2 - getFontHeight(unitSize) - UNIT_OFFSET;
            canvas.drawText(content, getWidth() / 2 - getFontWidth(content, valueSize) / 2,
                    (getHeight() - nameHeight) / 2 + (vHei > 0 ? vHei : 0), paint);

            paint.setTextSize(unitSize);
            paint.setTypeface(Typeface.DEFAULT);
            canvas.drawText(unit, getWidth() / 2 - getFontWidth(unit, unitSize) / 2, (getHeight()
                    - nameHeight) / 2 + height / 4 + (vHei > 0 ? vHei : -vHei), paint);
        }
    }

    /**
     * 写底部文字
     *
     * @param canvas
     */
    private void drawNameText(Canvas canvas) {
        Paint paint = new Paint();

        // 需要缩小字体
        while (getFontWidth(name, nameSize) > (getWidth() - 4 * MARGIN_OFFSET)) {
            nameSize--;
            // 缩小到最小字体后value值宽度仍然很大，则中间加小数点
            if (nameSize < unitSize) {
                nameSize = unitSize;
                while (getFontWidth(name, nameSize) > (getWidth() - 4 * MARGIN_OFFSET)) {
                    StringBuffer strb = new StringBuffer(name);
                    if (strb.indexOf(sb) != -1) {
                        strb.replace(strb.indexOf(sb) - 1, strb.indexOf(sb) + 1 + sb.length(), sb);
                    } else {
                        strb.replace(strb.length() / 2, strb.length() / 2 + 1, sb);
                    }
                    name = strb.toString();
                }
            }
        }
        nameHeight = getFontHeight(nameSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(nameSize);
        paint.setColor(outTextColor);

        canvas.drawText(name, getWidth() / 2 - getFontWidth(name, nameSize) / 2, getHeight() -
                MARGIN_OFFSET, paint);
    }

    public OnDesViewLongClickListerer getOnLongClick() {
        return onLongClick;
    }

    public void setOnLongClick(OnDesViewLongClickListerer onLongClick) {
        this.onLongClick = onLongClick;
    }

    public OnDesViewClickListener getOnClick() {
        return onClick;
    }

    public void setOnClick(OnDesViewClickListener onClick) {
        this.onClick = onClick;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return true;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
        if (null != anim)
            anim.setDuration(duration);
    }

    /**
     * 设置值、单位、名称等值
     *
     * @param value 值
     * @param unit  单位
     * @param name  名称
     */
    public void setContent(String value, String unit, String name) {
        if (null != this.value)
            mCurrentVlaue = this.value;
        this.value = value;
        this.unit = unit;
        this.name = name;
        this.mCount = value;
        if (radius > 0)
            invalidate();

        initAnimation();
    }

    /**
     * 点击事件
     */
    public interface OnDesViewLongClickListerer {
        public void onLongClick(ImageDescView view);
    }

    /**
     * 点击事件
     */
    public interface OnDesViewClickListener {
        public void onClick(ImageDescView view);
    }

    public int getValueSize() {
        return valueSize;
    }

    public void setValueSize(int valueSize) {
        this.valueSize = valueSize;

        if (radius > 0)
            invalidate();
    }

    public int getUnitSize() {
        return unitSize;
    }

    public void setUnitSize(int unitSize) {
        this.unitSize = unitSize;

        if (radius > 0)
            invalidate();
    }

    public int getNameSize() {
        return nameSize;
    }

    public void setNameSize(int nameSize) {
        this.nameSize = nameSize;

        if (radius > 0)
            invalidate();
    }

    public int getInTextColor() {
        return inTextColor;
    }

    public void setInTextColor(int inTextColor) {
        this.inTextColor = inTextColor;

        if (radius > 0)
            invalidate();
    }

    public int getOutTextColor() {
        return outTextColor;
    }

    public void setOutTextColor(int outTextColor) {
        this.outTextColor = outTextColor;

        if (radius > 0)
            invalidate();
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;

        if (radius > 0)
            invalidate();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isAnimation() {
        return isAnimation;
    }

    public void setIsAnimation(boolean isAnimation) {
        this.isAnimation = isAnimation;

        initAnimation();
    }

    private void initAnimation() {
        if (isAnimation && null != value) {
            init();
            this.startAnimation(anim);
            if (value.indexOf(".") != -1)
                this.isValueInt = false;
            else
                this.isValueInt = true;
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (null != this.value)
            mCurrentVlaue = this.value;
        this.value = value;
        this.mCount = value;
        if (radius > 0)
            invalidate();

        initAnimation();
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;

        if (radius > 0)
            invalidate();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

        if (radius > 0)
            invalidate();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (null != onLongClick)
            onLongClick.onLongClick(this);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (null != onClick)
            onClick.onClick(this);
        return false;
    }

    private String mCount;
    private String mCurrentVlaue = "0";

    public class BarAnimation extends Animation {
        /**
         * Initializes expand collapse animation, has two types, collapse (1)
         * and expand (0).
         */
        public BarAnimation() {
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);

            try {
                if (!isValueInt) {
                    if (interpolatedTime < 1.0f) {
                        BigDecimal b = new BigDecimal(Float.parseFloat(mCurrentVlaue) +
                                interpolatedTime * (Float.parseFloat(value) - Float.parseFloat
                                        (mCurrentVlaue)));
                        mCount = b.setScale(value.length() - value.indexOf(".") - 1, BigDecimal
                                .ROUND_HALF_UP).floatValue() + "";
                    } else {
                        mCount = Float.parseFloat(value) + "";
                    }
                } else {
                    if (interpolatedTime < 1.0f) {
                        mCount = (int) (Float.parseFloat(mCurrentVlaue) + interpolatedTime * 
                                (Float.parseFloat(value) - Float.parseFloat(mCurrentVlaue))) + "";
                    } else {
                        mCount = value;
                    }
                }
                postInvalidate();
            } catch (Exception e) {
                mCount = value;
                postInvalidate();
                anim.cancel();
            }
        }
    }
}
