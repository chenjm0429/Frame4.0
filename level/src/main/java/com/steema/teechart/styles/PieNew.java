// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Pie.java

package com.steema.teechart.styles;

import com.steema.teechart.*;
import com.steema.teechart.drawing.*;
import com.steema.teechart.editors.gallery.Gallery;
import com.steema.teechart.languages.Language;
import com.steema.teechart.legend.Legend;
import com.steema.teechart.misc.Utils;
import com.ztesoft.level1.chart.ColorDefault;

import java.util.ArrayList;

// Referenced classes of package com.steema.teechart.styles:
//            Circular, PieAngle, SeriesMarksPosition, PieMarks, 
//            SeriesRandom, MultiPies, SeriesMarks, MarksCallout, 
//            ValueList, PieOtherStyle, ValueListOrder, MarkPositions, 
//            Series

public class PieNew extends Circular {

    protected class CompareSlice implements Comparator {

        public final int compare(int i, int j) {
            double d = (6.2831853071795862D * (double) angleSize) / 360D;
            double d1 = getAngleSlice(sortedSlice[i], d);
            double d2 = getAngleSlice(sortedSlice[j], d);
            if (d1 < d2)
                return -1;
            return d1 <= d2 ? 0 : 1;
        }

        protected CompareSlice() {
        }
    }

    public final class ExplodedSliceList extends ArrayList {

        public int getSlice(int i) {
            if (i < size())
                return ((Integer) super.get(i)).intValue();
            else
                return 0;
        }

        public void setSlice(int i, int j) {
            for (; size() <= i; add(new Integer(0))) ;
            set(i, new Integer(j));
        }

        private void exchange(int i, int j) {
            int k = getSlice(i);
            setSlice(i, getSlice(j));
            setSlice(j, k);
        }

        public ExplodedSliceList(int i) {
        }
    }

    public final class SliceValueList extends ArrayList {

        public int getSlice(int i) {
            if (i < size())
                return ((Integer) super.get(i)).intValue();
            else
                return 0;
        }

        public void setSlice(int i, int j) {
            for (; i >= size(); add(new Integer(0))) ;
            if (((Integer) super.get(i)).intValue() != j) {
                set(i, new Integer(j));
                OwnerSeries.repaint();
            }
        }

        private void exchange(int i, int j) {
            int k = getSlice(i);
            setSlice(i, getSlice(j));
            setSlice(j, k);
        }

        public Series OwnerSeries;


        public SliceValueList() {
        }
    }

    public final class PieShadow extends Shadow {

        final PieNew this$0;

        public PieShadow(IBaseChart ibasechart) {
            super(ibasechart);
            this$0 = PieNew.this;

            bBrush.setDefaultColor(Color.DARK_GRAY);
            setDefaultVisible(false);
            setDefaultSize(20);
        }
    }

    public final class PieOtherSlice extends TeeBase {

        public Color getColor() {
            return color;
        }

        public void setColor(Color color1) {
            color = setColorProperty(color, color1);
        }

        private void setLegend(Legend legend1) {
            if (legend != null) {
                legend = legend1;
                legend.setSeries(series);
            }
        }

        public Legend getLegend() {
            if (legend == null) {
                legend = new Legend(series.getChart());
                legend.setVisible(false);
                legend.setSeries(series);
            }
            return legend;
        }

        public String getText() {
            return text;
        }

        public void setText(String s) {
            text = setStringProperty(text, s);
        }

        public double getValue() {
            return aValue;
        }

        public void setValue(double d) {
            aValue = setDoubleProperty(aValue, d);
        }

        public PieOtherStyle getStyle() {
            return style;
        }

        public void setStyle(PieOtherStyle pieotherstyle) {
            if (style != pieotherstyle) {
                style = pieotherstyle;
                invalidate();
            }
        }

        private Color color;
        private String text;
        private double aValue;
        private Legend legend;
        private Series series;
        private PieOtherStyle style;

