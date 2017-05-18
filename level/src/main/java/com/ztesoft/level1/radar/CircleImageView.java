package com.ztesoft.level1.radar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircleImageView extends ImageView {

	Path path;
	public PaintFlagsDrawFilter mPaintFlagsDrawFilter = null;// 毛边过滤
	Paint paint;

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            上下文
	 */
	public CircleImageView(Context context) {
		super(context);
	}

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            上下文
	 * @param attrs
	 *            属性
	 */
	public CircleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
	}

	public void setColor(int color) {
		mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0,
				Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setColor(color);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (null == mPaintFlagsDrawFilter) {
			return;
		}
		float h = getMeasuredHeight();
		float w = getMeasuredWidth();
		if (null == path) {
			path = new Path();
			path.addCircle(w / 2.0f, h / 2.0f,
					(float) Math.min(w / 2.0f, (h / 2.0)), Path.Direction.CCW);
			path.close();
		}
		canvas.drawPath(path, paint);
	}

}
