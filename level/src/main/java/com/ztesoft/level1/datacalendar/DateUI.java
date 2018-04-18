package com.ztesoft.level1.datacalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.ztesoft.level1.R;

import java.util.Calendar;
import java.util.Date;

/**
 * 日历控件 功能：获得点选的日期区间
 */
public class DateUI extends View implements View.OnTouchListener {
    private Date currentDate; // 字符串型的当前日期
    private Date selectedStartDate;
    private Date selectedEndDate;
    private Date curDate; // 当前日历显示的月
    private Date today; // 今天的日期文字显示红色
    private Date downDate; // 手指按下状态时临时日期
    private Date showFirstDate, showLastDate; // 日历显示的第一个日期和最后一个日期
    private Date[] checks;
    private int downIndex; // 按下的格子索引
    private Calendar calendar;
    private Surface surface;
    private Context context;
    private int[] date = new int[42]; // 日历显示数字
    private int curStartIndex, curEndIndex; // 当前显示的日历起始的索引
    // private boolean completed = false; //
    // 为false表示只选择一个日期，true表示选择开始和结束两个日期
    private boolean isSelected = true; // true:选中，false:取消选中

    // 给控件设置监听事件
    private OnDateClickListener onClickListener;

    private OnMonChgClickListener onMonChgClickListener;

    private InnerMonChgClickListener innerMonChgClickListener;

    private String[] weekText;
    // 0代表开始为周日，1代表开始为周一
    private int startType = 0;

    private int textColor = Color.BLACK;

    public DateUI(Context context) {
        super(context);
        this.context = context;
    }

