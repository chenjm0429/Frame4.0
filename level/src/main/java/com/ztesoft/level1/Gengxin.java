package com.ztesoft.level1;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.ztesoft.level1.util.DownloadThread;
import com.ztesoft.level1.util.NotificationUtil;
import com.ztesoft.level1.util.OpenFileIntents;
import com.ztesoft.level1.util.ServiceThread;

public class Gengxin {
    private Context ctx;
    private GengxinListener gxl;

    public Gengxin(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * 获取是否需要更新
     */
    public void setListener(GengxinListener gxl) {
        this.gxl = gxl;
        JSONObject param = new JSONObject();
        try {
            param.put("visitType", "getUpdateInfo");
            param.put("staffId", "dafda");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String httpUrl = "Login";
        ServiceThread st = new ServiceThread(httpUrl, param, ctx);
        st.setServiceHandler(sh);
        st.start();
    }

    public interface GengxinListener {
        void needUpdate(boolean flag, String content, String url);

        void notNeedUpdate();

        void fail(ServiceThread st, String reason);
    }

    ServiceThread.ServiceHandler sh = new ServiceThread.ServiceHandler() {

        @Override
        public void begin(ServiceThread st) {

        }

        @Override
        public void success(ServiceThread st, JSONObject dataObj) {
            String localVersion = null;
            try {
                PackageInfo info = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
                localVersion = info.versionName;
            } catch (NameNotFoundException e) {
            }
            String remoteVersion = dataObj.optString("version");
            if (toGetCheckNameTag(localVersion, remoteVersion)) {
                gxl.needUpdate("1".equals(dataObj.optString("updateFlag")) ? true : false,
                        dataObj.optString("updateInfo"),
                        dataObj.optString("appurl"));
            } else {
                gxl.notNeedUpdate();
            }
        }

        @Override
        public void fail(ServiceThread st, String errorCode, String errorMessage) {
            Toast.makeText(ctx, errorMessage, Toast.LENGTH_SHORT).show();
            gxl.fail(st, errorMessage);
        }

        /**
         * 版本比较
         *
         * @param versionName
         *            客户端实际版本号。格式为x.x.x
         * @param serverVersionName
         *            服务端记录的最新版本号。格式为x.x.x
         * @return
         */
        public boolean toGetCheckNameTag(String versionName, String serverVersionName) {
            boolean tag = false;
            try {//如果报错，则不更新
                if (versionName.split("\\.").length != serverVersionName.split("\\.").length) {
                    tag = false;
                } else {
                    for (int i = 0; i < versionName.split("\\.").length; i++) {
                        if (Integer.parseInt(versionName.split("\\.")[i]) > Integer
                                .parseInt(serverVersionName.split("\\.")[i])) {// 任意一位大于服务端版本号，则无需升级
                            tag = false;
                            return tag;
                        } else if (Integer.parseInt(versionName.split("\\.")[i]) < Integer
								.parseInt(serverVersionName
                                .split("\\.")[i])) {// 任意一位小于服务端版本号，则提示升级
                            tag = true;
                            return tag;
                        }
                    }
                }
            } catch (Exception e) {

            }
            return tag;
        }
    };

    /**
     * 下载客户端
     */
    public void downLoadClient(String apkUrl, String downloadpath, Handler handler) {
        DownloadThread dt = new DownloadThread(apkUrl, downloadpath, Level1Bean
				.AUTOUPDATE_FILENAME, handler);
        Thread thread = new Thread(dt);
        thread.start();
    }

    public void downLoadClient(String apkUrl, String downloadpath) {
        downLoadClient(apkUrl, downloadpath, downloadHandler);
    }

    // 客户端下载完成的通知用handler
    private Handler downloadHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            NotificationUtil notice = new NotificationUtil(ctx);
            switch (msg.what) {
                case Level1Bean.DOWNLOAD_COMPLETE:// 下载成功
                    notice.cancel();
                    // 点击查看文件
                    Intent myIntent = OpenFileIntents.openFile(msg.obj.toString());
                    ctx.startActivity(myIntent);// 直接打开，提示用户安装
                    break;
                case Level1Bean.DOWNLOAD_FAIL:// 下载失败
                    notice.setTitle("下载通知");
                    notice.setDesc("下载失败");
                    notice.start();
                    break;
                case Level1Bean.DOWNLOAD_PROS:// 正在下载
                    int pros = msg.arg1;
                    notice.setTitle("下载通知");
                    notice.setDesc("已下载" + pros + "%");
                    notice.start();
                    break;
            }
        }
    };
}
