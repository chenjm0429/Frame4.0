package com.ztesoft.level1.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ztesoft.level1.Level1Util;
import com.ztesoft.level1.R;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 按钮组控件
 *
 * @author fanlei@asiainfo-linkage.com  2012-7-30 上午9:59:18
 * @author wanghx2  update by 2013/5/3
 *         支持横排和纵排，---------已支持
 *         支持根据code和位置赋值，支持获取code，name和位置。---------已支持
 *         支持字体大小、颜色，按钮样式，---------已支持
 *         设置一排个数，---------已支持，0不换行
 *         ##按钮宽度自适应、自定义按钮宽度（分别定义和统一定义）---------暂不支持分别定义
 *         支持回调方法，---------已支持
 *         支持单层、多层按钮组---------已支持，最多9层
 * @author chenjianming  update by 2017/5/11
 *         美化控件样式；使用监听器回调取代类反射回调
 */
public class ButtonGroupUI extends LinearLayout {

    private Context context;

    private JSONArray jsonArray;
    private boolean isVertical = false; // true为垂直布局， false为水平布局

    private OnSelectListener mOnSelectListener;

    private int buttonNum = 4;//按钮组每行的按钮个数，超出则换行
    private int buttonFontSize = 16;//字体大小

    private int buttonWidth = 0;//每个按钮的宽度
    private int groupNum = 0;//层数

    private int buttonTextChooseColor = getResources().getColor(R.color.font_white_color);
    private int buttonTextInitColor = getResources().getColor(R.color.blue);

    //水平按钮初始样式
    private int[] btnInitStyles = {R.drawable.button_group_left_normal, R.drawable
            .button_group_mid_normal, R.drawable.button_group_right_normal};
    //水平按钮选中样式
    private int[] btnChooseStyles = {R.drawable.button_group_left_click, R.drawable
            .button_group_mid_click, R.drawable.button_group_right_click};

    //垂直按钮初始样式
    private int[] btnInitStyles_ver = {R.drawable.button_group_left_normal_ver, R.drawable
            .button_group_mid_normal_ver, R.drawable.button_group_right_normal_ver};
    //垂直按钮选中样式
    private int[] btnChooseStyles_ver = {R.drawable.button_group_left_click_ver, R.drawable
            .button_group_mid_click_ver, R.drawable.button_group_right_click_ver};

    private StringBuffer curCodes = new StringBuffer();//当前选中按钮编码，“;”号隔开
    private StringBuffer curNames = new StringBuffer();//当前选中按钮名称，“;”号隔开
    private StringBuffer curPositions = new StringBuffer();//当前选中按钮名称，“;”号隔开

    // 取值变量
    private String code = "code";
    private String name = "name";
    private String arrayName = "btnArray";

    public ButtonGroupUI(Context context) {
        super(context);
        this.context = context;
    }

    public ButtonGroupUI(Context context, String code, String name, String arrayName) {
        super(context);
        this.context = context;
        this.code = code;
        this.name = name;
        this.arrayName = arrayName;
    }

    public void create(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
        if (isVertical) {//纵向居左
            this.setOrientation(LinearLayout.HORIZONTAL);
        } else {//横向居上
            this.setOrientation(LinearLayout.VERTICAL);
        }
        drawLevelLayout(jsonArray);//绘制第一层按钮

        if ("".equals(curPositions)) {
            curPositions.append(";0");//默认选中第一个
        }
        setCurPositions(curPositions);
    }

