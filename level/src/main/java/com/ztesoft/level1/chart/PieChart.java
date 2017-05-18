package com.ztesoft.level1.chart;

import android.app.Activity;
import android.widget.LinearLayout;

import com.ztesoft.level1.Level1Util;
import com.steema.teechart.SimpleChart;
import com.steema.teechart.SimpleChart.ChartClickListener;
import com.steema.teechart.drawing.Color;
import com.steema.teechart.events.SeriesMouseAdapter;
import com.steema.teechart.events.SeriesMouseEvent;
import com.steema.teechart.legend.LegendAlignment;
import com.steema.teechart.legend.LegendSymbolPosition;
import com.steema.teechart.legend.LegendTextStyle;
import com.steema.teechart.styles.ColorList;
import com.steema.teechart.styles.Donut;
import com.steema.teechart.styles.MarksStyle;
import com.steema.teechart.styles.Series;
import com.steema.teechart.styles.SeriesCollection;

/**
 * 饼图
 *
 * @author fanlei@asiainfo-linkage.com  2012-6-28 下午5:16:32
 * @ClassName: PieChart
 */
public class PieChart {
    // ---------------通用信息设置开始------------------
    private Activity activity;
    private LinearLayout chartLayout;
    private SimpleChart chart;

    // 文字信息（图形标题...）
    private String title = "";
    private int titleTextSize = Level1Util.getSpSize(10);
    private String titleTextColor = "#000000";
    // 样式信息（大小、颜色...）
    private String legendPosition = "bottom";
    //设置图例的文字颜色、字号
    private int legendTextSize = Level1Util.getSpSize(10);
    private String legendTextColor = "#000000";

    private String markFontColor = "#FFFFFF";
    private int markFontSize = Level1Util.getSpSize(10);
    private String[] pieColors = ColorDefault.pieColors;

    // 其他信息（图例位置、图例背景、坐标轴标签格式...）
    private boolean legendBackground = true;
    private MarksStyle markStyle = null;
    private boolean has3D = false;
    // ----------------通用信息设置结束---------------------

    // ----------------特有信息设置开始---------------------
    private String[] percentNames;
    private double[] percentValues;

    //是否显示wall
    private boolean isShowWall = true;
    public boolean isDonut = false;//是否是环形图
    private String xLableFormat = null;
    private String bgColor = null;
    // ----------------特有信息设置结束---------------------
    private ChartClickListener clickListener;//点击事件
    private String selColor = null;//选中柱的颜色
    private int legSize = 15;//mark连线的长度
    private boolean legVisible = true;//mark连线是否显示
    private boolean markPositionCenter = false;//mark的位置是否居中
    private boolean roseFlag = false;//玫瑰图
    private boolean percentFlag = false;//百分比图
    private int donutPercent = 0;//环的大小

