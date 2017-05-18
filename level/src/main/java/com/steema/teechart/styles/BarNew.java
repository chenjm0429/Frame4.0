// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Bar.java

package com.steema.teechart.styles;

import com.steema.teechart.IBaseChart;
import com.steema.teechart.Rectangle;
import com.steema.teechart.axis.Axis;
import com.steema.teechart.drawing.Color;
import com.steema.teechart.drawing.Graphics3D;
import com.steema.teechart.drawing.IGraphics3D;
import com.steema.teechart.drawing.Point;
import com.steema.teechart.languages.Language;
import com.steema.teechart.misc.Utils;

// Referenced classes of package com.steema.teechart.styles:
//            CustomBar, BarStyle, MultiBars, Margins, 
//            ValueList, SeriesMarks, MarksCallout, SeriesMarksPosition

public class BarNew extends CustomBarNew {

    public BarNew() {
        this((IBaseChart) null);
    }

    public BarNew(IBaseChart ibasechart) {
        super(ibasechart);
    }

    public String getDescription() {
        return Language.getString("GalleryBar");
    }

    public int getBarWidthPercent() {
        return barSizePercent;
    }

    public void setBarWidthPercent(int i) {
        setBarSizePercent(i);
    }

    protected int internalCalcMarkLength(int i) {
        return chart.getGraphics3D().getFontHeight();
    }

    protected boolean internalClicked(int i, Point point) {
        boolean flag = false;
        int j = calcXPos(i);
        if (point.x >= j && point.x <= j + iBarSize) {
            int k = calcYPos(i);
            int l = getOriginPos(i);
            if (l < k) {
                int i1 = l;
                l = k;
                k = i1;
            }
            BarStyle barstyle = getBarStyle();
            if (barstyle == BarStyle.INVPYRAMID)
                flag = Graphics3D.pointInTriangle(point, j, j + iBarSize, k, l);
            else if ((barstyle == BarStyle.PYRAMID) | (barstyle == BarStyle.CONE))
                flag = Graphics3D.pointInTriangle(point, j, j + iBarSize, l, k);
            else if (barstyle == BarStyle.ELLIPSE)
                flag = Graphics3D.pointInEllipse(point, new Rectangle(j, k, j + iBarSize, l));
            else
                flag = point.y >= k && point.y <= l;
        }
        return flag;
    }

    protected Rectangle calcBarBounds(int i) {
        Rectangle rectangle;
        if (i < getCount()) {
            int k = calcXPos(i);
            int l;
            if (barSizePercent == 100 && customBarSize == 0 && iMultiBar != MultiBars.SELFSTACK 
                    && iMultiBar != MultiBars.SIDE) {
                if (getHorizAxis().getInverted()) {
                    if (i > 0)
                        l = calcXPos(i - 1);
                    else
                        l = k + iBarSize;
                } else if (i < getCount() - 1)
                    l = calcXPos(i + 1);
                else
                    l = k + iBarSize;
            } else {
                l = k + iBarSize;
            }
            int j = calcYPos(i);
            int i1 = getOriginPos(i);
            rectangle = Utils.fromLTRB(k, j, l, i1);
            if (getPen().getVisible() && !chart.getAspect().getView3D())
                rectangle.width++;
        } else {
            rectangle = Utils.emptyRectangle();
        }
        return rectangle;
    }

    public void calcHorizMargins(Margins margins) {
        super.calcHorizMargins(margins);
        internalApplyBarMargin(margins);
    }

    public void calcVerticalMargins(Margins margins) {
        super.calcVerticalMargins(margins);
        int i = calcMarkLength(0);
        if (i > 0) {
            i++;
            if (bUseOrigin && super.getMinYValue() < dOrigin)
                if (getVertAxis().getInverted())
                    margins.min += i;
                else
                    margins.max += i;
            if (!bUseOrigin || super.getMaxYValue() > dOrigin)
                if (getVertAxis().getInverted())
                    margins.max += i;
                else
                    margins.min += i;
        }
    }

    public void drawValue(int i) {
        normalBarColor = getValueColor(i);
        if (normalBarColor != Color.EMPTY) {
            iBarBounds = calcBarBounds(i);
            int j = iBarBounds.getTop();
            if (iBarBounds.getBottom() > j)
                drawBar(i, j, iBarBounds.getBottom());
            else
                drawBar(i, iBarBounds.getBottom(), j);
        }
    }

