package com.ztesoft.ui.load;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

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

    protected String sim = "";
    protected String phone = "";
    protected String imei = "";

    protected String userName;
    protected String userPwd;

    protected SharedPreferencesUtil spu;

    public static String remotePublicKey;

    @Override
    protected void initView(FrameLayout containerLayout) {
//        containerLayout.setForeground(new ColorDrawable());

        mTitleLayout.setVisibility(View.GONE);

        // 获取sim卡信息
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        sim = tm.getSubscriberId();// 获取imsi
        imei = tm.getDeviceId();
        phone = tm.getLine1Number();

        if (TextUtils.isEmpty(sim))
            sim = "";

        if (TextUtils.isEmpty(imei))
            imei = "";

        if (TextUtils.isEmpty(phone))
            phone = "";

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

        String flag = jsonObj.getString("flag");
        JSONArray funcArray;
        if (flag != null && "true".equals(flag)) {

            JSONObject userJSON = jsonObj.getJSONObject("userInfo");
            JSONArray areaArray = jsonObj.getJSONArray("areaArray");
            funcArray = jsonObj.getJSONArray("funcArray");

            GlobalField gf = ((MainApplication) getApplication()).getGlobalField();

            gf.setStaffId(userJSON.getString("staffId"));
            gf.setStaffName(userJSON.optString("staffName"));
            gf.setProvCode(userJSON.optString("provCode"));
            gf.setCityCode(userJSON.optString("cityCode"));
            gf.setCountyCode(userJSON.optString("countyCode"));
            gf.setVillageCode(userJSON.optString("villageCode"));
            gf.setRangeId(userJSON.optString("rangeId"));
            gf.setJobId(userJSON.optString("jobId"));
            gf.setJobName(userJSON.optString("jobName"));

            spu.putString("staffId", userJSON.optString("staffId"));
            spu.putString("sim", sim);
            spu.putString("lastGetMenuInfo", (new Date()).getTime() + "");

        } else {
            dismissLoadingDialog();
            return;
        }

        dismissLoadingDialog();

        //此处控制显示的主菜单数，不同的现场主要调整此处
        ArrayList<MenuEntity> menuEntities = new ArrayList<MenuEntity>();
        for (int i = 0; i < funcArray.length(); i++) {
            JSONObject menuObj = funcArray.getJSONObject(i);

            String rptCode = menuObj.getString("rptCode");
            String rptName = menuObj.getString("rptName");
            String picCode = menuObj.getString("picCode");

            String selectPicCode = picCode + "_select";

            int defaultId = R.drawable.class.getDeclaredField(picCode).getInt(null);
            int selectId = R.drawable.class.getDeclaredField(selectPicCode).getInt(null);

            MenuEntity entity = new MenuEntity();
            entity.setMenuId(rptCode);
            entity.setMenuName(rptName);
            entity.setMenuIcon(defaultId);
            entity.setMenuIconSelected(selectId);

            menuEntities.add(entity);
        }

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

    @Override
    protected void getBundles(Bundle bundle) {

    }

    @Override
    protected void addParamObject(JSONObject param) throws JSONException {

    }

    @Override
    protected void initAllLayout(JSONObject resultJsonObject) throws Exception {

    }
}
