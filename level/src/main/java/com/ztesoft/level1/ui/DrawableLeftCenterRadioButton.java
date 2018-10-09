package com.ztesoft.level1.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

/**
 * 文件名称 : DrawableLeftCenterRadioButton
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 设置DrawableLeft时，文字和图片同时居中
 * <p>
 * 创建时间 : 2017/9/26 15:49
 * <p>
 */
public class DrawableLeftCenterRadioButton extends AppCompatRadioButton {

    public DrawableLeftCenterRadioButton(Context context) {
        super(context);
    }

    public DrawableLeftCenterRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableLeftCenterRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        Drawable drawableLeft = drawables[0];
        if (drawableLeft != null) {
            float textWidth = getPaint().measureText(getText().toString());
            int drawablePadding = getCompoundDrawablePadding();
            int drawableWidth = drawableLeft.getIntrinsicWidth();
            float bodyWidth = textWidth + drawableWidth + drawablePadding;
            canvas.translate((getWidth() - bodyWidth) / 2, 0);
        }
        super.onDraw(canvas);
    }
}
