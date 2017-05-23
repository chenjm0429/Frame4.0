package com.steema.teechart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.steema.teechart.drawing.Color;
import com.steema.teechart.styles.Series;
import com.steema.teechart.styles.SeriesCollection;
import com.steema.teechart.styles.StringList;
import com.steema.teechart.styles.ValueList;
import com.steema.teechart.styles.ValuesLists;
import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.util.BitmapOperateUtil;

import java.text.DecimalFormat;

/***
 * 图形上面的层----------双指
 * @author wangsq
 *
 */
public class TowSingerTopView {
    private Context context;
    private Chart chart;
    private String markLineColor = "#000000";//线的颜色
    private int makrLineTextSize = 12;
    private String markLineTextColor = "#000000";
    private String markLineBackgroundColor = "#000000";
    private String markLineType = "V";
    private int startX = -1;//当前的x坐标
    private int endX = -1;//当前的x坐标

    public TowSingerTopView(Context context, Chart chart) {
        this.chart = chart;
        this.context = context;
    }

    /***
     * 长按事件触发
     * @param canvas
     * @param isLongPress
     */
    public void drawMuchSingerTick(Canvas canvas, int isLongPress) {
        SeriesCollection series = chart.getSeries();
        if (series.size() == 0 || isLongPress != 2) {
            return;
        }
        if (startX < chart.getChartRect().getLeft()
                || startX > chart.getChartRect().getRight()
                || endX < chart.getChartRect().getLeft()
                || endX > chart.getChartRect().getRight()) {
            return;
        }
        //获取显示图形的坐标
        int tickPos[] = new int[0];
        if (series.size() > 0) {
            Series series1 = series.getSeries(0);
            int first = 0;
            int last = series1.getLastVisible();
            tickPos = new int[last - first + 1];
            for (int x = first, j = 0; x < last + 1; x++, j++) {
                tickPos[j] = chart.getAxes().getBottom().calcXPosValue(x);
            }
        }
        double btwWidth = 5;
        if (tickPos.length > 1)
            btwWidth = (tickPos[1] - tickPos[0]) / 2;
        StringBuffer descStr = new StringBuffer();
        StringBuffer dataStr = new StringBuffer();
        StringBuffer pointStr = new StringBuffer();
        Paint pt = new Paint();
        pt.setStyle(Paint.Style.STROKE);
        pt.setStrokeWidth(1);
        pt.setColor(Color.parseColor(markLineColor));
        DecimalFormat df = new DecimalFormat("0.##");
        for (int k = 0; k < tickPos.length; k++) {
            if ((tickPos[k] >= startX - btwWidth && tickPos[k] <= startX + btwWidth) ||
                    (tickPos[k] >= endX - btwWidth && tickPos[k] <= endX + btwWidth)) {
                Series series1 = series.getSeries(0);
                if (series1.getActive()) {
                    df = new DecimalFormat(series1.getValueFormat());
                    ValuesLists vls = series1.getValuesLists();
                    ValueList vl = vls.getValueList(1);
                    StringList lbs = series1.getLabels();
                    if (vl.count == 0) {
                        continue;
                    }
                    int currId = k;

                    descStr.append(lbs.getString(currId));
                    descStr.append(",");
                    dataStr.append(vl.getValue(currId));
                    dataStr.append(",");
                }
                String dataAtrr[] = dataStr.toString().split(",");
                if (dataAtrr.length > 1) {
                    Double data2 = Double.parseDouble(dataAtrr[1]);
                    Double data1 = Double.parseDouble(dataAtrr[0]);
                    if (data2 > data1) {
                        pt.setColor(Color.parseColor(Level1Bean.plusColor));
                    } else if (data2 < data1) {
                        pt.setColor(Color.parseColor(Level1Bean.minusColor));
                    } else {
                        pt.setColor(Color.parseColor(Level1Bean.zeroColor));
                    }
                }
                canvas.drawLine(tickPos[k], chart.getChartRect().getTop(), tickPos[k], chart
                        .getChartRect().getBottom(), pt);
                pointStr.append(tickPos[k]);
                pointStr.append(",");
            }
        }

        if (descStr.length() > 0) {
            String descAtrr[] = descStr.toString().split(",");
            String dataAtrr[] = dataStr.toString().split(",");
            String pointAtrr[] = pointStr.toString().split(",");
            if (pointAtrr.length > 1) {
                Bitmap topBitMap = BitmapOperateUtil.getBitmapFromSimpleView(drawRoundRec(Integer
                                .parseInt(pointAtrr[0]), Integer.parseInt(pointAtrr[1]), descAtrr,
                        dataAtrr, df));
                int x1 = Integer.parseInt(pointAtrr[0]);
                int x2 = Integer.parseInt(pointAtrr[1]);
                int centerX = Math.abs((x2 - x1) / 2);
                centerX = Math.min(Math.abs(x1), Math.abs(x2)) + centerX;
                int xwidth = topBitMap.getWidth();
                int start;
                if (centerX - xwidth / 2 < chart.getChartRect().getLeft()) {
                    start = chart.getChartRect().getLeft();
                } else if (centerX + xwidth / 2 > chart.getChartRect().getRight()) {
                    start = chart.getChartRect().getRight() - xwidth;
                } else {
                    start = centerX - xwidth / 2;
                }
                canvas.drawBitmap(topBitMap, start, chart.getChartRect().getTop(), new Paint());
            }
        }
    }

    /***
     *
     * @param x1
     * @param x2
     * @param sAtrr----2个横坐标描述
     * @param data----2个横坐标的value值
     * @param df----format
     */
    public View drawRoundRec(int x1, int x2, String sAtrr[], String data[], DecimalFormat df) {
        Double data2 = Double.parseDouble(data[1]);
        Double data1 = Double.parseDouble(data[0]);
        String topStr = sAtrr[0] + "  " + sAtrr[1];
        DecimalFormat df1 = new DecimalFormat("0.00");
        String botomStr;
        if (data2 >= data1) {
            botomStr = "+" + df.format(data2 - data1);
        } else {
            botomStr = " " + df.format(data2 - data1);
        }

        double x;
        if (data1 != 0) {
            if (data1 > 0 && data2 > 0) {
                x = (data2 - data1) / data1 * 100;
                if (x >= 0) {
                    botomStr = botomStr + "   +" + df1.format(x) + "%";
                } else {
                    botomStr = botomStr + "   " + df1.format(x) + "%";
                }
            }
        }
        LinearLayout tView = new LinearLayout(context);
        tView.setOrientation(LinearLayout.VERTICAL);
        if (data2 < data1) {
            tView.setBackgroundColor(Color.parseColor(Level1Bean.minusColor));
        } else if (data2 > data1) {
            tView.setBackgroundColor(Color.parseColor(Level1Bean.plusColor));
        } else {
            tView.setBackgroundColor(Color.parseColor(Level1Bean.zeroColor));
        }
        tView.setPadding(5, 5, 5, 5);
        TextView txt = new TextView(context);
        txt.setTextSize(makrLineTextSize);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setTextColor(Color.BLACK.getRGB());
        txt.setText(topStr);
        tView.addView(txt, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams
                .WRAP_CONTENT);
        txt = new TextView(context);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setTextSize(makrLineTextSize);
        txt.setTextColor(Color.WHITE.getRGB());
        txt.setText(botomStr);
        tView.addView(txt, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams
                .WRAP_CONTENT);

        return tView;
    }

    public void setX1x2(int x1, int x2) {
        this.startX = x1;
        this.endX = x2;
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

    public void setMarkLineType(String markLineType) {
        this.markLineType = markLineType;
    }
}