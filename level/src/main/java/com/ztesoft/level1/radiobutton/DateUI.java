package com.ztesoft.level1.radiobutton;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.ztesoft.level1.Level1Util;
import com.ztesoft.level1.R;
import com.ztesoft.level1.radiobutton.util.ArrayWheelAdapter;
import com.ztesoft.level1.radiobutton.util.DateDialog;
import com.ztesoft.level1.radiobutton.util.NumericWheelAdapter;
import com.ztesoft.level1.radiobutton.util.OnWheelChangedListener;
import com.ztesoft.level1.radiobutton.util.WheelView;
import com.ztesoft.level1.util.DateUtil;

/**
 * 文件名称 : DateUI
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 弹出式选择日期控件，支持D/M格式
 * <p>
 * 创建时间 : 2017/4/21 10:59
 * <p>
 */
public class DateUI extends TextView {
    private OnSelectListener mOnSelectListener;

    private String dateType = "D";
    private String[] minDate = {"2010", "01", "01"};// 默认最小日期为20100101
    private String[] maxDate;
    private String buttonType;// 备用的临时属性

    private DateDialog mDialog;

    private Context context;
    private String[] selectValue;// 初始化值
    private int[] selectOrder = null;
    private WheelView[] wheelViews;// 滚轮组
    /**
     * 选中文本的颜色
     */
    private int value_text_color = Color.parseColor("#3385ff");
    /**
     * 未选中文本的颜色
     */
    private int items_text_color = Color.parseColor("#a4aeb8");
    /**
     * 滑轮中文本字体大小
     */
    private int text_size = 16;
    // 选中阴影色
    private Drawable centerDrawable;

    public DateUI(Context context) {
        super(context);

        this.context = context;
        selectOrder = new int[]{100, 100, 100};// 默认选中最大日期，故初始化时赋予极大值
    }

    public void create(String selDate) {
        selDate = init(selDate, dateType);
        if ("M".equals(dateType)) {// 月份，201304
            this.wheelViews = new WheelView[2];
            selectValue = new String[2];
            selectValue[0] = selDate.substring(0, 4);
            selectValue[1] = selDate.substring(4, 6);
            if (maxDate == null) {
                maxDate = new String[]{selectValue[0], selectValue[1]};
            } else {
                // 调整默认选中位置selectOrder
                selectOrder[0] = Integer.parseInt(selectValue[0]) - Integer.parseInt(minDate[0]);
                if (selectOrder[0] == 0) {
                    selectOrder[1] = Integer.parseInt(selectValue[1]) - Integer.parseInt
                            (minDate[1]);
                } else {
                    selectOrder[1] = Integer.parseInt(selectValue[1]) - 1;
                }
            }
        } else {
            this.wheelViews = new WheelView[3];
            selectValue = new String[3];
            if ("W".equals(dateType)) {// 周数据则坚持该日期是否为周一，不是则调整为该周的周一
                selDate = DateUtil.getInstance().convertDay_Week(selDate, 1);
            }
            selectValue[0] = selDate.substring(0, 4);
            selectValue[1] = selDate.substring(4, 6);
            selectValue[2] = selDate.substring(6);
            if (maxDate == null) {
                maxDate = new String[]{selectValue[0], selectValue[1], selectValue[2]};
            } else {
                // 调整默认选中位置selectOrder
                selectOrder[0] = Integer.parseInt(selectValue[0]) - Integer.parseInt(minDate[0]);
                if (selectOrder[0] == 0) {
                    selectOrder[1] = Integer.parseInt(selectValue[1]) - Integer.parseInt
                            (minDate[1]);
                } else {
                    selectOrder[1] = Integer.parseInt(selectValue[1]) - 1;
                }
                if ("D".equals(dateType)) {
                    if (selectOrder[0] == 0 && selectOrder[1] == 0) {
                        selectOrder[2] = Integer.parseInt(selectValue[2]) - Integer.parseInt
                                (minDate[2]);
                    } else {
                        selectOrder[2] = Integer.parseInt(selectValue[2]) - 1;
                    }
                } else {
                    int sel = DateUtil.getInstance().getCurWeekOfMonth(Integer.parseInt
                            (selectValue[0]), Integer.parseInt(selectValue[1]), Integer.parseInt
                            (selectValue[2]));
                    if (selectOrder[0] == 0 && selectOrder[1] == 0) {
                        int mi = DateUtil.getInstance().getCurWeekOfMonth(Integer.parseInt
                                (minDate[0]), Integer.parseInt(minDate[1]), Integer.parseInt
                                (minDate[2]));
                        selectOrder[2] = sel - mi;
                    } else {
                        selectOrder[2] = sel - 1;
                    }
                }
            }
        }
        // 日期按钮
        this.setGravity(Gravity.CENTER);
        this.setText(getShowName());
        this.setOnClickListener(new DateOnClickListener());
        dateFunc();
    }