    /**
     * 绘制单层按钮组
     *
     * @param array 绘制该层按钮需要的JSONArray
     */
    private void drawLevelLayout(JSONArray array) {
        LinearLayout groupBtnLayout = new LinearLayout(context);
        groupBtnLayout.setId(groupNum);
        if (isVertical) {
            groupBtnLayout.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            groupBtnLayout.setOrientation(LinearLayout.VERTICAL);
        }
        if (buttonNum <= 0 || array.length() <= buttonNum) {  //强制不换行||按钮数量小于等于每行指定数
            groupBtnLayout.addView(drawRowLayout(array, 0), LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);

        } else {  //根据buttonNum值，求出需要绘制多少行
            int size = array.length() / buttonNum + 1;
            for (int i = 0; i < size; i++) {  //循环绘制每一行
                JSONArray tempArray = new JSONArray();  //构建单行按钮需要的JSONArray
                for (int j = 0 + buttonNum * i; j < buttonNum * (i + 1) && j < array.length();
                     j++) {
                    tempArray.put(array.optJSONObject(j));
                }

                LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT);
                if (!isVertical && i != 0) {
                    lp.setMargins(0, Level1Util.dip2px(context, 5), 0, 0);
                }

                groupBtnLayout.addView(drawRowLayout(tempArray, i), lp);
            }
        }
        this.addView(groupBtnLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    /**
     * 绘制单行按钮组，多次调用拼成一层按钮组
     *
     * @param array    绘制该行按钮需要的JSONArray
     * @param rowOrder 行号
     * @return
     */
    private LinearLayout drawRowLayout(JSONArray array, int rowOrder) {
        LinearLayout rowLayout = new LinearLayout(context);
        if (isVertical) {
            rowLayout.setOrientation(LinearLayout.VERTICAL);
        } else {
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        }
        rowLayout.setId(rowOrder);

        for (int i = 0; i < array.length(); i++) {//循环绘制每个按钮
            rowLayout.addView(drawButton(array.optJSONObject(i), rowOrder * buttonNum + i));
        }
        return rowLayout;
    }

    /**
     * 绘制单个按钮，多次调用拼成单行按钮组
     *
     * @param obj         绘制按钮需要的JSONObject
     * @param buttonOrder 按钮位置编号，针对所有按钮的位置
     * @return
     */
    private TextView drawButton(JSONObject obj, final int buttonOrder) {
        if (obj == null)
            obj = new JSONObject();
        TextView button = new TextView(context);
        button.setGravity(Gravity.CENTER);
        button.setTextColor(buttonTextInitColor);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, buttonFontSize);
        button.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        button.setSingleLine();
        button.setId(buttonOrder);

        button.setPadding(0, 0, 0, 0);

//        button.setBackgroundResource(btnInitStyle);

        button.setText(obj.optString(name));
        if (buttonWidth == 0 || buttonNum <= 0) {  //宽度未设置或强制不换行时，宽度自适应
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                    .MATCH_PARENT, 1f);
            button.setLayoutParams(lp);
        } else {
            LayoutParams lp = new LayoutParams(buttonWidth, LayoutParams.MATCH_PARENT);
            button.setLayoutParams(lp);
        }
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setChoose(view);

