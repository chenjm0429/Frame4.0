package com.ztesoft.level1.login;

import android.content.Context;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ztesoft.level1.Level1Util;
import com.ztesoft.level1.R;
import com.ztesoft.level1.checkbox.CheckBoxView;
import com.ztesoft.level1.util.ServiceThread;
import com.ztesoft.level1.util.SharedPreferencesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserPwdLogin extends LinearLayout {

    SharedPreferencesUtil mPrefs;

    private String editColor = "#e9e9e9";
    private String bgColor = "#2aa1d3";
    protected Context ctx;
    private LoginListener loginListener;
    private boolean savePwdFlag = false;
    private boolean modifyPwdFlag = false;
    private boolean resetPwdFlag = false;
    private Boolean imsiFlag = true;

    private com.ztesoft.level1.ui.ClearEditText idEt;
    private com.ztesoft.level1.ui.ClearEditText pwdEt;
    private Button loginBtn;
    private CheckBoxView savePwdView;
    private Button modifyPwdBtn;
    private Button resetPwdBtn;

    private String url;
    private String sim;

    private UserPwdLogin(Context context) {
        super(context);
    }

    public UserPwdLogin(Context context, String url) {
        super(context);
        this.ctx = context;
        this.url = url;
        mPrefs = new SharedPreferencesUtil(ctx, "level1_login");
        this.setOrientation(LinearLayout.VERTICAL);
    }

    public void drawLayout() {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        sim = tm.getSubscriberId();// 获取imsi
        idEt = new com.ztesoft.level1.ui.ClearEditText(ctx);
        idEt.setHint(R.string.login_user);
        String userNameHis = mPrefs.getString("userName", "");
        idEt.setText(userNameHis);
        idEt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        idEt.setBackgroundDrawable(Level1Util.MAIN_RADIO_BORD(ctx, Color.parseColor(editColor),
                Color.TRANSPARENT, 2, 20));
        pwdEt = new com.ztesoft.level1.ui.ClearEditText(ctx);
        pwdEt.setHint(R.string.login_pwd);
        if (!"".equals(mPrefs.getString("savaPwdFlag", ""))) {
            String pwdHis = mPrefs.getString("pwd", "");
            pwdEt.setText(pwdHis);
        }
        pwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
        pwdEt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        pwdEt.setBackgroundDrawable(Level1Util.MAIN_RADIO_BORD(ctx, Color.parseColor(editColor),
                Color.TRANSPARENT, 2, 20));
        loginBtn = new Button(ctx);
        loginBtn.setText(R.string.login_login);
        loginBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        loginBtn.setBackgroundDrawable(Level1Util.MAIN_RADIO_BORD(ctx, Color.parseColor(bgColor),
                Color.TRANSPARENT, 2, 20));
        loginBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                JSONObject param = new JSONObject();
                String userName = idEt.getText().toString().trim();
                String userPwd = pwdEt.getText().toString().trim();
                if (!"".equals(userName) && !"".equals(userPwd)) {
                    try {
                        param.put("visitType", "pwdLogin");
                        param.put("staffId", userName);
                        param.put("staffPwd", userPwd);
                        param.put("tml_idf_cd", sim);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    loginListener.loginStart();
                    ServiceThread st = new ServiceThread(url, param, ctx);
                    st.setServiceHandler(sh);
                    st.start();
                } else {
                    Toast.makeText(ctx, R.string.error_userNull, Toast.LENGTH_SHORT).show();
                }
            }
        });
        this.addView(idEt);
        this.addView(pwdEt);
        if (savePwdFlag) {
            savePwdView = new CheckBoxView(ctx);
            JSONArray array = new JSONArray();
            try {
                JSONObject obj = new JSONObject();
                obj.put("kpiId", "111");
                obj.put("kpiName", ctx.getString(R.string.login_save_pwd));
                array.put(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!"".equals(mPrefs.getString("savaPwdFlag", ""))) {
                String xx1[] = {"111"};
                savePwdView.setSelValue(xx1);
            }
            savePwdView.create(array);
            this.addView(savePwdView);
        }
        this.addView(loginBtn);
        if (modifyPwdFlag) {
            modifyPwdBtn = new Button(ctx);
            modifyPwdBtn.setText(R.string.login_modify_pwd);
            modifyPwdBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            modifyPwdBtn.setBackgroundDrawable(Level1Util.MAIN_RADIO_BORD(ctx, Color.parseColor
                    (bgColor), Color.TRANSPARENT, 2, 20));
            modifyPwdBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 重绘界面
                    modifyPwdView();
                }
            });
            this.addView(modifyPwdBtn);
        }
        if (resetPwdFlag) {
            resetPwdBtn = new Button(ctx);
            resetPwdBtn.setText(R.string.login_reset_pwd);
            resetPwdBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            resetPwdBtn.setBackgroundDrawable(Level1Util.MAIN_RADIO_BORD(ctx, Color.parseColor
                    (bgColor), Color.TRANSPARENT, 2, 20));
            resetPwdBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    JSONObject param = new JSONObject();
                    loginListener.resetClick(param);
                }
            });
            this.addView(resetPwdBtn);
        }

        if (imsiFlag) {// 如果需要则直接进行imsi登录
            if (!"".equals(mPrefs.getString("userName", "")) && !"".equals(mPrefs.getString
                    ("tml_idf_cd", ""))) {
                JSONObject param = new JSONObject();
                try {
                    param.put("visitType", "imsiLogin");
                    param.put("staffId", mPrefs.getString("userName", ""));
                    param.put("tml_idf_cd", mPrefs.getString("tml_idf_cd", ""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loginListener.loginStart();
                ServiceThread st = new ServiceThread(url, param, ctx);
                st.setServiceHandler(sh);
                st.start();
            } else {
                imsiFlag = false;
            }
        }
    }

    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    public interface LoginListener {
        /**
         * 开始登录，绘制load框
         */
        void loginStart();

        /**
         * 登录成功
         *
         * @param param 服务端返回数据
         */
        void loginSuccess(JSONObject param);

        /**
         * 登录失败
         *
         * @param reason 失败原因
         */
        void loginFail(String reason);

        /**
         * 重置密码
         *
         * @param param ######
         */
        void resetClick(JSONObject param);
    }

    public com.ztesoft.level1.ui.ClearEditText getUserId() {
        return idEt;
    }

    public com.ztesoft.level1.ui.ClearEditText getUserPwd() {
        return pwdEt;
    }

    public Button getLoginBtn() {
        return loginBtn;
    }

    public CheckBoxView getSavePwdView() {
        return savePwdView;
    }

    public Button getModifyPwdBtn() {
        return modifyPwdBtn;
    }

    public Button getResetPwdBtn() {
        return resetPwdBtn;
    }

    public void setImsiFlag(Boolean imsiFlag) {
        this.imsiFlag = imsiFlag;
    }

    ServiceThread.ServiceHandler sh = new ServiceThread.ServiceHandler() {

        @Override
        public void begin(ServiceThread st) {
        }

        @Override
        public void success(ServiceThread st, JSONObject dataObj) {
            if ("true".equals(dataObj.optString("flag"))) {
                if (!imsiFlag) {// 非设备号登录，需要记录缓存
                    mPrefs.putString("userName", idEt.getText().toString().trim());
                    mPrefs.putString("tml_idf_cd", sim);
                    if (newPwdEt != null) {// 如果新密码输入框存在，则保存新密码
                        mPrefs.putString("pwd", newPwdEt.getText().toString().trim());
                    } else {
                        mPrefs.putString("pwd", pwdEt.getText().toString().trim());
                    }
                    if (savePwdView.getSelValue().length > 0) {
                        mPrefs.putString("savaPwdFlag", "true");
                    } else {
                        mPrefs.putString("savaPwdFlag", "");
                    }
                }
                loginListener.loginSuccess(dataObj);
            } else {
                imsiFlag = false;// 如登录失败，则认定是非imsi登录，需要其他方式登录
                loginListener.loginFail(dataObj.optString("reason"));
            }
        }

        @Override
        public void fail(ServiceThread st, String errorCode, String errorMessage) {
            imsiFlag = false;// 如登录失败，则认定是非imsi登录，需要其他方式登录
            loginListener.loginFail(errorMessage);
        }
    };

    private com.ztesoft.level1.ui.ClearEditText newPwdEt;
    private com.ztesoft.level1.ui.ClearEditText newPwd2Et;

    void modifyPwdView() {
        this.removeAllViews();
        this.addView(idEt);
        this.addView(pwdEt);

        newPwdEt = new com.ztesoft.level1.ui.ClearEditText(ctx);
        newPwdEt.setHint(R.string.login_newpwd);
        newPwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newPwdEt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        newPwdEt.setBackgroundDrawable(Level1Util.MAIN_RADIO_BORD(ctx, Color.parseColor
                (editColor), Color.TRANSPARENT, 2, 20));

        newPwd2Et = new com.ztesoft.level1.ui.ClearEditText(ctx);
        newPwd2Et.setHint(R.string.login_newpwd_second);
        newPwd2Et.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newPwd2Et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        newPwd2Et.setBackgroundDrawable(Level1Util.MAIN_RADIO_BORD(ctx, Color.parseColor
                (editColor), Color.TRANSPARENT, 2, 20));

        this.addView(newPwdEt);
        this.addView(newPwd2Et);
        this.addView(modifyPwdBtn);
        modifyPwdBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String userName = idEt.getText().toString().trim();
                String userPwd = pwdEt.getText().toString().trim();
                String newPwd = newPwdEt.getText().toString().trim();
                String newPwd2 = newPwd2Et.getText().toString().trim();
                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(ctx, R.string.error_userNull, Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(newPwd)) {
                    Toast.makeText(ctx, R.string.error_newpwdNull, Toast.LENGTH_SHORT).show();
                } else if (!newPwd.equals(newPwd2)) {
                    Toast.makeText(ctx, R.string.error_mismatching, Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject param = new JSONObject();
                    try {
                        param.put("visitType", "modifyPwd");
                        param.put("staffId", userName);
                        param.put("old_staff_pwd", userPwd);
                        param.put("staffPwd", newPwd);
                        param.put("tml_idf_cd", sim);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    loginListener.loginStart();
                    ServiceThread st = new ServiceThread(url, param, ctx);
                    st.setServiceHandler(sh);
                    st.start();
                }
            }
        });
    }

    public void setSavePwdFlag(boolean savePwdFlag) {
        this.savePwdFlag = savePwdFlag;
    }

    public void setModifyPwdFlag(boolean modifyPwdFlag) {
        this.modifyPwdFlag = modifyPwdFlag;
    }

    public void setResetPwdFlag(boolean resetPwdFlag) {
        this.resetPwdFlag = resetPwdFlag;
    }
}
