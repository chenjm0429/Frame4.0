package com.ztesoft.ui.other;

import android.os.Bundle;
import android.view.View;

import com.ztesoft.R;
import com.ztesoft.ui.base.BaseFragment;
import com.ztesoft.ui.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;


/**
 * 文件名称 : OtherFragment
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 测试用Fragment
 * <p>
 * 创建时间 : 2017/3/24 11:13
 * <p>
 */
public class OtherFragment extends BaseFragment {

    @Override
    protected int getContentViewId() {
        return R.layout.layout_other;
    }

    @Override
    protected void initData(Bundle arguments) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @Override
    public void addParamObject(JSONObject param) throws JSONException {

    }

    @Override
    protected void changeTitleBarStatus() {
        if (null != mActivity) {
            ((MainActivity) mActivity).mHeadLayout.setVisibility(View.GONE);
            setStatusBarFontColor(true);
        }
    }

    @Override
    public void updateUI(JSONObject resultJsonObject, Call call) {
    }
}
