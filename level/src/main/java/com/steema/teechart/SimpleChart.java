package com.steema.teechart;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.text.ClipboardManager;
import android.text.Html;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.Level1Util;
import com.steema.teechart.android.Graphics3DAndroid;
import com.steema.teechart.axis.Axes;
import com.steema.teechart.axis.Axis;
import com.steema.teechart.axis.AxisLabelAdapter;
import com.steema.teechart.axis.AxisLabelResolver;
import com.steema.teechart.drawing.Color;
import com.steema.teechart.drawing.Graphics3D;
import com.steema.teechart.drawing.IGraphics3D;
import com.steema.teechart.drawing.Image;
import com.steema.teechart.drawing.Point;
import com.steema.teechart.events.ChartDrawEvent;
import com.steema.teechart.events.ChartEvent;
import com.steema.teechart.events.ChartListener;
import com.steema.teechart.events.ChartMotionListener;
import com.steema.teechart.events.ChartMouseEvent;
import com.steema.teechart.events.ChartMouseListener;
import com.steema.teechart.events.ChartPaintListener;
import com.steema.teechart.events.ChartPrintListener;
import com.steema.teechart.events.EventListenerList;
import com.steema.teechart.events.FrameworkMouseEvent;
import com.steema.teechart.events.SeriesMouseEvent;
import com.steema.teechart.events.SeriesMouseListener;
import com.steema.teechart.exports.Exports;
import com.steema.teechart.imports.Imports;
import com.steema.teechart.languages.Language;
import com.steema.teechart.legend.Legend;
import com.steema.teechart.legend.LegendAdapter;
import com.steema.teechart.legend.LegendResolver;
import com.steema.teechart.printer.Printer;
import com.steema.teechart.styles.Area;
import com.steema.teechart.styles.BarNew;
import com.steema.teechart.styles.MarksStyle;
import com.steema.teechart.styles.Series;
import com.steema.teechart.styles.SeriesCollection;
import com.steema.teechart.styles.ValueList;
import com.steema.teechart.styles.ValuesLists;
import com.ztesoft.level1.util.BitmapOperateUtil;

public class SimpleChart extends View implements IChart, android.view.View.OnTouchListener {

    public interface Scrollable {
        ScrollResult isScrollable(Axis axis, ScrollResult scrollresult);
    }

    private Chart chart;
    private Graphics3DAndroid androidGraphics;
    private transient EventListenerList listenerList;
    public Point mousePosition;
    protected Scrollable scrollable;
    private static final transient LegendResolver defaultLegendResolver = new LegendAdapter();
    protected transient LegendResolver legendResolver;
    private static final transient AxisLabelResolver defaultAxisLabelResolver = new
            AxisLabelAdapter();
    protected transient AxisLabelResolver axisLabelResolver;
    private Handler timer;
    private double zoomDistance;
    private Context context;
    private LongCallBackEvent longCallBackEvent;
    private boolean muchSingleShow = false;//多指显示
    //长按事件是否被取消
    public static boolean isCancle = true;
    public int selectIndex = -1;//线默认在哪个节点上
    private String maskMethod = null;//外面画遮罩的回调函数名字
    //只针对柱图有效
    private int topDataTextSize = 10;
    private String topDataTextColor = "#000000";
    private String dataPosition = "none";
    private boolean dataIsPercent = false;
    private int dataTextSize = 8;
    private String dataTextColor = "#000000";
    private double[] stackedTotal;
    //Area特有的
    private String lineColor = null;
    private String pointColor = null;

    //单击事件要赋值
    private String clickShowText = null;
    private int curIndex = -1;
    private Series curSeries;

    //chart滑动到两边时是否把touch事件上传父容器
    private boolean dispatchEventWhenScrollToSide = true;

    public void setCurSeries(Series curSeries) {
        this.curSeries = curSeries;
    }

    public void setCurIndex(int curIndex) {
        this.curIndex = curIndex;
    }

    public void setClickShowText(String clickShowText) {
        this.clickShowText = clickShowText;
    }

    public void setStackedTotal(double[] stackedTotal) {
        this.stackedTotal = stackedTotal;
    }

    public void addChartListener(ChartListener chartlistener) {
        listenerList.add(com.steema.teechart.events.ChartListener.class, chartlistener);
    }

    public void removeChartListener(ChartListener chartlistener) {
        listenerList.remove(com.steema.teechart.events.ChartListener.class, chartlistener);
    }

    public void addChartMouseListener(ChartMouseListener chartmouselistener) {
        listenerList.add(com.steema.teechart.events.ChartMouseListener.class, chartmouselistener);
    }

    public void removeChartMouseListener(ChartMouseListener chartmouselistener) {
        listenerList.remove(com.steema.teechart.events.ChartMouseListener.class,
                chartmouselistener);
    }

    public void addChartMotionListener(ChartMotionListener chartmotionlistener) {
        listenerList.add(com.steema.teechart.events.ChartMotionListener.class, chartmotionlistener);
    }

    public void removeChartMotionListener(ChartMotionListener chartmotionlistener) {
        listenerList.remove(com.steema.teechart.events.ChartMotionListener.class,
                chartmotionlistener);
    }

