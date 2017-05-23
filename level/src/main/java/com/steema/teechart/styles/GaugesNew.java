/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   Gauges.java

package com.steema.teechart.styles;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.steema.teechart.IBaseChart;
import com.steema.teechart.Rectangle;
import com.steema.teechart.axis.Axis;
import com.steema.teechart.drawing.ChartPen;
import com.steema.teechart.drawing.Color;
import com.steema.teechart.drawing.IGraphics3D;
import com.steema.teechart.drawing.Point;
import com.steema.teechart.events.ChangeEvent;
import com.steema.teechart.events.ChangeListener;
import com.steema.teechart.languages.Language;
import com.steema.teechart.misc.MathUtils;

import java.text.DecimalFormat;
import java.util.Random;

/***
 * 仪表盘
 */
public class GaugesNew extends Circular {
    /**
     *
     */
    private static final long serialVersionUID = 8847283794792458554L;
    private Paint blackPain = null;

    private class LinePoints {

        Point p0;
        Point p1;

        private LinePoints() {
            super();
        }
    }

    public GaugesNew(IBaseChart ibasechart) {
        super(ibasechart);
        fStyle = HandStyle.LINE;
        fLabelsInside = true;
        fCenter = new SeriesPointer(ibasechart, this);
        fCenter.getBrush().setSolid(true);
        fCenter.setColor(Color.BLACK);
        fCenter.setVisible(true);
        fCenter.setStyle(PointerStyle.CIRCLE);
        fCenter.setHorizSize(8);
        fCenter.setVertSize(8);
        fCenter.getGradient().setVisible(true);
        fCenter.getGradient().setStartColor(Color.WHITE);
        fCenter.getGradient().setEndColor(Color.BLACK);
        fEndPoint = new SeriesPointer(ibasechart, this);
        fEndPoint.setVisible(false);
        fEndPoint.getBrush().setSolid(true);
        fEndPoint.setColor(Color.WHITE);
        fEndPoint.setStyle(PointerStyle.CIRCLE);
        fEndPoint.setHorizSize(3);
        fEndPoint.setVertSize(3);
        fEndPoint.getGradient().setVisible(false);
        add(0);
    }

    public void addChangeListener(ChangeListener changelistener) {
        listenerList.add(java.util.EventListener.class, changelistener);
    }

    public void removeChangeListener(ChangeListener changelistener) {
        listenerList.remove(java.util.EventListener.class, changelistener);
    }

    protected void fireChange() {
        Object aobj[] = listenerList.getListenerList();
        for (int i = aobj.length - 2; i >= 0; i -= 2)
            if (aobj[i] == com.steema.teechart.events.ChangeListener.class)
                ((ChangeListener) aobj[i + 1]).stateChanged(new ChangeEvent(this));
    }

    private LinePoints calcLinePoints() {
        Rectangle ref = getCircleRect();
        float yADD = ref.getBottom() - 2 * ref.getTop();
        float xADD = 0;
//    	if(this.getCircled()){
//    		xADD=yADD;
//    	}
//    	if(Math.abs(getTotalAngle())>180){
        yADD = xADD = 0;
//    	}
        LinePoints linepoints = new LinePoints();
        double d = 0.017453292519943295D * ((360D - (getTotalAngle() - ((getValue() - getMinimum
                ()) * getTotalAngle()) / (getMaximum() - getMinimum()))) + (double)
                getRotationAngle());
        linepoints.p1 = calcPoint(d, iCenter, getXRadius() + xADD, getYRadius() + yADD / 2);
        int i = getHandDistance() + sizePointer(fEndPoint);
        if (i > 0)
            linepoints.p1 = MathUtils.pointAtDistance(iCenter, linepoints.p1, i);
        if (fCenter.getVisible() && fCenter.getStyle() == PointerStyle.CIRCLE)
            linepoints.p0 = MathUtils.pointAtDistance(linepoints.p1, iCenter, sizePointer
                    (fCenter) / 2);
        else
            linepoints.p0 = iCenter;
        return linepoints;
    }

