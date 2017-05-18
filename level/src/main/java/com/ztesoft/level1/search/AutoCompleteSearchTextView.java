package com.ztesoft.level1.search;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/***
 * 搜索下拉框
 *
 * @author wangsq
 *         kpiArray:[{"kpiId":"0001","kpiName":"新增用户"},{},……………………]
 */
public class AutoCompleteSearchTextView extends LinearLayout {
    private AutoCompleteTextView atv = null;
    Drawable img_off = null;
    private Context context;
    private int style = 1;//0:点按钮进入 1：点list进入
    //字体大小
    private int textSize = 12;
    //字体颜色
    private String textColor = "#000000";
    //输入框提示
    private String hint = "输入要查找的指标";
    //点击事件
    private OnAutoItemClickListener itmClickListener;
    //最多显示的匹配个数 
    private int showCount = 2000;
    //下拉框背景，文字颜色
    private String dropBgColor = "#ffffff";
    private String dropTxtColor = "#000000";

    private String code = "code";
    private String name = "name";

    /***
     * 初始化
     *
     * @param context
     */
    public AutoCompleteSearchTextView(Context context) {
        super(context);
        this.context = context;
        atv = new AutoCompleteTextView(context);
        Resources res = context.getResources();
        img_off = res.getDrawable(android.R.drawable.ic_menu_search);
        // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
        img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());

        atv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                img_off.getMinimumHeight() + 4, 1));
        this.addView(atv);
    }

    public AutoCompleteSearchTextView(Context context, String codeDesc, String nameDesc) {
        this(context);
        this.code = codeDesc;
        this.name = nameDesc;
    }

    /**
     * 增加查询按钮
     */
    public void addQueryButton() {
        Button queryButton = new Button(context);
        queryButton.setText("查询");
        this.addView(queryButton, new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                .WRAP_CONTENT, img_off.getMinimumHeight() + 4));
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itmClickListener != null) {
                    itmClickListener.onItemClick(null, atv.getText().toString());
                }
                if (((Activity) context).getCurrentFocus() != null) {
                    ((InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(((Activity) context).getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
    }

    /**
     * 下拉框赋值
     *
     * @param kpiArray
     * @throws JSONException
     */
    public void create(JSONArray kpiArray) throws JSONException {
        atv.setHint(hint);
        atv.setTextSize(textSize);
        atv.setTextColor(Color.parseColor(textColor));
        atv.setThreshold(1);
        atv.setCompoundDrawables(img_off, null, null, null); //设置左图标

        ArrayList<String[]> autoStrs = new ArrayList<String[]>();
        String kpiId;
        String kpiName;
        JSONObject tmpObj;
        for (int i = 0; i < kpiArray.length(); i++) {
            tmpObj = kpiArray.getJSONObject(i);
            kpiId = tmpObj.getString(code);
            kpiName = tmpObj.getString(name);
            String[] temp = new String[]{kpiId, kpiName};
            autoStrs.add(temp);
        }
        ArrayList<String[]> result = new ArrayList<String[]>();
        AutoCompleteAdapter adapter = new AutoCompleteAdapter(context, autoStrs, result,
                showCount, new searchClickListener());
        adapter.setSTYLE(style);
        adapter.setBgColor(dropBgColor);
        adapter.setTextColor(dropTxtColor);
        atv.setAdapter(adapter);
    }

    private class searchClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            TextView iv = (TextView) v;
            LinearLayout linear = (LinearLayout) iv.getParent();
            TextView tv = (TextView) linear.getChildAt(0);
            TextView tvid = (TextView) linear.getChildAt(1);
            String dim_code = (String) tvid.getText();
            String dim_name = (String) tv.getText();
            if (itmClickListener != null) {
                itmClickListener.onItemClick(dim_code, dim_name);
            }
            atv.setText(dim_name);
            //隐藏下拉搜索框
            atv.dismissDropDown();
            //隐藏软键盘
            ((InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(((Activity) context).getCurrentFocus()
                            .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * @param style 0:点按钮进入 1：点list进入
     */
    public void setStyle(int style) {
        this.style = style;
    }

    public interface OnAutoItemClickListener {
        //点击事件
        void onItemClick(String kpiId, String kpiName);
    }

    public void setItmClickListener(OnAutoItemClickListener itmClickListener) {
        this.itmClickListener = itmClickListener;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public void setShowCount(int showCount) {
        this.showCount = showCount;
    }

    public void setDropBgColor(String dropbgColor) {
        this.dropBgColor = dropbgColor;
    }

    public void setDropTxtColor(String droptxtColor) {
        this.dropTxtColor = droptxtColor;
    }
}
