package com.ztesoft.level1.table.news;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.Level1Util;
import com.ztesoft.level1.R;
import com.ztesoft.level1.table.util.MyScrollView;
import com.ztesoft.level1.util.BitmapOperateUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 左右滑动表格控件 ###########表格整体截图----大图片会报错
 *
 * @author wanghx2
 */
public class MyTableView extends LinearLayout {

    private Context ctx;
    private int displayWidth = -1;// 显示宽度
    // 前X列需要锁定，-1表示不锁定列
    private int fixColumnIndex = -1;

    private OnRowClickListener rowListener;// 行事件
    private OnCellClickListener cellListener;// 单元格事件
    private OnLongClickListener longListener;
    private OnClickListener clickListener;
    // 加载监听的事件
    private OnTableLoadListener onTableLoadListener;

    private boolean titleSingle = false;// 表头是否强制不换行
    private String errorValue = "";// 异常值显示

    // 表头字体
    private int headFontSize = 15;
    private int headTextColor = Color.WHITE;// 表头文本颜色
    private int headBGColor = Color.BLUE;// 表头背景色，覆盖在headBgImage上面
    // 表体字体大小
    private int bodyFontSize = 14;
    private int bodyTextColor = Color.BLACK;// 表体文字颜色
    private int tableBGColor = Color.WHITE;// 整体背景色

    // 背景交替渲染"#00000000,#55555555"
    private String bgExpression = "#00000000,#55555555";
    private boolean rowSplitFlag = false;// 是否需要行分割线
    private int rowSplitColor = Color.BLACK;
    private int rowHeight = -1;

    private boolean sortFlag = true;// 是否支持表头排序，true:支持
    // 不参与排序的行：1:第一行 99:最后一行 0:全参与
    private int noOrderRow = 0;

    public View parentScrollView;// 谨慎使用,只是在有2个scrollView嵌套的时候使用
    private MyScrollView.OnBorderListener scrollListener;
    // 内部使用----------------------

    private int rowCount = 0;// 表格行数
    private int columnCount = 0;// 表格列数

    private boolean isLoading = false;// 事件加载锁
    private int mScrollX = 0;// 左右滑动偏移量
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
    private JSONArray tabArray;
    private JSONArray tableHead;
    private JSONArray multiHead;

    // 左侧锁定表体（固定部分）
    private TableLayout leftTabLayout;
    // 右侧滑动表体（滑动部分）
    private TableLayout rightTabLayout;
    // 左侧锁定表头（固定部分）
    private TableLayout leftTableHead;
    // 右侧滑动表头（滑动部分）
    private TableLayout rightTableHead;
    private JSONObject noOrderJSON = null;// 不参与排序行的JSON
    // 排序标示,升序为true,降序为false
    private boolean ASC = true;

    // 常量--------------------------
    public final static int NOORDER_FIRSTROW = 1;
    public final static int NOORDER_LASTROW = 99;
    public final static int NOORDER_NOROW = 0;

