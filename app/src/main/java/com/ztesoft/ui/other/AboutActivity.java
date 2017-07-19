package com.ztesoft.ui.other;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ztesoft.R;
import com.ztesoft.ui.base.BaseActivity;
import com.ztesoft.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * 文件名称 : AboutActivity
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 关于app
 * <p>
 * 创建时间 : 2017/3/27 15:29
 * <p>
 */
public class AboutActivity extends BaseActivity {

    private TextView versionInfoTv;


    @Override
    public void getBundles(Bundle bundle) {

    }

    @Override
    protected void initView(FrameLayout containerLayout) {
        mTitleTv.setText("关于" + getResources().getString(R.string.app_name));

        View view = View.inflate(this, R.layout.activity_about, null);
        containerLayout.addView(view);

        versionInfoTv = (TextView) findViewById(R.id.versionInfo);
        versionInfoTv.setText("v" + Utils.getVersionName(this));
    }

    @Override
    public void addParamObject(JSONObject param) throws JSONException {

    }

    @Override
    protected void initAllLayout(JSONObject resultJsonObject, Call call) throws Exception {

    }
}
