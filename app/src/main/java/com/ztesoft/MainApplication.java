package com.ztesoft;

import android.app.Application;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.ztesoft.fusion.FusionCode;
import com.ztesoft.fusion.GlobalField;
import com.ztesoft.level1.util.SDCardUtil;
import com.ztesoft.utils.MyActivityManager;

import java.io.File;

public class MainApplication extends Application {

    private GlobalField mGlobalField;

    private MyActivityManager activityManager;

    @Override
    public void onCreate() {
        super.onCreate();

        mGlobalField = new GlobalField(this);

//		CrashHandler crashHandler = CrashHandler.getInstance();
//		crashHandler.init(this);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(defaultOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCache(new UnlimitedDiskCache(StorageUtils.getOwnCacheDirectory(this, FusionCode.IMAGES_LOCALPATH)))
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);

        //如果app根目录不存在，则创建app根目录
        boolean ss = SDCardUtil.getInstance().isFileExist(FusionCode.FILE_LOCALPATH);
        if (!SDCardUtil.getInstance().isFileExist(FusionCode.FILE_LOCALPATH)) {
            SDCardUtil.getInstance().createSDDir(FusionCode.FILE_LOCALPATH);
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
