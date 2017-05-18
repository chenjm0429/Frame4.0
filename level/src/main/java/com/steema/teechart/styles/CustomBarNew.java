/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.steema.teechart.styles;


import com.steema.teechart.IBaseChart;
import com.steema.teechart.Rectangle;
import com.steema.teechart.axis.Axis;
import com.steema.teechart.drawing.ChartBrush;
import com.steema.teechart.drawing.ChartPen;
import com.steema.teechart.drawing.Color;
import com.steema.teechart.drawing.Gradient;
import com.steema.teechart.drawing.IGraphics3D;
import com.steema.teechart.drawing.Point;
import com.steema.teechart.editors.gallery.Gallery;
import com.steema.teechart.languages.Language;
import com.steema.teechart.misc.Utils;

// Referenced classes of package com.steema.teechart.styles:
//            Series, ISeries, BarStyle, MultiBars, 
//            SeriesMarks, MarksCallout, ValueList, Margins

public class CustomBarNew extends Series {
    public static interface BarStyleResolver {

        public abstract BarStyle getStyle(ISeries iseries, int i, BarStyle barstyle);
    }

    public static enum MarksLocation {
        Start, Center, End;
    }


    public CustomBarNew(IBaseChart ibasechart) {
        super(ibasechart);
        barSizePercent = 70;
        bDark3D = true;
        iMultiBar = MultiBars.SIDE;
        bUseOrigin = true;
        marksOnBar = false;
        marksLocation = MarksLocation.End;
        autoMarkPosition = true;
        barStyle = BarStyle.RECTANGLE;
        depthPercent = 100;
        sideMargins = true;
        iBarBounds = Rectangle.EMPTY;
        groups = new int[100];
        getMarks().setVisible(true);
        marks.setDefaultVisible(true);
        marks.getCallout().setLength(20);
        drawBetweenPoints = false;
    }

    public CustomBarNew() {
        this((IBaseChart) null);
    }

    public void setBarStyleResolver(BarStyleResolver barstyleresolver) {
        styleResolver = barstyleresolver;
    }

    public void removeBarStyleResolver() {
        styleResolver = null;
    }

    public boolean getMarksOnBar() {
        return marksOnBar;
    }

    public void setMarksOnBar(boolean flag) {
        marksOnBar = flag;
        invalidate();
    }

    public MarksLocation getMarksLocation() {
        return marksLocation;
    }

    public void setMarksLocation(MarksLocation markslocation) {
        if (marksLocation != markslocation) {
            marksLocation = markslocation;
            invalidate();
        }
    }

    public Gradient getGradient() {
        return getBrush().getGradient();
    }

    public boolean getGradientRelative() {
        return gradientRelative;
    }

    public void setGradientRelative(boolean flag) {
        gradientRelative = setBooleanProperty(gradientRelative, flag);
    }

    public int getStackGroup() {
        return stackGroup;
    }

    public void setStackGroup(int i) {
        stackGroup = setIntegerProperty(stackGroup, i);
    }

    public void setZPositions() {
        super.setZPositions();
        if (depthPercent != 0) {
            int i = (int) ((double) ((getEndZ() - getStartZ()) * (100 - depthPercent)) *
                    0.0050000000000000001D);
            setStartZ(getStartZ() + i);
            setEndZ(getEndZ() - i);
        }
    }

    public int getDepthPercent() {
        return depthPercent;
    }

    public void setDepthPercent(int i) {
        depthPercent = setIntegerProperty(depthPercent, i);
    }

    public boolean getDark3D() {
        return bDark3D;
    }

    public void setDark3D(boolean flag) {
        bDark3D = setBooleanProperty(bDark3D, flag);
    }

    public void prepareForGallery(boolean flag) {
        super.prepareForGallery(flag);
        barSizePercent = 85;
        setMultiBar(MultiBars.NONE);
    }

    protected void setBarSizePercent(int i) {
        barSizePercent = setIntegerProperty(barSizePercent, i);
    }

    private void setOtherBars(boolean flag) {
        if (chart != null) {
            for (int i = 0; i < chart.getSeriesCount(); i++) {
                Series series = chart.getSeries(i);
                if (!sameClass(series))
                    continue;
                CustomBarNew custombar = (CustomBarNew) series;
                if (flag) {
                    custombar.iMultiBar = iMultiBar;
                    custombar.sideMargins = sideMargins;
                } else {
                    iMultiBar = custombar.iMultiBar;
                    sideMargins = custombar.sideMargins;
                    break;
                }
                custombar.calcVisiblePoints = iMultiBar != MultiBars.SELFSTACK;
            }
        }
    }

