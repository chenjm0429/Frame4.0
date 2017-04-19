package com.ztesoft.ui.base;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.json.JSONObject;

/**
 * 文件名称 : BaseFragment
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 基类，主要是一级页面继承
 * <p>
 * 创建时间 : 2017/3/23 15:40
 * <p>
 */
public abstract class BaseFragment extends Fragment {

    protected FragmentCallBack mFragmentCallBack;

    public BaseFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context != null) {
            mFragmentCallBack = (FragmentCallBack) context;
        }
    }

    public abstract void updateUI(JSONObject jsonObj);

    /**
     * MainActivity实现该接口
     */
    public interface FragmentCallBack {

        /**
         * 设置标题
         *
         * @param title 标题名称
         */
        void setTitleText(String title);
    }
}
