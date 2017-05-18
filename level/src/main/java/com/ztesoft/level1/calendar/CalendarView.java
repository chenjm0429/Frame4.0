package com.ztesoft.level1.calendar;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ztesoft.level1.R;
import com.ztesoft.level1.util.PromptUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 文件名称 : CalendarView
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 完整的日历控件
 * <p>
 * 创建时间 : 2017/3/24 14:35
 * <p>
 */
public class CalendarView extends LinearLayout {

    private TextView mMonthText;
    private KCalendar mCalendar;

    private OnCalendarDateClickListener mOnCalendarDateClickListener;

    public CalendarView(Context context, String statDate, final String selectedStartDate, final
    String selectedEndDate) {
        super(context);

        View.inflate(context, R.layout.widget_calendar, this);

        mMonthText = (TextView) findViewById(R.id.calendar_month);
        mCalendar = (KCalendar) findViewById(R.id.calendar);

        mCalendar.setSelectedStartDate(selectedStartDate);
        mCalendar.setSelectedEndDate(selectedEndDate);

        if (TextUtils.isEmpty(statDate)) {  //默认值为空时，取当前时间
            Date curDate = new Date(System.currentTimeMillis());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.CHINESE);
            statDate = formatter.format(curDate);
        } else {
            if (statDate.length() != 8) {
                PromptUtils.instance.displayToastString(context, false, "默认时间格式不对！");
            }
        }

        String formatDate = statDate.substring(0, 4) + "-" + statDate.substring(4, 6) + "-" +
                statDate.substring(6, 8);

        int year = Integer.parseInt(statDate.substring(0, 4));
        int month = Integer.parseInt(statDate.substring(4, 6));

        mMonthText.setText(year + "年" + month + "月");

        mCalendar.showCalendar(year, month);
        mCalendar.setCalendarDayBgColor(formatDate, R.drawable.calendar_item_focused_bg);

        //监听所选中的日期
        mCalendar.setOnCalendarClickListener(new KCalendar.OnCalendarClickListener() {

            public void onCalendarClick(int row, int col, String dateFormat) {
                int month = Integer.parseInt(dateFormat.substring(dateFormat.indexOf("-") + 1,
                        dateFormat.lastIndexOf("-")));

                //跨年跳转
                if (mCalendar.getCalendarMonth() - month == 1 || mCalendar.getCalendarMonth() -
                        month == -11) {
                    mCalendar.lastMonth();

                    //跨年跳转
                } else if (month - mCalendar.getCalendarMonth() == 1 || month - mCalendar
                        .getCalendarMonth() == -11) {
                    mCalendar.nextMonth();

                } else {
                    String currentDate = dateFormat.replace("-", "");
                    if (!TextUtils.isEmpty(selectedStartDate) && !TextUtils.isEmpty
                            (selectedEndDate)) {
                        if (Integer.parseInt(selectedStartDate) > Integer.parseInt(currentDate)
                                || Integer.parseInt(selectedEndDate) < Integer.parseInt
                                (currentDate)) {
                            mOnCalendarDateClickListener.onUselessClick(currentDate, dateFormat);
                        } else {
                            mCalendar.removeAllBgColor();
                            mCalendar.setCalendarDayBgColor(dateFormat, R.drawable
                                    .calendar_item_focused_bg);
                            mOnCalendarDateClickListener.onClick(currentDate, dateFormat);
                        }
                    } else {
                        mCalendar.removeAllBgColor();
                        mCalendar.setCalendarDayBgColor(dateFormat, R.drawable
                                .calendar_item_focused_bg);
                        mOnCalendarDateClickListener.onClick(currentDate, dateFormat);
                    }
                }
            }
        });

        //监听当前月份
        mCalendar.setOnCalendarDateChangedListener(new KCalendar.OnCalendarDateChangedListener() {
            public void onCalendarDateChanged(int year, int month) {
                mMonthText.setText(year + "年" + month + "月");
            }
        });

        //上月监听按钮
        findViewById(R.id.calendar_last_month).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar.lastMonth();
            }
        });


        //下月监听按钮
        findViewById(R.id.calendar_next_month).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar.nextMonth();
            }
        });
    }

    public void setOnCalendarDateClickListener(OnCalendarDateClickListener
                                                       onCalendarDateClickListener) {
        this.mOnCalendarDateClickListener = onCalendarDateClickListener;
    }

    public interface OnCalendarDateClickListener {
        /**
         * 日历点击事件,获取选中时间
         *
         * @param statDate   选中的时间，格式yyyyMMdd
         * @param formatDate 选中的时间，格式yyyy-MM-dd
         */
        void onClick(String statDate, String formatDate);

        /**
         * 无效的点击事件，点击时间超出设置的范围
         *
         * @param statDate   选中的时间，格式yyyyMMdd
         * @param formatDate 选中的时间，格式yyyy-MM-dd
         */
        void onUselessClick(String statDate, String formatDate);
    }

    public void setSelectedStartDate(String selectedStartDate) {
        mCalendar.setSelectedStartDate(selectedStartDate);
    }

    public void setSelectedEndDate(String selectedEndDate) {
        mCalendar.setSelectedEndDate(selectedEndDate);
    }
}