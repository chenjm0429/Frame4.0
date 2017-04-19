package com.ztesoft.ui.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ztesoft.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件名称 : LabelSelectUI
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 流布局标签选择控件，支持多选
 * <p>
 * 创建时间 : 2017/3/24 15:56
 * <p>
 */
public class LabelSelectUI extends LinearLayout {

    private FlowLayout mFlowLayout;

    private LayoutInflater mInflater;
    private LinearLayout.LayoutParams params;

    private JSONArray jsonArray;
    private String code = "code";
    private String name = "name";
    private String[] selectCodes;

    private List<TextView> texts = new ArrayList<TextView>();
    private List<String> codes = new ArrayList<String>();
    private List<String> names = new ArrayList<String>();

    private List<String> selectCodesArray = new ArrayList<String>();
    private List<String> selectNamesArray = new ArrayList<String>();

    //是否支持多选
    private boolean isMultipleChoice = false;

    private OnItemClickListener mOnItemClickListener;

    public LabelSelectUI(Context context, String code, String name, String[] selectCodes) {
        super(context);

        this.code = code;
        this.name = name;
        this.selectCodes = selectCodes;

        mInflater = LayoutInflater.from(context);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;

        mFlowLayout = new FlowLayout(context);
        this.addView(mFlowLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    public void create(JSONArray jsonArray) {
        this.jsonArray = jsonArray;

        initView(selectCodes);
    }

    /**
     * 刷新页面显示的选中状态
     *
     * @param selectCodes
     */
    public void refreshSelect(String[] selectCodes) {
        this.selectCodes = selectCodes;

        if (null == selectCodes) {
            return;
        }

        selectCodesArray.clear();
        for (int i = 0; i < selectCodes.length; i++) {
            String cd = selectCodes[i];

            for (int j = 0; j < codes.size(); j++) {
                if (cd.equals(codes.get(j))) {
                    selectCodesArray.add(cd);
                    setTextStyle(texts.get(j), true);

                } else {
                    setTextStyle(texts.get(j), false);
                }
            }
        }
    }

    private void initView(String[] selectCodes) {
        for (int i = 0; i < jsonArray.length(); i++) {
            View itemView = mInflater.inflate(R.layout.view_label_select_item, null);
            mFlowLayout.addView(itemView, params);

            TextView text = (TextView) itemView.findViewById(R.id.text);
            texts.add(text);

            JSONObject jsonObj = jsonArray.optJSONObject(i);

            String nameStr = jsonObj.optString(name);
            String codeStr = jsonObj.optString(code);

            codes.add(codeStr);
            names.add(nameStr);

            if (hasElement(codeStr, selectCodes)) {
                selectCodesArray.add(codeStr);
                selectNamesArray.add(nameStr);
                setTextStyle(text, true);
            } else {
                setTextStyle(text, false);
            }

            text.setText(nameStr);

            itemView.setOnClickListener(new ItemClickListener(text, codeStr, nameStr));
        }
    }

    class ItemClickListener implements OnClickListener {
        private TextView text;
        private String code;
        private String name;

        public ItemClickListener(TextView text, String code, String name) {
            this.text = text;
            this.code = code;
            this.name = name;
        }

        @Override
        public void onClick(View v) {
            boolean flag = (Boolean) text.getTag();

            if (flag) {
                setTextStyle(text, false);
                int index = selectCodesArray.indexOf(code);
                if (index != -1) {
                    selectCodesArray.remove(index);
                    selectNamesArray.remove(index);
                }
            } else {

                if (isMultipleChoice) {
                    setTextStyle(text, true);
                    selectCodesArray.add(code);
                    selectNamesArray.add(name);
                } else {
                    setTextStyle(text, true);
                    selectCodesArray.clear();
                    selectCodesArray.add(code);
                    selectNamesArray.clear();
                    selectNamesArray.add(name);
                }

                mOnItemClickListener.onItemClick(selectCodesArray, selectNamesArray);
            }
        }
    }

    private void setTextStyle(TextView text, boolean isSelect) {
        if (isSelect) {
            if (isMultipleChoice) {
                text.setBackgroundResource(R.drawable.compare_area_selected_bg);
                text.setTag(true);
            } else {
                for (int i = 0; i < texts.size(); i++) {
                    if (texts.get(i) == text) {
                        texts.get(i).setBackgroundResource(R.drawable.compare_area_selected_bg);
                        texts.get(i).setTag(true);
                    } else {
                        texts.get(i).setBackgroundResource(R.drawable.compare_area_unselected_bg);
                        texts.get(i).setTag(false);
                    }
                }
            }

        } else {
            text.setBackgroundResource(R.drawable.compare_area_unselected_bg);
            text.setTag(false);
        }
    }


    public String[] getSelectCodes() {
        if (selectCodesArray.size() > 0) {
            String[] ss = new String[selectCodesArray.size()];
            for (int i = 0; i < selectCodesArray.size(); i++) {
                ss[i] = selectCodesArray.get(i);
            }
            return ss;
        } else {
            return new String[]{};
        }
    }

    public String[] getSelectNames() {
        if (selectNamesArray.size() > 0) {
            String[] ss = new String[selectNamesArray.size()];
            for (int i = 0; i < selectNamesArray.size(); i++) {
                ss[i] = selectNamesArray.get(i);
            }
            return ss;
        } else {
            return new String[]{};
        }
    }

    public void clearSelect() {
        selectCodesArray.clear();

        for (int i = 0; i < texts.size(); i++) {
            setTextStyle(texts.get(i), false);
        }
    }

    private boolean hasElement(String code, String[] codes) {
        for (int i = 0; i < codes.length; i++) {
            if (code.equals(codes[i])) {
                return true;
            }
        }

        return false;
    }

    public void setMultipleChoice(boolean multipleChoice) {
        isMultipleChoice = multipleChoice;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(List<String> codes, List<String> names);
    }
}
