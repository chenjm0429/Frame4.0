package com.ztesoft.level1.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

/**
 * 日期处理类
 *
 * @author 吴宏伟
 * @version 1.0
 * @since 1.0
 */

public class DateUtil {
    private static DateUtil ldu_ = null;

    public static DateUtil getInstance() {
        if (ldu_ == null) {
            ldu_ = new DateUtil();
        }
        return ldu_;
    }

    /**
     * 获取指定日期所在月的第一天
     *
     * @param dateStr
     * @return
     */
    public String getThisMonthStartDay(String dateStr) {
        return dateStr.substring(0, dateStr.length() - 2) + "01";
    }

    /**
     * 获取指定日期的上周同日
     *
     * @param dateStr
     * @param theFormat
     * @return
     */
    public String getLastWeekDate(String dateStr, String theFormat) {
        return convertDate(dateStr, theFormat, Calendar.DAY_OF_YEAR, -7);
    }

    /**
     * 获取指定日期所在月的前一天
     *
     * @param dateStr
     * @param theFormat
     * @return
     */
    public String getLastDate(String dateStr, String theFormat) {
        return convertDate(getThisMonthStartDay(dateStr), theFormat, Calendar.DATE, -1);
    }

    /**
     * 返回上一个统计月份
     *
     * @param acycID
     * @param theFormat
     * @return
     */
    public String getLastMonth(String acycID, String theFormat) {
        return convertDate(acycID.substring(0, 4) + "-" + acycID.substring(4, 6) + "-01",
                theFormat, Calendar.MONTH, -1)
                .substring(0, 6);
    }

    /**
     * 方法描述：返回上月同期的日期
     *
     * @param acycID
     * @param theFormat
     * @return
     */
    public String getLastMonthDate(String acycID, String theFormat) {
        return convertDate(acycID, theFormat, Calendar.MONTH, -1).substring(0, acycID.length());
    }

    /**
     * 方法描述：返回下月同期的日期
     *
     * @param acycID
     * @param theFormat
     * @return
     */
    public String getNextMonthDate(String acycID, String theFormat) {
        return convertDate(acycID, theFormat, Calendar.MONTH, 1).substring(0, acycID.length());
    }

    /**
     * 方法描述：返回去年同期的日期
     *
     * @param acycID
     * @param theFormat
     * @return
     */
    public String getLastYearDate(String acycID, String theFormat) {
        return convertDate(acycID, theFormat, Calendar.YEAR, -1).substring(0, acycID.length());
    }

    /**
     * 方法描述：返回明年同期的日期
     *
     * @param acycID
     * @param theFormat
     * @return
     */
    public String getNextYearDate(String acycID, String theFormat) {
        return convertDate(acycID, theFormat, Calendar.YEAR, 1).substring(0, acycID.length());
    }

    /**
     * 按照指定格式获取指定日期所在月的上个月的第一天
     *
     * @param dateStr
     * @param theFormat
     * @return
     */
    public String getLastMonthStartDay(String dateStr, String theFormat) {
        return convertDate(getThisMonthStartDay(dateStr), theFormat, Calendar.MONTH, -1);
    }

    /**
     * 按照指定格式获取指定日期所在月的上个月的最后一天
     *
     * @param dateStr
     * @param theFormat
     * @return
     */
    public String getLastMonthEndDay(String dateStr, String theFormat) {
        return convertDate(getThisMonthStartDay(dateStr), theFormat, Calendar.DAY_OF_YEAR, -1);
    }

    /**
     * <b>摘要： </b> 获取今天的日期。 <br>
     * <b>参数： </b> theFormat设置日期输出格式，例如"yyyyMMddHHmmss"。 <br>
     * <b>返回值：</b> 指定格式的今天日期，返回值类型是String。
     */
    public String getToday(String theFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(theFormat, Locale.CHINESE);
        Date thedate = new Date();
        return formatter.format(thedate);
    }

    /**
     * <b>摘要： </b> 获取昨天的日期。 <br>
     * <b>参数： </b> theFormat设置日期输出格式，例如"yyyyMMdd"。 <br>
     * <b>返回值：</b> 指定格式的昨天日期，返回值类型是String。
     */
    public String getYesterday(String todaystr, String theFormat) {
        return convertDate(todaystr, theFormat, Calendar.DAY_OF_YEAR, -1);
    }

    /**
     * <b>摘要： </b> 获取明天的日期。 <br>
     * <b>参数： </b> theFormat设置日期输出格式，例如"yyyyMMdd"。 <br>
     * <b>返回值：</b> 指定格式的明天日期，返回值类型是String。
     */
    public String getTomorrow(String todaystr, String theFormat) {
        return convertDate(todaystr, theFormat, Calendar.DAY_OF_YEAR, 1);
    }

