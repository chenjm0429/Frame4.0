package com.ztesoft.ui.base;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ztesoft.MainApplication;
import com.ztesoft.fusion.GlobalField;
import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.util.SharedPreferencesUtil;
import com.ztesoft.ui.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

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
    protected MainActivity mActivity;

    protected View mRootView;

    protected GlobalField gf;
    protected SharedPreferencesUtil spu;

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

        mActivity = (MainActivity) getActivity();

        gf = ((MainApplication) mActivity.getApplication()).getGlobalField();
        spu = new SharedPreferencesUtil(mActivity, Level1Bean.SHARE_PREFERENCES_NAME);

        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData(getArguments());

        changeTitleBarStatus();

        initView(savedInstanceState);
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

    protected abstract void initView(Bundle savedInstanceState);

    public abstract void addParamObject(JSONObject param) throws JSONException;

    public abstract void updateUI(JSONObject jsonObj, Call call) throws Exception;

    /**
     * 改变标题栏状态
     */
    protected abstract void changeTitleBarStatus();

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            changeTitleBarStatus();
        }
    }

    /**
     * MainActivity实现该接口
     */
    public interface FragmentCallBack {
        /**
         * 回调函数
         */
        void onCallBack();
    }
}
