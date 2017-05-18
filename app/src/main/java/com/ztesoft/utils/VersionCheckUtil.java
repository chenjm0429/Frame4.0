package com.ztesoft.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.ztesoft.R;
import com.ztesoft.fusion.FusionCode;
import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.util.PromptUtils;
import com.ztesoft.level1.util.SDCardUtil;
import com.ztesoft.level1.util.ServiceThread;
import com.ztesoft.ui.load.LoginBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 文件名称 : VersionCheckUtil
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 检查更新相关工具类
 * <p>
 * 创建时间 : 2017/3/24 9:24
 * <p>
 */
public class VersionCheckUtil {

    private Context context;
    private Activity activity;

    private String versionName;

    // 升级更新信息
    private Dialog updateDialog;
    private String strURL;

    private String updateFlag; // 更新标志位，1表示强制更新，0表示非强制更新
    private String updateInfo;

    private OnUpdateAppListrener onUpdateAppListener;

    public VersionCheckUtil(Context context) {

        this.context = context;
        activity = (Activity) context;

        versionName = Utils.getVersionName(context);

        JSONObject param = new JSONObject();
        try {
            param.put("visitType", "getUpdateInfo");
        } catch (JSONException e) {
            PromptUtils.instance.displayToastId(context, false, R.string.error_json);
        }
        ServiceThread st = new ServiceThread(
                context.getString(R.string.servicePath) + context.getString(R.string.serviceUrl)
                        + "Login", param,
                context);
        st.setServiceHandler(serviceHandler);
        st.start();

    }

