package com.ztesoft.level1.ui;

import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.Level1Util;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 进度条框，支持实际值大于目标值
 *
 * @author wanghx2
 */
public class PercentLayout extends LinearLayout {

    private String divisorValue;
    private String dividendValue;

    private int div_color = Color.BLUE;
    private int back_color = Color.LTGRAY;

    private float minLeft = 20;//防止进度过小而看不到，设置最小宽度
    private Context context;

    public PercentLayout(Context context) {
        super(context);
        this.context = context;
    }

    public void create() {
        FrameLayout l1 = new FrameLayout(context);
        TextView t1 = new TextView(context);//背景灰色条
        t1.setBackgroundDrawable(Level1Util.MAIN_RADIO_BORD(this.context, back_color, Color
                .TRANSPARENT, 22));
        t1.setGravity(Gravity.CENTER);
        t1.setTextColor(div_color);
        t1.setText(divisorValue + "/" + dividendValue);

        TextView t2 = new TextView(context);//进度蓝色条
        t2.setBackgroundDrawable(Level1Util.MAIN_RADIO_BORD(this.context, div_color, Color
                .TRANSPARENT, 22));
        LinearLayout dd1 = new LinearLayout(context);//透明字条
        dd1.addView(t2, Level1Bean.actualWidth / 2, LayoutParams.FILL_PARENT);
        dd1.setBackgroundDrawable(Level1Util.MAIN_RADIO_BORD(this.context, div_color, Color
                .TRANSPARENT, 22));
//		t2.setBackgroundColor(div_color);

        TextView t3 = new TextView(context);
        t3.setGravity(Gravity.CENTER);
        t3.setTextColor(Color.WHITE);
        t3.setText(divisorValue + "/" + dividendValue);
        LinearLayout dd = new LinearLayout(context);//透明字条
        dd.addView(t3, Level1Bean.actualWidth / 2, LayoutParams.FILL_PARENT);

        float value1 = Float.parseFloat(divisorValue);
        float value2 = Float.parseFloat(dividendValue);

        float width = Level1Bean.actualWidth / 2;
        if (value1 == 0f || value2 == 0f) {
            width = Level1Util.getDipSize(minLeft);
            l1.addView(t1, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            l1.addView(dd1, (int) width, LayoutParams.MATCH_PARENT);
            l1.addView(dd, (int) width, LayoutParams.MATCH_PARENT);
        } else if (value1 > value2) {
            width = (value2 * Level1Bean.actualWidth / 2) / value1;
            if (width < minLeft)
                width += minLeft;
            l1.addView(dd1, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            //临时灰色线
            LinearLayout l2 = new LinearLayout(context);
            l2.addView(new View(context), (int) width, LayoutParams.MATCH_PARENT);
            View v = new View(context);
            v.setBackgroundColor(back_color);
            l2.addView(v, 2, LayoutParams.MATCH_PARENT);
            l1.addView(l2, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            l1.addView(dd, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        } else {
            width = (value1 * Level1Bean.actualWidth / 2) / value2;
            l1.addView(t1, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            l1.addView(dd1, (int) width, LayoutParams.MATCH_PARENT);
            l1.addView(dd, (int) width, LayoutParams.MATCH_PARENT);
        }

        this.addView(l1, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }


    public String getDivisorValue() {
        return divisorValue;
    }

    /**
     * 设置实际值
     *
     * @param divisorValue
     */
    public void setDivisorValue(String divisorValue) {
        this.divisorValue = divisorValue;
    }

    /**
     * 设置目标值
     *
     * @return
     */
    public String getDividendValue() {
        return dividendValue;
    }

    public void setDividendValue(String dividendValue) {
        this.dividendValue = dividendValue;
    }

    /**
     * 设置完成颜色
     *
     * @param div_color
     */
    public void setDiv_color(int div_color) {
        this.div_color = div_color;
    }

    /**
     * 设置背景颜色
     *
     * @param back_color
     */
    public void setBack_color(int back_color) {
        this.back_color = back_color;
    }
}
