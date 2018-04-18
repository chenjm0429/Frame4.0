package com.ztesoft.level1.table;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ztesoft.level1.Level1Util;
import com.ztesoft.level1.R;
import com.ztesoft.level1.table.util.MyScrollView;
import com.ztesoft.level1.table.util.SyncHScrollView;
import com.ztesoft.level1.table.util.TableStyleUtil;
import com.ztesoft.level1.util.BitmapOperateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.ztesoft.level1.table.util.MyScrollView.OnBorderListener;

/**
 * 左右滑动表格控件 ###########表格整体截图----大图片会报错
 * ###########如支持分页，需要手动滑动展开其他页，才能截取。如不支持分页，大数据量表格展现速度慢
 * ###########设置行高后，上下滑动锚点失效
 *
 * @author wanghx2
 */
public class ScrollListView extends LinearLayout {

    private int allWidth = 0;
    private int allHeight = 0;
    // 多表头配置
    private String[][] tableMulTitle = null;
    private int[][] tableMulSpan = null;
    private int[][] tableMulWidth = null;
    // 最下级表头配置
    private String[] columnNames;// 表头名称数组
    private int[] columnWidths;// 列宽度数组
    private String[] columnTypes;

    private int rowCount = 0;// 表格行数
    private int columnCount = 0;// 表格列数
    // 左侧锁定表体（固定部分）
    private TableLayout leftTabLayout;
    // 右侧滑动表体（滑动部分）
    private TableLayout rightTabLayout;
    // 左侧锁定表头（固定部分）
    private TableLayout leftTableHead;
    // 右侧滑动表头（滑动部分）
    private TableLayout rightTableHead;

    private MyScrollView vScrollView = null;
    private int pagerSize = -1;// 一页最多显示条数,-1表示不分页,默认-1
    private int currentPager = 1;// 当前页
    private int totalPager = 1;// 所有页数
    private boolean isLoading = false;// 事件加载锁
    private int mScrollX = 0;// 左右滑动偏移量
    private int mScrollY = 0;// 上下滑动偏移量

    private Context ctx;
    private int displayWidth = -1;// 显示宽度
    private JSONArray tabArray;
    private JSONArray tableHead;
    private JSONArray multiHead;

    // 宽度设定的是否是百分比
    private boolean isPercent = true;
    // 前X列需要锁定，-1表示不锁定列
    private int fixColumnIndex = -1;

    private OnClickListener rowListener;// 行事件
    private OnClickListener cellListener;// 单元格事件
    private OnLongClickListener longListener;
    private OnClickListener clickListener;
    private boolean titleSingle = false;// 表头是否强制不换行
    // 表头字体
    private int headFontSize = 15;
    private int headTextColor = Color.WHITE;// 表头文本颜色
    private int headBGColor = Color.BLUE;// 表头背景色，覆盖在headBgImage上面
    private boolean headIsBold = true; // 表头是否加粗，默认加粗
    // 表体字体大小
    private int bodyFontSize = 14;
    private int bodyTextColor = Color.BLACK;// 表体文字颜色
    private int tableBGColor = Color.WHITE;// 整体背景色

    private boolean bodyTextIsLeft = false; // 表格内容居左，默认居中

    // 背景交替渲染"#00000000,#55555555"
    private String bgExpression = "#00000000,#55555555";
    private int rowHeight = -1;

    // 哪些列后面需要插入分割线
    private int[] splitCols = new int[]{};
    // 列分割线的颜色
    private int splitColColor = Color.BLACK;
    // 列分割线的宽度
    private int splitColWidth = 1;

    // 表头分割线的颜色
    private int borderColor = Color.WHITE;

    //
    private boolean hasSerialCell = false;

    private boolean sortFlag = true;// 是否支持表头排序，true:支持
    // 不参与排序的行：1:第一行 99:最后一行 0:全参与
    private int noOrderRow = 0;
    private JSONObject noOrderJSON = null;// 不参与排序行的JSON
    // 排序标示,升序为true,降序为false
    private boolean ASC = true;

    private String methodName = null;
    // 加载监听的事件
    private OnTableLoadListener onTableLoadListener;

    public View parentScrollView;// 谨慎使用,只是在有2个scrollView嵌套的时候使用

    private boolean isAlignWithType = true; // 是否按照type设置字段的对齐方式

    public void setParentScrollView(View parentScrollView) {
        this.parentScrollView = parentScrollView;
    }

    public void setOnTableLoadListener(OnTableLoadListener onTableLoadListener) {
        this.onTableLoadListener = onTableLoadListener;
    }

    /**
     * 表格控件构造函数
     *
     * @param context
     */
    public ScrollListView(Context context) {
        super(context);
        this.ctx = context;
        mScrollX = 0;
        mScrollY = 0;
    }

    /**
     * 表头JSONObject，必须含有tableHead，可以含有multiHead
     *
     * @param headObj "{" + "\"multiHead\":[" +
     *                "[{\"name\":\"地市\",\"colspan\":\"1\"}," +
     *                "{\"name\":\"当日\",\"colspan\":\"4\"}" + "]]," +
     *                "\"tableHead\":[" +
     *                "{\"name\":\"地市\",\"width\":\"44\",\"type\":\"\"}," +
     *                "{\"name\":\"当日\",\"width\":\"44\",\"type\":\"int\"}," +
     *                "{\"name\":\"当月\",\"width\":\"20\",\"type\":\"int\"}," +
     *                "{\"name\":\"同比\",\"width\":\"44\",\"type\":\"%\"}," +
     *                "{\"name\":\"环比\",\"width\":\"64\",\"type\":\"%\"}" + "]," +
     *                "}"; type 含义： DAY 日格式10-10 MONTH 月格式2014-10 INT
     *                千分位(右对齐),空值显示-- % 百分号(右对齐),空值显示-- 空 不处理
     */
    public void setTableHeadJSON(JSONObject headObj) {
        tableHead = headObj.optJSONArray("tableHead");
        multiHead = headObj.optJSONArray("multiHead");
        if (multiHead == null)
            multiHead = new JSONArray();
    }

    /**
     * 表体JSONArray
     *
     * @param tableArray "[" +
     *                   "{\"v1\":\"全省\",\"c1\":\"0\",\"o1\":\"1\",\"v2\":\"3333\",\"v3\":\"33\",
     *                   \"v4\":\"33\",\"v5\":\"33\"},"
     *                   +
     *                   "{\"v1\":\"南京\",\"c1\":\"3\",\"o1\":\"3\",\"v2\":\"3333\",\"v3\":\"33\",
     *                   \"v4\":\"-33\",\"v5\":\"-33\"},"
     *                   +
     *                   "{\"v1\":\"苏州\",\"c1\":\"20\",\"o1\":\"2\",\"v2\":\"3333\",\"v3\":\"33\",
     *                   \"v4\":\"33\",\"v5\":\"33\"}"
     *                   + "]"; 数字对应tableHead的列，v表示显示值，o表示排序值，c表示实际值
     */
    public void setTableBodyJSON(JSONArray tableArray) {
        tabArray = tableArray;
    }

