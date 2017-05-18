package com.ztesoft.level1.chart;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
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
import com.steema.teechart.styles.BarNew;
import com.steema.teechart.styles.ColorList;
import com.steema.teechart.styles.Line;
import com.steema.teechart.styles.MarksStyle;
import com.steema.teechart.styles.MultiBars;
import com.steema.teechart.styles.PointerStyle;
import com.steema.teechart.styles.Series;
import com.ztesoft.level1.Level1Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/***
 * 柱线图
 * headArray: [{type:x,value:v1},{name:移动,type:line,value:v2,top:v7,eventText:v6}....],
 * valueArray:[{v1:,v2:,v3},{}.....]
 * @author wangsq
 *
 */
public class LineBarChart {
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
    private String rAxisFormat = null;
    //设置X轴倾斜、交替
    private int xAxisAngle = 0;
    private int xAxisSeparator = 1;
    //设置x，y1,y2坐标轴描述文字
    private String xAxisDesc = null;
    private String lAxisDesc = null;
    private String rAxisDesc = null;
    //设置坐标轴描述文字大小、颜色
    private int axisDescSize = 12;
    private String axisDescColor = "#000000";
    //设置图形自适应
    private boolean adaptive = true;
    //设置柱宽
    private int barWidth = -1;//图形的最大宽度
    //设置图例位置
    private String legendPosition = "bottom";
    //设置图例的文字颜色、字号
    private int legendTextSize = Level1Util.getSpSize(10);
    private String legendTextColor = "#000000";
    //设置柱图颜色组
    private String[] barColors = ColorDefault.colors;
    private String[] lineColors = ColorDefault.colors;
    //设置柱图显示效果
    private int barStyle = 1;// (1:并列  2:堆积 3:叠加)
    //设置是否显示占比数据及显示位置（none，middle,top,bottom）。仅叠柱图时有效。
    private String dataPosition = "none";
    //设置图形数据是否显示百分比。仅叠柱图时有效。仅setDataPosition不为none时有效
    private boolean dataIsPercent = false;
    //设置图形数据显示大小、颜色
    private int dataTextSize = 10;
    private String dataTextColor = "#000000";
    //是否在顶部显示数据
    private boolean showTop = false;
    //设置顶部数据连接线的颜色
    private String topDataLineColoe = "#000000";
    //设置顶部数据大小、颜色
    private int topDataTextSize = Level1Util.getSpSize(10);
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
    private int markLineTextSize = 12;
    private String markLineTextColor = "#ffffff";
    private String markLineBackgroundColor = "#000000";
    //设置纵坐标刻度数
    private int lAxisScaleNum = 4;
    private int rAxisScaleNum = 4;
    //设置纵坐标是否格式化成K、M，默认格式化
    private boolean yAxisNumberScale = false;
    //设置是否从0开始
    private boolean yZeroInclude = false;
    private int showNum = 8;//一页显示的个数
    // 坐标系面板
    private String[] backWallColor = null;
    private boolean muchSingleShow = false;// 是否支持双指
    private int selectIndex = -1;// 选中的是第几个柱或点；
    private LongCallBackEvent longClickListener;// 长按滑动接口
    private ChartClickListener clickListener;//点击事件
    private String selColor = null;//选中柱的颜色
    private String maskMethod = null;//外面画遮罩的回调函数名字
    private int lineWidth = Level1Util.getSpSize(2);//线的宽度
    int allUnit = 0;
    HashMap<String, String[]> clickTextHash = new HashMap<String, String[]>();
    //图例是否可以被点击
    private boolean legendClick = false;
    //线图圆点直径
    private int linePointRd = 4;
    //y轴坐标适应
    private boolean yAxisAuto = false;

    private boolean needDispatchToParent = true;

    //是否居左显示
    private boolean isSHowFromLeft = false;

    private boolean smooth = true;//是否光滑
    private String pointStyle = "circle";//circle:实心圆,hcircle:空心圆,rectangle:矩形triangle:三角形
    private String avgValue;//均线
    private int avgValueColor = 0xFF000000;

