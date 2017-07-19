package com.ztesoft.ui.load;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.ztesoft.R;
import com.ztesoft.fusion.FusionCode;
import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.hscrollframe.HScrollFrame;
import com.ztesoft.level1.util.SharedPreferencesUtil;
import com.ztesoft.ui.base.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * 文件名称 : GuidanceActivity
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 引导页
 * <p>
 * 创建时间 : 2017/3/23 17:13
 * <p>
 */
public class GuidanceActivity extends BaseActivity {

    private int[] imageIds = {R.drawable.load_welcome_1, R.drawable.load_welcome_2, R.drawable
            .load_welcome_3, R.drawable.load_welcome_4};

    @Override
    protected void getBundles(Bundle bundle) {

    }

    @Override
    protected void initView(FrameLayout containerLayout) {

        mTitleLayout.setVisibility(View.GONE);

        HScrollFrame mGuidanceView = new HScrollFrame(this);
        containerLayout.addView(mGuidanceView);

        for (int i = 0; i < imageIds.length; i++) {

            ImageView iv = new ImageView(this);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            mGuidanceView.addView(iv, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

            setImageRes(imageIds[i], iv);

            if (i == imageIds.length - 1) {
                iv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(GuidanceActivity.this, LoadActivity.class);

                        setResult(RESULT_OK, intent);

                        // 第一次进入后提交消息
                        SharedPreferencesUtil spu = new SharedPreferencesUtil(GuidanceActivity.this,
                                Level1Bean.SHARE_PREFERENCES_NAME);
                        spu.putBoolean(FusionCode.WELCOME_VERSION, false);
                        GuidanceActivity.this.finish();
                    }
                });
            }
        }
    }

    private void setImageRes(int res, ImageView image) {
        String url = "drawable://" + res;

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
        ImageLoader.getInstance().displayImage(url, image, options);
    }

    @Override
    protected void addParamObject(JSONObject param) throws JSONException {

    }

    @Override
    protected void initAllLayout(JSONObject resultJsonObject, Call call) throws Exception {

    }
}