    public boolean getSideMargins() {
        return sideMargins;
    }

    public void setSideMargins(boolean flag) {
        sideMargins = setBooleanProperty(sideMargins, flag);
        setOtherBars(true);
    }

    protected boolean shouldSerializeYOrigin() {
        return false;
    }

    public double getYOrigin() {
        return dOrigin;
    }

    public void setYOrigin(double d) {
        setOrigin(d);
    }

    public boolean getUseOrigin() {
        return bUseOrigin;
    }

    public void setUseOrigin(boolean flag) {
        bUseOrigin = setBooleanProperty(bUseOrigin, flag);
    }

    public double getOrigin() {
        return dOrigin;
    }

    public void setOrigin(double d) {
        dOrigin = setDoubleProperty(dOrigin, d);
    }

    public boolean getAutoMarkPosition() {
        return autoMarkPosition;
    }

    public void setAutoMarkPosition(boolean flag) {
        autoMarkPosition = flag;
    }

    public int getConePercent() {
        return conePercent;
    }

    public void setConePercent(int i) {
        conePercent = setIntegerProperty(conePercent, i);
    }

    public int getOffsetPercent() {
        return offsetPercent;
    }

    public void setOffsetPercent(int i) {
        offsetPercent = setIntegerProperty(offsetPercent, i);
    }

    public BarStyle getBarStyle() {
        return barStyle;
    }

    public void setBarStyle(BarStyle barstyle) {
        if (barStyle != barstyle) {
            barStyle = barstyle;
            if (barStyle == BarStyle.RECTGRADIENT)
                getGradient().setVisible(true);
            else
                getGradient().setVisible(false);
            invalidate();
        }
    }

    public ChartBrush getBrush() {
        return bBrush;
    }

    public void setChart(IBaseChart ibasechart) {
        super.setChart(ibasechart);
        if (pPen != null)
            pPen.setChart(chart);
        if (bBrush != null)
            bBrush.setChart(chart);
        if (tickLines != null)
            tickLines.setChart(chart);
        setOtherBars(false);
    }

    public MultiBars getMultiBar() {
        return iMultiBar;
    }

    public void setMultiBar(MultiBars multibars) {
        if (iMultiBar != multibars) {
            iMultiBar = multibars;
            setOtherBars(true);
            invalidate();
        }
    }

    public ChartPen getPen() {
        if (pPen == null)
            pPen = new ChartPen(chart, Color.BLACK);
        return pPen;
    }

    public ChartPen getTickLines() {
        if (tickLines == null)
            tickLines = new ChartPen(chart, Color.BLACK, false);
        return tickLines;
    }

    public Rectangle getBarBounds() {
        return iBarBounds;
    }

    protected int getBarBoundsMidX() {
        return (iBarBounds.getLeft() + iBarBounds.getRight()) / 2;
    }

    private void calcGradientColor(int i) {
        Gradient gradient = getGradient();
        if (gradientRelative) {
            double d = bUseOrigin ? dOrigin : mandatory.getMinimum();
            double d1 = (mandatory.value[i] - d) / (mandatory.getMaximum() - d);
            int j = gradient.getStartColor().getRed();
            int k = gradient.getStartColor().getGreen();
            int l = gradient.getStartColor().getBlue();
            gradient.setEndColor(Color.fromArgb(gradient.getStartColor().getAlpha(), j + Utils
                    .round(d1 * (double) (normalBarColor.getRed() - j)), k + Utils.round(d1 *
                    (double) (normalBarColor.getGreen() - k)), l + Utils.round(d1 * (double)
                    (normalBarColor.getBlue() - l))));
        } else {
            gradient.setEndColor(normalBarColor);
        }
    }

    protected BarStyle doGetBarStyle(int i) {
        BarStyle barstyle = barStyle;
        if (styleResolver != null)
            barstyle = styleResolver.getStyle(this, i, barstyle);
        return barstyle;
    }

    protected int internalCalcMarkLength(int i) {
        return 0;
    }

