package com.ztesoft.level1.checkbox;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ztesoft.level1.Level1Util;
import com.ztesoft.level1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/***
 * 单选，复选按钮
 *
 * @author wangsq
 *         kpiArray:[{"kpiId":"0001","kpiName":"新增用户"},{},……………………]
 */
public class CheckBoxView extends LinearLayout {
    private Context context;
    // 显示样式 V:竖 H:横
    private String displayStyle = "v";
    // 是否是单选
    private boolean singleCheck = false;
    // 选择的值
    private String[] selValue;
    // 一行显示几个 只有displayStyle为“v"还有用
    private int colNum = 0;
    // 单选，多选摆放的位置 L:前面 R:后面 ---未使用
    private String displayPosition = "l";
    // 字体颜色
    private String textColor = "#000000";
    private String selTextColor = "#ffffff";
    private int textSize = 15;
    // 联动事件
    private OnUnitItemClickListener onItemClick = null;
    // 禁止选中
    private OnStopItemClickListener stopItemClick = null;
    private LinkedHashMap<String, String> selectMap = new LinkedHashMap<String, String>();
    // 所有的button组
    private ArrayList<LinearLayout> allCompButton = new ArrayList<LinearLayout>();
    // 最多选择几个
    private int maxSelNum = -1;

    private String code = "kpiId";
    private String name = "kpiName";
    // 选中、未选中的图片
    private int selImage = R.drawable.diycheck;
    private int noselImage = R.drawable.diynocheck;

    public CheckBoxView(Context context) {
        super(context);
        this.context = context;
        this.setGravity(Gravity.CENTER);
    }

    public CheckBoxView(Context context, String codeDesc, String nameDesc) {
        this(context);
        this.code = codeDesc;
        this.name = nameDesc;
    }

