package com.ztesoft.level1.chart;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.steema.teechart.ScrollMode;
import com.steema.teechart.SimpleChart;
import com.steema.teechart.SimpleChart.ChartClickListener;
import com.steema.teechart.SimpleChart.LongCallBackEvent;
import com.steema.teechart.axis.AxisTitle;
import com.steema.teechart.drawing.Color;
import com.steema.teechart.drawing.Gradient;
import com.steema.teechart.drawing.GradientDirection;
import com.steema.teechart.events.ChartEvent;
import com.steema.teechart.events.ChartMotionListener;
import com.steema.teechart.events.FrameworkMouseEvent;
import com.steema.teechart.events.SeriesMouseAdapter;
import com.steema.teechart.events.SeriesMouseEvent;
import com.steema.teechart.legend.LegendAlignment;
import com.steema.teechart.legend.LegendTextStyle;
import com.steema.teechart.styles.Area;
import com.steema.teechart.styles.Line;
import com.steema.teechart.styles.MarksStyle;
import com.steema.teechart.styles.PointerStyle;
import com.ztesoft.level1.Level1Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/***
 * 面积图
 * headArray: [{type:x,value:v1},{name:移动,type:line,value:v2,top:v7}....],
 * valueArray:[{v1:,v2:,v3},{}.....]
 *
 * @author wangsq
 */
public class AreaChart {
    private Context context;
    private JSONObject dataObj;// 图形数据源
    private LinearLayout chartLayout;// 图形布局
    private SimpleChart chart;
    //设置x,y轴显示隐藏
    private boolean xAxisShow = true;
    private boolean yAxisShow = true;
    //设置轴文字大小、颜色
    private int axisTextSize = Level1Util.getSpSize(10);
    private String axisTextColor = "#000000";
    //设置x，y1,y2坐标轴样式
    private String xAxisFormat = null;
    private String lAxisFormat = null;
    //设置X轴倾斜、交替
    private int xAxisAngle = 0;
    private int xAxisSeparator = 1;
    //设置x，y1,y2坐标轴描述文字
    private String xAxisDesc = null;
    private String lAxisDesc = null;
    //设置坐标轴描述文字大小、颜色
    private int axisDescSize = 12;
    private String axisDescColor = "#000000";
    //设置图形自适应
    private boolean adaptive = true;
    //设置图例位置
    private String legendPosition = "bottom";
    //设置图例的文字颜色、字号
    private int legendTextSize = Level1Util.getSpSize(10);
    private String legendTextColor = "#000000";
    //设置柱图颜色组
    private String[] areaColors = com.ztesoft.level1.chart.ColorDefault.colors;
    //是否在顶部显示数据
    private boolean showTop = false;
    //设置顶部数据大小、颜色
    private int topDataTextSize = 10;
    private String topDataTextColor = "#000000";
    //设置标题位置
    private String titlePosition = "none";
    //设置标题内容、颜色、大小
    private String title = null;
    private int titleTextSize = Level1Util.getSpSize(10);
    private String titleTextColor = "#000000";
    //是否显示背景网格
    private String showWall = "none";
    //设置整体背景色
    private String backgroundColor = "#ffffffff";
    //设置是否显示长按线
    private boolean showMarkLine = false;
    private String markLineColor = "#000000";
    private int makrLineTextSize = 10;
    private String markLineTextColor = "#ffffff";
    private String markLineBackgroundColor = "#000000";
    //设置纵坐标刻度数
    private int lAxisScaleNum = 4;
    //设置是否从0开始
    private boolean yZeroInclude = false;
    private int showNum = 30;//一页显示的个数
    // 坐标系面板
    private String[] backWallColor = null;
    private boolean muchSingleShow = false;// 是否支持双指
    private int selectIndex = -1;// 选中的是第几个柱或点；
    private LongCallBackEvent longClickListener;// 长按滑动接口
    private ChartClickListener clickListener;//点击事件
    private String maskMethod = null;//外面画遮罩的回调函数名字
    private String lineColor = null;//线的颜色
    private String showPoint = "all";//none:不显示 all:显示所有 se:开始结束
    private String pointColor = "#ffffff";
    private com.steema.teechart.drawing.Gradient gradient = null;//面积渐变
    private int allUnit = 0;
    private boolean smooth = true;//是否光滑
    private int pointWidth = 2;//圈圈的大小
    private String pointStyle = "circle";//circle:实心圆,hcircle:空心圆,rectangle:矩形triangle:三角形
    private String avgValue;//均线
    private int seriesTran = 30; //渐变区域透明度
    private boolean isGradientLineSame = true;

