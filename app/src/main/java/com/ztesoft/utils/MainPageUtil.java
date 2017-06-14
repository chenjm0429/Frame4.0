package com.ztesoft.utils;

import android.content.Context;
import android.text.TextUtils;

import com.ztesoft.level1.util.PromptUtils;
import com.ztesoft.ui.base.BaseFragment;
import com.ztesoft.ui.home.HomeFragment;
import com.ztesoft.ui.other.OtherFragment;

/**
 * 文件名称 : MainPageUtil
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 页面跳转目标工具类
 * <p>
 * 创建时间 : 2017/3/23 16:43
 * <p>
 */
public class MainPageUtil {

    public static BaseFragment getActuralFragment(Context context, String rptCode) {
        if (TextUtils.isEmpty(rptCode)) {
            PromptUtils.instance.displayToastString(context, true, "打开界面失败，请重试！");

            return null;
        }

        // 目标Fragment
        BaseFragment fragment = null;
        if (rptCode.equals("1")) {
            fragment = new HomeFragment();
        } else if (rptCode.equals("2")) {
            fragment = new OtherFragment();
        }


        return fragment;
    }
}