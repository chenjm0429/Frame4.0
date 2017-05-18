package com.ztesoft.level1.image;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.ztesoft.level1.R;

import java.io.InputStream;

/**
 * 圆形ImageView，可加载图片为园形(有边框)，并且可以点击选中、长按删除该图片
 *
 * @author wx
 */
public class ImageOperableView extends ImageView implements OnGestureListener {

    //边框宽度
    private int mBorderThickness = 10;
    private Context mContext;

    //图片编码
    private String code;

    // 选中颜色和未选中颜色
    private int checkedColor = Color.BLUE;
    private int unCheckedColor = Color.GRAY;

    // 控件默认长、宽
    private int defaultWidth = 0;
    private int defaultHeight = 0;

    // 是否被选中
    private boolean isChecked = false;

    // 该图片是否可删除
    private boolean isDel = false;

    // 删除、点击事件
    private DelClickListener delClick;
    private OnOperableViewClickListerer onClick;
    private OnOperableViewLongClickListener onLongClick;

    //手势时间处理(点击、长按处理)
    private GestureDetector detector;

    //是否触发了长按时间
    private boolean isLongClick = false;

    //删除按钮的bitmap
    private Bitmap delBitmap;

    // 半径
    private int radius = 0;

    public ImageOperableView(Context context) {
        super(context);
        mContext = context;

        detector = new GestureDetector(this);
    }