    /**
     * 校验最大值、最小值和显示值
     */
    private String init(String selDate, String dateType) {
        if (selDate == null || "".equals(selDate)) {
            if ("M".equals(dateType)) {
                selDate = DateUtil.getInstance().getToday("yyyyMM");
            } else {
                selDate = DateUtil.getInstance().getToday("yyyyMMdd");
            }
        }
        selDate = selDate.replaceAll("-", "");

        if ("M".equals(dateType)) {
            if (Integer.parseInt(selDate) < Integer.parseInt(minDate[0] + minDate[1])) {
                selDate = minDate[0] + minDate[1];
            }
            if (maxDate != null && Integer.parseInt(selDate) > Integer.parseInt(maxDate[0] +
                    maxDate[1])) {
                selDate = maxDate[0] + maxDate[1];
            }
        } else {
            if (Integer.parseInt(selDate) < Integer.parseInt(minDate[0] + minDate[1] +
                    minDate[2])) {
                selDate = minDate[0] + minDate[1] + minDate[2];
            }
            if (maxDate != null && Integer.parseInt(selDate) > Integer.parseInt(maxDate[0] +
                    maxDate[1] + maxDate[2])) {
                selDate = maxDate[0] + maxDate[1] + maxDate[2];
            }
        }

        if ("W".equals(dateType)) {
            String minD = minDate[0] + minDate[1] + minDate[2];
            minD = DateUtil.getInstance().convertDay_Week(minD, 1);
            setMinDate(minD);

            if (maxDate != null) {
                String maxD = maxDate[0] + maxDate[1] + maxDate[2];
                maxD = DateUtil.getInstance().convertDay_Week(maxD, 1);
                setMaxDate(maxD);
            }
        }
        return selDate;
    }

