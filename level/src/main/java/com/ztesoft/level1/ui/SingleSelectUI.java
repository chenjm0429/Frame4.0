package com.ztesoft.level1.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ztesoft.level1.R;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 文件名称 : SingleSelectUI
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 单项选择下拉控件
 * <p>
 * 创建时间 : 2017/3/24 16:04
 * <p>
 */
public class SingleSelectUI extends TextView {

    private Context context;

    // 取值变量
    protected String code = "code";
    protected String name = "name";

    private JSONArray jsArr;
    private String selectValue;// 初始化值
    private View view;

    private PopupWindow mDialog;

    private OnSingleSelectListener mOnSingleSelectListener;

    private String defalultColor = "#FFFFFF";
    private String selectColor = "#049BE4";

    public SingleSelectUI(Context context, String code, String name) {
        super(context);

        this.context = context;
        this.code = code;
        this.name = name;

        this.setGravity(Gravity.CENTER);

        this.setOnClickListener(new SelectOnClickListener());
    }

    /**
     * 获得参数后，初始化组件
     *
     * @param jsArr      数据源
     * @param initSelect 默认选择状态
     * @param view       弹出框基于该view弹出，null时基于本身弹出
     */
    public void create(JSONArray jsArr, String initSelect, View view) {
        this.jsArr = jsArr;
        this.selectValue = initSelect;
        this.view = view;

        this.setGravity(Gravity.CENTER);
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        this.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        this.setSingleLine(true);
        this.setTextColor(Color.parseColor("#333333"));
        this.setOnClickListener(new SelectOnClickListener());

        if (TextUtils.isEmpty(selectValue)) {
            selectValue = jsArr.optJSONObject(0).optString(code);
        }

        setContent();

        dateFunc();
    }

    private void setContent() {
        for (int i = 0; i < jsArr.length(); i++) {
            JSONObject jsonObj = jsArr.optJSONObject(i);

            String _name = jsonObj.optString(name);
            String _code = jsonObj.optString(code);

            if (selectValue.equals(_code))
                this.setText(_name);
        }
    }

    /**
     * 绘制弹出框
     */
    private void dateFunc() {

        final ListView listView = new ListView(context, null, R.style.normal_listview_style);
        listView.setBackgroundColor(Color.WHITE);

        final SingleAdapter adapter = new SingleAdapter(context, selectValue);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                JSONObject jsonObj = jsArr.optJSONObject(position);
                selectValue = jsonObj.optString(code);
                adapter.setSelectValue(selectValue);
                adapter.notifyDataSetChanged();

                String _name = jsonObj.optString(name);
                SingleSelectUI.this.setText(_name);

                mOnSingleSelectListener.onSingleSelect(position, jsonObj.optString(code), jsonObj
                        .optString(name), jsonObj);

                if (null != mDialog && mDialog.isShowing())
                    mDialog.dismiss();
            }
        });

        if (null == mDialog) {
            mDialog = new PopupWindow(listView, WindowManager.LayoutParams.MATCH_PARENT,
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

    class SingleAdapter extends BaseAdapter {

        private Context context;
        private String selectValue;

        public SingleAdapter(Context contex, String selectValue) {
            this.context = contex;
            this.selectValue = selectValue;
        }

        @Override
        public int getCount() {
            return jsArr.length();
        }

        @Override
        public Object getItem(int position) {
            return jsArr.optJSONObject(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView text = new TextView(context);
            text.setPadding(15, 15, 15, 15);
            text.setTextSize(16);

            JSONObject jsonObj = jsArr.optJSONObject(position);
            text.setText(jsonObj.optString(name));

            String cd = jsonObj.optString(code);
            if (cd.equals(selectValue)) {
                text.setBackgroundColor(Color.parseColor(selectColor));
                text.setTextColor(Color.WHITE);
            } else {
                text.setBackgroundColor(Color.parseColor(defalultColor));
                text.setTextColor(Color.parseColor("#333333"));
            }

            return text;
        }

        public void setSelectValue(String selectValue) {
            this.selectValue = selectValue;
        }
    }

    public void setOnSingleSelectListener(OnSingleSelectListener onSingleSelectListener) {
        this.mOnSingleSelectListener = onSingleSelectListener;
    }

    public interface OnSingleSelectListener {
        void onSingleSelect(int position, String code, String name, JSONObject jsonObj);
    }

    private void setBgAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        ((Activity) context).getWindow().setAttributes(lp);
    }
}