    public DateUI(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public DateUI(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public DateUI(Context context, Date date) {
        super(context);
        this.context = context;
        this.currentDate = date;
    }

    public DateUI(Context context, AttributeSet attrs, Date date) {
        super(context, attrs);
        this.context = context;
        this.currentDate = date;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public Date[] getChecks() {
        return checks;
    }

    public void setChecks(Date[] checks) {
        this.checks = checks;
    }

    public int getStartType() {
        return startType;
    }

    public void setStartType(int startType) {
        this.startType = startType;
    }

    public InnerMonChgClickListener getInnerMonChgClickListener() {
        return innerMonChgClickListener;
    }

    public void setInnerMonChgClickListener(
            InnerMonChgClickListener innerMonChgClickListener) {
        this.innerMonChgClickListener = innerMonChgClickListener;
    }

    public OnMonChgClickListener getOnMonChgClickListener() {
        return onMonChgClickListener;
    }

    public void setOnMonChgClickListener(
            OnMonChgClickListener onMonChgClickListener) {
        this.onMonChgClickListener = onMonChgClickListener;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void init() {

        if (null == currentDate) {
            curDate = today = new Date();
        } else {
            curDate = today = currentDate;
        }

        if (null == selectedStartDate && null == selectedEndDate) {
            selectedStartDate = curDate;
            selectedEndDate = curDate;
        } else if (null != selectedStartDate && null != selectedEndDate) {
            // 防止currentDate和selectedStartDate都设置了初始值,而且初始值不一样
            if (selectedStartDate.after(selectedEndDate)) {
                Toast.makeText(context, context.getString(R.string.calendar_init_date_error),
                        Toast.LENGTH_LONG).show();
            }
            curDate = selectedStartDate;
        } else {
            Toast.makeText(context, context.getString(R.string.calendar_init_date_error), Toast
                    .LENGTH_LONG).show();
            selectedStartDate = selectedEndDate = today = curDate;
        }

        calendar = Calendar.getInstance();
        calendar.setTime(curDate);
        surface = new Surface();
        surface.density = getResources().getDisplayMetrics().density;

        if (startType == 0)
            weekText = surface.weekText;
        else
            weekText = surface.weekText1;

//		setBackgroundColor(surface.bgColor);
        setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		surface.width = getResources().getDisplayMetrics().widthPixels / 2;
//		surface.height = (int) (getResources().getDisplayMetrics().heightPixels * 1 / 5);
//		 if (View.MeasureSpec.getMode(widthMeasureSpec) ==
//		 View.MeasureSpec.EXACTLY) {
//		 surface.width = View.MeasureSpec.getSize(widthMeasureSpec);
//		 }
        surface.width = View.MeasureSpec.getSize(widthMeasureSpec);
        surface.height = View.MeasureSpec.getSize(heightMeasureSpec);
//		 if (View.MeasureSpec.getMode(heightMeasureSpec) ==
//		 View.MeasureSpec.EXACTLY) {
//		 surface.height = View.MeasureSpec.getSize(heightMeasureSpec);
//		 }
        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(surface.width, View.MeasureSpec
                .EXACTLY);
        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(surface.height, View.MeasureSpec
                .EXACTLY);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//		Log.d(TAG, "[onLayout] changed:"
//				+ (changed ? "new size" : "not change") + " left:" + left
//				+ " top:" + top + " right:" + right + " bottom:" + bottom);
        if (changed) {
            surface.init();
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//		Log.d(TAG, "onDraw");
        // 画框
        // canvas.drawPath(surface.boxPath, surface.borderPaint);
        // 年月
        // String monthText = getYearAndmonth();
        // float textWidth = surface.monthPaint.measureText(monthText);
        // canvas.drawText(monthText, (surface.width - textWidth) / 2f,
        // surface.monthHeight * 3 / 4f, surface.monthPaint);
        // 上一月/下一月
        // canvas.drawPath(surface.preMonthBtnPath,
        // surface.monthChangeBtnPaint);
        // canvas.drawPath(surface.nextMonthBtnPath,
        // surface.monthChangeBtnPaint);
        // 星期
        float weekTextY = surface.monthHeight + surface.weekHeight * 3 / 4f;
        // 星期背景
        // surface.cellBgPaint.setColor(surface.textColor);
        // canvas.drawRect(surface.weekHeight, surface.width,
        // surface.weekHeight, surface.width, surface.cellBgPaint);
        for (int i = 0; i < weekText.length; i++) {
            float weekTextX = i * surface.cellWidth + (surface.cellWidth - surface.weekPaint
                    .measureText(weekText[i])) / 2f;
            canvas.drawText(weekText[i], weekTextX, weekTextY, surface.weekPaint);
        }

        // 计算日期
        calculateDate();
        // 按下状态，选择状态背景色
        drawDownOrSelectedBg(canvas);
        // write date number
        // today index
        int todayIndex = -1;
        calendar.setTime(curDate);
        String curYearAndMonth = calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.MONTH);
        calendar.setTime(today);
        String todayYearAndMonth = calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.MONTH);
        if (curYearAndMonth.equals(todayYearAndMonth)) {
            int todayNumber = calendar.get(Calendar.DAY_OF_MONTH);
            todayIndex = curStartIndex + todayNumber - 1;
        }

        int selectedIndex = -1;
        calendar.setTime(selectedStartDate);
        String selectedYearAndMonth = calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar
                .MONTH);
        if (curYearAndMonth.equals(selectedYearAndMonth)) {
            int selNumber = calendar.get(Calendar.DAY_OF_MONTH);
            selectedIndex = curStartIndex + selNumber - 1;
        }
        for (int i = 0; i < 42; i++) {
            int color = textColor;
            if (isLastMonth(i)) {
                color = surface.borderColor;
            } else if (isNextMonth(i)) {
                color = surface.borderColor;
            }

            // 当前日期
            if (todayIndex != -1 && i == todayIndex) {

                if (todayIndex == selectedIndex) {
                    color = surface.todayNumberColor;
                    drawCellBg(canvas, i, surface.cellSelectedColor);
                } else {
                    color = surface.cellSelectedColor;
                    drawCurrBg(canvas, i, surface.cellSelectedColor);
                }
            }

            // 选中日期
            if (selectedIndex != -1 && i == selectedIndex) {
                color = surface.todayNumberColor;
            }

            // 有数据的日期，加点
            if (isChecks(i))
                drawChecks(canvas, i, surface.checksColor);

            drawCellText(canvas, i, date[i] + "", color);
        }
        super.onDraw(canvas);
    }

    private boolean isChecks(int index) {
        boolean tag = false;

        if (null == checks)
            return false;

        calendar.setTime(curDate);
        String selectedDate = calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.MONTH);

        for (int m = 0; m < checks.length; m++) {
            calendar.setTime(checks[m]);
            if (selectedDate.equals(calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar
                    .MONTH)) && calendar.get(Calendar.DAY_OF_MONTH) - 1 == index - curStartIndex)
                tag = true;
        }
        return tag;
    }

