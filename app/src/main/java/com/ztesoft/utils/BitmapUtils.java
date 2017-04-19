package com.ztesoft.utils;

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

import com.ztesoft.fusion.FusionCode;
import com.ztesoft.level1.Level1Bean;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
     * @param bitmap
     * @param zf
     * @return
     */
    public static Bitmap zoom(Bitmap bitmap, float zf) {
        Matrix matrix = new Matrix();
        matrix.postScale(zf, zf);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
                true);
    }

    /**
     * 缩放图片
     *
     * @param bitmap
     * @param wf
     * @param hf
     * @return
     */
    public static Bitmap zoom(Bitmap bitmap, float wf, float hf) {
        Matrix matrix = new Matrix();
        matrix.postScale(wf, hf);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
                true);
    }

    /**
     * 图片圆角处理
     *
     * @param bitmap
     * @param roundPX
     * @return
     */
    public static Bitmap getRCB(Bitmap bitmap, float roundPX) {
        // RCB means
        // Rounded
        // Corner Bitmap
        Bitmap dstbmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config
                .ARGB_8888);
        Canvas canvas = new Canvas(dstbmp);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPX, roundPX, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return dstbmp;
    }

    /**
     * 质量压缩方法
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
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);

        return bitmap;
    }

    /**
     * 图片按比例大小压缩方法（根据路径获取图片并压缩）
     *
     * @param srcPath
     * @return
     */
    public static Bitmap getImage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空  

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
     * 将压缩的bitmap保存到SDCard卡临时文件夹，用于上传
     *
     * @param filename
     * @param bit
     * @return
     */
    public static String saveMyBitmap(String filename, Bitmap bit) {
        String baseDir = Level1Bean.SD_ROOTPATH + FusionCode.FILE_LOCALPATH + "Temp/";
        String filePath = baseDir + filename;
        File dir = new File(baseDir);
        if (!dir.exists()) {
            dir.mkdir();
        }

        File f = new File(filePath);
        try {
            f.createNewFile();
            FileOutputStream fOut = null;
            fOut = new FileOutputStream(f);
            bit.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return filePath;
    }

    public static void saveBitmap(Bitmap mBitmap, String path, String name, boolean isJpg, String
            type) {
        BufferedOutputStream bos = null;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fullName = path + name + "." + type;
        File f = new File(fullName);

        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            bos = new BufferedOutputStream(fOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (isJpg) {
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 10, bos);
        } else {
            mBitmap.compress(Bitmap.CompressFormat.PNG, 10, bos);
        }
        try {
            fOut.flush();
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