    private double internalPointOrigin(int i, boolean flag) {
        double d = 0.0D;
        double d1 = mandatory.value[i];
        if (chart != null) {
            for (int j = 0; j < chart.getSeriesCount(); j++) {
                Series series = chart.getSeries(j);
                if (!flag && series == this)
                    break;
                if (!series.getActive() || !sameClass(series) || series.getCount() <= i || (
                        (CustomBarNew) series).stackGroup != stackGroup)
                    continue;
                double d2 = series.getOriginValue(i);
                if (d1 < 0.0D) {
                    if (d2 < 0.0D)
                        d += d2;
                    continue;
                }
                if (d2 > 0.0D)
                    d += d2;
            }
        }
        return d;
    }

    protected void doGradient3D(int i, Point point, Point point1) {
        if (pPen.getVisible()) {
            point.x++;
            point.y++;
            int j = Utils.round(pPen.getWidth()) - 1;
            point1.x -= j;
            point1.y -= j;
        }
        calcGradientColor(i);
        getGradient().draw(chart.getGraphics3D(), point.x, point.y, point1.x, point1.y);
    }

    public void barRectangle(Color color, Rectangle rectangle) {
        barRectangle(color, rectangle.x, rectangle.y, rectangle.getRight(), rectangle.getBottom());
    }

    public void barRectangle(Color color, int i, int j, int k, int l) {
        IGraphics3D igraphics3d = chart.getGraphics3D();
        if (bBrush.getSolid())
            if (k == i || j == l) {
                igraphics3d.getPen().setColor(igraphics3d.getBrush().getColor());
                igraphics3d.getPen().setVisible(true);
                igraphics3d.line(i, j, k, l);
            } else if (Math.abs(k - i) < igraphics3d.getPen().getWidth() || Math.abs(l - j) <
                    igraphics3d.getPen().getWidth()) {
                igraphics3d.getPen().setColor(igraphics3d.getBrush().getColor());
                igraphics3d.getPen().setVisible(true);
                igraphics3d.getBrush().setVisible(false);
            }
        igraphics3d.rectangle(i, j, k, l);
    }

    protected void doBarGradient(int i, Rectangle rectangle) {
        calcGradientColor(i);
        getGradient().draw(chart.getGraphics3D(), rectangle);
        if (pPen.getVisible()) {
            chart.getGraphics3D().getBrush().setVisible(false);
            barRectangle(normalBarColor, iBarBounds);
        }
    }

    public int getCustomBarWidth() {
        return customBarSize;
    }

    public void setCustomBarWidth(int i) {
        customBarSize = i;
        chart.invalidate();
    }

    private void doCalcBarWidth() {
        if (customBarSize != 0)
            iBarSize = customBarSize;
        else if (iMaxBarPoints > 0) {
            Axis axis = yMandatory ? getHorizAxis() : getVertAxis();
            int i = 0;
            if (sideMargins)
                iMaxBarPoints++;
            i = axis.iAxisSize / iMaxBarPoints;
            iBarSize = Utils.round((double) barSizePercent * 0.01D * (double) i) / Math.max(1,
                    iNumBars);
            if (iBarSize % 2 == 1)
                iBarSize++;

//            Log.i("****************", iBarSize+"&&&&&&&&&&&&&&&&&"+maxWidth+"********"+axis
// .iAxisSize+"^^^^^^^^"+iMaxBarPoints);
            if (iBarSize > maxWidth) {
                iBarSize = maxWidth;
            }
        } else {
            iBarSize = 0;
        }
    }

    public int barMargin() {
        int i = iBarSize;
        if (iMultiBar != MultiBars.SIDEALL)
            i *= iNumBars;
        if (!sideMargins)
            i /= 2;
        return i;
    }

    protected void internalApplyBarMargin(Margins margins) {
        doCalcBarWidth();
        int i = barMargin();
        margins.min += i;
        margins.max += i;
    }

    protected Rectangle calcBarBounds(int i) {
        return Utils.emptyRectangle();
    }

    protected boolean isPointInChartRect(int i) {
        Rectangle rectangle = chart.getChartRect();
        Rectangle rectangle1 = calcBarBounds(i);
        if (rectangle.width == 0) {
            rectangle.x = rectangle.getLeft() - 1;
            rectangle.width++;
        }
        if (rectangle.height == 0) {
            rectangle.y = rectangle.getTop() - 1;
            rectangle.height++;
        }
        if (rectangle1.width == 0) {
            rectangle1.x = rectangle1.getLeft() - 1;
            rectangle1.width++;
        }
        if (rectangle1.height == 0) {
            rectangle1.y = rectangle1.getTop() - 1;
            rectangle1.height++;
        }
        return Utils.intersectWithRect(rectangle, rectangle1);
    }

