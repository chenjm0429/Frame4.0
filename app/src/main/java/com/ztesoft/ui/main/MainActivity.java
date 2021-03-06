package com.ztesoft.ui.main;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ztesoft.MainApplication;
import com.ztesoft.R;
import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.Level1Util;
import com.ztesoft.level1.util.PromptUtils;
import com.ztesoft.level1.util.SharedPreferencesUtil;
import com.ztesoft.ui.base.BaseActivity;
import com.ztesoft.ui.base.BaseFragment;
import com.ztesoft.ui.load.LoadActivity;
import com.ztesoft.ui.main.entity.MenuEntity;
import com.ztesoft.ui.other.AboutActivity;
import com.ztesoft.ui.widget.MenuItemView;
import com.ztesoft.utils.MainPageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * 文件名称 : MainActivity
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 程序主页面
 * <p>
 * 创建时间 : 2017/3/23 15:08
 * <p>
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    //侧边栏
    private DrawerLayout mDrawerLayout;

    public TextView mTitleText;

    /**
     * 标题布局
     */
    public RelativeLayout mHeadLayout;
    protected Toolbar mToolbar;

    private RelativeLayout mAboutLayout;  //关于app
    private TextView mExitText;  //退出登录

    // 底部菜单布局
    private LinearLayout mMenuLayout;

    private ArrayList<MenuEntity> mMenuEntities;

    private int mLastSelectedIndex = -1;

    @Override
    protected void getBundles(Bundle bundle) {
        if (null != bundle)
            mMenuEntities = bundle.getParcelableArrayList("menu");
    }

    @Override
    protected void initView(FrameLayout containerLayout) {
        setContentView(R.layout.activity_main);

        /**
         * android4.3以上的沉浸式 ，4.3以下没效果，所以不要头部填充状态栏高度
         */
        int sysVersion = Build.VERSION.SDK_INT;
        if (sysVersion > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            LinearLayout headTopLayout = findViewById(R.id.head_top_layout);
            RelativeLayout.LayoutParams para = new RelativeLayout.LayoutParams(Level1Util
                    .getDeviceWidth(this), Level1Util.getStatusBarHeight(this));
            //设置修改后的布局。
            headTopLayout.setLayoutParams(para);
        }

        mDrawerLayout = findViewById(R.id.drawer_layout);
        initSideBarView();

        mTitleText = findViewById(R.id.head_title_text);
        mHeadLayout = findViewById(R.id.head_layout);

        mToolbar = findViewById(R.id.toolbar);

        mMenuLayout = findViewById(R.id.menu_layout);

        if (null != mMenuEntities && mMenuEntities.size() > 0) {
            initMenu();
        }

        mToolbar.setNavigationIcon(R.drawable.app_action_button);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                setStatusBarFontColor(true);
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {
                setStatusBarFontColor(false);
            }

            @Override
            public void onDrawerStateChanged(int i) {
                
            }
        });
    }

    /**
     * 绘制侧边栏视图
     */
    private void initSideBarView() {
        NavigationView nv = findViewById(R.id.nav_view);
        View view = nv.getHeaderView(0);

        mAboutLayout = view.findViewById(R.id.about_layout);
        mAboutLayout.setOnClickListener(this);
        mExitText = view.findViewById(R.id.exit);
        mExitText.setOnClickListener(this);
    }

    @Override
    protected void addParamObject(JSONObject param) throws JSONException {
        BaseFragment fragment = mMenuEntities.get(mLastSelectedIndex).getFragment();

        if (null != fragment) {
            fragment.addParamObject(param);
        }
    }

    @Override
    protected void initAllLayout(JSONObject resultJsonObject, Call call) throws Exception {
        BaseFragment fragment = mMenuEntities.get(mLastSelectedIndex).getFragment();

        if (null != fragment) {
            fragment.updateUI(resultJsonObject, call);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mAboutLayout)) {
            forward(MainActivity.this, null, AboutActivity.class, false, ANIM_TYPE.LEFT);

        } else if (v.equals(mExitText)) {
            createDialog().show();
        }
    }

    /**
     * 初始化menu界面
     */
    private void initMenu() {
        mMenuLayout.removeAllViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);

        for (int i = 0, size = mMenuEntities.size(); i < size; i++) {

            MenuItemView menuItemLayout = new MenuItemView(MainActivity.this, mMenuEntities.get(i));

            final int position = i;

            menuItemLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    menuSelected(position);
                }
            });

            mMenuLayout.addView(menuItemLayout, params);

            if (i < size - 1) {
                ImageView iv = new ImageView(this);
                iv.setBackgroundColor(Color.parseColor("#E0E0E0"));
                mMenuLayout.addView(iv, 1, ViewGroup.LayoutParams.MATCH_PARENT);
            }
        }

        // 默认选中第一个
        menuSelected(0);
    }

    /**
     * 选择菜单
     *
     * @param index 菜单位置
     */
    public void menuSelected(int index) {
        if (index == mLastSelectedIndex) {
            return;
        }
        mLastSelectedIndex = index;

        for (int i = 0, size = (mMenuLayout.getChildCount() + 1) / 2; i < size; i++) {
            MenuItemView menuItemLayout = (MenuItemView) mMenuLayout.getChildAt(i * 2);

            menuItemLayout.unSelectMenu();
            if (i == index) {
                menuItemLayout.selectMenu();
            }
        }

        MenuEntity entity = mMenuEntities.get(index);
        mTitleText.setText(entity.getMenuName());  //设置默认标题

        FragmentManager fm = getSupportFragmentManager();
        // 开启Fragment事务  
        FragmentTransaction transaction = fm.beginTransaction();

        BaseFragment fragment = entity.getFragment();

        if (null == fragment) {
            String rptCode = entity.getMenuId();
            fragment = MainPageUtil.getActuralFragment(this, rptCode);

            entity.setFragment(fragment);
        }

        if (!fragment.isAdded()) {
            transaction.add(R.id.container_layout, fragment);

            transaction.addToBackStack(null);
        }

        hideFragment(transaction);
        transaction.show(fragment);

        transaction.commitAllowingStateLoss();
    }

    /**
     * 隐藏所有的Fragment
     */
    private void hideFragment(FragmentTransaction transaction) {

        for (int i = 0; i < mMenuEntities.size(); i++) {
            Fragment fragment = mMenuEntities.get(i).getFragment();

            if (null != fragment)
                transaction.hide(fragment);
        }
    }

    /**
     * 获取底部菜单栏
     *
     * @return 菜单栏视图
     */
    public LinearLayout getMenuLayout() {
        return mMenuLayout;
    }

    /**
     * 创建退出登录弹出框
     *
     * @return 弹出框
     */
    private Dialog createDialog() {

        final Dialog dialog = new Dialog(this, R.style.no_bg_dialog_style);
        dialog.setContentView(R.layout.dialog_side_bar);

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = Level1Util.getDeviceWidth(this) * 4 / 5; // 宽度
        dialogWindow.setAttributes(lp);

        TextView tipText = dialog.findViewById(R.id.tip_text);
        tipText.setText("确认退出？");

        Button cancelBtn = dialog.findViewById(R.id.cancel);
        Button confirmBtn = dialog.findViewById(R.id.confirm);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                SharedPreferencesUtil spu = new SharedPreferencesUtil(MainActivity.this, Level1Bean
                        .SHARE_PREFERENCES_NAME);

                spu.putBoolean("isSavePwd", false);
                spu.putString("userName", "");
                spu.putString("userPwd", "");

                forward(MainActivity.this, null, LoadActivity.class, true, ANIM_TYPE.RIGHT);
            }
        });

        return dialog;
    }

    @Override
    public void onBackPressed() {

        //如果侧边栏打开，优先关闭侧边栏
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START) || mDrawerLayout.isDrawerOpen
                (GravityCompat.END)) {
            mDrawerLayout.closeDrawers();
        } else {
            exit();
        }
    }

    /**
     * 退出程序对话框
     */
    private Dialog exitDialog;

    /**
     * 退出系统
     */
    public void exit() {

        View.OnClickListener confirmListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                MainApplication application = (MainApplication) MainActivity.this.getApplication();
                application.getActivityManager().getStack().removeAllElements();

                finish();
            }
        };

        View.OnClickListener cancelListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (exitDialog != null) {
                    exitDialog.dismiss();
                }
            }
        };

        exitDialog = PromptUtils.instance.initTwoButtonDialog(this, R.string.prompt, R.string
                        .quit, R.string.system_confirm, R.string.system_cancel, confirmListener,
                cancelListener);
        exitDialog.show();
    }
}