package com.ztesoft.level1.calendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ztesoft.level1.R;

/**
 * 文件名称 : CalendarSelectUI
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 日历弹出框选择控件
 * <p>
 * 创建时间 : 2017/3/24 15:43
 * <p>
 */
public class CalendarSelectUI extends TextView {

    private Context context;

    private String statDate;
    private View view;

    private PopupWindow mDialog;

    private String selectedStartDate;
    private String selectedEndDate;

    private OnDateSeleteListener mOnDateSeleteListener;

    private String defalultColor = "#FFFFFF";
    private String selectColor = "#8FCC41";

    public CalendarSelectUI(Context context) {
        super(context);

        this.context = context;

        this.setGravity(Gravity.CENTER);

        this.setOnClickListener(new SelectOnClickListener());
    }

    /**
     * 获得参数后，初始化组件
     *
     * @param statDate 选中时间
     * @param view     弹出框基于该view弹出，null时基于本身弹出
     */
    public void create(String statDate, View view) {
        this.statDate = statDate;
        this.view = view;

        this.setGravity(Gravity.CENTER);
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        this.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        this.setSingleLine(true);
        this.setTextColor(Color.parseColor("#333333"));
        this.setOnClickListener(new SelectOnClickListener());

        if (TextUtils.isEmpty(statDate)) {
            if (TextUtils.isEmpty(selectedEndDate))
                statDate = com.ztesoft.level1.util.DateUtil.getInstance().getToday("yyyyMMdd");
            else
                statDate = selectedEndDate;
        }

        this.setText(com.ztesoft.level1.util.DateUtil.getInstance().convertDay_Type(statDate,
                "yyyyMMdd", "yyyy-MM-dd"));

        dateFunc();
    }

    /**
     * 绘制弹出框
     */
    private void dateFunc() {

        CalendarView calendarView = new CalendarView(context, statDate, selectedStartDate,
                selectedEndDate);
//        calendarView.setSelectedStartDate(selectedStartDate);
//        calendarView.setSelectedEndDate(selectedEndDate);
        calendarView.setOnCalendarDateClickListener(new CalendarView.OnCalendarDateClickListener() {
            @Override
            public void onClick(String currentDate, String formatDate) {
                statDate = currentDate;

                CalendarSelectUI.this.setText(formatDate);

                mOnDateSeleteListener.onDateSeleted(currentDate, formatDate);

                if (null != mDialog && mDialog.isShowing())
                    mDialog.dismiss();
            }

            @Override
            public void onUselessClick(String statDate, String formatDate) {
//                PromptUtils.instance.displayToastString(context, false, "选择时间超出选择范围！");
            }
        });

        if (null == mDialog) {
            mDialog = new PopupWindow(calendarView, WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    true);
            mDialog.setBackgroundDrawable(new ColorDrawable());
            mDialog.setOutsideTouchable(true);
            mDialog.setAnimationStyle(R.style.PopupWindowAnimation);

            mDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    setBgAlpha(1f);
                }
            });
        }
    }

    // 弹出日期选择框
    class SelectOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (null != mDialog) {
                if (null != view)
                    mDialog.showAsDropDown(view, 0, 0);
                else
                    mDialog.showAsDropDown(v, 0, 0);

                setBgAlpha(0.7f);
            }
        }
    }

    public void setOnDateSeleteListener(OnDateSeleteListener onDateSeleteListener) {
        this.mOnDateSeleteListener = onDateSeleteListener;
    }

    public interface OnDateSeleteListener {
        void onDateSeleted(String currentDate, String formateDate);
    }

    private void setBgAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        ((Activity) context).getWindow().setAttributes(lp);
    }

    public void setSelectedStartDate(String selectedStartDate) {
        this.selectedStartDate = selectedStartDate;
    }

    public void setSelectedEndDate(String selectedEndDate) {
        this.selectedEndDate = selectedEndDate;
    }
}