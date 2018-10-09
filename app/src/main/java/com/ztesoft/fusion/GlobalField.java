package com.ztesoft.fusion;

import android.content.Context;
import android.text.TextUtils;

import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.util.SharedPreferencesUtil;

/**
 * 文件名称 : GlobalField
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 服务器传递的全局数据
 * <p>
 * 创建时间 : 2017/3/23 14:55
 * <p>
 */
public class GlobalField {

    /**
     * 是否保存在SharePreference中
     */
    private boolean isSaveFile = false;

    private SharedPreferencesUtil shareUtil;

    // 用户信息
    private String userId;

    public GlobalField(Context context, boolean isSaveFile) {
        this.isSaveFile = isSaveFile;

        if (isSaveFile)
            shareUtil = new SharedPreferencesUtil(context, Level1Bean.SHARE_PREFERENCES_NAME);
    }

    public String getUserId() {
        if (TextUtils.isEmpty(userId) && isSaveFile) {
            userId = shareUtil.getString("userId", "");
        }
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        if (isSaveFile) {
            shareUtil.putString("userId", userId);
        }
    }
}
