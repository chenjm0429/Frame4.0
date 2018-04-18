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
import com.ztesoft.ui.main.MainActivity;
import com.ztesoft.ui.main.entity.MenuEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;

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

        mUserEdit = view.findViewById(R.id.user_edit);
        mPwdEdit = view.findViewById(R.id.pwd_edit);
        mLoginButton = view.findViewById(R.id.login_btn);
        mLoginButton.setOnClickListener(this);

        mCheckBox = findViewById(R.id.check_box);
        mForgetText = findViewById(R.id.forget_text);
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
//            login();
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
//            queryData("", "login", TYPE_POST_JSON);

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

    @Override
    protected void addParamObject(JSONObject param) throws JSONException {
        super.addParamObject(param);
    }

    @Override
    protected void initAllLayout(JSONObject resultJsonObject, Call call) throws Exception {
        super.initAllLayout(resultJsonObject, call);

        initAppInfo(resultJsonObject, false);
    }
}