        public PieOtherSlice(IBaseChart ibasechart, Series series1) {
            super(ibasechart);
            style = PieOtherStyle.NONE;
            text = "Other";
            color = Color.EMPTY;
            if (series == null)
                series = series1;
        }
    }


    public PieNew() {
        this((IBaseChart) null);
    }

    public PieNew(IBaseChart ibasechart) {
        super(ibasechart);
        angleSize = 360;
        dark3D = true;
        bevelPercent = 0;
        edgeStyle = EdgeStyle.NONE;
        explodedSlice = new ExplodedSliceList(0);
        multiPie = MultiPies.AUTOMATIC;
        autoMarkPosition = true;
        sliceHeight = new SliceValueList();
        setColorEach(true);
        pen = new ChartPen(chart, Color.BLACK);
        getMarks().setDefaultVisible(true);
        marks.getArrow().setDefaultColor(Color.BLACK);
        marks.getCallout().setDefaultLength(8);
        useSeriesColor = false;
        sliceHeight.OwnerSeries = this;
    }

    public ValueList getPieValues() {
        return vyValues;
    }

    public void setChart(IBaseChart ibasechart) {
        super.setChart(ibasechart);
        if (pen != null)
            pen.setChart(chart);
        if (shadow != null)
            shadow.setChart(chart);
    }

    protected void setDonutPercent(int i) {
        iDonutPercent = setIntegerProperty(iDonutPercent, i);
    }

    public boolean getColorEach() {
        return super.getColorEach();
    }

    public void setColorEach(boolean flag) {
        super.setColorEach(flag);
    }

    public int getAngleSize() {
        return angleSize;
    }

    public void setAngleSize(int i) {
        angleSize = setIntegerProperty(angleSize, i);
    }

    public boolean getDark3D() {
        return dark3D;
    }

    public void setDark3D(boolean flag) {
        dark3D = setBooleanProperty(dark3D, flag);
    }

    public int getTransparency() {
        return getBrush().getTransparency();
    }

    public void setTransparency(int i) {
        getBrush().setTransparency(i);
    }

    public EdgeStyle getEdgeStyle() {
        return edgeStyle;
    }

    public void setEdgeStyle(EdgeStyle edgestyle) {
        if (edgeStyle != edgestyle) {
            edgeStyle = edgestyle;
            refreshSeries();
        }
    }

    public int getBevelPercent() {
        return bevelPercent;
    }

    public void setBevelPercent(int i) {
        if (bevelPercent != i) {
            bevelPercent = i;
            refreshSeries();
        }
    }

    public boolean getDarkPen() {
        return darkPen;
    }

    public void setDarkPen(boolean flag) {
        darkPen = setBooleanProperty(darkPen, flag);
    }

    public int getExplodeBiggest() {
        return explodeBiggest;
    }

    public void setExplodeBiggest(int i) {
        explodeBiggest = setIntegerProperty(explodeBiggest, i);
        calcExplodeBiggest();
    }

    public PieOtherSlice getOtherSlice() {
        if (otherSlice == null)
            otherSlice = new PieOtherSlice(chart, this);
        return otherSlice;
    }

    public ChartPen getPen() {
        return pen;
    }

    public ChartBrush getBrush() {
        return bBrush;
    }

    public PieShadow getShadow() {
        if (shadow == null)
            shadow = new PieShadow(chart);
        return shadow;
    }

    public boolean getUsePatterns() {
        return usePatterns;
    }

    public void setUsePatterns(boolean flag) {
        usePatterns = setBooleanProperty(usePatterns, flag);
    }

    public boolean getAutoMarkPosition() {
        return autoMarkPosition;
    }

    public void setAutoMarkPosition(boolean flag) {
        autoMarkPosition = setBooleanProperty(autoMarkPosition, flag);
    }

    private void calcExplodeBiggest() {
        int i = getYValues().indexOf(getYValues().getMaximum());
        if (i != -1)
            explodedSlice.setSlice(i, explodeBiggest);
    }

