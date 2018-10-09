package com.ztesoft.ui.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.BadTokenException;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ztesoft.MainApplication;
import com.ztesoft.R;
import com.ztesoft.fusion.FusionCode;
import com.ztesoft.fusion.GlobalField;
import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.Level1Util;
import com.ztesoft.level1.util.BitmapOperateUtil;
import com.ztesoft.level1.util.PromptUtils;
import com.ztesoft.level1.util.RequestManager;
import com.ztesoft.level1.util.SDCardUtil;
import com.ztesoft.level1.util.SharedPreferencesUtil;
import com.ztesoft.ui.other.ScrawlActivity;
import com.ztesoft.utils.ErrorLogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.Call;

/**
 * 文件名称 : BaseActivity
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 整个应用的Activity继承父类
 * <p>
 * 创建时间 : 2017/3/23 16:54
 * <p>
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * 正在加载弹出框
     */
    protected Dialog mLoadingDialog = null;

    /**
     * 功能模块编码
     */
    protected String rptCode;

    /**
     * 用户id
     */
    protected String userId;

    /**
     * 时间
     */
    protected String statDate;

    /**
     * 水印图片
     */
    protected BitmapDrawable mWatermark;

    /**
     * 界面切换动画枚举
     */
    public enum ANIM_TYPE {
        NONE, LEFT, RIGHT
    }

    /**
     * 标题文本框
     */
    protected TextView mTitleTv;
    /**
     * 标题内容
     */
    protected String title;
    /**
     * 标题布局
     */
    protected RelativeLayout mTitleLayout;

    /**
     * 左侧按钮；右侧按钮
     */
    protected ImageView mLeftButton, mRightButton;
    /**
     * 右侧的第二个按钮，默认隐藏
     */
    protected ImageView mSpecialButton;

    protected SharedPreferencesUtil spu;
    protected GlobalField gf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        MainApplication application = (MainApplication) this.getApplication();
        application.getActivityManager().pushActivity(this);

        spu = new SharedPreferencesUtil(this, Level1Bean.SHARE_PREFERENCES_NAME);
        gf = ((MainApplication) getApplication()).getGlobalField();

        initData();

        initFrameView(savedInstanceState);
    }

    private void initData() {
        userId = gf.getUserId();

        Bundle bundle = getIntent().getExtras();
        getBundles(bundle);
    }

    private void initFrameView(Bundle savedInstanceState) {

        setContentView(R.layout.activity_base);

        FrameLayout containerLayout = (FrameLayout) this.findViewById(R.id.container);
        // 增加水印
//        containerLayout.setForeground(getWatermark());

        mTitleTv = findViewById(R.id.title);
        mTitleLayout = findViewById(R.id.titleLayout);

        mLeftButton = findViewById(R.id.left_image);
        mLeftButton.setOnClickListener(mOnClickListener);
        mRightButton = findViewById(R.id.right_image);
        mRightButton.setOnClickListener(mOnClickListener);
        mSpecialButton = findViewById(R.id.special_image);

        initView(containerLayout);
        getSystemState(savedInstanceState);
    }

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.equals(mLeftButton)) {
                back();

            } else if (v.equals(mRightButton)) {
                Message msg = takeScreenHandler.obtainMessage();
                msg.what = 1;
                takeScreenHandler.sendMessage(msg);
            }
        }
    };

    /**
     * 获取Intent数据
     *
     * @param bundle 传递的数据
     */
    protected abstract void getBundles(Bundle bundle);

    /**
     * 初始化界面
     *
     * @param containerLayout 在其中添加自定义视图
     */
    protected abstract void initView(FrameLayout containerLayout);

    /**
     * 需要获取系统保存状态时，调用该方法
     *
     * @param savedInstanceState
     */
    protected void getSystemState(Bundle savedInstanceState) {

    }

    /**
     * 添加个性化请求参数
     *
     * @param param 向该object中添加参数
     * @throws JSONException
     */
    protected abstract void addParamObject(JSONObject param) throws JSONException;

    /**
     * 返回数据构造页面
     *
     * @param resultJsonObject 返回的数据
     * @throws Exception
     */
    protected abstract void initAllLayout(JSONObject resultJsonObject, Call call) throws Exception;

    /**
     * 添加通用请求参数
     *
     * @param param 所有参数对象
     * @throws JSONException
     */
    private void setCommonParam(JSONObject param) throws JSONException {

        param.put("rptCode", rptCode);
        param.put("statDate", statDate);
        param.put("userId", userId);
    }

    /**
     * 请求数据，默认带加载框
     *
     * @param visitType   访问标志
     * @param pathCode    路径
     * @param requestType 请求方式
     * @return
     */
    public Call queryData(String visitType, String pathCode, int requestType) {

        return queryData(visitType, pathCode, requestType, true, getString(R.string.loading));
    }

    /**
     * 请求数据
     *
     * @param visitType       访问标志
     * @param pathCode        路径
     * @param requestType     请求方式
     * @param isShowTipDialog 是否显示提示框
     * @param showContent     提示框内容
     * @return
     */
    public Call queryData(String visitType, String pathCode, int requestType, boolean
            isShowTipDialog, String showContent) {

        if (isShowTipDialog) {
            if (!TextUtils.isEmpty(showContent)) {
                showLoadingDialog(showContent, 0);
            } else {
                showLoadingDialog(null, R.string.loading);
            }
        }

        // 判断用户权限，并预录入相关信息
        JSONObject requestParams = new JSONObject();
        try {
            if (!TextUtils.isEmpty(visitType))
                requestParams.put("visitType", visitType);
            setCommonParam(requestParams);
            addParamObject(requestParams);
        } catch (JSONException e) {
            PromptUtils.instance.displayToastId(BaseActivity.this, false, R.string
                    .error_client_json);
        }

        String url = getString(R.string.servicePath) + getString(R.string.serviceUrl) + pathCode;
        RequestManager manage = RequestManager.getInstance(this);

        JSONObject headObj = new JSONObject();
        try {
            headObj.put("token", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        manage.setOtherHeaderObject(headObj);

        Call call = manage.requestAsyn(url, requestType, requestParams, reqCallBack);

        return call;
    }

    protected RequestManager.ReqCallBack reqCallBack = new RequestManager.ReqCallBack() {

        @Override
        public void onReqSuccess(Object result, Call tag) {
            try {
                initAllLayout(new JSONObject((String) result), tag);

            } catch (JSONException e) {
                ErrorLogUtil.getInstance().log(getApplicationContext(), e.getMessage());
                PromptUtils.instance.displayToastId(BaseActivity.this, false, R.string.error_json);
            } catch (Exception e) {
                ErrorLogUtil.getInstance().log(getApplicationContext(), e.getMessage());
                PromptUtils.instance.displayToastId(BaseActivity.this, false, R.string
                        .error_unknown);
            } finally {
                dismissLoadingDialog();
            }
        }

        @Override
        public void onReqFailed(int errorCode, String errorMsg) {
            dismissLoadingDialog();

            PromptUtils.instance.displayToastString(BaseActivity.this, false, errorMsg);
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        back();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        dismissLoadingDialog();
    }

    /**
     * 显示进度加载框
     *
     * @param msgId 文字id
     */
    public void showLoadingDialog(String msg, int msgId) {
        if (null != mLoadingDialog && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
        // 显示进度框
        mLoadingDialog = PromptUtils.instance.initLoadingDialog(this, msg, msgId, true);

        try {
            mLoadingDialog.show();
        } catch (BadTokenException ex) {
            // dialog依附的activity如果被销毁，会抛出改异常
            Log.e(this.getClass().getSimpleName(), "photomodule exp：" + ex.getMessage());
        }
    }

    /**
     * 隐藏加载框
     */
    public void dismissLoadingDialog() {
        if (null != mLoadingDialog && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    /**
     * 关闭当前页面
     */
    public void back() {

        BaseActivity.this.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    /**
     * 跳转到其他界面
     *
     * @param context    当前界面
     * @param bundle     传递参数
     * @param otherClass 目标界面
     * @param isFinish   跳转后是否关闭当前界面
     * @param animType   页面跳转动画，支持left，right 默认无动画
     */
    public void forward(Context context, Bundle bundle, Class<?> otherClass, boolean isFinish,
                        ANIM_TYPE animType) {
        Intent intent = new Intent(context, otherClass);

        if (null != bundle) {
            intent.putExtras(bundle);
        }

        context.startActivity(intent);

        if (isFinish) {
            ((Activity) context).finish();
        }

        Activity target = ((Activity) context).getParent();
        if (null == target) {
            target = (Activity) context;
        }
        if (animType == ANIM_TYPE.LEFT) {
            target.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

        } else if (animType == ANIM_TYPE.RIGHT) {
            target.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        }
    }

    /**
     * 跳转到其他界面，带返回结果
     *
     * @param context     当前界面
     * @param bundle      传递参数
     * @param otherClass  目标界面
     * @param requestCode 标志位
     * @param isFinish    跳转后是否关闭当前界面
     * @param animType    页面跳转动画，支持left，right 默认无动画
     */
    public void forwardForResult(Context context, Bundle bundle, Class<?> otherClass, int
            requestCode, boolean isFinish, ANIM_TYPE animType) {
        Intent intent = new Intent(context, otherClass);

        if (null != bundle) {
            intent.putExtras(bundle);
        }

        ((Activity) context).startActivityForResult(intent, requestCode);

        if (isFinish) {
            ((Activity) context).finish();
        }

        Activity target = ((Activity) context).getParent();
        if (null == target) {
            target = (Activity) context;
        }
        if (animType == ANIM_TYPE.LEFT) {
            target.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

        } else if (animType == ANIM_TYPE.RIGHT) {
            target.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        }
    }

    /**
     * 获取水印图片
     *
     * @return BitmapDrawable
     */
    protected BitmapDrawable getWatermark() {
        if (mWatermark == null) {
            Paint p = new Paint();
            Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
            p.setColor(Color.parseColor("#bac0c8"));
            p.setTypeface(font);
            p.setAntiAlias(true);
            p.setTextSize(Level1Util.getSpSize(20));
            p.setAlpha(50);
            String staff_id = gf.getUserId();
            String staff_name = "";
            String s = staff_id + ":" + staff_name;
            int w = (int) p.measureText(s);
            FontMetrics fm = p.getFontMetrics();
            int h = (int) Math.ceil(fm.descent - fm.ascent);

            Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(newb);
            canvasTemp.drawText(s, 0, h, p);
            Matrix matrix = new Matrix();
            // 设置图像的旋转角度
            matrix.setRotate(-45);
            // 旋转图像，并生成新的Bitmap对像
            newb = Bitmap.createBitmap(newb, 0, 0, newb.getWidth(), newb.getHeight(), matrix, true);
            mWatermark = new BitmapDrawable(getResources(), newb);
            mWatermark.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
        }

        return mWatermark;
    }

    /**
     * 截屏方法
     */
    protected final Handler takeScreenHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {

            Activity target = BaseActivity.this.getParent();
            if (null == target) {
                target = BaseActivity.this;
            }
            Bitmap bitmap = BitmapOperateUtil.getBitmapFromActivity(target);

            String fileName = System.currentTimeMillis() + ".png";
            if (!SDCardUtil.getInstance().isFileExist(FusionCode.MAIL_LOCAL_PATH))
                SDCardUtil.getInstance().createSDDir(FusionCode.MAIL_LOCAL_PATH);
            String returnFile = Level1Bean.SD_ROOTPATH + FusionCode.MAIL_LOCAL_PATH + fileName;

            BitmapOperateUtil.saveBitmapFile(bitmap, new File(returnFile));
            if (returnFile.length() > 0) {
                Bundle bundle = new Bundle();
                bundle.putString("filePath", returnFile);
                bundle.putString("type", "MAIL");
                forward(BaseActivity.this, bundle, ScrawlActivity.class, false, ANIM_TYPE.LEFT);

            } else {
                PromptUtils.instance.displayToastString(BaseActivity.this, false, "截屏失败！");
            }
        }
    };
}