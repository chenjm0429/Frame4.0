package com.ztesoft.level1.gridview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.R;
import com.ztesoft.level1.dialog.MyAlertDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * 九宫格
 *
 * @author wanghx2
 */
public class MyGridView extends GridView {

    private int dragPosition; // 开始拖拽的位置
    private int dragPointX; // 相对于item的x坐标
    private int dragPointY; // 相对于item的y坐标
    private int dragOffsetX; // MyGridView控件左边与屏幕最左边的距离
    private int dragOffsetY; // MyGridView控件顶部与屏幕最顶部的距离
    private ImageView dragImageView; // 被拖拽的项(item)，其实就是一个ImageView

    private WindowManager windowManager;// windows窗口控制类
    private WindowManager.LayoutParams windowParams;// 用于控制拖拽项的显示的参数

    private boolean isDelDark = false;// 是否是删除事件
    private boolean isNullDark = false;// 是否取消删除事件
    private boolean delFlag = false;// 是否允许删除
    private boolean moveFlag = false;// 是否允许移动
    private JSONObject addItem = null;// 如不为空，则存在新增按钮

    private GridAdapter adapter = null;
    private ArrayList<JSONObject> lstImageItem = null;

    private G_PageListener pageListener;// 监听点击事件

    private MyGridView(Context context) {
        super(context);
    }

    /**
     * 自定义adapter
     *
     * @param context
     * @param adapter 继承com.linkage.bace.ui.gridview.GridAdapter
     */
    public MyGridView(Context context, GridAdapter adapter) {
        super(context);
        this.adapter = adapter;
        lstImageItem = adapter.getLstImageItem();
        this.setVerticalFadingEdgeEnabled(false);
        this.setVerticalScrollBarEnabled(false);
    }

    /**
     * 绘制
     */
    public void create() {
        addItem();
        setAdapter(adapter);
        setSelector(new ColorDrawable(Color.TRANSPARENT));// 选中的时候为透明色
        if (!moveFlag) {// 如果不允许移动，则肯定不允许删除
            setDelFlag(false);
        }
        adapter.setAllowsDelFlag(delFlag);
        setStretchMode(STRETCH_COLUMN_WIDTH);
        setOnItemClickListener(new ItemClickListener());
    }

    /**
     * 添加新增按钮
     */
    private void addItem() {
        if (addItem != null) {
            lstImageItem.add(addItem);
        }
    }

    /**
     * 自定义adapter时，重绘方法
     *
     * @param lstImageItem
     */
    public void reset(ArrayList<JSONObject> lstImageItem) {
        lstImageItem.remove(addItem);// 删除掉原有可能存在的删除按钮
        this.lstImageItem = lstImageItem;
        addItem();// 重新添加新增按钮
        adapter.setLstImageItem(lstImageItem);
        adapter.notifyDataSetChanged();
    }