    private Point calcExplodedOffset(int i) {
        Point point = new Point();
        if (isExploded) {
            double d = explodedSlice.getSlice(i);
            if (d > 0.0D) {
                double d1 = angles[i].MidAngle;
                if (chart.getGraphics3D().getSupportsFullRotation())
                    d1 += (1.5707963267948966D * (double) angleSize) / 360D + 3.1415926535897931D;
                double d2 = Math.sin(d1 + rotDegree);
                double d3 = Math.cos(d1 + rotDegree);
                d *= 0.01D;
                point.x = Utils.round((double) iXRadius * d * d3);
                point.y = Utils.round((double) iYRadius * d * d2);
            }
        }
        return point;
    }

    public void galleryChanged3D(boolean flag) {
        super.galleryChanged3D(flag);
        disableRotation();
        setCircled(!chart.getAspect().getView3D());
    }

    private double getAngleSlice(int i, double d) {
        double d1 = angles[i].MidAngle + rotDegree;
        if (d1 > d)
            d1 -= d;
        if (d1 > 0.25D * d) {
            d1 -= 0.25D * d;
            if (d1 > 3.1415926535897931D)
                d1 = d - d1;
        } else {
            d1 = 0.25D * d - d1;
        }
        return d1;
    }

    private void disableRotation() {
        Aspect aspect = chart.getAspect();
        aspect.setOrthogonal(false);
        aspect.setRotation(0);
        aspect.setElevation(305);
    }

    protected void swapValueIndex(int i, int j) {
        super.swapValueIndex(i, j);
        if (explodedSlice.size() > 0)
            explodedSlice.exchange(i, j);
        if (sliceHeight.size() > 0)
            sliceHeight.exchange(i, j);
    }

    protected void addSampleValues(int i) {
        String as[] = {
                Language.getString("PieSample1"),
                Language.getString("PieSample2"),
                Language.getString("PieSample3"),
                Language.getString("PieSample4"),
                Language.getString("PieSample5"),
                Language.getString("PieSample6"),
                Language.getString("PieSample7"),
                Language.getString("PieSample8")};

        SeriesRandom seriesrandom = randomBounds(i);
        for (int j = 0; j < i; j++)
            add(1 + Utils.round(1000D * seriesrandom.Random()), as[j % 8]);
    }

    private int SliceEndZ(int i) {
        int j;
        if (sliceHeight.size() > i)
            j = getStartZ() + Utils.round((double) ((getEndZ() - getStartZ()) * sliceHeight
                    .getSlice(i)) * 0.01D);
        else
            j = getEndZ();

        return j;
    }

    private void calcAngles() {
        double d = (6.2831853071795862D * (double) angleSize) / 360D;
        double d1;
        if (getOtherSlice().getStyle() == PieOtherStyle.NONE && getFirstVisible() != -1) {
            d1 = 0.0D;
            for (int i = firstVisible; i <= lastVisible; i++)
                d1 += Math.abs(mandatory.getValue(i));

        } else {
            d1 = mandatory.getTotalABS();
        }
        double d2 = d1 == 0.0D ? 0.0D : d / d1;
        angles = new PieAngle[getCount()];
        double d3 = 0.0D;
        for (int j = firstVisible; j <= lastVisible; j++) {
            angles[j] = new PieAngle();
            angles[j].StartAngle = j != firstVisible ? angles[j - 1].EndAngle : 0.0D;
            if (d1 != 0.0D) {
                if (!belongsToOtherSlice(j))
                    d3 += Math.abs(mandatory.getValue(j));
                if (d3 == d1)
                    angles[j].EndAngle = d;
                else
                    angles[j].EndAngle = d3 * d2;
                if (angles[j].EndAngle - angles[j].StartAngle > d)
                    angles[j].EndAngle = angles[j].StartAngle + d;
            } else {
                angles[j].EndAngle = d;
            }
            angles[j].MidAngle = (angles[j].StartAngle + angles[j].EndAngle) * 0.5D;
        }
    }

