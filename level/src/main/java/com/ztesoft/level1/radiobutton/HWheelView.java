package com.ztesoft.level1.radiobutton;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.Level1Util;
import com.ztesoft.level1.R;
import com.ztesoft.level1.chart.ColorDefault;
import com.ztesoft.level1.radiobutton.util.OnWheelScrollListener;
import com.ztesoft.level1.util.NumericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/***
 * 地市横向滚轮控件
 *
 * @author wangsq
 *         [{"text":"","value":"","code":""}]
 */
public class HWheelView extends View {
    //动画时间
    private static final int SCROLLING_DURATION = 400;

    /**
     * Minimum delta for scrolling
     */
    private static final int MIN_DELTA_FOR_SCROLLING = 1;

    //默认字体的颜色
    private int ITEMS_TEXT_COLOR = 0xFF666666;
    //选中的颜色
    private int ITEMS_TEXT_COLOR_SELECT = 0xFF666666;
    //面积图颜色
    private int ITEMS_AREA_COLOR = Color.parseColor(ColorDefault.colors[0]);

    /**
     * Text size
     */
    private int TEXT_SIZE = Level1Util.getSpSize(14);

    // Wheel Values
    private JSONArray adapter = null;
    private int currentItem = 0;

    // Widths
    private int itemsWidth = 60;
    // Text paints
    private Paint itemsPaint;
    private Paint paint;
    //	private String label;
    private Drawable centerDrawable;    //中垂线(中间白色线)
    private Drawable centerBDrawable;   //滑块

    // Scrolling
    private boolean isScrollingPerformed;
    private int scrollingOffset;
    // Scrolling animation
    private GestureDetector gestureDetector;
    private Scroller scroller;
    private int lastScrollX;

    // Listeners
    private List<OnHWheelChangedListener> changingListeners = new
            LinkedList<OnHWheelChangedListener>();
    private List<OnHWheelScrollListener> scrollingListeners = new
            LinkedList<OnHWheelScrollListener>();

    private int centerWidth = 10;
    private Drawable itemDrawable;
    private Drawable itemDrawableSel;
    private int itemHeight = 0;

    //显示横坐标的值
    private List<String> textList = new ArrayList<String>();
    //横坐标的数值
    private List<String> dataList = new ArrayList<String>();
    //坐标系
    private int posAtrr[] = null;
    double maxValue = 0;
    double minValue = 0;
    //是否是多指
    boolean isMuchPointer = false;
    //显示的格式
    String xshowFormat = "";
    //多指X的显示格式
    String muchPointerXFormat = "";

    Context context;
    //是否有轨道
    private boolean hasTrack = true;
    //-1：不画图,0：面积图 1：柱图
    private int chartType = 0;
    //画下面的面积、柱图时 是否去除第一个值
    private boolean lostFirst = false;
    //对象ID
    private String elid = "";

    private int visibleItems = 30;

    private int maxTextLen = 0;
    //多手指功能
    private boolean muchPoitFun = false;

    //过滤汇总(-1为不过滤，0表示过滤第一条，其它表示过滤最后一条)
    private int filterIndex = -1;

    /**
     * Constructor
     */
    public HWheelView(Context context) {
        super(context);
        ITEMS_TEXT_COLOR_SELECT = ColorDefault.applyDark(Color.parseColor("#0091d5"), 64);
        this.context = context;
        initData(context);
    }

