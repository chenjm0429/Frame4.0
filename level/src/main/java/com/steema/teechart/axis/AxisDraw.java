// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AxisDraw.java
package com.steema.teechart.axis;

import com.steema.teechart.*;
import com.steema.teechart.drawing.*;
import com.steema.teechart.misc.Utils;
import com.steema.teechart.styles.*;

// Referenced classes of package com.steema.teechart.axis:
//            NextAxisLabelValue, Axis, AxisLabelItem, AxisTitle, 
//            AxisLabels, AxisLabelResolver, AxisLabelStyle, AxisLabelsItems, 
//            GridPen, Axes, CustomAxes, TicksPen, 
//            AxisLinePen
public class AxisDraw {

    final class GetAxisSeriesLabelResults {

        boolean result;
        double value;
        String label;

        public GetAxisSeriesLabelResults() {
            super();
        }
    }

    final class IntRange {

        int first;
        int last;

        public IntRange() {
            this(0, 0);
        }

        public IntRange(int i, int j) {
            super();
            first = i;
            last = j;
        }
    }

    public final class TicksGridDraw {

        public void setChart(IBaseChart ibasechart) {
            c = ibasechart;
        }

        private int getLimit(boolean flag) {
            if (axis.getUsePartnerAxis() && axis.getPartnerAxis() != null)
                if (flag && !axis.getPartnerAxis().getInverted()
                        || !flag && axis.getPartnerAxis().getInverted())
                    return axis.getPartnerAxis().calcPosValue(axis.getPartnerAxis().getMaximum());
                else
                    return axis.getPartnerAxis().calcPosValue(axis.getPartnerAxis().getMinimum());

            if (axis.horizontal)
                if (flag)
                    return r.getTop();
                else
                    return r.getBottom();

            if (flag)
                return r.getRight() - 1;
            else
                return r.getLeft();
        }

        private int getZGridPosition() {
            return (int) ((double) a.getWidth3D() * axis.getGrid().getZPosition() * 0.01D);
        }

        private void drawGridLine(int i) {
            int j = r.getTop();
            if (chart.getAxes().getCustom().size() > 0)
                j = chart.getAxes().getCustom().getAxis(0).calcPosValue(chart.getAxes().getCustom
                        ().getAxis(0).getMaximum());
            if (i > axis.iStartPos && i < axis.iEndPos)
                if (axis.isDepthAxis) {
                    g.verticalLine(r.getLeft(), r.getTop(), r.getBottom(), i);
                    g.horizontalLine(r.getLeft(), r.getRight(), r.getBottom(), i);
                } else if (axis.horizontal) {
                    if (is3D) {
                        if (axis.getOtherSide())
                            g.verticalLine(i, r.getTop(), r.getBottom(), a.getWidth3D());
                        else if (c.getAxes().getDrawBehind()) {
                            g.zLine(i, axis.posAxis, 0, a.getWidth3D());
                            if (axis.horizontal && !axis.getOtherSide())
                                g.verticalLine(i, j, r.getBottom(), a.getWidth3D());
                            else
                                g.verticalLine(i, r.getTop(), r.getBottom(), a.getWidth3D());
                        } else {
                            g.verticalLine(i, r.getTop(), r.getBottom(), 0);
                        }
                    } else {
                        g.verticalLine(i, getLimit(true), getLimit(false));
                    }
                } else if (is3D) {
                    if (axis.getOtherSide())
                        g.horizontalLine(r.getLeft(), r.getRight(), i, a.getWidth3D());
                    else if (c.getAxes().getDrawBehind()) {
                        g.zLine(axis.posAxis, i, 0, a.getWidth3D());
                        if (!axis.hideBackGrid)
                            if (axis.horizontal)
                                g.verticalLine(i, r.getTop(), r.getBottom(), a.getWidth3D());
                            else
                                g.horizontalLine(r.getLeft(), r.getRight(), i, a.getWidth3D());
                        if (!axis.hideSideGrid) {
                            int k = Utils.round((double) a.getWidth3D() * axis.grid.getZPosition
                                    () * 0.01D);
                            if (k != a.getWidth3D())
                                if (axis.horizontal)
                                    g.zLine(i, axis.posAxis, k, a.getWidth3D());
                                else
                                    g.zLine(axis.posAxis, i, k, a.getWidth3D());
                        }
                    } else {
                        g.horizontalLine(r.getLeft(), r.getRight(), i, 0);
                    }
                } else {
                    g.horizontalLine(getLimit(false), getLimit(true), i);
                }
        }