    protected Point calcExplodedRadius(int i) {
        double d = 1.0D + (double) explodedSlice.getSlice(i) * 0.01D;
        return new Point(Utils.round((double) iXRadius * d), Utils.round((double) iYRadius * d));
    }

    protected void clearLists() {
        super.clearLists();
        explodedSlice.clear();
        sliceHeight.clear();
    }

    public void doBeforeDrawChart() {
        super.doBeforeDrawChart();
        if (getPieValues().getOrder() != ValueListOrder.NONE)
            getPieValues().sort();
        removeOtherSlice();
        SeriesMarksPosition seriesmarksposition = otherMarkCustom();
        getXValues().fillSequence();
        if (otherSlice != null && otherSlice.getStyle() != PieOtherStyle.NONE && getYValues()
                .getTotalABS() > 0.0D) {
            boolean flag = false;
            double d1 = 0.0D;
            for (int i = 0; i < getCount(); i++) {
                double d = getYValues().value[i];
                if (otherSlice.getStyle() == PieOtherStyle.BELOWPERCENT)
                    d = (d * 100D) / getYValues().getTotalABS();
                if (d < otherSlice.getValue()) {
                    d1 += getYValues().value[i];
                    getXValues().value[i] = -1D;
                    flag = true;
                }
            }

            if (flag) {
                int j = add(2147483647D, d1, otherSlice.text, otherSlice.getColor());
                double d2 = getYValues().getTotalABS();
                getYValues().totalABS = d2 - d1;
                getYValues().statsOk = true;
                if (seriesmarksposition != null)
                    getMarks().getPositions().setPosition(j, seriesmarksposition);
            }
        }
    }

    private int pieIndex() {
        int i = 0;
        for (int j = 0; j < getChart().getSeriesCount() && chart.getSeries(j) != this; j++)
            if (chart.getSeries(j).getActive() && sameClass(chart.getSeries(j)))
                i++;

        return i;
    }

    private int pieCount() {
        int i = 0;
        for (int j = 0; j < chart.getSeriesCount(); j++)
            if (chart.getSeries(j).getActive() && sameClass(chart.getSeries(j)))
                i++;

        return i;
    }

    private void guessRectangle() {
        int i = pieCount();
        if (i > 1) {
            int j = pieIndex();
            Rectangle rectangle = chart.getChartRect();
            int k = rectangle.width;
            int l = rectangle.height;
            int i1 = (int) Math.round(Math.sqrt(i));
            rectangle.x += (j % i1) * (k / i1);
            rectangle.width = k / i1;
            int j1 = (int) Math.round(0.5D + Math.sqrt(i));
            rectangle.y += (j / i1) * (l / j1);
            rectangle.height = l / j1;
            chart.setChartRect(chart.getGraphics3D().calcRect3D(rectangle, 0));
        }
    }

    protected void doBeforeDrawValues() {
        iOldChartRect = chart.getChartRect().copy();
        if (multiPie == MultiPies.AUTOMATIC)
            guessRectangle();
        super.doBeforeDrawValues();
    }

    protected void doAfterDrawValues() {
        chart.setChartRect(iOldChartRect);
        super.doAfterDrawValues();
    }

    private void removeOtherSlice() {
        int i = 0;
        do {
            if (i >= getCount())
                break;
            if (vxValues.getValue(i) == 2147483647D) {
                delete(i);
                break;
            }
            i++;
        } while (true);
    }

    private SeriesMarksPosition otherMarkCustom() {
        int i = 0;
        do {
            if (i >= getCount())
                break;
            if (vxValues.getValue(i) == 2147483647D) {
                SeriesMarksPosition seriesmarksposition = getMarks().getPositions().getPosition(i);
                if (seriesmarksposition != null && seriesmarksposition.custom) {
                    SeriesMarksPosition seriesmarksposition1 = new SeriesMarksPosition();
                    seriesmarksposition.assign(seriesmarksposition1);
                    return seriesmarksposition1;
                }
                break;
            }
            i++;
        } while (true);

        return null;
    }

