package com.ztesoft.ui.other;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ztesoft.R;
import com.ztesoft.ui.base.BaseFragment;

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void updateUI(JSONObject resultJsonObject, Call call) {
    }
}
