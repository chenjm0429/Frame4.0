package com.ztesoft.ui.widget.radar;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.ztesoft.ui.widget.radar.PointTagView.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 文件名称 : CoordinateView
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 坐标系视图
 * <p>
 * 创建时间 : 2017/3/24 15:20
 * <p>
 */
public class CoordinateView extends RelativeLayout {

    private Context mContext;
    private int mWidth;

    private List<PointEntity> mPointEntities;

    private OnPointClickListener mOnPointClickListener;

    private List<Integer> mIndexList = new ArrayList<Integer>();

    public CoordinateView(Context context, int width, List<PointEntity> pointEntities) {
        super(context);

        this.mContext = context;
        this.mWidth = width;
        this.mPointEntities = pointEntities;

        mIndexList.clear();

        if (null == mPointEntities || mPointEntities.size() == 0) {
            return;
        }

        init();
    }

    private void init() {

        List<String> dataList = disposeData(mWidth);

        List<Integer> dataSource = new ArrayList<Integer>();
        dataSource.add(0);
        dataSource.add(1);
        dataSource.add(2);
        dataSource.add(3);
        dataSource.add(4);
        dataSource.add(5);
        dataSource.add(6);
        dataSource.add(7);

        int maxPosition = 7;

        boolean flag = true;
        while (flag) {

            int position = 0;
            if (maxPosition != 0) {
                Random random = new Random();
                position = random.nextInt(maxPosition);
            }

            mIndexList.add(dataSource.get(position));
            dataSource.remove(dataSource.get(position));

            maxPosition--;

            if (mIndexList.size() == mPointEntities.size()) {
                flag = false;
            }
        }

        for (int i = 0; i < mIndexList.size(); i++) {

            final PointEntity entity = mPointEntities.get(i);

            int index = mIndexList.get(i);
            String data = dataList.get(index);

            PointTagView pointView = new PointTagView(mContext);

            pointView.setDescription(entity.getName());
            int level = entity.getLevel();
            Level lvl = null;
            if (level == 3) {
                lvl = PointTagView.Level.WOREST;
            } else if (level == 2) {
                lvl = PointTagView.Level.MIDDLE;
            } else {
                lvl = PointTagView.Level.MILD;
            }
            pointView.setPointLevel(lvl);

            this.addView(pointView);

            int left = Integer.parseInt(data.split(",")[0]);
            int top = Integer.parseInt(data.split(",")[1]);

            pointView.resetMargin(left, top);

            pointView.startAnimation();

            pointView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mOnPointClickListener.onPointClick(entity);
                }
            });
        }
    }

    /**
     * 处理坐标点数据
     *
     * @param width
     * @return
     */
    private List<String> disposeData(int width) {

        List<String> returnData = new ArrayList<String>();

        String dataRes = "72,42;80,20;36,18;12,40;40,62;20,78;80,66;70,85";

        String[] dataArray = dataRes.split(";");

        for (int i = 0; i < dataArray.length; i++) {
            String[] point = dataArray[i].split(",");

            int pointX = width * Integer.parseInt(point[0]) / 100;
            int pointY = width * Integer.parseInt(point[1]) / 100;

            String ss = pointX + "," + pointY;

            returnData.add(ss);
        }

        return returnData;
    }

    public void setOnPointClickListener(
            OnPointClickListener mOnPointClickListener) {
        this.mOnPointClickListener = mOnPointClickListener;
    }

    public interface OnPointClickListener {
        public void onPointClick(PointEntity entity);
    }
}