    private void setOnItemLongClickListener(final MotionEvent ev, int xx, int yy) {
        this.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                if (!moveFlag) {// 不允许移动，直接返回
                    return true;
                }
                if (dragPosition == AdapterView.INVALID_POSITION) {// 未在图标上长按，直接返回
                    return true;
                }
                if (addItem != null && dragPosition == lstImageItem.size() - 1) {// 
                    // 允许添加，在添加按钮上长按，直接返回
                    return true;
                }
                Level1Bean.scrollToRight = true;
                Level1Bean.scrollToLeft = true;
//				Log.d(TAG, "成功触发长按事件！");
                lstImageItem.remove(addItem);// 隐藏新增图标
                setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        setGridStateNormal();

                    }
                });// 取消item点击事件
                adapter.setGridState(GridAdapter.GRIDSTATEDEL);
                adapter.notifyDataSetChanged();
                return true;
            }

            ;
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 将MotionEvent.ACTION_DOWN事件截获转给setOnItemLongClickListener事件
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            isNullDark = false;// 初始化
            isDelDark = false;// 初始化
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            int position = pointToPosition(x, y);
            dragPosition = position;

            if (adapter.getGridState() == GridAdapter.GRIDSTATEDEL) {// 已进入删除模式
                if (position == AdapterView.INVALID_POSITION) {// 如果点击空白
                    isNullDark = true;
                } else {
                    RelativeLayout itemView = (RelativeLayout) getChildAt(position -
                            getFirstVisiblePosition());
                    // 两次计算，获得触摸点与删除图标的距离
                    int dragPointX = Math.abs(x - itemView.getLeft());// 获得触摸点与griditem左侧距离
                    dragPointX = Math.abs(dragPointX - itemView.getChildAt(0).getLeft());// 
                    // 获得触摸点与删除图标左侧距离
                    int dragPointY = Math.abs(y - itemView.getTop());
                    dragPointY = Math.abs(dragPointY - itemView.getChildAt(0).getTop());
                    int gap = itemView.getWidth() / 4;
                    if (delFlag// 九宫格允许删除
                            && adapter.isAllowsDel(lstImageItem.get(dragPosition))// 该单元格允许删除
                            && dragPointX < gap && dragPointY < gap// XY在图标左上角
                            ) {// 点击删除图标
                        isDelDark = true;
                    } else {
                        Level1Bean.scrollToRight = true;
                        Level1Bean.scrollToLeft = true;
                    }
                }

            } else {
                setOnItemLongClickListener(ev, x, y);
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 开始拖动，根据bm创建dragImageView
     *
     * @param x
     * @param y
     */
    private void startDrag(int x, int y) {
        ViewGroup itemView = (ViewGroup) getChildAt(dragPosition - getFirstVisiblePosition());
        // 得到当前点在item内部的偏移量 即相对于item左上角的坐标
        dragPointX = x - itemView.getLeft();
        dragPointY = y - itemView.getTop();

        // 每次都销毁一次cache，重新生成一个bitmap
        itemView.destroyDrawingCache();
        itemView.setDrawingCacheEnabled(true);
        Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
        // 建立item的缩略图
        stopDrag();
        windowParams = new WindowManager.LayoutParams();
        windowParams.gravity = Gravity.TOP | Gravity.LEFT;// 这个必须加
        // 得到preview左上角相对于屏幕的坐标
        windowParams.x = x - dragPointX + dragOffsetX;
        windowParams.y = y - dragPointY + dragOffsetY;
        // 设置宽和高
        windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager
                .LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        windowParams.format = PixelFormat.TRANSLUCENT;
        windowParams.windowAnimations = 0;

        ImageView iv = new ImageView(getContext());
        iv.setImageBitmap(bm);
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);// 
        windowManager.addView(iv, windowParams);
        dragImageView = iv;
    }

    private int tttt; // 当前移动到的item
    MyAlertDialog gird;
    long downTime = 0;
    int dy = 0;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        int x = (int) ev.getX();
        int y = (int) ev.getY();
        int position = pointToPosition(x, y);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Date date = new Date();
                downTime = date.getTime();
                dy = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                Date da = new Date();
                if (Math.abs(dy - y) > 10 && da.getTime() - downTime < ViewConfiguration
                        .getLongPressTimeout()) {
                    return super.onTouchEvent(ev);
                }

                if (adapter.getGridState() == GridAdapter.GRIDSTATEDEL) {// 只有进入删除模式才处理
                    if (isNullDark || isDelDark) {// 点击空白/图标，都不处理
                        return true;
                    }
                    if (dragImageView == null) {// 移动时再触发有bug，当长按不移动时，会显示上次的图片########
                        // 绘制拖拽图标start
                        dragOffsetX = (int) (ev.getRawX() - x);
                        dragOffsetY = (int) (ev.getRawY() - y);
                        startDrag(x, y);
                        // 绘制拖拽图标end
                    }
                    if (position != tttt && position != AdapterView.INVALID_POSITION) {
                        tttt = position;
                        if (position != dragPosition) {// 交换
                            exchange(dragPosition, position);
                            dragPosition = position;
                            return true;
                        }
                    }
                    // 移动拖拽图标
                    onDrag(x, y);
                }
                break;
            case MotionEvent.ACTION_UP:
                // 删除拖拽图标
                stopDrag();
                if (isDelDark) {// 触发删除事件,前面严格控制了isDelDark，所以此处不需要其他条件
                    String title = getAdapter().getItemName(dragPosition);
                    if (position == dragPosition) {// 允许删除&&按下和抬起在同一个图标上
                        gird = new MyAlertDialog(getContext());
                        gird.setMessage(getContext().getString(R.string.system_isDelete, title));
                        gird.setPositiveButton(R.string.system_delete, new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                JSONObject x = lstImageItem.get(dragPosition);
                                lstImageItem.remove(dragPosition);
                                // 删除回调方法
                                pageListener.delClick(x);

                                if (lstImageItem.size() == 0) {
                                    setGridStateNormal();
                                } else {
                                    adapter.notifyDataSetChanged();
                                }
                                gird.dismiss();
                            }
                        });
                        gird.setNegativeButton(R.string.system_cancel, new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gird.dismiss();
                            }
                        });
                        gird.show();
                    }
                    return true;
                } else if (adapter.getGridState() == GridAdapter.GRIDSTATEDEL && isNullDark) {// 
                    // 隐藏删除小图标
                    setGridStateNormal();
                }
                setEnabled(true);
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 拖动中
     *
     * @param x 手指相对于父布局X坐标
     * @param y 手指相对于父布局Y坐标
     */
    private void onDrag(int x, int y) {
        if (dragImageView != null) {
            // 限制在gridview中拖动，X轴暂未限制
            if (y < dragPointY)
                y = dragPointY;
            if (y > getHeight() - dragPointY)
                y = getHeight() - dragPointY;

            windowParams.alpha = 0.6f;
            windowParams.x = x - dragPointX + dragOffsetX;
            windowParams.y = y - dragPointY + dragOffsetY;
            windowManager.updateViewLayout(dragImageView, windowParams);
        }

        // 当达到移动到屏幕上下两端时,自动滚动
        if (y < getHeight() / 4) {
            setEnabled(true);
            smoothScrollBy(-20, 50);
        } else if (y < getHeight() / 3) {
            setEnabled(true);
            smoothScrollBy(-10, 50);
        } else if (y > getHeight() / 3 * 2) {
            setEnabled(true);
            smoothScrollBy(10, 50);
        } else if (y > getHeight() / 4 * 3) {
            setEnabled(true);
            smoothScrollBy(20, 50);
        }
    }

    /**
     * 结束拖动，销毁dragImageView
     */
    private void stopDrag() {
        if (dragImageView != null) {
            windowManager.removeView(dragImageView);
            dragImageView = null;
        }
    }

    /**
     * 重新排序，并重绘界面
     *
     * @param startPosition 拖动起始点
     * @param endPosition   拖动结束点
     */
    private void exchange(int startPosition, int endPosition) {
        GridAdapter adapter = this.getAdapter();
        if (startPosition < endPosition) {
            for (int i = startPosition; i < endPosition; i++) {
                Object endObject = adapter.getItem(i + 1);
                Object startObject = adapter.getItem(i);
                lstImageItem.set(i, (JSONObject) endObject);
                lstImageItem.set(i + 1, (JSONObject) startObject);
            }
        } else {
            for (int i = startPosition; i > endPosition; i--) {
                Object endObject = adapter.getItem(i - 1);
                Object startObject = adapter.getItem(i);
                lstImageItem.set(i, (JSONObject) endObject);
                lstImageItem.set(i - 1, (JSONObject) startObject);
            }
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * 是否允许删除图标,允许移动时有效
     *
     * @param delFlag
     */
    public void setDelFlag(boolean delFlag) {
        this.delFlag = delFlag;
    }

    /**
     * 是否允许移动
     *
     * @param moveFlag
     */
    public void setMoveFlag(boolean moveFlag) {
        this.moveFlag = moveFlag;
    }

    /**
     * 设置九宫格列数
     *
     * @param numColumns
     */
    @Override
    public void setNumColumns(int numColumns) {
        super.setNumColumns(numColumns);
    }

    /**
     * 是否允许添加图标
     *
     * @param addItem
     */
    public void setAddItem(JSONObject addItem) {
        this.addItem = addItem;
    }

    /**
     * 退出删除状态
     */
    public void setGridStateNormal() {
        Level1Bean.scrollToRight = false;
        Level1Bean.scrollToLeft = false;
        setOnItemClickListener(new ItemClickListener());
        if (addItem != null) {// 添加add
            lstImageItem.add(addItem);
        }
        adapter.setGridState(GridAdapter.GRIDSTATENORMAL);
        adapter.notifyDataSetChanged();
        pageListener.moving();// 回调方法，用于存储移动后的九宫格顺序
    }

    class ItemClickListener implements OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0,// 九宫格，可以通过getChildAt获取每一个图标布局
                                View arg1,// 点击的图标布局
                                int arg2,// 点击的图标在九宫格中的位置
                                long arg3// 在九宫格控件中同arg2
        ) {
            try {
                if (addItem != null && addItem.equals(lstImageItem.get(arg2))) {// 针对添加按钮的特殊处理
                    pageListener.addClick();
                } else {
                    pageListener.itemClick(arg2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 监听点击事件
     *
     * @param pageListener
     */
    public void setPageListener(G_PageListener pageListener) {
        this.pageListener = pageListener;
    }

    public interface G_PageListener {
        /**
         * 监听点击新增按钮
         */
        void addClick();

        /**
         * 监听点击其他按钮
         *
         * @param arg2 按钮所在的位置
         */
        void itemClick(int arg2);

        /**
         * 监听删除事件
         *
         * @param delObj 删除的对象
         */
        void delClick(JSONObject delObj);

        /**
         * 监听移动事件
         */
        void moving();
    }

    public GridAdapter getAdapter() {
        return adapter;
    }
}