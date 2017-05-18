package com.ztesoft.level1.gridview;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ztesoft.level1.R;

/**
 * 九宫格用adapter
 *
 * @author wanghx2
 */
public abstract class GridAdapter extends BaseAdapter {
    public final static boolean ALLOWSDEL = true;// 可删除
    public final static boolean UNALLOWSDEL = false;// 不可删除
    public final static int GRIDSTATEDEL = 1;// 删除状态
    public final static int GRIDSTATENORMAL = 0;// 普通状态
    protected int gridState = GRIDSTATENORMAL;
    protected boolean allowsDelFlag = false;

    protected Context context;
    protected ArrayList<JSONObject> lstImageItem;
    protected Animation shake;

    public GridAdapter(Context context, ArrayList<JSONObject> lstImageItem) {
        this.context = context;
        this.lstImageItem = lstImageItem;
        shake = AnimationUtils.loadAnimation(context, R.anim.del_shake);
        LinearInterpolator lin = new LinearInterpolator();
        shake.setInterpolator(lin);
    }

    @Override
    public int getCount() {
        return lstImageItem.size();
    }

    @Override
    public Object getItem(int position) {
        return lstImageItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout layout = new RelativeLayout(context);
        layout.setGravity(Gravity.CENTER);
        try {
            JSONObject obj = lstImageItem.get(position);
            setLayoutStyle(layout, position);
            View v = addBodyView(obj);
            layout.addView(v);

            if (gridState == GRIDSTATEDEL) {
                v.startAnimation(shake);
            } else {
                v.clearAnimation();
            }
            if (allowsDelFlag && gridState == GRIDSTATEDEL) {
                if (isAllowsDel(obj)) {
                    ImageView iv = new ImageView(context);
                    iv.setImageResource(R.drawable.del_flag);
                    layout.addView(iv);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return layout;
    }

    /**
     * 返回单元格名称，用于删除提示
     *
     * @param position
     * @return
     */
    public abstract String getItemName(int position);

    /**
     * 设置单元格样式
     *
     * @param layout   单元格布局
     * @param position
     * @throws JSONException
     */
    public abstract void setLayoutStyle(RelativeLayout layout, int position) throws JSONException;

    /**
     * 绘制单元格主体
     *
     * @param obj
     * @return 单元格展示内容
     * @throws JSONException
     */
    public abstract View addBodyView(JSONObject obj) throws JSONException;

    /**
     * 该单元格是否允许删除
     *
     * @param obj
     * @return 该单元格是否允许删除
     */
    public abstract boolean isAllowsDel(JSONObject obj);

    /**
     * 获取九宫格数据ArrayList
     *
     * @return
     */
    public ArrayList<JSONObject> getLstImageItem() {
        return lstImageItem;
    }

    public int getGridState() {
        return gridState;
    }

    public void setGridState(int gridState) {
        this.gridState = gridState;
    }

    public void setAllowsDelFlag(boolean allowsDelFlag) {
        this.allowsDelFlag = allowsDelFlag;
    }

    public void setLstImageItem(ArrayList<JSONObject> lstImageItem) {
        this.lstImageItem = lstImageItem;
    }
}