    protected void calcFirstLastVisibleIndex(boolean flag) {
        super.calcFirstLastVisibleIndex();
        boolean flag1 = yMandatory ? getHorizAxis().iRangezero : getVertAxis().iRangezero;
        if (!flag1) {
            for (; lastVisible < getCount() - 1 && isPointInChartRect(lastVisible + 1);
                 lastVisible++) {

            }

            for (; lastVisible > -1 && lastVisible < getCount() - 1 && notMandatory
                    .value[lastVisible] == notMandatory.value[lastVisible + 1]; lastVisible++)
                ;
            for (; firstVisible > 0 && isPointInChartRect(firstVisible - 1); firstVisible--) ;
            for (; firstVisible > 0 && notMandatory.value[firstVisible] == notMandatory
                    .value[firstVisible - 1]; firstVisible--)
                ;
        }
        if (getMultiBar() == MultiBars.SIDEALL && flag && chart != null) {
            for (int i = 0; i < chart.getSeriesCount(); i++)
                if (chart.getSeries(i).getClass() == getClass() && chart.getSeries(i) != this) {
                    ((CustomBarNew) chart.getSeries(i)).calcFirstLastVisibleIndex(false);
                    lastVisible += chart.getSeries(i).lastVisible + 1;
                }
        }
    }

    public void calcFirstLastVisibleIndex() {
        calcFirstLastVisibleIndex(true);
    }

    protected int lastVisibleMark() {
        calcFirstLastVisibleIndex(false);
        return lastVisible;
    }

    protected int internalGetOriginPos(int i, int j) {
        int k = 0;
        double d = pointOrigin(i, false);
        if ((iMultiBar == MultiBars.STACKED) | (iMultiBar == MultiBars.SELFSTACK))
            k = calcPosValue(d);
        else if (iMultiBar == MultiBars.STACKED100) {
            double d1 = pointOrigin(i, true);
            k = d1 == 0.0D ? 0 : calcPosValue((d * 100D) / d1);
        } else {
            k = bUseOrigin ? calcPosValue(d) : j;
        }
        return k;
    }

    protected double maxMandatoryValue(double d) {
        double d1 = 0.0D;
        if (iMultiBar == MultiBars.STACKED100) {
            d1 = 100D;
        } else {
            d1 = d;
            if (iMultiBar == MultiBars.SELFSTACK)
                d1 = mandatory.getTotal();
            else if (iMultiBar == MultiBars.STACKED) {
                for (int i = 0; i < getCount(); i++) {
                    double d2 = pointOrigin(i, false) + mandatory.value[i];
                    if (d2 > d1)
                        d1 = d2;
                }
            }
            if (bUseOrigin && d1 < dOrigin)
                d1 = dOrigin;
        }
        return d1;
    }

    public double pointOrigin(int i, boolean flag) {
        if (iMultiBar == MultiBars.STACKED || iMultiBar == MultiBars.STACKED100)
            return internalPointOrigin(i, flag);
        if (iMultiBar == MultiBars.SELFSTACK) {
            double d = 0.0D;
            if (mandatory.value[i] >= 0.0D) {
                for (int j = 0; j < i; j++)
                    if (mandatory.value[j] >= 0.0D)
                        d += mandatory.value[j];

            } else {
                for (int k = 0; k < i; k++)
                    if (mandatory.value[k] < 0.0D)
                        d += mandatory.value[k];

            }
            return d;
        } else {
            return dOrigin;
        }
    }

    protected double minMandatoryValue(double d) {
        double d1 = 0.0D;
        if (iMultiBar == MultiBars.STACKED100) {
            d1 = 0.0D;
        } else {
            d1 = d;
            if (iMultiBar == MultiBars.STACKED || iMultiBar == MultiBars.SELFSTACK) {
                for (int i = 0; i < getCount(); i++) {
                    double d2 = pointOrigin(i, false) + mandatory.value[i];
                    if (d2 < d1)
                        d1 = d2;
                }
            }
            if (bUseOrigin && d1 > dOrigin)
                d1 = dOrigin;
        }
        return d1;
    }

    public void calcZOrder() {
        if (iMultiBar == MultiBars.NONE) {
            super.calcZOrder();
        } else {
            int i = -1;
            for (int j = 0; j < chart.getSeriesCount(); j++) {
                Series series = chart.getSeries(j);
                if (!series.getActive())
                    continue;
                if (series == this)
                    break;
                if (!sameClass(series))
                    continue;
                i = series.getZOrder();
                break;
            }

            if (i == -1)
                super.calcZOrder();
            else
                iZOrder = i;
        }
    }