        public void drawGrids(int i) {
            g.setPen(axis.getGrid());
            if (g.getPen().getColor().isEmpty())
                g.getPen().setColor(Color.WHITE_SMOKE);

            for (int j = 0; j < tmpNumTicks; j++) {
                if (0 != j % axis.getGrid().getDrawEvery())
                    continue;
                if (axis.getGrid().centered) {
                    if (j > 0)
                        drawGridLine((int) (0.5D * (double) (tmpTicks[j] + tmpTicks[j - 1])));
                } else {
                    drawGridLine(tmpTicks[j]);
                }
            }
        }

        private void internalDrawTick(int i, int j, int k) {
            if (axis.isDepthAxis) {
                if (axis.getOtherSide()) {
                    if (is3D)
                        g.horizontalLine(axis.posAxis + j, axis.posAxis + j + k, getDepthAxisPos
                                (), i);
                    else
                        g.horizontalLine(axis.posAxis + j, axis.posAxis + j + k, getDepthAxisPos());
                } else if (is3D)
                    g.horizontalLine(axis.posAxis - j, axis.posAxis - j - k, getDepthAxisPos(), i);
                else
                    g.horizontalLine(axis.posAxis - j, axis.posAxis - j - k, getDepthAxisPos());
            } else if (axis.getOtherSide()) {
                if (axis.horizontal) {
                    if (is3D)
                        g.verticalLine(i, axis.posAxis - j, axis.posAxis - j - k, axis.iZPos);
                    else
                        g.verticalLine(i, axis.posAxis - j, axis.posAxis - j - k);
                } else if (is3D)
                    g.horizontalLine(axis.posAxis + j, axis.posAxis + j + k, i, axis.iZPos);
                else
                    g.horizontalLine(axis.posAxis + j, axis.posAxis + j + k, i);
            } else {
                j += tmpWallSize;
                if (axis.horizontal) {
                    if (is3D)
                        g.verticalLine(i, axis.posAxis + j, axis.posAxis + j + k, axis.iZPos);
                    else
                        g.verticalLine(i, axis.posAxis + j, axis.posAxis + j + k);
                } else if (is3D)
                    g.horizontalLine(axis.posAxis - j, axis.posAxis - j - k, i, axis.iZPos);
                else
                    g.horizontalLine(axis.posAxis - j, axis.posAxis - j - k, i);
            }
        }

        private void drawAxisLine() {
            if (axis.isDepthAxis) {
                int i;
                if (axis.getOtherSide())
                    i = (r.getBottom() + c.getWalls().calcWallSize(c.getAxes().getBottom())) -
                            axis.iZPos;
                else
                    i = r.getTop() - axis.iZPos;
                g.line(axis.posAxis, i, axis.iStartPos, axis.posAxis, i, axis.iEndPos);
            } else if (axis.horizontal) {
                if (axis.getOtherSide())
                    g.horizontalLine(axis.iStartPos, axis.iEndPos, axis.posAxis, axis.iZPos);
                else
                    g.horizontalLine(axis.iStartPos - c.getWalls().calcWallSize(c.getAxes()
                            .getLeft()), axis.iEndPos, axis.posAxis + tmpWallSize, axis.iZPos);
            } else {
                int j = axis.getOtherSide() ? tmpWallSize : -tmpWallSize;
                g.verticalLine(axis.posAxis + j, axis.iStartPos, axis.iEndPos + c.getWalls()
                        .calcWallSize(c.getAxes().getBottom()), axis.iZPos);
            }
        }

        private void aProc(int i, boolean flag) {
            if (i > axis.iStartPos && i < axis.iEndPos)
                if (flag) {
                    ChartPen chartpen = axis.getMinorGrid();
                    drawGridLine(i);
                } else {
                    internalDrawTick(i, 1, axis.getMinorTicks().length);
                }
        }