    protected void drawTickLine(int i, BarStyle barstyle) {
        super.drawTickLine(i, barstyle);
        if ((barstyle == BarStyle.RECTANGLE) | (barstyle == BarStyle.RECTGRADIENT))
            chart.getGraphics3D().horizontalLine(getBarBounds().getLeft(), getBarBounds()
                    .getRight(), i, startZ);
        else if (barstyle == BarStyle.ARROW) {
            int j = getBarBounds().width / 4;
            chart.getGraphics3D().horizontalLine(getBarBounds().getLeft() + j, getBarBounds()
                    .getRight() - j, i, middleZ);
        }
        if (chart.getAspect().getView3D() && (barstyle == BarStyle.RECTANGLE) | (barstyle ==
                BarStyle.RECTGRADIENT)) {
            Point point = chart.getGraphics3D().calculate3DPosition(getBarBounds().getBottom(),
                    getBarBounds().getRight(), startZ);
            Point point2 = chart.getGraphics3D().calculate3DPosition(getBarBounds().getBottom(),
                    getBarBounds().getRight(), endZ);
            Point point4 = chart.getGraphics3D().calculate3DPosition(getBarBounds().getRight(),
                    getBarBounds().getTop(), endZ);
            if (Graphics3D.cull(point, point2, point4)) {
                Point point1 = chart.getGraphics3D().calculate3DPosition(getBarBounds().getLeft()
                        , getBarBounds().getBottom(), startZ);
                Point point3 = chart.getGraphics3D().calculate3DPosition(getBarBounds().getLeft()
                        , getBarBounds().getBottom(), endZ);
                Point point5 = chart.getGraphics3D().calculate3DPosition(getBarBounds().getLeft()
                        , getBarBounds().getTop(), endZ);
                if (Graphics3D.cull(point1, point3, point5))
                    chart.getGraphics3D().zLine(getBarBounds().getLeft(), i, startZ, endZ);
            } else {
                chart.getGraphics3D().zLine(getBarBounds().getRight(), i, startZ, endZ);
            }
        }
    }

    public void drawBar(int i, int j, int k) {
//    	if(!chart.getAspect().getView3D())
//        {
//    		Gradient gradient=new Gradient();
//    		gradient.setStartColor(normalBarColor.applyDark(128));
//    		gradient.setMiddleColor(normalBarColor);
//    		gradient.setEndColor(normalBarColor.applyDark(128));
//    		gradient.setVisible(true);
//    		gradient.setDirection(GradientDirection.HORIZONTAL);
//    		bBrush.setGradient(gradient);
//        }
        setPenBrushBar(normalBarColor);
        int l = getBarBoundsMidX();
        BarStyle barstyle = doGetBarStyle(i);
        IGraphics3D igraphics3d = chart.getGraphics3D();
        Rectangle rectangle = iBarBounds;
        if (chart.getAspect().getView3D()) {
            if (barstyle == BarStyle.RECTANGLE)
                igraphics3d.cube(rectangle.x, j, rectangle.getRight(), k, getStartZ(), getEndZ(),
                        bDark3D);
            else if (barstyle == BarStyle.PYRAMID)
                igraphics3d.pyramid(true, rectangle.x, j, rectangle.getRight(), k, getStartZ(),
                        getEndZ(), bDark3D);
            else if (barstyle == BarStyle.INVPYRAMID)
                igraphics3d.pyramid(true, rectangle.x, k, rectangle.getRight(), j, getStartZ(),
                        getEndZ(), bDark3D);
            else if (barstyle == BarStyle.CYLINDER)
                igraphics3d.cylinder(true, iBarBounds, getStartZ(), getEndZ(), bDark3D);
            else if (barstyle == BarStyle.ELLIPSE)
                igraphics3d.ellipse(iBarBounds, getMiddleZ());
            else if (barstyle == BarStyle.ARROW)
                igraphics3d.arrow(true, new Point(l, k), new Point(l, j), rectangle.width,
                        rectangle.width / 2, getMiddleZ());
            else if (barstyle == BarStyle.RECTGRADIENT) {
                igraphics3d.cube(rectangle.x, j, rectangle.getRight(), k, getStartZ(), getEndZ(),
                        bDark3D);
                if (igraphics3d.getSupportsFullRotation() || chart.getAspect().getOrthogonal())
                    doGradient3D(i, igraphics3d.calc3DPoint(rectangle.x, j, getStartZ()),
                            igraphics3d.calc3DPoint(rectangle.getRight(), k, getStartZ()));
            } else if (barstyle == BarStyle.CONE)
                igraphics3d.cone(true, iBarBounds, getStartZ(), getEndZ(), bDark3D, conePercent);
        } else if ((barstyle == BarStyle.RECTANGLE) | (barstyle == BarStyle.CYLINDER))
            barRectangle(normalBarColor, iBarBounds);
        else if ((barstyle == BarStyle.PYRAMID) | (barstyle == BarStyle.CONE)) {
            Point apoint[] = {
                    new Point(rectangle.x, k), new Point(l, j), new Point(rectangle.getRight(), k)
            };
            igraphics3d.polygon(apoint);
        } else if (barstyle == BarStyle.INVPYRAMID) {
            Point apoint1[] = {
                    new Point(rectangle.x, j), new Point(l, k), new Point(rectangle.getRight(), j)
            };
            igraphics3d.polygon(apoint1);
        } else if (barstyle == BarStyle.ELLIPSE)
            igraphics3d.ellipse(iBarBounds);
        else if (barstyle == BarStyle.ARROW)
            igraphics3d.arrow(true, new Point(l, k), new Point(l, j), rectangle.width, rectangle
                    .width / 2, getMiddleZ());
        else if (barstyle == BarStyle.RECTGRADIENT)
            doBarGradient(i, new Rectangle(rectangle.x, j, rectangle.getRight() - rectangle.x, k
                    - j));
        drawTickLines(j, k, barstyle);
    }

