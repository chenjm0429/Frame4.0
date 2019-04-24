package com.ztesoft.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ztesoft.MainApplication;
import com.ztesoft.R;
import com.ztesoft.fusion.GlobalField;
import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.util.SharedPreferencesUtil;

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
    protected BaseActivity mActivity;

    protected View mRootView;

    protected GlobalField gf;
    protected SharedPreferencesUtil spu;

    /**
     * 界面切换动画枚举
     */
    public enum ANIM_TYPE {
        NONE, LEFT, RIGHT
    }

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

        mActivity = (BaseActivity) getActivity();

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

    /**
     * 跳转到其他界面
     *
     * @param context    当前界面
     * @param bundle     传递参数
     * @param otherClass 目标界面
     * @param isFinish   跳转后是否关闭当前界面
     * @param animType   页面跳转动画，支持left，right 默认无动画
     */
    public void forward(Context context, Bundle bundle, Class<?> otherClass, boolean isFinish,
                        ANIM_TYPE animType) {
        Intent intent = new Intent(context, otherClass);

        if (null != bundle) {
            intent.putExtras(bundle);
        }

        startActivity(intent);

        if (isFinish) {
            ((Activity) context).finish();
        }

        Activity target = ((Activity) context).getParent();
        if (null == target) {
            target = (Activity) context;
        }
        if (animType == ANIM_TYPE.LEFT) {
            target.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

        } else if (animType == ANIM_TYPE.RIGHT) {
            target.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        }
    }

    /**
     * 跳转到其他界面，带返回结果
     *
     * @param context     当前界面
     * @param bundle      传递参数
     * @param otherClass  目标界面
     * @param requestCode 标志位
     * @param isFinish    跳转后是否关闭当前界面
     * @param animType    页面跳转动画，支持left，right 默认无动画
     */
    public void forwardForResult(Context context, Bundle bundle, Class<?> otherClass, int
            requestCode, boolean isFinish, ANIM_TYPE animType) {
        Intent intent = new Intent(context, otherClass);

        if (null != bundle) {
            intent.putExtras(bundle);
        }

        startActivityForResult(intent, requestCode);

        if (isFinish) {
            ((Activity) context).finish();
        }

        Activity target = ((Activity) context).getParent();
        if (null == target) {
            target = (Activity) context;
        }
        if (animType == ANIM_TYPE.LEFT) {
            target.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

        } else if (animType == ANIM_TYPE.RIGHT) {
            target.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        }
    }

    /**
     * 设置状态栏文字颜色
     *
     * @param isDark 是否为深色
     */
    public void setStatusBarFontColor(boolean isDark) {
        if (null == getActivity())
            return;
        
        if (isDark) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View
                    .SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//黑色
        } else {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View
                    .SYSTEM_UI_FLAG_VISIBLE);//白色
        }
    }
}