package com.ztesoft.level1.pickview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectChangeListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.ztesoft.level1.R;

import java.util.ArrayList;
import java.util.List;

import static com.ztesoft.level1.Level1Util.dip2px;

/**
 * 文件名称 : CustomerPickerView
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 自定义选择器控件
 * <p>
 * 创建时间 : 2018/11/20 13:54
 * <p>
 */
public class CustomerPickerView extends LinearLayout implements View.OnClickListener {

    private TextView showText;
    private Context mContext;
    private String mTitle;
    private OptionsPickerView optionsView;
    private List<PickerViewBean> options1Items1;
    private List<List<PickerViewBean>> options1Items2 = null;
    private List<List<List<PickerViewBean>>> options1Items3 = null;
    private int lastOption1 = 0;
    private int lastOption2 = 0;
    private int lastOption3 = 0;
    private boolean needShow = false;
    private OnPickViewSelected mListener = null;

    public CustomerPickerView(Context context, String title, ArrayList<PickerViewBean> beans,
                              OnPickViewSelected listener) {
        super(context);

        mContext = context;
        mTitle = title;
        options1Items1 = beans;
        mListener = listener;
        this.setOrientation(LinearLayout.HORIZONTAL);
        this.setGravity(Gravity.CENTER);
        this.setPadding(dip2px(context, 1), dip2px(context, 1), dip2px(context, 1), dip2px
                (context, 1));
        GradientDrawable gd = new GradientDrawable();
        gd.setStroke(dip2px(context, 1), 0xFFEDEDED);
        gd.setColor(0xFFFFFFFF);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.setBackground(gd);
        } else {
            this.setBackgroundDrawable(gd);
        }

        showText = new TextView(context);
        showText.setTextSize(16);
        showText.setTextColor(0xFF333333);
        showText.setText("");
        showText.setGravity(Gravity.CENTER);
        int padding = dip2px(context, 10);
        showText.setPadding(padding, 0, padding, 0);

        LayoutParams lp1 = new LayoutParams(0, dip2px(context, 28), 1.0f);
        this.addView(showText, lp1);

        View divider = new View(context);
        divider.setBackgroundColor(0xFFEDEDED);
        LayoutParams lp2 = new LayoutParams(dip2px(context, 1), dip2px(context, 28));
        this.addView(divider, lp2);

        LinearLayout rLyt = new LinearLayout(context);
        rLyt.setBackgroundColor(0XFFF7F7F7);
        rLyt.setGravity(Gravity.CENTER);

        LayoutParams lp3 = new LayoutParams(dip2px(context, 28), dip2px(context, 28));
        this.addView(rLyt, lp3);