    public void addChartPaintListener(ChartPaintListener chartpaintlistener) {
        listenerList.add(com.steema.teechart.events.ChartPaintListener.class, chartpaintlistener);
    }

    public void removeChartPaintListener(ChartPaintListener chartpaintlistener) {
        listenerList.remove(com.steema.teechart.events.ChartPaintListener.class,
                chartpaintlistener);
    }

    public void addChartPrintListener(ChartPrintListener chartprintlistener) {
        listenerList.add(com.steema.teechart.events.ChartPrintListener.class, chartprintlistener);
    }

    public void removeChartPrintListener(ChartPrintListener chartprintlistener) {
        listenerList.remove(com.steema.teechart.events.ChartPrintListener.class,
                chartprintlistener);
    }

    public void addSeriesMouseListener(SeriesMouseListener seriesmouselistener) {
        listenerList.add(com.steema.teechart.events.SeriesMouseListener.class, seriesmouselistener);
    }

    public void removeSeriesMouseListener(SeriesMouseListener seriesmouselistener) {
        listenerList.remove(com.steema.teechart.events.SeriesMouseListener.class,
                seriesmouselistener);
    }

    protected void fireSeriesClick(SeriesMouseEvent seriesmouseevent) {
        Object aobj[] = listenerList.getListenerList();
        for (int i = aobj.length - 2; i >= 0; i -= 2)
            if (aobj[i] == com.steema.teechart.events.SeriesMouseListener.class)
                ((SeriesMouseListener) aobj[i + 1]).seriesClicked(seriesmouseevent);
    }

    protected void fireChartAdded(ChartEvent chartevent) {
        Object obj = chartevent.getSource();
        Object aobj[] = listenerList.getListenerList();
        for (int i = aobj.length - 2; i >= 0; i -= 2) {
            if (aobj[i] != com.steema.teechart.events.ChartListener.class)
                continue;
            if (obj instanceof Series) {
                ((ChartListener) aobj[i + 1]).seriesAdded(chartevent);
                continue;
            }
        }
    }

    protected boolean fireChartClicked(ChartMouseEvent chartmouseevent) {
        boolean flag = false;
        Object aobj[] = listenerList.getListenerList();
        for (int i = aobj.length - 2; i >= 0; i -= 2)
            if (aobj[i] == com.steema.teechart.events.ChartMouseListener.class)
                switch (chartmouseevent.getClickedPart().getValue()) {
                    case 3: // '\003'
                        flag = true;
                        ((ChartMouseListener) aobj[i + 1]).axesClicked(chartmouseevent);
                        break;

                    case 8: // '\b'
                        flag = true;
                        ((ChartMouseListener) aobj[i + 1]).backgroundClicked(chartmouseevent);
                        break;

                    case 1: // '\001'
                        flag = true;
                        ((ChartMouseListener) aobj[i + 1]).legendClicked(chartmouseevent);
                        break;

                    case 4: // '\004'
                        flag = true;
                        ((ChartMouseListener) aobj[i + 1]).titleClicked(chartmouseevent);
                        break;

                    case 5: // '\005'
                        flag = true;
                        ((ChartMouseListener) aobj[i + 1]).titleClicked(chartmouseevent);
                        break;

                    case 6: // '\006'
                        flag = true;
                        ((ChartMouseListener) aobj[i + 1]).titleClicked(chartmouseevent);
                        break;

                    case 7: // '\007'
                        flag = true;
                        ((ChartMouseListener) aobj[i + 1]).titleClicked(chartmouseevent);
                        break;
                }

        return flag;
    }

    protected void fireChartMotion(ChartEvent chartevent) {
        Object aobj[] = listenerList.getListenerList();
        for (int i = aobj.length - 2; i >= 0; i -= 2)
            if (aobj[i] == com.steema.teechart.events.ChartMotionListener.class)
                switch (chartevent.getID()) {
                    case 54874546:
                        ((ChartMotionListener) aobj[i + 1]).scrolled(chartevent);
                        break;

                    case 54874548:
                        ((ChartMotionListener) aobj[i + 1]).zoomed(chartevent);
                        break;

                    case 54874547:
                        ((ChartMotionListener) aobj[i + 1]).unzoomed(chartevent);
                        break;
                }
    }

    protected void fireChartPaint(ChartDrawEvent chartdrawevent) {
        Object aobj[] = listenerList.getListenerList();
        for (int i = aobj.length - 2; i >= 0; i -= 2)
            if (aobj[i] == com.steema.teechart.events.ChartPaintListener.class)
                switch (chartdrawevent.getDrawPart()) {
                    default:
                        break;

                    case 1: // '\001'
                        if (chartdrawevent.getID() == 0x34551b5) {
                            ((ChartPaintListener) aobj[i + 1]).chartPainting(chartdrawevent);
                            break;
                        }
                        if (chartdrawevent.getID() == 0x34551b6)
                            ((ChartPaintListener) aobj[i + 1]).chartPainted(chartdrawevent);
                        break;

                    case 2: // '\002'
                        if (chartdrawevent.getID() == 0x34551b5)
                            ((ChartPaintListener) aobj[i + 1]).axesPainting(chartdrawevent);
                        break;

                    case 3: // '\003'
                        if (chartdrawevent.getID() == 0x34551b5) {
                            ((ChartPaintListener) aobj[i + 1]).seriesPainting(chartdrawevent);
                            break;
                        }
                        if (chartdrawevent.getID() == 0x34551b6)
                            ((ChartPaintListener) aobj[i + 1]).seriesPainted(chartdrawevent);
                        break;
                }
    }

