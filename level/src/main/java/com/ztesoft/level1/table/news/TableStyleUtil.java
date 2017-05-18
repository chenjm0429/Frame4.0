package com.ztesoft.level1.table.news;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.Level1Util;
import com.ztesoft.level1.R;
import com.ztesoft.level1.util.DateUtil;
import com.ztesoft.level1.util.NumericalUtil;

public class TableStyleUtil {

    private Context ctx;
    private int bodyTextSize = 14;
    private int bodyTextColor = Color.BLACK;
    private int headTextSize = 15;
    private int headTextColor = Color.WHITE;
    private boolean titleSingle = false;
    private String[] tableType;
    private int pad = 4;
    private String errorValue = "";// 异常数值默认显示值

    public TableStyleUtil(Context ctx, String[] tableType) {
        this.ctx = ctx;
        this.tableType = tableType;
        pad = Level1Util.dip2px(ctx, pad);
    }

    public LinearLayout drawTableBodyCell(int j, JSONObject obj) {
        String value = obj.optString("v" + (j + 1), "");
        String order = obj.optString("o" + (j + 1), "");
        String c = obj.optString("c" + (j + 1), "");
        JSONObject tmp = new JSONObject();
        try {
            tmp.put("v", value);
            tmp.put("o", order);
            tmp.put("c", c);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinearLayout tdLayout = new LinearLayout(ctx);
        tdLayout.setTag(tmp);
        tdLayout.setGravity(Gravity.CENTER);
        tdLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView fixColumnView = new TextView(ctx);
        fixColumnView.setGravity(Gravity.CENTER);
        tdLayout.setPadding(0, pad, 2, pad);
        fixColumnView.setTextSize(TypedValue.COMPLEX_UNIT_SP, bodyTextSize);
        fixColumnView.setTextColor(bodyTextColor);

        ImageView iv = null;

        if ("".equals(value)) {
            if (!"".equals(errorValue)) {// 错误显示不为空时，显示错误显示值，不需要进一步处理
                value = errorValue;
            }
        } else {
            try {
                if ("DAY".equalsIgnoreCase(tableType[j])) {
                    value = DateUtil.getInstance().convertDay_Type(value, "yyyyMMdd", "MM-dd");
                } else if ("MONTH".equalsIgnoreCase(tableType[j])) {
                    value = DateUtil.getInstance().convertDay_Type(value, "yyyyMMdd", "yyyy-MM");
                } else {//非日期处理
                    if (tableType[j].contains("C")) {
                        double tmpd = Double.parseDouble(value);
                        if (tmpd > 0) {
                            fixColumnView.setTextColor(Color.parseColor(Level1Bean.plusColor));
                        } else if (tmpd < 0) {
                            fixColumnView.setTextColor(Color.parseColor(Level1Bean.minusColor));
                        }
                    }
                    if (tableType[j].contains("I")) {
                        double tmpd = Double.parseDouble(value);
                        String picCode = "";
                        if (tmpd > 0) {//#############此处要修改，不是根据0来判断
                            picCode = Level1Bean.plusPicCode;
                        } else if (tmpd < 0) {
                            picCode = Level1Bean.minusPicCode;
                        } else {
                            picCode = Level1Bean.zeroPicCode;
                        }
                        iv = new ImageView(ctx);
                        iv.setScaleType(ScaleType.FIT_CENTER);
                        iv.setImageResource(R.drawable.class.getDeclaredField(picCode).getInt
                                (this));
                    }
                    //数值判断处理必须在百分号和千分位之前，否则无法转换为数值
                    if (tableType[j].contains("%")) {
                        value = value + "%";
                    }
                    if (tableType[j].contains(",")) {
                        value = NumericalUtil.getInstance().setThousands(value);
                    }
                }
            } catch (Exception e) {

            }
        }
        fixColumnView.setText(value);
        tdLayout.addView(fixColumnView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout
                .LayoutParams.MATCH_PARENT);
        if (iv != null) {
            tdLayout.addView(iv, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout
                    .LayoutParams.MATCH_PARENT);
        }
        return tdLayout;
    }

    public LinearLayout drawTableHeadCell(int j, String name, int width) {
        LinearLayout tableHeadCell = new LinearLayout(ctx);
        tableHeadCell.setOrientation(LinearLayout.HORIZONTAL);

        tableHeadCell.setId(j);

        ImageView imgView = new ImageView(ctx);
        int imageWidth = Level1Util.dip2px(ctx, 15);
        imgView.setVisibility(View.GONE);
        imgView.setAdjustViewBounds(true);// 是否保持图片的纵横比例

        TextView tx = drawTableHeadText(name);
        tableHeadCell.setGravity(Gravity.CENTER);
        tx.setGravity(Gravity.CENTER);
        TextPaint tp = tx.getPaint();
        float textWidth = tp.measureText(name);
        if (textWidth > (width - imageWidth) && imgView.getVisibility() == View.VISIBLE) {
            tx.setWidth(width - imageWidth);
        }

        tableHeadCell.addView(tx);
        tableHeadCell.addView(imgView, imageWidth, imageWidth);

        return tableHeadCell;
    }

    public LinearLayout drawMultiTableHeadCell(String name, int span) {
        LinearLayout tableHeadCell = new LinearLayout(ctx);
        tableHeadCell.setOrientation(LinearLayout.HORIZONTAL);
//		tableHeadCell.setBackgroundDrawable(Level1Util.MAIN_RADIO_BORD(ctx, Color.TRANSPARENT, 
// Color.WHITE, 1, 0));
        tableHeadCell.setGravity(Gravity.CENTER);
        TableRow.LayoutParams tlp = new TableRow.LayoutParams();
        tlp.span = span;// TableRow.LayoutParams可以设置这个属性
        tableHeadCell.setLayoutParams(tlp); // 别忘了把参数设置回去

        TextView titleView = drawTableHeadText(name);
        tableHeadCell.addView(titleView);
        return tableHeadCell;
    }

    /**
     * 绘制表头文本框
     *
     * @param name 文本框显示内容
     * @return
     */
    private TextView drawTableHeadText(String name) {
        TextView tx = new TextView(ctx);
        tx.setText(Html.fromHtml(name));
        tx.setTextColor(headTextColor);
        if (titleSingle) {
            tx.setSingleLine(true);// 设置单行
            tx.setEllipsize(TextUtils.TruncateAt.MIDDLE); // 设置省略号居中
        } else {
            tx.setSingleLine(false);
        }
        tx.setTextSize(TypedValue.COMPLEX_UNIT_SP, headTextSize);
        return tx;
    }

    public void setBodyTextSize(int valueSize) {
        this.bodyTextSize = valueSize;
    }

    public void setBodyTextColor(int textColor) {
        this.bodyTextColor = textColor;
    }

    public void setBodyTextColor(String textColor) {
        this.bodyTextColor = Color.parseColor(textColor);
    }

    public void setHeadTextSize(int headTextSize) {
        this.headTextSize = headTextSize;
    }

    public void setHeadTextColor(int headTextColor) {
        this.headTextColor = headTextColor;
    }

    public void setTitleSingle(boolean titleSingle) {
        this.titleSingle = titleSingle;
    }

    public void setErrorValue(String errorValue) {
        this.errorValue = errorValue;
    }

}
