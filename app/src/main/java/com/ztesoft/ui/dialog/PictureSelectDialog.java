package com.ztesoft.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.ztesoft.R;
import com.ztesoft.fusion.FusionCode;
import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.Level1Util;
import com.ztesoft.utils.Utils;

import java.io.File;

/**
 * 文件名称 : PictureSelectDialog
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 图片选择弹出框
 * <p>
 * 创建时间 : 2018/6/8 13:57
 * <p>
 */
public class PictureSelectDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private Activity activity;

    private TextView mCameraText, mPictureText;
    private TextView mCancelText;

    public final static int CONSULT_DOC_PICTURE = 1066;  //图库
    public final static int CONSULT_DOC_CAMERA = 1068;

    private Uri outputFileUri;

    private int maxLength = 3;

    public PictureSelectDialog(@NonNull Context context, int maxLength) {
        this(context, R.style.other_dialog_style, maxLength);

    }

    public PictureSelectDialog(@NonNull Context context, int themeResId, int maxLength) {
        super(context, themeResId);

        this.context = context;
        this.activity = (Activity) context;
        this.maxLength = maxLength;

        String path1 = Level1Bean.SD_ROOTPATH + FusionCode.IMAGES_LOCAL_PATH;
        File f1 = new File(path1);
        if (!f1.exists())
            f1.mkdirs();

        String path2 = Level1Bean.SD_ROOTPATH + FusionCode.FILE_LOCAL_PATH + "Temp/";
        File f2 = new File(path2);
        if (!f2.exists())
            f2.mkdirs();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_head_change_pop);

        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();

        lp.width = Utils.getDeviceWidth(context) - 40; // 宽度
        lp.alpha = 1.0f; // 透明度

        dialogWindow.setAttributes(lp);

        mCameraText = findViewById(R.id.camera);
        mCameraText.setOnClickListener(this);
        mPictureText = findViewById(R.id.picture);
        mPictureText.setOnClickListener(this);
        mCancelText = findViewById(R.id.cancel);
        mCancelText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mCameraText)) {

            File file = new File(Level1Bean.SD_ROOTPATH + FusionCode.IMAGES_LOCAL_PATH,
                    "camera_temp.jpg");
            outputFileUri = Uri.fromFile(file);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            activity.startActivityForResult(intent, CONSULT_DOC_CAMERA);

            dismiss();

        } else if (v.equals(mPictureText)) {

            Matisse.from(activity)
                    .choose(MimeType.of(MimeType.JPEG, MimeType.PNG)) // 选择 mime 的类型 
                    .countable(true)
//                    .capture(true)  //是否提供拍照功能
//                    .captureStrategy(new CaptureStrategy(true, "com.ztesoft.govmrkt.dev.smart" +
//                            ".river.chief.fileprovider"))  //存储到哪里 
                    .maxSelectable(maxLength) // 图片选择的最多数量 
                    .gridExpectedSize(Level1Util.dip2px(context, 120))
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f) // 缩略图的比例 
                    .theme(R.style.picture_select_style)
                    .imageEngine(new GlideEngine()) // 使用的图片加载引擎 
                    .forResult(CONSULT_DOC_PICTURE); // 设置作为标记的请求码 

            dismiss();

        } else if (v.equals(mCancelText)) {
            dismiss();
        }
    }

    public Uri getOutputFileUri() {
        return outputFileUri;
    }
}