    private boolean newGroup(int i) {
        for (int j = 0; j < numGroups; j++)
            if (groups[j] == i)
                return false;

        groups[numGroups] = i;
        numGroups++;
        return true;
    }

    public void doBeforeDrawChart() {
        super.doBeforeDrawChart();
        iOrderPos = 1;
        iPreviousCount = 0;
        iNumBars = 0;
        iMaxBarPoints = -1;
        numGroups = 0;
        boolean flag = false;
        for (int i = 0; i < chart.getSeriesCount(); i++) {
            Series series = chart.getSeries(i);
            if (series.getActive() && sameClass(series)) {
                flag |= series == this;
                int j = series.getCount();
                if (iMaxBarPoints == -1 || j > iMaxBarPoints)
                    iMaxBarPoints = j;
                if (iMultiBar == MultiBars.NONE)
                    iNumBars = 1;
                else if ((iMultiBar == MultiBars.SIDE) | (iMultiBar == MultiBars.SIDEALL)) {
                    iNumBars++;
                    if (!flag)
                        iOrderPos++;
                } else if ((iMultiBar == MultiBars.STACKED) | (iMultiBar == MultiBars.STACKED100)) {
                    if (newGroup(((CustomBarNew) series).stackGroup)) {
                        iNumBars++;
                        if (!flag)
                            iOrderPos++;
                    }
                } else if (iMultiBar == MultiBars.SELFSTACK)
                    iNumBars = 1;
                if (!flag)
                    iPreviousCount += j;
            }
            int k = 0;
            do {
                if (k >= numGroups)
                    break;
                if (groups[k] == stackGroup) {
                    iOrderPos = k + 1;
                    break;
                }
                k++;
            } while (true);
            if (chart.getPage().getMaxPointsPerPage() > 0)
                iMaxBarPoints = chart.getPage().getMaxPointsPerPage();
            if (iMaxBarPoints > 0) {
                iMaxBarPoints = (int) (chart.getAxes().getBottom().getMaximum() - chart.getAxes()
                        .getBottom().getMinimum());
            }

        }

    }

    protected void drawTickLine(int i, BarStyle barstyle) {
    }

    protected void drawTickLines(int i, int j, BarStyle barstyle) {
        if (getTickLines().getVisible()) {
            chart.getGraphics3D().setPen(getTickLines());
            Axis axis = getMandatoryAxis();
            for (int k = 0; k < axis.axisDraw.ticks.length; k++)
                if (axis.axisDraw.ticks[k] > i && axis.axisDraw.ticks[k] < j)
                    drawTickLine(axis.axisDraw.ticks[k], barstyle);
        }
    }

    protected void drawLegendShape(IGraphics3D igraphics3d, int i, Rectangle rectangle) {
        if (getBrush().getImage() != null)
            igraphics3d.getBrush().setImage(bBrush.getImage());
        super.drawLegendShape(igraphics3d, i, rectangle);
    }

    public boolean drawValuesForward() {
        return iMultiBar != MultiBars.SELFSTACK ? super.drawValuesForward() : !getMandatoryAxis()
                .getInverted();
    }

    protected int applyBarOffset(int i) {
        int j = i;
        if (offsetPercent != 0)
            j += Utils.round((double) (offsetPercent * iBarSize) * 0.01D);
        return j;
    }

    protected int calcMarkLength(int i) {
        if (getCount() > 0 && getMarks().getVisible()) {
            chart.getGraphics3D().setFont(getMarks().getFont());
            int j = getMarks().getArrowLength() + internalCalcMarkLength(i);
            if (getMarks().getPen().getVisible())
                j += Utils.round(2 * getMarks().getPen().getWidth());
            return j;
        } else {
            return 0;
        }
    }

    protected boolean internalClicked(int i, Point point) {
        return false;
    }

    public int clicked(int i, int j) {
        if (chart != null) {
            Point point = chart.getGraphics3D().calculate2DPosition(i, j, getStartZ());
            i = point.x;
            j = point.y;
        }
        if (firstVisible > -1 && lastVisible > -1) {
            Point point1 = new Point(i, j);
            for (int k = firstVisible; k <= Math.min(lastVisible, getCount() - 1); k++)
                if (internalClicked(k, point1))
                    return k;
        }
        return -1;
    }

