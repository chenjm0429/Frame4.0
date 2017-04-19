package com.ztesoft.ui.other;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ztesoft.R;
import com.ztesoft.ui.base.BaseFragment;

import org.json.JSONObject;


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

    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {

        mContext = getActivity();

        return inflater.inflate(R.layout.layout_other, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void updateUI(JSONObject resultJsonObject) {
    }
}