    public void create() {
        chart = new SimpleChart(activity);

        // 设置底部X坐标轴
        setAxisX(chart);
        // 设置底部y坐标轴
        setAxisY(chart);
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
        if (!this.isShowWall) {
            hideWall();
        }
        chart.getZoom().setAllow(false);
        chart.setScrollToLeft(false);
        chart.setScrollToRight(false);

        try {
            Series series = Series.createNewSeries(chart.getChart(), Donut.class, null);
            String partName;
            for (int i = 0; i < percentValues.length; i++) {
                if (percentNames == null || percentNames.length == 0) {
                    partName = "[" + i + "]";
                } else {
                    partName = percentNames[i].trim();
                }
                if (xLableFormat != null && !"".equals(xLableFormat)) {
                    if ("MM-dd".equals(xLableFormat)) {
                        if (partName.length() > 4) {
                            partName = partName.substring(4, 6) + "-" + partName.substring(6, 8);
                        } else {
                            partName = partName.substring(0, 2) + "-" + partName.substring(2, 4);
                        }
                    } else if ("HH:mm".equals(xLableFormat)) {
                        partName = partName.substring(8, 10) + ":" + partName.substring(10, 12);
                    } else if ("yyyy-MM".equals(xLableFormat)) {
                        partName = partName.substring(0, 4) + "-" + partName.substring(4, 6);
                    }
                }
                series.add(percentValues[i], partName, Color.fromCode(pieColors[i % pieColors
                        .length]));
            }

            // 设置图上显示值
            if (this.markStyle == null) {
                series.getMarks().setVisible(false);
                ((Donut) series).getPen().setVisible(false);
                ((Donut) series).getMarks().getArrow().setVisible(false);
                ((Donut) series).getMarks().getCallout().setLength(0);
            } else {
                ((Donut) series).getPen().setVisible(false);
                series.getMarks().setVisible(true);
                series.getMarks().setStyle(markStyle);
                series.getMarks().getFont().setSize(markFontSize);
                series.getMarks().getFont().setColor(Color.fromCode(markFontColor));
                series.getMarks().getBrush().setDefaultVisible(false);
                series.getMarks().getPen().setVisible(false);
                series.getMarks().setMultiLine(true);
                // series与mark连线颜色
                ((Donut) series).getMarks().getArrow().setColor(Color.fromCode(markFontColor));
                ((Donut) series).getMarks().getArrow().setVisible(legVisible);
                if (!legVisible) {
                    ((Donut) series).getMarks().getCallout().setLength(0);
                    ((Donut) series).getMarksPie().setLegSize(0);
                } else {// series与mark连线长度
                    ((Donut) series).getMarksPie().setLegSize(Level1Util.getSpSize(legSize));
                }
                ((Donut) series).getMarksPie().setVertCenter(true);
            }
            if (!has3D) {
                ((Donut) series).setCircled(true);
            }
            ((Donut) series).getPen().setVisible(false);
            ((Donut) series).setDonutPercent(donutPercent);
            if (isDonut) {
                ((Donut) series).setDonutPercent(donutPercent);
            }
            ((Donut) series).setMarkPositionCenter(this.markPositionCenter);
            ((Donut) series).setRoseFlag(roseFlag);
            ((Donut) series).setPercentFlag(percentFlag);
            if (percentFlag) {
                ((Donut) series).setDonutPercent(donutPercent);
            }
            if (clickListener != null) {
                series.addSeriesMouseListener(new SeriesMouseAdapter() {
                    public void seriesClicked(SeriesMouseEvent e) {
                        clickListener.seriesClicked(e.getValueIndex());
                        SeriesCollection series = chart.getSeries();
                        if (series.size() > 0) {
                            Series s = series.getSeries(0);
                            ColorList iColors = new ColorList(s.getLabels().getCount());
                            for (int j = 0; j < s.getLabels().getCount(); j++) {
                                if (j == e.getValueIndex()) {
                                    if (selColor == null) {
                                        iColors.add(Color.fromCode(pieColors[j % pieColors
                                                .length]).applyDark(80));
                                    } else {
                                        iColors.add(Color.fromArgb(Color.parseColor(selColor)));
                                    }
                                } else {
                                    iColors.add(Color.fromCode(pieColors[j % pieColors.length]));
                                }
                            }
                            s.setColors(iColors);
                            s.refreshSeries();
                        }
                    }
                });
            }
            chart.addSeries(series);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        chartLayout.addView(chart);
        if (bgColor != null) {
            chartLayout.setBackgroundColor(Color.parseColor(bgColor));
        }
    }

    /**
     * 设置底部X坐标轴
     *
     * @param chart
     */
    public void setAxisX(SimpleChart chart) {
        // 设置底部X坐标轴：
        chart.getAxes().getBottom().getAxisPen().setColor(new Color(120, 120, 120));
        // 设置底部X坐标轴的坐标值是否显示
        chart.getAxes().getBottom().setLabelsOnAxis(false);
        // 设置底部X坐标轴的标签信息
        chart.getAxes().getBottom().getLabels().getFont().setSize(10);
        chart.getAxes().getBottom().getLabels().getFont().setColor(Color.BLACK);
        // 设置底部X坐标轴的标签信息，旋转角度
        // chart.getAxes().getBottom().getLabels().setAngle(270);
        // 设置底部X坐标轴的标签信息，交替展现
        chart.getAxes().getBottom().getLabels().setAlternate(true);
        // 设置底部X坐标轴向上的竖网格线情况
        // chart.getAxes().getBottom().getGrid().setStyle(DashStyle.DOT);
        chart.getAxes().getBottom().getGrid().setColor(new Color(120, 120, 120));
        // chart.getAxes().getBottom().getGrid().setVisible(false);
    }

    /**
     * 设置左侧Y坐标轴
     *
     * @param chart
     */
    private void setAxisY(SimpleChart chart) {
        // 设置左侧Y坐标轴：
        chart.getAxes().getLeft().getAxisPen().setColor(new Color(120, 120, 120));
        chart.getAxes().getLeft().getLabels().getFont().setSize(10);
        // 设置左侧Y坐标轴向上的竖网格线情况
        // chart.getAxes().getLeft().getGrid().setStyle(DashStyle.DOT);
        chart.getAxes().getLeft().getGrid().setColor(new Color(120, 120, 120));
        // chart.getAxes().getLeft().getGrid().setVisible(false);
        chart.getAxes().getLeft().getLabels().getFont().setColor(Color.BLACK);
    }

    /**
     * 设置背景面板
     *
     * @param chart
     */
    private void setBackPanel(SimpleChart chart) {
        // 设置背景面板背景色的透明度 ： 100 全透明， 0 表示 不透明
        // chart.getPanel().getBrush().setTransparency(100);
        chart.getPanel().getPen().setVisible(false);
        // chart.getPanel().getPen().setWidth(10);
        // chart.getPanel().getPen().setColor(Color.RED);
        chart.getPanel().setVisible(true);
        // 设置背景面板圆角情况
        chart.getPanel().setBorderRound(20);
        chart.getPanel().getBrush().setTransparency(100);
        // 设置背景面板 渐变设置
        // chart.getPanel().getGradient().setStartColor(new Color(255,255,255));
        // chart.getPanel().getGradient().setEndColor(new Color(230,230,230));
        // chart.getPanel().getGradient().setVisible(true);
    }

    /**
     * 设置图例
     *
     * @param chart
     */
    private void setLegend(SimpleChart chart) {
        if (percentNames == null || percentNames.length == 0) {
            chart.getLegend().setVisible(false);
            return;
        }
        // 设置图例边框颜色
        chart.getLegend().getPen().setColor(Color.fromCode(legendTextColor));
        // 设置图例的图标摆放位置
        chart.getLegend().getSymbol().setPosition(LegendSymbolPosition.LEFT);
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
        chart.getLegend().getFont().setSize(legendTextSize);
        chart.getLegend().getFont().setColor(Color.fromCode(legendTextColor));
        if (!legendBackground) {
            chart.getLegend().getPen().setVisible(legendBackground);
            chart.getLegend().getBrush().setDefaultVisible(legendBackground);
        }
        chart.getLegend().getSymbol().setSquared(true);
    }

    /**
     * 设置柱
     *
     * @param chart
     */
    private void setShapeNode(SimpleChart chart) {
        // 设置柱的3D阴影比例                       
        chart.getAspect().setChart3DPercent(20);
        chart.getAspect().setView3D(has3D);

        chart.getAspect().setOrthogonal(false);
        chart.getAspect().setPerspective(140);
        chart.getAspect().setRotation(180);
        chart.getAspect().setTextSmooth(true);
        chart.getAspect().setSmoothingMode(true);

        chart.getAspect().setPerspective(0);
        chart.getAspect().setRotation(360);
        chart.getAspect().setElevation(315);
    }

    /**
     * 设置 坐标面板的效果
     *
     * @param chart
     */
    private void setWall(SimpleChart chart) {
        // 设置坐标面板背景区域 隐藏
        chart.getWalls().getBack().setVisible(false);
        // 设置坐标面板 背景区域 边框隐藏
        chart.getWalls().getBack().getPen().setVisible(false);
        chart.getWalls().getBottom().getPen().setVisible(false);
        // 设置坐标面板 底部坐标系隐藏
        chart.getWalls().getBottom().setVisible(false);
        chart.getWalls().getLeft().getPen().setVisible(false);
        // 设置坐标面板 左侧坐标系隐藏
        chart.getWalls().getLeft().setVisible(false); 
        /*
         * Gradient oGradient=new Gradient(); oGradient.setStartColor(new
		 * Color(255,255,255)); oGradient.setEndColor(new Color(200,200,200));
		 * oGradient.setVisible(true);
		 * chart.getWalls().getBack().getBrush().setGradient(oGradient);
		 */
    }

    /**
     * 设置标题内容和字体
     *
     * @param chart
     * @param t
     */
    private void setTitle(SimpleChart chart, String t) {
        if (t == null || t.trim().length() == 0) {
            chart.getHeader().setVisible(false);
        } else {
            // 设置标题内容和字体
            chart.getHeader().setText(t);
            chart.getHeader().getFont().setSize(titleTextSize);
            chart.getHeader().getFont().setBold(true);
            chart.getHeader().getFont().setColor(Color.fromCode(titleTextColor));
        }
    }

    private void hideWall() {
        chart.getLegend().setVisible(false);
        chart.getAxes().setVisible(false);
        chart.getWalls().setVisible(false);
        chart.getPanel().setVisible(true);
        // 设置背景面板圆角情况
        chart.getPanel().setBorderRound(20);
        chart.getPanel().getBrush().setVisible(false);
        chart.getPanel().getPen().setVisible(false);
        chart.getHeader().setVisible(false);
        chart.getZoom().setAllow(false);
        chart.getAspect().setOrthogonal(false);
        chart.getAspect().setTextSmooth(true);
        chart.getAspect().setSmoothingMode(true);
    }

    public void setTitleTextSize(int titleTextSize) {
        this.titleTextSize = Level1Util.getSpSize(titleTextSize);
    }

    public String getTitleTextColor() {
        return titleTextColor;
    }

    public void setTitleTextColor(String titleTextColor) {
        this.titleTextColor = titleTextColor;
    }

    public void setxLableFormat(String xLableFormat) {
        this.xLableFormat = xLableFormat;
    }

    public void setMarkFontColor(String markFontColor) {
        this.markFontColor = markFontColor;
    }

    public void setMarkFontSize(int markFontSize) {
        this.markFontSize = Level1Util.getSpSize(markFontSize);
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setLegendTextSize(int legendTextSize) {
        this.legendTextSize = Level1Util.getSpSize(legendTextSize);
    }

    public void setLegendTextColor(String legendTextColor) {
        this.legendTextColor = legendTextColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public PieChart(Activity activity) {
        this.activity = activity;
    }

    public void setChartInfo(LinearLayout chartLayout) {
        this.chartLayout = chartLayout;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLegendPosition(String legendPosition) {
        this.legendPosition = legendPosition;
    }

    public void setPercentNames(String[] percentNames) {
        this.percentNames = percentNames;
    }

    public void setPercentValues(float[] percentValues) {
        this.percentValues = new double[percentValues.length];
        for (int i = 0; i < percentValues.length; i++) {
            this.percentValues[i] = percentValues[i];
        }
    }

    public void setPercentValues(double[] percentValues) {
        this.percentValues = percentValues;
    }

    public void setPieColors(String[] pieColors) {
        this.pieColors = pieColors;
    }

    public void setMarkStyle(MarksStyle markStyle) {
        this.markStyle = markStyle;
    }

    public void setHas3D(boolean has3D) {
        this.has3D = has3D;
    }

    public void setLegendBackground(boolean legendBackground) {
        this.legendBackground = legendBackground;
    }

    public void setIsShowWall(boolean isShowWall) {
        this.isShowWall = isShowWall;
    }

    public void setXLableFormat(String xLableFormat) {
        this.xLableFormat = xLableFormat;
    }

    public void setClickListener(ChartClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setSelColor(String selColor) {
        this.selColor = selColor;
    }

    public void setDonut(boolean flag) {
        this.isDonut = flag;
        donutPercent = 40;
    }

    public void setDonutPercent(int donutPercent) {
        this.donutPercent = donutPercent;
    }

    public void setMarkPositionCenter(boolean markPositionCenter) {
        this.markPositionCenter = markPositionCenter;
    }

    public void setRoseFlag(boolean roseFlag) {
        this.roseFlag = roseFlag;
    }

    public void setPercentFlag(boolean percentFlag) {
        donutPercent = 80;
        this.percentFlag = percentFlag;
    }

    public void setLegVisible(boolean legVisible) {
        this.legVisible = legVisible;
    }

    public void setLegSize(int legSize) {
        this.legSize = legSize;
    }
}