    private static Point calcPoint(double d, Point point, double d1, double d2) {
        return new Point(point.x - MathUtils.round(d1 * Math.cos(d)), point.y - MathUtils.round
                (d2 * Math.sin(d)));
    }

    private static int sizePointer(SeriesPointer seriespointer) {
        int i = 0;
        if (seriespointer.getVisible()) {
            i = 2 * seriespointer.getVertSize();
            if (seriespointer.getPen().getVisible())
                i += seriespointer.getPen().getWidth();
        }
        return i;
    }

    /**
     * 绘制指针
     */
    private void drawValueLine() {
        LinePoints linepoints = calcLinePoints();
        IGraphics3D igraphics3d = chart.getGraphics3D();
        igraphics3d.getPen().assign(getPen());
        if (getHandStyle() == HandStyle.LINE) {
            igraphics3d.line(linepoints.p0, linepoints.p1);
        } else {
            double d = MathUtils.atan2(linepoints.p0.y - linepoints.p1.x, linepoints.p0.x -
                    linepoints.p1.y);
            igraphics3d.polygon(new Point[]{calcPoint(d, linepoints.p1, 4D, 4D), linepoints.p0,
                    calcPoint(d + 3.1415926535897931D, linepoints.p1, 4D, 4D)
            });
        }
        linepoints.p0 = MathUtils.pointAtDistance(iCenter, linepoints.p0, -sizePointer(fEndPoint)
                / 2);
        if (fEndPoint.getVisible()) {
            igraphics3d.getPen().assign(fEndPoint.getPen());
            igraphics3d.getGradient().assign(fEndPoint.getGradient());
            igraphics3d.getBrush().assign(fEndPoint.getBrush());
            fEndPoint.prepareCanvas(igraphics3d, fEndPoint.getBrush().getColor());
            fEndPoint.draw(linepoints.p0, fEndPoint.getBrush().getColor(), fEndPoint.getStyle());
        }
    }

    public ChartPen getPen() {
        if (pen == null)
            pen = new ChartPen(chart, Color.BLACK);
        return pen;
    }

    public HandStyle getHandStyle() {
        return fStyle;
    }

    public void setHandStyle(HandStyle handstyle) {
        if (fStyle != handstyle)
            fStyle = handstyle;
        repaint();
    }

    public double getTotalAngle() {
        return fAngle;
    }

    public void setTotalAngle(double d) {
        fAngle = setDoubleProperty(fAngle, d);
    }

    public int getHandDistance() {
        return fDistance;
    }

    public void setHandDistance(int i) {
        fDistance = setIntegerProperty(fDistance, i);
    }

    public double getValue() {
        if (getCount() == 0)
            add(0);
        return mandatory.value[0];
    }

    public void setValue(double d) {
        if (getValue() != d) {
            mandatory.value[0] = d;
            repaint();
            fireChange();
        }
    }

    public double getMinimum() {
        return fMin;
    }

    public void setMinimum(double d) {
        fMin = setDoubleProperty(fMin, d);
        setValue(Math.max(getMinimum(), getValue()));
    }

    public double getMaximum() {
        return fMax;
    }

    public void setMaximum(double d) {
        fMax = setDoubleProperty(fMax, d);
        setValue(Math.min(getMaximum(), getValue()));
    }

    public SeriesPointer getCenter() {
        return fCenter;
    }

    public SeriesPointer getEndPoint() {
        return fEndPoint;
    }

    public int getMinorTickDistance() {
        return fMinorDistance;
    }

    public void setMinorTickDistance(int i) {
        fMinorDistance = setIntegerProperty(fMinorDistance, i);
    }

    public boolean getLabelsInside() {
        return fLabelsInside;
    }

    public void setLabelsInside(boolean flag) {
        fLabelsInside = setBooleanProperty(fLabelsInside, flag);
    }