    public void calcFirstLastVisibleIndex() {
        firstVisible = -1;
        lastVisible = -1;
        if (getCount() > 0) {
            int i = getCount() - 1;
            if (calcVisiblePoints && notMandatory.getOrder() != ValueListOrder.NONE) {
                Rectangle rectangle = chart.getChartRect();
                double d = calcMinMaxValue(rectangle.x, rectangle.y, rectangle.getRight(),
                        rectangle.getBottom());
                firstVisible = 0;
                if (notMandatory.getFirst() <= d)
                    do {
                        if (notMandatory.value[firstVisible] >= d)
                            break;
                        firstVisible++;
                        if (firstVisible <= i)
                            continue;
                        firstVisible = -1;
                        break;
                    } while (true);
                if (firstVisible >= 0) {
                    double d1 = calcMinMaxValue(rectangle.getRight(), rectangle.getBottom(),
                            rectangle.x, rectangle.y);
                    if (notMandatory.getLast() <= d1) {
                        lastVisible = i;
                    } else {
                        for (lastVisible = firstVisible; lastVisible < i && notMandatory
                                .value[lastVisible] < d1; lastVisible++)
                            ;
                        for (; !drawBetweenPoints && lastVisible > 0 && notMandatory
                                .value[lastVisible] > d1; lastVisible--)
                            ;
                    }
                }
            } else {
                firstVisible = 0;
                lastVisible = i;
            }
        }
    }

    private double calcMinMaxValue(int i, int j, int k, int l) {
        Axis axis = yMandatory ? chart == null ? null : chart.getAxes().getBottom() : chart ==
                null ? null : chart.getAxes().getLeft();
        if (yMandatory)
            return axis.getInverted() ? axis.calcPosPoint(k) : axis.calcPosPoint(i);
        else
            return axis.getInverted() ? axis.calcPosPoint(j) : axis.calcPosPoint(l);
    }

    protected boolean moreSameZOrder() {
        return iMultiBar != MultiBars.SIDEALL ? super.moreSameZOrder() : false;
    }

    public int calcXPos(int i) {
        int j = 0;
        if (iMultiBar == MultiBars.SIDEALL)
            j = getHorizAxis().calcXPosValue(iPreviousCount + i) - iBarSize / 2;
        else if (iMultiBar == MultiBars.SELFSTACK) {
            j = super.calcXPosValue(getMinXValue()) - iBarSize / 2;
        } else {
            j = super.calcXPos(i);
            if (iMultiBar != MultiBars.NONE)
                j += Utils.round((double) iBarSize * ((double) iOrderPos - (double) iNumBars *
                        0.5D - 1.0D));
            else
                j -= iBarSize / 2;
        }
        return applyBarOffset(j);
    }

