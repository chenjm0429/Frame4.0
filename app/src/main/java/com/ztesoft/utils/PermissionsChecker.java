package com.ztesoft.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * 文件名称 : PermissionsChecker
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 检查权限的工具类
 * <p>
 * 创建时间 : 2017/3/30 14:46
 * <p>
 */
public class PermissionsChecker {

    private final Context mContext;

    public PermissionsChecker(Context context) {
        mContext = context.getApplicationContext();
    }

    // 判断权限集合
    public boolean lacksPermissions(String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    // 判断是否缺少权限
    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) == PackageManager
                .PERMISSION_DENIED;
    }
}