    protected void draw() {
        if (explodeBiggest > 0)
            calcExplodeBiggest();
        int i = -1;
        int j = 0;
        int k = getCount();
        for (int l = 0; l < explodedSlice.size(); l++)
            if (explodedSlice.getSlice(l) > j) {
                j = Utils.round(explodedSlice.getSlice(l));
                i = l;
            }

        calcAngles();
        isExploded = i != -1 || sliceHeight.size() > 0;
        if (i != -1) {
            Point point = calcExplodedOffset(i);
            getCircleRect().grow(-Math.abs(point.x) / 2, -Math.abs(point.y) / 2);
            adjustCircleRect();
            calcRadius();
        }
        Point point1 = angleToPos(0.0D, iXRadius, iYRadius);
        IGraphics3D igraphics3d = chart.getGraphics3D();
        if (shadow != null && shadow.getVisible() && !shadow.getColor().isEmpty() && (shadow
                .getWidth() != 0 || shadow.getHeight() != 0))
            shadow.draw(igraphics3d, rCircleRect.x, rCircleRect.y, rCircleRect.getRight(),
                    rCircleRect.getBottom(), getEndZ() - 10);
        Rectangle rectangle = chart.getChartRect();
        if (getOtherSlice().getLegend() != null && getOtherSlice().getLegend().getVisible()) {
            Legend legend = chart.getLegend();
            chart.setLegend(getOtherSlice().getLegend());
            rectangle = chart.doDrawLegend(chart.getGraphics3D(), rectangle);
            chart.setLegend(legend);
        }
        if (chart.getAspect().getView3D() && (isExploded || iDonutPercent > 0) && !igraphics3d
                .getSupportsFullRotation()) {
            if (sortedSlice == null)
                sortedSlice = new int[k];
            for (int i1 = 0; i1 < k; i1++)
                sortedSlice[i1] = i1;

            Utils.sort(sortedSlice, 0, k - 1, new CompareSlice());
            for (int j1 = 0; j1 < k; j1++)
                drawValue(sortedSlice[j1]);

        } else {
            super.draw();
            if (isDrill) {
                bBrush.setSolid(true);
                Color color = Color.TRANSPARENT;
                chart.setBrushCanvas(color, bBrush, calcCircleBackColor());
                preparePiePen(chart.getGraphics3D(), i);
                ChartPen pen = new ChartPen(Color.BLACK);
                pen.setVisible(true);
                pen.setWidth(1);
                igraphics3d.setPen(pen);
                int p = 4;
                igraphics3d.ellipse(iCircleXCenter - iXRadius - p, iCircleYCenter - iYRadius - p,
                        iCircleXCenter + iXRadius + p, iCircleYCenter + iYRadius + p);
            }
        }
    }

    private boolean shouldDrawShadow() {
        return shadow != null && shadow.visible && !shadow.getColor().isEmpty() && (shadow
                .getWidth() != 0 || shadow.getHeight() != 0);
    }