        private void processMinorTicks(boolean flag) {
            double d3 = 1.0D / (double) (axis.getMinorTickCount() + 1);
            if (tmpNumTicks > 1) {
                if (!axis.getLogarithmic()) {
                    double d = 1.0D * (double) (tmpTicks[1] - tmpTicks[0]) * d3;
                    for (int i = 1; i <= axis.getMinorTickCount(); i++) {
                        aProc(tmpTicks[0] - (int) ((double) i * d), flag);
                        aProc(tmpTicks[tmpNumTicks - 1] + (int) ((double) i * d), flag);
                    }
                }
                label0:
                for (int j = 1; j <= tmpNumTicks - 1; j++) {
                    if (axis.getLogarithmic()) {
                        double d4 = axis.calcPosPoint(tmpTicks[j - 1]);
                        double d1 = (d4 * axis.getLogarithmicBase() - d4) * d3;
                        int l = 1;
                        do {
                            if (l >= axis.getMinorTickCount())
                                continue label0;
                            d4 += d1;
                            if (d4 > axis.iMaximum)
                                continue label0;
                            aProc(axis.calcPosValue(d4), flag);
                            l++;
                        } while (true);
                    }
                    double d2 = 1.0D * (double) (tmpTicks[j] - tmpTicks[j - 1]) * d3;
                    for (int k = 1; k <= axis.getMinorTickCount(); k++)
                        aProc(tmpTicks[j] - (int) ((double) k * d2), flag);
                }
            }
        }

        private void processTicks(ChartPen chartpen, int i, int j) {
            if (chartpen.getVisible() && j != 0) {
                g.setPen(chartpen);
                for (int k = 0; k < tmpNumTicks; k++)
                    internalDrawTick(tmpTicks[k], i, j);
            }
        }

        private void processMinor(ChartPen chartpen, boolean flag) {
            if (tmpNumTicks > 0 && chartpen.getVisible()) {
                g.setPen(chartpen);
                processMinorTicks(flag);
            }
        }

        public void drawTicksGrid(int ai[], int i, double d) {
            c = axis.chart;
            g = c.getGraphics3D();
            a = c.getAspect();
            is3D = a.getView3D();
            tmpTicks = ai;
            tmpNumTicks = i;
            r = c.getChartRect();
            g.getBrush().setVisible(false);
            tmpWallSize = c.getWalls().calcWallSize(axis);
            if (axis.getAxisPen().getVisible()) {
                g.setPen(axis.axispen);
                drawAxisLine();
            }
            processTicks(axis.getTicks(), 1, axis.getTicks().length);
            if (axis.getGrid().getVisible())
                drawGrids(ticks.length);
            processTicks(axis.getTicksInner(), -1, -axis.getTicksInner().length);
            processMinor(axis.getMinorTicks(), false);
            if (axis.minorGrid != null)
                processMinor(axis.getMinorGrid(), true);
        }

        private Axis axis;
        private int tmpTicks[];
        private int tmpWallSize;
        private IBaseChart c;
        private IGraphics3D g;
        private Aspect a;
        private boolean is3D;
        private Rectangle r;

        public TicksGridDraw(Axis axis1) {
            super();
            if (axis1 != null) {
                axis = axis1;
                setChart(axis.chart);
            }
        }
    }


    public AxisDraw(Axis axis1) {
        ticks = new int[2000];
        if (axis1 != null) {
            axis = axis1;
            setChart(axis.getChart());
        }
        drawTicksAndGrid = new TicksGridDraw(axis);
    }

    public AxisDraw() {
        this(null);
    }

    public int getNumTicks() {
        return tmpNumTicks;
    }

    private int getDepthAxisPos() {
        int i;
        if (axis.getOtherSide())
            i = chart.getChartRect().getBottom() - axis.calcZPos();
        else
            i = chart.getChartRect().getTop() + axis.calcZPos();
        return i;
    }

