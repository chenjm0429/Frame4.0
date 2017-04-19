package com.ztesoft.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.ztesoft.R;
import com.ztesoft.level1.Level1Util;
import com.ztesoft.level1.radiobutton.util.NumericWheelAdapter;
import com.ztesoft.level1.radiobutton.util.OnWheelChangedListener;
import com.ztesoft.level1.radiobutton.util.WheelView;
import com.ztesoft.level1.ui.MyAlertDialog;
import com.ztesoft.level1.util.DateUtil;

import java.lang.reflect.Method;

/**
 * 文件名称 : DateTimeUI
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 日期控件，显示信息包含yyyyMMddHHmm
 * <p>
 * 创建时间 : 2017/3/24 15:50
 * <p>
 */
public class DateTimeUI extends TextView {

    private String methodName = null;
    private String[] minDate = {"2010", "01", "01", "00", "00"};// 默认最小时间为201001010000
    private String[] maxDate;
    private String buttonType;// 备用的临时属性

    private MyAlertDialog ad;
    private Context context;
    private String[] selectValue;// 初始化值
    private int[] selectOrder = null;
    private WheelView[] wheelViews;// 滚轮组
    /**
     * 选中文本的颜色
     */
    private int value_text_color = 0xF0000000;
    /**
     * 未选中文本的颜色
     */
    private int items_text_color = 0xFF000000;
    /**
     * 滑轮中文本字体大小
     */
    private int text_size = 18;
    // 选中阴影色
    private Drawable centerDrawable;

    public DateTimeUI(Context context) {
        super(context);

        this.context = context;
        selectOrder = new int[]{100, 100, 100, 100, 100};// 默认选中最大日期，故初始化时赋予极大值
    }