    protected void drawMark(int i, String s, SeriesMarksPosition seriesmarksposition) {
        Point point = calcExplodedOffset(i);
        if (!belongsToOtherSlice(i)) {
            Point point1 = calcExplodedRadius(i);
            double d = angles[i].MidAngle;
            getMarks().setZPosition(SliceEndZ(i));
            seriesmarksposition.arrowFix = true;
            int j = getMarks().getCallout().getLength() + getMarks().getCallout().getDistance();
            Point point2 = angleToPos(d, point1.x + j, point1.y + j);
            seriesmarksposition.arrowTo = point2;
            j = getMarks().getCallout().getDistance();
            point2 = angleToPos(d, point1.x + j, point1.y + j);
            seriesmarksposition.arrowFrom = point2;
            if (seriesmarksposition.arrowTo.x > iCircleXCenter)
                seriesmarksposition.leftTop.x = seriesmarksposition.arrowTo.x;
            else
                seriesmarksposition.leftTop.x = seriesmarksposition.arrowTo.x -
                        seriesmarksposition.width;
            if (seriesmarksposition.arrowTo.y > iCircleYCenter)
                seriesmarksposition.leftTop.y = seriesmarksposition.arrowTo.y;
            else
                seriesmarksposition.leftTop.y = seriesmarksposition.arrowTo.y -
                        seriesmarksposition.height;
            if (getMarksPie().getVertCenter()) {
                double d1 = seriesmarksposition.height / 2;
                if (seriesmarksposition.arrowTo.y > iCircleYCenter)
                    seriesmarksposition.arrowTo.y += d1;
                else
                    seriesmarksposition.arrowTo.y -= d1;
            }
            if (getMarksPie().getLegSize() == 0) {
                seriesmarksposition.hasMid = false;
                seriesmarksposition.midPoint.x = 0;
                seriesmarksposition.midPoint.y = 0;
            } else {
                seriesmarksposition.hasMid = true;
                if (seriesmarksposition.arrowTo.x > iCircleXCenter) {
                    if (seriesmarksposition.arrowTo.x - getMarksPie().getLegSize() <
                            seriesmarksposition.arrowFrom.x) {
                        seriesmarksposition.midPoint.x = seriesmarksposition.arrowFrom.x;
                        seriesmarksposition.arrowTo.x = seriesmarksposition.arrowTo.x +
                                getMarksPie().getLegSize();
                        seriesmarksposition.leftTop.x = seriesmarksposition.arrowTo.x;
                    } else {
                        seriesmarksposition.midPoint.x = seriesmarksposition.arrowTo.x -
                                getMarksPie().getLegSize();
                    }
                } else if (seriesmarksposition.arrowTo.x + getMarksPie().getLegSize() >
                        seriesmarksposition.arrowFrom.x) {
                    seriesmarksposition.midPoint.x = seriesmarksposition.arrowFrom.x;
                    seriesmarksposition.arrowTo.x = seriesmarksposition.arrowFrom.x - getMarksPie
                            ().getLegSize();
                    seriesmarksposition.leftTop.x = seriesmarksposition.arrowTo.x -
                            seriesmarksposition.width;
                } else {
                    seriesmarksposition.midPoint.x = seriesmarksposition.arrowTo.x + getMarksPie
                            ().getLegSize();
                }
                seriesmarksposition.midPoint.y = seriesmarksposition.arrowTo.y;
            }
            if (getAutoMarkPosition())
                getMarks().antiOverlap(firstVisible, i, seriesmarksposition);
            super.drawMark(i, s, seriesmarksposition);
        }
    }

    public PieMarks getMarksPie() {
        if (piemarks == null)
            piemarks = new PieMarks(chart, this);
        return piemarks;
    }

    public void setMarksPie(PieMarks piemarks1) {
        piemarks = piemarks1;
    }

    public void drawOutlineSlice(int i) {
        Point point = calcExplodedOffset(i);
        IGraphics3D igraphics3d = chart.getGraphics3D();
        if (chart.getAspect().getView3D() || iDonutPercent == 0)
            igraphics3d.pie(iCircleXCenter + point.x, iCircleYCenter - point.y, 0, 0, iXRadius,
                    iYRadius, getStartZ(), SliceEndZ(i), angles[i].StartAngle + rotDegree,
                    angles[i].EndAngle + rotDegree, dark3D, false, iDonutPercent, bevelPercent,
                    edgeStyle, false);
        else
            igraphics3d.donut(iCircleXCenter + point.x, iCircleYCenter - point.y, iXRadius,
                    iYRadius, angles[i].StartAngle + rotDegree, angles[i].EndAngle + rotDegree,
                    iDonutPercent);
    }

