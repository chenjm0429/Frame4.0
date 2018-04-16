package com.ztesoft;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import com.ztesoft.fusion.FusionCode;
import com.ztesoft.fusion.GlobalField;
import com.ztesoft.level1.util.SDCardUtil;
import com.ztesoft.utils.MyActivityManager;

public class MainApplication extends Application {

    private GlobalField mGlobalField;

    private MyActivityManager activityManager;

    @Override
    public void onCreate() {
        super.onCreate();

        mGlobalField = new GlobalField(this, false);

//		CrashHandler crashHandler = CrashHandler.getInstance();
//		crashHandler.init(this);

        //如果app根目录不存在，则创建app根目录
        if (!SDCardUtil.getInstance().isFileExist(FusionCode.FILE_LOCAL_PATH)) {
            SDCardUtil.getInstance().createSDDir(FusionCode.FILE_LOCAL_PATH);
        }

        //解决7.0相机报错的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    public GlobalField getGlobalField() {
        return mGlobalField;
    }

    public MyActivityManager getActivityManager() {
        if (activityManager == null) {

            activityManager = MyActivityManager.getScreenManager();
        }

        return activityManager;
    }
}