    private ServiceThread.ServiceHandler serviceHandler = new ServiceThread.ServiceHandler() {

        @Override
        public void success(ServiceThread st, JSONObject dataObj) {
            updateResult(dataObj);
        }

        @Override
        public void fail(final ServiceThread st, String errorCode, String errorMessage) {
            if ("1".equals(errorCode)) {

                PromptUtils.instance.initTwoButtonDialog(context, R.string.prompt, R.string
								.error_network,
                        R.string.system_confirm, R.string.system_cancel, new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                ServiceThread newSt = new ServiceThread(st.getHttpUrl(), st
										.getParam(), context);
                                newSt.setServiceHandler(serviceHandler);
                                newSt.start();
                            }
                        }, new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                activity.finish();
                            }
                        }).show();

            } else {
                PromptUtils.instance.displayToastString(context, false, errorMessage);
                onUpdateAppListener.onError();
            }
        }

        @Override
        public void begin(ServiceThread st) {

        }
    };

    private void updateResult(JSONObject jsonObj) {
        try {
            JSONObject version = jsonObj.getJSONObject("version");
            String updateVersionName = version.getString("version");
            updateInfo = version.getString("updateInfo");
            updateFlag = version.optString("updateFlag", "0");

            strURL = version.getString("appurl");

            if (!TextUtils.isEmpty(version.optString("phoneLoading")))
                onUpdateAppListener.handleInfo(version.optString("phoneLoading"));

            LoginBaseActivity.remotePublicKey = jsonObj.optString("publicKey");

            // 登录成功，且获取了版本信息，判断是否需要升级
            if (getCheckTag(versionName, updateVersionName)) {// 需要更新
                showUpdateDialog();
                updateDialog.show();

            } else { // 不需要更新
                onUpdateAppListener.onNoUpdate();
            }

        } catch (JSONException e) {
            PromptUtils.instance.displayToastId(context, false, R.string.error_json);
            onUpdateAppListener.onError();
        }
    }

    // 判断是否网络连接并且是否要跟新版本
    private boolean getCheckTag(String versionName, String serverVersionName) throws JSONException {
        boolean tag = false;
        if (toGetCheckNameTag(versionName, serverVersionName)) {
            tag = true;
        }
        return tag;
    }

    /**
     * 版本比较
     *
     * @param versionName       客户端实际版本号。格式为x.x.x.x
     * @param serverVersionName 服务端记录的最新版本号。格式为x.x.x.x
     * @return 是否升级
     */
    private boolean toGetCheckNameTag(String versionName, String serverVersionName) {
        if (versionName.split("\\.").length != serverVersionName.split("\\.").length) {
            return false;
        } else {
            for (int i = 0; i < versionName.split("\\.").length; i++) {
                if (Integer.parseInt(versionName.split("\\.")[i]) > Integer
                        .parseInt(serverVersionName.split("\\.")[i])) {// 任意一位大于服务端版本号，则无需升级
                    return false;
                } else if (Integer.parseInt(versionName.split("\\.")[i]) < Integer
                        .parseInt(serverVersionName.split("\\.")[i])) {// 任意一位小于服务端版本号，则提示升级
                    return true;
                }
            }
        }

        return false;
    }

    // 更新提示框
    private void showUpdateDialog() {

        OnClickListener confirmListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (SDCardUtil.getInstance().getSDCardState()) {// SD卡存在且可读写

                    // 先清楚下载文件夹下的文件
                    DataCleanUtil.cleanCustomCache(Level1Bean.SD_ROOTPATH + FusionCode
							.AUTOUPDATE_LOCALPATH);

                    DownloadEntity downloadEntity = new DownloadEntity();
                    downloadEntity.setTitle(context.getString(R.string.app_name) + "升级");
                    downloadEntity.setFileName(FusionCode.AUTOUPDATE_FILENAME);
                    downloadEntity.setPathName(FusionCode.AUTOUPDATE_LOCALPATH);
                    downloadEntity.setUri(strURL);
                    downloadEntity.setDescription(updateInfo);
                    downloadEntity.setVisiblityHidden(true);

                    new DownLoadManagerUtil(context, downloadEntity);

                } else {
                    PromptUtils.instance.displayToastId(context, true, R.string.error_SD_notFound);
                }

                onUpdateAppListener.onConfirm();
            }
        };

        OnClickListener cancelListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                updateDialog.dismiss();

                onUpdateAppListener.onCancel();
            }
        };

        String tip = context.getResources().getString(R.string.upgrade_prompt) + "\n" + updateInfo;
        if (updateFlag.equals("1")) { // 强制更新

            updateDialog = PromptUtils.instance.initOneButtonDialog(context, R.string.prompt, tip,
                    R.string.upgrade_true, confirmListener);

        } else { // 非强制更新
            updateDialog = PromptUtils.instance.initTwoButtonDialog(context, R.string.prompt, tip,
                    R.string.upgrade_true, R.string.upgrade_false, confirmListener, cancelListener);
        }

        Window window = updateDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = Utils.getDeviceWidth(context) * 9 / 10;
        window.setAttributes(lp);

        updateDialog.setCanceledOnTouchOutside(false);
        updateDialog.setCancelable(false);

        updateDialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                ((Activity) context).finish();
            }
        });
    }

    public void setOnUpdateAppListener(OnUpdateAppListrener onUpdateAppListener) {
        this.onUpdateAppListener = onUpdateAppListener;
    }

    /**
     * 检测更新后不需要更新的操作
     *
     * @author chenjm 2014年8月19日
     */
    public interface OnUpdateAppListrener {

        /**
         * 内部方法
         *
         * @param imagePath load页背景图片
         */
        void handleInfo(String imagePath);

        /**
         * 检测不需要更新后的操作
         */
        void onNoUpdate();

        /**
         * 取消更新后的操作
         */
        void onCancel();

        /**
         * 确定更新后的操作
         */
        void onConfirm();

        /**
         * 检测更新报错
         */
        void onError();
    }

    protected  class DownloadEntity {

        // 下载地址
        private String uri;

        // 是否在屏幕顶部显示
        private boolean visiblityHidden;

        // 下载保存的地址
        private String pathName;
        // 下载后保存的文件名
        private String fileName;

        // 下载任务名称
        private String title;

        // 下载描述
        private String description;

        public DownloadEntity() {

        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public boolean isVisiblityHidden() {
            return visiblityHidden;
        }

        public void setVisiblityHidden(boolean visiblityHidden) {
            this.visiblityHidden = visiblityHidden;
        }

        public String getPathName() {
            return pathName;
        }

        public void setPathName(String pathName) {
            this.pathName = pathName;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }
}


