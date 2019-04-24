package com.ztesoft.ui.other;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.ztesoft.R;
import com.ztesoft.ui.base.BaseActivity;
import com.ztesoft.utils.PermissionsChecker;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * 文件名称 : PermissionsActivity
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 权限获取页面
 * <p>
 * 创建时间 : 2017/3/30 15:38
 * <p>
 */
public class PermissionsActivity extends BaseActivity {

    public static final int PERMISSIONS_GRANTED = 0; // 权限授权
    public static final int PERMISSIONS_DENIED = 1; // 权限拒绝

    private static final int PERMISSION_REQUEST_CODE = 0; // 系统权限管理页面的参数
    private static final String PACKAGE_URL_SCHEME = "package:"; // 方案

    private PermissionsChecker mChecker; // 权限检测器
    private boolean isRequireCheck; // 是否需要系统权限检测

    private String[] PERMISSIONS;

    @Override
    protected void getBundles(Bundle bundle) {

        if (null == bundle || !bundle.containsKey("extra_permission")) {
            throw new RuntimeException("PermissionsActivity需要获取权限数组启动！");
        }

        PERMISSIONS = bundle.getStringArray("extra_permission");
    }

    @Override
    protected void initView(FrameLayout containerLayout) {

        mHeadLayout.setVisibility(View.GONE);
        containerLayout.setBackgroundResource(R.drawable.login_bg);
        containerLayout.setForeground(new ColorDrawable());

        mChecker = new PermissionsChecker(this);
        isRequireCheck = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isRequireCheck) {
            if (mChecker.lacksPermissions(PERMISSIONS)) {
                requestPermissions(PERMISSIONS); // 请求权限
            } else {
                allPermissionsGranted(); // 全部权限都已获取
            }
        } else {
            isRequireCheck = true;
        }
    }

    // 请求权限兼容低版本
    private void requestPermissions(String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    // 全部权限均已获取
    private void allPermissionsGranted() {
        setResult(PERMISSIONS_GRANTED);
        finish();
    }

    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            isRequireCheck = true;
            allPermissionsGranted();
        } else {
            isRequireCheck = false;
            showMissingPermissionDialog();
        }
    }

    // 含有全部的权限
    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    // 显示缺失权限提示
    private void showMissingPermissionDialog() {
        AlertDialog dialog = new AlertDialog.Builder(PermissionsActivity.this).create();
        dialog.setTitle("帮助");
        dialog.setMessage("当前应用缺少必要的权限。\n请点击“设置”-“权限”-打开所需权限\n最后点击两次后退按钮，即可返回。");

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "退出", new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                setResult(PERMISSIONS_DENIED);
                back();
            }
        });

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "设置", new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });

        dialog.show();

        Button negativeBtn = dialog.getButton(dialog.BUTTON_NEGATIVE);
        Button positiveBtn = dialog.getButton(dialog.BUTTON_POSITIVE);

        negativeBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        negativeBtn.setTextColor(Color.parseColor("#189E91"));
        positiveBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        positiveBtn.setTextColor(Color.parseColor("#189E91"));

        dialog.setCanceledOnTouchOutside(false);//禁止点击 dialog 外部取消弹窗  
    }

    // 启动应用的设置
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }

    @Override
    protected void addParamObject(JSONObject param) throws JSONException {

    }

    @Override
    protected void initAllLayout(JSONObject resultJsonObject, Call call) throws Exception {

    }
}