    /**
     * 表格控件构造函数
     *
     * @param context
     */
    public MyTableView(Context context) {
        super(context);
        this.ctx = context;
        mScrollX = 0;
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
     * 绘制表格,必须先调用setTableHeadJSON和setTableBodyJSON方法
     *
     * @param width 控件实际显示宽度(具体像素),默认为屏幕宽度
     */
    public void create(int width) {
        if (width < 0) {//如果宽度小于0，则默认屏宽
            this.displayWidth = Level1Bean.actualWidth;
        } else {
            this.displayWidth = width;
        }
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
                        Toast.makeText(ctx, R.string.error_tableMuliHead, Toast.LENGTH_SHORT)
                                .show();
                        this.addView(new LinearLayout(ctx), LayoutParams.MATCH_PARENT,
                                LayoutParams.WRAP_CONTENT);
                        return;
                    }
                }
            }
        }

        reColumColsWidth();
        if (sortFlag && tabArray.length() > 1) {// 支持排序并且超过1条
            if (noOrderRow == NOORDER_FIRSTROW) {
                noOrderJSON = tabArray.optJSONObject(0);
            } else if (noOrderRow == NOORDER_LASTROW) {
                noOrderJSON = tabArray.optJSONObject(tabArray.length() - 1);
            }
        }
        buidScrollListView();
    }

    /**
     * 重新计算表格宽度
     */
    private void reColumColsWidth() {
        if (fixColumnIndex > -1) {// 锁定且百分比。按照显示宽度×百分比计算
            for (int i = 0; i < columnCount; i++) {
                this.columnWidths[i] = (int) (this.columnWidths[i] / 100.00 * displayWidth);
            }
        } else {// 不锁定且百分比。强制全部展现。计算列宽占比×显示宽度
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
        leftTableHead.setPadding(0, Level1Util.getDipSize(5), 0, Level1Util.getDipSize(5));
        leftTableHead.setGravity(Gravity.CENTER_VERTICAL);
        rightTableHead = new TableLayout(ctx);
        rightTableHead.setPadding(0, Level1Util.getDipSize(5), 0, Level1Util.getDipSize(5));
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
        tableStyle.setHeadTextSize(headFontSize);
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
                            (tableMulTitle[x][y], tableMulSpan[x][y]);
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
            fixHeadLayout.setTag(columnTypes[i]);
            if (i < fixColumnIndex + 1) {
                if (sortFlag) {
                    fixHeadLayout.setOnClickListener(new titleClick("left"));
                }
                ltr.addView(fixHeadLayout, columnWidths[i], LinearLayout.LayoutParams.MATCH_PARENT);
            } else {
                if (sortFlag) {
                    fixHeadLayout.setOnClickListener(new titleClick("right"));
                }
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
        if (fixColumnIndex > -1) {
            // 左右滑动必须要设置宽度，不然被遮挡的部分不绘制
            rightTableHead.addView(rtr, width, LinearLayout.LayoutParams.WRAP_CONTENT);
        } else {
            rightTableHead.addView(rtr);
        }

        if (fixColumnIndex != -1) {//只有在左右滑动的情况下，才需要左右行高对齐
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
    }

    /**
     * 生成表体（固定部分+滑动部分） 自定义的ScrollView和HorizontalScrollView嵌套组成
     *
     * @return
     */
    private View createScrollTable() {
        LinearLayout tabLayout = new LinearLayout(ctx);
        leftTabLayout.removeAllViews();
        rightTabLayout.removeAllViews();
        drawTableBody(0, rowCount);
        // 添加列分割线
        if (fixColumnIndex > -1) {
            int fixColumnWidth = 0;
            for (int i = 0; i < fixColumnIndex + 1; i++) {
                fixColumnWidth += columnWidths[i];
            }
            tabLayout.addView(leftTabLayout, fixColumnWidth, LayoutParams.WRAP_CONTENT);
        }
        com.ztesoft.level1.table.util.MyScrollView vScrollView = new com.ztesoft.level1.table
                .util.MyScrollView(ctx);
        // 添加到表体总布局tabLayout
        if (fixColumnIndex == -1) {
            tabLayout.addView(rightTabLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        } else {
            final com.ztesoft.level1.table.util.SyncHScrollView hScrollView = new com.ztesoft
                    .level1.table.util.SyncHScrollView(ctx);
            hScrollView.setHorizontalScrollBarEnabled(false);
            hScrollView.setHorizontalFadingEdgeEnabled(false);
            hScrollView.addView(rightTabLayout, LayoutParams.MATCH_PARENT, LayoutParams
                    .WRAP_CONTENT);
            hScrollView.setScrollView((HorizontalScrollView) rightTableHead.getParent());// 
            // 绑定可滑动表头,保证左右同步滑动
            hScrollView.post(new Runnable() {
                public void run() {
                    hScrollView.scrollTo(mScrollX, 0);
                }
            });
            tabLayout.addView(hScrollView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        // 设置上下滑动
        vScrollView.addView(tabLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        if (scrollListener != null) {
            vScrollView.setOnBorderListener(scrollListener);
        }
        if (parentScrollView != null) {
            vScrollView.setParentScrollView(parentScrollView);
        }
        return vScrollView;
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
            final TableRow rightTabRow = new TableRow(ctx);
            if (rowSplitFlag) {
                rightTabRow.setBackgroundResource(R.drawable.tableview_row_bg);
                //修改显示颜色
                LayerDrawable myGrad = (LayerDrawable) rightTabRow.getBackground();
                GradientDrawable ddd = (GradientDrawable) myGrad.getDrawable(0);
                ddd.setColor(rowSplitColor);//分割线颜色
                GradientDrawable ddd2 = (GradientDrawable) myGrad.getDrawable(1);
                ddd2.setColor(tableBGColor);//背景色
                leftTabRow.setBackgroundResource(R.drawable.tableview_row_bg);
            }
            leftTabLayout.addView(leftTabRow);
            rightTabLayout.addView(rightTabRow);

            for (int i = 0; i < columnCount; i++) {
                TableStyleUtil tableStyle = new TableStyleUtil(ctx, columnTypes);
                tableStyle.setBodyTextSize(bodyFontSize);
                tableStyle.setBodyTextColor(bodyTextColor);
                tableStyle.setErrorValue(errorValue);
                LinearLayout tdLayout = new LinearLayout(ctx);
                if (0 != columnWidths[i]) {//如果列宽不为空，则显示内容
                    tdLayout = tableStyle.drawTableBodyCell(i, itemObj);
                    if (cellListener != null) {
                        tdLayout.setOnClickListener(new cellClick(j, i));
                    }
                }

                // 先自适应，后续修改行高
                if (i < fixColumnIndex + 1) {// 将锁定列单元格放入leftTabRow
                    leftTabRow.addView(tdLayout, columnWidths[i], LayoutParams.MATCH_PARENT);
                } else {// 将滑动列单元格放入rightTabRow
                    rightTabRow.addView(tdLayout, columnWidths[i], LayoutParams.MATCH_PARENT);
                }
            }

            leftTabRow.setTag(itemObj);
            rightTabRow.setTag(itemObj);
            if (rowListener != null) {
                leftTabRow.setOnClickListener(new rowClick(j));
                rightTabRow.setOnClickListener(new rowClick(j));
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

            if (fixColumnIndex != -1) {//只有在左右滑动的情况下，才需要左右行高对齐
                // 修改行高，并保证左右滑动表格行高对齐
                ViewTreeObserver vto2 = rightTabRow.getViewTreeObserver();
                vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        rightTabRow.getViewTreeObserver().removeGlobalOnLayoutListener(this);
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
                                //						} else if (rightH != leftH) {// 
                                // 大于规定行高，且左右不相等。左边设置为右边行高
                                //							setTableRowHeight(leftTabRow, rightH);
                                //							allHeight += rightH;
                            } else {
                                setTableRowHeight(leftTabRow, rightH);
                                allHeight += rightH;
                            }
                        }
                    }

                });
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
    private void setTableRowHeight(TableRow tabRow, int height) {
        ViewGroup.LayoutParams lp = null;
        for (int i = 0; i < 1; i++) {
            lp = tabRow.getChildAt(i).getLayoutParams();
            lp.height = height;
            tabRow.getChildAt(i).setLayoutParams(lp);
        }
    }

    /**
     * 刷新表体数据
     *
     * @param tableArray
     */
    public void refreshData(JSONArray tableArray) {
//		// 重新加载表格表体
//		tabArray = tableArray;
//		rowCount = tableArray.length();
//		
//		if (!isLoading) {
//			isLoading = true;
//			//释放leftTabLayout和rightTabLayout，方便后续重新载入
//			if (leftTabLayout.getParent() != null){// 当不左右滑动时，leftTabLayout.getParent()为null
//				((ViewGroup) leftTabLayout.getParent()).removeAllViews();
//			}
//			((ViewGroup) rightTabLayout.getParent()).removeAllViews();
//			addView(createScrollTable(), LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//		}


        //追加表格内容
        int oldRowCount = rowCount;
        for (int i = 0; i < tableArray.length(); i++) {
            tabArray.put(tableArray.optJSONObject(i));
        }
        rowCount += tableArray.length();
        if (!isLoading) {
            isLoading = true;
            drawTableBody(oldRowCount, rowCount);
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
                LinearLayout tempMoveLayout = null;
                if (orderLayout.getChildAt(i) instanceof LinearLayout) {
                    tempMoveLayout = (LinearLayout) orderLayout.getChildAt(i);
                } else {
                    continue;
                }

                ImageView moveImgView = (ImageView) tempMoveLayout.getChildAt(1);
                moveImgView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            if (fixColumnIndex > -1)// 只有左右滑动表格且点击右侧排序列才计算偏移量
                mScrollX = ((com.ztesoft.level1.table.util.SyncHScrollView) rightTabLayout
                        .getParent()).getScrollX();

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
                LinearLayout fixLayout = null;
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
                        String curColType = (String) v.getTag();
                        if ("DAY".equalsIgnoreCase(curColType) || "MONTH".equalsIgnoreCase
                                (curColType)) {// 日期类型的默认倒序
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
                    int flag = 0;
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
                    }
                    return flag;
                }
            });
            // 重新组织为JSONArray并添加上不参与排序的行
            JSONArray newTabArray = new JSONArray();
            if (NOORDER_FIRSTROW == noOrderRow) {
                newTabArray.put(noOrderJSON);
            }
            for (int i = 0; i < newArrayList.size(); i++) {
                newTabArray.put(newArrayList.get(i));
            }
            if (NOORDER_LASTROW == noOrderRow) {
                newTabArray.put(noOrderJSON);
            }
            // 重新加载表格表体
            if (!isLoading) {
                tabArray = newTabArray;
                isLoading = true;
                //释放leftTabLayout和rightTabLayout，方便后续重新载入
                if (leftTabLayout.getParent() != null) {// 当不左右滑动时，leftTabLayout.getParent()为null
                    ((ViewGroup) leftTabLayout.getParent()).removeAllViews();
                }
                ((ViewGroup) rightTabLayout.getParent()).removeAllViews();
                addView(createScrollTable(), LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            }
        }
    }

    class rowClick implements OnClickListener {
        private int rowId = -1;

        rowClick(int rowId) {
            this.rowId = rowId;
        }

        @Override
        public void onClick(View v) {
            rowListener.onClick(v, rowId);
        }
    }

    class cellClick implements OnClickListener {
        private int rowId = -1;
        private int colId = -1;

        cellClick(int rowId, int colId) {
            this.rowId = rowId;
            this.colId = colId;
        }

        @Override
        public void onClick(View v) {
            cellListener.onClick(v, rowId, colId);
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
     * 设置默认排序列，必须在create方法后调用，默认初始不显示排序图标。
     * 只显示排序图标，不做实际排序，需要服务端排序好。
     *
     * @param col   列号，从0开始
     * @param order 排序方向，true表示升序，false表示倒序
     */
    public void setSortCol(int col, boolean order) {
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
            if (order) {
                iv.setImageResource(R.drawable.arrow_up);
            } else {
                iv.setImageResource(R.drawable.arrow_down);
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
     * 设置行间隔背景色，默认"#00000000,#55555555"(第一个是偶数行背景色，第二个是奇数行背景色)
     * 与“行分割线”不兼容
     *
     * @param bgExpression
     */
    public void setRowBackgroundColors(String bgExpression) {
        this.bgExpression = bgExpression;
        if (bgExpression != null && !"".equals(bgExpression)) {//取消行分割线
            setRowSplitFlag(false);
        }
    }

    /**
     * 设置行分割线，默认false没有分割线
     * 与“行间隔背景色”不兼容
     *
     * @param rowSplitFlag
     */
    public void setRowSplitFlag(boolean rowSplitFlag) {
        this.rowSplitFlag = rowSplitFlag;
        if (rowSplitFlag) {//取消行间隔色
            setRowBackgroundColors(null);
        }
    }

    /**
     * 设置行分割线颜色（仅rowSplitFlag=true时生效），默认黑色
     *
     * @param rowSplitColor
     */
    public void setRowSplitColor(int rowSplitColor) {
        if (rowSplitFlag) {
            this.rowSplitColor = rowSplitColor;
        }
    }

    /**
     * 表头是否强制不换行，默认换行
     *
     * @param titleSingle
     */
    public void setTitleSingle(boolean titleSingle) {
        this.titleSingle = titleSingle;
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
     * 行点击事件，不能和整体表格点击事件并存
     *
     * @param rowListener
     */
    public void setRowListener(OnRowClickListener rowListener) {
        if (rowListener != null)
            this.rowListener = rowListener;
    }

    /**
     * 单元格点击事件，不能和整体表格点击事件并存
     *
     * @param cellListener
     */
    public void setCellListener(OnCellClickListener cellListener) {
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
     * 获取表格行数，异步加载时，仅返回单次访问的行数
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
        TableLayout tl = null;
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
        TableLayout tl = null;
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
            Bitmap leftTab = BitmapOperateUtil.getBitmapFromView(leftTabLayout);
            Bitmap leftHead = BitmapOperateUtil.getBitmapFromView(leftTableHead);
            rightStartX = leftHead.getWidth();
            cv.drawBitmap(leftHead, 0, 0, null);
            cv.drawBitmap(leftTab, 0, leftHead.getHeight(), null);
        }
        Bitmap rightTab = BitmapOperateUtil.getBitmapFromView(rightTabLayout);
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
     * 表格嵌入scrollview中时使用，可实现内外部滚动
     *
     * @param parentScrollView 父类
     */
    public void setParentScrollView(View parentScrollView) {
        this.parentScrollView = parentScrollView;
    }

    /**
     * 回调监听
     *
     * @param onTableLoadListener
     */
    public void setOnTableLoadListener(OnTableLoadListener onTableLoadListener) {
        this.onTableLoadListener = onTableLoadListener;
    }

    public void setScrollListener(MyScrollView.OnBorderListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    public interface OnTableLoadListener {

        /**
         * 回调监听
         *
         * @param sv 自身
         */
        public void onLoaded(MyTableView sv);
    }

    /**
     * 当数据json中对应的单元格无显示值v时，显示该 异常值
     *
     * @param errorValue
     */
    public void setErrorValue(String errorValue) {
        this.errorValue = errorValue;
    }


    /**
     * 行点击事件
     *
     * @author wanghx2
     */
    public interface OnRowClickListener {

        /**
         * 行点击事件
         *
         * @param v     点击的view，可以通过v.getTag获取该行对应的json
         * @param rowId 点击行的行号，从0开始
         */
        public void onClick(View v, int rowId);
    }

    public interface OnCellClickListener {

        /**
         * 单元格点击事件
         *
         * @param v     点击的view，可以通过v.getTag获取该行对应的json
         * @param rowId 点击单元格的行号，从0开始
         * @param colId 点击单元格的列号，从0开始
         */
        public void onClick(View v, int rowId, int colId);
    }
}
