package com.ztesoft.level1.gesture;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class GestureGridView extends GridView {

    private Context ctx;
    private boolean touchFlag = false;
    public boolean state = true;
    private int[] images;
    private StringBuffer xxx = new StringBuffer();//输入的手势码内容
    private G_PageListener pageListener;//监听点击事件

    public GestureGridView(Context context, int[] images) {
        super(context);
        this.ctx = context;
        this.state = true;
        this.touchFlag = false;
        this.images = images;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        int position = pointToPosition(x, y);
        RelativeLayout rl = (RelativeLayout) getChildAt(position - getFirstVisiblePosition());
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xxx = new StringBuffer();
                if (position != AdapterView.INVALID_POSITION && state) {
                    pageListener.down();
                    touchFlag = true;
                    xxx.append(position + 1);
                    ImageView iv = (ImageView) rl.getChildAt(0);
                    iv.setImageResource(images[2]);// 更换图片
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //进入解锁状态
                if (position != AdapterView.INVALID_POSITION && touchFlag && xxx.indexOf("" + 
                        (position + 1)) == -1) {//未触发过
                    xxx.append(position + 1);
                    ImageView iv = (ImageView) rl.getChildAt(0);
                    iv.setImageResource(images[2]);// 更换图片
                }
                break;
            case MotionEvent.ACTION_UP:
                if (touchFlag) {
                    touchFlag = false;
                    pageListener.unlock(xxx.toString());
                }
                break;
        }
        return true;//不继续传递给gridview，屏蔽掉其自带的选中效果
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
         * 监听点击其他按钮
         *
         * @param num 按钮所在的位置
         */
        void unlock(String num);

        void down();
    }

    public GestureGridView getView() {
        ArrayList<JSONObject> lstImageItem = new ArrayList<JSONObject>();
        for (int i = 0; i < 9; i++) {
            JSONObject obj = new JSONObject();
            lstImageItem.add(obj);
        }
        ImageAdapter fa = new ImageAdapter(ctx, lstImageItem);
        this.setAdapter(fa);
        this.setNumColumns(3);
        return this;
    }

    private class ImageAdapter extends com.ztesoft.level1.gridview.GridAdapter {
        public ImageAdapter(Context context, ArrayList<JSONObject> lstImageItem) {
            super(context, lstImageItem);
        }

        @Override
        public View addBodyView(JSONObject obj) throws JSONException {
            ImageView functionImage = new ImageView(context);
            functionImage.setImageResource(images[0]);// 对功能按钮的图标进行赋值
            return functionImage;
        }

        @Override
        public boolean isAllowsDel(JSONObject obj) {
            return false;
        }

        @Override
        public String getItemName(int position) {
            return "";
        }

        @Override
        public void setLayoutStyle(RelativeLayout layout, int position) throws JSONException {
        }
    }
}
