package com.ztesoft.ui.base;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    /**
     * 依附的Activity
     */
    protected Activity mActivity;

    protected View mRootView;

    protected FragmentCallBack mFragmentCallBack;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        if (getContentViewId() != 0) {
            mRootView = inflater.inflate(getContentViewId(), null);
        } else {
            mRootView = super.onCreateView(inflater, container, savedInstanceState);
        }

        initData(getArguments());

        return mRootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context != null) {
            mActivity = getActivity();
            mFragmentCallBack = (FragmentCallBack) context;
        }
    }

    /**
     * 设置根布局资源id
     *
     * @return
     */
    protected abstract int getContentViewId();

    /**
     * 初始化数据
     *
     * @param arguments 接收到从其它地方传递过来的参数
     */
    protected abstract void initData(Bundle arguments);

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