    /**
     * Initializes class data
     *
     * @param context the context
     */
    private void initData(Context context) {
        gestureDetector = new GestureDetector(context, gestureListener);
        gestureDetector.setIsLongpressEnabled(false);
        scroller = new Scroller(context);
        if (itemsPaint == null) {
            itemsPaint = new Paint();
            itemsPaint.setAntiAlias(true);
            itemsPaint.setTextSize(TEXT_SIZE);
            itemsPaint.setTypeface(Typeface.DEFAULT);
        }
        if (paint == null) {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.parseColor("#666666"));
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.STROKE);
        }

        if (centerDrawable == null) {
            centerDrawable = getContext().getResources().getDrawable(R.drawable.icon_fixed_needle);
        }
        if (centerBDrawable == null) {
            centerBDrawable = getContext().getResources().getDrawable(R.drawable.icon_fixed_button);
        }
        if (itemDrawable == null) {
            itemDrawable = getContext().getResources().getDrawable(R.drawable.icon_scale_middle);
            itemDrawable.setColorFilter(Color.parseColor("#0091d5"), Mode.SRC_ATOP);
            itemDrawable.setAlpha(150);
        }

        if (itemDrawableSel == null) {
            itemDrawableSel = getContext().getResources().getDrawable(R.drawable
                    .bg_choosedate_diff);
        }
        Bitmap middle = BitmapFactory.decodeResource(getResources(), R.drawable.icon_scale_middle);

        itemHeight = middle.getHeight();
        centerWidth = BitmapFactory.decodeResource(getResources(), R.drawable.icon_fixed_button)
                .getWidth();
        itemsWidth = middle.getWidth() * 2;
        this.setBackgroundColor(Color.TRANSPARENT);
    }

    /**
     * Gets wheel adapter
     *
     * @return the adapter
     */
    public JSONArray getAdapter() {
        return adapter;
    }

    /**
     * 设置数据源
     *
     * @param adapter     数据源
     * @param filterIndex 过滤汇总(-1为不过滤，0表示过滤第一条，其它表示过滤最后一条)
     * @throws JSONException
     */
    public void setAdapter(JSONArray adapter, int filterIndex) {
        this.filterIndex = filterIndex;
        if (filterIndex == -1) {
            this.adapter = adapter;
        } else if (filterIndex == 0) {
            this.adapter = arrayRemoveAt(adapter, 0);
        } else {
            this.adapter = arrayRemoveAt(adapter, adapter.length() - 1);
        }
        textList = new ArrayList<String>();
        dataList = new ArrayList<String>();
        filterData();
        invalidateLayouts();
        invalidate();
    }

    /**
     * json数组移除某个下标为index的json串后，剩下的数组
     *
     * @param array 原数组
     * @param index 下标(0表示第一个json串)
     * @return
     * @throws JSONException
     */
    private JSONArray arrayRemoveAt(JSONArray array, int index) {
        JSONArray jsArray = new JSONArray();
        try {
            for (int i = 0; i < array.length(); i++) {
                if (i == index)
                    continue;

                jsArray.put(array.getJSONObject(i));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsArray;
    }

    public void filterData() {
        Object obj = null;
        JSONObject temp = null;
        int count = adapter.length();

        String showText = "";
        for (int i = 0; i < count; i++) {
            obj = adapter.optJSONObject(i);
            if (obj instanceof String) {
                showText = ((String) obj);
            } else if (obj instanceof JSONObject) {
                temp = (JSONObject) obj;
                showText = (temp.optString("text", ""));
                if (temp.has("value")) {
                    String x = temp.optString("value", "");
                    if (x.trim().length() == 0) {
                        x = "0";
                    }
                    dataList.add(x);
                }
            }
            if ("MM-dd".equals(xshowFormat)) {
                if (showText.length() > 4) {
                    showText = showText.substring(4, 6) + "-" + showText.substring(6, 8);
                } else {
                    showText = showText.substring(0, 2) + "-" + showText.substring(2, 4);
                }
            } else if ("yyyy-MM".equals(xshowFormat)) {
                showText = showText.substring(0, 4) + "-" + showText.substring(4, 6);
            }
            textList.add(showText);
            if (showText.length() > maxTextLen) {
                maxTextLen = showText.length();
            }
        }
        if (dataList.size() > 0) {
            maxValue = Double.parseDouble(dataList.get(0));
            if (lostFirst && dataList.size() > 1) {
                maxValue = Double.parseDouble(dataList.get(1));
            }
            minValue = maxValue;
            double tempValue = 0;
            int i = 0;
            if (lostFirst) {
                i = 1;
            }
            for (; i < dataList.size(); i++) {
                tempValue = Double.parseDouble(dataList.get(i));
                if (tempValue > maxValue) {
                    maxValue = tempValue;
                }
                if (tempValue < minValue) {
                    minValue = tempValue;
                }
            }
        }
        minValue = minValue * 0.8;
    }

    /**
     * Adds wheel changing listener
     *
     * @param listener the listener
     */
    public void addChangingListener(OnHWheelChangedListener listener) {
        changingListeners.add(listener);
    }

    /**
     * Removes wheel changing listener
     *
     * @param listener the listener
     */
    public void removeChangingListener(OnHWheelChangedListener listener) {
        changingListeners.remove(listener);
    }

    /**
     * Notifies changing listeners
     *
     * @param oldValue the old wheel value
     * @param newValue the new wheel value
     */
    protected void notifyChangingListeners(int oldValue, int newValue) {
        for (OnHWheelChangedListener listener : changingListeners) {
            listener.onChanged(this, oldValue, newValue);
        }
    }

    /**
     * Adds wheel scrolling listener
     *
     * @param listener the listener
     */
    public void addScrollingListener(OnHWheelScrollListener listener) {
        scrollingListeners.add(listener);
    }

    /**
     * Removes wheel scrolling listener
     *
     * @param listener the listener
     */
    public void removeScrollingListener(OnWheelScrollListener listener) {
        scrollingListeners.remove(listener);
    }

    /**
     * Notifies listeners about starting scrolling
     */
    protected void notifyScrollingListenersAboutStart() {
        for (OnHWheelScrollListener listener : scrollingListeners) {
            listener.onScrollingStarted(this);
        }
    }

    /**
     * Notifies listeners about ending scrolling
     */
    protected void notifyScrollingListenersAboutEnd() {
        for (OnHWheelScrollListener listener : scrollingListeners) {
            listener.onScrollingFinished(this);
        }
    }

    /**
     * Gets current value
     *
     * @return the current value
     */
    public int getCurrentItem() {
        return currentItem;
    }

    public String getCurrentValue() {
        if (adapter.optJSONObject(currentItem).has("code")) {
            return adapter.optJSONObject(currentItem).optString("code");
        }
        return adapter.optJSONObject(currentItem).optString("text");
    }

    /**
     * Sets the current item. Does nothing when index is wrong.
     *
     * @param index    the item index
     * @param animated the animation flag
     */
    public void setCurrentItem(int index, boolean animated) {
        if (adapter == null || adapter.length() == 0) {
            return; // throw?
        }
        if (index < 0 || index >= adapter.length()) {
            return; // throw?
        }
        if (index != currentItem) {
            if (animated) {
                scroll(index - currentItem, SCROLLING_DURATION);
            } else {
                invalidateLayouts();

                int old = currentItem;
                currentItem = index;

                if (filterIndex == 0)
                    notifyChangingListeners(old + 1, currentItem + 1);
                else
                    notifyChangingListeners(old, currentItem);

                invalidate();
            }
        }
    }

    /**
     * Sets the current item w/o animation. Does nothing when index is wrong.
     *
     * @param index the item index
     */
    public void setCurrentItem(int index) {
        setCurrentItem(index, false);
    }

    /**
     * @param code
     * @param defaultIndex:默认选中
     */
    public void setCurrentValue(String code, int defaultIndex) {
        if (code == null) code = "";
        if (adapter.length() > 0) {
            boolean hasValue = false;
            if (adapter.optJSONObject(0).has("code")) {
                for (int i = 0; i < adapter.length(); i++) {
                    if (code.equals(adapter.optJSONObject(i).optString("code"))) {
                        setCurrentItem(i, false);
                        hasValue = true;
                        break;
                    }
                }
            } else {
                for (int i = 0; i < adapter.length(); i++) {
                    if (code.equals(adapter.optJSONObject(i).optString("text"))) {
                        setCurrentItem(i, false);
                        hasValue = true;
                        break;
                    }
                }
            }
            if (!hasValue) {
                setCurrentItem(defaultIndex, false);
            }
        }

    }

    /**
     * Invalidates layouts
     */
    private void invalidateLayouts() {
        scrollingOffset = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (xshowFormat.trim().length() == 0) {
            StringBuffer s = new StringBuffer();
            for (int i = 0; i < maxTextLen; i++) {
                s.append("哈");
            }
            if (itemsPaint.measureText(s.toString()) > itemsWidth) {
                itemsWidth = (int) itemsPaint.measureText(s.toString()) + 10;
            }
        }

        if (itemsWidth > 0) {
            canvas.save();
            canvas.translate(0, 0);
            //画标度尺
            drawItems(canvas);
            canvas.restore();
        }
        drawCenterRect(canvas);
    }

    /***
     * 画标度尺
     *
     * @param canvas
     */
    private void drawItems(Canvas canvas) {
        canvas.save();
        int addItems = visibleItems / 2 + 1;

        int left = currentItem * itemsWidth - getWidth() / 2;
        canvas.translate(-left + scrollingOffset, 0);
        itemsPaint.setColor(ITEMS_TEXT_COLOR);
//		itemsPaint.drawableState = getDrawableState();		
        int count = textList.size();
        FontMetrics fm = itemsPaint.getFontMetrics();
        int fh = (int) Math.ceil(fm.descent - fm.ascent);
//		for(int i=0;i<count;i++){
//			if(i==currentItem){
//				TextPaint itemsPaintSel = new TextPaint(Paint.ANTI_ALIAS_FLAG
//						| Paint.FAKE_BOLD_TEXT_FLAG);
//				itemsPaintSel.setTextSize(TEXT_SIZE);
//				itemsPaintSel.setColor(ITEMS_TEXT_COLOR_SELECT);
//				itemsPaintSel.setShadowLayer(3, 2, 2, 0xa0ffffff); 
//				canvas.drawText(textList.get(i), i*itemsWidth-itemsPaint.measureText(textList.get
// (i))/2, (int)(fh*1.5),  itemsPaintSel);
//			}else{
//				itemsPaint.setColor(ITEMS_TEXT_COLOR);
//				canvas.drawText(textList.get(i), i*itemsWidth-itemsPaint.measureText(textList.get
// (i))/2, (int)(fh*1.5),  itemsPaint);
//			}
//		}


        for (int i = currentItem - addItems; i <= currentItem + addItems; i++) {
            if (adapter != null && adapter.length() > 0) {
                if (i >= 0 && i < adapter.length()) {
                    itemsPaint.setColor(ITEMS_TEXT_COLOR);
                    if (isScrollingPerformed) {
                        canvas.drawText(textList.get(i), i * itemsWidth - itemsPaint.measureText
                                (textList.get(i)) / 2, (int) (fh * 1.5), itemsPaint);
                    } else {
                        if (i != currentItem)
                            canvas.drawText(textList.get(i), i * itemsWidth - itemsPaint
                                            .measureText(textList.get(i)) / 2, (int) (fh * 1.5),
                                    itemsPaint);
                    }

                }
            }
        }

        itemsPaint.setColor(ITEMS_AREA_COLOR);
        int offset = centerWidth / 2;
        if (dataList.size() > 0) {
            int maxHeight = (int) ((this.getHeight() - fh * 2 - itemHeight) * 0.9);
            Path p = new Path();
            p.moveTo(0, getHeight());
            int curY = 0;
            posAtrr = new int[dataList.size()];
            for (int i = 0; i < dataList.size(); i++) {
                posAtrr[i] = i * itemsWidth + getWidth() / 2 - currentItem * itemsWidth;
                if (lostFirst && i == 0) {
                    continue;
                }
                if (maxValue == minValue && maxValue == 0) {
                    curY = 0;
                } else if (maxValue == minValue) {
                    curY = maxHeight;
                } else {
                    curY = (int) ((Double.parseDouble(dataList.get(i)) - minValue) / (maxValue -
                            minValue) * maxHeight);
                }
                p.lineTo(i * itemsWidth, getHeight() - curY);
                //画柱图
                if (chartType == 1) {
                    Path bp = new Path();
                    bp.moveTo(i * itemsWidth - (int) (itemsWidth * 0.3) + offset, getHeight() -
                            curY);
                    bp.lineTo(i * itemsWidth + (int) (itemsWidth * 0.3) + offset, getHeight() -
                            curY);
                    bp.lineTo(i * itemsWidth + (int) (itemsWidth * 0.3) + offset, getHeight());
                    bp.lineTo(i * itemsWidth - (int) (itemsWidth * 0.3 * 2) + offset, getHeight());
                    bp.lineTo(i * itemsWidth - (int) (itemsWidth * 0.3 * 2) + offset, getHeight()
                            - curY);
                    canvas.drawPath(bp, itemsPaint);
                }
            }
            if (chartType == 0) {//画面积图
                p.lineTo((dataList.size() - 1) * itemsWidth, getHeight());
                p.lineTo(0, getHeight());
                canvas.drawPath(p, itemsPaint);
            }
        }
        itemsPaint.setColor(ITEMS_TEXT_COLOR);
        canvas.restore();
    }

    /***
     * 画中间的指针
     *
     * @param canvas
     */
    private void drawCenterRect(Canvas canvas) {
        int center = getWidth() / 2;
        int offset = centerWidth / 2;
        FontMetrics fm = itemsPaint.getFontMetrics();
        int fh = (int) Math.ceil(fm.descent - fm.ascent);

        //画中心选中的		
        Paint itemsPaintSel = new Paint();
//		itemsPaintSel.setColor(ITEMS_TEXT_COLOR_SELECT);
//		itemsPaintSel.setStyle(Paint.Style.FILL);
//		Path p = new Path();
//		p.moveTo(center-itemsWidth/2,(int)(fh*0) );
//		p.lineTo(center+itemsWidth/2,(int)(fh*0) );
//		p.lineTo(center+itemsWidth/2,(int)(fh*1.5) );
//		p.lineTo(center-itemsWidth/2,(int)(fh*1.5) );
//		p.lineTo(center-itemsWidth/2,(int)(fh*0) );
//		canvas.drawPath(p, itemsPaintSel);


        itemsPaintSel = new TextPaint(Paint.ANTI_ALIAS_FLAG
                | Paint.FAKE_BOLD_TEXT_FLAG);
        itemsPaintSel.setTextSize(TEXT_SIZE);
        itemsPaintSel.setColor(ITEMS_TEXT_COLOR_SELECT);

//		itemsPaintSel.setShadowLayer(0.3f, 0, 0, ThemeColorBean.font_shadow); 
        String curValue = textList.get(currentItem);
        if ("MM-dd".equals(xshowFormat)) {
            Object obj = adapter.optJSONObject(currentItem);
            String showText = "";
            if (obj instanceof String) {
                showText = ((String) obj);
            } else if (obj instanceof JSONObject) {
                showText = ((JSONObject) obj).optString("text", "");
            }
            if (showText.length() == 8)
                curValue = showText.substring(0, 4) + "-" + showText.substring(4, 6) + "-" +
                        showText.substring(6, 8);
        }
        canvas.drawText(curValue, center - itemsWidth / 2 + ((itemsWidth - itemsPaint.measureText
                (curValue)) / 2), (int) (fh * 1.5), itemsPaintSel);


        //中心指针
        if (dataList.size() > 0) {
            centerDrawable.setBounds(center - offset, fh * 2 + itemHeight, center + offset,
                    getHeight());
            centerDrawable.draw(canvas);
        }
        int num = center / itemsWidth + 1;
        if (hasTrack) {
            //中心按钮
            centerBDrawable.setBounds(center - offset, (int) (fh * 1.5), center + offset, (int)
                    (fh * 2.5 + itemHeight));
            centerBDrawable.draw(canvas);
            //画轨道
            for (int i = 0; i < num; i++) {
                itemDrawable.setBounds(center + i * itemsWidth, fh * 2, center + (i + 1) *
                        itemsWidth, fh * 2 + itemHeight);
                itemDrawable.draw(canvas);
                itemDrawable.setBounds(center - (i + 1) * itemsWidth, fh * 2, center - i *
                        itemsWidth, fh * 2 + itemHeight);
                itemDrawable.draw(canvas);
            }

        }
        //多指
        if (isMuchPointer && dataList.size() > 0) {
            itemsPaint.setColor(0xFFff0000);
            List<Integer> poiterNum = new ArrayList<Integer>();
            for (int i = 0; i < posAtrr.length; i++) {
                if ((posAtrr[i] >= x1 - itemsWidth / 2 && posAtrr[i] <= x1 + itemsWidth / 2)) {

                    poiterNum.add(i);
                }
                if ((posAtrr[i] >= x2 - itemsWidth / 2 && posAtrr[i] <= x2 + itemsWidth / 2)) {

                    poiterNum.add(i);
                }
                if (poiterNum.size() == 2) {
                    break;
                }
            }
            //画多指的显示信息
            if (poiterNum.size() == 2) {
                int firstP = poiterNum.get(0);
                int endP = poiterNum.get(1);
                if (firstP > endP) {
                    firstP = poiterNum.get(1);
                    endP = poiterNum.get(2);
                }
                if (firstP >= 0 && firstP < adapter.length() && endP >= 0 && endP < adapter
                        .length()) {
                    for (int x = firstP; x < endP; x++) {
                        itemDrawableSel.setBounds(posAtrr[x], fh * 2, posAtrr[x + 1], fh * 2 +
                                itemHeight);
                        itemDrawableSel.setAlpha(180);
                        itemDrawableSel.draw(canvas);
                    }
                    centerDrawable.setBounds(posAtrr[firstP] - offset, (int) (fh * 2.5 +
                            itemHeight), posAtrr[firstP] + offset, getHeight());
                    centerDrawable.draw(canvas);
                    centerBDrawable.setBounds(posAtrr[firstP] - offset, (int) (fh * 1.5),
                            posAtrr[firstP] + offset, (int) (fh * 2.5 + itemHeight));
                    centerBDrawable.draw(canvas);

                    centerDrawable.setBounds(posAtrr[endP] - offset, (int) (fh * 2.5 +
                            itemHeight), posAtrr[endP] + offset, getHeight());
                    centerDrawable.draw(canvas);
                    centerBDrawable.setBounds(posAtrr[endP] - offset, (int) (fh * 1.5),
                            posAtrr[endP] + offset, (int) (fh * 2.5 + itemHeight));
                    centerBDrawable.draw(canvas);


                    String fS = adapter.optJSONObject(firstP).optString("text");
                    String fDS = adapter.optJSONObject(firstP).optString("value");
                    String eS = adapter.optJSONObject(endP).optString("text");
                    String eDS = adapter.optJSONObject(endP).optString("value");
                    Double fD = Double.parseDouble(fDS);
                    Double eD = Double.parseDouble(eDS);
                    if ("yyyy/MM/dd".equals(muchPointerXFormat)) {
                        if (fS.length() == 8) {
                            fS = fS.substring(0, 4) + "/" + fS.substring(4, 6) + "/" + fS
                                    .substring(6, 8);
                        }
                        if (eS.length() == 8) {
                            eS = eS.substring(0, 4) + "/" + eS.substring(4, 6) + "/" + eS
                                    .substring(6, 8);
                        }
                    }
                    DecimalFormat df = new DecimalFormat("#,##0.##");
                    DecimalFormat df2 = new DecimalFormat("#,##0.00");
                    String s1 = "  " + fS + " ";
                    String s2 = NumericalUtil.getInstance().setThousands(fDS);
                    String s3 = "- " + eS + " ";
                    String s4 = NumericalUtil.getInstance().setThousands(eDS);
                    String botomStr = "";
                    double x = 0;
                    if (fD != 0) {
                        x = (eD - fD) / fD * 100;
                    }
//					if(eD>=fD){
//						botomStr=" +"+df.format(eD-fD)+"   +"+df.format(x)+"%  ";
//					}else{
//						botomStr=" "+df.format(eD-fD)+"  "+df.format(x)+"%  ";
//					}
                    botomStr = " " + df.format(eD - fD) + "  " + df2.format(x) + "%  ";
                    Paint paint = getNomorlPaint();
                    fm = paint.getFontMetrics();
                    fh = (int) Math.ceil(fm.descent - fm.ascent);
                    int xwidth = (int) paint.measureText(s1 + s3);
                    paint = getBoldPaint();
                    int xwidth1 = (int) paint.measureText(s2 + s4 + botomStr);
                    int firstX = posAtrr[firstP];
                    int endX = posAtrr[endP];
                    int centerX = (endX - firstX) / 2 + firstX;
                    RectF rectf;
                    xwidth = xwidth + xwidth1;
                    if (centerX - xwidth / 2 < 0) {
                        rectf = new android.graphics.RectF(getLeft(), 0, getLeft() + xwidth,
                                (int) (0 + fh * 2));
                        centerX = getLeft() + xwidth / 2;
                    } else if (centerX + xwidth / 2 > getRight()) {
                        rectf = new android.graphics.RectF(getRight() - xwidth, 0, getRight(),
                                (int) (0 + fh * 2));
                        centerX = getRight() - xwidth / 2;
                    } else {
                        rectf = new android.graphics.RectF(centerX - xwidth / 2, 0, centerX +
                                xwidth / 2, 0 + (int) (fh * 2));
                    }
                    paint = new Paint();
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(android.graphics.Color.WHITE);
                    paint.setAntiAlias(true);
                    canvas.drawRoundRect(rectf, 8, 8, paint);
                    paint = getNomorlPaint();
                    int sx = centerX - xwidth / 2;
                    canvas.drawText(s1, sx, (int) (rectf.top + fh * 1.5), paint);
                    xwidth = (int) paint.measureText(s1);
                    paint = getBoldPaint();
                    sx = sx + xwidth;
                    canvas.drawText(s2, sx, (int) (rectf.top + fh * 1.5), paint);
                    xwidth = (int) paint.measureText(s2);
                    sx = sx + xwidth;
                    paint = getNomorlPaint();
                    canvas.drawText(s3, sx, (int) (rectf.top + fh * 1.5), paint);
                    xwidth = (int) paint.measureText(s3);
                    sx = sx + xwidth;
                    paint = getBoldPaint();
                    canvas.drawText(s4, sx, (int) (rectf.top + fh * 1.5), paint);
                    xwidth = (int) paint.measureText(s4);
                    sx = sx + xwidth;
                    if (eD >= fD) {
                        paint.setColor(Color.parseColor(Level1Bean.plusColor));
                    } else {
                        paint.setColor(Color.parseColor(Level1Bean.minusColor));
                    }
                    canvas.drawText(botomStr, sx, (int) (rectf.top + fh * 1.5), paint);
                }
            }
        }
        //画边框
        //canvas.drawRect(0,0,getWidth(),getHeight(), paint);   
        itemsPaint.setColor(ITEMS_TEXT_COLOR);
    }

    public Paint getNomorlPaint() {
        Paint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG
                | Paint.FAKE_BOLD_TEXT_FLAG);
        paint.setAntiAlias(true);
        paint.setTextSize(TEXT_SIZE);
        paint.setColor(Color.BLACK);
        Typeface tf = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC);
        paint.setTypeface(tf);
        return paint;
    }

    public Paint getBoldPaint() {
        Paint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG
                | Paint.FAKE_BOLD_TEXT_FLAG);
        paint.setAntiAlias(true);
        paint.setTextSize(TEXT_SIZE + 2);
        paint.setColor(Color.BLACK);
        paint.setFakeBoldText(true);
        Typeface tf = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
        paint.setTypeface(tf);
        return paint;
    }

    int x1 = 0;
    int x2 = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Level1Bean.scrollToLeft = true;
        Level1Bean.scrollToRight = true;
        JSONArray adapter = getAdapter();
        if (adapter == null) {
            return true;
        }
        if (dataList.size() > 0 && muchPoitFun) {
            if (!isScrollingPerformed) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_POINTER_2_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        isMuchPointer = true;
                        x1 = (int) event.getX(event.getPointerId(0));
                        x2 = (int) event.getX(event.getPointerId(1));
                        this.invalidate();
                        break;

                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_POINTER_2_UP:
                        isMuchPointer = false;
                        this.invalidate();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isMuchPointer) {
                            x1 = (int) event.getX(event.getPointerId(0));
                            x2 = (int) event.getX(event.getPointerId(1));
                        }
                        this.invalidate();
                        break;
                }
            }

            //不是多指
            if (!isMuchPointer) {
                if (!gestureDetector.onTouchEvent(event) && event.getAction() == MotionEvent
                        .ACTION_UP) {
                    justify();
                }
            }
        } else {
            if (!gestureDetector.onTouchEvent(event) && event.getAction() == MotionEvent
                    .ACTION_UP) {
                justify();
            }
        }
        return true;
    }

    /**
     * Scrolls the wheel
     *
     * @param delta the scrolling value
     */
    private void doScroll(int delta) {
        //滚动的距离
        scrollingOffset += delta;
        //滚动了几个对象
        int count = scrollingOffset / itemsWidth;
        int pos = currentItem - count;
        //还在滚动
        if (isScrollingPerformed) {
            //滚动到头啦，就滚动了当前的个数个对象
            if (pos < 0) {
                count = currentItem;
                pos = 0;
            } else if (pos >= adapter.length()) {//滚动到尾
                count = currentItem - adapter.length() + 1;
                pos = adapter.length() - 1;
            }
        } else {
            // 滚动停止-----------保证pos在0--adapter.getItemsCount() - 1之间
            pos = Math.max(pos, 0);
            pos = Math.min(pos, adapter.length() - 1);
        }
        int offset = scrollingOffset;

        if (pos != currentItem) {
            setCurrentItem(pos, false);
        } else {
            invalidate();
        }

        // update offset
        scrollingOffset = offset - count * itemsWidth;
        if (scrollingOffset > getWidth()) {
            scrollingOffset = scrollingOffset % getWidth() + getWidth();
        }
    }

    // gesture listener
    private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
        public boolean onDown(MotionEvent e) {
            //正在滚动
            if (isScrollingPerformed) {
                scroller.forceFinished(true);
                clearMessages();
                return true;
            }
            return false;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //开始滚动
            startScrolling();
            doScroll((int) -distanceX);
            return true;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            lastScrollX = currentItem * itemsWidth + scrollingOffset;
            int maxX = adapter.length() * itemsWidth;
            int minX = 0;
            scroller.fling(lastScrollX, 0, (int) -velocityX / 2, 0, minX, maxX, 0, 0);
            setNextMessage(MESSAGE_SCROLL);
            return true;
        }
    };

    // Messages
    private final int MESSAGE_SCROLL = 0;
    private final int MESSAGE_JUSTIFY = 1;

    /**
     * Set next message to queue. Clears queue before.
     *
     * @param message the message to set
     */
    private void setNextMessage(int message) {
        clearMessages();
        animationHandler.sendEmptyMessage(message);
    }

    /**
     * Clears messages from queue
     */
    private void clearMessages() {
        animationHandler.removeMessages(MESSAGE_SCROLL);
        animationHandler.removeMessages(MESSAGE_JUSTIFY);
    }

    // animation handler
    private Handler animationHandler = new Handler() {
        public void handleMessage(Message msg) {
            scroller.computeScrollOffset();
            int currX = scroller.getCurrX();
            int delta = lastScrollX - currX;
            lastScrollX = currX;
            if (delta != 0) {
                doScroll(delta);
            }

            // scrolling is not finished when it comes to final Y
            // so, finish it manually 
            if (Math.abs(currX - scroller.getFinalX()) < MIN_DELTA_FOR_SCROLLING) {
                currX = scroller.getFinalX();
                scroller.forceFinished(true);
            }
            if (!scroller.isFinished()) {
                animationHandler.sendEmptyMessage(msg.what);
            } else if (msg.what == MESSAGE_SCROLL) {
                justify();
            } else {
                finishScrolling();
            }
        }
    };

    /**
     * Justifies wheel
     */
    private void justify() {
        if (adapter == null) {
            return;
        }

        lastScrollX = 0;
        int offset = scrollingOffset;
        boolean needToIncrease = offset > 0 ? currentItem < adapter.length() : currentItem > 0;
        if ((needToIncrease) && Math.abs((float) offset) > (float) itemsWidth / 2) {
            if (offset < 0)
                offset += itemsWidth + MIN_DELTA_FOR_SCROLLING;
            else
                offset -= itemsWidth + MIN_DELTA_FOR_SCROLLING;
        }
        if (Math.abs(offset) > MIN_DELTA_FOR_SCROLLING) {
            scroller.startScroll(0, 0, 0, offset, SCROLLING_DURATION);
            setNextMessage(MESSAGE_JUSTIFY);
        } else {
            finishScrolling();
        }
    }

    /**
     * Starts scrolling
     */
    private void startScrolling() {
        if (!isScrollingPerformed) {
            isScrollingPerformed = true;
            notifyScrollingListenersAboutStart();
        }
    }

    /**
     * Finishes scrolling
     */
    void finishScrolling() {
        if (isScrollingPerformed) {
            notifyScrollingListenersAboutEnd();
            isScrollingPerformed = false;
        }
        invalidateLayouts();
        invalidate();
    }


    /**
     * Scroll the wheel
     *
     * @param itemsToSkip items to scroll
     * @param time        scrolling duration
     */
    public void scroll(int itemsToScroll, int time) {
        scroller.forceFinished(true);

        lastScrollX = scrollingOffset;
        int offset = itemsToScroll * itemsWidth;

        scroller.startScroll(lastScrollX, 0, offset - lastScrollX, 0, time);
        setNextMessage(MESSAGE_SCROLL);

        startScrolling();
    }

    public interface OnHWheelScrollListener {
        void onScrollingStarted(HWheelView wheel);

        void onScrollingFinished(HWheelView wheel);
    }

    public interface OnHWheelChangedListener {
        void onChanged(HWheelView wheel, int oldValue, int newValue);
    }


    public void setXshowFormat(String xshowFormat) {
        this.xshowFormat = xshowFormat;
    }

    public void setMuchPointerXFormat(String muchPointerXFormat) {
        this.muchPointerXFormat = muchPointerXFormat;
    }

    public void setHasTrack(boolean hasTrack) {
        this.hasTrack = hasTrack;
    }

    public void setChartType(int chartType) {
        this.chartType = chartType;
    }

    public void setLostFirst(boolean lostFirst) {
        this.lostFirst = lostFirst;
    }

    public String getElid() {
        return elid;
    }

    public void setElid(String elid) {
        this.elid = elid;
    }

    public boolean isMuchPoitFun() {
        return muchPoitFun;
    }

    public void setMuchPoitFun(boolean muchPoitFun) {
        this.muchPoitFun = muchPoitFun;
    }

    /**
     * 设置文本(日期等)字体大小
     *
     * @param tEXT_SIZE
     */
    public void setTEXT_SIZE(int tEXT_SIZE) {
        TEXT_SIZE = tEXT_SIZE;
    }

    /**
     * 设置文本(日期等)颜色
     *
     * @param iTEMS_TEXT_COLOR
     */
    public void setITEMS_TEXT_COLOR(int iTEMS_TEXT_COLOR) {
        ITEMS_TEXT_COLOR = iTEMS_TEXT_COLOR;
    }

    /**
     * 设置选中文本颜色
     *
     * @param iTEMS_TEXT_COLOR_SELECT
     */
    public void setITEMS_TEXT_COLOR_SELECT(int iTEMS_TEXT_COLOR_SELECT) {
        ITEMS_TEXT_COLOR_SELECT = iTEMS_TEXT_COLOR_SELECT;
    }

    /**
     * 图形颜色
     *
     * @param iTEMS_AREA_COLOR
     */
    public void setITEMS_AREA_COLOR(int iTEMS_AREA_COLOR) {
        ITEMS_AREA_COLOR = iTEMS_AREA_COLOR;
    }

    public Drawable getCenterDrawable() {
        return centerDrawable;
    }

    public void setCenterDrawable(Drawable centerDrawable) {
        this.centerDrawable = centerDrawable;
    }

    public Drawable getCenterBDrawable() {
        return centerBDrawable;
    }

    public void setCenterBDrawable(Drawable centerBDrawable) {
        this.centerBDrawable = centerBDrawable;
    }

}