    private void drawChecks(Canvas canvas, int index, int color) {
        int x = getXByIndex(index);
        int y = getYByIndex(index);
        surface.datePaint.setColor(color);
        float cellY = surface.monthHeight + surface.weekHeight + (y - 1) * surface.cellHeight +
                surface.cellHeight;
        float cellX = (surface.cellWidth * (x - 1)) + surface.cellWidth / 2f;
        canvas.drawCircle(cellX, cellY, surface.cellWidth / 12f, surface.datePaint);
    }

    private void calculateDate() {
        calendar.setTime(curDate);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);
//		Log.d(TAG, "day in week:" + dayInWeek);
        int monthStart = dayInWeek;
        if (monthStart == 1) {
            monthStart = 8;
        }
        if (startType == 0)
            monthStart -= 1; // 以日为开头-1，以星期一为开头-2
        else
            monthStart -= 2;
        curStartIndex = monthStart;
        date[monthStart] = 1;
        // last month
        if (monthStart > 0) {
            calendar.set(Calendar.DAY_OF_MONTH, 0);
            int dayInmonth = calendar.get(Calendar.DAY_OF_MONTH);
            for (int i = monthStart - 1; i >= 0; i--) {
                date[i] = dayInmonth;
                dayInmonth--;
            }
            calendar.set(Calendar.DAY_OF_MONTH, date[0]);
        }
        showFirstDate = calendar.getTime();
        // this month
        calendar.setTime(curDate);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        // Log.d(TAG, "m:" + calendar.get(Calendar.MONTH) + " d:" +
        // calendar.get(Calendar.DAY_OF_MONTH));
        int monthDay = calendar.get(Calendar.DAY_OF_MONTH);
        for (int i = 1; i < monthDay; i++) {
            date[monthStart + i] = i + 1;
        }
        curEndIndex = monthStart + monthDay;
        // next month
        for (int i = monthStart + monthDay; i < 42; i++) {
            date[i] = i - (monthStart + monthDay) + 1;
        }
        if (curEndIndex < 42) {
            // 显示了下一月的
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        calendar.set(Calendar.DAY_OF_MONTH, date[41]);
        showLastDate = calendar.getTime();
    }

    /**
     * @param canvas
     * @param index
     * @param text
     */
    private void drawCellText(Canvas canvas, int index, String text, int color) {
        int x = getXByIndex(index);
        int y = getYByIndex(index);
        surface.datePaint.setColor(color);
        float cellY = surface.monthHeight + surface.weekHeight + (y - 1) * surface.cellHeight +
                surface.cellHeight * 3 / 4f;
        float cellX = (surface.cellWidth * (x - 1)) + (surface.cellWidth - surface.datePaint
                .measureText(text)) / 2f;
        canvas.drawText(text, cellX, cellY, surface.datePaint);
    }