    /**
     * 绘制弹出框
     */
    private void dateFunc() {
        // 滚轮
        LinearLayout wheelLayout = new LinearLayout(context);
        wheelLayout.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < wheelViews.length; i++) {
            wheelViews[i] = new WheelView(context);
            wheelViews[i].setITEMS_TEXT_COLOR(items_text_color);
            wheelViews[i].setVALUE_TEXT_COLOR(value_text_color);
            wheelViews[i].setTEXT_SIZE(text_size);
            wheelViews[i].setCenterDrawable(centerDrawable);
            wheelViews[i].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT, 1));
            wheelLayout.addView(wheelViews[i]);
        }
        wheelLayout.setMinimumWidth(Level1Util.getDipSize(450));

        if (null == mDialog)
            mDialog = new DateDialog(context, R.style.prompt_style);

        mDialog.setView(wheelLayout);
        mDialog.setShowHourAndMinute(false);

        String today;
        if (dateType.equals("D")) {
            mDialog.setShowDay(true);
            today = DateUtil.getInstance().getToday("yyyyMMdd");
        } else {
            mDialog.setShowDay(false);
            today = DateUtil.getInstance().getToday("yyyyMM");
        }
        mDialog.setCurrentDate(today);

        mDialog.setPositiveButton(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < wheelViews.length; i++) {
                    selectOrder[i] = wheelViews[i].getCurrentItem();
                    selectValue[i] = wheelViews[i].getTextItem(wheelViews[i].getCurrentItem());
                }
                DateUI.this.setText(getShowName());
                mDialog.dismiss();

                mOnSelectListener.OnSelected(getTrueName());
            }
        });
        mDialog.setNegativeButton(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }

    // 弹出日期选择框
    class DateOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            // 当日起滚动时触发事件
            OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
                int[] tmp = new int[wheelViews.length];// 滚轮选中的位置数组。还未保存，用来刷新滚轮的显示值

                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    for (int i = 0; i < wheelViews.length; i++) {
                        tmp[i] = wheelViews[i].getCurrentItem();
                    }
                    getNamesAdapt(tmp);
                }
            };

            getNamesAdapt(selectOrder);

            for (int i = 0; i < wheelViews.length; i++) {
                wheelViews[i].setCurrentItem(selectOrder[i]);
                wheelViews[i].addChangingListener(wheelListener);
            }
            mDialog.show();
        }
    }

    private void getNamesAdapt(int[] tmp) {
        // 年
        int minYear = Integer.parseInt(minDate[0]);
        int maxYear = Integer.parseInt(maxDate[0]);
        if (tmp[0] >= maxYear - minYear) {
            tmp[0] = maxYear - minYear;
            wheelViews[0].setCurrentItem(tmp[0]);
        }
        wheelViews[0].setAdapter(new NumericWheelAdapter(minYear, maxYear));
        // 月
        int minMonth = 1, maxMonth = 12;
        if (tmp[0] == 0)// 最小年，取设置的最小月
            minMonth = Integer.parseInt(minDate[1]);
        if (tmp[0] >= maxYear - minYear)// 最大年，取设置的最大月
            maxMonth = Integer.parseInt(maxDate[1]);
        if (tmp[1] >= maxMonth - minMonth) {
            tmp[1] = maxMonth - minMonth;
            wheelViews[1].setCurrentItem(tmp[1]);
        }
        wheelViews[1].setAdapter(new NumericWheelAdapter(minMonth, maxMonth));

        if (wheelViews.length == 3) {
            if ("W".equals(dateType)) {// 周
                int minDay = 1, maxDay = DateUtil.getInstance().getDayOfCurrentMonth(minYear +
                        tmp[0], minMonth + tmp[1]);
                if (tmp[0] == 0 && tmp[1] == 0)// 最小年月，取设置的最小日
                    minDay = Integer.parseInt(minDate[2]);
                if (tmp[0] >= maxYear - minYear && tmp[1] >= maxMonth - minMonth)// 最大年月，取设置的最大日
                    maxDay = Integer.parseInt(maxDate[2]);
                String w[] = DateUtil.getInstance().getWeekOfCurrentMonth(minYear + tmp[0],
                        minMonth + tmp[1], minDay,
                        maxDay, 1);
                if (tmp[2] > w.length - 1) {
                    tmp[2] = w.length - 1;
                    wheelViews[2].setCurrentItem(tmp[2]);
                }
                wheelViews[2].setAdapter(new ArrayWheelAdapter<String>(w, w.length));
            } else {// 日
                int minDay = 1,
                        maxDay = DateUtil.getInstance().getDayOfCurrentMonth(minYear + tmp[0],
                                minMonth + tmp[1]);
                if (tmp[0] == 0 && tmp[1] == 0)// 最小年月，取设置的最小日
                    minDay = Integer.parseInt(minDate[2]);
                if (tmp[0] >= maxYear - minYear && tmp[1] >= maxMonth - minMonth)// 最大年月，取设置的最大日
                    maxDay = Integer.parseInt(maxDate[2]);
                if (tmp[2] >= maxDay - minDay) {
                    tmp[2] = maxDay - minDay;
                    wheelViews[2].setCurrentItem(tmp[2]);
                }
                wheelViews[2].setAdapter(new NumericWheelAdapter(minDay, maxDay));
            }
        }
    }

    /**
     * 获取选中日期，yyyy-MM-dd或yyyy-MM格式
     *
     * @return
     */
    public String getShowName() {
        String text = getTrueName();
        if ("M".equals(dateType))
            text = DateUtil.getInstance().convertDay_Type(text, "yyyyMM", "yyyy-MM");
        else
            text = DateUtil.getInstance().convertDay_Type(text, "yyyyMMdd", "yyyy-MM-dd");
        return text;
    }

    /**
     * 获取选中日期，yyyyMMdd或yyyyMM格式
     *
     * @return
     */
    public String getTrueName() {
        String text = "";
        for (int i = 0; i < selectValue.length; i++) {
            if (selectValue[i].length() == 1)
                text += "0";
            text += selectValue[i];
        }
        return text;
    }

    /**
     * 设置默认日期，支持yyyMMdd、yyyMM 默认自动执行回调函数
     *
     * @param selectDate
     */
    public void setTrueValue(String selectDate) {

        setTrueValue(selectDate, true);
    }

    /***
     *
     * @param selectDate
     * @param isDoBackFunc:是否执行回调函数
     */
    public void setTrueValue(String selectDate, boolean isDoBackFunc) {
        if ("M".equals(dateType)) {
            selectDate = DateUtil.getInstance().convertDay_Type(selectDate, "yyyyMM", "yyyy-MM");
        } else {
            if ("W".equals(dateType)) {
                selectDate = DateUtil.getInstance().convertDay_Week(selectDate, 1);
            }
            selectDate = DateUtil.getInstance().convertDay_Type(selectDate, "yyyyMMdd",
                    "yyyy-MM-dd");
        }
        setShowValue(selectDate, isDoBackFunc);
    }

    /**
     * 设置默认日期，支持yyyy-MM-dd、yyyy-MM
     *
     * @param selectDate
     */
    public void setShowValue(String selectDate, boolean isDoBackFunc) {
        selectValue = selectDate.split("-");
        String[] curminDate = {minDate[0], "01", "01"};
        if (selectValue[0].equals(minDate[0])) {// 年选择的最小的年
            curminDate[1] = minDate[1];// 月就是最小的月
        }
        if (!"M".equals(dateType)) {// 不是月份，就计算日
            if (selectValue[0].equals(minDate[0]) && selectValue[1].equals(minDate[1])) {
                curminDate[2] = minDate[2];// 日就是最小的日
            }
            if ("W".equals(dateType)) {
                String w[] = DateUtil.getInstance().getWeekOfCurrentMonth(Integer.parseInt
                        (selectValue[0]), Integer.parseInt(selectValue[1]), 1, 31, 1);
                curminDate[2] = w[0];
            }
        }

        for (int i = 0; i < selectValue.length; i++) {
            selectOrder[i] = Integer.parseInt(selectValue[i]) - Integer.parseInt(curminDate[i]);
            if (i == 2 && "W".equals(dateType)) {
                String currentDay = DateUtil.getInstance().convertDay_Week(selectValue[0] +
                        selectValue[1] + selectValue[2], 1).substring(6, 8);
                selectOrder[i] = (Integer.parseInt(currentDay) - Integer.parseInt(curminDate[2]))
                        / 7;
            }
        }
        this.setText(getShowName());
        if (isDoBackFunc) {
            mOnSelectListener.OnSelected(getTrueName());
        }
    }

    /**
     * 设置最小日期，支持yyyy-MM-dd和yyyMMdd、yyyy-MM和yyyMM四种格式
     *
     * @param minDate
     */
    public void setMinDate(String minDate) {
        if (minDate.length() < 6) {
            return;
        }
        minDate = minDate.replaceAll("-", "");
        this.minDate[0] = minDate.substring(0, 4);
        if (Integer.parseInt(minDate.substring(4, 6)) > 12) {
            this.minDate[1] = "12";
        } else {
            this.minDate[1] = minDate.substring(4, 6);
        }
        if (minDate.length() == 8) {
            int num = DateUtil.getInstance().getDayOfCurrentMonth(Integer.parseInt(this
                    .minDate[0]), Integer.parseInt(this.minDate[1]));
            if (Integer.parseInt(minDate.substring(6)) > num) {
                this.minDate[2] = num + "";
            } else {
                this.minDate[2] = minDate.substring(6);
            }
        }
    }

    /**
     * 设置最大日期，支持yyyy-MM-dd和yyyyMMdd、yyyy-MM和yyyMM四种格式
     *
     * @param maxDate
     */
    public void setMaxDate(String maxDate) {
        if (maxDate.length() < 6) {
            return;
        }
        maxDate = maxDate.replaceAll("-", "");
        this.maxDate = new String[3];
        this.maxDate[0] = maxDate.substring(0, 4);
        if (Integer.parseInt(maxDate.substring(4, 6)) > 12) {
            this.maxDate[1] = "12";
        } else {
            this.maxDate[1] = maxDate.substring(4, 6);
        }
        if (maxDate.length() == 8) {
            int num = DateUtil.getInstance().getDayOfCurrentMonth(Integer.parseInt(this
                    .maxDate[0]), Integer.parseInt(this.maxDate[1]));
            if (Integer.parseInt(maxDate.substring(6)) > num) {
                this.maxDate[2] = num + "";
            } else {
                this.maxDate[2] = maxDate.substring(6);
            }
        }
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public void setButtonType(String buttonType) {
        this.buttonType = buttonType;
    }

    public String getButtonType() {
        return buttonType;
    }

    /**
     * 选中文本的颜色
     *
     * @return
     */
    public void setValue_text_color(int value_text_color) {
        this.value_text_color = value_text_color;
    }

    /**
     * 未选中文本的颜色
     *
     * @return
     */
    public void setItems_text_color(int items_text_color) {
        this.items_text_color = items_text_color;
    }

    /**
     * 文本大小
     *
     * @return
     */
    public int getText_size() {
        return text_size;
    }

    public void setText_size(int text_size) {
        this.text_size = text_size;
    }

    /**
     * 选中阴影色
     *
     * @return
     */
    public void setCenterDrawable(int centerDrawable) {
        this.centerDrawable = context.getResources().getDrawable(R.drawable.wheel_val);
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.mOnSelectListener = onSelectListener;
    }

    /**
     * 时间选择监听器
     */
    public interface OnSelectListener {
        /**
         * 选中的时间
         *
         * @param date 时间
         */
        void OnSelected(String date);
    }
}
