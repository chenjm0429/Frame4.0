package com.ztesoft.level1.table.util;

import org.json.JSONException;
import org.json.JSONObject;

import com.ztesoft.level1.Level1Util;
import com.ztesoft.level1.util.DateUtil;
import com.ztesoft.level1.util.NumericalUtil;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TableStyleUtil {

    private Context ctx;
    private int bodyTextSize = 14;
    private int bodyTextColor = Color.BLACK;
    private int headTextSize = 15;
    private int headTextColor = Color.WHITE;
    private boolean headIsBold = true;
    private boolean titleSingle = false;
    private String[] tableType;
    private int pad = 7;

    private boolean bodyTextIsLeft = false; // 表格内容居左，默认居中
    private boolean isAlignWithType = true; // 是否按照type设置字段的对齐方式

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
        TextView fixColumnView = new TextView(ctx);
        if (isAlignWithType) {
            if (bodyTextIsLeft) {
                fixColumnView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            } else {
                if (tableType[j].startsWith("%") || tableType[j].equalsIgnoreCase("INT")) {
                    fixColumnView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                } else {
                    fixColumnView.setGravity(Gravity.CENTER);
                }
            }
        } else {
            fixColumnView.setGravity(Gravity.CENTER);
        }

        tdLayout.setPadding(0, pad, 0, pad);
        fixColumnView.setTextSize(TypedValue.COMPLEX_UNIT_SP, bodyTextSize);
        fixColumnView.setTextColor(bodyTextColor);

        if (value == null)
            value = "";
        // if ("--".equalsIgnoreCase(value.trim())) {
        // fixColumnView.setText(value.trim());
        // tdLayout.addView(fixColumnView);
        // tabRow.addView(tdLayout, columnWidths[j],
        // LinearLayout.LayoutParams.FILL_PARENT);
        //
        // return tdLayout;
        // }
        if ("%".equalsIgnoreCase(tableType[j])) {
            if (value.trim().length() > 0) {
                fixColumnView.setText(value + "%");
            } else {
                fixColumnView.setText("--");
            }
        } else if ("%C".equalsIgnoreCase(tableType[j])) {
            if (value.trim().length() > 0) {
                fixColumnView.setText(value + "%");
            } else {
                fixColumnView.setText("--");
            }
        } else if ("INT".equalsIgnoreCase(tableType[j])) {
            if (value.trim().length() > 0) {
                value = NumericalUtil.getInstance().setThousands(value);
                fixColumnView.setText(value);
            } else {
                fixColumnView.setText("--");
            }
        } else if ("DAY".equalsIgnoreCase(tableType[j])) {
            value = DateUtil.getInstance().convertDay_Type(value, "yyyyMMdd", "MM-dd");
            fixColumnView.setText(value);
        } else if ("MONTH".equalsIgnoreCase(tableType[j])) {
            value = DateUtil.getInstance().convertDay_Type(value, "yyyyMMdd", "yyyy-MM");
            fixColumnView.setText(value);
        } else if ("DATE".equalsIgnoreCase(tableType[j])) {
            if (value.length() == 8) {
                value = DateUtil.getInstance().convertDay_Type(value, "yyyyMMdd", "MM-dd");
            } else if (value.length() == 6) {
                value = DateUtil.getInstance().convertDay_Type(value, "yyyyMM", "yyyy-MM");
            } else if (value.length() == 4) {
                value = DateUtil.getInstance().convertDay_Type(value, "MMdd", "MM-dd");
            }
            fixColumnView.setText(value);
        } else {
            fixColumnView.setText(value);
        }

        tdLayout.addView(fixColumnView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout
                .LayoutParams.MATCH_PARENT);
        return tdLayout;
    }

    public LinearLayout drawTableHeadCell(int j, String name, int width) {
        LinearLayout tableHeadCell = new LinearLayout(ctx);
        tableHeadCell.setOrientation(LinearLayout.HORIZONTAL);
        // tableHeadCell.setBackgroundDrawable(Level1Util.MAIN_RADIO_BORD(ctx,
        // Color.TRANSPARENT, Color.WHITE, 1,0));

        tableHeadCell.setId(j);

        ImageView imgView = new ImageView(ctx);
        int imageWidth = Level1Util.dip2px(ctx, 15);
        imgView.setVisibility(View.GONE);
        imgView.setAdjustViewBounds(true);// 是否保持图片的纵横比例

        TextView tx = drawTableHeadText(name);
        // 当为数值列时，为了和标题对齐，表头也右对齐、
        if (isAlignWithType) {
            if (bodyTextIsLeft) {
                tableHeadCell.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                tx.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            } else {
                if (tableType[j].startsWith("%") || tableType[j].toUpperCase().equals("INT")) {
                    tableHeadCell.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                    tx.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                } else {
                    tableHeadCell.setGravity(Gravity.CENTER);
                    tx.setGravity(Gravity.CENTER);
                }
            }
        } else {
            tableHeadCell.setGravity(Gravity.CENTER);
            tx.setGravity(Gravity.CENTER);
        }

        TextPaint tp = tx.getPaint();
        tp.setFakeBoldText(headIsBold);
        float textWidth = tp.measureText(name);
        if (textWidth > (width - imageWidth) && imgView.getVisibility() == View.VISIBLE) {
            tx.setWidth(width - imageWidth);
        }

        tableHeadCell.addView(tx);
        tableHeadCell.addView(imgView, imageWidth, imageWidth);

        return tableHeadCell;
    }

    public LinearLayout drawMultiTableHeadCell(String name, int span, int borderColor) {
        LinearLayout tableHeadCell = new LinearLayout(ctx);
        tableHeadCell.setOrientation(LinearLayout.HORIZONTAL);
//		if(!"".equals(name)){
//			tableHeadCell.setBackgroundDrawable(Level1Util.MAIN_RADIO_BORD(ctx, Color.TRANSPARENT,
// borderColor, 1, 0));
//		}
        tableHeadCell.setBackgroundDrawable(Level1Util.MAIN_RADIO_BORD(ctx, Color.TRANSPARENT,
                borderColor, 1, 0));
        tableHeadCell.setGravity(Gravity.CENTER);
        TableRow.LayoutParams tlp = new TableRow.LayoutParams();
        tlp.span = span;// TableRow.LayoutParams可以设置这个属性
        tableHeadCell.setLayoutParams(tlp); // 别忘了把参数设置回去

        TextView titleView = drawTableHeadText(name);
        tableHeadCell.addView(titleView);
        return tableHeadCell;
    }

    public LinearLayout drawMultiTableHeadCell(String name, int span) {
        LinearLayout tableHeadCell = new LinearLayout(ctx);
        tableHeadCell.setOrientation(LinearLayout.HORIZONTAL);
        tableHeadCell.setBackgroundDrawable(Level1Util.MAIN_RADIO_BORD(ctx, Color.TRANSPARENT,
                Color.WHITE, 1, 0));
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
        TextPaint tp = tx.getPaint();
        tp.setFakeBoldText(headIsBold);// 字体加粗
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

    public void setHeadIsBold(boolean headIsBold) {
        this.headIsBold = headIsBold;
    }

    public void setTitleSingle(boolean titleSingle) {
        this.titleSingle = titleSingle;
    }

    public void setBodyTextIsLeft(boolean bodyTextIsLeft) {
        this.bodyTextIsLeft = bodyTextIsLeft;
    }

    public void setAlignWithType(boolean isAlignWithType) {
        this.isAlignWithType = isAlignWithType;
    }
}
