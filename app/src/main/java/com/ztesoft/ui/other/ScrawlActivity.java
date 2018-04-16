package com.ztesoft.ui.other;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.ztesoft.R;
import com.ztesoft.fusion.FusionCode;
import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.scrawl.ScrawlView;
import com.ztesoft.level1.util.BitmapOperateUtil;
import com.ztesoft.ui.base.BaseActivity;
import com.ztesoft.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.Call;

/**
 * 文件名称 : ScrawlActivity
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 涂鸦页面
 * <p>
 * 创建时间 : 2017/3/27 16:57
 * <p>
 */
public class ScrawlActivity extends BaseActivity {

    private ImageView colorBtn; // 画笔颜色
    private ImageView weightBtn; // 画笔粗细
    private ImageView revocationBtn; // 撤销
    private ImageView clearBtn; // 清空

    private LinearLayout bodyLayout;

    private ScrawlView mainView;

    private PopupWindow popWindow;

    private Bitmap mBitmap;

    private String type;

    @Override
    protected void getBundles(Bundle bundle) {
        if (null != bundle) {
            String filePath = bundle.getString("filePath", "");
            mBitmap = BitmapFactory.decodeFile(filePath);

            type = bundle.getString("type", "");
        }
    }

    @Override
    protected void initView(FrameLayout containerLayout) {

        mTitleLayout.setVisibility(View.GONE);

        View.inflate(this, R.layout.activity_scrawl, containerLayout);

        findViewById(R.id.back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainView.getSavePath().size() == 0)
                    back();
                else
                    createDialog().show();
            }
        });

        findViewById(R.id.right_text).setOnClickListener(mOnClickListener);

        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(Utils.getDeviceWidth(this) - 20, Utils.getDeviceHeight
                    (this) - 50, Config.ARGB_8888);
        }

        Matrix matrix = new Matrix();
        matrix.postScale(1f, 0.9f); // 长和宽放大缩小的比例
        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(),
                matrix, true);
        mainView = new ScrawlView(this, mBitmap);

        initParams();

        bodyLayout.addView(mainView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        Drawable drawable = new BitmapDrawable(getResources(), mBitmap);
        bodyLayout.setBackgroundDrawable(drawable);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mainView.getSavePath().size() == 0) {
            back();
        } else {
            createDialog().show();
        }
    }

    private void initParams() {

        colorBtn = (ImageView) findViewById(R.id.iv_btn_color);
        weightBtn = (ImageView) findViewById(R.id.iv_btn_weight);
        revocationBtn = (ImageView) findViewById(R.id.iv_btn_revocation);
        clearBtn = (ImageView) findViewById(R.id.iv_btn_clear);

        bodyLayout = (LinearLayout) findViewById(R.id.body);

        colorBtn.setOnClickListener(mOnClickListener);
        weightBtn.setOnClickListener(mOnClickListener);
        revocationBtn.setOnClickListener(mOnClickListener);
        clearBtn.setOnClickListener(mOnClickListener);
    }

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.iv_btn_color: // 画笔颜色
                    showPopupWindow(v);
                    break;

                case R.id.iv_btn_weight: // 画笔粗细
                    showPopupWindow(v);
                    break;

                case R.id.iv_btn_revocation: // 撤销
                    mainView.revocation();
                    break;

                case R.id.iv_btn_clear: // 清空
                    mainView.setEraser(true);
                    break;

                case R.id.right_text:// 完成
                    Bitmap bitmap = mainView.getBitmap();

                    String fileName = System.currentTimeMillis() + ".png";
                    String returnFile = Level1Bean.SD_ROOTPATH + FusionCode.MAIL_LOCAL_PATH +
                            fileName;

                    BitmapOperateUtil.saveBitmapFile(bitmap, new File(returnFile));

                    String shareInfo = "";

                    if (type.equals("MMS")) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(returnFile)));
                        // uri为你的附件的uri
                        intent.putExtra("subject", getString(R.string.app_name) +
                                "截图分享"); //彩信的主题
                        intent.putExtra("sms_body", shareInfo); //彩信中文字内容
                        intent.setType("image/*");// 彩信附件类型
                        intent.setPackage("com.android.mms");
                        startActivity(intent);

                    } else if (type.equals("MAIL")) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(returnFile)));
                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) +
                                "截图分享");
                        intent.putExtra(Intent.EXTRA_TEXT, shareInfo);
                        intent.setType("image/*");
                        startActivity(Intent.createChooser(intent, "请选择邮件发送方式"));

                    } else {
                        Intent intent = getIntent();
                        intent.putExtra("returnFile", returnFile);
                        setResult(Activity.RESULT_OK, intent);
                    }

                    back();

                    break;

                default:
                    break;
            }
        }
    };

    private void showPopupWindow(View v) {

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.HORIZONTAL);
        
//        View view = View.inflate(this, R.layout.dialog_scrawl_paint, null);
        popWindow = new PopupWindow(container, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

//        LinearLayout container = (LinearLayout) view.findViewById(R.id.container);
        LayoutParams lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);

        if (v.equals(colorBtn)) {

            for (int i = 0; i < ScrawlView.PAINT_COLORS_DRAWABLES.length; i++) {

                View item = View.inflate(ScrawlActivity.this, R.layout.view_scrawl_paint_item,
                        null);
                ImageView image = (ImageView) item.findViewById(R.id.image);
                image.setImageResource(ScrawlView.PAINT_COLORS_DRAWABLES[i]);

                container.addView(item, lp);

                final int xx = i;
                item.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mainView.setCurrentColor(ScrawlView.PAINT_COLORS[xx]);
                        colorBtn.setImageResource(ScrawlView.PAINT_COLORS_DRAWABLES[xx]);
                        popWindow.dismiss();
                    }
                });
            }

        } else if (v.equals(weightBtn)) {

            for (int i = 0; i < ScrawlView.PAINT_SIZES.length; i++) {

                View item = View.inflate(ScrawlActivity.this, R.layout.view_scrawl_paint_item,
                        null);
                ImageView image = (ImageView) item.findViewById(R.id.image);
                image.setImageResource(ScrawlView.PAINT_SIZES_DRAWABLES[i]);

                container.addView(item, lp);

                final int xx = i;
                item.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mainView.setCurrentSize(ScrawlView.PAINT_SIZES[xx]);
                        popWindow.dismiss();
                    }
                });
            }
        }

        popWindow.setFocusable(true);
        popWindow.setBackgroundDrawable(new ColorDrawable());
        popWindow.setOutsideTouchable(true);
        popWindow.showAtLocation(bodyLayout, Gravity.BOTTOM | Gravity.LEFT, 20, colorBtn
                .getHeight());

        popWindow.update();
    }

    @Override
    protected void initAllLayout(JSONObject resultJsonObject, Call call) throws Exception {

    }

    @Override
    public void addParamObject(JSONObject param) throws JSONException {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != mBitmap) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    /**
     * 创建保存弹出框
     *
     * @return 弹出框
     */
    private Dialog createDialog() {

        final Dialog dialog = new Dialog(this, R.style.no_bg_dialog_style);
        dialog.setContentView(R.layout.dialog_scrawl_tip);

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = Utils.getDeviceWidth(this) * 9 / 10; // 宽度
        dialogWindow.setAttributes(lp);

        Button cancelBtn = (Button) dialog.findViewById(R.id.cancel);
        Button confirmBtn = (Button) dialog.findViewById(R.id.confirm);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        return dialog;
    }
}
