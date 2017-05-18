package com.ztesoft.level1.radiobutton;

import java.lang.reflect.Method;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ztesoft.level1.Level1Util;
import com.ztesoft.level1.R;
import com.ztesoft.level1.radiobutton.util.ArrayWheelAdapter;
import com.ztesoft.level1.radiobutton.util.OnWheelChangedListener;
import com.ztesoft.level1.radiobutton.util.WheelView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * 多级滚轮列表菜单
 * json格式 [{code:"",name:"",array:[{code:"",name:"",array[....]}]}....]
 *
 * @author wangsq
 * @author wanghx2  update by 2013/4/28
 */
public class MultiSelectUI extends TextView {
    final String TAG = "MultiSelectUI";

    private Context context;
    private com.ztesoft.level1.ui.MyAlertDialog ad;// 弹出窗
    private JSONArray jsArr;
    private String diloagTitle;// 弹出窗标题
    private String methodName = null;// 回调函数

    private String[] selectValue;// 初始化值
    private int[] selectOrder;// 选择序列
    private String[] selectName;// 选择名称

    private WheelView[] wheelViews;// 滚轮组
    private String[][] wheelNamesAdapt;//滚轮组数据
    private int[] wheelWidth;//滚动组宽度
    private int value_text_color = 0xF0000000;// 选中文本的颜色
    private int items_text_color = 0xFF000000;// 未选中文本的颜色
    private int wheelTextSize = 18;//滚动组文本字体大小
    private Drawable centerDrawable;//选中阴影色

    //是否显示选择项的两级目录
    private boolean isShowTwoLevel = false;
    //两级目录的分隔符
    private String levelSplit = "-";

    // 多节级联菜单中，子级选择全部（全市、全县，其中code值是空），显示父级
    private boolean isShowParantWhenSelectAll = false;

    private Object backObj = null;

    // 取值变量
    protected String code = "code";
    protected String name = "name";
    protected String arrayName = "array";

    private String buttonType;//备用的临时属性

    /***
     * 多级滚轮列表菜单
     *
     * @param context    上下文
     * @param title      弹出框标题
     * @param methodName 回调函数
     */
    public MultiSelectUI(Context context, String title, String methodName) {
        super(context);
        this.context = context;
        this.diloagTitle = title;
        this.methodName = methodName;
    }

    /***
     * @param context    上下文
     * @param title      弹出框标题
     * @param methodName 回调函数
     * @param code       取值编码变量
     * @param name       取值名称变量
     * @param arrName    取值json数组变量
     */
    public MultiSelectUI(Context context, String title, String methodName, String code, String
            name, String arrName) {
        super(context);
        this.context = context;
        this.diloagTitle = title;
        this.methodName = methodName;
        this.code = code;
        this.name = name;
        this.arrayName = arrName;
    }

    public void reCreate(JSONArray jsArr, String[] initSelect) {
        ad = null;
        create(jsArr, initSelect);
    }

    /**
     * 获得参数后，初始化组件
     */
    public void create(JSONArray jsArr, String[] initSelect) {
        this.jsArr = jsArr;
        this.selectValue = initSelect;

        selectName = new String[selectValue.length];
        selectOrder = new int[selectValue.length];
        wheelViews = new WheelView[selectValue.length];
        wheelNamesAdapt = new String[selectValue.length][];
        checkSelectValue(jsArr, 0, selectValue);
        this.setGravity(Gravity.CENTER);
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        this.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        this.setSingleLine(true);
        this.setTextColor(Color.BLACK);
        setTextCotent();
        this.setOnClickListener(new SelectOnClickListener());
        //按钮至少四个汉字的长度
//		this.setMinimumWidth((int)v.getPaint().measureText("四个汉字"));
//		this.setMinimumHeight(Level1Util.dip2px(context, 30));
        dateFunc();

    }