    public ImageOperableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        detector = new GestureDetector(this);
    }

    public ImageOperableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        detector = new GestureDetector(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        this.measure(0, 0);

        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
        if (defaultWidth == 0) {
            defaultWidth = getWidth();
        }
        if (defaultHeight == 0) {
            defaultHeight = getHeight();
        }

        // 半径
        radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - 2 *
                mBorderThickness;
        // 边框与图形之间的白色圆环
        drawCircleBorder(canvas, radius + mBorderThickness / 2, Color.WHITE);

        if (isChecked) {// 定义画两个边框，分别为外圆边框和内圆边框
            // 画选中
            drawCircleBorder(canvas, radius + mBorderThickness + mBorderThickness / 2,
                    checkedColor);
        } else {
            // 画未选中
            drawCircleBorder(canvas, radius + mBorderThickness + mBorderThickness / 2,
                    unCheckedColor);
        }
        Bitmap roundBitmap = getCroppedRoundBitmap(bitmap, radius);
//		if(isChecked)
        canvas.drawBitmap(roundBitmap, defaultWidth / 2 - radius, defaultHeight
                / 2 - radius, null);
//		else  //未选中时图片置为灰色
//			canvas.drawBitmap(roundBitmap, defaultWidth / 2 - radius, defaultHeight
//					/ 2 - radius, getGrayPaint());
        roundBitmap.recycle();

        // 长按事件后，显示删除按钮
        if (isLongClick) {
            if (null == delBitmap) {
                setDelBitmap(R.drawable.del_flag);
            }

            delBitmap = getCroppedRoundBitmap(delBitmap, radius / 4);
            canvas.drawBitmap(delBitmap, defaultWidth - 2 * radius / 4, defaultHeight / 2 -
                    radius, null);
        }
    }

    /**
     * 获取裁剪后的圆形图片
     *
     * @param radius 半径
     */
    public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
        Bitmap scaledSrcBmp;
        int diameter = radius * 2;

        // 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        int squareWidth, squareHeight;
        int x, y;
        Bitmap squareBitmap;
        if (bmpHeight > bmpWidth) {// 高大于宽
            squareWidth = squareHeight = bmpWidth;
            x = 0;
            y = (bmpHeight - bmpWidth) / 2;
            // 截取正方形图片
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeight);
        } else if (bmpHeight < bmpWidth) {// 宽大于高
            squareWidth = squareHeight = bmpHeight;
            x = (bmpWidth - bmpHeight) / 2;
            y = 0;
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeight);
        } else {
            squareBitmap = bmp;
        }

        if (squareBitmap.getWidth() != diameter || squareBitmap.getHeight() != diameter) {
            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter, diameter, true);

        } else {
            scaledSrcBmp = squareBitmap;
        }
        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(), scaledSrcBmp.getHeight(),
                Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(), scaledSrcBmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2, scaledSrcBmp.getHeight() / 2, scaledSrcBmp
                .getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
        // bitmap回收(recycle导致在布局文件XML看不到效果)
        // bmp.recycle();
        // squareBitmap.recycle();
        // scaledSrcBmp.recycle();
        bmp = null;
        squareBitmap = null;
        scaledSrcBmp = null;
        return output;
    }

    /**
     * 画圆形图片的边框
     */
    private void drawCircleBorder(Canvas canvas, int radius, int color) {
        Paint paint = new Paint();
        // 去锯齿
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(color);
        // 设置paint的　style　为STROKE：空心
        paint.setStyle(Paint.Style.STROKE);
        // 设置paint的外框宽度
        paint.setStrokeWidth(mBorderThickness);
        canvas.drawCircle(defaultWidth / 2, defaultHeight / 2, radius, paint);
    }

    private float downX = 0;
    private float downY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 长按事件后可以点击删除按钮
        if (isLongClick) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 获得down事件的坐标，用来判断是否点击在删除图片上
                    downX = event.getX();
                    downY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:

                    // 删除按钮的中心点
                    int x = defaultWidth - radius / 5;
                    int y = defaultHeight / 2 - radius + radius / 5;

                    if (Math.abs(downX - x) < 2 * radius / 5
                            && Math.abs(downY - y) < 2 * radius / 5
                            && Math.abs(event.getX() - x) < 2 * radius / 5
                            && Math.abs(event.getY() - y) < 2 * radius / 5) {
                        isLongClick = false;
                        delClick.delClick(this);
                    } else {
                        detector.onTouchEvent(event);
                    }
                    break;
            }
        } else {
            detector.onTouchEvent(event);
        }
        //return true可以调用detector的onSingleTapUp为单击事件
        return true;
    }

    /**
     * 删除事件
     */
    public interface DelClickListener {
        void delClick(ImageOperableView view);
    }

    /**
     * 点击事件
     */
    public interface OnOperableViewClickListerer {
        void onClick(ImageOperableView view);
    }

    public interface OnOperableViewLongClickListener {
        void onLongClick(ImageOperableView view);
    }

    public int getCheckedColor() {
        return checkedColor;
    }

    public void setCheckedColor(int checkedColor) {
        this.checkedColor = checkedColor;

        if (radius > 0)
            invalidate();
    }

    public int getUnCheckedColor() {
        return unCheckedColor;
    }

    public void setUnCheckedColor(int unCheckedColor) {
        this.unCheckedColor = unCheckedColor;

        if (radius > 0)
            invalidate();
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;

        if (radius > 0)
            invalidate();
    }

    public boolean isDel() {
        return isDel;
    }

    public void setDel(boolean isDel) {
        this.isDel = isDel;
    }

    public DelClickListener getDelClick() {
        return delClick;
    }

    public void setDelClick(DelClickListener delClick) {
        this.delClick = delClick;
    }

    public OnOperableViewLongClickListener getOnLongClick() {
        return onLongClick;
    }

    public void setOnLongClick(OnOperableViewLongClickListener onLongClick) {
        this.onLongClick = onLongClick;
    }

    public OnOperableViewClickListerer getOnClick() {
        return onClick;
    }

    public void setOnClick(OnOperableViewClickListerer onClick) {
        this.onClick = onClick;
    }

    public Bitmap getDelBitmap() {
        return delBitmap;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDelBitmap(int delBitmap) {
        // 得到Resources对象
        Resources r = mContext.getResources();
        // 以数据流的方式读取资源
        InputStream is = r.openRawResource(delBitmap);
        BitmapDrawable bmpDraw = new BitmapDrawable(is);
        this.delBitmap = bmpDraw.getBitmap();
    }

    /**
     * 设置view的可删除状态
     *
     * @param tag true:可删除状态，false:不可删除状态
     */
    public void setViewDelState(boolean tag) {
        if (isDel) {
            isLongClick = tag;
            invalidate();
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (null != onLongClick)
            onLongClick.onLongClick(this);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // 单击事件,切换选中状态
        if (isLongClick) {
            setViewDelState(false);
        } else {
            isChecked = !isChecked;
            // 调用单击事件方法
            if (null != onClick)
                onClick.onClick(this);

            invalidate();
        }

        return false;
    }

    /**
     * 获得暗颜色画笔
     */
    private Paint getGrayPaint() {
        Paint paint = new Paint();
        try {
            ColorMatrix colorMatrix = new ColorMatrix();
            //设置为0，画笔画出的图形为灰色
            colorMatrix.setSaturation(0);
            ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);

            paint.setColorFilter(colorMatrixFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paint;
    }
}
