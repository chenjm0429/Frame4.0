package com.ztesoft.level1.chart;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Paint;
import android.widget.LinearLayout;

import com.ztesoft.level1.Level1Util;
import com.steema.teechart.SimpleChart;
import com.steema.teechart.drawing.Color;
import com.steema.teechart.drawing.Gradient;
import com.steema.teechart.drawing.GradientDirection;
import com.steema.teechart.legend.LegendAlignment;
import com.steema.teechart.legend.LegendTextStyle;
import com.steema.teechart.styles.GaugesNew;

/**
 * 仪表盘
 * 支持JSON格式：
 * {minimum:"0",maximum:"100",minorTickCount:"4",increment:"10",values:[{dimName:"用户",value:"0"},
 * {}]}
 * <P>Description: 仪表盘图形控件
 *
 * @author fanlei@asiainfo-linkage.com  2013-4-25 下午3:55:16
 * @ClassName: GaugesChart
 */
public class GaugesChart {
    // ---------------通用信息设置开始------------------
    private Activity activity;

    private SimpleChart chart;

    // 图形布局
    private LinearLayout chartLayout;

    // 文字信息（图形标题...）
    private String title = "";

    // 样式信息（大小、颜色...）
    private int titleFontSize = Level1Util.getSpSize(15);
    private int legendSize = Level1Util.getSpSize(10);
    private int yFontSize = Level1Util.getSpSize(12);
    private String textColor = "#FFFFFF";
    private String[] backWallColor = null;// 面板背景色
    private String[] chartColors = {"#03A3D5", "#D90000", "#996600", "#FFC014", "#ff41D6",
            "#AFD8F8", "#F6BD0F", "#8BBA00", "#FF8E46", "#008E8E", "#D64646", "#8E468E",
            "#588526", "#FFF468", "#008ED6", "#9D080D", "#A186BE", "#CC6600", "#FDC689",
            "#ABA000", "#F26D7D", "#FFF200", "#0054A6", "#F7941C", "#CC3300", "#006600",
            "#663300", "#6DCFF6"};// 图形颜色数组

    // 其他信息（图例位置、坐标轴标签格式...）
    private String legendPosition = "bottom";
    private String lLableFormat = null;
    // ----------------通用信息设置结束---------------------

    // ----------------特有信息设置开始---------------------
    private int totalAngle = 180; // 圆的弧度
    private int rotationAngle = 180; // 圆弧旋转角度
    private int centerWidth = 1;// 中心圆的水平大小
    private String centerColor = "#000000";
    private Paint blackPain;// 圈内背景
    private int axisPenWidth = 5;// 圈边框宽度

    private double minimum = 0;// 最小值
    private double maximum = 100;// 最大值
    private int minorTickCount = 4;// 刻度值之间的刻度线数量
    private int increment = 10;// 刻度间隔
    private String vArray[][] = new String[0][2];// 图形数据
    // ----------------特有信息设置结束---------------------

    public GaugesChart(Activity activity) {
        this.activity = activity;
    }