    /**
     * 转换为包含序号列的数据源
     */
    private JSONObject convertSerialData(JSONArray bodyArray, JSONArray headArray, JSONArray
            multiHeadArray) throws JSONException {
        JSONObject afterJson = new JSONObject();
        if (multiHeadArray.length() > 0) {
            for (int j = 0; j < multiHeadArray.length(); j++) {
                JSONArray tmpArray = multiHeadArray.getJSONArray(j);
                while (true) {
                    Object obj = tmpArray.get(0);
                    if (obj instanceof JSONObject) {
                        ArrayList<JSONObject> jsons = new ArrayList<JSONObject>();
                        for (int i = 0; i < tmpArray.length(); i++) {
                            jsons.add(tmpArray.getJSONObject(i));
                        }
                        JSONObject firstJson = new JSONObject();
                        firstJson.put("name", "");
                        firstJson.put("colspan", "1");
                        tmpArray.put(0, firstJson);
                        for (int i = 0; i < jsons.size(); i++) {
                            tmpArray.put(i + 1, jsons.get(i));
                        }
                        break;
                    }
                    tmpArray = (JSONArray) obj;
                }

            }
            afterJson.put("multiHead", multiHeadArray);
        } else {
            afterJson.put("multiHead", multiHeadArray);
        }
        int actualHeadCount = headArray.length();
        if (headArray.length() > 0) {
            ArrayList<JSONObject> jsons = new ArrayList<JSONObject>();
            for (int i = 0; i < headArray.length(); i++) {
                jsons.add(headArray.getJSONObject(i));
            }
            JSONObject firstJson = new JSONObject();
            firstJson.put("name", "序号");
            firstJson.put("width", "10");
            firstJson.put("type", "");
            headArray.put(0, firstJson);
            for (int i = 0; i < jsons.size(); i++) {
                headArray.put(i + 1, jsons.get(i));
            }
            afterJson.put("tableHead", headArray);
        } else {
            afterJson.put("tableHead", headArray);
        }
        for (int i = 0; i < bodyArray.length(); i++) {
            JSONObject beforeJson = bodyArray.getJSONObject(i);
            JSONObject newJson = new JSONObject();
            newJson.put("v1", i + 1);
            for (int j = 1; j < actualHeadCount + 1; j++) {
                if (beforeJson.has("v" + j)) {
                    newJson.put("v" + (j + 1), beforeJson.get("v" + j));
                }
                if (beforeJson.has("o" + j)) {
                    newJson.put("o" + (j + 1), beforeJson.get("o" + j));
                }
                if (beforeJson.has("c" + j)) {
                    newJson.put("c" + (j + 1), beforeJson.get("c" + j));
                }
            }
            bodyArray.put(i, newJson);
        }
        afterJson.put("tableArray", bodyArray);
        return afterJson;
    }

