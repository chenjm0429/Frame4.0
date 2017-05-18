package com.ztesoft.level1.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 截屏工具类，可对整个activity界面进行截屏(可选是否包含状态栏和标题栏)，也可以对某个view进行截屏
 * 备注1：未在屏幕显示的view，必须用getBitmapFromSimpleView截图
 * 备注2：未在屏幕完成显示的view，只能截出显示部分，除非其父控件为ScrollView、ListView、HorizontalScrollView
 * 备注3：ScrollView、ListView、HorizontalScrollView请使用对应的方法截图
 * 备注4：当截图view子类复杂无法直接截图时，可以考虑分块截图，最后合并的方式（参考表格控件）。
 *
 * @author wangxin
 */
public class BitmapOperateUtil {
    /**
     * 对activity页面进行截屏，可选择截图是否包含状态栏和标题栏
     * 如果只有isTitleTab为false，则不做处理（当包含状态栏时，肯定包含标题栏）
     *
     * @param activity   activity界面
     * @param isTitleTab 是否包含状态栏，true:包含，false:不包含
     * @param isTitleTab 是否包含标题栏，true:包含，false:不包含
     * @return
     */
    public static Bitmap getBitmapFromActivity(Activity activity, boolean isStatusTab, boolean
            isTitleTab) {
        // View是需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap ttt = view.getDrawingCache();
        Bitmap b1 = Bitmap.createBitmap(ttt);
        ttt.recycle();
        view.setDrawingCacheEnabled(false);
        if (!isStatusTab) {
            // 获取状态栏高度
            Rect frame = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top; // 状态栏高度
            if (statusBarHeight < 0)
                statusBarHeight = 0;

            int width = b1.getWidth();
            int height = b1.getHeight();

            // 获取标题栏高度
            Rect outRect = new Rect();
            activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(outRect);
            int titleBarHeight = height - outRect.height() - statusBarHeight; // 标题栏高度

            // 去掉标题栏和状态栏
            if (!isTitleTab && !isStatusTab)
                statusBarHeight += titleBarHeight;

            Bitmap b2 = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height -
                    statusBarHeight);
            b1.recycle();
            return b2;
        }
        return b1;
    }

    /**
     * 对activity页面进行截屏，包含标题栏，不包含状态栏
     *
     * @param activity activity界面
     * @return
     */
    public static Bitmap getBitmapFromActivity(Activity activity) {
        return getBitmapFromActivity(activity, false, true);
    }

    public static Bitmap getBitmapFromSimpleView(View view) {
        Bitmap bitmap = null;
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec
                .makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap b = view.getDrawingCache();
        bitmap = Bitmap.createBitmap(b);
        return bitmap;
    }

    /**
     * 对view进行截屏
     *
     * @param view view视图
     * @return
     */
    public static Bitmap getBitmapFromView(View view) {
        return getBitmapFromView(view, view.getWidth(), view.getHeight());
    }

    public static Bitmap getBitmapFromView(View view, int width, int height) {
        Bitmap bitmap = null;
        if (view != null) {
            view.clearFocus();
            view.setPressed(false);
            // int color = view.getDrawingCacheBackgroundColor();
            // view.setDrawingCacheBackgroundColor(0);
            // if (color != 0) {
            // view.destroyDrawingCache();
            // }
            // view.setDrawingCacheEnabled(true);
            // view.setWillNotCacheDrawing(false);
            // view.measure(MeasureSpec.makeMeasureSpec(0,
            // MeasureSpec.UNSPECIFIED),
            // MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            // view.layout(0, 0, view.getMeasuredWidth(),
            // view.getMeasuredHeight());
            // view.buildDrawingCache();
            // Bitmap b = view.getDrawingCache(true);
            // bitmap = Bitmap.createBitmap(b);
            // Restore the view
            // view.destroyDrawingCache();
            // view.setDrawingCacheBackgroundColor(color);

            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
        }
        return bitmap;
    }

    /**
     * 获取ScrollView或ListView的截图 4.0以下版本截图时分页处有阴影
     *
     * @param scrollView
     * @return
     */
    public static Bitmap getBitmapFromScrollView(ScrollView scrollView) {
        scrollView.clearFocus();
        int h = 0;
        Bitmap bitmap;
        // 获取listView实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }
//		Log.d(TAG, "实际高度:" + h);
//		Log.d(TAG, " 高度:" + scrollView.getHeight());
        int fadingEdgeLength = scrollView.getVerticalFadingEdgeLength();
        scrollView.setVerticalFadingEdgeEnabled(false);
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        if (fadingEdgeLength != 0) {
            scrollView.setVerticalFadingEdgeEnabled(true);
        }
        return bitmap;
    }

    /**
     * 获取HorizontalScrollView的截图
     *
     * @param hScrollView
     * @return
     */
    public static Bitmap getBitmapFromHScrollView(HorizontalScrollView hScrollView) {
        hScrollView.clearFocus();
        int w = 0;
        Bitmap bitmap;
        // 获取hScrollView实际宽度
        for (int i = 0; i < hScrollView.getChildCount(); i++) {
            w += hScrollView.getChildAt(i).getWidth();
        }
        int fadingEdgeLength = hScrollView.getHorizontalFadingEdgeLength();
        hScrollView.setHorizontalFadingEdgeEnabled(false);
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(w, hScrollView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        hScrollView.draw(canvas);
        if (fadingEdgeLength != 0) {
            hScrollView.setHorizontalFadingEdgeEnabled(true);
        }
        return bitmap;
    }

    /**
     * 图片合并（用于添加水印,水印平铺展现）
     *
     * @param background 底部图形
     * @param foreground 顶部图形（水印）
     * @return
     */
    public static Bitmap toConformBitmap(Bitmap background, Bitmap foreground) {
        if (background == null) {
            return null;
        }

        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        // create the new blank bitmap 创建一个新的和SRC长度宽度一样的位图
        Bitmap newbmp = Bitmap.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
        Canvas cv = new Canvas(newbmp);
        // draw bg into
        cv.drawBitmap(background, 0, 0, null);// 在 0，0坐标开始画入bg
        // 循环添加水印
        int w = foreground.getWidth();
        int wnum = bgWidth / w + (bgWidth % w > 0 ? 1 : 0);
        int h = foreground.getHeight();
        int hnum = bgHeight / h + (bgHeight % w > 0 ? 1 : 0);
        for (int i = 0; i < hnum; i++) {
            for (int j = 0; j < wnum; j++) {
                cv.drawBitmap(foreground, 0 + j * w, 0 + i * h, null);// 在
                // 0,0坐标开始画入fg,可以从任意位置画入
            }
        }
        // save all clip
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        // store
        cv.restore();// 存储
        return newbmp;
    }

    /**
     * 图片保存为文件，不压缩
     *
     * @param bitmap
     * @param file   图片文件（文件必须存在 ）
     */
    public static void saveBitmapFile(Bitmap bitmap, File file) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 压缩并保存文件
     *
     * @param bmp
     * @param file     图片文件（文件必须存在 ）
     * @param fileSize 压缩后文件大小不超过maxSize(K)，默认100
     */
    public static void saveBitmapFile(Bitmap bmp, File file, int fileSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 80;// 个人喜欢从80开始,
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length / 1024 > fileSize) {
            baos.reset();
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务器图片URL转换为bitmap，不压缩（可能会导致内存溢出，慎用
     *
     * @param bmpUrl
     * @return
     */
    public static Bitmap getBitmapFormRemote(String bmpUrl) {
        InputStream is = null;
        try {
            Bitmap bitmap;
            URL imageUrl = new URL(bmpUrl);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            // 取得返回的InputStream
            is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 服务器图片URL转换为bitmap
     *
     * @param bmpUrl
     * @param hh     压缩后高度,为0表示不压缩
     * @param ww     压缩后宽度,为0表示不压缩
     * @return
     */
    public static Bitmap getBitmapFormRemote(String bmpUrl, float hh, float ww) {
        if (hh == 0 || ww == 0) {//如果压缩后高度或宽度为0，则表示不压缩
            return getBitmapFormRemote(bmpUrl);
        }
        InputStream is = null;
        try {
            URL imageUrl = new URL(bmpUrl);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            // 取得返回的InputStream
            is = conn.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray
                    ()), null, o);
            o.inJustDecodeBounds = false;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            if (width_tmp > height_tmp && width_tmp > ww) {
                scale = (int) (o.outWidth / ww);
            } else if (width_tmp < height_tmp && height_tmp > hh) {
                scale = (int) (o.outHeight / hh);
            }
            if (scale <= 0)
                scale = 1;
            o.inSampleSize = scale;// 设置采样率
            o.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
            o.inPurgeable = true;// 同时设置才会有效
            o.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

            bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()),
                    null, o);
            return bitmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从资源文件读取到bitmap
     *
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap getBitmapFormRes(Context context, int resId) {
        Bitmap bb = BitmapFactory.decodeResource(context.getResources(), resId);
        return bb;
    }

    /**
     * 从文件读取到bitmap，不压缩（可能会导致内存溢出，慎用
     *
     * @param srcPath
     * @return
     */
    public static Bitmap getBitmapFormFile(String srcPath) {
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath);
        return bitmap;
    }

    /**
     * 从文件读取到bitmap，压缩分辨率
     *
     * @param srcPath
     * @param hh      压缩后高度
     * @param ww      压缩后宽度
     * @return
     */
    public static Bitmap getBitmapFormFile(String srcPath, float hh, float ww) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;// 只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置采样率

        newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return bitmap;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param angle  旋转角度
     * @param bitmap 要旋转的图片
     * @return Bitmap 旋转后的图片
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        if (bitmap != null) {
            // 旋转图片 动作
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            // System.out.println("angle2=" + angle);
            // 创建新的图片
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                    matrix, true);
        }
        return bitmap;
    }

    /**
     * 计算图片的缩放值
     *
     * @param options   图片实际信息（宽高）
     * @param reqWidth  缩放后宽度
     * @param reqHeight 缩放后高度
     * @return 缩放比例值
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int
            reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}