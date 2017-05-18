package com.ztesoft.level1.map;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.ztesoft.level1.Level1Util;

/***
 * 四级地图画法
 *
 * @author wangsq
 */
public class SJMapViewLayout extends View {
    //地图数据
    private Context context;
    //图片放大
    private float zoomScale[] = {1, 2, 2.5f};
    //字体放大
    private float folatScale[] = {1, 1.5f, 2f};
    private int[] defaultFontSize = {12, 15, 18, 20};
    private String defaultFontColor = "#000000";//默认字体的颜色

    private String boderColor[] = {"#4B4B4B", "#808080", "#A8A8A8", "#BFBFBF"};
    private int boderStock[] = {1, 2, 3, 4};

    //地图的默认颜色
    private String defaultColor = "#00000000";
    //气泡颜色
    private String qpColor = "#ff0000";
    //区域颜色
    public HashMap<String, JSONObject> regionColor = new HashMap<String, JSONObject>();
    //是否画气泡
    public boolean hasQP = false;
    private String selFontColor = "#ffffff";//选中的颜色
    private String selBorderColor = "#ffffff";//选中的颜色
    private int selBorderSize = 3;//选中的颜色

    //当前选中的区域
    private AreaPointBean curBean = null;

    //原始放大倍数
    private double scaleOld;
    //当前放大倍数
    private double scaleNew;
    private Paint mPaint = new Paint();
    private Paint qPaint = null;
    //宽度高度
    private int width;
    private int height;

    //左偏移量
    public int leftX = 0;
    public int leftY = 0;

    Bitmap newb = null;
    //最外层的名字\轮廓点\区域	
    public String parentPoints = null;
    public AreaPointBean parentRegion = null;
    //子区域点、区域
    private JSONArray childPoits;
    public AreaPointBean[] childRegion = null;
    //其他的辅助点可以为null
    private JSONArray otherPoits;
    private ArrayList<AreaPointBean[]> otherList = new ArrayList<AreaPointBean[]>();

    //原始x,y偏移量
    public int xxX;
    public int xxY;

    //区域指标值的最大,最小值
    double maxValue = 0;
    double minValue = 0;
    int minRadius = 20;
    int maxRadius = 50;
    int[][] radiusAtrr = {{50, 100}, {10, 50}, {5, 30}, {1, 5}};
    //是否可以移动
    private boolean moveFlag = false;
    //是否可以放大
    private boolean zoomFlag = false;

    //点击事件
    OnMapItemClickListener clickEvent;

    public SJMapViewLayout(Context context, String parentPoints, JSONArray childPoits,
                           HashMap<String, JSONObject> regionColor) {
        super(context);
        this.context = context;
        this.regionColor = regionColor;
        this.parentPoints = parentPoints;
        this.childPoits = childPoits;
    }

