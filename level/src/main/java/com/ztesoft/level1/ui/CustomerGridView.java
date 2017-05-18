package com.ztesoft.level1.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 文件名称 : CustomerGridView
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 解决与ScrollView嵌套冲突的GridView
 * <p>
 * 创建时间 : 2017/3/24 15:46
 * <p>
 */
public class CustomerGridView extends GridView {
    public CustomerGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomerGridView(Context context) {
        super(context);
    }

    public CustomerGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