        ImageView img = new ImageView(context);
        img.setImageResource(R.drawable.xiala);
        img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        LayoutParams lp4 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rLyt.addView(img, lp4);
        parseData();
        initPickerView();
        this.setOnClickListener(this);
        updateText();
    }

    private void updateText() {
        String txt;
        int len = 1;
        if (null != options1Items3) {
            len = 3;
        } else if (null != options1Items2) {
            len = 2;
        }

        PickerViewBean firstBean = options1Items1.get(lastOption1);
        txt = firstBean.getName();
        if (len > 1) {
            PickerViewBean secondBean = firstBean.getChilds().get(lastOption2);
            txt = secondBean.getName();
            if (len == 3) {
                PickerViewBean thirdBean = secondBean.getChilds().get(lastOption3);
                txt = thirdBean.getName();
            }
        }
        showText.setText(txt);
        this.requestLayout();
    }

    private void parseData() {
        if (isEmpty(options1Items1)) {
            return;
        }
        boolean hasSecond = false;
        boolean hasThird = false;
        for (PickerViewBean firstBean : options1Items1) {
            List<PickerViewBean> secondList = firstBean.getChilds();
            if (!isEmpty(secondList)) {
                hasSecond = true;
            }
            for (PickerViewBean secondBean : secondList) {
                List<PickerViewBean> thirdList = secondBean.getChilds();
                if (!isEmpty(thirdList)) {
                    hasThird = true;
                    break;
                }
            }
            if (hasThird) {
                break;
            }
        }
        if (hasSecond) {
            options1Items2 = new ArrayList<>();
            if (hasThird) {
                options1Items3 = new ArrayList<>();
            }
            for (int i = 0; i < options1Items1.size(); i++) {//遍历一级菜单
                List<PickerViewBean> secondList = new ArrayList<>();//二级菜单
                List<List<PickerViewBean>> thirdList = new ArrayList<>();//三级菜单

                PickerViewBean firstBean = options1Items1.get(i);
                List<PickerViewBean> secondBeans = firstBean.getChilds();
                if (secondBeans.size() == 0) {
                    PickerViewBean tmp1 = new PickerViewBean();
                    tmp1.setName(firstBean.getName());
                    secondList.add(tmp1);
                    if (hasThird) {
                        ArrayList<PickerViewBean> tmp3 = new ArrayList<>();
                        PickerViewBean tmp2 = new PickerViewBean();
                        tmp2.setName(firstBean.getName());
                        tmp3.add(tmp2);
                        thirdList.add(tmp3);
                    }
                } else {
                    for (int j = 0; j < secondBeans.size(); j++) {
                        PickerViewBean secondBean = secondBeans.get(j);
                        secondList.add(secondBean);
                        List<PickerViewBean> thirdBeans = secondBean.getChilds();
                        ArrayList<PickerViewBean> thirdList2 = new ArrayList<>();//二级菜单
                        if (thirdBeans.size() == 0) {
                            PickerViewBean tmp = new PickerViewBean();
                            tmp.setName(secondBean.getName());
                            thirdList2.add(tmp);
                        } else {
                            thirdList2.addAll(thirdBeans);
                        }
                        thirdList.add(thirdList2);//添加该省所有地区数据
                    }
                }

                options1Items2.add(secondList);
                if (hasThird) {
                    options1Items3.add(thirdList);
                }
            }
        }
    }

    private void onSelected(int options1, int options2, int options3) {
        lastOption1 = options1;
        lastOption2 = options2;
        lastOption3 = options3;
        updateText();
        if (null != mListener) {
            String[] names;
            String[] codes;
            int len = 1;
            if (null != options1Items3) {
                len = 3;
            } else if (null != options1Items2) {
                len = 2;
            }
            names = new String[len];
            codes = new String[len];

            PickerViewBean firstBean = options1Items1.get(options1);
            names[0] = firstBean.getName();
            codes[0] = firstBean.getCode();
            if (len > 1) {
                PickerViewBean secondBean = firstBean.getChilds().get(options2);
                names[1] = secondBean.getName();
                codes[1] = secondBean.getCode();
                if (len == 3) {
                    PickerViewBean thirdBean = secondBean.getChilds().get(options3);
                    names[2] = thirdBean.getName();
                    codes[2] = thirdBean.getCode();
                }
            }
            mListener.onSelected(names, codes);
        }
    }

    private void initPickerView() {
        OptionsPickerBuilder builder = new OptionsPickerBuilder(mContext, new
                OnOptionsSelectListener() {

                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        onSelected(options1, options2, options3);
                    }
                })
                .setContentTextSize(20)//设置滚轮文字大小
                .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .setBackgroundId(0x00000000) //设置外部遮罩颜色
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
                    }
                });

        if (!TextUtils.isEmpty(mTitle)) {
            builder.setTitleText(mTitle);
        }
        optionsView = builder.build();

        if (null != options1Items3) {
            if (!isEmpty(options1Items1) && !isEmpty(options1Items2)) {
                optionsView.setPicker(options1Items1, options1Items2, options1Items3);
                needShow = true;
            }
        } else if (!isEmpty(options1Items2)) {
            if (!isEmpty(options1Items1)) {
                optionsView.setPicker(options1Items1, options1Items2);
                needShow = true;
            }
        } else {
            if (!isEmpty(options1Items1)) {
                optionsView.setPicker(options1Items1);
                needShow = true;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (needShow) {
            if (null != options1Items3) {
                optionsView.setSelectOptions(lastOption1, lastOption2, lastOption3);
            } else if (null != options1Items2) {
                optionsView.setSelectOptions(lastOption1, lastOption2);
            } else {
                optionsView.setSelectOptions(lastOption1);
            }
            optionsView.show();
        }
    }

    private boolean isEmpty(Object obj) {
        List<Object> lists = (ArrayList<Object>) obj;
        return (null == lists || lists.size() == 0);
    }

    /*
     ArrayList<PickerViewBean> firstLists = new ArrayList<>();
        PickerViewBean bean1 = new PickerViewBean();
        bean1.name = "江苏";
        bean1.code = "1";
        ArrayList<PickerViewBean> beans11 = new ArrayList<>();
        PickerViewBean bean11 = new PickerViewBean();
        bean11.name = "南京";
        bean11.code = "1";

        ArrayList<PickerViewBean> tmps1 = new ArrayList<>();
        PickerViewBean tmp2 = new PickerViewBean();
        tmp2.name = "建邺";
        tmp2.code = "1";
        PickerViewBean tmp3 = new PickerViewBean();
        tmp3.name = "江宁";
        tmp3.code = "1";
        tmps1.add(tmp2);
        tmps1.add(tmp3);
        bean11.childs = tmps1;

        beans11.add(bean11);
        PickerViewBean bean12 = new PickerViewBean();
        bean12.name = "盐城";
        bean12.code = "1";
        beans11.add(bean12);
        bean1.childs = beans11;

        firstLists.add(bean1);
        PickerViewBean bean2 = new PickerViewBean();
        bean2.name = "北京";
        bean2.code = "2";

        ArrayList<PickerViewBean> beans21 = new ArrayList<>();
        PickerViewBean bean21 = new PickerViewBean();
        bean21.name = "北京市";
        bean21.code = "1";
        beans21.add(bean21);
        bean2.childs = beans21;

        firstLists.add(bean2);
        PickerViewBean bean3 = new PickerViewBean();
        bean3.name = "上海";
        bean3.code = "3";
        firstLists.add(bean3);
        PickerViewBean bean4 = new PickerViewBean();
        bean4.name = "香港";
        bean4.code = "4";
        firstLists.add(bean4);
         CustomerPickerView cs = new CustomerPickerView(this, "菜单选择", firstLists, new 
         OnPickViewSelected() {
            @Override
            public void onSeleced(String[] names, String[] codes) {
            }
        });
     */
}