    /**
     * 绘制弹出框
     */
    private void dateFunc() {
        // 弹出框布局
        LinearLayout adLayout = new LinearLayout(context);
        adLayout.setOrientation(LinearLayout.VERTICAL);
        // 滚轮
        LinearLayout wheelLayout = new LinearLayout(context);
        for (int i = 0; i < wheelViews.length; i++) {
            wheelViews[i] = new WheelView(context);
            wheelViews[i].setITEMS_TEXT_COLOR(items_text_color);
            wheelViews[i].setVALUE_TEXT_COLOR(value_text_color);
            wheelViews[i].setTEXT_SIZE(wheelTextSize);
            wheelViews[i].setCenterDrawable(centerDrawable);
            if (wheelWidth != null) {
                wheelViews[i].setLayoutParams(new LayoutParams(wheelWidth[i], LayoutParams
                        .WRAP_CONTENT));
            } else {
                wheelViews[i].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT, 1));
            }
            wheelLayout.addView(wheelViews[i]);
        }
        wheelLayout.setMinimumWidth(Level1Util.getDipSize(450));

        adLayout.addView(wheelLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        if (null == ad)
            ad = new com.ztesoft.level1.ui.MyAlertDialog(context, R.style.prompt_style);
        // 增加弹出框背景半透明效果
        ad.setTitle(diloagTitle);
        ad.setView(adLayout);
        ad.setPositiveButton(R.string.system_confirm, new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 根据order得到name,value
                for (int i = 0; i < wheelViews.length; i++) {
                    selectOrder[i] = wheelViews[i].getCurrentItem();
                }
                setSelectOrder(selectOrder);
                ad.hide();
            }
        });
        ad.setNegativeButton(R.string.system_cancel, new OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.hide();
            }
        });
    }

    /**
     * 调用回调函数
     */
    private void backFunc() {
        if (methodName != null && methodName.trim().length() > 0) {
            try {
                Class<?> yourClass = Class.forName(context.getClass().getName());
                // 假设你要动态加载的类为YourClass
                Method method;
                if (null == backObj) {
                    method = yourClass.getMethod(methodName);// 这里假设你的类为YourClass，而要调用的方法是methodName
                } else {
                    method = yourClass.getMethod(methodName, MultiSelectUI.class, Object.class);
                    // 这里假设你的类为YourClass，而要调用的方法是methodName
                }

                method.setAccessible(true);//提高反射速度
                if (null == backObj) {
                    method.invoke(context);// 调用方法
                } else {
                    method.invoke(context, MultiSelectUI.this, backObj);// 调用方法
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 弹出日期选择框
    class SelectOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            WheelView tempWheel;
            // 当日起滚动时触发事件
            OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
                int[] tmp = new int[wheelViews.length];//滚轮选中的位置数组。还未保存，用来刷新滚轮的显示值

                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    for (int i = 0; i < wheelViews.length; i++) {
                        tmp[i] = wheelViews[i].getCurrentItem();
                    }
                    getNamesAdapt(jsArr, 0, tmp);
                    for (int i = 0; i < wheelViews.length; i++) {
                        wheelViews[i].setCurrentItem(tmp[i]);
                        wheelViews[i].setAdapter(new ArrayWheelAdapter<String>
                                (wheelNamesAdapt[i], 1000));
                    }
                }
            };
            getNamesAdapt(jsArr, 0, selectOrder);
            for (int i = 0; i < wheelViews.length; i++) {
                tempWheel = wheelViews[i];
                tempWheel.setAdapter(new ArrayWheelAdapter<String>(wheelNamesAdapt[i], 1000));
                tempWheel.setCurrentItem(selectOrder[i]);
                tempWheel.addChangingListener(wheelListener);
            }
            ad.show();
        }
    }

    /**
     * 根据selectOrder，实时获得滚轮显示数组
     *
     * @param jsArr
     * @param currOrderIndex
     * @param selectOrder
     */
    private void getNamesAdapt(JSONArray jsArr, int currOrderIndex, int[] selectOrder) {
        try {
            if (selectOrder[currOrderIndex] > jsArr.length() - 1) {
                selectOrder[currOrderIndex] = jsArr.length() - 1;
            }
            wheelNamesAdapt[currOrderIndex] = new String[jsArr.length()];
            for (int i = 0; i < jsArr.length(); i++) {
                wheelNamesAdapt[currOrderIndex][i] = jsArr.getJSONObject(i).getString(name);
            }
            JSONObject tempObj = jsArr.getJSONObject(selectOrder[currOrderIndex]);
            if (tempObj.has(arrayName) && currOrderIndex < selectValue.length - 1) {
                currOrderIndex++;
                getNamesAdapt(tempObj.getJSONArray(arrayName), currOrderIndex, selectOrder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据selectValue，检查是否正确，并初始化selectName和selectOrder
     *
     * @param array
     * @param i
     * @param selectValue
     */
    private void checkSelectValue(JSONArray array, int i, String[] selectValue) {
        try {
            boolean flag = false;
            JSONObject tempObj = null;
            for (int j = 0; j < array.length(); j++) {
                tempObj = array.getJSONObject(j);
                if (tempObj.getString(code).trim().equals(selectValue[i])) {
                    this.selectValue[i] = tempObj.getString(code).trim();
                    selectName[i] = tempObj.getString(name).trim();
                    selectOrder[i] = j;
                    flag = true;
                    break;
                }
            }
            if (!flag) {// 初始值错误，则默认第一项
                tempObj = array.getJSONObject(0);
                this.selectValue[i] = tempObj.getString(code).trim();
                selectName[i] = tempObj.getString(name).trim();
                selectOrder[i] = 0;
            }
            if (tempObj.has(arrayName) && i < selectValue.length - 1)
                checkSelectValue(tempObj.getJSONArray(arrayName), i + 1, selectValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据selectOrder，校验是否正确，并初始化selectValue和selectName
     *
     * @param array
     * @param i
     * @param selectOrder
     */
    private void checkSelectOrder(JSONArray array, int i, int[] selectOrder) {
        try {
            if (array == null || array.length() == 0) {
                selectValue[i] = "";
                selectName[i] = "";
                this.selectOrder[i] = 0;
                return;
            }

            JSONObject tempObj = null;
            if (array.length() > selectOrder[i])
                tempObj = array.getJSONObject(selectOrder[i]);
            else//如果不存在，则默认第一项
                tempObj = array.getJSONObject(0);

            selectValue[i] = tempObj.getString(code).trim();
            selectName[i] = tempObj.getString(name).trim();
            this.selectOrder[i] = selectOrder[i];
            if (tempObj.has(arrayName) && i < selectOrder.length - 1)
                checkSelectOrder(tempObj.getJSONArray(arrayName), i + 1, selectOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据selectName，校验是否正确，并初始化selectValue和selectOrder
     *
     * @param array
     * @param i
     * @param selectName
     */
    private void checkSelectName(JSONArray array, int i, String[] selectName) {
        try {
            boolean flag = false;
            JSONObject tempObj = null;
            for (int j = 0; j < array.length(); j++) {
                tempObj = array.getJSONObject(j);
                if (tempObj.getString(name).trim().equals(selectName[i].trim())) {
                    selectValue[i] = tempObj.getString(code).trim();
                    this.selectName[i] = tempObj.getString(name).trim();
                    selectOrder[i] = j;
                    flag = true;
                    break;
                }
            }
            if (!flag) {// 初始值错误，则默认第一项
                tempObj = array.getJSONObject(0);
                selectValue[i] = tempObj.getString(code).trim();
                this.selectName[i] = tempObj.getString(name).trim();
                selectOrder[i] = 0;
            }
            if (tempObj.has(arrayName) && i < selectName.length - 1)
                checkSelectName(tempObj.getJSONArray(arrayName), i + 1, selectName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取选中指标的值
     *
     * @return selectValue
     */
    public String[] getSelectValue() {
        return selectValue;
    }

    /**
     * 根据选中指标的值，给控件赋值
     *
     * @param selectValue
     */
    public void setSelectValue(String[] selectValue) {
        checkSelectValue(jsArr, 0, selectValue);
        setTextCotent();
        backFunc();
    }

    public void setSelectVal(String[] selectValue) {
        checkSelectValue(jsArr, 0, selectValue);
        setTextCotent();
    }

    /**
     * 获取选中指标的位置
     *
     * @return selectOrder
     */
    public int[] getSelectOrder() {
        return selectOrder;
    }

    /**
     * 根据选中指标的位置，给控件赋值
     *
     * @param selectOrder
     */
    public void setSelectOrder(int[] selectOrder) {
        checkSelectOrder(jsArr, 0, selectOrder);
        setTextCotent();
        backFunc();
    }

    /**
     * 获取去选中指标名称
     *
     * @return selectOrder
     */
    public String[] getSelectName() {
        return selectName;
    }

    /**
     * 根据选中指标的名称，给控件赋值-----不推荐
     *
     * @param selectName
     */
    public void setSelectName(String[] selectName) {
        checkSelectName(jsArr, 0, selectName);
        setTextCotent();
        backFunc();
    }

    public void setButtonType(String buttonType) {
        this.buttonType = buttonType;
    }

    public String getButtonType() {
        return buttonType;
    }

    public com.ztesoft.level1.ui.MyAlertDialog getAd() {
        return ad;
    }

    public void setAd(com.ztesoft.level1.ui.MyAlertDialog ad) {
        this.ad = ad;
    }

    /**
     * 选中文本的颜色
     *
     * @return
     */
    public int getValue_text_color() {
        return value_text_color;
    }

    public void setVALUE_TEXT_COLOR(int value_text_color) {
        this.value_text_color = value_text_color;
    }

    /**
     * 未选中文本的颜色
     *
     * @return
     */
    public int getITEMS_TEXT_COLOR() {
        return items_text_color;
    }

    public void setItems_text_color(int items_text_color) {
        this.items_text_color = items_text_color;
    }

    public int getWheelTextSize() {
        return wheelTextSize;
    }

    /**
     * 滚轮中文本大小
     *
     * @return
     */
    public void setWheelTextSize(int wheelTextSize) {
        this.wheelTextSize = wheelTextSize;
    }

    /**
     * 选中阴影色
     *
     * @return
     */
    public Drawable getCenterDrawable() {
        return centerDrawable;
    }

    public void setCenterDrawable(int centerDrawable) {
        this.centerDrawable = context.getResources().getDrawable(R.drawable.wheel_val);
    }

    public int[] getWheelWidth() {
        return wheelWidth;
    }

    /**
     * 设置滚轮组宽度
     *
     * @param wheelWidth
     */
    public void setWheelWidth(int[] wheelWidth) {
        this.wheelWidth = wheelWidth;
    }

    public void setShowTwoLevel(boolean isShowTwoLevel) {
        this.isShowTwoLevel = isShowTwoLevel;
    }

    public void setShowParantWhenSelectAll(boolean isShowParantWhenSelectAll) {
        this.isShowParantWhenSelectAll = isShowParantWhenSelectAll;
    }

    private void setTextCotent() {
        if (isShowTwoLevel && selectName.length > 1) {
            this.setText(selectName[selectName.length - 2] + levelSplit + selectName[selectName
                    .length - 1]);
        } else {
            if (isShowParantWhenSelectAll && selectName.length > 1) {
                for (int i = 1; i < selectName.length; i++) {
                    if ("".equals(selectValue[i])) {
                        this.setText(selectName[i - 1]);
                        return;
                    }
                }
            }
            this.setText(selectName[selectName.length - 1]);
        }
    }

    public JSONArray getJsArr() {
        return jsArr;
    }

    public void setBackObj(Object obj) {
        this.backObj = obj;
    }
}