    public void setChartInfo(JSONObject chartObj, LinearLayout chartLayout) {
        this.chartLayout = chartLayout;
        this.chartLayout.setOrientation(LinearLayout.VERTICAL);
        this.minimum = chartObj.optDouble("minimum", 0);
        this.maximum = chartObj.optDouble("maximum", 100);
        this.minorTickCount = chartObj.optInt("minorTickCount", 4);
        this.increment = chartObj.optInt("increment", 10);
        if (chartObj.has("values")) {
            JSONArray valuesArray = chartObj.optJSONArray("values");
            this.vArray = new String[valuesArray.length()][2];
            for (int i = 0; i < valuesArray.length(); i++) {
                vArray[i][0] = valuesArray.optJSONObject(i).optString("dimName");
                vArray[i][1] = valuesArray.optJSONObject(i).optString("value");
            }
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitleFontSize(int titleFontSize) {
        this.titleFontSize = titleFontSize;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public void setBackWallColor(String[] backWallColor) {
        this.backWallColor = backWallColor;
    }

    public void setLLableFormat(String lLableFormat) {
        this.lLableFormat = lLableFormat;
    }

    public void setLegendPosition(String legendPosition) {
        this.legendPosition = legendPosition;
    }

    public void setYFontSize(int yFontSize) {
        this.yFontSize = yFontSize;
    }

    public void setLegendSize(int legendSize) {
        this.legendSize = legendSize;
    }

    public void setChartColors(String[] chartColors) {
        this.chartColors = chartColors;
    }

    public void setTotalAngle(int totalAngle) {
        this.totalAngle = totalAngle;
    }

    public void setRotationAngle(int rotationAngle) {
        this.rotationAngle = rotationAngle;
    }

    public void setCenterWidth(int centerWidth) {
        this.centerWidth = centerWidth;
    }

    public void setCenterColor(String centerColor) {
        this.centerColor = centerColor;
    }

    public void setAxisPenWidth(int axisPenWidth) {
        this.axisPenWidth = axisPenWidth;
    }

    public void setBlackPain(Paint blackPain) {
        this.blackPain = blackPain;
    }

    public void create() {
        chart = new SimpleChart(activity);
        chart.setScrollToLeft(false);
        chart.setScrollToRight(false);
        // 设置底部y坐标轴
        setAxisY(chart);
        // 设置坐标系背景
        setWall(chart);
        // 设置标题
        setTitle(chart, title);
        setBackPanel(chart);
        // 设置图例
        setLegend(chart);

        chart.getZoom().setAllow(false);

        for (int i = 0; i < vArray.length; i++) {
            GaugesNew gauges = new GaugesNew(chart.getChart());
            gauges.getPen().color = Color.fromCode(chartColors[i]);
            gauges.getPen().width = 3;
            gauges.setTotalAngle(totalAngle); // 圆的弧度
            gauges.setRotationAngle(rotationAngle); // 圆弧旋转角度
            gauges.setCircled(false);

            gauges.setHandStyle(com.steema.teechart.styles.HandStyle.LINE);// 指针样式
            gauges.getCenter().setStyle(com.steema.teechart.styles.PointerStyle.CIRCLE); // 中心圆样式

            Gradient oGradient = new Gradient();
            oGradient.setStartColor(Color.fromCode(centerColor));
            oGradient.setEndColor(Color.black);
            oGradient.setDirection(GradientDirection.VERTICAL);
            oGradient.setVisible(true);
            gauges.getCenter().getBrush().setVisible(true);
            gauges.getCenter().getBrush().setGradient(oGradient);

            if (i == 0) {
                if (blackPain != null) {
                    gauges.setBlackPain(blackPain);
                }
            }

            gauges.getCenter().setHorizSize(centerWidth); // 中心圆的水平大小
            gauges.getCenter().setVertSize(centerWidth);// 中心圆的垂直大小
            if (legendPosition.equalsIgnoreCase("none")) {
                gauges.setShowInLegend(false);
            } else {
                gauges.setShowInLegend(true);
            }
            gauges.setHandDistance(13);// 指针与表盘之间的长度
            gauges.setLabelsInside(true);
            gauges.setValue(Double.parseDouble(vArray[i][1])); // 指针指向的值
            gauges.setColor(Color.fromCode(chartColors[i]));
            gauges.setMinimum(minimum);// 最小值
            gauges.setMaximum(maximum);// 最大值
            gauges.setMinorTickDistance(0);
            gauges.setTitle(vArray[i][0]);
            chart.addSeries(gauges);
        }
        chartLayout.addView(chart, 400, 400);
    }

    /**
     * 设置左侧Y坐标轴
     *
     * @param chart
     */
    public void setAxisY(SimpleChart chart) {
        chart.getAxes().getLeft().getLabels().getFont().setSize(yFontSize);
        chart.getAxes().getLeft().getLabels().getFont().setColor(Color.fromCode(textColor));

        if (this.lLableFormat != null && this.lLableFormat.trim().length() > 0) {
            chart.getAxes().getLeft().getLabels().setValueFormat(this.lLableFormat);
        }

        chart.getAxes().getLeft().getAxisPen().setWidth(axisPenWidth);
        chart.getAxes().getLeft().getAxisPen().setDefaultVisible(true);
        chart.getAxes().getLeft().getAxisPen().setVisible(true);

        chart.getAxes().getLeft().setMinorTickCount(minorTickCount); // 刻度值之间的刻度线数量
        chart.getAxes().getLeft().getMinorTicks().setLength(10);// 刻度值之间的刻度线长短
        chart.getAxes().getLeft().getTicks().setLength(20); // 显示值的刻度线长短
        chart.getAxes().getLeft().setIncrement(increment);// 刻度值的间隔大小
        chart.getAxes().getLeft().setVisible(true);
    }

    /**
     * 设置背景面板
     *
     * @param chart
     */
    public void setBackPanel(SimpleChart chart) {
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
     *
     * @param chart
     */
    public void setLegend(SimpleChart chart) {
        // 设置图例边框颜色
        chart.getLegend().getPen().setColor(new Color(202, 202, 202));

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
        chart.getLegend().getFont().setSize(legendSize);
        chart.getLegend().getFont().setColor(Color.fromCode(textColor));
        chart.getLegend().getPen().setVisible(false);
    }

    /**
     * 设置 坐标面板的效果
     *
     * @param chart
     */
    public void setWall(SimpleChart chart) {
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
     *
     * @param chart
     * @param t
     */
    public void setTitle(SimpleChart chart, String t) {
        if (t == null || t.trim().length() == 0) {
            chart.getHeader().setVisible(false);
            return;
        }
        // 设置标题内容和字体
        chart.getHeader().setText(t);
        chart.getHeader().getFont().setSize(titleFontSize);
        chart.getHeader().getFont().setBold(true);
        chart.getHeader().getFont().setColor(Color.fromCode(textColor));
    }
}