    public void create(String selDate) {
        selDate = init(selDate);

        this.wheelViews = new WheelView[5];
        selectValue = new String[5];

        selectValue[0] = selDate.substring(0, 4);
        selectValue[1] = selDate.substring(4, 6);
        selectValue[2] = selDate.substring(6, 8);
        selectValue[3] = selDate.substring(8, 10);
        selectValue[4] = selDate.substring(10);
        if (maxDate == null) {
            maxDate = new String[]{selectValue[0], selectValue[1], selectValue[2],
                    selectValue[3], selectValue[4]};
        } else {
            // 调整默认选中位置selectOrder
            selectOrder[0] = Integer.parseInt(selectValue[0])
                    - Integer.parseInt(minDate[0]);
            if (selectOrder[0] == 0) {
                selectOrder[1] = Integer.parseInt(selectValue[1])
                        - Integer.parseInt(minDate[1]);
            } else {
                selectOrder[1] = Integer.parseInt(selectValue[1]) - 1;
            }

            if (selectOrder[0] == 0 && selectOrder[1] == 0) {
                selectOrder[2] = Integer.parseInt(selectValue[2])
                        - Integer.parseInt(minDate[2]);
            } else {
                selectOrder[2] = Integer.parseInt(selectValue[2]) - 1;
            }

            selectOrder[3] = Integer.parseInt(selectValue[3]);
            selectOrder[4] = Integer.parseInt(selectValue[4]);

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
    private String init(String selDate) {
        if (TextUtils.isEmpty(selDate)) {
            selDate = DateUtil.getInstance().getToday("yyyyMMddHHmm");
        }

        if (Long.parseLong(selDate) < Long.parseLong(minDate[0] + minDate[1]
                + minDate[2] + minDate[3] + minDate[4])) {
            selDate = minDate[0] + minDate[1] + minDate[2] + minDate[3]
                    + minDate[4];
        }
        if (maxDate != null
                && Long.parseLong(selDate) > Long.parseLong(maxDate[0]
                + maxDate[1] + maxDate[2] + minDate[3] + minDate[4])) {
            selDate = maxDate[0] + maxDate[1] + maxDate[2] + maxDate[3]
                    + maxDate[4];
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
            wheelViews[i].setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1));
            wheelLayout.addView(wheelViews[i]);

            if (i == 3) {
                wheelViews[i].setLabel("时");
            } else if (i == 4) {
                wheelViews[i].setLabel("分");
            }
        }
        wheelLayout.setMinimumWidth(Level1Util.getDipSize(450));

        if (null == ad)
            ad = new MyAlertDialog(context);
        ad.setTitle("时间选择");
        ad.setView(wheelLayout);
        ad.setPositiveButton(R.string.system_confirm, new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < wheelViews.length; i++) {
                    selectOrder[i] = wheelViews[i].getCurrentItem();
                    selectValue[i] = wheelViews[i].getTextItem(wheelViews[i].getCurrentItem());
                }
                DateTimeUI.this.setText(getShowName());
                ad.dismiss();
                backFunc();
            }
        });
        ad.setNegativeButton(R.string.system_cancel, new OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
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
            ad.show();
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

        int minDay = 1, maxDay = DateUtil.getInstance().getDayOfCurrentMonth(
                minYear + tmp[0], minMonth + tmp[1]);
        if (tmp[0] == 0 && tmp[1] == 0)// 最小年月，取设置的最小日
            minDay = Integer.parseInt(minDate[2]);
        if (tmp[0] >= maxYear - minYear && tmp[1] >= maxMonth - minMonth)// 最大年月，取设置的最大日
            maxDay = Integer.parseInt(maxDate[2]);
        if (tmp[2] >= maxDay - minDay) {
            tmp[2] = maxDay - minDay;
            wheelViews[2].setCurrentItem(tmp[2]);
        }
        wheelViews[2].setAdapter(new NumericWheelAdapter(minDay, maxDay));

        wheelViews[3].setAdapter(new NumericWheelAdapter(0, 23));
        wheelViews[3].setCurrentItem(tmp[3]);

        wheelViews[4].setAdapter(new NumericWheelAdapter(0, 59));
        wheelViews[4].setCurrentItem(tmp[4]);

    }

    /**
     * 回调函数
     */
    private void backFunc() {
        if (methodName != null && methodName.trim().length() > 0) {
            try {
                Class<?> yourClass = Class.forName(context.getClass().getName());
                Method method = yourClass.getMethod(methodName);
                method.setAccessible(true);// 提高反射速度
                method.invoke(context);
            } catch (Exception e) {
                e.printStackTrace();
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

        String show = DateUtil.getInstance().getFormatDate(text, "yyyyMMddHHmm", "yyyy-MM-dd " +
                "HH:mm");

        return show;
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
     * 设置默认日期，支持yyyMMdd、yyyMM
     *
     * @param selectDate
     */
    public void setTrueValue(String selectDate) {

        selectDate = DateUtil.getInstance().convertDay_Type(selectDate, "yyyyMMddHHmm", 
                "yyyy-MM-dd HH:mm");

        setShowValue(selectDate);
    }

    /**
     * 设置默认日期，支持yyyy-MM-dd、yyyy-MM
     *
     * @param selectDate
     */
    public void setShowValue(String selectDate) {
        selectValue = selectDate.split("-");
        for (int i = 0; i < selectValue.length; i++) {
            selectOrder[i] = Integer.parseInt(selectValue[i]) - Integer.parseInt(minDate[i]);
        }
        this.setText(getShowName());
        backFunc();
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

        int num = DateUtil.getInstance().getDayOfCurrentMonth(Integer.parseInt(this.minDate[0]), 
                Integer.parseInt(this.minDate[1]));
        if (Integer.parseInt(minDate.substring(6, 8)) > num) {
            this.minDate[2] = num + "";
        } else {
            this.minDate[2] = minDate.substring(6, 8);
        }

        this.minDate[3] = minDate.substring(8, 10);
        this.minDate[4] = minDate.substring(10);

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
        this.maxDate = new String[5];
        this.maxDate[0] = maxDate.substring(0, 4);
        if (Integer.parseInt(maxDate.substring(4, 6)) > 12) {
            this.maxDate[1] = "12";
        } else {
            this.maxDate[1] = maxDate.substring(4, 6);
        }

        int num = DateUtil.getInstance().getDayOfCurrentMonth(Integer.parseInt(this.maxDate[0]), 
                Integer.parseInt(this.maxDate[1]));
        if (Integer.parseInt(maxDate.substring(6, 8)) > num) {
            this.maxDate[2] = num + "";
        } else {
            this.maxDate[2] = maxDate.substring(6, 8);
        }

        this.maxDate[3] = maxDate.substring(8, 10);
        this.maxDate[4] = maxDate.substring(10);

    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
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
    public void setValueTextColor(int value_text_color) {
        this.value_text_color = value_text_color;
    }

    /**
     * 未选中文本的颜色
     *
     * @return
     */
    public void setItemsTextColor(int items_text_color) {
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

    public void setTextSize(int text_size) {
        this.text_size = text_size;
    }

    /**
     * 选中阴影色
     *
     * @return
     */
    public void setCenterDrawable(int centerDrawable) {
        this.centerDrawable = context.getResources().getDrawable(
                R.drawable.wheel_val);
    }
}