    protected void addSampleValues(int i) {
        Random random = new Random();
        setValue(getMinimum() + (getMaximum() - getMinimum()) * random.nextDouble());
    }

    protected void draw() {
        IGraphics3D igraphics3d = chart.getGraphics3D();
        Rectangle ref = getCircleRect();
        float yADD = 0;
        float xADD = 0;
//        if(Math.abs(180 - getRotationAngle())<=180&&Math.abs(getTotalAngle())
// <=180&&getRotationAngle()<=180){
//        	yADD=ref.getBottom()-2*ref.getTop();
//        	if(this.getCircled()){
//        		xADD=yADD;
//        	}
//        }
        if (blackPain != null) {
            blackPain.setStrokeWidth(0f);
            ((Canvas) igraphics3d.getGraphics()).drawArc(new RectF(ref.getLeft() - xADD, ref
                    .getTop(), ref.getRight() + xADD, ref.getBottom() + yADD), (float) (180 -
                    getRotationAngle()), (float) (-getTotalAngle()), true, blackPain);
        }


        if (getAxis() != null) {
            //getAxis().setIncrement(10D);
            iCenter = new Point(igraphics3d.getXCenter(), (int) (igraphics3d.getYCenter() + yADD
                    / 2));
            double d10 = getMaximum() - getMinimum();
            double d11 = getAxis().getIncrement();

            if (getAxis().getTicks().getVisible() || getAxis().getLabels().getVisible()) {
                igraphics3d.getFont().assign(getAxis().getLabels().getFont());
                igraphics3d.getPen().assign(getAxis().getTicks());
                igraphics3d.setBackColor(Color.TRANSPARENT);
                double d6 = getXRadius() + xADD - getAxis().getTicks().getLength();
                double d8 = getYRadius() + yADD / 2 - getAxis().getTicks().getLength();
                int j = igraphics3d.getFontHeight();
                if (d11 != 0.0D) {
                    double d3 = getMinimum();
                    do {
                        double d = getTotalAngle() - ((d3 - getMinimum()) * getTotalAngle()) / d10;
                        double d2 = (360D - d) + (double) getRotationAngle();
                        d = 0.017453292519943295D * d2;
                        Point point = calcPoint(d, iCenter, d6, d8);
                        Point point1 = calcPoint(d, iCenter, getXRadius() + xADD, getYRadius() +
                                yADD / 2);
                        if (getAxis().getTicks().getVisible())
                            igraphics3d.line(point, point1);
                        if (getAxis().getLabels().getVisible()) {
                            String s = (new DecimalFormat(getValueFormat())).format(d3);
                            if (!getLabelsInside())
                                point = calcPoint(d, iCenter, getXRadius() + xADD + j, getYRadius
                                        () + yADD / 2 + j);
                            point.x -= MathUtils.round((double) igraphics3d.textWidth(s) * 0.5D);
                            if (getLabelsInside()) {
                                if (d2 > 360D)
                                    d2 -= 360D;
                                if (d2 < 0.0D)
                                    d2 = 360D + d2;
                                point.x += MathUtils.round((double) igraphics3d.textWidth(s) *
                                        0.5D * Math.cos(0.017453292519943295D * d2));
                            }
                            if (d2 > 180D && d2 < 360D)
                                point.y += MathUtils.round((double) igraphics3d.getFontHeight() *
                                        Math.sin(0.017453292519943295D * d2));
                            igraphics3d.textOut(point.x, point.y, s);
                        }
                        d3 += d11;
                    }
                    while (getTotalAngle() >= 360D && d3 < getMaximum() || getTotalAngle() < 360D
                            && d3 <= getMaximum());
                }
            }
            if (getAxis().getMinorTicks().getVisible() && getAxis().getMinorTickCount() > 0) {
                igraphics3d.getPen().assign(getAxis().getMinorTicks());
                double d7 = getXRadius() + xADD - getAxis().getMinorTicks().getLength() -
                        getMinorTickDistance();
                double d9 = getYRadius() + yADD / 2 - getAxis().getMinorTicks().getLength() -
                        getMinorTickDistance();
                if (d11 != 0.0D) {
                    double d5 = d11 / (double) (getAxis().getMinorTickCount() + 1);
                    double d4 = getMinimum();
                    do {
                        for (int i = 1; i <= getAxis().getMinorTickCount(); i++) {
                            double d1 = getTotalAngle() - ((d4 + (double) i * d5) * getTotalAngle
                                    ()) / d10;
                            d1 = 0.017453292519943295D * ((360D - d1) + (double) getRotationAngle
                                    ());
                            igraphics3d.line(calcPoint(d1, iCenter, d7, d9), calcPoint(d1,
                                    iCenter, getXRadius() + xADD - getMinorTickDistance(),
                                    getYRadius() + yADD / 2 - getMinorTickDistance()));
                        }

                        d4 += d11;
                    } while (d4 <= getMaximum() - d11);
                }
            }
            if (getAxis().getVisible())
                drawAxis();
        }
        if (getPen().getVisible())
            drawValueLine();
        if (fCenter.getVisible()) {
            igraphics3d.getPen().assign(fCenter.getPen());
            igraphics3d.getGradient().assign(fCenter.getGradient());
            igraphics3d.getBrush().assign(fCenter.getBrush());
            fCenter.prepareCanvas(igraphics3d, fCenter.getBrush().getColor());
            fCenter.draw(iCenter.x, iCenter.y, fCenter.getBrush().getColor(), fCenter.getStyle());
        }
    }

