package com.ztesoft.level1.text;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.ztesoft.level1.Level1Util;
import com.ztesoft.level1.R;

import java.util.ArrayList;

/**
 * 文件名称 : SpecialTextView
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 自定义文本标签，自动换行
 * <p>
 * 创建时间 : 2017/5/2 10:21
 * <p>
 */
public class SpecialTextView extends TextView {

    private int textColor = Color.parseColor("#c0c0c0");
    private float textSize = Level1Util.getSpSize(15);
    private String text = "";
    private int minNum = 2;
    private int lineNum = 0;
    private boolean isOpen = false;

    private int width = 0;
    private Paint mPaint = new Paint();
    private int smallHeight = 0;
    private int nomalHeight = 0;
    private Region rg = null;// 展开收缩区域
    private ArrayList<String> texts = new ArrayList<String>();

    public SpecialTextView(Context context) {
        this(context, null);
    }

    public SpecialTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpecialTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 最后调用
     *
     * @param minNum 收缩后显示的行数，必须>=1
     */
    public void create(int minNum) {
        mPaint.setAntiAlias(true);
        mPaint.setColor(textColor);
        mPaint.setStyle(Style.FILL);
        mPaint.setTextSize(textSize);
        this.minNum = minNum;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        setHeightFun(text, mPaint, widthSize);
        this.width = widthSize;
        if (!isOpen) {
            this.setMeasuredDimension(widthSize, smallHeight);
        } else {
            this.setMeasuredDimension(widthSize, nomalHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        FontMetrics fm = mPaint.getFontMetrics();
        float baseline = fm.descent - fm.ascent;
        float x = 0;
        float y = baseline; // 由于系统基于字体的底部来绘制文本，所有需要加上字体的高度。
        if (this.width == 0)
            return;

        String text;
        for (int i = 0; i < texts.size(); i++) {
            text = texts.get(i);
            if (text != null) {
                if (!isOpen) {
                    if (texts.size() > minNum) {
                        if (i == (minNum - 1)) {
                            if (text.length() >= 3) {
                                text = text.substring(0, text.length() - 3) + "...";
                            } else {
                                text = text + "...";
                            }
                            canvas.drawText(text, x, y, mPaint); // 坐标以控件左上角为原点
                        } else if (i < minNum) {
                            canvas.drawText(text, x, y, mPaint); // 坐标以控件左上角为原点
                        }
                        if (i > minNum - 1)
                            break;
                    } else {
                        canvas.drawText(text, x, y, mPaint); // 坐标以控件左上角为原点
                    }
                } else {
                    canvas.drawText(text, x, y, mPaint); // 坐标以控件左上角为原点
                }
                y += baseline + fm.leading; // 添加字体行间距
            }
        }
        lineNum = texts.size();

        float textWidth = mPaint.measureText(">>" + getContext().getString(R.string.system_show))
                * 1.5f;
        if (lineNum > minNum) {
            if (!isOpen) {
                mPaint.setColor(Color.GREEN);
                canvas.drawText(">>" + getContext().getString(R.string.system_show), this.width -
                        textWidth, y, mPaint); // 坐标以控件左上角为原点
            } else {
                mPaint.setColor(Color.BLUE);
                canvas.drawText("<<" + getContext().getString(R.string.system_hide), this.width -
                        textWidth, y, mPaint); // 坐标以控件左上角为原点
            }

            // 画缩放按钮点击区域
            Path path = new Path();
            path.moveTo((int) (this.width - textWidth), (int) y);
            path.lineTo(this.width, (int) y);
            path.lineTo(this.width, (int) (y - baseline));
            path.lineTo((int) (this.width - textWidth), (int) (y - baseline));
            RectF r = new RectF();
            path.computeBounds(r, true);
            rg = new Region();
            rg.setPath(path, new Region((int) r.left, (int) r.top, (int) r.right, (int) r.bottom));
        }

        mPaint.setColor(textColor);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            if (rg != null && rg.contains((int) x, (int) y)) {
                if (!isOpen) {
                    this.setIsOpen(true);
                } else {
                    this.setIsOpen(false);
                }
                setHeightFun(text, mPaint, this.width);
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 自动分割文本
     *
     * @param content 需要分割的文本
     * @param p       画笔，用来根据字体测量文本的宽度
     * @param width   指定的宽度
     * @return 一个字符串数组，保存每行的文本
     */
    private ArrayList<String> autoSplit(String content, Paint p, float width) {
        width = width - 15;
        ArrayList<String> lineTexts = new ArrayList<String>();
        int length = content.length();
        float textWidth = p.measureText(content);
        if (textWidth <= width) {
            String tempAree[] = content.split("\\n");
            for (int i = 0; i < tempAree.length; i++) {
                lineTexts.add(tempAree[i]);
            }
            // lineTexts.add(content);
            return lineTexts;
        }
        int start = 0, end = 1;
        String tempStr;
        int strLen;
        while (start < length) {
            if (p.measureText(content, start, end) > width) { // 文本宽度超出控件宽度时
                end = end - 1;
                tempStr = content.substring(start, end);
                if (tempStr.indexOf('\n') != -1) {
                    strLen = tempStr.indexOf('\n');
                    lineTexts.add(content.substring(start, start + strLen));
                    start = end = start + strLen + 1;
                } else {
                    lineTexts.add(content.substring(start, end));
                    start = end;
                }
            }
            if (end == length) { // 不足一行的文本
                tempStr = content.substring(start, end);
                String tempAree[] = tempStr.split("\\n");
                for (int i = 0; i < tempAree.length; i++) {
                    lineTexts.add(tempAree[i]);
                }
                // lineTexts.add((String) content.substring(start, end));
                break;
            }
            end += 1;
        }
        return lineTexts;
    }

    private void setHeightFun(String text, Paint mPaint, int width) {
        this.texts = autoSplit(text, mPaint, width);
        FontMetrics fm = mPaint.getFontMetrics();

        float baseline = fm.descent - fm.ascent;
        float y = baseline; // 由于系统基于字体的底部来绘制文本，所有需要加上字体的高度。
        y += (baseline + fm.leading) * (texts.size() + 1); // 添加字体行间距
        if (texts.size() <= minNum) {
            y = baseline;
            y += (baseline + fm.leading) * (texts.size()); // 添加字体行间距
            nomalHeight = smallHeight = (int) (y - baseline + 10);
        } else {
            smallHeight = (int) ((baseline + fm.leading) * (minNum + 1) + 5);
            nomalHeight = (int) (y - baseline) + 10;
        }
        if (!isOpen) {
            setHeight(smallHeight);
        } else {
            setHeight(nomalHeight);
        }
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = Level1Util.getSpSize(textSize);
    }

    public boolean getIsOpen() {
        return isOpen;
    }

    /**
     * 设置是否展现，默认false不展开
     *
     * @param flag
     */
    public void setIsOpen(boolean flag) {
        this.isOpen = flag;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getLineNum() {
        return lineNum;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}