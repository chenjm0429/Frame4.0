package com.ztesoft.level1.util;

import android.graphics.Path;
import android.graphics.RectF;

/**
 * 简单图形绘制
 * @author wanghx2
 *
 */
public class DrawPathUtil {

	public DrawPathUtil() {
	}

	/**
	 * 绘制任意角度的环形
	 * @param startAngle	起始角度
	 * @param endAngle		结束角度
	 * @param radius		环形最大半径(环宽度为radius/2)
	 */
	public Path drawDra(float startAngle, float endAngle, float radius) {
		Path path = new Path();
		//计算起始坐标
		float x = (float) (radius - Math.cos(Math.toRadians(startAngle)) * radius);
		float y = (float) (radius - Math.sin(Math.toRadians(startAngle)) * radius);
		path.moveTo(x, y+2);
		//绘制外弧形
		RectF rect = new RectF(0, 2, radius * 2, radius * 2 + 2);//往下偏移2px，保证顶端圆弧效果
		path.addArc(rect, startAngle - 180, endAngle - startAngle);
		//计算内环起始坐标
		float xx = (float) (radius - Math.cos(Math.toRadians(endAngle)) * radius / 2);
		float yy = (float) (radius - Math.sin(Math.toRadians(endAngle)) * radius / 2);
		path.lineTo(xx, yy+2);
		//绘制内弧形
		RectF rect2 = new RectF(radius / 2, radius / 2 + 2, radius * 3 / 2, radius * 3 / 2 + 2);//往下偏移2px，保证顶端圆弧效果
		path.addArc(rect2, endAngle - 180, startAngle - endAngle);
		//闭环
		path.lineTo(x, y+2);
//		path.close();
		return path;
	}
	
}
