package com.ztesoft.level1.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 文件名称 : CustomerListView
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 解决与ScrollView嵌套冲突的ListView
 * <p>
 * 创建时间 : 2017/3/24 15:47
 * <p>
 */
public class CustomerListView extends ListView {
    public CustomerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomerListView(Context context) {
        super(context);
    }

    public CustomerListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
