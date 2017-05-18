package com.ztesoft.level1.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.ztesoft.level1.Level1Bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 跑马灯
 *
 * @author wangxin
 * @author wanghx2  update by 2013/4/28
 * @注意：跑马灯宽度建议设置为MATCH_PARENT，否则最好设置具体数值，或weight，不可设置为WRAP_CONTENT,否则会根据每段跑马灯的文本自动调整宽度
 * @注意：create方法不可以写在oncreate线程中！否则初次加载时，getWidth方法无效。
 */
public class AutoScrollTextView extends TextView implements OnClickListener {

    private Context context = null;
    private String[] content;  //需要播放的数据

    private OnScrollChangeListener mOnScrollChangeListener;


    private int numTag = 0;  //调用数据的下标
    private int textColor = Color.BLACK;  //字体颜色
    private float stepValue = 1.2f;  //文字滚动速度
    private boolean isVertical = false;  //true为垂直滚动， false为水平滚动

    private int maxTextLength = 5000;  //每条播放数据的最大长度

    public boolean scrollFlag = false;  //滚动状态
    private float step = 0f;  //文字的横坐标
    private float temp_view_plus_text_length = 0.0f;  //用于计算的临时变量
    private float temp_view_plus_two_text_length = 0.0f;  //用于计算的临时变量
    private Paint paint = new Paint();  //绘图样式
    private List<String> textList = new ArrayList<String>();  //分行保存textview的显示信息(当isVertical为true时)

    /**
     * 跑马灯
     *
     * @param context
     * @param listener
     * @注意：跑马灯宽度不可设置为WRAP_CONTENT
     */
    public AutoScrollTextView(Context context, OnScrollChangeListener listener) {
        super(context);
        this.context = context;
        this.mOnScrollChangeListener = listener;
        setOnClickListener(this);
    }

    /**
     * 初始化方法，必须在onCreate线程外调用，否则第一次显示效果有问题
     *
     * @param content 跑马灯内容
     * @param numTag  设置当前显示的数据下标
     */
    public void create(String[] content, int numTag) {
        this.content = content;
        numTag = Math.max(0, Math.min(numTag, content.length - 1));
        this.numTag = numTag;
        init();
        startScroll();
    }

    public void create(String content, int numTag) {
        create(new String[]{content}, numTag);
    }

    private void init() {
        this.setHorizontallyScrolling(true);
        String text = content[numTag];
        if (text.length() > maxTextLength) {  //不支持超过maxTextLength的字符
            text = text.substring(0, maxTextLength);
        }

        paint = getPaint();
        paint.setColor(textColor);
        setText(text);
        float viewWidth = getWidth();
        if (viewWidth == 0) {
            viewWidth = Level1Bean.actualWidth;
        }
        if (!isVertical) {
            float textLength = paint.measureText(text);
            step = textLength;
            temp_view_plus_text_length = viewWidth + textLength;
            temp_view_plus_two_text_length = viewWidth + textLength * 2;
        } else {
            int wid = (int) viewWidth;
            float width = MeasureSpec.getSize(wid);

            float length = 0;
            if (TextUtils.isEmpty(text)) {
                return;
            }

            // 下面的代码是根据宽度和字体大小，来计算textview显示的行数。
            textList.clear();
            step = 0;
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                if (length < width) {  //长度小于宽度则在同一行
                    builder.append(text.charAt(i));
                    length += paint.measureText(text.substring(i, i + 1));
                    if (i == text.length() - 1) {  //保证最后一行正确添加进去
                        textList.add(builder.toString());
                    }
                } else {  //否则新建一行
                    textList.add(builder.toString().substring(0, builder.toString().length() - 1));
                    builder.delete(0, builder.length() - 1);
                    length = paint.measureText(text.substring(i, i + 1));
                    i--;
                }
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        paint = getPaint();  //此处最好再显式的取出paint对象,以防空值
        if (!isVertical) {
            float y = getTextSize() + getPaddingTop();
            canvas.drawText(content[numTag], temp_view_plus_text_length - step, y, paint);
            if (!scrollFlag) {  //如果没有滚动，则不需要重绘，直接返回
                return;
            }
            step += stepValue;

            if (step > temp_view_plus_two_text_length) {  //超过预设范围，则换下一条
                numTag++;
                if (numTag == content.length)
                    numTag = 0;
                if (null != mOnScrollChangeListener)
                    mOnScrollChangeListener.onScrollChange(numTag);
                init();
            }
            invalidate();
        } else {
            for (int i = 0; i < textList.size(); i++) {  //循环画每一行文本
                canvas.drawText(textList.get(i), 0, this.getHeight() + (i + 1) * paint
                        .getTextSize() - step, getPaint());
            }
            if (!scrollFlag) {  //如果没有滚动，则不需要重绘，直接返回
                return;
            }

            step += stepValue;  //0.5为文字滚动速度。
            if (step >= this.getHeight() + textList.size() * paint.getTextSize()) {  //超过预设范围，则换下一条
                numTag++;
                if (numTag == content.length)
                    numTag = 0;
                if (null != mOnScrollChangeListener)
                    mOnScrollChangeListener.onScrollChange(numTag);
                init();
            }
            invalidate();
        }
    }

    @Override
    public void onClick(View v) {
        if (scrollFlag)
            stopScroll();
        else
            startScroll();
    }

    @Override
    public void setTextColor(int color) {
        textColor = color;
    }

    public void startScroll() {
        scrollFlag = true;
        invalidate();
    }

    public void stopScroll() {
        scrollFlag = false;
        invalidate();
    }

    /**
     * 设置文字滚动速度
     *
     * @param stepValue
     */
    public void setStepValue(float stepValue) {
        this.stepValue = stepValue;
    }

    /**
     * 获取当前显示的数据下标
     *
     * @return
     */
    public int getNumTag() {
        return numTag;
    }

    /**
     * 获取滚动状态
     */
    public boolean getScrollFlag() {
        return scrollFlag;
    }

    /**
     * 获取滚动方向
     *
     * @return
     */
    public boolean isVertical() {
        return isVertical;
    }

    /**
     * 设置滚动方向
     * true为垂直滚动， false为水平滚动
     *
     * @param isVertical
     */
    public void setVertical(boolean isVertical) {
        this.isVertical = isVertical;
    }

    /**
     * 设置支持的最大字符长度
     *
     * @param maxTextLength 最大字符长度
     */
    public void setMaxTextLength(int maxTextLength) {
        this.maxTextLength = maxTextLength;
    }

    public interface OnScrollChangeListener {

        /**
         * 跑马灯下标变化时触发方法
         *
         * @param numTag
         */
        void onScrollChange(int numTag);
    }
}