    /**
     * @param canvas
     * @param index
     * @param color
     */
    private void drawCurrBg(Canvas canvas, int index, int color) {

        if (index < curStartIndex || index >= curEndIndex)
            return;

        int x = getXByIndex(index);
        int y = getYByIndex(index);
        surface.cellBgPaint.setColor(color);
        surface.cellBgPaint.setStrokeWidth(3 * surface.borderWidth);
        surface.cellBgPaint.setStyle(Paint.Style.STROKE);
        float left = surface.cellWidth * (x - 1) + surface.borderWidth;
        float top = surface.monthHeight + surface.weekHeight + (y - 1) * surface.cellHeight +
                surface.borderWidth;
        if (isSelected || (!isSelected && downIndex != index))
            canvas.drawCircle(left + surface.cellWidth / 2, top + surface.cellHeight / 2, surface
                    .cellWidth / 3f - 6 * surface.borderWidth, surface.cellBgPaint);
        // canvas.drawRect(left, top, left + surface.cellWidth
        // - surface.borderWidth, top + surface.cellHeight
        // - surface.borderWidth, surface.cellBgPaint);
        surface.cellBgPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * @param canvas
     * @param index
     * @param color
     */
    private void drawCellBg(Canvas canvas, int index, int color) {

        if (index < curStartIndex || index >= curEndIndex)
            return;

        int x = getXByIndex(index);
        int y = getYByIndex(index);
        surface.cellBgPaint.setColor(color);
        float left = surface.cellWidth * (x - 1) + surface.borderWidth;
        float top = surface.monthHeight + surface.weekHeight + (y - 1) * surface.cellHeight +
                surface.borderWidth;
        if (isSelected || (!isSelected && downIndex != index))
            canvas.drawCircle(left + surface.cellWidth / 2, top + surface.cellHeight / 2, surface
                    .cellWidth / 3f - 6 * surface.borderWidth, surface.cellBgPaint);
        // canvas.drawRect(left, top, left + surface.cellWidth
        // - surface.borderWidth, top + surface.cellHeight
        // - surface.borderWidth, surface.cellBgPaint);

    }

    private void drawDownOrSelectedBg(Canvas canvas) {
        // down and not up
        if (downDate != null) {
            drawCellBg(canvas, downIndex, surface.cellDownColor);
        }
        // selected bg color
        if (!selectedEndDate.before(showFirstDate) && !selectedStartDate.after(showLastDate)) {
            int[] section = new int[]{-1, -1};
            calendar.setTime(curDate);
            calendar.add(Calendar.MONTH, -1);
            findSelectedIndex(0, curStartIndex, calendar, section);
            if (section[1] == -1) {
                calendar.setTime(curDate);
                findSelectedIndex(curStartIndex, curEndIndex, calendar, section);
            }
            if (section[1] == -1) {
                calendar.setTime(curDate);
                calendar.add(Calendar.MONTH, 1);
                findSelectedIndex(curEndIndex, 42, calendar, section);
            }
            if (section[0] == -1) {
                section[0] = 0;
            }
            if (section[1] == -1) {
                section[1] = 41;
            }
            for (int i = section[0]; i <= section[1]; i++) {
                drawCellBg(canvas, i, surface.cellSelectedColor);
            }
        }
    }

    private void findSelectedIndex(int startIndex, int endIndex, Calendar calendar, int[] section) {
        for (int i = startIndex; i < endIndex; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, date[i]);
            Date temp = calendar.getTime();
            // Log.d(TAG, "temp:" + temp.toLocaleString());
            if (temp.compareTo(selectedStartDate) == 0) {
                section[0] = i;
            }
            if (temp.compareTo(selectedEndDate) == 0) {
                section[1] = i;
                return;
            }
        }
    }

    public Date getSelectedStartDate() {
        return selectedStartDate;
    }

    public Date getSelectedEndDate() {
        return selectedEndDate;
    }

    private boolean isLastMonth(int i) {
        if (i < curStartIndex) {
            return true;
        }
        return false;
    }

    private boolean isNextMonth(int i) {
        if (i >= curEndIndex) {
            return true;
        }
        return false;
    }

    private int getXByIndex(int i) {
        return i % 7 + 1; // 1 2 3 4 5 6 7
    }

    private int getYByIndex(int i) {
        return i / 7 + 1; // 1 2 3 4 5 6
    }

