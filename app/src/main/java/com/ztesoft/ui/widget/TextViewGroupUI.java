package com.ztesoft.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ztesoft.level1.Level1Util;
import com.ztesoft.utils.PromptUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 文件名称 : TextViewGroupUI
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 单排多按钮控件
 * <p>
 * 创建时间 : 2017/3/24 16:05
 * <p>
 */
public class TextViewGroupUI extends LinearLayout {

    private Context mContext;
    private JSONArray datas = null;
    private String mNameKey, mValueKey;
    private ViewSelectedCallBack mCallBack = null;
    private int currentPosition = -1;
    private int selectedColor = 0xFFFF0000;
    private int unSelectedColor = 0xFF000000;
    private int divderColor = 0xFF000000;
    private Object backObj;
    private ArrayList<TextView> tvList = new ArrayList<TextView>();

    public interface ViewSelectedCallBack {
        void onViewSelected(int position, String name, String code, Object obj);
    }

    public void registerCallback(ViewSelectedCallBack callback) {
        mCallBack = callback;
    }

    public TextViewGroupUI(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public TextViewGroupUI(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        this.setBackgroundColor(0x00FFFFFF);
        this.setOrientation(LinearLayout.HORIZONTAL);
        this.setGravity(Gravity.CENTER);
        int padding = Level1Util.dip2px(mContext, 5);
        this.setPadding(0, padding, 0, padding);
    }

    public void setDataSource(JSONArray datas, String nameKey, String valueKey) {
        tvList.clear();
        this.datas = datas;
        this.removeAllViews();
        this.currentPosition = -1;
        if (null == datas) {
            return;
        }

        this.mNameKey = nameKey;
        this.mValueKey = valueKey;
        try {
            for (int i = 0; i < datas.length(); i++) {
                TextView tv = new TextView(mContext);
                tv.setTextSize(18);
                tv.setGravity(Gravity.CENTER);
                int padding = Level1Util.dip2px(mContext, 10);
                tv.setPadding(padding, 0, padding, 0);
                tv.setText(datas.getJSONObject(i).optString(mNameKey));
                tv.setTag(i);
                this.addView(tv, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                if (i != datas.length() - 1) {
                    View diver = new View(mContext);
                    diver.setBackgroundColor(divderColor);
                    this.addView(diver, Level1Util.dip2px(mContext, 1), Level1Util.dip2px
                            (mContext, 20));
                }
                tv.setOnClickListener(txtClicker);
                tvList.add(tv);
            }
            currentPosition = datas.length() - 1;
            setTextStatus();
        } catch (Exception e) {
            PromptUtils.instance.displayToastString(mContext, false, "数据源错误!");
            e.printStackTrace();
        }
    }

    private View.OnClickListener txtClicker = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int nowPosition = (Integer) v.getTag();
            if (nowPosition == currentPosition) {
                return;
            }

            currentPosition = nowPosition;
            setTextStatus();

            if (null != mCallBack) {
                try {
                    JSONObject json = datas.getJSONObject(nowPosition);
                    mCallBack.onViewSelected(nowPosition, json.optString(mNameKey), json
                            .optString(mValueKey), backObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void setTextStatus() {
        for (int i = 0; i < tvList.size(); i++) {
            if (i == currentPosition) {
                tvList.get(i).setTextColor(selectedColor);
            } else {
                tvList.get(i).setTextColor(unSelectedColor);
            }
        }
    }

    public String getSelectedCode() {
        if (-1 == currentPosition || null == datas || datas.length() == 1) {
            return null;
        }
        String value = null;
        try {
            value = datas.getJSONObject(currentPosition).optString(mValueKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public void setBackObj(Object backObj) {
        this.backObj = backObj;
    }

}