    protected void fireChartPrint(ChartDrawEvent chartdrawevent) {
        Object aobj[] = listenerList.getListenerList();
        for (int i = aobj.length - 2; i >= 0; i -= 2)
            if (aobj[i] == com.steema.teechart.events.ChartPrintListener.class)
                ((ChartPrintListener) aobj[i + 1]).chartPrinted(chartdrawevent);
    }

    public SimpleChart(Context context) {
        this(context, null);
    }

    public SimpleChart(Context context, AttributeSet attributeset) {
        this(context, attributeset, 0);
    }

    public SimpleChart(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        listenerList = new EventListenerList();
        mousePosition = new Point();
        legendResolver = defaultLegendResolver;
        axisLabelResolver = defaultAxisLabelResolver;
        Language.isDesignTime = isDesignTime();
        chart = new Chart(this);
        chart.setParent(this);
        androidGraphics = new Graphics3DAndroid(chart);
        chart.setGraphics3D(androidGraphics);
        setOnTouchListener(this);
        getLegend().setUseMaxWidth(true);
//      wangsq添加
        this.context = context;
        topView = new LongTopView(context, chart);
        muchSingerView = new TowSingerTopView(context, chart);
        //长按线程
        mLongPressRunnable = new Runnable() {
            @Override
            public void run() {
                curStatus = 1;
                topView.setX(x);
                invalidate();
                isCancle = false;
            }
        };
    }

    public void paint() {
        Graphics3DAndroid graphics3dandroid;
        if (androidGraphics == null)
            graphics3dandroid = new Graphics3DAndroid(chart);
        else
            graphics3dandroid = androidGraphics;
        chart.paint(graphics3dandroid, getWidth(), getHeight());
    }

    Bitmap newb = null;
    LongTopView topView;
    TowSingerTopView muchSingerView;

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();

