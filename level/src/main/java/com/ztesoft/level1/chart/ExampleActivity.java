package com.ztesoft.level1.chart;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.steema.teechart.SimpleChart;
import com.steema.teechart.SimpleChart.ChartClickListener;
import com.steema.teechart.drawing.Color;
import com.steema.teechart.events.ChartEvent;
import com.steema.teechart.events.ChartMotionListener;
import com.ztesoft.level1.Level1Util;

import org.json.JSONException;
import org.json.JSONObject;

/***
 * 面积图
 * @author wangsq
 *
 */
public class ExampleActivity {
    private Context activity;
    private JSONObject dataObj;// 图形数据源
    private LinearLayout chartLayout;// 图形布局
    private SimpleChart chart;
    private int titleTextSize = Level1Util.getSpSize(10);
    private String titleTextColor = "#000000";
    //设置整体背景色
    private String backgroundColor = "#ffffffff";
    //设置是否显示长按线
    private boolean showMarkLine = false;
    private int makrLineTextSize = 10;
    private String markLineTextColor = "#ffffff";
    private int showNum = 30;//一页显示的个数
    private int selectIndex = -1;// 选中的是第几个柱或点；
    private ChartClickListener clickListener;//点击事件
    int allUnit = 0;

    /***
     * 构造函数
     * @param activity
     * @param parentLayout
     */
    public ExampleActivity(Context activity, LinearLayout parentLayout) {
        this.activity = activity;
        this.chartLayout = new LinearLayout(activity) {
            GestureDetector mGestureDetector = new GestureDetector(
                    (OnGestureListener) new gesDetector());

            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return mGestureDetector.onTouchEvent(ev);
            }
        };
        this.chartLayout.setOrientation(LinearLayout.VERTICAL);
        parentLayout.addView(chartLayout);
    }

    //########提供给外部的方法必须加注释，说明方法作用，（入参和出参的含义，抛错说明）
    //所有控件原则上必须提供create方法
    public void create() throws JSONException {
        if (dataObj == null) {
            return;
        }
        chartLayout.removeAllViews();
        chart = new SimpleChart(activity);
        chart.setMakrLineTextSize(makrLineTextSize);
        chart.setMarkLineTextColor(markLineTextColor);
        chart.setShowMarkLine(showMarkLine);

        chartLayout.addView(chart);
        chartLayout.setBackgroundColor(Color.parseColor(backgroundColor));
    }

    //########
    class gesDetector extends SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (clickListener != null) {
                clickListener.onbgClick();
            }
            return true;
        }
    }

    //########

    /***
     * 滑动事件
     * @author wangsq
     *
     */
    class xxChartMotionListener implements ChartMotionListener {
        @Override
        public void scrolled(ChartEvent chartevent) {
            chart.getAxes().getBottom().setAutomaticMinimum(false);
            chart.getAxes().getBottom().setAutomaticMaximum(false);
            chart.setScrollToLeft(true);
            chart.setScrollToRight(true);
            if (chart.getAxes().getBottom().getMinimum() <= 0) {
                chart.setScrollToLeft(true);
                chart.setScrollToRight(false);
                chart.getAxes().getBottom().setMinimum(0);
                chart.getAxes().getBottom().setMaximum(showNum);
            }
            int serNum = allUnit - 1;
            if (chart.getAxes().getBottom().getMaximum() >= serNum) {
                chart.setScrollToLeft(false);
                chart.setScrollToRight(true);
                if (serNum >= showNum) {
                    chart.getAxes().getBottom().setMinimum(serNum - showNum);
                    chart.getAxes().getBottom().setMaximum(serNum);
                } else {
                    chart.getAxes().getBottom().setMaximum(showNum);
                    chart.getAxes().getBottom().setMinimum(0);
                }
            }
        }

        @Override
        public void unzoomed(ChartEvent chartevent) {
            chart.getAxes().getBottom().setMaximum(35);
        }

        @Override
        public void zoomed(ChartEvent chartevent) {
            chart.getAxes().getBottom().setMaximum(10);
        }

    }


    //########这种应该是内部方法，必须是private

    /**
     * 设置标题内容和字体
     */
    public void setTitle(SimpleChart chart, String t) {
        if (t == null || t.trim().length() == 0) {
            chart.getHeader().setVisible(false);
            return;
        }
        // 设置标题内容和字体
        chart.getHeader().setText(t);
        chart.getHeader().getFont().setSize(titleTextSize);
        chart.getHeader().getFont().setBold(true);
        chart.getHeader().getFont().setColor(Color.fromCode(titleTextColor));
    }

    //########下列所有get，set方法必须有注释，说明属性含义，（设置范围，生效条件）
    //例如：设置范围----“bar：柱图，line：线图”
    //例如：生效条件----“仅在柱图时生效”
    public void setJSON(JSONObject chartObj) {
        this.dataObj = chartObj;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setShowMarkLine(boolean showMarkLine) {
        this.showMarkLine = showMarkLine;
        if (selectIndex > -1) {
            this.showMarkLine = true;
        }
    }

    public void setMakrLineTextSize(int makrLineTextSize) {
        this.makrLineTextSize = makrLineTextSize;
    }

    public void setMarkLineTextColor(String markLineTextColor) {
        this.markLineTextColor = markLineTextColor;
    }

    public void setShowNum(int showNum) {
        this.showNum = showNum;
    }

    public void setSelectIndex(int selectIndex) {
        if (selectIndex > -1) {
            this.showMarkLine = true;
            this.selectIndex = selectIndex;
        }
    }

    public void setClickListener(ChartClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
