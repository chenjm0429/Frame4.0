package com.ztesoft.ui.load;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ztesoft.R;
import com.ztesoft.fusion.FusionCode;
import com.ztesoft.level1.util.ServiceThread;
import com.ztesoft.ui.main.MainActivity;
import com.ztesoft.ui.main.entity.MenuEntity;
import com.ztesoft.level1.util.PromptUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 文件名称 : LoginActivity
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 用户名、密码登录页面
 * <p>
 * 创建时间 : 2017/3/24 9:37
 * <p>
 */
public class LoginActivity extends LoginBaseActivity implements OnClickListener {

    private EditText mUserEdit; // 用户名输入框
    private EditText mPwdEdit; // 密码输入框
    private Button mLoginButton; // 登录按钮

    private CheckBox mCheckBox;  //自动登录
    private TextView mForgetText;  //忘记密码

    @Override
    protected void initView(FrameLayout containerLayout) {
        super.initView(containerLayout);

        View view = View.inflate(this, R.layout.activity_login, null);
        containerLayout.addView(view);

        mUserEdit = (EditText) view.findViewById(R.id.user_edit);
        mPwdEdit = (EditText) view.findViewById(R.id.pwd_edit);
        mLoginButton = (Button) view.findViewById(R.id.login_btn);
        mLoginButton.setOnClickListener(this);

        mCheckBox = (CheckBox) findViewById(R.id.check_box);
        mForgetText = (TextView) findViewById(R.id.forget_text);
        mForgetText.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //记住用户名、密码，直接登录
        boolean isSave = spu.getBoolean("isSavePwd", false);
        if (isSave) {
            mCheckBox.setChecked(true);

            userName = spu.getString("userName", "");
            userPwd = spu.getString("userPwd", "");
            login();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mLoginButton)) {
            userName = mUserEdit.getText().toString().trim();
            userPwd = mPwdEdit.getText().toString().trim();

            // 第一登陆，需要验证码
//            if (TextUtils.isEmpty(userName)) {
//                PromptUtils.instance.displayToastString(LoginActivity.this, false,
//                        "用户名为空，请重新输入");
//                return;
//            }
//            login();

            //此处控制显示的主菜单数，不同的现场主要调整此处
            ArrayList<MenuEntity> menuEntities = new ArrayList<MenuEntity>();

            MenuEntity entity1 = new MenuEntity();
            entity1.setMenuId("1");
            entity1.setMenuName("首页");
            entity1.setMenuIcon(R.drawable.app_menu_home);
            entity1.setMenuIconSelected(R.drawable.app_menu_home_selected);
            menuEntities.add(entity1);

            MenuEntity entity2 = new MenuEntity();
            entity2.setMenuId("2");
            entity2.setMenuName("其它");
            entity2.setMenuIcon(R.drawable.app_menu_home);
            entity2.setMenuIconSelected(R.drawable.app_menu_home_selected);
            menuEntities.add(entity2);

            Bundle bundle = new Bundle();
            bundle.putSerializable("menu", menuEntities);
            forward(this, bundle, MainActivity.class, true, ANIM_TYPE.LEFT);

        } else if (v.equals(mForgetText)) {

        }
    }

    /**
     * 登录
     */
    public void login() {

        showLoadingDialog(null, R.string.logining);

        JSONObject param = new JSONObject();
        try {
            param.put("visitType", "login");
            param.put("loginType", "bang_imsi");
            param.put("staffId", staffId);
            param.put("staffPwd", userPwd); // 短信验证码 getEncryptedValue(userPwd)
            param.put("tml_idf_cd", sim);
            param.put("phone", phone);

            ServiceThread serviceThread = new ServiceThread(
                    getString(R.string.servicePath) + getString(R.string.serviceUrl) + "Login",
                    param, this);
            serviceThread.setEncryFlag(FusionCode.encryFlag);
            serviceThread.setEncryKey(FusionCode.encrykey);
            serviceThread.setServiceHandler(loginFirstHandler);
            serviceThread.start();

        } catch (JSONException e) {
            PromptUtils.instance.displayToastString(getApplicationContext(), false, e.getMessage());
        }
    }

    private ServiceThread.ServiceHandler loginFirstHandler = new ServiceThread.ServiceHandler() {

        @Override
        public void success(ServiceThread st, JSONObject dataObj) {
            dismissLoadingDialog();

            if ("false".equals(dataObj.optString("loginFlag", "false"))) {

                PromptUtils.instance.displayToastString(LoginActivity.this, false, dataObj
                        .optString("loginReason"));


            } else {
                try {
                    initAppInfo(dataObj, true);
                    spu.putString("staffId", staffId);
                    spu.putString("phone", phone);
                } catch (Exception e) {
                    e.printStackTrace();
                    PromptUtils.instance.displayToastId(getApplicationContext(), false, R.string
                            .error_json);
                }
            }
        }

        @Override
        public void fail(final ServiceThread st, String errorCode, String errorMessage) {
            dismissLoadingDialog();

            if ("1".equals(errorCode)) {
                PromptUtils.instance.initTwoButtonDialog(getApplicationContext(), R.string.prompt,
                        R.string.error_network, R.string.system_confirm, R.string.system_cancel,
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                ServiceThread newSt = new ServiceThread(st.getHttpUrl(), st
                                        .getParam(),
                                        LoginActivity.this);
                                newSt.setEncryFlag(FusionCode.encryFlag);
                                newSt.setEncryKey(FusionCode.encrykey);
                                newSt.setServiceHandler(loginFirstHandler);
                                newSt.start();
                            }
                        }, new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                LoginActivity.this.finish();
                            }
                        }).show();

            } else {
                PromptUtils.instance.displayToastString(LoginActivity.this, false, errorMessage);
            }
        }

        @Override
        public void begin(ServiceThread st) {

        }
    };
}
