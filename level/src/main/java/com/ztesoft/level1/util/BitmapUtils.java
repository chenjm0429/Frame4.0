package com.ztesoft.level1.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件名称 : BitmapUtils
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 位图工具类
 * <p>
 * 创建时间 : 2017/3/24 9:49
 * <p>
 */
public class BitmapUtils {

    /**
     * 缩放图片
     *
     * @param bitmap 图片
     * @param wf     目标宽度
     * @param hf     目标高度
     * @return 缩放后的图片
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, float wf, float hf) {
        Matrix matrix = new Matrix();
        matrix.postScale(wf, hf);

        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight
                (), matrix, true);
        bitmap.recycle();  //产生新的Bitmap后，原来的回收

        return newBitmap;
    }

    /**
     * 把Bitmap转Byte
     */
    public static byte[] bmpToBytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);

        return baos.toByteArray();
    }

    /**
     * 图片圆角处理
     *
     * @param bitmap  图片
     * @param roundPX 圆角角度
     * @return 处理后的图片
     */
    public static Bitmap getRoundBitmap(Bitmap bitmap, float roundPX) {

        Bitmap dstbmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config
                .ARGB_8888);

        Canvas canvas = new Canvas(dstbmp);

        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        //防止锯齿
        paint.setAntiAlias(true);
        //相当于清屏 
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        //先画了一个带圆角的矩形
        canvas.drawRoundRect(rectF, roundPX, roundPX, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        //再把原来的bitmap画到现在的bitmap
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return dstbmp;
    }

    /**
     * 质量压缩方法，此方法压缩后对Bitmap的大小没有影响，只会减小保存到SD卡的图片大小
     *
     * @param image
     * @return
     */
    private static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(bis, null, null);

        return bitmap;
    }

    /**
     * 尺寸压缩方法
     *
     * @param srcPath
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getImage(String srcPath, float width, float height) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts); // 此时返回bm为空  

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 360f;// 这里设置高度为800f  
        float ww = 200f;// 这里设置宽度为480f  
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
        int be = 1;// be=1表示不缩放  
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放  
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放  
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例  
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩  
    }

    /**
     * 将Bitmap压缩后保存到指定目录生成图片
     *
     * @param bitmap  图片
     * @param path    SD卡路径
     * @param name    图片名称
     * @param quality 图像压缩比的值,0-100 意味着小尺寸压缩,100意味着高质量压缩
     */
    public static void compressAndSaveBitmap(Bitmap bitmap, String path, String name, int quality) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fullName = path + name;
        File f = new File(fullName);

        try {
            FileOutputStream fos = new FileOutputStream(f);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获取图片的缩略图 
    private Bitmap getBitmapThumbnail(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
//true那么将不返回实际的bitmap对象,不给其分配内存空间但是可以得到一些解码边界信息即图片大小等信息 
        options.inJustDecodeBounds = true;
//此时rawBitmap为null 
        Bitmap rawBitmap = BitmapFactory.decodeFile(filePath, options);
        if (rawBitmap == null) {
            System.out.println("此时rawBitmap为null");
        }
//inSampleSize表示缩略图大小为原始图片大小的几分之一,若该值为3 
//则取出的缩略图的宽和高都是原始图片的1/3,图片大小就为原始大小的1/9 
//计算sampleSize 
        int sampleSize = computeSampleSize(options, 150, 200 * 200);
//为了读到图片,必须把options.inJustDecodeBounds设回false 
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
//原图大小为625x690 90.2kB 
//测试调用computeSampleSize(options, 100, 200*100); 
//得到sampleSize=8 
//得到宽和高位79和87 
//79*8=632 87*8=696 
        Bitmap thumbnailBitmap = BitmapFactory.decodeFile(filePath, options);
//保存到SD卡方便比较 
        return thumbnailBitmap;
    }


    /**
     * 图像压缩
     *
     * @param options        原本Bitmap的options
     * @param minSideLength  希望生成的缩略图的宽高中的较小的值
     * @param maxNumOfPixels 希望生成的缩量图的总像素
     * @return
     */
    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int
            maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength,
                                                int maxNumOfPixels) {
        //原始图片的宽 
        double w = options.outWidth;
        //原始图片的高 
        double h = options.outHeight;
        System.out.println("========== w=" + w + ",h=" + h);
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone. 
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}
