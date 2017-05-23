// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AxisLabels.java
package com.steema.teechart.axis;

import com.steema.teechart.DateTime;
import com.steema.teechart.MultiLine;
import com.steema.teechart.TextShape;
import com.steema.teechart.languages.Language;

import java.text.DecimalFormat;

// Referenced classes of package com.steema.teechart.axis:
//            AxisLabelsItems, Axis, AxisLabelStyle, AxisLabelAlign, 
//            AxisLabelResolver
public class AxisLabels extends TextShape {

    public AxisLabels(Axis axis1) {
        super(axis1.chart);
        bOnAxis = true;
        iSeparation = 10;
        iStyle = AxisLabelStyle.AUTO;
        axisvaluesformat = Language.getString("DefValueFormat");
        sDatetimeformat = "";
        align = AxisLabelAlign.DEFAULT;
        exactDateTime = true;
        roundfirstlabel = true;
        axis = axis1;
        bTransparent = true;
        items = new AxisLabelsItems(axis1);
        readResolve();
    }

    protected Object readResolve() {
        valuesDecimal = new DecimalFormat(Language.getString("DefValueFormat"));
        return this;
    }

    protected boolean shouldSerializeTransparent() {
        return !bTransparent;
    }

    public boolean getExactDateTime() {
        return exactDateTime;
    }

    public void setExactDateTime(boolean flag) {
        exactDateTime = setBooleanProperty(exactDateTime, flag);
    }

    public int getAngle() {
        return iAngle;
    }

    public void setAngle(int i) {
        iAngle = setIntegerProperty(iAngle, i % 360);
    }

    public boolean getOnAxis() {
        return bOnAxis;
    }

    public void setOnAxis(boolean flag) {
        bOnAxis = setBooleanProperty(bOnAxis, flag);
    }

    public int getSeparation() {
        return iSeparation;
    }

    public void setSeparation(int i) {
        iSeparation = setIntegerProperty(iSeparation, i);
    }

    public int getCustomSize() {
        return customSize;
    }

    public void setCustomSize(int i) {
        customSize = setIntegerProperty(customSize, i);
    }

    public AxisLabelStyle getStyle() {
        return iStyle;
    }

    public void setStyle(AxisLabelStyle axislabelstyle) {
        if (iStyle != axislabelstyle) {
            iStyle = axislabelstyle;
            invalidate();
        }
    }

    public boolean getRoundFirstLabel() {
        return roundfirstlabel;
    }

    public void setRoundFirstLabel(boolean flag) {
        roundfirstlabel = setBooleanProperty(roundfirstlabel, flag);
    }

    public String getDateTimeFormat() {
        return sDatetimeformat;
    }

    public void setDateTimeFormat(String s) {
        sDatetimeformat = setStringProperty(sDatetimeformat, s);
    }

    public String getValueFormat() {
        return axisvaluesformat;
    }

    public void setValueFormat(String s) {
        if (axisvaluesformat != s) {
            axisvaluesformat = setStringProperty(axisvaluesformat, s);
            valuesDecimal = new DecimalFormat(axisvaluesformat);
        }
    }

    /***
     * 是否以K,M显示
     * @param numberScale
     */
    public void setNumberScale(boolean numberScale) {
        this.numberScale = numberScale;
    }

    public boolean getMultiLine() {
        return multiline;
    }

    public void setMultiLine(boolean flag) {
        multiline = setBooleanProperty(multiline, flag);
    }

    public String splitInLines(String s, String s1) {
        return s.replaceAll(s1, Language.lineSeparator);
    }

    public String splitInLines(String s, char c) {
        return s.replace(c, Language.lineSeparator.charAt(0));
    }

    public boolean getExponent() {
        return bExponent;
    }

    public void setExponent(boolean flag) {
        bExponent = setBooleanProperty(bExponent, flag);
    }

    public AxisLabelAlign getAlign() {
        return align;
    }

    public void setAlign(AxisLabelAlign axislabelalign) {
        if (align != axislabelalign) {
            align = axislabelalign;
            invalidate();
        }
    }

    public AxisLabelsItems getItems() {
        return items;
    }

    private int internalLabelSize(double d, boolean flag) {
        MultiLine multiline1 = chart.multiLineTextWidth(labelValue(d));
        int i = multiline1.width;
        int j = multiline1.count;
        boolean flag1;
        if (flag)
            flag1 = iAngle == 90 || iAngle == 270;
        else
            flag1 = iAngle == 0 || iAngle == 180;
        if (flag1)
            i = chart.getGraphics3D().getFontHeight() * j;
        return i;
    }

    public int labelWidth(double d) {
        return internalLabelSize(d, true);
    }

    public int labelHeight(double d) {
        return internalLabelSize(d, false);
    }

    public String labelValue(double d) {
        String s;
        if (axis.iAxisDateTime)
            try {
                if (sDatetimeformat.length() == 0)
                    s = (new DateTime(d)).toString(axis.dateTimeDefaultFormat(axis.iRange));
                else
                    s = (new DateTime(d)).toString(sDatetimeformat);
            } catch (Exception exception) {
                s = (new DateTime(d)).toString(axis.dateTimeDefaultFormat(axis.iRange));
            }
        else
            try {
                String format = getValueFormat();
                double absd = Math.abs(d);
                if (numberScale) {
                    if (absd > 1000 && absd < 1000000) {
                        d = d / 1000;
                        if (format.contains(".")) {
                            format = format + "K";
                        } else {
                            format = format + ".##K";
                        }

                    } else if (absd >= 1000000 && absd < 1000000000) {
                        d = d / 1000000;
                        if (format.contains(".")) {
                            format = format + "M";
                        } else {
                            format = format + ".##M";
                        }
                    } else if (absd >= 1000000000) {
                        d = d / 1000000000;
                        if (format.contains(".")) {
                            format = format + "B";
                        } else {
                            format = format + ".##B";
                        }
                    } else {
                        if (!format.contains(".")) {
                            format = format + ".###";
                        }
                    }
                    s = (new DecimalFormat(format)).format(d);
                } else {
                    s = (new DecimalFormat(format)).format(d);
                }

            } catch (Exception exception1) {
                s = (new DecimalFormat(Language.getString("DefValueFormat"))).format(d);
            }
        if (chart.getParent() != null)
            s = chart.getParent().getAxisLabelResolver().getLabel(axis, null, -1, s);
        if (multiline)
            s = splitInLines(s, " ");
        return s;
    }

    public boolean getAlternate() {
        return labelsAlternate;
    }

    public void setAlternate(boolean flag) {
        labelsAlternate = setBooleanProperty(labelsAlternate, flag);
    }

    public int getPosition() {
        return position;
    }

    protected int iAngle;
    protected boolean bOnAxis;
    protected int iSeparation;
    public AxisLabelStyle iStyle;
    protected boolean bExponent;
    protected int position;
    protected String axisvaluesformat;
    protected transient DecimalFormat valuesDecimal;
    protected String sDatetimeformat;
    protected transient Axis axis;
    private AxisLabelAlign align;
    private int customSize;
    private boolean multiline;
    private boolean exactDateTime;
    private boolean roundfirstlabel;
    private AxisLabelsItems items;
    private boolean labelsAlternate;
    private boolean numberScale = false;
    //是否显示
    private boolean[] visibilities = null;

    public boolean[] getVisibilities() {
        return visibilities;
    }

    public void setVisibilities(boolean[] visibilities) {
        this.visibilities = visibilities;
    }
}