    public void drawPie(int i, boolean flag) {
        Point point = calcExplodedOffset(i);
        IGraphics3D igraphics3d = chart.getGraphics3D();
        if (angleSize < 360)
            isExploded = true;
        igraphics3d.pie(iCircleXCenter, iCircleYCenter, point.x, point.y, iXRadius, iYRadius,
                startZ, SliceEndZ(i), angles[i].StartAngle + rotDegree, angles[i].EndAngle +
                        rotDegree, dark3D, isExploded, iDonutPercent, bevelPercent, edgeStyle,
                flag);
        if (isDrill) {
            if (usePatterns || chart.getGraphics3D().getMonochrome())
                bBrush.setStyle(Graphics3D.getDefaultPattern(0));
            else
                bBrush.setSolid(true);
            Color color = Color.fromCode(ColorDefault.pieCenterColor[0]);
            chart.setBrushCanvas(color, bBrush, calcCircleBackColor());
            preparePiePen(chart.getGraphics3D(), i);

            igraphics3d.pie(iCircleXCenter, iCircleYCenter, point.x, point.y, iXRadius / 2,
                    iYRadius / 2, startZ, SliceEndZ(i), angles[i].StartAngle + rotDegree - 1,
                    angles[i].EndAngle + rotDegree + 1, dark3D, isExploded, iDonutPercent,
                    bevelPercent, edgeStyle, false);

            color = Color.fromCode(ColorDefault.pieCenterColor[1]);
            chart.setBrushCanvas(color, bBrush, calcCircleBackColor());
            preparePiePen(chart.getGraphics3D(), i);
            igraphics3d.pie(iCircleXCenter, iCircleYCenter, point.x, point.y, iXRadius / 8,
                    iYRadius / 8, startZ, SliceEndZ(i), angles[i].StartAngle + rotDegree - 2,
                    angles[i].EndAngle + rotDegree + 2, dark3D, isExploded, iDonutPercent,
                    bevelPercent, edgeStyle, false);
        }
    }

    public void drawValue(int i) {
        if (getCircleWidth() > 4 && getCircleHeight() > 4 && !belongsToOtherSlice(i)) {
            if (usePatterns || chart.getGraphics3D().getMonochrome())
                bBrush.setStyle(Graphics3D.getDefaultPattern(i));
            else
                bBrush.setSolid(true);
            Color color = chart.getGraphics3D().getMonochrome() ? Color.BLACK : getValueColor(i);
            chart.setBrushCanvas(color, bBrush, calcCircleBackColor());
            preparePiePen(chart.getGraphics3D(), i);
            drawPie(i, i == getCount() - 1);
        }
    }

    protected int numSampleValues() {
        return 8;
    }

    public void prepareForGallery(boolean flag) {
        super.prepareForGallery(flag);
        fillSampleValues(8);
        chart.getAspect().setChart3DPercent(75);
        getMarks().getCallout().setLength(0);
        getMarks().setDrawEvery(1);
        disableRotation();
        setColorEach(flag);
    }

    public int legendToValueIndex(int i) {
        int j = -1;
        boolean flag = chart.getLegend() != null && chart.getLegend() == getOtherSlice()
                .getLegend();
        for (int k = 0; k < getCount(); k++) {
            boolean flag1 = belongsToOtherSlice(k);
            if ((flag && flag1 || !flag && !flag1) && ++j == i)
                return k;
        }

        return i;
    }

    private void preparePiePen(IGraphics3D igraphics3d, int i) {
        igraphics3d.setPen(pen);
        if (darkPen) {
            Color color = new Color(getValueColor(i));
            color.applyDark(128);
            igraphics3d.getPen().setColor(color);
        }
    }

    protected void prepareLegendCanvas(IGraphics3D igraphics3d, int i, Color color, ChartBrush
            chartbrush) {
        super.prepareLegendCanvas(igraphics3d, i, color, chartbrush);
        preparePiePen(igraphics3d, i);
        if (usePatterns || igraphics3d.getMonochrome())
            chartbrush.setStyle(Graphics3D.getDefaultPattern(i));
        else
            chartbrush.setSolid(true);
    }