    public int calcYPos(int i) {
        int j = 0;
        if ((iMultiBar == MultiBars.NONE) | (iMultiBar == MultiBars.SIDE) | (iMultiBar ==
                MultiBars.SIDEALL)) {
            j = super.calcYPos(i);
        } else {
            double d = vyValues.value[i] + pointOrigin(i, false);
            if (iMultiBar == MultiBars.STACKED || iMultiBar == MultiBars.SELFSTACK) {
                j = calcYPosValue(d);
            } else {
                double d1 = pointOrigin(i, true);
                j = d1 == 0.0D ? 0 : calcYPosValue((d * 100D) / d1);
            }
        }
        return j;
    }

    protected void drawMark(int i, String s, SeriesMarksPosition seriesmarksposition) {
        int j = iBarSize / 2;
        int k = getMarks().getCallout().getLength() + getMarks().getCallout().getDistance();
        if (!getMarksOnBar()) {
            if (seriesmarksposition.arrowFrom.getY() > getOriginPos(i)) {
                k = -k - seriesmarksposition.height;
                seriesmarksposition.arrowFrom.y += getMarks().getCallout().getDistance();
            } else {
                seriesmarksposition.arrowFrom.y -= getMarks().getCallout().getDistance();
            }
            seriesmarksposition.leftTop.x += j;
            seriesmarksposition.leftTop.y -= k;
            seriesmarksposition.arrowTo.x += j;
            seriesmarksposition.arrowTo.y -= k;
            seriesmarksposition.arrowFrom.x += j;
            seriesmarksposition.arrowFrom.y--;
        } else {
            getMarks().getArrow().setVisible(false);
            seriesmarksposition.leftTop.x += j;
            int l = calcYPosValue(getYValues().getValue(i));
            int i1 = calcYPosValue(0.0D);
            if (getMarksLocation() == CustomBarNew.MarksLocation.Start) {
                if (getMarks().getAngle() == 90D || getMarks().getAngle() == 270D)
                    seriesmarksposition.leftTop.y -= (l - i1) + 10 + Utils.round((double)
                            getMarks().getFont().getSize() * 0.80000000000000004D);
                else
                    seriesmarksposition.leftTop.y -= (l - i1) + Utils.round(getMarks().getFont()
                            .getSize() / 2);
            } else if (getMarksLocation() == CustomBarNew.MarksLocation.Center) {
                if (getMarks().getAngle() == 90D || getMarks().getAngle() == 270D)
                    seriesmarksposition.leftTop.y = l - (l - i1) / 2 - getMarks().textWidth(i) / 2;
                else
                    seriesmarksposition.leftTop.y = l - (l - i1) / 2 - getMarks().getFont()
                            .getSize() / 2;
            } else if (getMarksLocation() == CustomBarNew.MarksLocation.End)
                if (getMarks().getAngle() == 90D || getMarks().getAngle() == 270D)
                    seriesmarksposition.leftTop.y += getMarks().textWidth(i);
                else
                    seriesmarksposition.leftTop.y += Math.round((double) getMarks().getFont()
                            .getSize() * 2.2000000000000002D);
        }
        if (getAutoMarkPosition())
            getMarks().antiOverlap(firstVisible, i, seriesmarksposition);
        super.drawMark(i, s, seriesmarksposition);
    }

    public void drawMarks() {
        int i = (int) (chart.getChartRect().getWidth() / (double) (4 * (getLastVisible() -
                getFirstVisible())));
        Rectangle rectangle = Utils.fromLTRB(chart.getAxes().getLeft().getPosAxis() - i, 0, chart
                .getChartRect().getRight() + chart.getAspect().getWidth3D() + i, chart
                .getChartBounds().getBottom());
        chart.getGraphics3D().clipRectangle(rectangle);
        super.drawMarks();
    }

    public int getOriginPos(int i) {
        return internalGetOriginPos(i, getVertAxis().iEndPos);
    }

    public double getMaxXValue() {
        if (iMultiBar == MultiBars.SELFSTACK)
            return getMinXValue();
        else
            return iMultiBar != MultiBars.SIDEALL ? super.getMaxXValue() : (iPreviousCount +
                    getCount()) - 1;
    }

    public double getMinXValue() {
        if (iMultiBar == MultiBars.SELFSTACK)
            return (double) getChart().getSeriesIndexOf(this);
        else
            return super.getMinXValue();
    }

    public double getMaxYValue() {
        return maxMandatoryValue(super.getMaxYValue());
    }

    public double getMinYValue() {
        return minMandatoryValue(super.getMinYValue());
    }
}