    private void drawAxisTitle() {
        if (axis.axisTitle != null && axis.axisTitle.getVisible() && axis.axisTitle.getCaption()
                .length() != 0) {
            Rectangle rectangle = axis.getChart().getChartRect();
            if (axis.isDepthAxis)
                axis.drawTitle(axis.posTitle, rectangle.getBottom());
            else if (axis.horizontal)
                axis.drawTitle(axis.iCenterPos, axis.posTitle);
            else
                axis.drawTitle(axis.posTitle, axis.iCenterPos);
        }
    }

    private void addTick(int i) {
        ticks[tmpNumTicks] = i;
        tmpNumTicks++;
    }

    private void internalDrawLabel(boolean flag) {
        int i = axis.calcPosValue(tmpValue);
        if (axis.getLabels().bOnAxis || i > axis.iStartPos && i < axis.iEndPos) {
            if (!axis.getTickOnLabelsOnly())
                addTick(i);
            if (axis.getLabels().getVisible())
                drawThisLabel(i, axis.getLabels().labelValue(tmpValue), null);
        }
        if (flag)
            tmpValue = axis.incDecDateTime(false, tmpValue, iIncrement, tmpWhichDatetime);
    }

    private void drawThisLabel(int i, String s, TextShape textshape) {
        if (axis.getTickOnLabelsOnly())
            addTick(i);
        IBaseChart ibasechart = axis.chart;
        IGraphics3D igraphics3d = ibasechart.getGraphics3D();
        igraphics3d.setFont(textshape != null ? textshape.getFont() : axis.getLabels().getFont());
        igraphics3d.getBrush().setVisible(false);
        if (axis.isDepthAxis) {
            igraphics3d.setTextAlign(axis.getDepthAxisAlign());
            int l = i;
            if (ibasechart.getAspect().getRotation() == 360 || ibasechart.getAspect()
                    .getOrthogonal())
                l += igraphics3d.getFontHeight() / 2;
            int j;
            if (axis.getOtherSide())
                j = axis.getLabels().position;
            else
                j = axis.getLabels().position - 2 - chart.getGraphics3D().textWidth("W") / 2;
            igraphics3d.textOut(j, getDepthAxisPos(), l, s);
        } else {
            int k;
            if (axis.getLabels().getAlternate()) {
                if (tmpAlternate)
                    k = axis.getLabels().position;
                else if (axis.horizontal) {
                    if (axis.getOtherSide())
                        k = axis.getLabels().position - igraphics3d.getFontHeight();
                    else
                        k = axis.getLabels().position + igraphics3d.getFontHeight();
                } else if (axis.getOtherSide())
                    k = axis.getLabels().position + axis.maxLabelsWidth();
                else
                    k = axis.getLabels().position - axis.maxLabelsWidth();
                tmpAlternate = !tmpAlternate;
            } else {
                k = axis.getLabels().position;
            }
            if (axis.horizontal)
                axis.drawAxisLabel(i, k, axis.getLabels().iAngle, s, textshape);
            else
                axis.drawAxisLabel(k, i, axis.getLabels().iAngle, s, textshape);
        }
    }

    private static double intPower(double d, int i) {
        return Utils.pow(d, i);
    }

    private static double logBaseN(double d, double d1) {
        return Utils.log(d1, d);
    }

    private static DateTime roundDate(DateTime datetime, int i) {
        if (datetime.toDouble() <= 0.0D)
            return datetime;
        int j = datetime.getYear();
        int k = datetime.getMonth();
        int l = datetime.getDay();
        switch (i) {
            default:
                break;

            case 19: // '\023'
                if (l >= 15)
                    l = 15;
                else
                    l = 1;
                break;

            case 20: // '\024'
            case 21: // '\025'
            case 22: // '\026'
            case 23: // '\027'
            case 24: // '\030'
                l = 1;
                break;

            case 25: // '\031'
                l = 1;
                k = 1;
                break;
        }
        return new DateTime(j, k, l);
    }

