package com.ztesoft.ui.load;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.ztesoft.R;
import com.ztesoft.fusion.FusionCode;
import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.util.SharedPreferencesUtil;
import com.ztesoft.ui.base.BaseActivity;
import com.ztesoft.ui.other.GestureActivity;
import com.ztesoft.ui.other.PermissionsActivity;
import com.ztesoft.utils.PermissionsChecker;
import com.ztesoft.utils.VersionCheckUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 文件名称 : LoadActivity
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 启动页面
 * <p>
 * 创建时间 : 2017/3/23 17:09
 * <p>
 */
public class LoadActivity extends BaseActivity {

    private static final int REQUEST_CODE_GUIDANCE = 1001;  // 引导页
    private static final int REQUEST_CODE_PERMISSION = 1002;  //权限请求

    private FrameLayout frameLayout;
    private ImageView mWelcomeView;

    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE};

    private PermissionsChecker mPermissionsChecker; // 权限检测器

    private SharedPreferencesUtil spu;

    @Override
    protected void initView(FrameLayout containerLayout) {
        containerLayout.setForeground(new ColorDrawable());

        mTitleLayout.setVisibility(View.GONE);

        frameLayout = new FrameLayout(this);
        containerLayout.addView(frameLayout);

        showWelcomeView();

        mPermissionsChecker = new PermissionsChecker(this);

        spu = new SharedPreferencesUtil(this, Level1Bean.SHARE_PREFERENCES_NAME);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {

            Bundle bundle = new Bundle();
            bundle.putStringArray("extra_permission", PERMISSIONS);

            forwardForResult(this, bundle, PermissionsActivity.class, REQUEST_CODE_PERMISSION,
                    false, ANIM_TYPE.LEFT);

        } else {
            boolean isFirstCome = spu.getBoolean(FusionCode.WELCOME_VERSION, true);

            if (!isFirstCome) { // 不是第一次进入应用，启动版本检测
//			startVersionCheck();
                startLogin();

            } else { // 第一次进入应用，跳转到引导页
                forwardForResult(this, null, GuidanceActivity.class, REQUEST_CODE_GUIDANCE,
                        false, null);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GUIDANCE) {
            if (resultCode == RESULT_OK) {
//                startVersionCheck(); // 启动版本检测
                startLogin();
            }
        } else if (requestCode == REQUEST_CODE_PERMISSION) {
            if (resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
                back();
            }
        }
    }

    /**
     * 启动登录程序，如果一次登录，走LoginPnActivity流程，如果二次登录，直接走手势码流程
     */
    private void startLogin() {
        staffId = spu.getString("staffId", "");
        String phone = spu.getString("phone", "");
        boolean isSetGesture = spu.getBoolean("isSetGesture", false);

        Class<?> c;
        Bundle bundle = new Bundle();
        if (!TextUtils.isEmpty(staffId) && !TextUtils.isEmpty(phone) && isSetGesture) {
            c = GestureActivity.class;
            bundle.putInt("mode_type", GestureActivity.MODE_TYPE.LOGIN.ordinal());
        } else {
            c = LoginActivity.class;
        }

        forward(this, bundle, c, true, ANIM_TYPE.LEFT);
    }

    /**
     * 欢迎页面
     */
    private void showWelcomeView() {
        mWelcomeView = new ImageView(this);
        mWelcomeView.setScaleType(ImageView.ScaleType.FIT_XY);

        String url = "drawable://" + R.drawable.load_welcome_bg;
        changeLoadBg(url);

        frameLayout.addView(mWelcomeView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    /**
     * 启动版本检测
     */
    private void startVersionCheck() {

        // 可添加loading图片，修改背景图等短时间操作
        showLoadingDialog(null, R.string.check_version);

        VersionCheckUtil checkUtil = new VersionCheckUtil(this);
        checkUtil.setOnUpdateAppListener(mOnUpdateAppListener);
    }

    private VersionCheckUtil.OnUpdateAppListrener mOnUpdateAppListener = new VersionCheckUtil
            .OnUpdateAppListrener() {

        @Override
        public void handleInfo(String loadBgPath) {

            String url = getString(R.string.servicePath) + loadBgPath;
            changeLoadBg(url);

        }

        @Override
        public void onCancel() {
            startLogin();
            dismissLoadingDialog();
        }

        @Override
        public void onConfirm() {

            dismissLoadingDialog();
            LoadActivity.this.finish();
        }

        @Override
        public void onNoUpdate() {

            startLogin();
            dismissLoadingDialog();
        }

        @Override
        public void onError() {

            dismissLoadingDialog();
            LoadActivity.this.finish();
        }
    };

    /**
     * 切换load背景
     */
    private void changeLoadBg(String url) {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)// 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
                // .considerExifParams(true) //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.NONE)// 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
                // .delayBeforeLoading(int delayInMillis)//int
                // delayInMillis为你设置的下载前的延迟时间
                // 设置图片加入缓存前，对bitmap进行设置
                // .preProcessor(BitmapProcessor preProcessor)
                .resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
                // .displayer(new RoundedBitmapDisplayer(45))// 是否设置为圆角，弧度为多少
//                .displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
                .build();// 构建完成
        ImageLoader.getInstance().displayImage(url, mWelcomeView, options);
    }

    @Override
    protected void getBundles(Bundle bundle) {
    }

    @Override
    protected void addParamObject(JSONObject param) throws JSONException {

    }

    @Override
    protected void initAllLayout(JSONObject resultJsonObject) throws Exception {

    }
}
