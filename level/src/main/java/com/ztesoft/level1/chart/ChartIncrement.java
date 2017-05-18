package com.ztesoft.level1.chart;

import com.steema.teechart.axis.Axis;

import java.math.BigDecimal;

/**
 * 图形数据操作类 作用：调整最大值、最小值<取整>,同时计算出合适的间隔
 *
 * @author fanlei@asiainfo-linkage.com 2012-12-28 上午11:42:49
 * @ClassName: ChartIncrement
 */
public class ChartIncrement {
    // 最大值
    private double maxValue = 90;
    // 最小值
    private double minValue = 0;
    private double sRange = 0;
    // 间隔距离
    private double sInterval;
    // 间隔个数（刻度个数-1）
    private int numDivLines = 4;

    /**
     * 得到坐标轴label显示的间隔值
     *
     * @param maxValue
     * @param minValue
     * @author fanlei@asiainfo-linkage.com 2012-12-28 上午11:43:52
     */
    private void getAxisLimits(double maxValue, double minValue) {
        this.maxValue = maxValue;
        this.minValue = minValue;
        if ((this.maxValue == this.minValue)) {
            this.maxValue = this.minValue + 20;
        }
        double maxPowerOfTen = Math.floor(Math.log(Math.abs(this.maxValue)) / Math.log(10));
        double minPowerOfTen = Math.floor(Math.log(Math.abs(this.minValue)) / Math.log(10));
        double powerOfTen = Math.max(minPowerOfTen, maxPowerOfTen);
        double y_interval = Math.pow(10, powerOfTen);
        if (Math.abs(this.maxValue) / y_interval < 2 && Math.abs(this.minValue) / y_interval < 2) {
            powerOfTen--;
            y_interval = Math.pow(10, powerOfTen);
        }
        double rangePowerOfTen = Math.floor(Math.log(this.maxValue - this.minValue) / Math.log(10));
        double rangeInterval = Math.pow(10, rangePowerOfTen);
        if (((this.maxValue - this.minValue) > 0) && ((y_interval / rangeInterval) >= 10)) {
            y_interval = rangeInterval;
            powerOfTen = rangePowerOfTen;
        }
        double y_topBound = (Math.floor(this.maxValue / y_interval) + 1) * y_interval;
        double y_lowerBound;
        if (this.minValue < 0) {
            y_lowerBound = -1 * ((Math.floor(Math.abs(this.minValue / y_interval)) + 1) *
                    y_interval);
        } else {
            y_lowerBound = Math.floor(Math.abs(this.minValue / y_interval) - 1) * y_interval;
            y_lowerBound = (y_lowerBound < 0) ? 0 : y_lowerBound;
        }
        this.maxValue = y_topBound;
        this.minValue = y_lowerBound;
        this.sRange = Math.abs(this.maxValue - this.minValue);
        this.sInterval = y_interval;
        calcDivs();
    }

    private void calcDivs() {
        if (this.maxValue > 0 && this.minValue < 0) {//跨越正负值
            boolean found = false;
            double adjInterval = (this.sInterval > 10) ? (this.sInterval / 10) : this.sInterval;
            double range = getDivisibleRange(this.minValue, this.maxValue, numDivLines,
                    adjInterval, false);
            double nextRange = range - (numDivLines + 1) * (adjInterval);
            double rangeDiv;
            double deltaRange;
            double mf;
            double smallArm, bigArm;
            double extSmallArm, extBigArm;
            while (found == false) {
                // Get range
                nextRange = nextRange + (numDivLines + 1) * (adjInterval);
                // If it's divisible for the given range and adjusted interval,
                // proceed
                if (isRangeDivisible(nextRange, numDivLines, adjInterval)) {
                    // Delta Range
                    deltaRange = nextRange - this.sRange;
                    // Range division
                    rangeDiv = nextRange / (numDivLines + 1);
                    // Get the smaller arm of axis
                    smallArm = Math.min(Math.abs(this.minValue), this.maxValue);
                    // Bigger arm of axis.
                    bigArm = this.sRange - smallArm;
                    // Get the multiplication factor (if smaller arm is
                    // negative, set -1);
                    mf = (smallArm == Math.abs(this.minValue)) ? -1 : 1;
                    // If num div lines ==0, we do not calculate anything
                    if (numDivLines == 0) {
                        // Set flag to true to exit loop
                        found = true;
                    } else {
                        // Now, we need to make sure that the smaller arm of
                        // axis is a multiple
                        // of rangeDiv and the multiplied result is greater than
                        // smallArm.
                        for (int i = 1; i <= Math.floor((numDivLines + 1) / 2); i++) {
                            // Get extended small arm
                            extSmallArm = rangeDiv * i;
                            // If extension is more than original intended
                            // delta, we move to next
                            // value of loop as this range is smaller than our
                            // intended range
                            if ((extSmallArm - smallArm) > deltaRange) {
                                // Iterate to next loop value
                                continue;
                            }
                            // Else if extended arm is greater than smallArm, we
                            // do the 0 div test
                            if (extSmallArm > smallArm) {
                                // Get extended big arm
                                extBigArm = nextRange - extSmallArm;
                                // Check whether for this range, 0 can come as a
                                // div
                                // By checking whether both extBigArm and
                                // extSmallArm
                                // are perfectly divisible by rangeDiv
                                if (((extBigArm / rangeDiv) == (Math.floor(extBigArm / rangeDiv)))
                                        && ((extSmallArm / rangeDiv) == (Math.floor(extSmallArm /
                                        rangeDiv)))) {
                                    // Store in global containers
                                    this.sRange = nextRange;
                                    this.maxValue = (mf == -1) ? extBigArm : extSmallArm;
                                    this.minValue = (mf == -1) ? (-extSmallArm) : (-extBigArm);
                                    // Set found flag to true to exit loop
                                    found = true;
                                }
                            } else {
                                // Iterate to next loop value, as we need the
                                // arm to be greater
                                // than original value.
                                continue;
                            }
                        }
                    }
                }
            }
        } else {//都为正值或都为负值
            // Case 1.1 or 1.2
            /**
             * In this case, we first get apt divisible range based on yMin,
             * yMax, numDivLines and the calculated interval. Thereby, get the
             * difference between original range and new range and store as
             * delta. If yMax>0, add this delta to yMax. Else substract from
             * yMin.
             */
            // Get the adjusted divisible range
            double adjRange = getDivisibleRange(this.minValue, this.maxValue, numDivLines, this
                    .sInterval, true);
            // Get delta (Calculated range minus original range)
            double deltaRange = adjRange - this.sRange;
            // Update global range storage
            this.sRange = adjRange;
            // Now, add the change in range to yMax, if yMax > 0, else deduct
            // from yMin
            if (this.maxValue > 0) {
                this.maxValue = this.maxValue + deltaRange;
            } else {
                this.minValue = this.minValue - deltaRange;
            }
        }

        /**
         * First get apt divisible range based on yMin, yMax, numDivLines and
         * the calculated interval. Thereby, get the difference between original
         * range and new range and store as delta. If yMax>0, add this delta to
         * yMax. Else substract from yMin.
         */
        // Set flag that we do not have to format div (y-axis values) decimals
        // Get the adjusted divisible range
        double adjRange = getDivisibleRange(this.minValue, this.maxValue, numDivLines, this
                .sInterval, true);
        // Get delta (Calculated range minus original range)
        double deltaRange = adjRange - this.sRange;
        // Update global range storage
        this.sRange = adjRange;
        // Now, add the change in range to yMax, if yMax > 0, else deduct from
        // yMin
        //调整最大值，符合整除范围
        if (this.maxValue > 0) {
            this.maxValue = this.maxValue + deltaRange;
        } else {
            this.minValue = this.minValue - deltaRange;
        }

        this.sInterval = (this.maxValue - this.minValue) / (numDivLines + 1);
    }