    /**
     * 获取当前日期在所在月的第几周
     *
     * @param iYear
     * @param iMonth
     * @param iDay
     * @return
     */
    public int getCurWeekOfMonth(int iYear, int iMonth, int iDay) {
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.set(iYear, iMonth, iDay);
        iDay = newCalendar.get(Calendar.WEEK_OF_MONTH);
        return iDay;
    }

    /**
     * <b>摘要： </b> 计算相对于dateStr的日期，如果转换日期为上个月同期日期
     * dateStr,theFormat="yyyyMMdd"，feildnum=Calendar.WEEK_OF_YEAR，thenum=-4。 <br>
     * <b>参数： </b> dateStr设置参照的日期，theFormat设置参照日期格式，feildnum处理方式，thenum计算值。 <br>
     * <b>返回值：</b> 指定格式的相对于dateStr的日期，返回值类型是String。
     */
    public String convertDate(String dateStr, String theFormat, int feildnum, int thenum) {
        SimpleDateFormat formatter = null;
        Calendar cldr = null;
        Date date_pre = null;
        Date date1 = null;
        try {
            formatter = new SimpleDateFormat(theFormat, Locale.CHINESE);
            cldr = new GregorianCalendar();
            date1 = formatter.parse(dateStr);
            cldr.setTime(date1);
            cldr.add(feildnum, thenum);
            date_pre = cldr.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (date_pre == null)
            return formatter.format(date1);
        else
            return formatter.format(date_pre);
    }

    /**
     * 返回两个时间的之间相差的天数
     *
     * @param dateStr1  结束时间
     * @param datestr2  开始时间
     * @param theFormat 日期格式
     * @return
     */
    public int calDate(String dateStr1, String datestr2, String theFormat) {
        SimpleDateFormat formatter = null;
        Date date1;
        Date date2;
        int num = 0;
        try {
            formatter = new SimpleDateFormat(theFormat, Locale.CHINESE);
            date1 = formatter.parse(dateStr1);
            date2 = formatter.parse(datestr2);
            num = Integer.parseInt(String.valueOf((date1.getTime() - date2.getTime()) / 1000 / 60
                    / 60 / 24));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    /**
     * 返回两个时间的之间相差的天数
     *
     * @param date1 Date.getTime()
     * @param date2 Date.getTime()
     * @return
     */
    public int calDate(long date1, long date2) {
        return Integer.parseInt(String.valueOf((date1 - date2) / 1000 / 60 / 60 / 24));
    }


    /**
     * 返回两个日期之间的日期（是指日）
     *
     * @param dateStr1
     * @param datestr2
     * @param theFormat
     * @return
     */
    public Vector<String> betweenDates(String dateStr1, String datestr2, String theFormat) {
        Vector<String> v1 = new Vector<String>();
        int checknum = calDate(datestr2, dateStr1, theFormat);
        if (checknum > 0) {
            for (int i = 0; i <= checknum; i++) {
                String datestr = convertDate(dateStr1, "yyyyMMdd", Calendar.DAY_OF_YEAR, i);
                v1.add(datestr);
            }
            return v1;
        } else if (checknum == 0) {
            v1.add(dateStr1);
            return v1;
        } else {
            return null;
        }
    }

    /**
     * 返回两个月份之间的月份
     *
     * @param fromDate
     * @param toDate
     * @return
     */
    public Vector<String> betweenMons(String fromDate, String toDate) {
        try {
            if (Double.parseDouble(fromDate) > Double.parseDouble(toDate)) {
                return null;
            }
            Vector<String> v1 = new Vector<String>();
            String currMonth = fromDate;
            v1.add(currMonth);
            while (!currMonth.equals(toDate)) {
                currMonth = convertDate(currMonth, "yyyyMM", GregorianCalendar.MONTH, 1);
                v1.add(currMonth);
            }

            return v1;
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    /**
     * <b>摘要： </b> 转换str_d日期的格式。 <br>
     * <b>参数： </b> str_d需要转换格式的日期，Format_Old设置旧日期格式，Format_New设置新日期格式。 <br>
     * <b>返回值：</b> 指定格式的相对于str_d的日期，返回值类型是String。
     */
    public String convertDay_Type(String str_d, String Format_Old, String Format_New) {
        SimpleDateFormat sdf = new SimpleDateFormat(Format_Old, Locale.CHINESE);
        SimpleDateFormat sdf2 = new SimpleDateFormat(Format_New, Locale.CHINESE);
        String Str_date = "";
        try {
            Date d1 = sdf.parse(str_d);
            Str_date = sdf2.format(d1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Str_date;
    }

    /**
     * 返回指定日期的所在周的周几日期（周日是一周的结束）
     *
     * @param dateStr
     * @param thenum  取1~7,7表示星期日
     * @return
     */
    public String convertDay_Week(String dateStr, int thenum) {
        SimpleDateFormat formatter = null;
        Calendar cldr;
        Date date_pre = null;
        Date date1 = null;
        try {
            formatter = new SimpleDateFormat("yyyyMMdd", Locale.CHINESE);
            cldr = new GregorianCalendar();
            date1 = formatter.parse(dateStr);
            cldr.setTime(date1);
            int iWeek = cldr.get(Calendar.DAY_OF_WEEK);

            int i;
            if (iWeek == 1) { // 指定日期是星期日
                i = thenum - iWeek - 6;
            } else {
                i = thenum - iWeek + 1;
            }
            cldr.add(Calendar.DAY_OF_YEAR, i);

            date_pre = cldr.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (date_pre == null)
            return formatter.format(date1);
        else
            return formatter.format(date_pre);
    }

    /**
     * 返回指定日期的所在周的周几日期；（周日是一周的结束）
     *
     * @param dateStr
     * @return
     */
    public int convDay_Week(String dateStr) {
        SimpleDateFormat formatter = null;
        Calendar cldr;
        Date date1;
        int iWeek = 0;
        try {
            formatter = new SimpleDateFormat("yyyyMMdd", Locale.CHINESE);
            cldr = new GregorianCalendar();
            date1 = formatter.parse(dateStr);
            cldr.setTime(date1);
            iWeek = cldr.get(Calendar.DAY_OF_WEEK);
            if (iWeek == 1) { // 指定日期是星期日
                iWeek = 7;
            } else {
                iWeek = iWeek - 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iWeek;
    }

    /**
     * 获得当前月的天数
     *
     * @param iYear
     * @param iMonth
     * @return
     */
    public int getDayOfCurrentMonth(int iYear, int iMonth) {
        int iDayCount = 31;
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.set(iYear, iMonth, 1);
        newCalendar.add(Calendar.DATE, -1);
        int iDay = newCalendar.get(Calendar.DATE);
        if (iDay > 10)
            iDayCount = iDay;
        return iDayCount;
    }

    /**
     * 获得当前月的周几个数
     *
     * @param iYear
     * @param iMonth
     * @param thenum thenum取1~7;7表示星期日
     * @return 所有符合条件的日期
     */
    public String[] getWeekOfCurrentMonth(int iYear, int iMonth, int minDay, int maxDay, int
            thenum) {
        List<Integer> l = new ArrayList<Integer>();
        Date date1;
        try {
            for (int i = minDay; i <= maxDay; i++) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.CHINESE);
                String date = iYear + "";
                if (iMonth < 10) {
                    date += "0" + iMonth;
                } else {
                    date += "" + iMonth;
                }
                if (i < 10) {
                    date += "0" + i;
                } else {
                    date += "" + i;
                }
                date1 = formatter.parse(date);
                GregorianCalendar cldr = new GregorianCalendar();
                cldr.setTime(date1);
                int iWeek = cldr.get(Calendar.DAY_OF_WEEK);
                if (iWeek == 1) { // 指定日期是星期日
                    iWeek = 7;
                } else {
                    iWeek--;
                }
                if (iWeek == thenum) { // 指定日期
                    l.add(i);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] ddd = new String[l.size()];
        for (int i = 0; i < l.size(); i++) {
            ddd[i] = l.get(i) + "";
        }
        return ddd;
    }

    /**
     * 将yyyyMMdd格式的日期转换成calendar
     *
     * @param date
     * @param needAddDay
     * @return
     */
    public Calendar getTargetCalendar(String date, boolean needAddDay) {
        Calendar calendar = Calendar.getInstance();
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);

        int yearInt = Integer.valueOf(year);
        int monthInt = Integer.valueOf(month);
        int dayInt = Integer.valueOf(day);

        calendar.set(Calendar.YEAR, yearInt);
        calendar.set(Calendar.MONTH, monthInt - 1);
        if (needAddDay) {
            calendar.set(Calendar.DAY_OF_MONTH, dayInt + 1);
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, dayInt);
        }

        return calendar;
    }

    /**
     * 将yyyyMM格式的日期转换成calendar
     *
     * @param date
     * @return calendar
     */
    public Calendar getTargetMonthCalendar(String date) {
        Calendar calendar = Calendar.getInstance();

        String year = date.substring(0, 4);
        String month = date.substring(4, 6);

        int yearInt = Integer.valueOf(year);
        int monthInt = Integer.valueOf(month);

        calendar.set(Calendar.YEAR, yearInt);
        calendar.set(Calendar.MONTH, monthInt - 1);

        return calendar;
    }

    /**
     * 时间格式化
     *
     * @param date       时间
     * @param fromFormat 原本的格式
     * @param toFormat   目标格式
     * @return
     */
    public String getFormatDate(String date, String fromFormat, String toFormat) {

        SimpleDateFormat sdf = new SimpleDateFormat(fromFormat);
        Calendar cal = Calendar.getInstance();

        try {
            cal.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String titleDate = new SimpleDateFormat(toFormat, Locale.CHINESE).format(cal.getTime());

        return titleDate;
    }
}