        if (curStatus == 1 || curStatus == 2) {
            //原始图形保存为图片
            if (newb == null) {
                getDrawingRect(rect);
                newb = Bitmap.createBitmap(rect.width(), rect.height(), Config.ARGB_8888);
                Canvas canvasNew = new Canvas(newb);
                androidGraphics.setGraphics(canvasNew);
                getDrawingRect(rect);
                chart.chartBounds.x = rect.left;
                chart.chartBounds.y = rect.top;
                chart.chartBounds.width = rect.width();
                chart.chartBounds.height = rect.height();
                chart.paint();
                drawTopData(canvasNew);
                canvasNew.save(Canvas.ALL_SAVE_FLAG);
                canvasNew.restore();//存储
            }
            Paint mPaint = new Paint();
            canvas.drawBitmap(newb, 0, 0, mPaint);
        } else {
            newb = null;
            androidGraphics.setGraphics(canvas);
            getDrawingRect(rect);
            chart.chartBounds.x = rect.left;
            chart.chartBounds.y = rect.top;
            chart.chartBounds.width = rect.width();
            chart.chartBounds.height = rect.height();
            chart.paint();
            drawTopData(canvas);
        }
        //点击事件---------------------弹出说明框
        if (clickShowText != null && curIndex != -1) {
            LinearLayout tView = new LinearLayout(context);
            tView.setPadding(5, 2, 5, 2);
            tView.setBackgroundColor(Color.fromCode(markLineBackgroundColor).getRGB());
            TextView txt = new TextView(context);
            txt.setTextColor(android.graphics.Color.BLACK);
            txt.setTextSize(12);
            txt.setText(Html.fromHtml(clickShowText));
            tView.addView(txt);
            Bitmap topBitMap = BitmapOperateUtil.getBitmapFromSimpleView(tView);
            int x = curSeries.calcXPos(curIndex);
            int y = curSeries.calcYPos(curIndex);
            int startx = x - topBitMap.getWidth() / 2;
            if (startx < chart.getChartRect().getLeft()) {
                startx = chart.getChartRect().getLeft();
            } else if (x + topBitMap.getWidth() / 2 > chart.getChartRect().getRight()) {
                startx = chart.getChartRect().getRight() - topBitMap.getWidth();
            }
            int starty = y - topBitMap.getHeight();
            if (y - topBitMap.getHeight() < 0) {
                starty = chart.getChartRect().getTop() - 5;
            }
            canvas.drawBitmap(topBitMap, startx, starty, new Paint());
        }
        //长按事件画线条
        topView.drawLongTick(canvas, curStatus);
        muchSingerView.drawMuchSingerTick(canvas, curStatus);
        if (maskMethod != null) {
            callMaskMethod(chart);
            maskMethod = null;//只要画一次
        }
    }

    public void drawTopData(Canvas canvas) {
        SeriesCollection series = chart.getSeries();
        if (lineColor != null) {
            Paint p = new Paint();
            p.setAntiAlias(true);
            p.setColor(Color.parseColor(lineColor));

            Paint p1 = new Paint();
            p1.setAntiAlias(true);
            p1.setColor(Color.parseColor(pointColor));
            for (int i = 0; i < series.size(); i++) {
                Series s = series.getSeries(i);
                if (s instanceof Area) {
                    int x = s.calcXPos(s.getFirstVisible());
                    int y = s.calcYPos(s.getFirstVisible());
                    canvas.drawCircle(x, y, Level1Util.dip2px(context, 6), p);
                    canvas.drawCircle(x, y, Level1Util.dip2px(context, 4), p1);

                    x = s.calcXPos(s.getLastVisible());
                    y = s.calcYPos(s.getLastVisible());
                    canvas.drawCircle(x, y, Level1Util.dip2px(context, 6), p);
                    canvas.drawCircle(x, y, Level1Util.dip2px(context, 4), p1);
                }
            }
        }
        for (int i = 0; i < series.size(); i++) {
            Series s = series.getSeries(i);
            if (s.getMarks().getVisible()) {
                if (s instanceof BarNew && s.getLabels().size() > 0) {
                    s.drawMarks();
                }
            }
            if (!"none".equalsIgnoreCase(dataPosition)) {
                for (int j = 0; j <= s.getLastVisible(); j++) {
                    if (s instanceof BarNew) {
                        s.getMarks().setStyle(MarksStyle.VALUE);
                        String mText = s.getMarkText(j);
                        if (mText.length() == 0) {
                            continue;
                        }
                        if (dataIsPercent && stackedTotal != null) {
                            ValuesLists vls = s.getValuesLists();
                            ValueList vl = vls.getValueList(1);
                            DecimalFormat df = new DecimalFormat("0.##%");
                            mText = df.format(vl.getValue(j) / stackedTotal[j]);
                        }
                        TextView txt = new TextView(context);
                        txt.setText(mText);
                        txt.setTextSize(dataTextSize);
                        txt.setTextColor(Color.parseColor(dataTextColor));
                        Bitmap topBitMap = BitmapOperateUtil.getBitmapFromSimpleView(txt);
                        int x = s.calcXPos(j);
                        int y = s.calcYPos(j);
                        int y1 = ((BarNew) s).getOriginPos(j);
                        if ("middle".equalsIgnoreCase(dataPosition)) {
                            canvas.drawBitmap(topBitMap, x + ((BarNew) s).barMargin() / ((
                                    (BarNew) s).getINumBars() * 2) - topBitMap.getWidth() / 2, y1
                                    + (y - y1) / 2 - topBitMap.getHeight() / 2, new Paint());
                        } else if (("top".equalsIgnoreCase(dataPosition))) {
                            canvas.drawBitmap(topBitMap, x + ((BarNew) s).barMargin() / ((
                                    (BarNew) s).getINumBars() * 2) - topBitMap.getWidth() / 2, y
                                    - topBitMap.getHeight(), new Paint());
                        } else if (("bottom".equalsIgnoreCase(dataPosition))) {
                            canvas.drawBitmap(topBitMap, x + ((BarNew) s).barMargin() / ((
                                    (BarNew) s).getINumBars() * 2) - topBitMap.getWidth() / 2, y1
                                    - topBitMap.getHeight(), new Paint());
                        }
                    }
                }
            }
        }
    }

    /***
     * 画遮罩层
     *
     * @param chart
     */
    private void callMaskMethod(Chart chart) {
        if (maskMethod != null) {
            try {
                Class yourClass = Class.forName(context.getClass().getName());
                //假设你要动态加载的类为YourClass   
                Class[] parameterTypes = new Class[1];//这里你要调用的方法只有一个参数   
                parameterTypes[0] = Chart.class;//参数类型为String   
                Method method = yourClass.getMethod(maskMethod, parameterTypes);
                //这里假设你的类为YourClass，而要调用的方法是methodName   
                method.invoke(context, chart);//调用方法   
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Cursor getTeeCursor() {
        return new Cursor(0);
    }

    public void doBeforeDrawAxes() {
        fireChartPaint(new ChartDrawEvent(this, 0x34551b5, 2));
    }

    public void doAfterDrawSeries() {
        fireChartPaint(new ChartDrawEvent(this, 0x34551b6, 3));
    }

    public void doBeforeDrawSeries() {
        fireChartPaint(new ChartDrawEvent(this, 0x34551b5, 3));
    }

    public void doAfterDraw() {
        fireChartPaint(new ChartDrawEvent(this, 0x34551b6, 1));
    }

    public void doBeforeDraw() {
        fireChartPaint(new ChartDrawEvent(this, 0x34551b5, 1));
    }

    public void doClickSeries(Object obj, Series series, int i, FrameworkMouseEvent
            frameworkmouseevent) {
        fireSeriesClick(new SeriesMouseEvent(series, 0x3a8dfcf6, i, frameworkmouseevent));
    }

    public void doAllowScroll(Axis axis, double d, ScrollResult scrollresult) {
        scrollresult.allow = true;
        if (scrollable != null)
            scrollresult = scrollable.isScrollable(axis, scrollresult);
    }

    public void setCursor(Cursor cursor) {
    }

    public void setOpaque(boolean flag) {
    }

    public void setToolTip(ToolTip tooltip, String s) {
        tooltip.setText(s);
    }

    public void refreshControl() {
        chart.isDirty = true;
        invalidate();
    }

    public void checkTitle(Title title, FrameworkMouseEvent frameworkmouseevent, ClickedParts
            clickedparts) {
        if (fireChartClicked(new ChartMouseEvent(title, 0x34551b5, clickedparts,
                frameworkmouseevent))) {
            chart.cancelMouse = true;
            chart.iClicked = chart.cancelMouse;
        }
        if (!chart.iClicked)
            chart.checkZoomPanning(frameworkmouseevent);
    }

    public void doClickLegend(Legend legend, FrameworkMouseEvent frameworkmouseevent) {

        int curIndex1 = legend.clicked(frameworkmouseevent.getPoint());
        if (curIndex1 != -1) {
            if (chart.getSeries(curIndex1) == curSeries) {
                curIndex = -1;
            }
        }
        if (fireChartClicked(new ChartMouseEvent(legend, 0x34551b5, ClickedParts.LEGEND,
                frameworkmouseevent))) {
            chart.cancelMouse = true;
            chart.iClicked = chart.cancelMouse;
        }
    }

    public void doScroll(Object obj) {
        fireChartMotion(new ChartEvent(obj, 0x34551b2));
    }

    public void doZoomed(Object obj) {
        fireChartMotion(new ChartEvent(obj, 0x34551b4));
    }

    public void doSetBuffered(boolean flag) {
    }

    public void doUnZoomed(Object obj) {
        fireChartMotion(new ChartEvent(obj, 0x34551b3));
    }

    public void doInvalidate() {
        refreshControl();
    }

    public Point pointToScreen(Point point) {
        return point;
    }

    public Chart getChart() {
        return chart;
    }

    public void showEditor() {
        getChart().setCancelMouse(true);
    }

    public String getText() {
        return chart != null ? chart.getHeader().getText() : "";
    }

    public void setText(String s) {
        if (chart != null)
            chart.getHeader().setText(s);
    }

    public Panel getPanel() {
        return chart.getPanel();
    }

    public Printer getPrinter() {
        return chart.getPrinter();
    }

    public Page getPage() {
        return chart.getPage();
    }

    public Legend getLegend() {
        return chart.getLegend();
    }

    public Header getHeader() {
        return chart.getHeader();
    }

    public Header getSubHeader() {
        return chart.getSubHeader();
    }

    public Footer getFooter() {
        return chart.getFooter();
    }

    public Zoom getZoom() {
        return chart.getZoom();
    }

    public Scroll getPanning() {
        return chart.getPanning();
    }

    public Footer getSubFooter() {
        return chart.getSubFooter();
    }

    public Aspect getAspect() {
        return chart.getAspect();
    }

    public IGraphics3D getCanvas() {
        return getGraphics3D();
    }

    public void setCanvas(Graphics3D graphics3d) {
        setGraphics3D(graphics3d);
        graphics3d.invalidate();
    }

    public IGraphics3D getGraphics3D() {
        return chart.getGraphics3D();
    }

    public void setGraphics3D(Graphics3D graphics3d) {
        chart.setGraphics3D(graphics3d);
    }

    public SeriesCollection getSeries() {
        return chart.getSeries();
    }

    public Walls getWalls() {
        return chart.getWalls();
    }

    public Axes getAxes() {
        return chart.getAxes();
    }

    public Exports getExport() {
        return chart.getExport();
    }

    public Imports getImport() {
        return chart.getImport();
    }

    public Series getSeries(int i) {
        return chart.getSeries(i);
    }

    public void setAxes(Axes axes) {
        chart.setAxes(axes);
    }

    public void setFooter(Footer footer) {
        chart.setFooter(footer);
    }

    public void setHeader(Header header) {
        chart.setHeader(header);
    }

    public void setLegend(Legend legend) {
        chart.setLegend(legend);
    }

    public void setSubFooter(Footer footer) {
        chart.setSubFooter(footer);
    }

    public void setSubHeader(Header header) {
        chart.setSubHeader(header);
    }

    public void setWalls(Walls walls) {
        chart.setWalls(walls);
    }

    public void setZoom(Zoom zoom) {
        chart.setZoom(zoom);
    }

    public void setSeries(int i, Series series) {
        chart.getSeries().setSeries(i, series);
    }

    public void setPanel(Panel panel) {
        chart.setPanel(panel);
    }

    public void setPage(Page page) {
        chart.setPage(page);
    }

    public void setPrinter(Printer printer) {
        chart.setPrinter(printer);
    }

    public void setPanning(Scroll scroll) {
        chart.setPanning(scroll);
    }

    public void setBackground(Color color) {
        chart.getPanel().setColor(color);
    }

    public void mouseDragged(FrameworkMouseEvent frameworkmouseevent) {
        mousePosition = frameworkmouseevent.getPoint();
    }

    public void mouseMoved(FrameworkMouseEvent frameworkmouseevent) {
        mousePosition = frameworkmouseevent.getPoint();
    }

    public Series addSeries(Series series) {
        Series series1 = chart.getSeries().add(series);
        fireChartAdded(new ChartEvent(series, 0x34551b1));
        return series1;
    }

    public boolean getAutoRepaint() {
        return chart.getAutoRepaint();
    }

    public void setAutoRepaint(boolean flag) {
        chart.setAutoRepaint(flag);
    }

    public void setHeight(int i) {
        setSize(getWidth(), i);
    }

    private void setSize(int i, int j) {
    }

    public void setWidth(int i) {
        setSize(i, getHeight());
    }

    public void removeAllSeries() {
        chart.getSeries().clear();
    }

    public int getSeriesCount() {
        return chart.getSeries().size();
    }

    public void setScrollable(Scrollable scrollable1) {
        scrollable = scrollable1;
    }

    public void removeScrollable() {
        scrollable = null;
    }

    public Image getBackgroundImage() {
        return chart.getWalls().getBack().getImage();
    }

    public void doDrawImage(IGraphics3D igraphics3d) {
        Image image1 = getBackgroundImage();
        if (image1 != null)
            igraphics3d.draw(chart.getChartRect().x, chart.getChartRect().y, image1);
    }

    public void setChart(Chart chart1) {
        if (chart1 != null) {
            chart = chart1;
            chart.setGraphics3D(androidGraphics);
            chart.setParent(this);
            invalidate();
        }
    }

    public void setLegendResolver(LegendResolver legendresolver) {
        legendResolver = legendresolver;
    }

    public void removeLegendResolver() {
        legendResolver = defaultLegendResolver;
    }

    public LegendResolver getLegendResolver() {
        return legendResolver;
    }

    public void doChartPrint() {
        fireChartPrint(new ChartDrawEvent(this, 0x34551b7, 1));
    }

    public Graphics3D checkGraphics() {
        return (Graphics3D) getGraphics3D();
    }

    public void doClickAxis(Axis axis, FrameworkMouseEvent frameworkmouseevent) {
        if (fireChartClicked(new ChartMouseEvent(axis, 0x34551b5, ClickedParts.AXIS,
                frameworkmouseevent)))
            chart.cancelMouse = true;
    }

    public void checkBackground(Object obj, FrameworkMouseEvent frameworkmouseevent) {
        if (fireChartClicked(new ChartMouseEvent(obj, 0x34551b5, ClickedParts.CHARTRECT,
                frameworkmouseevent))) {
            chart.cancelMouse = true;
            chart.iClicked = chart.cancelMouse;
        }
    }

    public void setAxisLabelResolver(AxisLabelResolver axislabelresolver) {
        axisLabelResolver = axislabelresolver;
    }

    public void removeAxisLabelResolver() {
        axisLabelResolver = defaultAxisLabelResolver;
    }

    public AxisLabelResolver getAxisLabelResolver() {
        return axisLabelResolver;
    }

    private void prepareGraphics() {
    }

    protected void processMouseEvent(FrameworkMouseEvent frameworkmouseevent) {
        chart.cancelMouse = false;
        if (!isDesignTime()) {
            prepareGraphics();
            if (frameworkmouseevent.getID() == 3)
                chart.mousePressed(frameworkmouseevent);
            else if (frameworkmouseevent.getID() == 4)
                chart.mouseReleased(frameworkmouseevent);
            else if (frameworkmouseevent.getID() == 7) {
                chart.getPanning().setActive(false);
                setCursor(chart.originalCursor);
            }
        }
    }

    protected void processMouseMotionEvent(FrameworkMouseEvent frameworkmouseevent) {
        chart.cancelMouse = false;
        prepareGraphics();
        Cursor cursor = chart.mouseMoved(frameworkmouseevent);
        if (cursor == null) {
            if (chart.originalCursor == null)
                setSuperCursor(null);
            setSuperCursor(chart.originalCursor);
        } else {
            setSuperCursor(cursor);
        }
    }

    private void setSuperCursor(Cursor cursor) {
    }

    public boolean checkClickSeries() {
        for (int i = 0; i < getSeriesCount(); i++)
            if (getSeries(i).hasClickEvents())
                return true;

        return false;
    }

    public boolean isDesignTime() {
        return isInEditMode();
    }

    public void timerExec(int i, Runnable runnable) {
        if (timer != null)
            timer.removeCallbacks(runnable);
        if (i != -1) {
            if (timer == null)
                timer = new Handler();
            timer.postDelayed(runnable, i);
        }
    }

    public void copyToClipboard(Object obj) {
        ClipboardManager clipboardmanager = (ClipboardManager) getContext().getSystemService
                ("clipboard");
        if (obj instanceof String)
            clipboardmanager.setText((String) obj);
    }

    public Chart xmlDecode(InputStream inputstream) {
        return null;
    }

    public Object getGraphics() {
        return androidGraphics;
    }

    public int getScreenHeight() {
        return Level1Bean.actualHeight;
    }

    public int getScreenWidth() {
        return Level1Bean.actualWidth;
    }

    public Object getControl() {
        return this;
    }

    public Image image(int i, int j) {
        Object obj = androidGraphics.getGraphics();
        Image image1 = new Image(i, j);
        Canvas canvas = new Canvas(image1.bitmap);
        androidGraphics.setGraphics(canvas);
        chart.paint(androidGraphics, i, j);

        androidGraphics.setGraphics(obj);
        return image1;
    }

    int y = 0;

    public boolean onTouch(View view, MotionEvent motionevent) {

        x = (int) motionevent.getX();
        y = (int) motionevent.getY();
        if (curStatus == 1) {
            topView.setX(x);
        }
        boolean flag = false;
        switch (motionevent.getAction()) {
            default:
                break;

            case MotionEvent.ACTION_POINTER_2_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (muchSingleShow) {
                    curStatus = 2;
                    removeCallbacks(mLongPressRunnable);
                    if (motionevent.getPointerCount() > 1) {
                        int x1 = (int) motionevent.getX(motionevent.getPointerId(0));
                        int x2 = (int) motionevent.getX(motionevent.getPointerId(1));
                        muchSingerView.setX1x2(x1, x2);
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_3_UP:
                break;

            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_POINTER_2_UP:
                if (muchSingleShow) {
                    curStatus = 0;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                mLastMotionY = y;
                isCancle = true;
                downTime = new Date().getTime();
                if (showMarkLine) {//触发长按事件
                    postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout());
                }
                motionevent.setLocation(motionevent.getX(), chart.getChartRect().getTop() + 1);
                triggerEvent(motionevent, 3);
                flag = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (curStatus == 1) {
                    isCancle = false;
                    Level1Bean.scrollToLeft = true;
                    Level1Bean.scrollToRight = true;
                    invalidate();
                    return true;
                } else if (curStatus == 2) {
                    if (motionevent.getPointerCount() > 1) {
                        int x1 = (int) motionevent.getX(motionevent.getPointerId(0));
                        int x2 = (int) motionevent.getX(motionevent.getPointerId(1));
                        muchSingerView.setX1x2(x1, x2);
                        invalidate();
                    }
                    return true;
                } else {
                    if (Math.abs(mLastMotionX - x) > TOUCH_SLOP
                            || Math.abs(mLastMotionY - y) > TOUCH_SLOP) {
                        //移动超过阈值，则表示移动了  
                        removeCallbacks(mLongPressRunnable);
                        isCancle = true;
                    }
                    if (Level1Bean.scrollToLeft
                            && Level1Bean.scrollToRight
                            && Math.abs(mLastMotionY - y) > TOUCH_SLOP) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }

                if (chart.getZoom().getZoomStyle() == Zoom.ZoomStyle.FULLCHART) {

    			/*  由于不支持多点触摸，所以删除
                    if(motionevent.getPointerCount() > 1)
                    {
                        double d = Math.hypot(motionevent.getX(1) - motionevent.getX(0), 
                        motionevent.getY(1) - motionevent.getY(0));
                        if(zoomDistance != 0.0D && d != zoomDistance)
                        {
                            int i = chart.aspect.getZoom();
                            i = (int)((long)i + Math.round(0.14999999999999999D * (d - 
                            zoomDistance)));
                            if(i < 1)
                                i = 1;
                            chart.aspect.setView3D(true);
                            chart.aspect.setZoom(i);
                        }
                        zoomDistance = d;
                    } else
    			 */
                    {
                        chart.zoom.setActive(false);
                        FrameworkMouseEvent frameworkmouseevent = new FrameworkMouseEvent(3, 0,
                                5, (int) motionevent.getX(), (int) motionevent.getY());
                        processMouseMotionEvent(frameworkmouseevent);
                    }
                } else {
                    if (motionevent.getSize() > 1.0F) {
                        double d1 = Math.hypot(motionevent.getHistoricalX(1) - motionevent
                                .getHistoricalX(0), motionevent.getHistoricalY(1) - motionevent
                                .getHistoricalY(0));
                        if (zoomDistance != 0.0D && d1 != zoomDistance) {
                            int j = chart.aspect.getZoom();
                            j += (int) (0.5D * (d1 - zoomDistance));
                            if (j < 1)
                                j = 1;
                            chart.aspect.setZoom(j);
                        }
                        zoomDistance = d1;
                    } else {
                        FrameworkMouseEvent frameworkmouseevent1 = new FrameworkMouseEvent(1, 0,
                                5, (int) motionevent.getX(), (int) motionevent.getY());
                        processMouseMotionEvent(frameworkmouseevent1);
                    }
                    flag = true;
                    if (scrollToLeft && !scrollToRight) {
                        curStatus = 0;
                    }
                    if (!scrollToLeft && scrollToRight) {
                        curStatus = 0;
                    }
                    break;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //释放了  
                removeCallbacks(mLongPressRunnable);
                isCancle = true;
                if (curStatus == 1) {
                    curStatus = 0;
                    if (longCallBackEvent != null) {
                        longCallBackEvent.onItemLongUp(topView.getCurIndex());
                    }
                    topView.setX(-1);
                    invalidate();
                } else if (curStatus == 2) {
                    if (muchSingleShow) {
                        curStatus = 0;
                        invalidate();
                    }
                } else {
                    if (new Date().getTime() - downTime < ViewConfiguration.getLongPressTimeout()
                            && isCancle) {
                        chart.getPanning().setActive(false);
                        motionevent.setLocation(x, y);
                        triggerEvent(motionevent, 3);
                    }
                }

                zoomDistance = 0.0D;
                triggerEvent(motionevent, 4);
                flag = true;
                break;
        }
        return flag;

    }

    //滑动图形时默认显示最新的图形
    private boolean scrollToLeft = false;
    private boolean scrollToRight = true;
    private boolean showMarkLine = false;//是否显示长按线

    private int mLastMotionX, mLastMotionY;
    //长按的runnable  
    private Runnable mLongPressRunnable;
    //移动的阈值  
    private static final int TOUCH_SLOP = 20;
    //当前的状态
    private int curStatus = 0;//0:正常状态 1：长按状态 2:双手指
    long downTime = 0;
    int x = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!dispatchEventWhenScrollToSide) {
            getParent().requestDisallowInterceptTouchEvent(true);
        } else {
            Level1Bean.scrollToLeft = scrollToLeft;
            Level1Bean.scrollToRight = scrollToRight;
            if ((Level1Bean.scrollToLeft && Level1Bean.scrollToRight) || !isCancle) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
        }
        return super.dispatchTouchEvent(event);
    }

    public void setScrollToLeft(boolean scrollToLeft) {
        this.scrollToLeft = scrollToLeft;
    }

    public void setScrollToRight(boolean scrollToRight) {
        this.scrollToRight = scrollToRight;
    }

    public boolean onTrackballEvent(MotionEvent motionevent) {
        return onTouch(this, motionevent);
    }

    private void triggerEvent(MotionEvent motionevent, int i) {
        FrameworkMouseEvent frameworkmouseevent = new FrameworkMouseEvent(1, 0, i, (int)
                motionevent.getX(), (int) motionevent.getY());
        processMouseEvent(frameworkmouseevent);
    }

    public boolean invertedRotation() {
        return chart.invertedRotation;
    }

    /***
     * 长按事件回调函数
     *
     * @author wangsq
     */
    public interface LongCallBackEvent {
        void onItemLongDown(int selIndex);//长按触发的事件

        void onItemLongUp(int selIndex);//释放触摸事件触发

        View drawOnCanvas(int selIndex);//长按时要显示的view
    }

    /***
     * 点击事件
     *
     * @author wangsq
     */
    public interface ChartClickListener {
        void onbgClick();

        void seriesClicked(int selIndex);
    }

    //属性
    public void setShowMarkLine(boolean showMarkLine) {
        this.showMarkLine = showMarkLine;
    }

    public void setMarkLineColor(String markLineColor) {
        topView.setMarkLineColor(markLineColor);
    }

    public void setMakrLineTextSize(int makrLineTextSize) {
        topView.setMakrLineTextSize(makrLineTextSize);
        muchSingerView.setMakrLineTextSize(makrLineTextSize);
    }

    public void setMarkLineTextColor(String markLineTextColor) {
        topView.setMarkLineTextColor(markLineTextColor);
    }

    private String markLineBackgroundColor = "#000000";

    public void setMarkLineBackgroundColor(String markLineBackgroundColor) {
        topView.setMarkLineBackgroundColor(markLineBackgroundColor);
        this.markLineBackgroundColor = markLineBackgroundColor;
    }

    public void setLongCallBackEvent(LongCallBackEvent longEvent) {
        this.longCallBackEvent = longEvent;
        topView.setLongCallBackEvent(longEvent);
    }

    public void setSelectIndex(int showIndex) {
        topView.setCurIndex(showIndex);
    }

    public void setMuchSingleShow(boolean muchSingleShow) {
        this.muchSingleShow = muchSingleShow;
    }

    public void setMaskMethod(String maskMethod) {
        this.maskMethod = maskMethod;
    }

    public void setTopDataTextSize(int topDataTextSize) {
        this.topDataTextSize = topDataTextSize;
    }

    public void setTopDataTextColor(String topDataTextColor) {
        this.topDataTextColor = topDataTextColor;
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

    public void setLineColor(String lineColor) {
        this.lineColor = lineColor;
    }

    public void setPointColor(String pointColor) {
        this.pointColor = pointColor;
    }

    public void setDispatchEventWhenScrollToSide(boolean dispatchEventWhenScrollToSide) {
        this.dispatchEventWhenScrollToSide = dispatchEventWhenScrollToSide;
    }
}