    /**
     * 获取能够整除的范围
     *
     * @param yMin
     * @param yMax
     * @param numDivLines    刻度个数
     * @param interval       刻度间隔值
     * @param interceptRange
     * @return
     */
    private double getDivisibleRange(double yMin, double yMax, int numDivLines, double interval, 
                                     boolean interceptRange) {
        // Get the range division for current yMin, yMax and numDivLines
        double range = Math.abs(yMax - yMin);
        double rangeDiv = range / (numDivLines + 1);
        // Now, the range is not divisible
        if (!isRangeDivisible(range, numDivLines, interval)) {//不能整除，则调整范围到可整除
            // We need to get new rangeDiv which can be equally distributed.
            // If intercept range is set to true
            if (interceptRange) {
                // Re-adjust interval so that gap is not much (conditional)
                // Condition check limit based on value
                double checkLimit = (interval > 1) ? 2 : 0.5;
                if (rangeDiv / interval < checkLimit) {
                    // Decrease power of ten to get closer rounding
                    interval = interval / 10;
                }
            }
            // Adjust range division based on new interval
            rangeDiv = (Math.floor(rangeDiv / interval) + 1) * interval;
            // Get new range
            range = rangeDiv * (numDivLines + 1);
        }
        // Return range
        return range;
    }

    /**
     * 是否可以整除
     *
     * @param range       最大-最小值的绝对值
     * @param numDivLines 刻度个数
     * @param interval    刻度间隔值
     * @return 是否可整除
     */
    private boolean isRangeDivisible(double range, double numDivLines, double interval) {
        // Get range division
        double rangeDiv = range / (numDivLines + 1);
        // Now, if the decimal places of rangeDiv and interval do not match,
        // it's not divisible, else it's divisible
        if (numDecimals(rangeDiv) > numDecimals(interval)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 返回有几位小数
     *
     * @param num
     * @return
     */
    private double numDecimals(double num) {
        String str = num + "";
        if (str.indexOf(".") != -1) {
            str = str.substring(str.indexOf(".") + 1, str.length());
            if ("0".equals(str.trim())) {
                return 0;
            } else {
                return str.length();
            }
        } else {
            return 0;
        }
    }

    /***
     * 给坐标轴赋值
     *
     * @param ax 坐标轴
     * @param minValue 最小值
     * @param maxValue 最大值
     * @param format 格式化
     */
    public void setAxesValue(Axis ax, double minValue, double maxValue, String format) {
        int n = 1;
        if (maxValue < 1) {
            n = 1000000;
        }
        ax.setAutomatic(false);
        getAxisLimits(maxValue * n, minValue * n);
        double inc = sInterval / n;
        if (inc > 0.0001) {
            BigDecimal bg = new BigDecimal(inc);
            inc = bg.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
        } else {
            if (format != null && format.trim().length() > 0 && format.contains("%")) {
                ax.getLabels().setValueFormat("#,##0.###%");
            }
        }
        ax.setIncrement(inc);
        ax.setMinMax(this.minValue / n, this.maxValue / n);
    }

    /**
     * 设置刻度个数，默认4
     *
     * @param numDivLines 刻度个数
     */
    public void setNumDivLines(int numDivLines) {
        this.numDivLines = numDivLines;
    }
}