    public boolean belongsToOtherSlice(int i) {
        return vxValues.value[i] == -1D;
    }

    public int calcXPos(int i) {
        if (vxValues.value[i] == 2147483647D)
            return 0;
        else
            return super.calcXPos(i);
    }

    public int clicked(int i, int j) {
        int k = super.clicked(i, j);
        if (k == -1)
            k = calcClickedPie(i, j);
        return k;
    }

    private int calcClickedPie(int i, int j) {
        if (chart != null) {
            Point point = chart.getGraphics3D().calculate2DPosition(i, j, chart.getAspect()
                    .getWidth3D());
            i = point.x;
            j = point.y;
        }
        double d = pointToAngle(i, j);
        for (int k = 0; k < getCount(); k++) {
            Point point1 = calcExplodedOffset(k);
            if (Math.abs(i - getCircleXCenter()) <= getXRadius() + point1.x && Math.abs(j -
                    getCircleYCenter()) <= getYRadius() + point1.y && angles[k].contains(d))
                return k;
        }

        return -1;
    }

    public int getCountLegendItems() {
        int i = 0;
        for (int j = 0; j < getCount(); j++)
            if (belongsToOtherSlice(j))
                i++;

        if (chart.getLegend() != null && chart.getLegend() == getOtherSlice().getLegend())
            return i;
        else
            return getCount() - i;
    }

    public void createSubGallery(Gallery gallery) {
        super.createSubGallery(gallery);
        gallery.createSubChart(Language.getString("Patterns"));
        gallery.createSubChart(Language.getString("Exploded"));
        gallery.createSubChart(Language.getString("Shadow"));
        gallery.createSubChart(Language.getString("Marks"));
        gallery.createSubChart(Language.getString("SemiPie"));
        gallery.createSubChart(Language.getString("NoBorder"));
        gallery.createSubChart(Language.getString("DarkPen"));
    }

    public void setSubGallery(int i) {
        switch (i) {
            case 1: // '\001'
                setUsePatterns(true);
                break;

            case 2: // '\002'
                setExplodeBiggest(30);
                break;

            case 3: // '\003'
                getShadow().setVisible(true);
                getShadow().setWidth(10);
                getShadow().setHeight(10);
                break;

            case 4: // '\004'
                getMarks().setVisible(true);
                clear();
                add(30D, "A");
                add(70D, "B");
                break;

            case 5: // '\005'
                setAngleSize(180);
                break;

            case 6: // '\006'
                getPen().setVisible(false);
                break;

            case 7: // '\007'
                setDarkPen(true);
                break;
        }
    }

    public ExplodedSliceList getExplodedSlice() {
        return explodedSlice;
    }

    public SliceValueList SliceHeight() {
        return sliceHeight;
    }

    public String getDescription() {
        return Language.getString("GalleryPie");
    }

    public static final int BelongsToOther = -1;
    private static final int OtherFlag = 0x7fffffff;
    private int angleSize;
    private boolean dark3D;
    private boolean darkPen;
    protected int iDonutPercent;
    private int bevelPercent;
    private EdgeStyle edgeStyle;
    private ExplodedSliceList explodedSlice;
    private int explodeBiggest;
    private PieOtherSlice otherSlice;
    private MultiPies multiPie;
    private PieShadow shadow;
    private boolean usePatterns;
    private ChartPen pen;
    private boolean autoMarkPosition;
    private SliceValueList sliceHeight;
    private PieMarks piemarks;
    private Rectangle iOldChartRect;
    protected transient int iniX;
    protected transient int iniY;
    private transient int sortedSlice[];
    private transient boolean isExploded;
    public transient PieAngle angles[];
    //是否挖开
    public boolean isDrill = false;

    public boolean isDrill() {
        return isDrill;
    }

    public void setDrill(boolean isDrill) {
        this.isDrill = isDrill;
    }
}