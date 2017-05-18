package com.ztesoft.level1.radiobutton.util;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ztesoft.level1.Level1Util;
import com.ztesoft.level1.R;

/**
 * 文件名称 : DateDialog
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 时间选择器弹出框
 * <p>
 * 创建时间 : 2017/5/5 14:36
 * <p>
 */
public class DateDialog extends Dialog {

    private Context context;

    private TextView mCurrentYearText, mCurrentDayText;

    private TextView mYearText, mMonthText, mDayText, mHourText, mMinuteText;

    private LinearLayout mContainerLayout;

    private TextView mCancelText, mConfirmText;

    private boolean isShowDay = true;  //是否显示日

    private boolean isShowHourAndMinute = true;  //是否显示小时和分钟

    public DateDialog(Context context) {
        this(context, R.style.prompt_style);
    }

    public DateDialog(Context context, int theme) {
        super(context, theme);

        this.context = context;

        setContentView(R.layout.dialog_date);

        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) (Level1Util.getDeviceWidth(context) * 0.9);
        window.setAttributes(lp);

        initParam();
    }

    private void initParam() {
        mCurrentYearText = (TextView) findViewById(R.id.current_year);
        mCurrentDayText = (TextView) findViewById(R.id.current_day);

        mYearText = (TextView) findViewById(R.id.year_text);
        mMonthText = (TextView) findViewById(R.id.month_text);
        mDayText = (TextView) findViewById(R.id.day_text);
        mHourText = (TextView) findViewById(R.id.hour_text);
        mMinuteText = (TextView) findViewById(R.id.minute_text);

        mContainerLayout = (LinearLayout) findViewById(R.id.container_layout);

        mCancelText = (TextView) findViewById(R.id.cancel_text);
        mConfirmText = (TextView) findViewById(R.id.confiem_text);
    }

    public void setView(View view) {
        mContainerLayout.removeAllViews();
        mContainerLayout.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout
                .LayoutParams.MATCH_PARENT);
    }

    public void setPositiveButton(View.OnClickListener listener) {
        mConfirmText.setOnClickListener(listener);
    }

    public void setNegativeButton(View.OnClickListener listener) {
        mCancelText.setOnClickListener(listener);
    }

    public void setCurrentDate(String date) {
        if (TextUtils.isEmpty(date)) {
            return;
        }

        if (date.length() == 8) {
            String year = date.substring(0, 4);
            String month = date.substring(4, 6);
            String day = date.substring(6);

            mCurrentYearText.setText(year);

            if (month.startsWith("0"))
                month = month.substring(1);
            if (day.startsWith("0"))
                day = day.substring(1);
            mCurrentDayText.setText(month + "月" + day + "日");

        } else if (date.length() == 6) {
            String year = date.substring(0, 4);
            String month = date.substring(4, 6);

            mCurrentYearText.setText(year);

            if (month.startsWith("0"))
                month = month.substring(1);

            mCurrentDayText.setText(month + "月");
        }
    }

    public void setShowDay(boolean showDay) {
        isShowDay = showDay;

        if (showDay) {
            mDayText.setVisibility(View.VISIBLE);
        } else {
            mDayText.setVisibility(View.GONE);
        }
    }

    public void setShowHourAndMinute(boolean showHourAndMinute) {
        isShowHourAndMinute = showHourAndMinute;

        if (showHourAndMinute) {
            mHourText.setVisibility(View.VISIBLE);
            mMinuteText.setVisibility(View.VISIBLE);
        } else {
            mHourText.setVisibility(View.GONE);
            mMinuteText.setVisibility(View.GONE);
        }
    }
}

