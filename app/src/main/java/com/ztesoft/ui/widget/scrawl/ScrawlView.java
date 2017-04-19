package com.ztesoft.ui.widget.scrawl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ztesoft.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 文件名称 : ScrawlView
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 涂鸦功能
 * <p>
 * 创建时间 : 2017/3/24 15:35
 * <p>
 */
public class ScrawlView extends View {

    private Bitmap bitmap;

    private Paint mPaint;

    private Canvas cacheCanvas;

    private Bitmap cacheBitmap;

    private Path mPath;

    // 是否处于橡皮擦模式
    private boolean isEraser = false;

    // 截图宽度，高度
    private int mWidth, mHeight;

    // 橡皮擦宽度
    private final static int ERASE_WIDTH = 15;

    private int currentColor = Color.parseColor("#ff0000"); // 默认红色
    private int currentSize = 4;

    // 画笔大小
    public static final int[] PAINT_SIZES = new int[]{2, 8, 16, 24};
    // 画笔大小图标
    public static final int[] PAINT_SIZES_DRAWABLES = new int[]{R.drawable.scrawl_thickness_1, R
            .drawable.scrawl_thickness_2,
            R.drawable.scrawl_thickness_3, R.drawable.scrawl_thickness_4};

    // 各种颜色画笔图标
    public static final int[] PAINT_COLORS_DRAWABLES = new int[]{R.drawable.scrawl_draw_pen_red,
            R.drawable.scrawl_draw_pen_dark, R.drawable.scrawl_draw_pen_blue, R.drawable.scrawl_draw_pen_green,
            R.drawable.scrawl_draw_pen_purse, R.drawable.scrawl_draw_pen_yellow};
    // 红、黑、蓝、绿、紫、黄
    public static final int[] PAINT_COLORS = new int[]{Color.parseColor("#ff0000"), Color
            .parseColor("#000000"),
            Color.parseColor("#519ed0"), Color.parseColor("#8fc154"), Color.parseColor("#8b37d9"),
            Color.parseColor("#ffd130")};

    // 记录path路径对象
    private DrawPath drawPath;
    // 保存Path路径的集合,用List集合来模拟栈
    private static List<DrawPath> savePath;

    private class DrawPath {
        public Path path;// 路径
        public Paint paint;// 画笔
    }

    public ScrawlView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrawlView(Context context, Bitmap bitmap) {

        super(context);
        savePath = new ArrayList<DrawPath>();
        this.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        mWidth = bitmap.getWidth();
        mHeight = bitmap.getHeight();
        // 创建一张屏幕大小的位图，作为缓冲
        cacheBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
//        cacheBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
        cacheCanvas = new Canvas(cacheBitmap);
        cacheCanvas.drawColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (isEraser) {
            if (mPath != null && mPaint != null) {
                cacheCanvas.drawPath(mPath, mPaint);
            }
            canvas.drawBitmap(cacheBitmap, 0, 0, null);
        } else {
            // 绘制上一次的，否则不连贯
            canvas.drawBitmap(cacheBitmap, 0, 0, null);
            if (mPath != null && mPaint != null) {

                canvas.drawPath(mPath, mPaint);
            }
        }
        canvas.restore();
    }

    /**
     * 撤销上一步操作
     */
    public void revocation() {

        if (savePath != null && savePath.size() > 0) {
            // 移除最后一个path,相当于出栈操作
            cacheBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            cacheCanvas = new Canvas(cacheBitmap);
            cacheCanvas.drawColor(Color.TRANSPARENT);
            savePath.remove(savePath.size() - 1);
            Iterator<DrawPath> iter = savePath.iterator();
            while (iter.hasNext()) {
                DrawPath drawPath = iter.next();
                cacheCanvas.drawPath(drawPath.path, drawPath.paint);
            }
            invalidate();// 刷新
        }   
    }

    private float cur_x, cur_y;
    private boolean isMoving;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mPaint = new Paint();
                mPaint.setAntiAlias(true); // 抗锯齿
                mPaint.setStrokeWidth(currentSize); // 线条宽度
                mPaint.setStyle(Paint.Style.STROKE); // 设置填充方式为描边
                mPaint.setColor(currentColor); // 颜色
                mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置笔刷的图像样式（外边缘）
                mPaint.setStrokeCap(Paint.Cap.ROUND);// 设置画笔转弯处的连接风格
                mPaint.setDither(true); // 使用抖动效果

                if (isEraser) {
                    mPaint.setStrokeWidth(ERASE_WIDTH);
                    mPaint.setStrokeCap(Paint.Cap.SQUARE);
                    mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                }

                // 每次down下去重新new一个Path
                mPath = new Path();
                // 每一次记录的路径对象是不一样的
                drawPath = new DrawPath();
                drawPath.path = mPath;
                drawPath.paint = mPaint;

                cur_x = x;
                cur_y = y;

                mPath.moveTo(cur_x, cur_y);

                isMoving = true;

                break;

            case MotionEvent.ACTION_MOVE:

                if (!isMoving)

                    break;

                // 二次曲线方式绘制
                mPath.quadTo(cur_x, cur_y, x, y);
                // 下面这个方法貌似跟上面一样
                // path.lineTo(x, y);

                cur_x = x;
                cur_y = y;

                break;

            case MotionEvent.ACTION_UP:

                mPath.lineTo(cur_x, cur_y);
                // 鼠标弹起保存最后状态
                cacheCanvas.drawPath(mPath, mPaint);
                // 将一条完整的路径保存下来(相当于入栈操作)
                savePath.add(drawPath);
                mPath = null;

                isMoving = false;

                isEraser = false;

                break;
        }

        // 通知刷新界面
        invalidate();

        return true;
    }

    /**
     * 分享图片
     *
     * @param context 上下文
     * @param imgPath 图片路径
     */
    public void shareMsg(Context context, String imgPath) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        if (TextUtils.isEmpty(imgPath)) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f.exists() && f.isFile()) {
                intent.setType("image/jpg");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "图片分享"));
    }

    public Bitmap getBitmap() {
        Bitmap tmpBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas tmpCan = new Canvas(tmpBitmap);
        tmpCan.drawBitmap(bitmap, 0, 0, null);
        tmpCan.drawBitmap(cacheBitmap, 0, 0, null);
        return tmpBitmap;
//    	return cacheBitmap;
    }

    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;
    }

    public void setCurrentSize(int currentSize) {
        this.currentSize = currentSize;
    }

    public void setEraser(boolean isEraser) {
        this.isEraser = isEraser;
    }

    public List<DrawPath> getSavePath() {
        return savePath;
    }
}