    private void doDefaultLabels() {
        double d = 0.0D;
        tmpValue = axis.iMaximum / iIncrement;
        if (Math.abs(axis.iRange / iIncrement) < 10000D) {
            if (axis.iAxisDateTime && axis.getLabels().getExactDateTime()
                    && tmpWhichDatetime != 26 && tmpWhichDatetime > 15)
                tmpValue = roundDate(new DateTime(axis.iMaximum), tmpWhichDatetime).toDouble();
            else if (Double.compare(axis.iMinimum, axis.iMaximum) == 0
                    || !axis.getLabels().getRoundFirstLabel())
                tmpValue = axis.iMaximum;
            else
                tmpValue = iIncrement * (double) (int) tmpValue;
            for (; tmpValue > axis.iMaximum; tmpValue = axis.incDecDateTime(false, tmpValue,
                    iIncrement, tmpWhichDatetime)) {
            }
            
            if (axis.iRangezero)
                internalDrawLabel(false);
            else if (Math.abs(axis.iMaximum - axis.iMinimum) < axis.iMinAxisIncrement || Double
                    .compare(tmpValue, tmpValue - iIncrement) == 0) {
                internalDrawLabel(false);
            } else {
                for (double d1 = (axis.iMinimum - axis.iMinAxisIncrement) / (1.0D + axis
                        .iMinAxisIncrement); tmpValue >= d1; )
                    internalDrawLabel(true);
            }
        }
    }

    private void doDefaultLogLabels() {
        if (axis.iMinimum != axis.iMaximum) {
            if (axis.iMinimum <= 0.0D) {
                if (axis.iMinimum == 0.0D)
                    axis.iMinimum = 0.10000000000000001D;
                else
                    axis.iMinimum = 1E-010D;
                tmpValue = axis.iMinimum;
            } else {
                tmpValue = intPower(axis.getLogarithmicBase(), Utils.round(logBaseN(axis
                        .getLogarithmicBase(), axis.iMinimum)));
            }
            boolean flag = axis.minorGrid != null && axis.minorGrid.getVisible();
            if (flag) {
                double d = tmpValue;
                if (d >= axis.iMinimum)
                    d = intPower(axis.getLogarithmicBase(), Utils.round(logBaseN(axis
                            .getLogarithmicBase(), axis.iMinimum)) - 1);
                if (d < axis.iMinimum)
                    addTick(axis.calcPosValue(d));
            }
            if (axis.getLogarithmicBase() > 1.0D)
                for (; tmpValue <= axis.iMaximum; tmpValue *= axis.getLogarithmicBase())
                    if (tmpValue >= axis.iMinimum)
                        internalDrawLabel(false);

            if (flag && tmpValue > axis.iMaximum)
                addTick(axis.calcPosValue(tmpValue));
        }
    }

    private void doNotCustomLabels() {
        if (axis.getLogarithmic() && axis.getIncrement() == 0.0D)
            doDefaultLogLabels();
        else
            doDefaultLabels();
    }

    private void doCustomLabels() {
        boolean flag = false;
        tmpValue = axis.iMinimum;
        int i = 0;
        NextAxisLabelValue nextaxislabelvalue = new NextAxisLabelValue();
        nextaxislabelvalue.setStop(true);
        do {
            if (axis.chart.getParent() != null) {
                nextaxislabelvalue = axis.chart.getParent().getAxisLabelResolver().getNextLabel
                        (axis, i, nextaxislabelvalue);
                tmpValue = nextaxislabelvalue.getLabelValue();
            }
            if (nextaxislabelvalue.getStop()) {
                if (i == 0)
                    doNotCustomLabels();
                return;
            }
            flag = tmpValue >= axis.iMinimum - 9.9999999999999995E-008D && tmpValue <= axis
                    .iMaximum + 9.9999999999999995E-008D;
            if (flag)
                internalDrawLabel(false);
            i++;
        } while (flag || i < 2000 || !nextaxislabelvalue.getStop());
    }

    private IntRange calcFirstLastAllSeries(Rectangle rectangle) {
        IntRange intrange = new IntRange(0x7fffffff, -1);
        for (int i = 0; i < axis.iSeriesList.size(); i++) {
            Series series = axis.iSeriesList.getSeries(i);
            series.calcFirstLastVisibleIndex();
            if (series.getFirstVisible() < intrange.first && series.getFirstVisible() != -1)
                intrange.first = series.getFirstVisible();
            if (series.getLastVisible() > intrange.last)
                intrange.last = series.getLastVisible();
        }

        return intrange;
    }

