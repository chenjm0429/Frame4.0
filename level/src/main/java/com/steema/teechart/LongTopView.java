package com.steema.teechart;

import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ztesoft.level1.Level1Util;
import com.steema.teechart.SimpleChart.LongCallBackEvent;
import com.steema.teechart.drawing.Color;
import com.steema.teechart.styles.Area;
import com.steema.teechart.styles.Bar;
import com.steema.teechart.styles.BarNew;
import com.steema.teechart.styles.Line;
import com.steema.teechart.styles.Series;
import com.steema.teechart.styles.SeriesCollection;
import com.steema.teechart.styles.StringList;
import com.steema.teechart.styles.ValueList;
import com.steema.teechart.styles.ValuesLists;
import com.ztesoft.level1.util.BitmapOperateUtil;

/***
 * 图形上面的层----------长按显示图形具体信息
 * @author wangsq
 *
 */
public class LongTopView {
    private Context context;
    private Chart chart;
    private String markLineColor;//线的颜色
    private int makrLineTextSize = 12;
    private String markLineTextColor = "#000000";
    private String markLineBackgroundColor = "#000000";
    private String markLineType = "V";
    private int x = -1;//当前的x坐标
    private LongCallBackEvent longCallBackEvent;
    private int curIndex = -1;//当前选择的第几列
    private boolean drawLine = false;//默认是否需要画线

    public LongTopView(Context context, Chart chart) {
        this.chart = chart;
        this.context = context;
    }