    /***
     * 构造函数
     *
     * @param context 上下文
     * @param parentLayout 父视图
     */
    public AreaChart(Context context, LinearLayout parentLayout) {
        this.context = context;
        this.chartLayout = new LinearLayout(context) {
            GestureDetector mGestureDetector = new GestureDetector((OnGestureListener) new 
                    gesDetector());

            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return mGestureDetector.onTouchEvent(ev);
            }
        };
        this.chartLayout.setOrientation(LinearLayout.VERTICAL);
        parentLayout.addView(chartLayout);
    }

    /**
     * 设置数据
     *
     * @param chartObj 数据源
     */
    public void setJSON(JSONObject chartObj) {
        this.dataObj = chartObj;
    }

    /**
     * 添加线柱图
     *
     * @param propArray 多柱属性组
     * @param xCode     x轴取值code
     * @throws JSONException 数据异常
     */
    private void addSeriesChart(JSONArray propArray, String xCode) throws JSONException {
        JSONObject tempObj;
        double minValue = Double.MIN_VALUE;
        double maxValue = Double.MIN_VALUE;
        JSONArray dataArray = dataObj.getJSONArray("valueArray");
        if (propArray.length() > 0) {
            Area series;
            for (int i = 0; i < propArray.length(); i++) {
                tempObj = propArray.getJSONObject(i);
                series = new com.steema.teechart.styles.Area(chart.getChart());
                series.getAreaLines().setVisible(false);
                if (lineColor == null) {
                    lineColor = areaColors[i % areaColors.length];
                }
                lineColor = areaColors[i % areaColors.length];
                series.getLinePen().setColor(Color.fromCode(lineColor));
                series.getLinePen().setWidth(Level1Util.dip2px(context, 1));
                if (gradient != null) {
                    if (isGradientLineSame) {
                        series.setColor(Color.fromCode(areaColors[i % areaColors.length]));//线颜色  
                    }
//	        		series.getGradient().setStartColor(gradient.getStartColor());
                    series.getGradient().setStartColor(Color.fromArgb(Color.parseColor(lineColor)));
                    series.getGradient().setEndColor(gradient.getEndColor());
                    series.getGradient().setDirection(gradient.getDirection());
                    series.getGradient().setVisible(true);
                } else {
                    series.setColor(Color.fromCode(areaColors[i % areaColors.length]));//线颜色
                }
                series.setTransparency(seriesTran);//图层透明
                if ("all".equalsIgnoreCase(showPoint)) {
                    series.getPointer().setVisible(true);
                    series.getPointer().setStyle(PointerStyle.CIRCLE);
                    series.getPointer().getPen().setWidth(Level1Util.dip2px(context, 1));//线粗细
                    series.getPointer().getPen().setVisible(false);
                    series.getPointer().getPen().setColor(Color.fromCode(lineColor));
                    series.getPointer().setColor(Color.fromCode(lineColor));
                    if ("circle".equals(this.pointStyle)) {
                        series.getPointer().setStyle(PointerStyle.CIRCLE);
                    } else if ("hcircle".equals(this.pointStyle)) {
                        series.getPointer().setStyle(PointerStyle.CIRCLE);
                        series.getPointer().setColor(Color.fromCode(pointColor));
                        series.getPointer().getPen().setVisible(true);
                    } else if ("rectangle".equals(this.pointStyle)) {
                        series.getPointer().setStyle(PointerStyle.RECTANGLE);
                    } else if ("triangle".equals(this.pointStyle)) {
                        series.getPointer().setStyle(PointerStyle.TRIANGLE);
                    }

                    series.getPointer().setHorizSize(Level1Util.dip2px(context, pointWidth));//圆点半径
                    series.getPointer().setVertSize(Level1Util.dip2px(context, pointWidth));//圆点半径

                } else {
                    series.getPointer().setVisible(false);
                }
                //添加点
                if (dataArray.length() > 0) {
                    String title = tempObj.optString("name", null);
                    String value = tempObj.optString("value", null);
                    if (value == null) {
                        continue;
                    }
                    if (title != null) {
                        series.setTitle(title);
                    }
                    //看看是否是百分比
                    boolean divi100 = false;
                    if (lAxisFormat != null && lAxisFormat.contains("%")) {
                        divi100 = true;
                        series.setValueFormat(lAxisFormat);
                    }
                    series.setCustomVertAxis(chart.getAxes().getLeft());
                    boolean[] visibilities = new boolean[dataArray.length()];

                    double v;
                    for (int j = 0; j < dataArray.length(); j++) {
                        v = 0;

                        visibilities[j] = (j % xAxisSeparator == 0);
                        if (dataArray.getJSONObject(j).getString(value).trim().length() > 0) {
                            v = dataArray.getJSONObject(j).getDouble(value);
                        }
                        if (divi100) {
                            v = v / 100;
                        }
                        if (minValue == Double.MIN_VALUE && maxValue == Double.MIN_VALUE) {
                            minValue = maxValue = v;
                        }
                        if (v < minValue) {
                            minValue = v;
                        }
                        if (v > maxValue) {
                            maxValue = v;
                        }
                        series.add(v, getxAxes(dataArray.getJSONObject(j).getString(xCode)));
                    }
                    if (xAxisSeparator > 1) {
                        chart.getAxes().getBottom().getLabels().setVisibilities(visibilities);
                    }
                }
                //看是否要显示top数据---------本数据占整体数据的百分比
                if (showTop) {
                    series.getMarks().setVisible(true);
//					series.getMarks().setArrowLength(0);//连线的长度
                    series.getMarks().getArrow().setColor(Color.fromCode(axisTextColor));
                    series.getMarks().setStyle(MarksStyle.VALUE);
                    series.getMarks().getFont().setSize(topDataTextSize);
                    series.getMarks().getFont().setColor(Color.fromCode(topDataTextColor));
                    series.getMarks().getBrush().setDefaultVisible(false);
                    series.getMarks().getPen().setVisible(false);
                } else {
                    series.getMarks().setVisible(false);
                }
                if (clickListener != null) {
                    series.addSeriesMouseListener(new SeriesMouseAdapter() {
                        public void seriesClicked(SeriesMouseEvent e) {
                            clickListener.seriesClicked(e.getValueIndex());
                        }
                    });
                }
                series.setSmoothed2D(smooth);
                chart.addSeries(series);
            }

            com.ztesoft.level1.chart.ChartIncrement increm = new com.ztesoft.level1.chart
                    .ChartIncrement();
            if (minValue != maxValue) {
                if (yZeroInclude && minValue > 0) minValue = 0;
                increm.setNumDivLines(lAxisScaleNum);
                increm.setAxesValue(chart.getAxes().getLeft(), minValue, maxValue, lAxisFormat);
            }
        }
    }

    private class gesDetector extends SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (clickListener != null) {
                clickListener.onbgClick();
            }
            return true;
        }
    }

    /***
     * 获取x轴格式化后的值
     *
     * @param dimName 格式化前的值
     * @return 格式化后的值
     */
    private String getxAxes(String dimName) {
        if (xAxisFormat != null && !"".equals(xAxisFormat)) {
            try {
                dimName = dimName.replaceAll("-", "");
                if ("MM-dd".equals(xAxisFormat)) {
                    if (dimName.length() >= 8) {
                        dimName = dimName.substring(4, 6) + "-" + dimName.substring(6, 8);
                    } else if (dimName.length() >= 4) {
                        dimName = dimName.substring(0, 2) + "-" + dimName.substring(2, 4);
                    }
                } else if ("HH:mm".equals(xAxisFormat)) {
                    dimName = dimName.substring(8, 10) + ":" + dimName.substring(10, 12);
                } else if ("yyyy-MM".equals(xAxisFormat)) {
                    dimName = dimName.substring(0, 4) + "-" + dimName.substring(4, 6);
                } else if ("yyyy/MM".equals(xAxisFormat)) {
                    dimName = dimName.substring(0, 4) + "/" + dimName.substring(4, 6);
                } else if ("MM".equals(xAxisFormat.toUpperCase())) {
                    if (dimName.length() >= 6) {
                        dimName = dimName.substring(4, 6);
                    }
                } else if ("yy-MM".equals(xAxisFormat)) {
                    if (dimName.length() > 4) {
                        dimName = dimName.substring(2, 4) + "-" + dimName.substring(4, 6);
                    } else {
                        dimName = dimName.substring(0, 2) + "-" + dimName.substring(2, 4);
                    }
                } else if ("yyMM".equals(xAxisFormat)) {
                    if (dimName.length() > 4) {
                        dimName = dimName.substring(2, 4) + dimName.substring(4, 6);
                    } else {
                        dimName = dimName.substring(0, 2) + dimName.substring(2, 4);
                    }
                }
            } catch (Exception e) {//如报错，则使用实际值
                e.printStackTrace();
            }
        }
        return dimName;
    }

    public void create() throws JSONException {
        if (dataObj == null) {
            return;
        }
        chartLayout.removeAllViews();
//		if(!adaptive){
//			showPoint="all";
//		}
        chart = new SimpleChart(context);
        chart.setMakrLineTextSize(makrLineTextSize);
        chart.setMarkLineBackgroundColor(markLineBackgroundColor);
        chart.setMarkLineColor(markLineColor);
        chart.setMarkLineTextColor(markLineTextColor);
        chart.setShowMarkLine(showMarkLine);
        chart.setLongCallBackEvent(longClickListener);
        chart.setSelectIndex(selectIndex);
        chart.setMuchSingleShow(muchSingleShow);
        chart.setMaskMethod(maskMethod);
        JSONArray valueArray = dataObj.getJSONArray("valueArray");
        allUnit = valueArray.length();
        JSONArray propArray = dataObj.getJSONArray("headArray");
        String xCode = "";
        JSONObject tempObj;
        JSONArray areaArray = new JSONArray();
        for (int i = 0; i < propArray.length(); i++) {
            tempObj = propArray.getJSONObject(i);
            if ("x".equalsIgnoreCase(tempObj.optString("type", ""))) {
                xCode = tempObj.optString("value", "");
            } else {
                areaArray.put(tempObj);
            }
        }
        // 解决单图例显示问题
        if (areaArray.length() == 1) {
            this.legendPosition = "none";
        }

        // 设置底部X坐标轴
        setAxisX(chart);
        // 设置左侧Y坐标轴
        setAxisLeftY(chart);
        // 设置右侧Y坐标轴
        setAxisRightY(chart);
        // 设置坐标系背景
        setWall(chart);
        // 设置标题
        setTitle(chart, title);
        // 设置每个柱属性
        setShapeNode(chart);
        // 设置背景面板
        setBackPanel(chart);
        // 设置图例
        setLegend(chart);
        chart.getZoom().setAllow(false);
        //柱图优先依附左y轴
        addSeriesChart(areaArray, xCode);

        if (avgValue != null && avgValue.trim().length() > 0) {
            JSONArray dataArray = dataObj.getJSONArray("valueArray");
            Line series = new com.steema.teechart.styles.Line(chart.getChart());
            series.setTitle("均线");
            series.getLinePen().setWidth(Level1Util.dip2px(context, 1));
            for (int i = 0; i < dataArray.length(); i++) {
                series.add(Double.parseDouble(avgValue));
            }
            chart.addSeries(series);
        }
        //判断是否要添加滑动事件
        int serNum = valueArray.length() - 1;//所有节点
        if (!adaptive) {
            if (serNum > showNum) {
                chart.getAxes().getBottom().setMaximum(serNum);
                chart.getAxes().getBottom().setMinimum(serNum - showNum);
                chart.getPanning().setMouseButton(FrameworkMouseEvent.BUTTON1);
                chart.addChartMotionListener(new xxChartMotionListener());
            } else {//小于一屏显示的个数自动为自适应
                chart.getAxes().getBottom().setMaximum(serNum);
                chart.getAxes().getBottom().setMinimum(0);
                chart.setScrollToLeft(false);
                chart.setScrollToRight(false);
            }
            chart.getAxes().getBottom().setAutomatic(false);
            chart.getPanning().setAllow(ScrollMode.HORIZONTAL);
        } else {
            chart.getAxes().getBottom().setAutomatic(false);
            chart.getAxes().getBottom().setMaximum(serNum);
            chart.getAxes().getBottom().setMinimum(0);
            chart.setScrollToLeft(false);
            chart.setScrollToRight(false);
        }

        LinearLayout AxisDesclay = new LinearLayout(context);
        if (lAxisDesc != null) {
            AxisDesclay.setPadding(5, 5, 5, 5);
            TextView lView = new TextView(context);
            lView.setText(lAxisDesc);
            lView.setTextColor(Color.parseColor(axisDescColor));
            lView.setTextSize(axisDescSize);
            AxisDesclay.addView(lView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                    .FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

//			lView = new TextView(activity);
//			lView.setText(rAxisDesc);
//			lView.setTextColor(Color.parseColor(axisDescColor));
//			lView.setTextSize(axisDescSize);
//			AxisDesclay.addView(lView,LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout
// .LayoutParams.WRAP_CONTENT);
//			
            chartLayout.addView(AxisDesclay, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout
                    .LayoutParams.WRAP_CONTENT);
        }

        chartLayout.addView(chart);
        chartLayout.setBackgroundColor(Color.parseColor(backgroundColor));
    }

    /***
     * 滑动事件
     *
     * @author wangsq
     */
    private class xxChartMotionListener implements ChartMotionListener {
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

    /**
     * 设置底部X坐标轴
     */
    private void setAxisX(SimpleChart chart) {
        if (xAxisShow) {
            // 设置底部X坐标轴：
            chart.getAxes().getBottom().getAxisPen().setColor(Color.fromCode(axisTextColor));
            // 设置底部X坐标轴的坐标值是否显示
            chart.getAxes().getBottom().setLabelsOnAxis(false);
            // 设置底部X坐标轴的标签信息
            chart.getAxes().getBottom().getLabels().getFont().setSize(axisTextSize);
            chart.getAxes().getBottom().getLabels().getFont().setColor(Color.fromCode
                    (axisTextColor));
            // 设置底部X坐标轴的标签信息，旋转角度
            chart.getAxes().getBottom().getLabels().setAngle(xAxisAngle);
            // 设置底部X坐标轴向上的竖网格线情况
            if ("X".equalsIgnoreCase(showWall) || "XY".equalsIgnoreCase(showWall)) {
                chart.getAxes().getBottom().getGrid().setColor(Color.fromCode(axisTextColor));
            } else {
                chart.getAxes().getBottom().getGrid().setVisible(false);
            }
            chart.getAxes().getBottom().getLabels().setSeparation(0);
            if (!showPoint.equalsIgnoreCase("none")) {
                chart.getAxes().getBottom().setMaximumOffset(Level1Util.dip2px(context,
                        pointWidth));
                chart.getAxes().getBottom().setMinimumOffset(Level1Util.dip2px(context,
                        pointWidth));
            }
//			chart.getAxes().getBottom().setMaximumOffset(10);
//			chart.getAxes().getBottom().setMinimumOffset(5);
//			刻度的显示控制
//			chart.getAxes().getBottom().getMinorTicks().setVisible(showTicks);
//			chart.getAxes().getBottom().getTicks().setVisible(showTicks);
        } else {
            chart.getAxes().getBottom().setMaximumOffset(10);
            chart.getAxes().getBottom().setMinimumOffset(5);
            chart.getAxes().getBottom().setVisible(false);
        }

        if (xAxisDesc != null && xAxisDesc.trim().length() > 0) {
            AxisTitle t = new AxisTitle(chart.getChart());
            t.setText(xAxisDesc);
            t.getFont().setSize(Level1Util.getSpSize(axisDescSize));
            t.getFont().setColor(Color.fromCode(axisDescColor));
            chart.getAxes().getBottom().setTitle(t);
        }
    }

    /**
     * 设置左侧Y坐标轴
     */
    private void setAxisLeftY(SimpleChart chart) {
        if (yAxisShow) {
            // 设置左侧Y坐标轴：
            chart.getAxes().getLeft().getAxisPen().setColor(Color.fromCode(axisTextColor));
            chart.getAxes().getLeft().getLabels().getFont().setSize(axisTextSize);
            chart.getAxes().getLeft().getLabels().getFont().setColor(Color.fromCode(axisTextColor));
            chart.getAxes().getLeft().getLabels().setNumberScale(true);//K,M格式化

            // 设置底部X坐标轴向上的竖网格线情况
            if ("Y".equalsIgnoreCase(showWall) || "XY".equalsIgnoreCase(showWall)) {
                chart.getAxes().getLeft().getGrid().setColor(Color.fromCode(axisTextColor));
            } else {
                chart.getAxes().getLeft().getGrid().setVisible(false);
            }
            if (this.lAxisFormat != null && this.lAxisFormat.trim().length() > 0) {
                chart.getAxes().getLeft().getLabels().setValueFormat(this.lAxisFormat);
            }
            chart.getAxes().getLeft().setMaximumOffset(15);
            chart.getAxes().getLeft().setMinimumOffset(10);
//			chart.getAxes().getLeft().getMinorTicks().setVisible(showTicks);
//			chart.getAxes().getLeft().getTicks().setVisible(showTicks);
        } else {
            chart.getAxes().getLeft().setMaximumOffset(15);
            chart.getAxes().getLeft().setMinimumOffset(10);
            chart.getAxes().getLeft().setVisible(false);
        }
    }

    /**
     * 设置右侧Y坐标轴
     */
    private void setAxisRightY(SimpleChart chart) {
        if (yAxisShow) {
            // 设置左侧Y坐标轴：
            chart.getAxes().getRight().getAxisPen().setColor(Color.fromCode(axisTextColor));
            chart.getAxes().getRight().getLabels().getFont().setSize(axisTextSize);
            chart.getAxes().getRight().getLabels().getFont().setColor(Color.fromCode
                    (axisTextColor));
            chart.getAxes().getRight().getLabels().setNumberScale(true);//K,M格式化
            // 设置底部X坐标轴向上的竖网格线情况
            if ("Y".equalsIgnoreCase(showWall) || "XY".equalsIgnoreCase(showWall)) {
                chart.getAxes().getRight().getGrid().setColor(Color.fromCode(axisTextColor));
            } else {
                chart.getAxes().getRight().getGrid().setVisible(false);
            }
//			if (this.rAxisFormat != null && this.rAxisFormat.trim().length() > 0) {
//				chart.getAxes().getRight().getLabels().setValueFormat(this.rAxisFormat);
//			}
            chart.getAxes().getRight().setMaximumOffset(15);
            chart.getAxes().getRight().setMinimumOffset(10);
        } else {
            chart.getAxes().getRight().setMaximumOffset(15);
            chart.getAxes().getRight().setMinimumOffset(10);
            chart.getAxes().getRight().setVisible(false);
        }
    }

    /**
     * 设置背景面板
     */
    private void setBackPanel(SimpleChart chart) {
        // 设置背景面板背景色的透明度 ： 100 全透明， 0 表示 不透明
        chart.getPanel().getBrush().setTransparency(100);
        // 设置背景面板圆角情况
        chart.getPanel().setBorderRound(20);
        // 设置背景面板 渐变设置
        chart.getPanel().getGradient().setStartColor(new Color(255, 255, 255));
        chart.getPanel().getGradient().setEndColor(new Color(230, 230, 230));
        chart.getPanel().getGradient().setVisible(true);
    }

    /**
     * 设置图例
     */
    private void setLegend(SimpleChart chart) {
        // 设置图例边框颜色
        chart.getLegend().getPen().setColor(Color.fromCode(legendTextColor));
        // 设置图例位置
        if (legendPosition.equalsIgnoreCase("none")) {
            chart.getLegend().setVisible(false);
        } else if (legendPosition.equalsIgnoreCase("right")) {
            chart.getLegend().setAlignment(LegendAlignment.RIGHT);
        } else if (legendPosition.equalsIgnoreCase("left")) {
            chart.getLegend().setAlignment(LegendAlignment.LEFT);
        } else if (legendPosition.equalsIgnoreCase("bottom")) {
            chart.getLegend().setAlignment(LegendAlignment.BOTTOM);
        } else if (legendPosition.equalsIgnoreCase("top")) {
            chart.getLegend().setAlignment(LegendAlignment.TOP);
        }
        // 设置图例显示内容
        chart.getLegend().setTextStyle(LegendTextStyle.PLAIN);
        chart.getLegend().getBrush().setDefaultVisible(false);
        chart.getLegend().getFont().setSize(legendTextSize);
        chart.getLegend().getFont().setColor(Color.fromCode(legendTextColor));
        chart.getLegend().getPen().setVisible(false);
        chart.getLegend().getSymbol().setSquared(true);
    }

    /**
     * 设置柱
     */
    private void setShapeNode(SimpleChart chart) {
        chart.getAspect().setView3D(false);//平面显示柱图
        chart.getAspect().setOrthogonal(true);
        chart.getAspect().setTextSmooth(true);
        chart.getAspect().setSmoothingMode(true);
        chart.getAspect().setOrthoAngle(45);
    }

    /**
     * 设置 坐标面板的效果
     */
    private void setWall(SimpleChart chart) {
        // 设置坐标面板背景区域 隐藏
        chart.getWalls().getBack().setVisible(false);
        // 设置坐标面板 背景区域边框隐藏
        chart.getWalls().getBack().getPen().setVisible(false);
        chart.getWalls().getBottom().getPen().setVisible(false);
        // 设置坐标面板 底部坐标系隐藏
        chart.getWalls().getBottom().setVisible(false);
        chart.getWalls().getLeft().getPen().setVisible(false);
        // 设置坐标面板 左侧坐标系隐藏
        chart.getWalls().getLeft().setVisible(false);
        if (backWallColor != null && backWallColor.length > 1) {
            Gradient oGradient = new Gradient();
            oGradient.setStartColor(Color.fromCode(backWallColor[0]));
            oGradient.setMiddleColor(Color.fromCode(backWallColor[1]));
            oGradient.setEndColor(Color.fromCode(backWallColor[2]));
            oGradient.setDirection(GradientDirection.VERTICAL);
            oGradient.setVisible(true);
            chart.getWalls().getBack().setVisible(true);
            chart.getWalls().getBack().getBrush().setVisible(true);
            chart.getWalls().getBack().getBrush().setGradient(oGradient);
        }
    }

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

    public void setDataObj(JSONObject dataObj) {
        this.dataObj = dataObj;
    }

    public void setxAxisShow(boolean xAxisShow) {
        this.xAxisShow = xAxisShow;
    }

    public void setyAxisShow(boolean yAxisShow) {
        this.yAxisShow = yAxisShow;
    }

    public void setAxisTextSize(int axisTextSize) {
        this.axisTextSize = Level1Util.getSpSize(axisTextSize);
    }

    public void setAxisTextColor(String axisTextColor) {
        this.axisTextColor = axisTextColor;
    }

    public void setxAxisFormat(String xAxisFormat) {
        this.xAxisFormat = xAxisFormat;
    }

    public void setlAxisFormat(String lAxisFormat) {
        this.lAxisFormat = lAxisFormat;
    }

    //	public void setrAxisFormat(String rAxisFormat) {
//		this.rAxisFormat = rAxisFormat;
//	}
    public void setxAxisAngle(int xAxisAngle) {
        this.xAxisAngle = xAxisAngle;
    }

    public void setxAxisSeparator(int xAxisSeparator) {
        this.xAxisSeparator = xAxisSeparator;
    }

    public void setxAxisDesc(String xAxisDesc) {
        this.xAxisDesc = xAxisDesc;
    }

    public void setlAxisDesc(String lAxisDesc) {
        this.lAxisDesc = lAxisDesc;
    }

    //	public void setrAxisDesc(String rAxisDesc) {
//		this.rAxisDesc = rAxisDesc;
//	}
    public void setAxisDescSize(int axisDescSize) {
        this.axisDescSize = axisDescSize;
    }

    public void setAxisDescColor(String axisDescColor) {
        this.axisDescColor = axisDescColor;
    }

    public void setAdaptive(boolean adaptive) {
        this.adaptive = adaptive;
    }

    public void setLegendPosition(String legendPosition) {
        this.legendPosition = legendPosition;
    }

    public void setLegendTextSize(int legendTextSize) {
        this.legendTextSize = Level1Util.getSpSize(legendTextSize);
    }

    public void setLegendTextColor(String legendTextColor) {
        this.legendTextColor = legendTextColor;
    }

    public void setShowTop(boolean showTop) {
        this.showTop = showTop;
    }

    public void setTopDataTextSize(int topDataTextSize) {
        this.topDataTextSize = topDataTextSize;
    }

    public void setTopDataTextColor(String topDataTextColor) {
        this.topDataTextColor = topDataTextColor;
    }

    public void setTitlePosition(String titlePosition) {
        this.titlePosition = titlePosition;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitleTextSize(int titleTextSize) {
        this.titleTextSize = Level1Util.getSpSize(titleTextSize);
    }

    public void setTitleTextColor(String titleTextColor) {
        this.titleTextColor = titleTextColor;
    }

    public void setShowWall(String showWall) {
        this.showWall = showWall;
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

    public void setMarkLineColor(String markLineColor) {
        this.markLineColor = markLineColor;
    }

    public void setMakrLineTextSize(int makrLineTextSize) {
        this.makrLineTextSize = makrLineTextSize;
    }

    public void setMarkLineTextColor(String markLineTextColor) {
        this.markLineTextColor = markLineTextColor;
    }

    public void setMarkLineBackgroundColor(String markLineBackgroundColor) {
        this.markLineBackgroundColor = markLineBackgroundColor;
    }

    public void setlAxisScaleNum(int lAxisScaleNum) {
        if (lAxisScaleNum < 2) {
            lAxisScaleNum = 2;
        }
        this.lAxisScaleNum = lAxisScaleNum;
    }

    public void setyZeroInclude(boolean yZeroInclude) {
        this.yZeroInclude = yZeroInclude;
    }

    public void setShowNum(int showNum) {
        this.showNum = showNum;
    }

    public void setMuchSingleShow(boolean muchSingleShow) {
        this.muchSingleShow = muchSingleShow;
    }

    public void setSelectIndex(int selectIndex) {
        if (selectIndex > -1) {
            this.showMarkLine = true;
            this.selectIndex = selectIndex;
        }
    }

    public void setLongClickListener(LongCallBackEvent longClickListener) {
        this.longClickListener = longClickListener;
    }

    public void setMaskMethod(String maskMethod) {
        this.maskMethod = maskMethod;
    }

    public void setClickListener(ChartClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setAreaColors(String[] areaColors) {
        this.areaColors = areaColors;
    }

    public void setLineColor(String lineColor) {
        this.lineColor = lineColor;
    }

    public void setShowPoint(String showPoint) {
        this.showPoint = showPoint;
    }

    public void setGradient(com.steema.teechart.drawing.Gradient gradient) {
        this.gradient = gradient;
    }

    public void setSeriesTran(int seriesTran) {
        this.seriesTran = seriesTran;
    }

    public void setGradientLineSame(boolean isGradientLineSame) {
        this.isGradientLineSame = isGradientLineSame;
    }

    public void setAvgValue(String avgValue) {
        this.avgValue = avgValue;
    }

    public void setPointStyle(String pointStyle) {
        this.pointStyle = pointStyle;
    }

    public void setPointWidth(int pointWidth) {
        this.pointWidth = pointWidth;
    }

    public void setSmooth(boolean smooth) {
        this.smooth = smooth;
    }
}