package com.ztesoft.ui.load;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.ztesoft.MainApplication;
import com.ztesoft.R;
import com.ztesoft.fusion.GlobalField;
import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.util.SharedPreferencesUtil;
import com.ztesoft.ui.base.BaseActivity;
import com.ztesoft.ui.main.MainActivity;
import com.ztesoft.ui.main.entity.MenuEntity;
import com.ztesoft.ui.other.GestureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;

/**
 * 文件名称 : LoginBaseActivity
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 登录页面基类
 * <p>
 * 创建时间 : 2017/3/23 17:17
 * <p>
 */
public class LoginBaseActivity extends BaseActivity {

    protected String userName;
    protected String userPwd;

    protected SharedPreferencesUtil spu;

    public static String remotePublicKey;

    @Override
    protected void initView(FrameLayout containerLayout) {
        mTitleLayout.setVisibility(View.GONE);

        spu = new SharedPreferencesUtil(this, Level1Bean.SHARE_PREFERENCES_NAME);
    }

    /**
     * 初始化app信息，此方法根据各个现场调整
     *
     * @param jsonObj      登录成功返回的数据源
     * @param isFirstLogin 是否是首次登录
     * @throws Exception
     */
    protected void initAppInfo(JSONObject jsonObj, boolean isFirstLogin) throws Exception {

       setAppInfo(jsonObj);

        dismissLoadingDialog();

        //此处控制显示的主菜单数，不同的现场主要调整此处
        ArrayList<MenuEntity> menuEntities = new ArrayList<MenuEntity>();

        MenuEntity entity1 = new MenuEntity();
        entity1.setMenuId("1");
        entity1.setMenuName("首页");
        entity1.setMenuIcon(R.drawable.app_menu_home);
        entity1.setMenuIconSelected(R.drawable.app_menu_home_selected);
        menuEntities.add(entity1);

        Bundle bundle = new Bundle();
        bundle.putSerializable("menu", menuEntities);

        if (isFirstLogin) {  //首次登录
            boolean isSetGesture = spu.getBoolean("isSetGesture", false);

            int mode_type = 0;
            if (isSetGesture) {
                mode_type = GestureActivity.MODE_TYPE.LOGIN.ordinal();
            } else {
                mode_type = GestureActivity.MODE_TYPE.FIRST.ordinal();
            }
            bundle.putInt("mode_type", mode_type);
            forward(this, bundle, GestureActivity.class, true, ANIM_TYPE.LEFT);

        } else { //二次登录
            forward(this, bundle, MainActivity.class, true, ANIM_TYPE.LEFT);
        }
    }

    /**
     * 设置app的相关信息
     */
    protected void setAppInfo(JSONObject dataObj) {

        String flag = dataObj.optString("flag");
        if (flag != null && "true".equals(flag)) {

            JSONObject userJSON = dataObj.optJSONObject("userInfo");

            GlobalField gf = ((MainApplication) getApplication()).getGlobalField();

            gf.setStaffId(userJSON.optString("staffId"));
            gf.setStaffName(userJSON.optString("staffName"));
            gf.setRangeId(userJSON.optString("rangeId"));
            gf.setJobId(userJSON.optString("jobId"));
            gf.setJobName(userJSON.optString("jobName"));

            spu.putString("staffId", userJSON.optString("staffId"));
            spu.putString("lastGetMenuInfo", (new Date()).getTime() + "");
        } 
    }

    @Override
    protected void getBundles(Bundle bundle) {

    }

    @Override
    protected void addParamObject(JSONObject param) throws JSONException {

    }

    @Override
    protected void initAllLayout(JSONObject resultJsonObject, Call call) throws Exception {

    }
}