    /***
     * 开始画组件
     *
     * @param kpiArray
     */
    public void create(JSONArray kpiArray) {
        try {
            if (displayStyle.equalsIgnoreCase("v")) {
                createV(kpiArray);
            } else {
                createH(kpiArray);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 横显示
    private void createH(JSONArray kpiArray) throws JSONException {
        LinearLayout contextView = new LinearLayout(context);
        HorizontalScrollView sv = new HorizontalScrollView(context);
        sv.addView(contextView);
        sv.setHorizontalScrollBarEnabled(false);
        sv.setHorizontalFadingEdgeEnabled(false);
        this.addView(sv);
        contextView.setOrientation(LinearLayout.HORIZONTAL);
        contextView.setPadding(5, 2, 5, 2);
        int kpiNum = kpiArray.length();
        for (int i = 0; i < kpiNum; i++) {
            contextView.addView(drawUnit(kpiArray.getJSONObject(i), i));
        }
    }

    // 竖显示
    private void createV(JSONArray kpiArray) throws JSONException {
        LinearLayout contextView = new LinearLayout(context);
        ScrollView sv = new ScrollView(context);
        // contextView.setBackgroundColor(Color.RED);
        sv.addView(contextView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        sv.setVerticalScrollBarEnabled(false);
        sv.setVerticalFadingEdgeEnabled(false);
        // sv.setBackgroundColor(Color.BLACK);
        this.addView(sv, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        contextView.setOrientation(LinearLayout.VERTICAL);
        contextView.setPadding(5, 2, 5, 2);
        int kpiNum = kpiArray.length();
        LinearLayout rowLayout = null;
        if (colNum < 1) {
            colNum = 1;
        }
        BitmapDrawable bt = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R
                .drawable.diyline));
        bt.setTileModeX(TileMode.REPEAT);
        bt.setDither(true);
        for (int i = 0; i < kpiNum; i++) {
            if (i % colNum == 0) {
                rowLayout = new LinearLayout(context);
                rowLayout.setGravity(Gravity.CENTER);
                rowLayout.setPadding(5, 5, 5, 5);
                contextView.addView(rowLayout);
                View v = new View(context);
                v.setBackgroundDrawable(bt);
                contextView.addView(v, LayoutParams.MATCH_PARENT, 1);
            }
            rowLayout.addView(drawUnit(kpiArray.getJSONObject(i), i));
        }
        // 补空单元格
        if (colNum > 1 && kpiNum > colNum && (kpiNum % colNum) > 0) {
            int spanNum = colNum - (kpiNum % colNum);
            for (int i = 0; i < spanNum; i++) {
                LinearLayout row = new LinearLayout(context);
                row.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                rowLayout.addView(row);
            }
        }
    }

    // 画单元信息
    private LinearLayout drawUnit(JSONObject dataObject, int index) throws JSONException {
        String kpiId = dataObject.getString(code);
        String kpiName = dataObject.getString(name);
        LinearLayout row = new LinearLayout(context);
        row.setGravity(Gravity.CENTER);
        int pad = Level1Util.dip2px(context, 5);
        row.setPadding(pad, pad, pad, 0);
        row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .WRAP_CONTENT, 1));
        LinearLayout td = new LinearLayout(context);
        td.setGravity(Gravity.CENTER_VERTICAL);
        td.setPadding(pad, pad, pad, pad);
        // 指标ID
        TextView txtid = new TextView(context);
        txtid.setText(kpiId);
        txtid.setVisibility(View.GONE);

        // 指标描述
        TextView txt = new TextView(context);
        txt.setTextColor(Color.parseColor(textColor));
        txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        txt.setGravity(Gravity.CENTER_VERTICAL);
        txt.setText(kpiName);
        if (singleCheck) {
            td.setBackgroundResource(noselImage);
            td.setTag("N");
            if (selValue != null && Arrays.asList(selValue).contains(kpiId)) {
                // 把前面的radio不选，保证只选择一个
                for (int x = 0; x < allCompButton.size(); x++) {
                    allCompButton.get(x).setBackgroundResource(noselImage);
                    txt.setTextColor(Color.parseColor(textColor));
                    allCompButton.get(x).setTag("N");
                    selectMap = new LinkedHashMap<String, String>();
                    selectMap.put(kpiId, kpiName);
                }
                td.setBackgroundResource(selImage);
                txt.setTextColor(Color.parseColor(selTextColor));
                td.setTag("Y");
            } else {
                // 默认选择第一个
                if (index == 0) {
                    td.setBackgroundResource(selImage);
                    txt.setTextColor(Color.parseColor(selTextColor));
                    td.setTag("Y");
                    selectMap.put(kpiId, kpiName);
                }
            }
        } else {
            td.setBackgroundResource(noselImage);
            txt.setTextColor(Color.parseColor(textColor));
            td.setTag("N");
            if (selValue != null && Arrays.asList(selValue).contains(kpiId)) {
                td.setBackgroundResource(selImage);
                txt.setTextColor(Color.parseColor(selTextColor));
                td.setTag("Y");
                selectMap.put(kpiId, kpiName);
            }
        }
        if (displayPosition.equalsIgnoreCase("l")) {
            td.addView(txtid);
            td.addView(txt);
        } else {
            td.addView(txtid);
            td.addView(txt);
        }

        row.addView(td, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        allCompButton.add(td);
        row.setOnClickListener(new RowOnClickListener());
        return row;
    }

    class RowOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {

            v = ((LinearLayout) (v)).getChildAt(0);
            if (maxSelNum > 0) {
                if (selectMap.size() >= maxSelNum && "N".equals((String) v.getTag())) {// 已选数量大于允许值
                    // 并且
                    // 继续选中时提示
                    Toast.makeText(context, getContext().getString(R.string.error_string_most, 
                            maxSelNum), Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            TextView tx, txId;

            if (displayPosition.equalsIgnoreCase("l")) {
                tx = (TextView) ((LinearLayout) (v)).getChildAt(1);
                txId = (TextView) ((LinearLayout) (v)).getChildAt(0);
            } else {
                tx = (TextView) ((LinearLayout) (v)).getChildAt(1);
                txId = (TextView) ((LinearLayout) (v)).getChildAt(0);
            }

            if ("Y".equals((String) v.getTag())) {// 点击已选中选项
                if (!singleCheck) {// 单选不可取消选择
                    v.setBackgroundResource(noselImage);
                    tx.setTextColor(Color.parseColor(textColor));
                    v.setTag("N");
                    selectMap.remove(txId.getText().toString());
                }
            } else {
                // 禁止选中
                if (stopItemClick != null && stopItemClick.onStopItemClick(txId.getText()
                        .toString(), tx.getText().toString()))
                    return;

                if (singleCheck) {// 如果是单选，需要将其他选项置为未选中
                    for (int x = 0; x < allCompButton.size(); x++) {
                        allCompButton.get(x).setBackgroundResource(noselImage);
                        allCompButton.get(x).setTag("N");
                        ((TextView) (allCompButton.get(x)).getChildAt(1)).setTextColor(Color
                                .parseColor(textColor));
                    }
                    selectMap = new LinkedHashMap<String, String>();
                    selectMap.put(txId.getText().toString(), tx.getText().toString());
                }

                v.setBackgroundResource(selImage);
                tx.setTextColor(Color.parseColor(selTextColor));
                v.setTag("Y");
                selectMap.put(txId.getText().toString(), tx.getText().toString());
            }
            // 事件触发
            if (onItemClick != null) {
                String x = (String) v.getTag();
                onItemClick.onItemClick(txId.getText().toString(), tx.getText().toString(), "Y"
                        .equals(x) ? true : false);
                onItemClick.onItemClick(selectMap);
            }
        }
    }

    // -------------------------------------事件注册--------------------------------------------//
    public interface OnUnitItemClickListener {
        // 点击事件
        void onItemClick(String kpiId, String kpiName, boolean isCheck);

        void onItemClick(LinkedHashMap<String, String> selectMap);
    }

    /**
     * 禁止点击选中
     *
     * @author Sing
     */
    public interface OnStopItemClickListener {
        // 返回true,则禁止选中
        boolean onStopItemClick(String kpiId, String kpiName);
    }

    public void setDisplayStyle(String displayStyle) {
        this.displayStyle = displayStyle;
    }

    public String[] getSelValue() {
        if (selectMap.size() > 0) {
            selValue = new String[selectMap.size()];
            Iterator<?> iter = selectMap.entrySet().iterator();
            int i = 0;
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry) iter.next();
                selValue[i] = (String) entry.getKey();
                i++;
            }
        } else {
            selValue = new String[0];
        }
        return selValue;
    }

    public void setSelValue(String[] selValue) {
        this.selValue = selValue;
    }

    public String[] getSelText() {
        String selText[] = null;
        if (selectMap.size() > 0) {
            selText = new String[selectMap.size()];
            Iterator<?> iter = selectMap.entrySet().iterator();
            int i = 0;
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry) iter.next();
                selText[i] = (String) entry.getValue();
                i++;
            }
        } else {
            selText = new String[0];
        }
        return selText;
    }

    public void setDisplayPosition(String displayPosition) {
        this.displayPosition = displayPosition;
    }

    public void setSingleCheck(boolean singleCheck) {
        this.singleCheck = singleCheck;
    }

    public void setColNum(int colNum) {
        this.colNum = colNum;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public void setMaxSelNum(int maxSelNum) {
        this.maxSelNum = maxSelNum;
    }

    public void setSelImage(int selImage) {
        this.selImage = selImage;
    }

    public void setNoselImage(int noselImage) {
        this.noselImage = noselImage;
    }

    public void setOnItemClick(OnUnitItemClickListener onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void setOnStopItemClick(OnStopItemClickListener onStopItemClick) {
        this.stopItemClick = onStopItemClick;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setSelTextColor(String selTextColor) {
        this.selTextColor = selTextColor;
    }
}