    /***
     * 长按事件触发
     * @param canvas
     * @param isLongPress
     */
    public void drawLongTick(Canvas canvas, int isLongPress) {
        SeriesCollection series = chart.getSeries();
        if (series.size() == 0) {
            return;
        }
        if (!drawLine) {
            if (isLongPress != 1) {
                return;
            }
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
        boolean showLine = true;
        for (int i = series.size() - 1; i >= 0; i--) {
            if (((series.getSeries(i)) instanceof Bar)
                    || ((series.getSeries(i)) instanceof BarNew)
                    || ((series.getSeries(i)) instanceof Line)
                    || ((series.getSeries(i)) instanceof Area)) {
            } else {
                showLine = false;
                break;
            }
        }
        if (showLine) {
            Paint pt = new Paint();
            pt.setStyle(Paint.Style.STROKE);
            pt.setStrokeWidth(1);
            pt.setColor(Color.parseColor(markLineColor));
            double btwWidth = 5;
            if (tickPos.length > 1) btwWidth = (tickPos[1] - tickPos[0]) / 2;
            int canvasIndex = -1;
            if (x == -1 && drawLine) {//初始画图形时
                int f = 0;
                int e = series.getSeries(0).getLastVisible();
                if (curIndex >= f && curIndex <= e) {
                    canvasIndex = curIndex - f;
                }
            } else {
                for (int k = 0; k < tickPos.length; k++) {
                    if (tickPos[k] >= x - btwWidth && tickPos[k] <= x + btwWidth) {
                        canvasIndex = k;
                        break;
                    }
                }
            }
            if (canvasIndex == -1) return;

            int cur = canvasIndex;
            //------------------------------------画线-----------------------------------
            canvas.drawLine(tickPos[canvasIndex], chart.getChartRect().getTop(),
                    tickPos[canvasIndex], chart.getChartRect().getBottom(), pt);
            //-------------------------------------画text--------------------------------
            View topView = null;
            if (longCallBackEvent != null) {
                topView = longCallBackEvent.drawOnCanvas(cur);
            }
            if (topView != null) {
                Bitmap topBitMap = BitmapOperateUtil.getBitmapFromSimpleView(topView);
                canvas.drawBitmap(topBitMap, tickPos[canvasIndex], chart.getChartRect().getTop(),
						new Paint());
            } else {
                Bitmap topBitMap = BitmapOperateUtil.getBitmapFromSimpleView(drawTopTextView
						(series, canvasIndex));
                if ("V".equals(markLineType)) {
                    int startx = tickPos[canvasIndex] - topBitMap.getWidth() / 2;
                    if (startx < chart.getChartRect().getLeft()) {
                        startx = chart.getChartRect().getLeft();
                    } else if (tickPos[canvasIndex] + topBitMap.getWidth() / 2 > chart
							.getChartRect().getRight()) {
                        startx = chart.getChartRect().getRight() - topBitMap.getWidth();
                    }
                    canvas.drawBitmap(topBitMap, startx, chart.getChartRect().getTop(), new Paint
							());
                } else if ("H".equals(markLineType)) {
                    int centerx = chart.getChartRect().getLeft() + (chart.getChartRect().getRight
							() - chart.getChartRect().getLeft()) / 2;
                    int startx = centerx - topBitMap.getWidth() / 2;
                    canvas.drawBitmap(topBitMap, startx, chart.getChartRect().getTop(), new Paint
							());
                }
            }
            //------------------------------------回调函数-------------------------------	
			
            if (cur != curIndex) {
                curIndex = canvasIndex;
                if (longCallBackEvent != null) {
                    longCallBackEvent.onItemLongDown(canvasIndex);
                }
            }
        }
    }

    /***
     *
     * @param series
     * @param selIndex---当前柱的序列
     * @return
     */
    public View drawTopTextView(SeriesCollection series, int selIndex) {
        LinearLayout tView = new LinearLayout(context);
        int p = Level1Util.dip2px(context, 5);
        tView.setPadding(p, p, p, p);
        tView.setBackgroundDrawable(Level1Util.MAIN_RADIO_BORD(context, Color.fromCode
				(markLineBackgroundColor).getRGB(), Color.fromCode(markLineColor).getRGB(), 1));
        String texts[] = new String[series.size() + 1];
        int colors[] = new int[series.size() + 1];
        String showStr = "";
        DecimalFormat df = new DecimalFormat("0.##");
        for (int i = 0; i < series.size(); i++) {
            Series series1 = series.getSeries(i);
            if (series1.getActive()) {
                df = new DecimalFormat(series1.getValueFormat());
                ValuesLists vls = series1.getValuesLists();
                ValueList vl = vls.getValueList(1);
                StringList lbs = series1.getLabels();
                if (vl.count == 0) {
                    continue;
                }
                if (i == 0) {
                    texts[0] = lbs.getString(selIndex);
                    colors[0] = Color.fromCode(markLineTextColor).getRGB();

                }
                int currId = selIndex;
                if (series1.getTitle() != null && series1.getTitle().trim().length() > 0) {
                    texts[i + 1] = " " + series1.getTitle() + "：" + df.format(vl.getValue(currId));
                } else {
                    texts[i + 1] = df.format(vl.getValue(currId));
                }
                colors[i + 1] = series1.getColor().getRGB();
                showStr += texts[i + 1];
            }
        }
        if ("V".equals(markLineType)) {
            tView.setOrientation(LinearLayout.VERTICAL);
            tView.setGravity(Gravity.LEFT);
            for (int i = 0; i < texts.length; i++) {
                TextView txt = new TextView(context);
                if (i == 0) {
                    txt.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                txt.setTextColor(colors[i]);
                txt.setTextSize(makrLineTextSize);
                txt.setText(texts[i]);
                if (texts[i] == null) {
                    continue;
                }
                tView.addView(txt, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        } else {
            TextView txt = new TextView(context);
            txt.setTextSize(makrLineTextSize);
            SpannableStringBuilder style = new SpannableStringBuilder(showStr);
            int start = 0;
            for (int i = 0; i < texts.length; i++) {
                if (texts[i] == null) {
                    continue;
                }
                style.setSpan(new ForegroundColorSpan(colors[i]), start, start + texts[i].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = start + texts[i].length();
            }
            txt.setMaxWidth((chart.getChartRect().getRight() - chart.getChartRect().getLeft()) - 10);
            txt.setText(style);
            tView.addView(txt);
        }
        return tView;
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

    public void setX(int x) {
        this.x = x;
    }

    public void setLongCallBackEvent(LongCallBackEvent longCallBackEvent) {
        this.longCallBackEvent = longCallBackEvent;
    }

    public int getCurIndex() {
        return curIndex;
    }

    /***
     * draw
     * @param curIndex
     */
    public void setCurIndex(int curIndex) {
        this.curIndex = curIndex;
        if (curIndex > -1) {
            drawLine = true;
        }
    }
}