    private void calcAllSeries() {
        if (axis.iSeriesList == null)
            axis.iSeriesList = new SeriesCollection(chart);
        axis.iSeriesList.clear();
        for (int i = 0; i < axis.chart.getSeriesCount(); i++) {
            Series series = axis.chart.getSeries(i);
            if (series.getActive() && series.associatedToAxis(axis))
                axis.iSeriesList.internalAdd(series);
        }
    }

    private GetAxisSeriesLabelResults getAxisSeriesLabel(int i) {
        GetAxisSeriesLabelResults getaxisserieslabelresults = new GetAxisSeriesLabelResults();
        getaxisserieslabelresults.result = false;
        for (int j = 0; j < axis.iSeriesList.size(); j++) {
            Series series = axis.iSeriesList.getSeries(j);
            if (i < series.getFirstVisible() || i > series.getLastVisible())
                continue;
            if (tmpLabelStyle == AxisLabelStyle.MARK)
                getaxisserieslabelresults.label = series.getValueMarkText(i);
            else if (tmpLabelStyle == AxisLabelStyle.TEXT)
                getaxisserieslabelresults.label = series.getLabels().getString(i);
            if (axis.chart.getParent() != null)
                getaxisserieslabelresults.label = axis.chart.getParent().getAxisLabelResolver()
                        .getLabel(axis, series, i, getaxisserieslabelresults.label);
            if (getaxisserieslabelresults.label.length() == 0)
                continue;
            getaxisserieslabelresults.result = true;
            if (axis.horizontal)
                getaxisserieslabelresults.value = series.getXValues().value[i];
            else
                getaxisserieslabelresults.value = series.getYValues().value[i];
            break;
        }

        return getaxisserieslabelresults;
    }

    private void depthAxisLabels() {
        if (axis.chart.countActiveSeries() > 0) {
            for (int i = (int) axis.iMinimum; (double) i <= axis.iMaximum; i++) {
                int j = axis.calcYPosValue(axis.iMaximum - (double) i - 0.5D);
                if (!axis.getTickOnLabelsOnly())
                    addTick(j);
                if (!axis.getLabels().getVisible())
                    continue;
                String s = axis.chart.getSeriesTitleLegend(i, true);
                if (axis.chart.getParent() != null)
                    s = axis.chart.getParent().getAxisLabelResolver().getLabel(axis, null, i, s);
                drawThisLabel(j, s, null);
            }

        }
    }

    private void axisLabelsSeries(Rectangle rectangle) {
        boolean flag = false;
        String s = "";
        double d = 0.0D;
        calcAllSeries();
        IntRange intrange = calcFirstLastAllSeries(rectangle);
        if (intrange.first != 0x7fffffff) {
            int l = -1;
            int i1 = 0;
            boolean flag2 = axis.horizontal;
            int j1;
            switch (axis.getLabels().iAngle) {
                case 90: // 'Z'
                case 270:
                    flag2 = !flag2;
                    // fall through

                default:
                    j1 = intrange.first;
                    break;
            }
            for (; j1 <= intrange.last; j1++) {
                GetAxisSeriesLabelResults getaxisserieslabelresults = getAxisSeriesLabel(j1);
                if (!getaxisserieslabelresults.result)
                    continue;
                double d1 = getaxisserieslabelresults.value;
                String s1 = getaxisserieslabelresults.label;
                if (d1 < axis.iMinimum || d1 > axis.iMaximum)
                    continue;
                int i = axis.calcPosValue(d1);
                if (!axis.getTickOnLabelsOnly())
                    addTick(i);
                if (!axis.getLabels().getVisible() || s1.length() == 0)
                    continue;
                MultiLine multiline = axis.chart.multiLineTextWidth(s1);
                int k = multiline.width;
                int j = multiline.count;
                if (!flag2)
                    k = chart.getGraphics3D().getFontHeight() * j;
                if (axis.getLabels().getVisibilities() != null) {
                    if ((axis.getLabels().getVisibilities())[j1]) {
                        drawThisLabel(i, s1, null);
                        l = i;
                        i1 = k / 2;
                    } else {
                        continue;
                    }
                } else {

                    if (axis.getLabels().iSeparation != 0 && l != -1) {
                        k += (int) (0.02D * (double) k * (double) axis.getLabels().iSeparation);
                        k = (int) ((double) k * 0.5D);
                        boolean flag1;
                        if (i >= l)
                            flag1 = i - k >= l + i1;
                        else
                            flag1 = i + k <= l - i1;
                        if (flag1) {
                            drawThisLabel(i, s1, null);
                            l = i;
                            i1 = k;
                        }
                    } else {
                        drawThisLabel(i, s1, null);
                        l = i;
                        i1 = k / 2;
                    }
                }
            }

            axis.iSeriesList.clear(false);
        }
    }