    protected int numSampleValues() {
        if (chart != null && chart.getSeriesCount() > 1) {
            for (int i = 0; i < chart.getSeriesCount(); i++) {
                Series series = chart.getSeries(i);
                if (series != this && (series instanceof CustomBarNew) && series.getCount() > 0)
                    return series.getCount();
            }
        }
        return 6;
    }

    protected void setPenBrushBar(Color color) {
        chart.getGraphics3D().setPen(pPen);
        if (color.isNull())
            chart.getGraphics3D().getPen().setColor(color);
        if (getBrush().getColor().isEmpty())
            getBrush().setColor(getColor());
        chart.setBrushCanvas(color, getBrush(), getColor());
    }

    protected boolean subGalleryStack() {
        return true;
    }

    public void createSubGallery(Gallery gallery) {
        super.createSubGallery(gallery);
        gallery.createSubChart(Language.getString("Colors"));
        gallery.createSubChart(Language.getString("Pyramid"));
        gallery.createSubChart(Language.getString("Ellipse"));
        gallery.createSubChart(Language.getString("InvPyramid"));
        gallery.createSubChart(Language.getString("Gradient"));
        if (subGalleryStack()) {
            gallery.createSubChart(Language.getString("Stack"));
            gallery.createSubChart(Language.getString("Stack"));
            gallery.createSubChart(Language.getString("SelfStack"));
        }
        gallery.createSubChart(Language.getString("Sides"));
        gallery.createSubChart(Language.getString("SideAll"));
    }

    public void setSubGallery(int i) {
        switch (i) {
            case 0: // '\0'
                break;

            case 1: // '\001'
                setColorEach(true);
                break;

            case 2: // '\002'
                setBarStyle(BarStyle.PYRAMID);
                break;

            case 3: // '\003'
                setBarStyle(BarStyle.ELLIPSE);
                break;

            case 4: // '\004'
                setBarStyle(BarStyle.INVPYRAMID);
                break;

            case 5: // '\005'
                setBarStyle(BarStyle.RECTGRADIENT);
                break;

            default:
                if (chart != null && chart.getSeriesCount() == 1) {
                    fillSampleValues(2);
                    try {
                        try {
                            Series series = (Series) getClass().newInstance();
                            getChart().addSeries(series);
                            series.setTitle("");
                            series.fillSampleValues(2);
                            series.getMarks().setVisible(false);
                            ((CustomBarNew) (CustomBarNew) series).barSizePercent = barSizePercent;
                            getMarks().setVisible(false);
                            series.setSubGallery(i);
                        } catch (InstantiationException instantiationexception) {
                        }
                    } catch (IllegalAccessException illegalaccessexception) {
                    }
                }
                if (!subGalleryStack())
                    i += 3;
                switch (i) {
                    case 6: // '\006'
                        setMultiBar(MultiBars.STACKED);
                        break;

                    case 7: // '\007'
                        setMultiBar(MultiBars.STACKED100);
                        break;

                    case 8: // '\b'
                        setMultiBar(MultiBars.SELFSTACK);
                        break;

                    case 9: // '\t'
                        setMultiBar(MultiBars.SIDE);
                        break;

                    case 10: // '\n'
                        setMultiBar(MultiBars.SIDEALL);
                        break;

                    default:
                        super.setSubGallery(i);
                        break;
                }
                break;
        }
    }

    protected int barSizePercent;
    protected int conePercent;
    protected boolean bDark3D;
    protected MultiBars iMultiBar;
    protected boolean bUseOrigin;
    protected boolean marksOnBar;
    protected double dOrigin;
    protected ChartPen pPen;
    private MarksLocation marksLocation;
    private boolean autoMarkPosition;
    private BarStyle barStyle;
    private boolean gradientRelative;
    private ChartPen tickLines;
    private int depthPercent;
    private int offsetPercent;
    private boolean sideMargins;
    private int stackGroup;
    protected Rectangle iBarBounds;
    protected int iNumBars;
    private int iMaxBarPoints;
    protected int iOrderPos;
    protected int iPreviousCount;
    private int groups[];
    private int numGroups;
    protected int iBarSize;
    protected int customBarSize;
    protected Color normalBarColor;
    private transient BarStyleResolver styleResolver;

    //柱的最大值
    private int maxWidth = 70;

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getINumBars() {
        return iNumBars;
    }
}