                if (null != mOnSelectListener)
                    mOnSelectListener.onSelected(buttonOrder, getCurCodes(), getCurNames());
            }
        });
        return button;
    }

    /**
     * 拼接curPositions，并删除所在层以下的所有层
     *
     * @param view 点击按钮
     */
    private void setChoose(View view) {
        LinearLayout layout = (LinearLayout) view.getParent().getParent();//得到当层按钮组
        try {
            curPositions.delete(2 * (layout.getId()), curPositions.length());
            curPositions.append(";" + view.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        int tmp = this.getChildCount();
        for (int k = layout.getId() + 1; k < tmp; k++) {//删除点击当前层以下的所有层
            this.removeViewAt(layout.getId() + 1);
            groupNum--;
        }
        setCurPositions(curPositions);
    }

    /**
     * 设置当前选中按钮组
     *
     * @param curPositions 选中按钮位置，分号隔开，如：;0;1;2
     */
    private void setCurPositions(StringBuffer curPositions) {
        this.curPositions = curPositions;
        curCodes = new StringBuffer();//以防万一，进行初始化，在下个方法中重新赋值
        curNames = new StringBuffer();
        drawButton(0, jsonArray);
    }

    /**
     * 根据curPositions绘制每一层按钮
     *
     * @param i     循环次数，默认传入0，内部循环调用
     * @param array json数组，默认传入jsonArray，内部循环调用
     */
    private void drawButton(int i, JSONArray array) {
        int tt;
        try {
            String s = curPositions.substring(2 * i + 1, 2 * (i + 1));
            tt = Integer.parseInt(s);
        } catch (Exception e) {  //选中按钮可能有下一层，需要绘制，这时curPositions的长度不够，故捕获错误，默认第一个
            tt = 0;
            curPositions.append(";" + tt);
        }
        LinearLayout ll = (LinearLayout) this.getChildAt(i);//获取当前层
        for (int j = 0; j < ll.getChildCount(); j++) {//将该层全部置为未选中
            LinearLayout row = (LinearLayout) ll.getChildAt(j);
            for (int k = 0; k < row.getChildCount(); k++) {
                TextView b = (TextView) row.getChildAt(k);

                if (!isVertical) {
                    if (k == 0) {
                        b.setBackgroundResource(btnInitStyles[0]);
                    } else if (k == row.getChildCount() - 1) {
                        b.setBackgroundResource(btnInitStyles[2]);
                    } else {
                        b.setBackgroundResource(btnInitStyles[1]);
                    }
                } else {
                    if (k == 0) {
                        b.setBackgroundResource(btnInitStyles_ver[0]);
                    } else if (k == row.getChildCount() - 1) {
                        b.setBackgroundResource(btnInitStyles_ver[2]);
                    } else {
                        b.setBackgroundResource(btnInitStyles_ver[1]);
                    }
                }

                b.setTextColor(buttonTextInitColor);
            }
        }
        int rowOrder = 0, buttonOrder = tt;
        if (buttonNum > 0) {  //当buttonNum=0表示不换行，不需要换算
            rowOrder = tt / buttonNum;
            buttonOrder = tt % buttonNum;
        }
        LinearLayout row = (LinearLayout) ll.getChildAt(rowOrder);//取到按钮所在的行
        JSONObject obj = array.optJSONObject(tt);
        if (obj == null)
            obj = new JSONObject();
        TextView b = (TextView) row.getChildAt(buttonOrder);

        //将该层指定按钮置为选中
        if (!isVertical) {
            if (buttonOrder == 0) {
                b.setBackgroundResource(btnChooseStyles[0]);
            } else if (buttonOrder == row.getChildCount() - 1) {
                b.setBackgroundResource(btnChooseStyles[2]);
            } else {
                b.setBackgroundResource(btnChooseStyles[1]);
            }
        } else {
            if (buttonOrder == 0) {
                b.setBackgroundResource(btnChooseStyles_ver[0]);
            } else if (buttonOrder == row.getChildCount() - 1) {
                b.setBackgroundResource(btnChooseStyles_ver[2]);
            } else {
                b.setBackgroundResource(btnChooseStyles_ver[1]);
            }
        }
        b.setTextColor(buttonTextChooseColor);

        curCodes.append(";" + obj.optString(code));
        curNames.append(";" + b.getText());  //拼接curNames
        i++;
        JSONArray tempArray = obj.optJSONArray(arrayName);
        if (tempArray != null && tempArray.length() != 0) {  //如果存在下一层
            if (groupNum < i) {  //当实际层数小于需要层数时，表示需要重绘
                groupNum++;
                drawLevelLayout(tempArray);
            }
            drawButton(i, tempArray);
        }
    }

    /**
     * 设置横向/纵向布局
     *
     * @param isVertical true为垂直布局， false为水平布局
     */
    public void setVertical(boolean isVertical) {
        this.isVertical = isVertical;
    }

    /**
     * 设置一行有多少按钮
     *
     * @param buttonNum 0表示强制不换行,此时setButtonWidth无效
     */
    public void setButtonNum(int buttonNum) {
        if (buttonNum < 0)
            buttonNum = 0;
        this.buttonNum = buttonNum;
    }

    /**
     * 设置每个按钮的宽度，单位为DP
     *
     * @param btnWidth
     */
    public void setButtonWidth(int btnWidth) {
        this.buttonWidth = Level1Util.getDipSize(btnWidth);
    }

    /**
     * 设置字体大小，单位为SP
     *
     * @param buttonFontSize
     */
    public void setButtonFontSize(int buttonFontSize) {
        this.buttonFontSize = buttonFontSize;
    }

    /**
     * 设置默认字体颜色
     *
     * @param buttonTextInitColor
     */
    public void setButtonTextInitColor(int buttonTextInitColor) {
        this.buttonTextInitColor = buttonTextInitColor;
    }

    /**
     * 设置选中字体颜色
     *
     * @param buttonTextChooseColor
     */
    public void setButtonTextChooseColor(int buttonTextChooseColor) {
        this.buttonTextChooseColor = buttonTextChooseColor;
    }

    /**
     * 设置水平按钮样式
     *
     * @param btnInitStyles   初始样式
     * @param btnChooseStyles 选中样式
     */
    public void setBtnStyles(int[] btnInitStyles, int[] btnChooseStyles) {
        this.btnInitStyles = btnInitStyles;
        this.btnChooseStyles = btnChooseStyles;
    }

    /**
     * 设置垂直按钮样式
     *
     * @param btnInitStyles_ver   初始样式
     * @param btnChooseStyles_ver 选中样式
     */
    public void setBtnStyles_ver(int[] btnInitStyles_ver, int[] btnChooseStyles_ver) {
        this.btnInitStyles_ver = btnInitStyles_ver;
        this.btnChooseStyles_ver = btnChooseStyles_ver;
    }

    /**
     * 获取当前选中按钮组名称
     *
     * @return 分号隔开，如：测试1;测试11
     */
    public String getCurNames() {
        String tmp = curNames.substring(1, curNames.length());
        return tmp;
    }

    public String getCurCodes() {
        String tmp = curCodes.substring(1, curCodes.length());
        return tmp;
    }

    public void setCurCodes(String curCodes) {
        //获取对应的curPositions
        JSONArray array = jsonArray;
        StringBuffer n = new StringBuffer();
        String[] s = curCodes.split(";");
        int[] k = new int[s.length];
        for (int j = 0; j < s.length; j++) {
            if (j > 0) {
                array = array.optJSONObject(k[j - 1]).optJSONArray(arrayName);
            }
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.optJSONObject(i);
                if (obj.optString(code, "").equals(s[j])) {
                    n.append(";" + i);
                    k[j] = i;
                    break;
                }
            }
        }
        int tmp = this.getChildCount();
        for (int kk = 1; kk < tmp; kk++) {  //删除第一层以下的所有层
            this.removeViewAt(1);
            groupNum--;
        }
        setCurPositions(n);
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.mOnSelectListener = onSelectListener;
    }

    public interface OnSelectListener {
        void onSelected(int position, String currentCode, String currentName);
    }
}