    /**
     * 构造函数
     *
     * @param context      上下文
     * @param parentLayout 父视图
     */
    public LineBarChart(Context context, LinearLayout parentLayout) {
        this.context = context;
        this.chartLayout = new LinearLayout(context) {
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

    /**
     * 设置数据
     *
     * @param chartObj 数据源
     */
    public void setJSON(JSONObject chartObj) {
        this.dataObj = chartObj;
    }

    /***
     * 获取柱的类型
     * @param barStyle
     * @return
     */
    private MultiBars getBarsType(int barStyle) {
        switch (barStyle) {
            case 1:
                return MultiBars.SIDE;
            case 2:
                return MultiBars.STACKED;
            case 3:
                return MultiBars.NONE;
            default:
                return MultiBars.NONE;
        }
    }

    /**
     * 添加线柱图
     *
     * @param propArray 多柱属性组
     * @param xCode     x轴取值code
     * @param axisType  l,r：依附的y轴
     * @throws Exception 抛出异常
     */
    private void addSeriesChart(JSONArray propArray, String xCode, String axisType) throws
            Exception {
        JSONObject tempObj;
        double minValue = Double.MIN_VALUE;
        double maxValue = Double.MIN_VALUE;
        JSONArray dataArray = dataObj.getJSONArray("valueArray");
        if (propArray.length() > 0) {
            Series series;
            double[] stackedTotal = new double[dataArray.length()];//叠柱图整体总数
            double[] plusTotal = new double[dataArray.length()];//正数
            double[] minusTotal = new double[dataArray.length()];//负数
            for (int i = 0; i < propArray.length(); i++) {
                tempObj = propArray.getJSONObject(i);
                if ("bar".equals(tempObj.optString("type"))) {
                    series = Series.createNewSeries(chart.getChart(), BarNew.class, null);
                    ((BarNew) series).setMultiBar(getBarsType(barStyle));
                    if (barWidth > 0) {
                        ((BarNew) series).setCustomBarWidth(barWidth);// 设置柱的最大宽度
//						if(barStyle==3){
//							barWidth=barWidth-8;
//						}
                    }
                    if (tempObj.has("index"))
                        series.setColor(Color.fromCode(barColors[tempObj.getInt("index") %
                                barColors.length]));//柱颜色
                    else
                        series.setColor(Color.fromCode(barColors[i % barColors.length]));//柱颜色
                    ((BarNew) series).getPen().setVisible(false);
                } else {
                    series = Series.createNewSeries(chart.getChart(), Line.class, null);
                    ((Line) series).setSmoothed2D(smooth);
                    if (tempObj.has("index"))
                        series.setColor(Color.fromCode(lineColors[tempObj.getInt("index") %
                                lineColors.length]));//线颜色
                    else
                        series.setColor(Color.fromCode(lineColors[i % lineColors.length]));//线颜色
                    //线的圆点					
                    ((Line) series).getLinePen().setWidth(lineWidth);
                    //线粗细
                    ((Line) series).getPointer().setVisible(true);
                    ((Line) series).getPointer().getPen().setWidth(lineWidth);//线粗细
                    ((Line) series).getPointer().setStyle(PointerStyle.CIRCLE);
                    ((Line) series).getPointer().getPen().setVisible(false);
                    ((Line) series).getPointer().getPen().setColor(series.getColor());
                    ((Line) series).getPointer().setColor(series.getColor());
                    if ("circle".equals(this.pointStyle)) {
                        ((Line) series).getPointer().setStyle(PointerStyle.CIRCLE);
                    } else if ("hcircle".equals(this.pointStyle)) {
                        ((Line) series).getPointer().setStyle(PointerStyle.CIRCLE);
                        ((Line) series).getPointer().setColor(Color.fromCode("#ffffff"));
                        ((Line) series).getPointer().getPen().setVisible(true);
                    } else if ("rectangle".equals(this.pointStyle)) {
                        ((Line) series).getPointer().setStyle(PointerStyle.RECTANGLE);
                    } else if ("triangle".equals(this.pointStyle)) {
                        ((Line) series).getPointer().setStyle(PointerStyle.TRIANGLE);
                    }
                    ((Line) series).getPointer().setHorizSize(Level1Util.dip2px(context,
                            linePointRd));//圆点半径
                    ((Line) series).getPointer().setVertSize(Level1Util.dip2px(context,
                            linePointRd));//圆点半径					
                }
                //添加点
                if (dataArray.length() > 0) {
                    String title = tempObj.optString("name", null);
                    String value = tempObj.optString("value", null);
//                    String top = tempObj.optString("top", null);
                    String eventText = tempObj.optString("eventText", null);
                    String[] eventTextArr = new String[dataArray.length()];
                    if (value == null) {
                        continue;
                    }
                    if (title != null) {
                        series.setTitle(title);
                    }
                    //看看是否是百分比
                    boolean divi100 = false;
                    if ("l".equals(axisType)) {
                        if (lAxisFormat != null && lAxisFormat.contains("%")) {
                            divi100 = true;
                            series.setValueFormat(lAxisFormat);
                        }
                        series.setCustomVertAxis(chart.getAxes().getLeft());
                    }
                    if ("r".equals(axisType)) {
                        if (rAxisFormat != null && rAxisFormat.contains("%")) {
                            divi100 = true;
                            series.setValueFormat(rAxisFormat);
                        }
                        series.setCustomVertAxis(chart.getAxes().getRight());
                    }
                    boolean[] visibilities = new boolean[dataArray.length()];
                    double v;
                    for (int j = 0; j < dataArray.length(); j++) {
                        v = 0;
                        visibilities[j] = (j % xAxisSeparator == 0);

                        if (dataArray.getJSONObject(j).optString(value, "").trim().length() > 0) {
                            v = dataArray.getJSONObject(j).optDouble(value, 0);
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
                        if ("bar".equals(tempObj.optString("type")) && barStyle == 2) {//仅在堆积柱图时计算
                            if (v > 0) {
                                plusTotal[j] = plusTotal[j] + v;
                            } else {
                                minusTotal[j] = minusTotal[j] + v;
                            }
                            stackedTotal[j] = stackedTotal[j] + v;
                        }
                        series.add(v, getxAxes(dataArray.getJSONObject(j).getString(xCode)));

                        if (eventText != null) {
                            eventTextArr[j] = dataArray.getJSONObject(j).getString(eventText);
                        }
                    }
                    if (eventText != null) {
                        clickTextHash.put(chart.getSeriesCount() + "", eventTextArr);
                    }
                    if (xAxisSeparator > 1) {
                        chart.getAxes().getBottom().getLabels().setVisibilities(visibilities);
                    }
                    chart.setStackedTotal(stackedTotal);
                }
                //看是否要显示top数据---------本数据占整体数据的百分比
                if (showTop) {
                    series.getMarks().setVisible(true);
//					series.getMarks().setArrowLength(0);//连线的长度
                    series.getMarks().getArrow().setColor(Color.fromCode(topDataLineColoe));
                    series.getMarks().setStyle(MarksStyle.VALUE);
                    series.getMarks().getFont().setSize(topDataTextSize);
                    series.getMarks().getFont().setColor(Color.fromCode(topDataTextColor));
                    series.getMarks().getBrush().setDefaultVisible(false);
                    series.getMarks().getPen().setVisible(false);
                } else {
                    series.getMarks().setVisible(false);
                }
                if (clickListener != null || clickTextHash.size() > 0) {
                    series.addSeriesMouseListener(new SeriesMouseAdapter() {
                        public void seriesClicked(SeriesMouseEvent e) {
                            if (clickListener != null) {
                                clickListener.seriesClicked(e.getValueIndex());
                            }
                            Series curSeries = (Series) e.getSeries();
                            for (int i = 0; i < chart.getSeriesCount(); i++) {
                                if (curSeries == chart.getSeries(i)) {
                                    if (clickTextHash.get((i + 1) + "") != null) {
                                        chart.setClickShowText(((String[]) clickTextHash.get((i +
                                                1) + ""))[e.getValueIndex()]);
                                        chart.setCurIndex(e.getValueIndex());
                                        chart.setCurSeries(curSeries);
                                    }
                                }
                            }
                            if (curSeries instanceof BarNew && selColor != null) {
                                ColorList iColors = new ColorList(curSeries.getLabels().getCount());
                                for (int j = 0; j < curSeries.getLabels().getCount(); j++) {
                                    if (j == e.getValueIndex()) {
                                        if (selColor == null) {
                                            iColors.add(curSeries.getColor().applyDark(80));
                                        } else {
                                            iColors.add(Color.fromArgb(Color.parseColor(selColor)));
                                        }
                                    } else {
                                        iColors.add(curSeries.getColor());
                                    }
                                }
                                curSeries.setColors(iColors);
                            }
                            curSeries.refreshSeries();
                        }
                    });
                }

                chart.addSeries(series);
            }

            ChartIncrement increm = new ChartIncrement();
            if (minValue != maxValue && !yAxisAuto) {//最大值不等于最小值，且y轴刻度不自动
                if (yZeroInclude && minValue > 0) minValue = 0;
                if (barStyle == 2) {//当为堆积柱图时，重新计算最大最小值
                    for (int x = 0; x < plusTotal.length; x++) {
                        if (maxValue < plusTotal[x]) {
                            maxValue = plusTotal[x];
                        }
                    }
                    for (int x = 0; x < minusTotal.length; x++) {
                        if (minValue > minusTotal[x]) {
                            minValue = minusTotal[x];
                        }
                    }
                }
                if ("l".equals(axisType)) {
                    increm.setNumDivLines(lAxisScaleNum);
                    increm.setAxesValue(chart.getAxes().getLeft(), minValue, maxValue, lAxisFormat);
                } else {
                    increm.setNumDivLines(rAxisScaleNum);
                    increm.setAxesValue(chart.getAxes().getRight(), minValue, maxValue,
                            rAxisFormat);
                }
            }
        }
    }

    private class gesDetector extends SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (clickListener != null) {
                clickListener.onbgClick();
//				SeriesCollection series=chart.getSeries();
//				for(int i=0;i<series.size();i++){
//	    			Series s = series.getSeries(i);
//	    			s.setColors(null);
//	    			s.refreshSeries();
//				}
            }
            return true;
        }
    }

    /**
     * 获取x轴格式化后的值
     *
     * @param dimName 输入值
     * @return 格式化后的值
     */
    private String getxAxes(String dimName) {
        if (!TextUtils.isEmpty(xAxisFormat)) {
            dimName = dimName.replaceAll("-", "");
            if ("MM-dd".equals(xAxisFormat)) {
                if (dimName.length() > 4) {
                    dimName = dimName.substring(4, 6) + "-" + dimName.substring(6, 8);
                } else {
                    dimName = dimName.substring(0, 2) + "-" + dimName.substring(2, 4);
                }
            } else if ("HH:mm".equals(xAxisFormat)) {
                dimName = dimName.substring(8, 10) + ":" + dimName.substring(10, 12);
            } else if ("yyyy-MM".equals(xAxisFormat)) {
                dimName = dimName.substring(0, 4) + "-" + dimName.substring(4, 6);
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
        }
        return dimName;
    }

    public void create() throws JSONException {
        if (dataObj == null) {
            return;
        }
        chartLayout.removeAllViews();
        chart = new SimpleChart(context);
        chart.setDispatchEventWhenScrollToSide(needDispatchToParent);
        chart.setMakrLineTextSize(markLineTextSize);
        chart.setMarkLineBackgroundColor(markLineBackgroundColor);
        chart.setMarkLineColor(markLineColor);
        chart.setMarkLineTextColor(markLineTextColor);
        chart.setShowMarkLine(showMarkLine);
        chart.setLongCallBackEvent(longClickListener);
        chart.setSelectIndex(selectIndex);
        chart.setMuchSingleShow(muchSingleShow);
        chart.setMaskMethod(maskMethod);
//		if(barStyle==2){//仅叠柱图
        chart.setDataPosition(dataPosition);
        chart.setDataIsPercent(dataIsPercent);
        chart.setDataTextColor(dataTextColor);
        chart.setDataTextSize(dataTextSize);
//			showTop=false;
//		}else{
//			if(showTop)dataPosition="none";
//		}
        JSONArray valueArray = dataObj.getJSONArray("valueArray");
        allUnit = valueArray.length();
        JSONArray propArray = dataObj.getJSONArray("headArray");
        String xCode = "";
        JSONObject tempObj;
        JSONArray lArray = new JSONArray();
        JSONArray rArray = new JSONArray();
        boolean isAllBar = true;
        for (int i = 0; i < propArray.length(); i++) {
            tempObj = propArray.getJSONObject(i);
            if ("x".equalsIgnoreCase(tempObj.optString("type", ""))) {
                xCode = tempObj.optString("value", "");
            } else {
                if (tempObj.has("axisType")) {
                    tempObj.put("index", i);
                    if ("l".equalsIgnoreCase(tempObj.optString("axisType", ""))) {
                        lArray.put(tempObj);
                    } else if ("r".equalsIgnoreCase(tempObj.optString("axisType", ""))) {
                        rArray.put(tempObj);
                    }
                } else {
                    if ("bar".equalsIgnoreCase(tempObj.optString("type", ""))) {
                        lArray.put(tempObj);
                    } else if ("line".equalsIgnoreCase(tempObj.optString("type", ""))) {
                        isAllBar = false;
                        rArray.put(tempObj);
                    }
                }
            }
        }
        // 解决单图例显示问题
        if (lArray.length() + rArray.length() == 1) {
            this.legendPosition = "none";
        }

        try {
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
            if (lArray.length() > 0) {
                addSeriesChart(lArray, xCode, "l");
                addSeriesChart(rArray, xCode, "r");
            } else {
                addSeriesChart(rArray, xCode, "l");
            }

            if (avgValue != null && avgValue.trim().length() > 0) {
                JSONArray dataArray = dataObj.getJSONArray("valueArray");
                Line series = new com.steema.teechart.styles.Line(chart.getChart());
                series.setTitle("均线");
                ((Line) series).getLinePen().setWidth(lineWidth);
                for (int i = 0; i < dataArray.length(); i++) {
                    series.add(Double.parseDouble(avgValue), Color.fromArgb(avgValueColor));
                }
                chart.addSeries(series);
            }
            //判断是否要添加滑动事件
            int serNum = valueArray.length() - 1;//所有节点
            if (!adaptive) {
                if (serNum > showNum) {
                    if (isAllBar && isSHowFromLeft) {
                        chart.getAxes().getBottom().setMaximum(showNum + 0.5);
                        chart.getAxes().getBottom().setMinimum(-0.5);
                    } else {
                        chart.getAxes().getBottom().setMaximum(serNum + 0.5);
                        chart.getAxes().getBottom().setMinimum(serNum - showNum - 0.5);
                    }

                    chart.getPanning().setMouseButton(FrameworkMouseEvent.BUTTON1);
                    chart.addChartMotionListener(new xxChartMotionListener());
                } else {//小于一屏显示的个数自动为自适应
                    chart.getAxes().getBottom().setMaximum(serNum + 0.5);
                    chart.getAxes().getBottom().setMinimum(-0.5);
                    chart.setScrollToLeft(false);
                    chart.setScrollToRight(false);
                }
                chart.getAxes().getBottom().setAutomatic(false);
                chart.getPanning().setAllow(ScrollMode.HORIZONTAL);
            } else {
                chart.getAxes().getBottom().setAutomatic(false);
                chart.getAxes().getBottom().setMaximum(serNum + 0.5);
                chart.getAxes().getBottom().setMinimum(-0.5);
                chart.setScrollToLeft(false);
                chart.setScrollToRight(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LinearLayout axisDesclay = new LinearLayout(context);
        if (!TextUtils.isEmpty(lAxisDesc) || !TextUtils.isEmpty(rAxisDesc)) {
            axisDesclay.setPadding(Level1Util.getDipSize(15), 0, Level1Util.getDipSize(15), 0);
            TextView lView = new TextView(context);
            lView.setText(lAxisDesc);
            lView.setTextColor(Color.parseColor(axisDescColor));
            lView.setTextSize(TypedValue.COMPLEX_UNIT_SP, axisDescSize);
            axisDesclay.addView(lView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                    .MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            lView = new TextView(context);
            lView.setText(rAxisDesc);
            lView.setTextColor(Color.parseColor(axisDescColor));
            lView.setTextSize(TypedValue.COMPLEX_UNIT_SP, axisDescSize);
            axisDesclay.addView(lView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout
                    .LayoutParams.WRAP_CONTENT);

            chartLayout.addView(axisDesclay, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout
                    .LayoutParams.WRAP_CONTENT);
        }

        chartLayout.addView(chart);
        chartLayout.setBackgroundColor(Color.parseColor(backgroundColor));
    }

    /***
     * 滑动事件
     * @author wangsq
     *
     */
    private class xxChartMotionListener implements ChartMotionListener {
        @Override
        public void scrolled(ChartEvent chartevent) {
            chart.setCurIndex(-1);
            chart.getAxes().getBottom().setAutomaticMinimum(false);
            chart.getAxes().getBottom().setAutomaticMaximum(false);
            chart.setScrollToLeft(true);
            chart.setScrollToRight(true);
            if (chart.getAxes().getBottom().getMinimum() <= -0.5) {
                chart.setScrollToLeft(true);
                chart.setScrollToRight(false);
                chart.getAxes().getBottom().setMinimum(-0.5);
                chart.getAxes().getBottom().setMaximum(showNum + 0.5);
            }
            int serNum = allUnit - 1;
            if (chart.getAxes().getBottom().getMaximum() >= serNum + 0.5) {
                chart.setScrollToLeft(false);
                chart.setScrollToRight(true);
                if (serNum >= showNum + 0.5) {
                    chart.getAxes().getBottom().setMinimum(serNum - showNum - 0.5);
                    chart.getAxes().getBottom().setMaximum(serNum + 0.5);
                } else {
                    chart.getAxes().getBottom().setMaximum(showNum + 0.5);
                    chart.getAxes().getBottom().setMinimum(-0.5);
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

        // 设置底部X坐标轴：
        chart.getAxes().getBottom().getAxisPen().setColor(Color.fromCode(axisTextColor));
        // 设置底部X坐标轴的坐标值是否显示
        chart.getAxes().getBottom().setLabelsOnAxis(false);
        // 设置底部X坐标轴的标签信息
        chart.getAxes().getBottom().getLabels().getFont().setSize(axisTextSize);
        chart.getAxes().getBottom().getLabels().getFont().setColor(Color.fromCode(axisTextColor));
        // 设置底部X坐标轴的标签信息，旋转角度
        chart.getAxes().getBottom().getLabels().setAngle(xAxisAngle);
        // 设置底部X坐标轴向上的竖网格线情况
        if ("X".equalsIgnoreCase(showWall) || "XY".equalsIgnoreCase(showWall)) {
            chart.getAxes().getBottom().getGrid().setColor(Color.fromCode(axisTextColor));
        } else {
            chart.getAxes().getBottom().getGrid().setVisible(false);
        }
        chart.getAxes().getBottom().getLabels().setSeparation(0);
        //			刻度的显示控制
        //			chart.getAxes().getBottom().getMinorTicks().setVisible(showTicks);
        //			chart.getAxes().getBottom().getTicks().setVisible(showTicks);


        if (!xAxisShow) {
            if ("X".equalsIgnoreCase(showWall) || "XY".equalsIgnoreCase(showWall)) {
                chart.getAxes().getBottom().setTickOnLabelsOnly(false);
                chart.getAxes().getBottom().getLabels().setVisible(false);
                chart.getAxes().getBottom().getMinorTicks().setVisible(false);
                chart.getAxes().getBottom().getTicks().setVisible(false);
            } else {
                chart.getAxes().getBottom().setVisible(false);
            }
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

        // 设置左侧Y坐标轴：
        chart.getAxes().getLeft().getAxisPen().setColor(Color.fromCode(axisTextColor));
        chart.getAxes().getLeft().getLabels().getFont().setSize(axisTextSize);
        chart.getAxes().getLeft().getLabels().getFont().setColor(Color.fromCode(axisTextColor));
        chart.getAxes().getLeft().getLabels().setNumberScale(yAxisNumberScale);//K,M格式化

        // 设置底部X坐标轴向上的竖网格线情况
        if ("Y".equalsIgnoreCase(showWall) || "XY".equalsIgnoreCase(showWall)) {
            chart.getAxes().getLeft().getGrid().setColor(Color.fromCode(axisTextColor));
        } else {
            chart.getAxes().getLeft().getGrid().setVisible(false);
        }
        if (this.lAxisFormat != null && this.lAxisFormat.trim().length() > 0) {
            chart.getAxes().getLeft().getLabels().setValueFormat(this.lAxisFormat);
        }
        chart.getAxes().getLeft().setMaximumOffset(Level1Util.getDipSize(10));
        //			chart.getAxes().getLeft().getMinorTicks().setVisible(showTicks);
        //			chart.getAxes().getLeft().getTicks().setVisible(showTicks);
        if (!yAxisShow) {
            if ("Y".equalsIgnoreCase(showWall) || "XY".equalsIgnoreCase(showWall)) {
                chart.getAxes().getLeft().setTickOnLabelsOnly(false);
                chart.getAxes().getLeft().getLabels().setVisible(false);
                chart.getAxes().getLeft().getMinorTicks().setVisible(false);
                chart.getAxes().getLeft().getTicks().setVisible(false);
            } else {
                chart.getAxes().getLeft().setVisible(false);
            }
        }
    }

    /**
     * 设置右侧Y坐标轴
     */
    private void setAxisRightY(SimpleChart chart) {
        // 设置左侧Y坐标轴：
        chart.getAxes().getRight().getAxisPen().setColor(Color.fromCode(axisTextColor));
        chart.getAxes().getRight().getLabels().getFont().setSize(axisTextSize);
        chart.getAxes().getRight().getLabels().getFont().setColor(Color.fromCode(axisTextColor));
        chart.getAxes().getRight().getLabels().setNumberScale(yAxisNumberScale);//K,M格式化
        // 设置底部X坐标轴向上的竖网格线情况
        if ("Y".equalsIgnoreCase(showWall) || "XY".equalsIgnoreCase(showWall)) {
            chart.getAxes().getRight().getGrid().setColor(Color.fromCode(axisTextColor));
        } else {
            chart.getAxes().getRight().getGrid().setVisible(false);
        }
        if (this.rAxisFormat != null && this.rAxisFormat.trim().length() > 0) {
            chart.getAxes().getRight().getLabels().setValueFormat(this.rAxisFormat);
        }
        chart.getAxes().getRight().setMaximumOffset(10);
        if (!yAxisShow) {
            if ("Y".equalsIgnoreCase(showWall) || "XY".equalsIgnoreCase(showWall)) {
                chart.getAxes().getRight().setTickOnLabelsOnly(false);
                chart.getAxes().getRight().getLabels().setVisible(false);
                chart.getAxes().getRight().getMinorTicks().setVisible(false);
                chart.getAxes().getRight().getTicks().setVisible(false);
            } else {
                chart.getAxes().getRight().setVisible(false);
            }
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
        if (!yAxisShow) {
            chart.getPanel().setMarginLeft(0);
            chart.getPanel().setMarginRight(0);
        }
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
        chart.getLegend().setCheckBoxes(legendClick);
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
    private void setTitle(SimpleChart chart, String t) {
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

    public boolean isSHowFromLeft() {
        return isSHowFromLeft;
    }

    public void setSHowFromLeft(boolean isSHowFromLeft) {
        this.isSHowFromLeft = isSHowFromLeft;
    }

    public void setNeedDispatchToParent(boolean needDispatchToParent) {
        this.needDispatchToParent = needDispatchToParent;
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

    public void setrAxisFormat(String rAxisFormat) {
        this.rAxisFormat = rAxisFormat;
    }

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

    public void setrAxisDesc(String rAxisDesc) {
        this.rAxisDesc = rAxisDesc;
    }

    public void setAxisDescSize(int axisDescSize) {
        this.axisDescSize = axisDescSize;
    }

    public void setAxisDescColor(String axisDescColor) {
        this.axisDescColor = axisDescColor;
    }

    public void setAdaptive(boolean adaptive) {
        this.adaptive = adaptive;
    }

    public void setBarWidth(int barWidth) {
        this.barWidth = Level1Util.getSpSize(barWidth);
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

    public void setBarColors(String[] barColors) {
        this.barColors = barColors;
    }

    public void setLineColors(String[] lineColors) {
        this.lineColors = lineColors;
    }

    public void setBarStyle(int barStyle) {
        this.barStyle = barStyle;
    }

    public void setDataPosition(String dataPosition) {
        this.dataPosition = dataPosition;
    }

    public void setDataIsPercent(boolean dataIsPercent) {
        this.dataIsPercent = dataIsPercent;
    }

    public void setDataTextSize(int dataTextSize) {
        this.dataTextSize = dataTextSize;
    }

    public void setDataTextColor(String dataTextColor) {
        this.dataTextColor = dataTextColor;
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

    public void setMarkLineTextSize(int makrLineTextSize) {
        this.markLineTextSize = makrLineTextSize;
    }

    public void setMarkLineTextColor(String markLineTextColor) {
        this.markLineTextColor = markLineTextColor;
    }

    public void setMarkLineBackgroundColor(String markLineBackgroundColor) {
        this.markLineBackgroundColor = markLineBackgroundColor;
    }

    public void setlAxisScaleNum(int lAxisScaleNum) {
        if (lAxisScaleNum < 0) {
            lAxisScaleNum = 2;
        }
        this.lAxisScaleNum = lAxisScaleNum;
    }

    public void setrAxisScaleNum(int rAxisScaleNum) {
        if (rAxisScaleNum < 0) {
            rAxisScaleNum = 2;
        }
        this.rAxisScaleNum = rAxisScaleNum;
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

    public void setLineWidth(int lineWidth) {
        this.lineWidth = Level1Util.getSpSize(lineWidth);
    }

    public String getSelColor() {
        return selColor;
    }

    public void setSelColor(String selColor) {
        this.selColor = selColor;
    }

    public void setLegendClick(boolean legendClick) {
        this.legendClick = legendClick;
    }

    public void setLinePointRd(int linePointRd) {
        this.linePointRd = linePointRd;
    }

    public void setyAxisAuto(boolean yAxisAuto) {
        this.yAxisAuto = yAxisAuto;
    }

    public SimpleChart getChart() {
        return chart;
    }

    public void setyAxisNumberScale(boolean yAxisNumberScale) {
        this.yAxisNumberScale = yAxisNumberScale;
    }

    public void setAvgValueColor(int avgValueColor) {
        this.avgValueColor = avgValueColor;
    }

    public void setTopDataLineColoe(String topDataLineColoe) {
        this.topDataLineColoe = topDataLineColoe;
    }

    public void setAvgValue(String avgValue) {
        this.avgValue = avgValue;
    }

    public void setPointStyle(String pointStyle) {
        this.pointStyle = pointStyle;
    }

    public void setSmooth(boolean smooth) {
        this.smooth = smooth;
    }
}