    /***
     * 是否有气泡
     *
     * @param hasQP
     */
    public void setHasQP(boolean hasQP) {
        this.hasQP = hasQP;
        if (qPaint == null) {
            qPaint = new Paint();
            qPaint.setAntiAlias(true);
            qPaint.setColor(Color.parseColor(qpColor));
            qPaint.setAlpha(50);
            qPaint.setTextAlign(Align.CENTER);
        }
        Iterator iter = regionColor.entrySet().iterator();
        int i = 0;
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            JSONObject val = (JSONObject) entry.getValue();
            if (val.optString("kpiValue", "").trim().length() > 0) {
                if (maxValue < val.optDouble("kpiValue")) {
                    maxValue = val.optDouble("kpiValue");
                }
                if (i == 0) {
                    minValue = val.optDouble("kpiValue");
                }
                if (minValue > val.optDouble("kpiValue")) {
                    minValue = val.optDouble("kpiValue");
                }
                i++;
            }
        }
        invalidate();
    }

    /***
     * 多级地图其他层的坐标点
     *
     * @param otherPoits
     */
    public void setOtherPoits(JSONArray otherPoits) {
        this.otherPoits = otherPoits;
    }

    /***
     * 计算初始放大倍数,及偏移量
     */
    public void getScaleXY() {
        int maxX = 0;
        int minX = 0;
        int maxY = 0;
        int minY = 0;
        //计算放大倍数
        if (this.parentPoints != null) {
            String poits[] = this.parentPoints.split(":");
            String areaPoits[] = poits[3].split(",");
            String outerCityArray[] = areaPoits;
            for (int a = 0; a < outerCityArray.length; a = a + 2) {
                int x = Integer.parseInt(outerCityArray[a]);
                int y = Integer.parseInt(outerCityArray[a + 1]);
                if (a == 0) {
                    minX = x;
                    minY = y;
                }
                if (maxX < x) {
                    maxX = x;
                }
                if (minX > x) {
                    minX = x;
                }
                if (maxY < y) {
                    maxY = y;
                }
                if (minY > y) {
                    minY = y;
                }
            }
        } else {
            for (int i = 0; i < childPoits.length(); i++) {
                String poits[] = childPoits.optJSONObject(i).optString("mapPosStr").split(":");
                String areaPoits[] = poits[3].split(",");
                String outerCityArray[] = areaPoits;
                for (int a = 0; a < outerCityArray.length; a = a + 2) {
                    int x = Integer.parseInt(outerCityArray[a]);
                    int y = Integer.parseInt(outerCityArray[a + 1]);
                    if (i == 0 && a == 0) {
                        minX = x;
                        minY = y;
                    }
                    if (maxX < x) {
                        maxX = x;
                    }
                    if (minX > x) {
                        minX = x;
                    }
                    if (maxY < y) {
                        maxY = y;
                    }
                    if (minY > y) {
                        minY = y;
                    }
                }
            }
            this.parentPoints = "xx:0:0:" + minX + "," + minY + "," + maxX + "," + minY + "," +
                    maxX + "," + maxY + "," + minX + "," + maxY + "," + minX + "," + minY;
        }
        double scaleXOld = 1.00000000000000 * (width - 10) / (maxX - minX);
        double scaleYOld = 1.00000000000000 * (height - 10) / (maxY - minY);
        if (scaleXOld < scaleYOld) {
            scaleOld = scaleXOld;
        } else {
            scaleOld = scaleYOld;
        }
        leftX = -minX;
        leftY = -minY;
        xxX = (int) (width - 10 - (maxX - minX) * scaleOld) / 2;
        xxY = (int) (height - 10 - (maxY - minY) * scaleOld) / 2;
    }

    public void init() {
        scaleNew = scaleOld * zoomScale[cureScaleOrder];
        mPaint.setAntiAlias(true);
        if (parentPoints != null) {
            parentRegion = initOneMap(this.parentPoints);
            if (otherPoits != null) {
                otherList = new ArrayList<AreaPointBean[]>();
                for (int i = 0; i < otherPoits.length(); i++) {
                    otherList.add(initTwoMap(otherPoits.optJSONArray(i)));
                }
            }
            childRegion = initTwoMap(this.childPoits);
            drawCanvas();
        }
    }

    /***
     * 画图保存成图片
     */
    public void drawCanvas() {
        if (newb == null && width > 0 && height > 0) {
            newb = Bitmap.createBitmap((int) (width * zoomScale[cureScaleOrder]), (int) (height *
                    zoomScale[cureScaleOrder]), Config.ARGB_8888);
            Canvas canvas = new Canvas(newb);
            AreaPointBean[] tempRegion = null;
            int len = otherList.size();
            int colorIndex = 0;
            if (len == 0) {
                colorIndex = 3;
            } else {
                colorIndex = 3 - len;
            }
            String cityAtrr[] = null;
            //画轮廓线
            for (int i = 0; i < childRegion.length; i++) {
                if (childRegion[i] == null) continue;
                cityAtrr = null;
                if (childRegion[i].getCityCode() != null) {
                    cityAtrr = childRegion[i].getCityCode().split(":");
                }
                if (cityAtrr != null && regionColor.containsKey(cityAtrr[cityAtrr.length - 1])) {
                    drawPath(canvas, childRegion[i].getCityRegion(), boderStock[colorIndex],
                            boderColor[colorIndex], ((JSONObject) regionColor.get
                                    (cityAtrr[cityAtrr.length - 1])).optString("color",
                                    defaultColor));
                } else if (regionColor.containsKey(childRegion[i].getCityName())) {
                    drawPath(canvas, childRegion[i].getCityRegion(), boderStock[colorIndex],
                            boderColor[colorIndex], ((JSONObject) regionColor.get(childRegion[i]
                                    .getCityName())).optString("color", defaultColor));
                } else {
                    drawPath(canvas, childRegion[i].getCityRegion(), boderStock[colorIndex],
                            boderColor[colorIndex], defaultColor);
                }
            }
            for (int ix = 0; ix < otherList.size(); ix++) {
                tempRegion = otherList.get(ix);
                for (int i = 0; i < tempRegion.length; i++) {
                    if (tempRegion[i] == null) continue;
                    drawPath(canvas, tempRegion[i].getCityRegion(), boderStock[4 - otherList.size
                            () + ix], boderColor[4 - otherList.size() + ix], null);
                }
            }

            //写描述
            for (int i = 0; i < childRegion.length; i++) {
                if (childRegion[i] == null) continue;
                cityAtrr = null;
                if (childRegion[i].getCityCode() != null) {
                    cityAtrr = childRegion[i].getCityCode().split(":");
                }
                if (otherList.size() != 2) {
                    boolean flag = regionColor.containsKey(cityAtrr[cityAtrr.length - 1]) ||
                            regionColor.containsKey(childRegion[i].getCityName());
                    drawDesc(canvas, childRegion[i], defaultFontSize[colorIndex], flag);
                }
            }

            if (hasQP) {
                for (int i = 0; i < childRegion.length; i++) {
                    if (childRegion[i] == null) continue;
                    cityAtrr = null;
                    if (childRegion[i].getCityCode() != null) {
                        cityAtrr = childRegion[i].getCityCode().split(":");
                    }
                    //是否显示气泡
                    if (cityAtrr != null) {
                        drawQP(canvas, childRegion[i], otherList.size(), regionColor.get
                                (cityAtrr[cityAtrr.length - 1]));
                    }
                }
            }
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();//存储
        }
    }

    //单个区域的画法
    private AreaPointBean initOneMap(String areaPoints) {
        String poits[] = areaPoints.split(":");
        String areaPoits[] = poits[3].split(",");
        String pathTemp[] = null;
        pathTemp = areaPoits;
        Path path = new Path();
        int x = Integer.parseInt(pathTemp[0]);
        int y = Integer.parseInt(pathTemp[1]);
        path.moveTo((int) ((x + leftX) * scaleNew), (int) ((y + leftY) * scaleNew));
        for (int i = 0, j = 0; i < pathTemp.length; i = i + 2, j++) {
            x = Integer.parseInt(pathTemp[i]);
            y = Integer.parseInt(pathTemp[i + 1]);
            path.lineTo((int) ((x + leftX) * scaleNew), (int) ((y + leftY) * scaleNew));
        }
        x = Integer.parseInt(pathTemp[0]);
        y = Integer.parseInt(pathTemp[1]);
        path.lineTo((int) ((x + leftX) * scaleNew), (int) ((y + leftY) * scaleNew));
        //画区域
        RectF r = new RectF();
        path.computeBounds(r, true);
        Region rg = new Region();
        rg.setPath(path, new Region((int) r.left, (int) r.top, (int) r.right, (int) r.bottom));
        AreaPointBean bean = new AreaPointBean();
        bean.setCityName(poits[0]);
        bean.setDesX((int) ((Integer.parseInt(poits[1]) + leftX) * scaleNew));
        bean.setDesY((int) ((Integer.parseInt(poits[2]) + leftY) * scaleNew));
        bean.setCityRegion(rg);
        return bean;
    }

    //数组
    private AreaPointBean[] initTwoMap(JSONArray areaPoints) {
        AreaPointBean regions[] = new AreaPointBean[areaPoints.length()];
        JSONObject mapObj = null;
        try {
            for (int i = 0; i < areaPoints.length(); i++) {
                mapObj = areaPoints.getJSONObject(i);
                if (!mapObj.has("mapPosStr")) {
                    continue;
                }
                AreaPointBean xxBean = initOneMap(mapObj.getString("mapPosStr"));
                xxBean.setProvCode(mapObj.optString("provCode", ""));
                xxBean.setCityCode(mapObj.optString("cityCode", ""));
                xxBean.setCountyCode(mapObj.optString("countyCode", ""));
                xxBean.setThirdLine(mapObj.optString("thirdLine", ""));
                regions[i] = xxBean;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return regions;
    }

    public AreaPointBean[] getAreaRegion() {
        return childRegion;
    }

    private int PADDINGX = 0;
    private int PADDINGY = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.width == 0) {
            this.width = this.getWidth();
            this.height = this.getHeight();
            getScaleXY();
            if (this.width > 0)
                init();
        }
        canvas.save();
        if (zoomScale[cureScaleOrder] == 1) {
            canvas.translate(-PADDINGX + xxX, -PADDINGY + xxY);
        } else {
            canvas.translate(-PADDINGX, -PADDINGY);
        }
        canvas.drawBitmap(newb, 0, 0, mPaint);
        if (curBean != null) {
            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.parseColor(selBorderColor));
            mPaint.setStrokeWidth(selBorderSize);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(curBean.getCityRegion().getBoundaryPath(), mPaint);

            if (otherList.size() == 2) {
                drawDesc(canvas, curBean, defaultFontSize[2], true);
            }
            if (clickEvent != null && curBean != null) {
                clickEvent.onItemCanvasClick(curBean, canvas);
            }
        }
        canvas.restore();
    }

    //画路径
    public void drawPath(Canvas canvas, Region provRegion, int stockWidth, String stokeColor,
                         String fillColor) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor(stokeColor));
        mPaint.setStrokeWidth(stockWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(provRegion.getBoundaryPath(), mPaint);
        //画区域填充
        if (fillColor != null) {
            mPaint.setStrokeWidth(stockWidth);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.parseColor(fillColor));
            canvas.drawPath(provRegion.getBoundaryPath(), mPaint);
        }
    }

    //画描述
    public void drawDesc(Canvas canvas, AreaPointBean areaBean, int defaultFontSize, boolean flag) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        if (!flag) {
            mPaint.setColor(Color.parseColor(defaultFontColor));
        } else {
            mPaint.setColor(Color.parseColor(selFontColor));
        }
        mPaint.setTextSize(Level1Util.dip2px(context, defaultFontSize) *
                folatScale[cureScaleOrder]);
        mPaint.setTextAlign(Align.CENTER);
        if (areaBean.getDesX() != 0 && areaBean.getDesY() != 0) {
            canvas.drawText(areaBean.getCityName(), areaBean.getDesX(), areaBean.getDesY(), mPaint);
        }
    }

    /***
     * 画气泡
     *
     * @param canvas
     * @param areaBean
     */
    public void drawQP(Canvas canvas, AreaPointBean areaBean, int level, JSONObject dataObj) {

        int attr[] = radiusAtrr[level];
        int radius = attr[0];
        if (dataObj != null && dataObj.optString("kpiValue").trim().length() > 0 && maxValue -
                minValue != 0) {
            radius = (int) ((dataObj.optDouble("kpiValue") - minValue) * (attr[1] - attr[0]) /
                    (maxValue - minValue)) + attr[0];
        }
        if (areaBean.getDesX() != 0 && areaBean.getDesY() != 0) {
            canvas.drawCircle(areaBean.getDesX(), areaBean.getDesY(), radius, qPaint);
        }
    }

    float oldDist;
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;
    int isFinish = 0;//1：开始 2：移动 0:结束
    int cureScaleOrder = 0;
    float mLastMotionX = 0;
    float mLastMotionY = 0;
    boolean signalTouch = true;
    long downTime = 0;

    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            //设置拖拉模式  
            case MotionEvent.ACTION_DOWN:
                Date date = new Date();
                downTime = date.getTime();
                signalTouch = true;
                mode = DRAG;
                mLastMotionX = x;
                mLastMotionY = y;
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (clickEvent != null) {
                    Date da = new Date();
                    clickEvent.onbgClick();
                    if (signalTouch && mode == DRAG) {
                        curBean = getMapClickRange(event);
                        if (curBean != null) {
                            //触发点击事件
                            if (da.getTime() - downTime < ViewConfiguration.getLongPressTimeout()) {
                                clickEvent.onItemClick(curBean);
                            } else {//触发长按事件
                                clickEvent.onlongClick(curBean);
                            }
                            downTime = 0;
                        }
                        super.invalidate();
                    }
                }
                mode = NONE;
                isFinish = 0;
                break;
            //设置多点触摸模式  
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                isFinish = 1;
                mode = ZOOM;
                break;
            //若为DRAG模式，则点击移动
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG && Math.abs(mLastMotionX - x) > 5 && Math.abs(mLastMotionY - y)
                        > 5 && moveFlag) {
                    signalTouch = false;
                    PADDINGX += (int) (mLastMotionX - x);
                    PADDINGY += (int) (mLastMotionY - y);
                    mLastMotionX = x;
                    mLastMotionY = y;
                    super.invalidate();
                }
                //若为ZOOM模式，则多点触摸缩放  
                else if (mode == ZOOM && zoomFlag) {
                    float newDist = spacing(event);
                    if (oldDist < newDist && newDist - oldDist > 10f) {//放大
                        if (cureScaleOrder < 2) {
                            if (isFinish == 1) {
                                cureScaleOrder++;
                                PADDINGX = (int) (x * zoomScale[cureScaleOrder] - x);
                                PADDINGY = (int) (y * zoomScale[cureScaleOrder] - y);
                                PADDINGX = PADDINGY = 0;
                                curBean = null;
                                invalidate();
                                isFinish = 2;
                            }
                        }
                    } else if (oldDist > newDist && oldDist - newDist > 10f) {//缩小
                        if (cureScaleOrder > 0) {
                            if (isFinish == 1) {
                                cureScaleOrder--;
                                PADDINGX = (int) (x * zoomScale[cureScaleOrder] - x);
                                PADDINGY = (int) (y * zoomScale[cureScaleOrder] - y);
                                PADDINGX = PADDINGY = 0;
                                curBean = null;
                                invalidate();
                                isFinish = 2;
                            }
                        }
                    }
                }
                break;
        }
        return true;
    }

    //计算移动距离  
    private float spacing(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            float x = event.getX(event.getPointerId(0)) - event.getX(event.getPointerId(1));
            float y = event.getY(event.getPointerId(0)) - event.getY(event.getPointerId(1));
            return FloatMath.sqrt(x * x + y * y);
        }
        return 0;
    }

    public AreaPointBean getMapClickRange(MotionEvent event) {
        float x = event.getX() + PADDINGX;
        float y = event.getY() + PADDINGY;
        if (zoomScale[cureScaleOrder] == 1) {
            x = event.getX() + PADDINGX - xxX;
            y = event.getY() + PADDINGY - xxY;
        }
        if (parentRegion.getCityRegion().contains((int) x, (int) y)) {
            if (childRegion != null) {
                for (int j = 0; j < childRegion.length; j++) {
                    if (childRegion[j] == null) continue;
                    if (childRegion[j].getCityRegion().contains((int) x, (int) y)) {
                        return childRegion[j];
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void invalidate() {
        if (newb != null) {
            newb.recycle();
            System.gc();
            newb = null;
        }
        init();
        super.invalidate();
    }

    public interface OnMapItemClickListener {
        //点击事件
        void onItemCanvasClick(AreaPointBean ab, Canvas canvas);

        void onItemClick(AreaPointBean ab);

        void onlongClick(AreaPointBean ab);

        void onbgClick();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;
        getScaleXY();
        invalidate();
    }

    public void setRegionColor(HashMap regionColor) {
        this.regionColor = regionColor;
        invalidate();
    }

    public class AreaPointBean {
        public String cityName;
        public String provCode;
        public String cityCode;
        public String countyCode;
        public String ThirdLine;
        public Region cityRegion;
        public int desX;
        public int desY;

        public String getThirdLine() {
            return ThirdLine;
        }

        public void setThirdLine(String thirdLine) {
            ThirdLine = thirdLine;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public Region getCityRegion() {
            return cityRegion;
        }

        public void setCityRegion(Region cityRegion) {
            this.cityRegion = cityRegion;
        }

        public int getDesX() {
            return desX;
        }

        public void setDesX(int desX) {
            this.desX = desX;
        }

        public int getDesY() {
            return desY;
        }

        public void setDesY(int desY) {
            this.desY = desY;
        }

        public String getProvCode() {
            return provCode;
        }

        public void setProvCode(String provCode) {
            this.provCode = provCode;
        }

        public String getCityCode() {
            return cityCode;
        }

        public void setCityCode(String cityCode) {
            this.cityCode = cityCode;
        }

        public String getCountyCode() {
            return countyCode;
        }

        public void setCountyCode(String countyCode) {
            this.countyCode = countyCode;
        }
    }

    public void setCurBean(AreaPointBean curBean) {
        this.curBean = curBean;
    }

    public void setMoveFlag(boolean moveFlag) {
        this.moveFlag = moveFlag;
    }

    public void setZoomFlag(boolean zoomFlag) {
        this.zoomFlag = zoomFlag;
    }

    public void setClickEvent(OnMapItemClickListener clickEvent) {
        this.clickEvent = clickEvent;
    }

    public void setDefaultFontSize(int[] defaultFontSize) {
        this.defaultFontSize = defaultFontSize;
    }

    public void setDefaultFontColor(String defaultFontColor) {
        this.defaultFontColor = defaultFontColor;
    }

    public void setDefaultColor(String defaultColor) {
        this.defaultColor = defaultColor;
    }

    public void setSelFontColor(String selFontColor) {
        this.selFontColor = selFontColor;
    }

    public void setQpColor(String qpColor) {
        this.qpColor = qpColor;
    }

    public void setSelBorderColor(String selBorderColor) {
        this.selBorderColor = selBorderColor;
    }

    public void setSelBorderSize(int selBorderSize) {
        this.selBorderSize = selBorderSize;
    }

    /***
     * 目前支持4级地图，必须同时设置4级地图的设置
     *
     * @param boderColor
     */
    public void setBoderColor(String[] boderColor) {
        if (boderColor.length > 3) {
            this.boderColor = boderColor;
        }
    }

    public void setBoderStock(int[] boderStock) {
        if (boderStock.length > 3) {
            this.boderStock = boderStock;
        }
    }
}