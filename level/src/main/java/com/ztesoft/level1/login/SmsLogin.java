package com.ztesoft.level1.login;

import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.Level1Util;
import com.ztesoft.level1.R;
import com.ztesoft.level1.ui.ClearEditText;
import com.ztesoft.level1.util.ServiceThread;
import com.ztesoft.level1.util.SharedPreferencesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class SmsLogin extends LinearLayout {

    SharedPreferencesUtil mPrefs;
    private String editColor = "#e9e9e9";
    private String bgColor = "#2aa1d3";
    private String pwdColor = "#8ab71b";
    protected Context ctx;
    private LoginListener loginListener;
    private Boolean imsiFlag = true;
    private String url;
    private String sim;

    private ClearEditText phoneEt;
    private ClearEditText smsEt;
    private Button checkPhoneBtn;
    private Button loginBtn;

    public SmsLogin(Context context) {
        super(context);
        this.ctx = context;
        mPrefs = new SharedPreferencesUtil(ctx, "level1_login");
        this.setOrientation(LinearLayout.VERTICAL);
        this.setGravity(Gravity.CENTER);
    }

    public void drawLayout() {
        LinearLayout ll1 = new LinearLayout(ctx);
        phoneEt = new ClearEditText(ctx);
        phoneEt.setHint(R.string.login_phonenum);
        phoneEt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        phoneEt.setInputType(InputType.TYPE_CLASS_NUMBER);// 数字
        phoneEt.setBackgroundDrawable(Level1Util.MAIN_RADIO_BORD_LEFT(ctx, Color.parseColor
                (editColor), Color.TRANSPARENT, 2, 20));

        checkPhoneBtn = new Button(ctx);
        checkPhoneBtn.setText(R.string.login_phone);
        checkPhoneBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        checkPhoneBtn.setBackgroundDrawable(Level1Util.MAIN_RADIO_BORD_RIGHT(ctx, Color
                .parseColor(pwdColor), Color.TRANSPARENT, 2, 20));
        checkPhoneBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String phone = phoneEt.getText().toString().trim();

                Pattern pattern = Pattern.compile("[0-9]*");
                if (phone.length() == 11 && phone.startsWith("1") && pattern.matcher(phone)
                        .matches()) {
                    loginListener.checkPhone(phone, checkPhone);
                } else {
                    Toast.makeText(ctx, R.string.error_phoneNum, Toast.LENGTH_SHORT).show();
                }
            }
        });
        ll1.addView(phoneEt, (int) (Level1Bean.actualWidth * 0.55), LayoutParams.WRAP_CONTENT);
        ll1.addView(checkPhoneBtn, (int) (Level1Bean.actualWidth * 0.4), LayoutParams.WRAP_CONTENT);
        this.addView(ll1);

        LinearLayout ll2 = new LinearLayout(ctx);
        smsEt = new ClearEditText(ctx);
        smsEt.setHint(R.string.login_phonepwd);
        smsEt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        smsEt.setInputType(InputType.TYPE_CLASS_NUMBER);// 数字
        smsEt.setBackgroundDrawable(Level1Util.MAIN_RADIO_BORD_LEFT(ctx, Color.parseColor
                (editColor), Color.TRANSPARENT, 2, 20));
        smsEt.setVisibility(View.GONE);
        loginBtn = new Button(ctx);
        loginBtn.setText(R.string.login_login);
        loginBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        loginBtn.setBackgroundDrawable(Level1Util.MAIN_RADIO_BORD_RIGHT(ctx, Color.parseColor
                (bgColor), Color.TRANSPARENT, 2, 20));
        loginBtn.setVisibility(View.GONE);
        loginBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String smsPwd = smsEt.getText().toString().trim();
                JSONObject param = new JSONObject();
                try {
                    param.put("visitType", "smsLogin");
                    param.put("staffId", mPrefs.getString("staffId", ""));
                    param.put("smsPwd", smsPwd);
                    param.put("tml_idf_cd", sim);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                loginListener.loginStart();
                ServiceThread st = new ServiceThread(url, param, ctx);
                st.setServiceHandler(sh);
                st.start();
            }
        });
        ll2.addView(smsEt, (int) (Level1Bean.actualWidth * 0.55), LayoutParams.WRAP_CONTENT);
        ll2.addView(loginBtn, (int) (Level1Bean.actualWidth * 0.4), LayoutParams.WRAP_CONTENT);
        this.addView(ll2);

        if (imsiFlag) {//如果需要则直接进行imsi登录
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
         * 关闭load框
         */
        void loginEnd();

        void checkPhone(String phone, ServiceThread.ServiceHandler checkPhone);
    }

    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    public void setImsiFlag(Boolean imsiFlag) {
        this.imsiFlag = imsiFlag;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    ServiceThread.ServiceHandler sh = new ServiceThread.ServiceHandler() {

        @Override
        public void begin(ServiceThread st) {
        }

        @Override
        public void success(ServiceThread st, JSONObject dataObj) {
            if ("true".equals(dataObj.optString("flag"))) {
                if (!imsiFlag) {//非设备号登录，需要记录缓存
                    mPrefs.putString("userName", dataObj.optString("staffId"));
                    mPrefs.putString("tml_idf_cd", sim);
                }
                loginListener.loginSuccess(dataObj);
            } else {
                imsiFlag = false;// 如登录失败，则认定是非imsi登录，需要其他方式登录
                loginListener.loginFail(dataObj.optString("reason"));
            }
        }

        @Override
        public void fail(ServiceThread st, String errorCode, String errorMessage) {
            imsiFlag = false;//如登录失败，则认定是非imsi登录，需要其他方式登录
            loginListener.loginFail(errorMessage);
        }
    };


    ServiceThread.ServiceHandler checkPhone = new ServiceThread.ServiceHandler() {

        @Override
        public void begin(ServiceThread st) {
        }

        @Override
        public void success(ServiceThread st, JSONObject dataObj) {
            if ("true".equals(dataObj.optString("flag"))) {
                mPrefs.putString("staffId", dataObj.optString("staffId"));
                smsEt.setVisibility(View.VISIBLE);
//				smsEt.setFocusable(true);
//				smsEt.setFocusableInTouchMode(true);
                smsEt.requestFocus();
                loginBtn.setVisibility(View.VISIBLE);
                phoneEt.setEnabled(false);
            } else {
                Toast.makeText(ctx, dataObj.optString("reason"), Toast.LENGTH_SHORT).show();
            }
            loginListener.loginEnd();
        }

        @Override
        public void fail(ServiceThread st, String errorCode, String errorMessage) {
            Toast.makeText(ctx, errorMessage, Toast.LENGTH_SHORT).show();
            loginListener.loginEnd();
        }
    };
}