    /**
     * 绘制外框
     */
    private void drawAxis() {
        IGraphics3D igraphics3d = chart.getGraphics3D();
        igraphics3d.getPen().assign(getAxis().getAxisPen());

        Rectangle ref = getCircleRect();
        float yADD = 0;
        float xAdd = 0;
//        if(Math.abs(180 - getRotationAngle())<=180&&Math.abs(getTotalAngle())
// <=180&&getRotationAngle()<=180){
//        	yADD=ref.getBottom()-2*ref.getTop();
//       	 	if(this.getCircled()){
//       	 		xAdd=yADD;
//       	 	}
//        }
//         igraphics3d.arc(getCircleRect(), 180 - getRotationAngle(), -getTotalAngle());
        ((Canvas) igraphics3d.getGraphics()).drawArc(new RectF(ref.getLeft() - xAdd, ref.getTop()
                , ref.getRight() + xAdd, ref.getBottom() + yADD), (float) (180 - getRotationAngle
                ()), (float) (-getTotalAngle()), true, igraphics3d.getPen().getPaint());
    }

    private Axis getAxis() {
        return getVertAxis();
    }

    public void setChart(IBaseChart ibasechart) {
        super.setChart(ibasechart);
        if (fCenter != null)
            fCenter.setChart(ibasechart);
        if (fEndPoint != null)
            fEndPoint.setChart(ibasechart);
        if (chart != null)
            chart.getAspect().setView3D(false);
    }

    public void prepareForGallery(boolean flag) {
        super.prepareForGallery(flag);
        chart.getAspect().setChart3DPercent(0);
        chart.getAxes().getLeft().getLabels().setVisible(false);
        getCenter().setVertSize(3);
        getCenter().setHorizSize(3);
        getPen().setColor(Color.BLUE);
        setHandDistance(5);
        setValue(70D);
    }

    public String getDescription() {
        return Language.getString("GalleryGauge");
    }

    private static final int HANDDISTANCE = 30;
    private double fAngle;
    private SeriesPointer fCenter;
    private int fDistance;
    private SeriesPointer fEndPoint;
    private double fMax;
    private double fMin;
    private int fMinorDistance;
    private HandStyle fStyle;
    private ChartPen pen;
    private boolean fLabelsInside;
    private transient Point iCenter;

    public Paint getBlackPain() {
        return blackPain;
    }

    public void setBlackPain(Paint blackPain) {
        this.blackPain = blackPain;
    }
}