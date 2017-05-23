package com.ztesoft.level1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ztesoft.level1.util.ServiceThread;

import org.json.JSONException;
import org.json.JSONObject;

public class CommonPage extends Activity {
    /***
     * 整体layout
     */
    protected LinearLayout layout;
    protected long lastClickTime = 0;
    protected JSONObject jsonObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /***
     * 整体页面构造
     */
    public void initAllLayout() throws Exception {

    }

    /***
     * 多线程返回填写页面------json串，线程号
     *
     * @param obj
     * @param i
     * @throws Exception
     */
    public void initAllLayout(JSONObject obj, int i) throws Exception {

    }

    /***
     * 查询事件----单线程
     */
    public void initKpiData(String url) {
        initKpiData(url, new JSONObject());
    }

    /***
     * 单线程
     */
    public void initKpiData(String url, JSONObject param) {
        myShowDialog(1);
        // 判断用户权限，并预录入相关信息
        try {
            addParamObject(param);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_json), Toast
                    .LENGTH_SHORT).show();
        }
        com.ztesoft.level1.util.ServiceThread st = new com.ztesoft.level1.util.ServiceThread(url,
                param, getApplicationContext());
        st.setServiceHandler(sh);
        st.start();
    }

    ServiceThread.ServiceHandler sh = new ServiceThread.ServiceHandler() {

        @Override
        public void begin(com.ztesoft.level1.util.ServiceThread st) {

        }

        @Override
        public void success(com.ztesoft.level1.util.ServiceThread st, JSONObject dataObj) {
            if ("false".equals(dataObj.optString("flag", "false"))) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_serivce), Toast
                        .LENGTH_SHORT).show();
                myDismissDialog();
            } else {
                try {
                    jsonObj = dataObj;
                    initAllLayout();
                } catch (Exception e) {
//					Log.e("error", e.toString());
                    Toast.makeText(getApplicationContext(), getString(R.string.error_json), Toast
                            .LENGTH_SHORT).show();
                } finally {
                    myDismissDialog();
                }
            }
        }

        @Override
        public void fail(final com.ztesoft.level1.util.ServiceThread st, String errorCode, String
                errorMessage) {
            myDismissDialog();
            if ("1".equals(errorCode)) {
                LinearLayout ll = new LoadLayout(getApplicationContext(), st.getParam());
                layout.addView(ll, 0, new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                        .FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
                ((LoadLayout) ll).getNegButton().setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layout.removeViewAt(0);
                        initKpiData(st.getHttpUrl(), (JSONObject) v.getTag());
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void addParamObject(JSONObject param) throws JSONException {

    }

    com.ztesoft.level1.ui.MyProgressDialog mAD;

    public void myShowDialog(int id) {
        if (mAD == null) {
            mAD = new com.ztesoft.level1.ui.MyProgressDialog(CommonPage.this);
        }

        switch (id) {
            case 1:
                mAD.setTitle(getString(R.string.system_loading));
                break;
            case 2:
                mAD.setTitle(getString(R.string.audit_update));
                break;
        }
        mAD.show();
    }

    public void myDismissDialog() {
        if (mAD != null) {
            mAD.hide();
        }
    }

    class LoadLayout extends LinearLayout {
        private TextView negButton;

        public LoadLayout(Context context, JSONObject queryJson) {
            super(context);
            this.setGravity(Gravity.CENTER);
            this.setOrientation(LinearLayout.VERTICAL);
            int pad = Level1Util.dip2px(context, 8);
            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams
                    .WRAP_CONTENT);
            lp.setMargins(pad, 0, pad, 0);
            LinearLayout alLayout = new LinearLayout(context);
            alLayout.setGravity(Gravity.CENTER);
            alLayout.setBackgroundColor(Color.parseColor("#373b44"));
            // this.setBackgroundColor(ThemeColorBean.title_bg);
            alLayout.setOrientation(LinearLayout.VERTICAL);
            alLayout.setPadding(Level1Util.dip2px(context, 2), 0, Level1Util.dip2px(context, 2), 0);
            this.addView(alLayout, lp);

            TextView titleView = new TextView(context);
            titleView.setTextColor(Color.WHITE);
            titleView.setText(R.string.error_serivce);
            titleView.setGravity(Gravity.LEFT);
            titleView.setPadding(pad, Level1Util.dip2px(context, 10), 0, Level1Util.dip2px
                    (context, 10));
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            alLayout.addView(titleView);
            RelativeLayout buttonView = new RelativeLayout(context);
            TextView posButton = new TextView(context);
            posButton.setPadding(5 * pad, pad / 2, 5 * pad, pad / 2);
            posButton.setBackgroundColor(Color.parseColor("#39724b"));
            posButton.setTextColor(Color.WHITE);
            posButton.setText(R.string.system_setting_net);
            posButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(android.provider.Settings
                            .ACTION_WIRELESS_SETTINGS), 0);
                }
            });
            negButton = new TextView(context);
            negButton.setText(R.string.system_retry);
            negButton.setPadding(5 * pad, pad / 2, 5 * pad, pad / 2);
            negButton.setBackgroundColor(Color.parseColor("#39724b"));
            negButton.setTextColor(Color.WHITE);
            negButton.setTag(queryJson);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            negButton.setLayoutParams(params);

            buttonView.addView(posButton);
            buttonView.addView(negButton);
            buttonView.setPadding(pad, pad, pad, pad);

            buttonView.setGravity(Gravity.CENTER);
            alLayout.addView(buttonView);
        }

        public TextView getNegButton() {
            return negButton;
        }
    }

    public boolean isClickable() {
        boolean tag = true;
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 1000 && lastClickTime != 0) {
            tag = false;
        }
        lastClickTime = time;

        return tag;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAD != null) {
            mAD.dismiss();
        }
    }
}