    private void drawCustomLabels() {
        for (int i = 0; i < axis.getLabels().getItems().size(); i++) {
            AxisLabelItem axislabelitem = axis.getLabels().getItems().getItem(i);
            if (axislabelitem.getValue() < axis.getMinimum() || axislabelitem.getValue() > axis
                    .getMaximum())
                continue;
            int j = axis.calcPosValue(axislabelitem.getValue());
            if (!axis.getTickOnLabelsOnly())
                addTick(j);
            if (!axislabelitem.getVisible())
                continue;
            String s = axislabelitem.getText();
            if (s.equals(""))
                s = axis.getLabels().labelValue(axislabelitem.getValue());
            drawThisLabel(j, s, axislabelitem);
        }
    }

    public void draw(boolean flag) {
        axis.iAxisDateTime = axis.isDateTime();
        if (flag)
            axis.posAxis = axis.applyPosition(axis.getRectangleEdge(axis.chart.getChartRect()),
                    axis.chart.getChartRect());
        drawAxisTitle();
        tmpNumTicks = 0;
        tmpAlternate = axis.horizontal;
        if (axis.getLabels().getItems().size() == 0) {
            tmpLabelStyle = axis.calcLabelStyle();
            if (tmpLabelStyle != AxisLabelStyle.NONE) {
                chart.getGraphics3D().setFont(axis.getLabels().getFont());
                iIncrement = axis.getCalcIncrement();
                if (axis.iAxisDateTime && axis.getLabels().getExactDateTime() && axis
                        .getIncrement() != 0.0D) {
                    tmpWhichDatetime = Axis.findDateTimeStep(axis.getIncrement());
                    if (tmpWhichDatetime != 26)
                        for (; iIncrement > DateTimeStep.STEP[tmpWhichDatetime]
                                && tmpWhichDatetime != 25; tmpWhichDatetime++) {
                        }
                } else {
                    tmpWhichDatetime = 26;
                }
                if ((iIncrement > 0.0D || tmpWhichDatetime >= 19 && tmpWhichDatetime <= 25) &&
                        axis.iMaximum >= axis.iMinimum)
                    if (tmpLabelStyle == AxisLabelStyle.VALUE)
                        doCustomLabels();
                    else if (tmpLabelStyle == AxisLabelStyle.MARK)
                        axisLabelsSeries(axis.chart.getChartRect());
                    else if (tmpLabelStyle == AxisLabelStyle.TEXT)
                        if (axis.isDepthAxis)
                            depthAxisLabels();
                        else
                            axisLabelsSeries(axis.chart.getChartRect());
            }
        } else {
            drawCustomLabels();
        }
        drawTicksAndGrid.drawTicksGrid(ticks, tmpNumTicks, tmpValue);
    }

    void setChart(IBaseChart ibasechart) {
        chart = ibasechart;
        if (drawTicksAndGrid != null)
            drawTicksAndGrid.setChart(ibasechart);
    }

    private static final int MAXAXISTICKS = 2000;
    public int ticks[];
    protected Axis axis;
    protected double tmpValue;
    public transient TicksGridDraw drawTicksAndGrid;
    private int tmpNumTicks;
    public boolean tmpAlternate;
    private IBaseChart chart;
    private double iIncrement;
    private int tmpWhichDatetime;
    private static final double DIFFLOAT = 9.9999999999999995E-008D;
    private AxisLabelStyle tmpLabelStyle;
}
