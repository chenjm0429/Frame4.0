package com.ztesoft.level1.datacalendar;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ztesoft.level1.util.DateUtil;

import java.util.Date;

public class DataCalendar extends LinearLayout {
    private Context ctx;
    private TextView showDate;  //显示年、月
    private TextView left;
    private TextView right;
    private String format = "yyyy年MM月"; //日期格式
    private Date currentDate;  //日历组件初始化日期
    private Date[] checks;
    private DateUI dd;

    //选择某一天后触发事件
    private DateUI.OnDateClickListener onDateClickListener;
    private DateUI.OnMonChgClickListener onMonChgClickListener;

    //日历组件日期范围初始化
//	private Date startDate;
//	private Date endDate;

    public void setOnDateClick(DateUI.OnDateClickListener onDateClickListener) {
        this.onDateClickListener = onDateClickListener;
    }

//	public void setStartDate(Date startDate) {
//		this.startDate = startDate;
//	}
//
//	public void setEndDate(Date endDate) {
//		this.endDate = endDate;
//	}

    public Date[] getChecks() {
        return checks;
    }

    public void setChecks(Date[] checks) {
        this.checks = checks;
        if (null != checks && null != dd) {
            dd.setChecks(checks);
            dd.invalidate();
        }
    }

    public DateUI.OnMonChgClickListener getOnMonChgClickListener() {
        return onMonChgClickListener;
    }

    public void setOnMonChgClickListener(DateUI.OnMonChgClickListener onMonChgClickListener) {
        this.onMonChgClickListener = onMonChgClickListener;
    }

    public Date getStartDate() {
        if (null == dd)
            return new Date();
        else
            return dd.getSelectedStartDate();
    }

    public Date getEndDate() {
        if (null == dd)
            return new Date();
        else
            return dd.getSelectedEndDate();
    }

    public Date getCurrentDate() {
        if (null == dd)
            return new Date();
        else
            return dd.getSelectedEndDate();
    }

    public void setToday(Date currentDate) {
        this.currentDate = currentDate;

        if (null != currentDate && null != dd) {
            dd.setCurrentDate(currentDate);
            dd.invalidate();
        }
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public DataCalendar(Context context) {
        super(context);
        ctx = context;
    }

    public DataCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
    }

    private int textColor = Color.BLACK;

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    private int padding = 20;
    private int textSize = 20;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);

        textSize = (int) (height / 22f);
        left.setTextSize(textSize);
        right.setTextSize(textSize);
        showDate.setTextSize(textSize);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void init() {
        this.setOrientation(VERTICAL);
        this.removeAllViews();
        left = new TextView(ctx);
        left.setTextColor(textColor);
        left.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        left.setText("<");
        left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dd.clickLeftMonth();
            }
        });

        right = new TextView(ctx);
        right.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        right.setTextColor(textColor);
        right.setText(">");
        right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dd.clickRightMonth();
            }
        });

        showDate = new TextView(ctx);
        showDate.setTextColor(textColor);
        showDate.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        LinearLayout top = new LinearLayout(ctx);
//		top.setBackgroundColor(Color.WHITE);
        top.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        top.addView(left);
        top.addView(showDate);
        top.addView(right);

        this.addView(top);

        dd = new DateUI(ctx);
        if (null != currentDate)
            dd.setCurrentDate(currentDate);
//		if(null!=startDate)
//			dd.setSelectedStartDate(startDate);
//		if(null!=endDate)
//			dd.setSelectedEndDate(endDate);
        if (null != checks)
            dd.setChecks(checks);
        dd.setTextColor(textColor);
        if (null != onDateClickListener)
            dd.setOnDateClick(onDateClickListener);
        if (null != onMonChgClickListener)
            dd.setOnMonChgClickListener(onMonChgClickListener);

        dd.setInnerMonChgClickListener(new DateUI.InnerMonChgClickListener() {
            @Override
            public void onMonChg(String yearAndMonth) {
                showDate.setText(DateUtil.getInstance().convertDay_Type(yearAndMonth, "yyyy-MM", 
                        format));
            }
        });
        dd.init();
        this.addView(dd, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

        showDate.setText(DateUtil.getInstance().convertDay_Type(dd.getYearAndmonth(), "yyyy-MM",
                format));
        showDate.setPadding(padding, 0, padding, 0);
    }
}