    // 获得当前应该显示的年月
    public String getYearAndmonth() {
        calendar.setTime(curDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        return year + "-" + surface.monthText[month];
    }

    // 上一月
    public String clickLeftMonth() {
        calendar.setTime(curDate);
        calendar.add(Calendar.MONTH, -1);
        curDate = calendar.getTime();
        invalidate();
        String dat = getYearAndmonth();
        if (null != onMonChgClickListener)
            onMonChgClickListener.onMonChg(dat);

        if (null != innerMonChgClickListener)
            innerMonChgClickListener.onMonChg(dat);

        return dat;
    }

    // 下一月
    public String clickRightMonth() {
        calendar.setTime(curDate);
        calendar.add(Calendar.MONTH, 1);
        curDate = calendar.getTime();
        invalidate();

        String dat = getYearAndmonth();
        if (null != onMonChgClickListener)
            onMonChgClickListener.onMonChg(dat);

        if (null != innerMonChgClickListener)
            innerMonChgClickListener.onMonChg(dat);

        return dat;
    }

    private void setSelectedDateByCoor(float x, float y) {
        // change month
        // if (y < surface.monthHeight) {
        // // pre month
        // if (x < surface.monthChangeWidth) {
        // calendar.setTime(curDate);
        // calendar.add(Calendar.MONTH, -1);
        // curDate = calendar.getTime();
        // }
        // // next month
        // else if (x > surface.width - surface.monthChangeWidth) {
        // calendar.setTime(curDate);
        // calendar.add(Calendar.MONTH, 1);
        // curDate = calendar.getTime();
        // }
        // }
        // cell click down
        if (y > surface.monthHeight + surface.weekHeight) {
            int m = (int) (Math.floor(x / surface.cellWidth) + 1);
            int n = (int) (Math.floor((y - (surface.monthHeight + surface.weekHeight)) / Float
                    .valueOf(surface.cellHeight)) + 1);
            downIndex = (n - 1) * 7 + m - 1;
//			Log.d(TAG, "downIndex:" + downIndex);

            if (downIndex < curStartIndex || downIndex >= curEndIndex) {
                downDate = null;
                return;
            }

            calendar.setTime(curDate);
            if (isLastMonth(downIndex)) {
                calendar.add(Calendar.MONTH, -1);
            } else if (isNextMonth(downIndex)) {
                calendar.add(Calendar.MONTH, 1);
            }
            calendar.set(Calendar.DAY_OF_MONTH, date[downIndex]);
            downDate = calendar.getTime();
        }
        invalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setSelectedDateByCoor(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                if (downDate != null) {
                    selectedStartDate = selectedEndDate = downDate;
                    // 响应监听事件
                    if (null != onClickListener) {
                        boolean tag = false;
                        calendar.setTime(selectedStartDate);
                        String selectedDate = calendar.get(Calendar.YEAR) + "" + calendar.get
                                (Calendar.MONTH) + calendar.get(Calendar.DAY_OF_MONTH);

                        if (null != checks && checks.length > 0) {
                            for (int m = 0; m < checks.length; m++) {
                                calendar.setTime(checks[m]);
                                if (selectedDate.equals(calendar.get(Calendar.YEAR) + "" + 
                                        calendar.get(Calendar.MONTH) + calendar.get(Calendar
                                        .DAY_OF_MONTH)))
                                    tag = true;
                            }
                        }
                        onClickListener.onDateClick(selectedEndDate, tag);
                    }
                    downDate = null;
                    invalidate();
                }

                if (downIndex < curStartIndex) {
                    // 向前翻月
                    clickLeftMonth();
                } else if (downIndex >= curEndIndex) {
                    // 向后翻月
                    clickRightMonth();
                }
                break;
        }
        return true;
    }

    public void setChangeData(Date[] checks) {
        if (null == this.checks) {
            this.checks = checks;
        } else {
            this.checks = checks;
            invalidate();
        }
    }

    // 给控件设置监听事件
    public void setOnDateClick(OnDateClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    // 监听接口
    public interface OnDateClickListener {
        void onDateClick(Date date, boolean isChecked);
    }

    public interface OnMonChgClickListener {
        // 参数为yyyy-MM
        void onMonChg(String yearAndMonth);
    }

    interface InnerMonChgClickListener {
        void onMonChg(String yearAndMonth);
    }

    public void setSelectedStartDate(Date selectedStartDate) {
        this.selectedStartDate = selectedStartDate;
    }

    public void setSelectedEndDate(Date selectedEndDate) {
        this.selectedEndDate = selectedEndDate;
    }

    public float getWeekTextSize() {
        return surface.weekPaint.getTextSize();
    }

    /**
     * 1. 布局尺寸 2. 文字颜色，大小 3. 当前日期的颜色，选择的日期颜色
     */
    private class Surface {
        public float density;
        public int width; // 整个控件的宽度
        public int height; // 整个控件的高度
        public float monthHeight; // 显示月的高度
        // public float monthChangeWidth; // 上一月、下一月按钮宽度
        public float weekHeight; // 显示星期的高度
        public float cellWidth; // 日期方框宽度
        public float cellHeight; // 日期方框高度
        public float borderWidth;
        //		public int bgColor = Color.parseColor("#FFFFFF");
//		private int textColor = Color.BLACK;
        // private int textColorUnimportant = Color.parseColor("#666666");
        private int btnColor = Color.parseColor("#666666");
        private int borderColor = Color.parseColor("#CCCCCC");
        public int todayNumberColor = Color.WHITE;
        public int checksColor = Color.parseColor("#ff6d00"); // 有数据的日期加点的颜色
        public int cellDownColor = Color.parseColor("#CCFFFF");
        public int cellSelectedColor = Color.parseColor("#21bdee");
        public Paint borderPaint;
        public Paint monthPaint;
        public Paint weekPaint;
        public Paint datePaint;
        public Paint monthChangeBtnPaint;
        public Paint cellBgPaint;
        public Path boxPath; // 边框路径
        // public Path preMonthBtnPath; // 上一月按钮三角形
        // public Path nextMonthBtnPath; // 下一月按钮三角形
        public String[] weekText = {"日", "一", "二", "三", "四", "五", "六"};
        public String[] weekText1 = {"一", "二", "三", "四", "五", "六", "日"};

        // public String[] weekText = { "Sun", "Mon", "Tue", "Wed", "Thu",
        // "Fri",
        // "Sat" };
        // public String[] weekText1 = { "Mon", "Tue", "Wed", "Thu", "Fri",
        // "Sat",
        // "Sun" };
        // public String[] monthText =
        // {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        public String[] monthText = {"1", "2", "3", "4", "5", "6", "7", "8",
                "9", "10", "11", "12"};

        public void init() {
            float temp = height / 7f;
            monthHeight = 0;// (float) ((temp + temp * 0.3f) * 0.6);
            // monthChangeWidth = monthHeight * 1.5f;
            weekHeight = (float) ((temp + temp * 0.3f) * 0.7);
            cellHeight = (height - monthHeight - weekHeight) / 6f;
            cellWidth = width / 7f;
            borderPaint = new Paint();
            borderPaint.setColor(borderColor);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderWidth = (float) (0.5 * density);
            // Log.d(TAG, "borderwidth:" + borderWidth);
            borderWidth = borderWidth < 1 ? 1 : borderWidth;
            borderPaint.setStrokeWidth(borderWidth);
            monthPaint = new Paint();
            monthPaint.setColor(textColor);
            monthPaint.setAntiAlias(true);
            float textSize = cellHeight * 0.4f;
//			Log.d(TAG, "text size:" + textSize);
            monthPaint.setTextSize(textSize);
            monthPaint.setTypeface(Typeface.DEFAULT_BOLD);
            weekPaint = new Paint();
            weekPaint.setColor(textColor);
            weekPaint.setAntiAlias(true);
            float weekTextSize = weekHeight * 0.6f;
            weekPaint.setTextSize(weekTextSize);
            weekPaint.setTypeface(Typeface.DEFAULT_BOLD);
            datePaint = new Paint();
            datePaint.setColor(textColor);
            datePaint.setAntiAlias(true);
            float cellTextSize = cellHeight * 0.5f;
            datePaint.setTextSize(cellTextSize);
            datePaint.setTypeface(Typeface.DEFAULT_BOLD);
            boxPath = new Path();
            // boxPath.addRect(0, 0, width, height, Direction.CW);
            // boxPath.moveTo(0, monthHeight);
            boxPath.rLineTo(width, 0);
            boxPath.moveTo(0, monthHeight + weekHeight);
            boxPath.rLineTo(width, 0);
            for (int i = 1; i < 6; i++) {
                boxPath.moveTo(0, monthHeight + weekHeight + i * cellHeight);
                boxPath.rLineTo(width, 0);
                boxPath.moveTo(i * cellWidth, monthHeight);
                boxPath.rLineTo(0, height - monthHeight);
            }
            boxPath.moveTo(6 * cellWidth, monthHeight);
            boxPath.rLineTo(0, height - monthHeight);
            monthChangeBtnPaint = new Paint();
            monthChangeBtnPaint.setAntiAlias(true);
            monthChangeBtnPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            monthChangeBtnPaint.setColor(btnColor);
            cellBgPaint = new Paint();
            cellBgPaint.setAntiAlias(true);
            cellBgPaint.setStyle(Paint.Style.FILL);
            cellBgPaint.setColor(cellSelectedColor);
        }
    }
}
