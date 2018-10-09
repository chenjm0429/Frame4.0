package com.ztesoft.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Environment;
import android.view.View;

import com.ztesoft.MainApplication;
import com.ztesoft.R;
import com.ztesoft.fusion.FusionCode;
import com.ztesoft.fusion.GlobalField;
import com.ztesoft.level1.Level1Util;
import com.ztesoft.level1.util.PromptUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 文件名称 : TakeScreenPic
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 截屏保存图片，图片可以加水印
 * <p>
 * 创建时间 : 2017/3/24 9:46
 * <p>
 */
public class TakeScreenPic {

    /**
     * 截图加水印
     *
     * @throws IOException
     */
    public Bitmap paintBitmap(Bitmap bmp, Context context) throws IOException {

        GlobalField gf = ((MainApplication) ((Activity) context).getApplication()).getGlobalField();

        String mstrTitle = "用户ID:" + gf.getUserId();
        String mstrTitle1 = "用户名:" + "";
        int height = bmp.getHeight();
        int width = bmp.getWidth();

        Bitmap bb = bmp.copy(Config.ARGB_8888, true);

        // 图象大小要根据文字大小算下,以和文本长度对应
        Canvas canvasTemp = new Canvas(bb);
        Paint p = new Paint();
        String familyName = "宋体";
        Typeface font = Typeface.create(familyName, Typeface.BOLD);
        p.setColor(Color.parseColor("#898989"));
        p.setTypeface(font);
        p.setTextSize(30);
        p.setAlpha(80);

        Matrix matrix = new Matrix();
        matrix.setRotate(325);
        canvasTemp.setMatrix(matrix);

        Bitmap iconBg = BitmapFactory.decodeResource(context.getResources(), R.mipmap
                .ic_launcher).copy(Config.ARGB_8888, true);

        boolean flag = true;
        for (int i = 0; i < height; i += 200) {

            for (int j = -i; j < width; j += 300) {

                if (flag) {
                    canvasTemp.drawText(mstrTitle, j, i, p);
                    canvasTemp.drawText(mstrTitle1, j, i + 30, p);
                } else {
                    canvasTemp.drawBitmap(iconBg, j, i, null);
                }
            }

            flag = !flag;
        }

        iconBg.recycle();

        canvasTemp.save();// 保存
        canvasTemp.restore();// 存储
        return bb;
    }

    public Bitmap getBitmapFormRemote(String bmpUrl) throws IOException {
        URL url = new URL(bmpUrl);
        URLConnection conn = url.openConnection();

        // Object file1 = url.getContent();
        InputStream is = conn.getInputStream();

        Bitmap bb = BitmapFactory.decodeStream(is);

        return bb;
    }

    /**
     * 截图保存
     **/
    public String savePic(Bitmap b, Context context) {
        String fileName = System.currentTimeMillis() + ".png";
        String filePath;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// SD卡存在且可读写
            filePath = Environment.getExternalStorageDirectory() + FusionCode.MAIL_LOCAL_PATH;
        } else {
            return "";
        }
        File f = new File(filePath);
        if (!f.exists()) {
            f.mkdirs();
        }

        // 删除24小时前的邮件附件
        File[] files = f.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isFile()) {
                String name = file.getName();
                try {
                    // 如果文件名的时间戳小于当前日期戳-24小时，则删除
                    int index1 = name.lastIndexOf(".png");
                    int index2 = fileName.lastIndexOf(".png");
                    if (Long.parseLong(name.substring(0, index1)) < Long.parseLong(fileName
                            .substring(0, index2)) - 24 * 60 * 60 * 1000) {
                        file.delete();
                    }
                } catch (Exception e) {
                    file.delete();
                }
            }
        }

        Bitmap bitmap;
        try {
            bitmap = paintBitmap(b, context);
        } catch (IOException e) {
            PromptUtils.instance.displayToastId(context, false, R.string.error_service);
            return null;
        }

        FileOutputStream fos;
        try {
            //SD卡存在且可读写
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                fos = new FileOutputStream(filePath + File.separator + fileName);
            } else {
                fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            }
            if (null != fos) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath + File.separator + fileName;
    }

    /**
     * 获取指定Activity的截屏,去除通知栏、标题栏
     *
     * @param activity
     * @param isShowTitBar 是否展示标题栏
     * @return
     */
    public Bitmap takeScreenShot(Activity activity, boolean isShowTitBar) {
        // View是需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        if (statusBarHeight < 0)
            statusBarHeight = 0;

        if (!isShowTitBar) {
            statusBarHeight = statusBarHeight + Level1Util.dip2px(activity, 50);
        }

        // 去掉通知栏
        Bitmap temp = Bitmap.createBitmap(b1, 0, statusBarHeight, b1.getWidth(), b1.getHeight() -
                statusBarHeight);

        b1.recycle();

        view.destroyDrawingCache();

        return temp;
    }
}