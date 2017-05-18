package com.ztesoft.level1.util;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;

/**
 * 水印图片生成
 * @author wanghx2
 *
 */
public class WaterMark {
	private int textSize = 25;
	private int rotate = -45;
	private int alpha = 50;
	private String[] texts;
	private String textColor = "#bac0c8";
	
	
	public WaterMark(){
		
	}
	
	/**
	 * 绘制水印图片，返回bitmap
	 * @return
	 */
	public Bitmap drawBitmap() {
		Paint p = new Paint();
        Typeface font = Typeface.create(Typeface.SANS_SERIF,Typeface.BOLD);
        p.setColor(Color.parseColor(textColor));
        p.setTypeface(font);
        p.setAntiAlias(true);
		p.setTextSize(textSize);
		p.setAlpha(alpha);
		
		int width = 0;
		int height = 0;
		for(int i=0;i<texts.length;i++){
			int t = (int) p.measureText(texts[i]);
			if(t>width)
				width = t;
		}
		FontMetrics fm = p.getFontMetrics();
		height = (int) Math.ceil(fm.descent - fm.ascent);

		Bitmap newb = Bitmap.createBitmap(width, height*texts.length, Config.ARGB_8888 );
		Canvas canvasTemp = new Canvas( newb );
		for(int i=0;i<texts.length;i++){
			canvasTemp.drawText(texts[i], 0, height*(i+1), p);
		}
		Matrix matrix = new Matrix(); 
		//设置图像的旋转角度    
		matrix.setRotate(rotate);
		//旋转图像，并生成新的Bitmap对像  
		newb=Bitmap.createBitmap(newb, 0, 0, newb.getWidth(), newb.getHeight(), matrix, true);		
		
        return newb;
	}
	
	/**
	 * 绘制水印图片，返回保存标识
	 * @param path		图片保存路径
	 * @return
	 */
	public boolean drawBitmap(String path) {
		Bitmap bmp = drawBitmap();
		File f = new File(path);
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 水印文本颜色
	 * @param textColor
	 */
	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}

	/**
	 * 水印文本字号
	 * @param textSize
	 */
	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	/**
	 * 多行水印文本
	 * @param texts
	 */
	public void setTexts(String[] texts) {
		this.texts = texts;
	}
	/**
	 * 单行水印文本
	 * @param text
	 */
	public void setText(String text) {
		this.texts = new String[]{text};
	}

	/**
	 * 旋转角度
	 * @param rotate
	 */
	public void setRotate(int rotate) {
		this.rotate = rotate;
	}

	/**
	 * 透明度
	 * @param alpha
	 */
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

}
