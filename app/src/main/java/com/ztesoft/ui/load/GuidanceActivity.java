package com.ztesoft.ui.load;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
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

    private int[] imageIds = {R.drawable.load_welcome_1};

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

            Glide.with(this)
                    .load(imageIds[i])
                    .asBitmap()
                    .into(iv);

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

    @Override
    protected void addParamObject(JSONObject param) throws JSONException {

    }

    @Override
    protected void initAllLayout(JSONObject resultJsonObject, Call call) throws Exception {

    }
}