    /**
     * 绘制表格
     *
     * @param width 控件实际显示宽度，具体像素
     */
    public void create(int width) {
        if (hasSerialCell) {
            if (fixColumnIndex != -1) {
                fixColumnIndex++;
            }
            try {
                JSONObject newData = convertSerialData(tabArray, tableHead, multiHead);
                multiHead = newData.getJSONArray("multiHead");
                tableHead = newData.getJSONArray("tableHead");
                tabArray = newData.getJSONArray("tableArray");
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }
        this.displayWidth = width;
        this.removeAllViews();
        this.rowCount = tabArray.length();
        this.columnCount = tableHead.length();

        this.columnWidths = new int[columnCount];
        this.columnNames = new String[columnCount];
        this.columnTypes = new String[columnCount];
        for (int i = 0; i < columnCount; i++) {
            JSONObject headObj = tableHead.optJSONObject(i);
            this.columnTypes[i] = headObj.optString("type");
            this.columnWidths[i] = headObj.optInt("width");
            this.columnNames[i] = headObj.optString("name");
        }
        // 有多表头配置
        if (multiHead.length() > 0) {
            tableMulTitle = new String[multiHead.length()][columnCount];
            tableMulSpan = new int[multiHead.length()][columnCount];
            JSONArray tempArray = null;
            for (int i = 0; i < multiHead.length(); i++) {
                tempArray = multiHead.optJSONArray(i);
                for (int x = 0; x < tempArray.length(); x++) {
                    tableMulTitle[i][x] = tempArray.optJSONObject(x).optString("name");
                    tableMulSpan[i][x] = tempArray.optJSONObject(x).optInt("colspan", 1);
                }
            }
        }

        if (pagerSize != -1) {// 处理分页
            totalPager = rowCount / pagerSize;
            if (rowCount % pagerSize > 0) {
                totalPager = totalPager + 1;
            }
        }
        if (fixColumnIndex > columnCount - 2) {// 锁定列必须小于总列数
            fixColumnIndex = -1;
        }
        if (fixColumnIndex > -1 && tableMulSpan != null) {// 判断锁定列和多表头跨列是否冲突
            for (int j = 0; j < tableMulSpan.length; j++) {
                int tempNum = -1;
                for (int i = 0; i < tableMulSpan[j].length; i++) {
                    tempNum += tableMulSpan[j][i];
                    if (fixColumnIndex > tempNum) {
                        continue;
                    } else if (fixColumnIndex == tempNum) {
                        break;
                    } else {
                        Toast.makeText(ctx, R.string.error_tableMultiHead, Toast.LENGTH_SHORT)
                                .show();
                        this.addView(new LinearLayout(ctx), LayoutParams.MATCH_PARENT,
                                LayoutParams.WRAP_CONTENT);
                        return;
                    }
                }
            }
        }

        // 日期类型的默认倒序
        if ("DAY".equalsIgnoreCase(columnTypes[0]) || "MONTH".equalsIgnoreCase(columnTypes[0])) {
            this.ASC = false;
        }
        reColumColsWidth();
        if (sortFlag && tabArray.length() > 1) {// 支持排序并且超过1条
            if (noOrderRow == 1) {
                noOrderJSON = tabArray.optJSONObject(0);
            } else if (noOrderRow == 99) {
                noOrderJSON = tabArray.optJSONObject(tabArray.length() - 1);
            }
        }
        buidScrollListView();
    }

    /**
     * 重新计算表格宽度
     */
    private void reColumColsWidth() {
        if (fixColumnIndex > -1 && isPercent) {// 锁定且百分比。按照显示宽度×百分比计算
            int totalWidth = 0;
            for (int i = 0; i < columnCount; i++) {
                totalWidth += this.columnWidths[i];
            }
            if (totalWidth < 100) {
                for (int i = 0; i < columnCount; i++) {
                    this.columnWidths[i] = (int) (this.columnWidths[i] * 100 / totalWidth);
                }
            }
            for (int i = 0; i < columnCount; i++) {
                this.columnWidths[i] = (int) (this.columnWidths[i] / 100.00 * displayWidth);
            }
        } else if (fixColumnIndex > -1 && !isPercent) {
            for (int i = 0; i < columnCount; i++) {
                this.columnWidths[i] = Level1Util.getDipSize(this.columnWidths[i]);
            }
        } else if (isPercent) {// 不锁定且百分比。强制全部展现。计算列宽占比×显示宽度
            int totalWidth = 0;
            for (int i = 0; i < columnCount; i++) {
                totalWidth += this.columnWidths[i];
            }
            for (int i = 0; i < columnCount; i++) {
                this.columnWidths[i] = (int) (this.columnWidths[i] * 1.00 / totalWidth *
                        displayWidth);
            }
        }
        for (int i = 0; i < columnWidths.length; i++) {
            allWidth += columnWidths[i];
        }
        if (tableMulSpan != null) {// 存在多表头
            this.tableMulWidth = new int[tableMulSpan.length][tableMulSpan[0].length];
            for (int i = 0; i < tableMulSpan.length; i++) {// 遍历多行表头
                int aculIndex = -1;
                for (int x = 0; x < tableMulSpan[i].length; x++) {// 遍历每列
                    aculIndex = aculIndex + tableMulSpan[i][x];
                    if (tableMulSpan[i][x] == 1) {// 如果只占一列，则将对应列的宽度赋给它
                        if (aculIndex >= columnCount)
                            break;
                        tableMulWidth[i][x] = columnWidths[aculIndex];
                    } else {
                        for (int j = tableMulSpan[i][x] - 1; j >= 0; j--) {
                            if (aculIndex - j >= columnCount)
                                break;
                            tableMulWidth[i][x] += columnWidths[aculIndex - j];
                        }
                    }
                }
            }
        }
    }

    /**
     * 构造表格整体布局
     */
    private void buidScrollListView() {
        this.setOrientation(LinearLayout.VERTICAL);
        this.setBackgroundColor(tableBGColor);
        leftTabLayout = new TableLayout(ctx);
        leftTabLayout.setBackgroundColor(tableBGColor);
        leftTabLayout.setId(R.id.table_left);
        rightTabLayout = new TableLayout(ctx);
        rightTabLayout.setBackgroundColor(tableBGColor);
        rightTabLayout.setId(R.id.table_right);
        this.addView(buildHeadLayout(), LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        this.addView(createScrollTable(), LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     * 构造表头布局（固定表头+滑动表头）
     *
     * @return
     */
    private View buildHeadLayout() {
        LinearLayout relativeLayout = new LinearLayout(ctx);
        relativeLayout.setGravity(Gravity.CENTER_VERTICAL);

        leftTableHead = new TableLayout(ctx);
        leftTableHead.setPadding(0, 15, 0, 15);
        leftTableHead.setGravity(Gravity.CENTER_VERTICAL);
        rightTableHead = new TableLayout(ctx);
        rightTableHead.setPadding(0, 15, 0, 15);
        rightTableHead.setGravity(Gravity.CENTER_VERTICAL);
        if (headBGColor != -1) {
            leftTableHead.setBackgroundColor(headBGColor);
            rightTableHead.setBackgroundColor(headBGColor);
        }
        createTableHead();

        if (fixColumnIndex > -1) {
            relativeLayout.addView(leftTableHead, LayoutParams.WRAP_CONTENT, LayoutParams
                    .WRAP_CONTENT);
        }
        HorizontalScrollView hsv = new HorizontalScrollView(ctx) {
            @Override
            public boolean arrowScroll(int direction) {
                return super.arrowScroll(direction);
            }

            @Override
            public boolean onTouchEvent(MotionEvent ev) {
                return false;
            }
        };
        hsv.setHorizontalFadingEdgeEnabled(false);
        hsv.setHorizontalScrollBarEnabled(false);
        hsv.addView(rightTableHead);
        relativeLayout.addView(hsv, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        return relativeLayout;
    }

    /**
     * 构造表头布局
     */
    private void createTableHead() {
        TableStyleUtil tableStyle = new TableStyleUtil(ctx, columnTypes);
        tableStyle.setHeadTextColor(headTextColor);
        tableStyle.setAlignWithType(isAlignWithType);
        tableStyle.setHeadTextSize(headFontSize);
        tableStyle.setHeadIsBold(headIsBold);
        tableStyle.setBodyTextIsLeft(bodyTextIsLeft);
        tableStyle.setTitleSingle(titleSingle);
        if (tableMulTitle != null) {// 绘制多表头
            for (int x = 0; x < tableMulTitle.length; x++) {// 遍历多表头
                TableRow ltr = new TableRow(ctx);
                TableRow rtr = new TableRow(ctx);
                int curIndex = 0;
                int allWidth = 0;
                for (int y = 0; y < tableMulTitle[x].length; y++) {
                    if (tableMulTitle[x][y] == null)// 多出的列
                        break;

                    LinearLayout titleView = tableStyle.drawMultiTableHeadCell
                            (tableMulTitle[x][y], tableMulSpan[x][y], borderColor);
                    curIndex += tableMulSpan[x][y];
                    if (curIndex > fixColumnIndex + 1) {// 右侧
                        rtr.addView(titleView);
                        allWidth += tableMulWidth[x][y];
                    } else {// 左侧
                        ltr.addView(titleView);
                    }
                }
                alignTableHeadRow(ltr, rtr, allWidth);
            }
        }
        int allWidth = 0;
        TableRow ltr = new TableRow(ctx);
        TableRow rtr = new TableRow(ctx);
        for (int i = 0; i < columnCount; i++) {
            LinearLayout fixHeadLayout = tableStyle.drawTableHeadCell(i, columnNames[i],
                    columnWidths[i]);
            fixHeadLayout.setBackgroundDrawable(Level1Util.MAIN_RADIO_BORD(ctx, Color
                    .TRANSPARENT, borderColor, 1, 0));

            if (i < fixColumnIndex + 1) {
                if (sortFlag)
                    fixHeadLayout.setOnClickListener(new titleClick("left"));
                ltr.addView(fixHeadLayout, columnWidths[i], LinearLayout.LayoutParams.MATCH_PARENT);
            } else {
                if (sortFlag)
                    fixHeadLayout.setOnClickListener(new titleClick("right"));
                rtr.addView(fixHeadLayout, columnWidths[i], LinearLayout.LayoutParams.MATCH_PARENT);
                allWidth += columnWidths[i];
            }
        }
        alignTableHeadRow(ltr, rtr, allWidth);
    }

    /**
     * 左右行高对齐
     *
     * @param ltr   左侧行
     * @param rtr   右侧行
     * @param width 右侧行宽度
     */
    private void alignTableHeadRow(final TableRow ltr, final TableRow rtr, int width) {
        leftTableHead.addView(ltr);
        if (null != tableMulTitle) {
            width += (columnCount * 2);
        }
        if (fixColumnIndex > -1) {
            // 左右滑动必须要设置宽度，不然被遮挡的部分不绘制
            rightTableHead.addView(rtr, width, LinearLayout.LayoutParams.WRAP_CONTENT);
        } else {
            rightTableHead.addView(rtr);
        }

        // 修改行高，并保证左右滑动表格行高对齐
        ViewTreeObserver vto2 = rtr.getViewTreeObserver();
        vto2.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                rtr.getViewTreeObserver().removeOnPreDrawListener(this);
                int leftH = ltr.getHeight();
                int rightH = rtr.getHeight();
                if (leftH > rightH) {
                    setTableRowHeight(rtr, leftH);
                } else {
                    setTableRowHeight(ltr, rightH);
                }
                return false;
            }
        });
    }

    /**
     * 生成滑动表格（固定部分+滑动部分） 自定义的ScrollView和HorizontalScrollView嵌套组成
     *
     * @return
     */
    private View createScrollTable() {
        LinearLayout tabLayout = new LinearLayout(ctx);
        leftTabLayout.removeAllViews();
        rightTabLayout.removeAllViews();
        int rr = rowCount;
        if (pagerSize != -1) {
            rr = rowCount < pagerSize ? rowCount : pagerSize;
        }
        drawTableBody(0, rr);
        // 添加列分割线
        if (fixColumnIndex > -1) {
            RelativeLayout fl = new RelativeLayout(ctx);// 左侧加上列分割线
            fl.addView(leftTabLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            int fixColumnWidth = 0;
            for (int i = 0; i < fixColumnIndex + 1; i++) {
                fixColumnWidth += columnWidths[i];
            }
            if (splitCols.length > 0) {
                LinearLayout l = creatSplitCols(1);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(new
                        LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                params.addRule(RelativeLayout.ALIGN_BOTTOM, leftTabLayout.getId());
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                l.setLayoutParams(params);
                fl.addView(l);
            }
            tabLayout.addView(fl, fixColumnWidth, LayoutParams.WRAP_CONTENT);
        }
        RelativeLayout fl = new RelativeLayout(ctx);// 右侧滑动加上列分割线
        fl.addView(rightTabLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        if (splitCols.length > 0) {
            LinearLayout l = creatSplitCols(2);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(new LayoutParams
                    (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            params.addRule(RelativeLayout.ALIGN_BOTTOM, rightTabLayout.getId());
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            l.setLayoutParams(params);
            fl.addView(l);
        }
        vScrollView = new MyScrollView(ctx);
        // 添加到表体总布局tabLayout
        if (fixColumnIndex == -1) {
            tabLayout.addView(fl, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        } else {
            final SyncHScrollView hScrollView = new SyncHScrollView(ctx);
            hScrollView.setHorizontalScrollBarEnabled(false);
            hScrollView.setHorizontalFadingEdgeEnabled(false);
            hScrollView.addView(fl, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            // 绑定可滑动表头,保证左右同步滑动
            hScrollView.setScrollView((HorizontalScrollView) rightTableHead.getParent());// 
            hScrollView.post(new Runnable() {
                public void run() {
                    hScrollView.scrollTo(mScrollX, 0);
                }
            });
            tabLayout.addView(hScrollView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        // 设置上下滑动
        vScrollView.addView(tabLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        vScrollView.setOnBorderListener(new OnBorderListener() {
            @Override
            public void onTop() {

            }

            @Override
            public void onTopY(ScrollView scv) {
            }

            @Override
            public void onBottom() {
                if (currentPager < totalPager && !isLoading) {
                    isLoading = true;
                    int s = currentPager * pagerSize;
                    int e = (currentPager + 1) * pagerSize;
                    if (e > rowCount) {
                        e = rowCount;
                    }
                    drawTableBody(s, e);
                    currentPager++;
                }
            }
        });
        if (parentScrollView != null) {
            vScrollView.setParentScrollView(parentScrollView);
        }
        vScrollView.post(new Runnable() {
            public void run() {
                vScrollView.scrollTo(0, mScrollY);
            }
        });
        return vScrollView;
    }

    /**
     * 创建分割线
     *
     * @param type 表示分割线在左侧还是右侧表体
     * @return
     */
    private LinearLayout creatSplitCols(int type) {
        LinearLayout ll = new LinearLayout(ctx);
        for (int i = 0; i < columnCount; i++) {
            if (type == 1) {// 左侧固定
                if (i < fixColumnIndex + 1) {// i小于锁定列
                    drawSplitCol(ll, i);
                } else {
                    break;
                }
            } else if (type == 2) {// 右侧滑动
                if (i > fixColumnIndex && i != columnCount - 1) {// 最后一列不加分割线
                    drawSplitCol(ll, i);
                } else {
                    continue;
                }
            }
        }
        return ll;
    }

    /**
     * 绘制单列分割线
     *
     * @param ll 总分割线布局
     * @param i  所在列
     */
    private void drawSplitCol(LinearLayout ll, int i) {
        View v = new View(ctx);
        v.setBackgroundColor(Color.TRANSPARENT);
        View splitV = null;
        for (int j = 0; j < splitCols.length; j++) {
            if (i == splitCols[j]) {
                splitV = new View(ctx);
                splitV.setBackgroundColor(splitColColor);
            }
        }
        if (splitV != null) {
            ll.addView(v, (columnWidths[i] - splitColWidth), LayoutParams.MATCH_PARENT);
            ll.addView(splitV, splitColWidth, LayoutParams.MATCH_PARENT);
        } else {
            ll.addView(v, columnWidths[i], LayoutParams.MATCH_PARENT);
        }
    }

    /**
     * 绘制表体
     *
     * @param startIndex 起始行
     * @param endIndex   结束行
     */
    private void drawTableBody(int startIndex, int endIndex) {
        for (int j = startIndex; j < endIndex; j++) {
            JSONObject itemObj = tabArray.optJSONObject(j);

            final TableRow leftTabRow = new TableRow(ctx);
            leftTabRow.setGravity(Gravity.CENTER_VERTICAL);
            final TableRow rightTabRow = new TableRow(ctx) {
                @Override
                public boolean onTouchEvent(MotionEvent ev) {
                    switch (ev.getAction()) {
                        case MotionEvent.ACTION_DOWN:// 当向下按时
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            break;
                    }
                    boolean flag1 = super.onTouchEvent(ev);
                    return flag1;
                }
            };
            rightTabRow.setGravity(Gravity.CENTER_VERTICAL);
            leftTabLayout.addView(leftTabRow);
            rightTabLayout.addView(rightTabRow);

            // long tt = System.currentTimeMillis();
            for (int i = 0; i < columnCount; i++) {
                TableStyleUtil tableStyle = new TableStyleUtil(ctx, columnTypes);
                tableStyle.setAlignWithType(isAlignWithType);
                tableStyle.setBodyTextSize(bodyFontSize);
                tableStyle.setBodyTextColor(bodyTextColor);
                tableStyle.setBodyTextIsLeft(bodyTextIsLeft);
                final LinearLayout tdLayout = tableStyle.drawTableBodyCell(i, itemObj);
                if (cellListener != null)
                    tdLayout.setOnClickListener(cellListener);

                if (0 == columnWidths[i]) {
                    TextView v = (TextView) tdLayout.getChildAt(0);
                    v.setText("");
                }

                // 先自适应，后续修改行高
                if (i < fixColumnIndex + 1) {// 将锁定列单元格放入leftTabRow
                    leftTabRow.addView(tdLayout, columnWidths[i], LayoutParams.MATCH_PARENT);
                } else {// 将滑动列单元格放入rightTabRow
                    if (i == columnCount - 1) {
                        if (null != tableMulTitle) {
                            rightTabRow.addView(tdLayout, columnWidths[i] + (columnCount * 2),
                                    LayoutParams.MATCH_PARENT);
                        } else {
                            rightTabRow.addView(tdLayout, columnWidths[i], LayoutParams
                                    .MATCH_PARENT);
                        }

                    } else {
                        rightTabRow.addView(tdLayout, columnWidths[i], LayoutParams.MATCH_PARENT);
                    }
                }
            }

            leftTabRow.setTag(itemObj);
            rightTabRow.setTag(itemObj);
            if (rowListener != null) {
                leftTabRow.setOnClickListener(rowListener);
                rightTabRow.setOnClickListener(rowListener);
            }
            if (longListener != null) {
                leftTabRow.setOnLongClickListener(longListener);
                rightTabRow.setOnLongClickListener(longListener);
            }
            if (clickListener != null) {
                leftTabRow.setOnClickListener(clickListener);
                rightTabRow.setOnClickListener(clickListener);
            }

            if (bgExpression != null) {// 背景间隔色
                String color[] = bgExpression.split(",");
                if (color.length > 0) {
                    leftTabRow.setBackgroundColor(Color.parseColor(color[j % color.length]));
                    rightTabRow.setBackgroundColor(Color.parseColor(color[j % color.length]));
                }
            }

            // 修改行高，并保证左右滑动表格行高对齐
            ViewTreeObserver vto2 = rightTabRow.getViewTreeObserver();
            vto2.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    rightTabRow.getViewTreeObserver().removeOnPreDrawListener(
                            this);
                    int leftH = leftTabRow.getHeight();
                    int rightH = rightTabRow.getHeight();
                    if (leftH > rightH) {
                        if (leftH < rowHeight) {// 小于规定行高，则左右全部设置为规定行高
                            setTableRowHeight(leftTabRow, rowHeight);
                            setTableRowHeight(rightTabRow, rowHeight);
                            allHeight += rowHeight;
                        } else {// 否则右边设置为左边行高
                            setTableRowHeight(rightTabRow, leftH);
                            allHeight += leftH;
                        }
                    } else {
                        if (rightH < rowHeight) {// 小于规定行高，则左右全部设置为规定行高
                            setTableRowHeight(leftTabRow, rowHeight);
                            setTableRowHeight(rightTabRow, rowHeight);
                            allHeight += rowHeight;
                        } else if (rightH != leftH) {// 大于规定行高，且左右不相等。左边设置为右边行高
                            setTableRowHeight(leftTabRow, rightH);
                            allHeight += rightH;
                        } else {
                            allHeight += rightH;
                        }

                    }
                    return false;
                }

            });
        }

        if (methodName != null && methodName.trim().length() > 0) {
            try {
                Class<?> yourClass = Class.forName(ctx.getClass().getName());
                Method method = yourClass.getMethod(methodName);
                method.setAccessible(true);// 提高反射速度
                method.invoke(ctx);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (onTableLoadListener != null) {
            onTableLoadListener.onLoaded(this);
        }

        isLoading = false;
    }

    /**
     * 设置行高
     *
     * @param tabRow 行
     * @param height 行高
     */
    private void setTableRowHeight(LinearLayout tabRow, int height) {
        ViewGroup.LayoutParams lp;
        for (int i = 0; i < tabRow.getChildCount(); i++) {
            lp = tabRow.getChildAt(i).getLayoutParams();
            lp.height = height;
            tabRow.getChildAt(i).setLayoutParams(lp);
        }
    }

    // 表头点击事件
    class titleClick implements OnClickListener {
        private String headType = "";

        titleClick(String headType) {
            this.headType = headType;
        }

        private void goneSortPic(TableRow orderLayout) {
            for (int i = 0; i < orderLayout.getChildCount(); i++) {
                LinearLayout tempMoveLayout;
                if (orderLayout.getChildAt(i) instanceof LinearLayout) {
                    tempMoveLayout = (LinearLayout) orderLayout.getChildAt(i);
                } else {
                    continue;
                }

                ImageView moveImgView = (ImageView) tempMoveLayout.getChildAt(1);
                moveImgView.setVisibility(View.GONE);
            }
        }

        private String removePercentSymbol(String source) {
            int index = source.lastIndexOf("%");
            if (-1 == index) {
                return source;
            } else {
                return source.substring(0, index);
            }
        }

        @Override
        public void onClick(View v) {
            if (fixColumnIndex > -1)// 只有左右滑动表格且点击右侧排序列才计算偏移量
                mScrollX = ((SyncHScrollView) rightTabLayout.getParent().getParent()).getScrollX();

            // 隐藏另一侧的排序图标
            if ("left".equals(headType)) {// 隐藏滑动表头排序图标
                TableRow orderLayout = (TableRow) rightTableHead.getChildAt(rightTableHead
                        .getChildCount() - 1);
                goneSortPic(orderLayout);
            } else if ("right".equals(headType) && fixColumnIndex > -1) {// 隐藏固定表头排序图标
                TableRow orderLayout = (TableRow) leftTableHead.getChildAt(leftTableHead
                        .getChildCount() - 1);
                goneSortPic(orderLayout);
            }
            // 显示点击的表头排序图标，并隐藏同侧的其他列排序图标
            LinearLayout tempTitle = (LinearLayout) v.getParent();
            for (int i = 0; i < tempTitle.getChildCount(); i++) {
                LinearLayout fixLayout;
                if (tempTitle.getChildAt(i) instanceof LinearLayout) {
                    fixLayout = (LinearLayout) tempTitle.getChildAt(i);
                } else {
                    continue;
                }

                ImageView fixImgView = (ImageView) fixLayout.getChildAt(1);
                TextView tx = (TextView) fixLayout.getChildAt(0);
                TextPaint tp = tx.getPaint();
                float textWidth = tp.measureText(tx.getText().toString());
                int imageWidth = Level1Util.dip2px(ctx, 15);
                if (fixLayout == v) {
                    if (textWidth > (fixLayout.getWidth() - imageWidth)) {
                        tx.setWidth(fixLayout.getWidth() - imageWidth);
                    }
                    if (fixImgView.getVisibility() == View.VISIBLE) {// 如果点击已排序列，则反转
                        ASC = !ASC;
                    } else {
                        if ("DAY".equalsIgnoreCase(columnTypes[0]) || "MONTH".equalsIgnoreCase
                                (columnTypes[0])) {// 日期类型的默认倒序
                            ASC = false;
                        } else {
                            ASC = true;
                        }
                    }
                    fixImgView.setVisibility(View.VISIBLE);
                    if (ASC) {
                        fixImgView.setImageResource(R.drawable.arrow_up);
                    } else {
                        fixImgView.setImageResource(R.drawable.arrow_down);
                    }

                } else {// 不是点击列则隐藏排序图标
                    fixImgView.setVisibility(View.GONE);
                    if (textWidth > (fixLayout.getWidth() - imageWidth)) {
                        tx.setWidth(fixLayout.getWidth());
                    }
                }
            }
            // 转化为ArrayList进行排序，并去除不参与排序的行
            ArrayList<JSONObject> newArrayList = new ArrayList<JSONObject>();
            JSONArray tempArray = tabArray;
            for (int i = 0; i < tempArray.length(); i++) {
                if (noOrderJSON != null && tempArray.optJSONObject(i) == noOrderJSON) {
                    continue;
                }
                newArrayList.add(tempArray.optJSONObject(i));
            }
            final int colIndex = v.getId() + 1;
            Collections.sort(newArrayList, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject arg0, JSONObject arg1) {
                    // 先查看是否有排序order字段，没有则获取value字段
                    String str1 = arg0.optString("o" + colIndex, arg0.optString("v" + colIndex));
                    String str2 = arg1.optString("o" + colIndex, arg1.optString("v" + colIndex));
                    str1 = removePercentSymbol(str1);
                    str2 = removePercentSymbol(str2);
                    int flag;
                    if (!ASC) {
                        flag = (str2.compareTo(str1));
                    } else {
                        flag = (str1.compareTo(str2));
                    }
                    // 如果两个对象都是浮点型的数据，则用BigDecimal的方法比较
                    try {
                        BigDecimal b1 = new BigDecimal(str1);
                        BigDecimal b2 = new BigDecimal(str2);
                        if (!ASC) {
                            flag = (b2.compareTo(b1));
                        } else {
                            flag = b1.compareTo(b2);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return flag;
                }
            });
            // 重新组织为JSONArray并添加上不参与排序的行
            JSONArray newTabArray = new JSONArray();
            if (1 == noOrderRow) {
                newTabArray.put(noOrderJSON);
            }
            for (int i = 0; i < newArrayList.size(); i++) {
                newTabArray.put(newArrayList.get(i));
            }
            if (99 == noOrderRow) {
                newTabArray.put(noOrderJSON);
            }
            // 重新加载表格表体
            if (!isLoading) {
                currentPager = 1;// 重置为第一页
                tabArray = newTabArray;
                isLoading = true;
                // 当不左右滑动时，leftTabLayout.getParent()为null
                if (leftTabLayout.getParent() != null)
                    ((RelativeLayout) leftTabLayout.getParent()).removeAllViews();
                ((RelativeLayout) rightTabLayout.getParent()).removeAllViews();
                vScrollView.removeAllViews();
                removeView(vScrollView);
                addView(createScrollTable(), LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            }
            // 修改行高
            ViewTreeObserver vto2 = rightTableHead.getViewTreeObserver();
            vto2.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int maxHeight = 0;
                    rightTableHead.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (rightTableHead.getChildCount() > 0) {
                        if (rightTableHead.getChildAt(rightTableHead.getChildCount() - 1)
                                instanceof TableRow) {
                            TableRow row = (TableRow) rightTableHead.getChildAt(rightTableHead
                                    .getChildCount() - 1);
                            int count = row.getChildCount();
                            for (int i = 0; i < count; i++) {
                                if (row.getChildAt(i) instanceof LinearLayout) {
                                    LinearLayout targetLyt = (LinearLayout) row.getChildAt(i);
                                    if (targetLyt.getHeight() > maxHeight) {
                                        maxHeight = targetLyt.getHeight();
                                    }
                                }
                            }
                        }
                    }

                    if (leftTableHead.getChildCount() > 0) {
                        if (leftTableHead.getChildAt(leftTableHead.getChildCount() - 1)
                                instanceof TableRow) {
                            TableRow row = (TableRow) leftTableHead.getChildAt(leftTableHead
                                    .getChildCount() - 1);
                            int count = row.getChildCount();
                            for (int i = 0; i < count; i++) {
                                if (row.getChildAt(i) instanceof LinearLayout) {
                                    LinearLayout targetLyt = (LinearLayout) row.getChildAt(i);
                                    if (targetLyt.getHeight() > maxHeight) {
                                        maxHeight = targetLyt.getHeight();
                                    }
                                }
                            }
                        }
                    }
                    if (leftTableHead.getChildCount() > 0) {
                        if (leftTableHead.getChildAt(leftTableHead.getChildCount() - 1)
                                instanceof TableRow) {
                            TableRow row = (TableRow) leftTableHead.getChildAt(leftTableHead
                                    .getChildCount() - 1);
                            setTableRowHeight(row, maxHeight);
                        }
                    }
                    if (rightTableHead.getChildCount() > 0) {
                        if (rightTableHead.getChildAt(rightTableHead.getChildCount() - 1)
                                instanceof TableRow) {
                            TableRow row = (TableRow) rightTableHead.getChildAt(rightTableHead
                                    .getChildCount() - 1);
                            setTableRowHeight(row, maxHeight);
                        }
                    }

                    return false;
                }
            });
        }
    }

    /**
     * 设置锁定列，-1表示不锁定,0表示锁定第一列，类推。默认-1
     *
     * @param fixColumnIndex
     */
    public void setColumnLockNum(int fixColumnIndex) {
        this.fixColumnIndex = fixColumnIndex < 0 ? -1 : fixColumnIndex;
    }

    /**
     * 是否支持表头点击排序，默认支持
     *
     * @param sortFlag
     */
    public void setSort(boolean sortFlag) {
        this.sortFlag = sortFlag;
    }

    /**
     * 设置默认排序列，必须在create方法后调用
     *
     * @param col   列号，从0开始
     * @param order 排序方向，只能为 desc或asc
     */
    public void setSortCol(int col, String order) {
        TableLayout tl = null;
        if (col > fixColumnIndex) {
            tl = rightTableHead;
            col = col - fixColumnIndex - 1;// 重新计算列号
        } else {
            tl = leftTableHead;
        }
        TableRow tr = (TableRow) tl.getChildAt(tl.getChildCount() - 1);// 拿到最后一行表头
        if (tr.getChildAt(col) != null) {
            LinearLayout headCell = (LinearLayout) tr.getChildAt(col);
            ImageView iv = (ImageView) headCell.getChildAt(1);
            iv.setVisibility(View.VISIBLE);
            if ("desc".equals(order)) {
                iv.setImageResource(R.drawable.arrow_down);
            } else {
                iv.setImageResource(R.drawable.arrow_up);
            }
        }
    }

    /**
     * 不参与排序的行：1:第一行 99:最后一行 0:全参与
     *
     * @param noOrderRow
     */
    public void setNoSortRow(int noOrderRow) {
        this.noOrderRow = noOrderRow;
    }

    /**
     * 宽度设定的是否是百分比
     *
     * @param isPercent
     */
    public void setWidthIsPercent(boolean isPercent) {
        this.isPercent = isPercent;
    }

    /**
     * 行间隔背景色，默认"#00000000,#55555555"(第一个是偶数行背景色，第二个是奇数行背景色)
     *
     * @param bgExpression
     */
    public void setRowBackgroundColors(String bgExpression) {
        this.bgExpression = bgExpression;
    }

    /**
     * 行点击事件，不能和整体表格点击事件并存 通过v.getTag()方法获取整行的JSON
     *
     * @param rowListener
     */
    public void setRowListener(OnClickListener rowListener) {
        if (rowListener != null)
            this.rowListener = rowListener;
    }

    /**
     * 单元格点击事件，不能和整体表格点击事件并存 通过v.getTag()方法获取该单元格的JSON
     *
     * @param cellListener
     */
    public void setCellListener(OnClickListener cellListener) {
        this.cellListener = cellListener;
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        longListener = l;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        clickListener = l;
    }

    /**
     * 表头背景色
     *
     * @param headBGColor
     */
    public void setTableHeadBackgroundColor(String headBGColor) {
        this.headBGColor = Color.parseColor(headBGColor);
    }

    public void setTableHeadBackgroundColor(int headBGColor) {
        this.headBGColor = headBGColor;
    }

    public void setTableHeadTextColor(String headTextColor) {
        this.headTextColor = Color.parseColor(headTextColor);
    }

    public void setTableHeadTextColor(int headTextColor) {
        this.headTextColor = headTextColor;
    }

    public void setTableHeadTextSize(int headFontSize) {
        this.headFontSize = headFontSize;
    }

    /**
     * 表头是否加粗
     *
     * @param headIsBold
     */
    public void setTableHeadIsBold(boolean headIsBold) {
        this.headIsBold = headIsBold;
    }

    /**
     * 非数据列内容是否居左
     *
     * @param bodyTextIsLeft
     */
    public void setBodyTextIsLeft(boolean bodyTextIsLeft) {
        this.bodyTextIsLeft = bodyTextIsLeft;
    }

    /**
     * 表头是否强制不换行
     *
     * @param titleSingle
     */
    public void setTitleSingle(boolean titleSingle) {
        this.titleSingle = titleSingle;
    }

    /**
     * 表格背景色
     *
     * @param tableBGColor
     */
    public void setTableBackgroundColor(String tableBGColor) {
        this.tableBGColor = Color.parseColor(tableBGColor);
    }

    public void setTableBackgroundColor(int tableBGColor) {
        this.tableBGColor = tableBGColor;
    }

    public void setTableBodyTextColor(int bodyTextColor) {
        this.bodyTextColor = bodyTextColor;
    }

    public void setTableBodyTextColor(String bodyTextColor) {
        this.bodyTextColor = Color.parseColor(bodyTextColor);
    }

    public void setTableBodyTextSize(int bodyFontSize) {
        this.bodyFontSize = bodyFontSize;
    }

    /**
     * 哪些列后面需要插入分割线
     *
     * @param splitCols
     */
    public void setColSplitLine(int[] splitCols) {
        if (splitCols == null)
            splitCols = new int[]{};
        this.splitCols = splitCols;
    }

    /**
     * 列分割线的颜色
     *
     * @param splitColColor
     */
    public void setColSplitLineColor(int splitColColor) {
        this.splitColColor = splitColColor;
    }

    public void setColSplitLineColor(String splitColColor) {
        this.splitColColor = Color.parseColor(splitColColor);
    }

    /**
     * 列分割线的宽度
     *
     * @param splitColWidth
     */
    public void setColSplitLineWidth(int splitColWidth) {
        this.splitColWidth = splitColWidth;
    }

    /**
     * 获取表格行数
     *
     * @return int
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * 获取表格列数
     *
     * @return
     */
    public int getColumnCount() {
        return columnCount;
    }

    /**
     * 获取表格每个列的宽度
     *
     * @return
     */
    public int[] getColumnWidths() {
        return columnWidths;
    }

    /**
     * 获取表格每个列的类型
     *
     * @return
     */
    public String[] getTableType() {
        return columnTypes;
    }

    /**
     * 获取表格每个列的名称
     *
     * @return
     */
    public String[] getHeadNames() {
        return columnNames;
    }

    /**
     * 设置最小行高，如数值过小导致显示不全，则使用自适应行高。 默认-1表示自适应。
     *
     * @param rowHeight
     */
    public void setRowHeight(int rowHeight) {
        this.rowHeight = Level1Util.getDipSize(rowHeight);
    }

    /**
     * 设置回调方法
     *
     * @param methodName
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * 获取指定行
     *
     * @param row 行号,从0开始
     * @return 指定行LinearLayout布局数组，指定行不存在返回空数组
     */
    public ArrayList<LinearLayout> getRow(int row) {
        ArrayList<LinearLayout> ll = new ArrayList<LinearLayout>();
        if (leftTabLayout == null || rightTabLayout == null)
            return new ArrayList<LinearLayout>();
        TableRow ltr = (TableRow) leftTabLayout.getChildAt(row);
        TableRow rtr = (TableRow) rightTabLayout.getChildAt(row);
        if (ltr == null || rtr == null)
            return new ArrayList<LinearLayout>();
        for (int j = 0; j < ltr.getChildCount(); j++) {
            LinearLayout tmp = (LinearLayout) ltr.getChildAt(j);
            ll.add(tmp);
        }
        for (int j = 0; j < rtr.getChildCount(); j++) {
            LinearLayout tmp = (LinearLayout) rtr.getChildAt(j);
            ll.add(tmp);
        }
        return ll;
    }

    /**
     * 获取指定列
     *
     * @param col 列号,从0开始
     * @return 指定列LinearLayout布局数组，指定列不存在返回空数组
     */
    public ArrayList<LinearLayout> getColumn(int col) {
        TableLayout tl;
        ArrayList<LinearLayout> ll = new ArrayList<LinearLayout>();
        // 判断列在leftTabLayout还是rightTabLayout
        if (col > fixColumnIndex) {
            tl = rightTabLayout;
            col = col - fixColumnIndex - 1;// 重新计算列号
        } else {
            tl = leftTabLayout;
        }
        if (tl == null)
            return new ArrayList<LinearLayout>();

        for (int j = 0; j < tl.getChildCount(); j++) {
            TableRow tr = (TableRow) tl.getChildAt(j);
            if (tr.getChildAt(col) != null)
                ll.add((LinearLayout) tr.getChildAt(col));
        }
        return ll;
    }

    /**
     * 获取指定单元格
     *
     * @param row 行号,从0开始
     * @param col 列号,从0开始
     * @return 指定单元格LinearLayout布局，单元格不存在返回null
     */
    public LinearLayout getCell(int row, int col) {
        TableLayout tl;
        // 判断列在leftTabLayout还是rightTabLayout
        if (col > fixColumnIndex) {
            tl = rightTabLayout;
            col = col - fixColumnIndex - 1;// 重新计算列号
        } else {
            tl = leftTabLayout;
        }
        if (tl == null)
            return null;
        TableRow tr = (TableRow) tl.getChildAt(row);
        if (tr == null)
            return null;
        return (LinearLayout) tr.getChildAt(col);
    }

    /**
     * 获取截图
     *
     * @param foreground 水印图
     * @return 截图Bitmap
     */
    public Bitmap getExportImg(Bitmap foreground) {
        allHeight += rightTableHead.getHeight();// 加上表头高度
        int rightStartX = 0;
        Bitmap bitmap = Bitmap.createBitmap(allWidth, allHeight, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(bitmap);
        if (fixColumnIndex > -1) {
            Bitmap leftTab = BitmapOperateUtil.getBitmapFromView((RelativeLayout) leftTabLayout
                    .getParent());
            Bitmap leftHead = BitmapOperateUtil.getBitmapFromView(leftTableHead);
            rightStartX = leftHead.getWidth();
            cv.drawBitmap(leftHead, 0, 0, null);
            cv.drawBitmap(leftTab, 0, leftHead.getHeight(), null);
        }
        Bitmap rightTab = BitmapOperateUtil.getBitmapFromView((RelativeLayout) rightTabLayout
                .getParent());
        Bitmap rightHead = BitmapOperateUtil.getBitmapFromView(rightTableHead);
        cv.drawBitmap(rightHead, rightStartX, 0, null);
        cv.drawBitmap(rightTab, rightStartX, rightHead.getHeight(), null);
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        cv.restore();// 存储
        if (foreground != null) {
            bitmap = BitmapOperateUtil.toConformBitmap(bitmap, foreground);
        }
        return bitmap;
    }

    /**
     * 一页最多显示条数,-1表示不分页,默认-1
     *
     * @param pagerSize
     */
    public void setPagerSize(int pagerSize) {
        this.pagerSize = pagerSize;
    }

    // 设置表头分割线颜色
    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    // 设置是否包含序列列
    public void setHasSerialCell(boolean hasSerialCell) {
        this.hasSerialCell = hasSerialCell;
    }

    // 设置是否按照type对齐文字
    public void setAlignWithType(boolean isAlignWithType) {
        this.isAlignWithType = isAlignWithType;
    }

    /**
     * 设置表体锚点偏移量
     *
     * @param mScrollX
     */
    public void setmScrollX(int mScrollX) {
        this.mScrollX = mScrollX;
    }

    /**
     * 设置表体锚点偏移量
     *
     * @param mScrollY
     */
    public void setmScrollY(int mScrollY) {
        this.mScrollY = mScrollY;
    }

    /**
     * 获取表格左右滑动距离
     *
     * @return
     */
    public int getTableScrollX() {
        if (fixColumnIndex > -1) {
            return ((SyncHScrollView) rightTabLayout.getParent().getParent()).getScrollX();
        } else {
            return 0;
        }
    }

    /**
     * 获取表格上下滑动距离(有点不准)
     *
     * @return
     */
    public int getTableScrollY() {
        if (fixColumnIndex > -1) {
            return ((MyScrollView) leftTabLayout.getParent().getParent().getParent()).getScrollY();
        } else {
            return ((MyScrollView) rightTabLayout.getParent().getParent().getParent()).getScrollY();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean flag = super.onInterceptTouchEvent(ev);
        return flag;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean flag = super.onTouchEvent(ev);
        return flag;
    }

    public interface OnTableLoadListener {

        void onLoaded(ScrollListView sv);
    }

    public int getHeadHeight() {
        return leftTableHead.getHeight() + leftTabLayout.getHeight();
    }
}
