package com.ztesoft.ui.other;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ztesoft.R;
import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.gesture.GesturePatternView;
import com.ztesoft.level1.util.PromptUtils;
import com.ztesoft.level1.util.SharedPreferencesUtil;
import com.ztesoft.ui.base.BaseActivity;
import com.ztesoft.ui.load.LoginBaseActivity;
import com.ztesoft.ui.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * 文件名称 : GestureActivity
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 手势码页面
 * <p>
 * 创建时间 : 2017/3/23 17:29
 * <p>
 */
public class GestureActivity extends LoginBaseActivity {

    private GesturePatternView gpv;

    private Bundle bundle;

    private TextView mTipText; // 提示信息
    private TextView mForgetText; // 忘记手势码

    private String cacheCode;

    private int errorNum = 0;

    /**
     * 手势码状态
     */
    public enum MODE_TYPE {
        LOGIN, CHANGE, DELETE, FIRST, SECOND, ERROR
    }

    private MODE_TYPE mode_type;

    private boolean isChange = false; // 是否是修改

    private SharedPreferencesUtil spu;

    @Override
    protected void getBundles(Bundle bundle) {

        if (bundle != null) {
            int i = bundle.getInt("mode_type");
            mode_type = MODE_TYPE.values()[i];

        } else {
            mode_type = MODE_TYPE.ERROR;
        }
    }

    @Override
    protected void initView(FrameLayout containerLayout) {
        super.initView(containerLayout);

        mRightButton.setVisibility(View.GONE);

//		containerLayout.setForeground(getWatermark());
        View.inflate(this, R.layout.activity_gesture, containerLayout);

        mTipText = findViewById(R.id.tip);
        mForgetText = findViewById(R.id.forget);

        if (mode_type == MODE_TYPE.LOGIN) {
            mTipText.setText(R.string.sign_login);
            findViewById(R.id.titleLayout).setVisibility(View.GONE);

        } else if (mode_type == MODE_TYPE.CHANGE) {
            mTitleTv.setText("修改手势码");
            mTipText.setText(R.string.sign_old);
            isChange = true;

        } else if (mode_type == MODE_TYPE.FIRST) {
            mTitleTv.setText("设置手势码");
            mTipText.setText(R.string.sign_set);
            findViewById(R.id.titleLayout).setVisibility(View.GONE);
            mForgetText.setVisibility(View.GONE);

        } else if (mode_type == MODE_TYPE.DELETE) {
            mTitleTv.setText("删除手势码");
            mTipText.setText(R.string.sign_old);

        } else {
            mTitleTv.setText("手势码");
            mTipText.setText("手势码");
        }

        spu = new SharedPreferencesUtil(this, Level1Bean.SHARE_PREFERENCES_NAME);

        gpv = findViewById(R.id.mLocusPassWordView);
        gpv.setOnCompleteListener(new GesturePatternView.OnCompleteListener() {
            @Override
            public void onComplete(String mPassword) {

                if (mode_type == MODE_TYPE.LOGIN) {
                    if (gpv.verifyPassword(mPassword)) {
                        if (null != bundle) {
//                            reqLogin();
                        } else
                            PromptUtils.instance.displayToastString(GestureActivity.this, false,
                                    "登录获取的应用信息为空，请重新登录！");

                    } else {
                        gpv.markError(1000);
                        errorNum++;

                        if (errorNum == 5) {
                            PromptUtils.instance.displayToastId(GestureActivity.this, false, R
                                    .string.sign_error_five);

                            spu.putBoolean("isSetGesture", false);
                            goLoginView();
                        } else {
                            PromptUtils.instance.displayToastString(GestureActivity.this, false,
                                    "手势码不正确，剩余尝试次数" + (5 - errorNum));
                        }
                    }

                } else if (mode_type == MODE_TYPE.FIRST) {
                    if (!isChange) {
                        mTipText.setText("请再次设置手势码");
                    } else {
                        mTipText.setText("请再次设置新手势码");
                    }

                    cacheCode = mPassword;

                    mode_type = MODE_TYPE.SECOND;
                    gpv.clearPassword();

                } else if (mode_type == MODE_TYPE.SECOND) {
                    if (cacheCode.equals(mPassword)) {
                        gpv.resetPassWord(mPassword);

                        if (isChange) {
                            PromptUtils.instance.displayToastString(GestureActivity.this, false,
                                    "手势码修改成功");
                        } else {
                            PromptUtils.instance.displayToastId(GestureActivity.this, false, R
                                    .string.sign_ok);
                        }

                        spu.putBoolean("isSetGesture", true);
                        forward(GestureActivity.this, bundle, MainActivity.class, true,
                                BaseActivity.ANIM_TYPE.LEFT);
                    } else {
                        gpv.markError(1000);
                        mTipText.setText(R.string.sign_error);
                        cacheCode = "";
                        mode_type = MODE_TYPE.FIRST;
                    }

                    gpv.clearPassword();

                } else if (mode_type == MODE_TYPE.CHANGE) {
                    if (gpv.verifyPassword(mPassword)) {
                        mTipText.setText(R.string.sign);
                        mode_type = MODE_TYPE.FIRST;

                    } else {
                        gpv.markError(1000);

                        errorNum++;

                        if (errorNum == 5) {
                            PromptUtils.instance.displayToastId(GestureActivity.this, false, R
                                    .string.sign_error_five);

                            spu.putBoolean("isSetGesture", false);
                            forward(GestureActivity.this, null, MainActivity.class, true,
                                    BaseActivity.ANIM_TYPE.LEFT);
                        } else {
                            PromptUtils.instance.displayToastString(GestureActivity.this, false,
                                    "手势码不正确，剩余尝试次数" + (5 - errorNum));
                        }
                    }
                    gpv.clearPassword();

                } else if (mode_type == MODE_TYPE.DELETE) {
                    if (gpv.verifyPassword(mPassword)) {
                        mTipText.setText("手势码删除成功！");
                        spu.putBoolean("isSetGesture", false);
                        back();

                    } else {
                        gpv.markError(1000);
                        mTipText.setText(R.string.sign_error);
                    }
                } else if (mode_type == MODE_TYPE.ERROR) {
                    gpv.clearPassword();
                    PromptUtils.instance.displayToastString(GestureActivity.this, false,
                            "手势码错误，请返回重新操作！");
                }
            }
        });

        mForgetText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                spu.putBoolean("isSetGesture", false);
                spu.putString("staffId", "");
                goLoginView();
            }
        });
    }

    /**
     * 跳转到登录页面
     */
    private void goLoginView() {
        forward(GestureActivity.this, bundle, MainActivity.class, true, BaseActivity.ANIM_TYPE
                .LEFT);
    }

    @Override
    protected void addParamObject(JSONObject param) throws JSONException {

    }

    @Override
    protected void initAllLayout(JSONObject resultJsonObject, Call call) throws Exception {

    }
}