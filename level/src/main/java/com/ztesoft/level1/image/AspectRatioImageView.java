package com.ztesoft.level1.image;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 文件名称 : AspectRatioImageView
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 保持图片长宽比同时拉伸
 * <p>
 * 创建时间 : 2017/5/3 15:22
 * <p>
 */
public class AspectRatioImageView extends ImageView {

    public AspectRatioImageView(Context context) {
        super(context);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
        setMeasuredDimension(width, height);
    }
}  
