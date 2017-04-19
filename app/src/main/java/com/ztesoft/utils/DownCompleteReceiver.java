package com.ztesoft.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.ztesoft.R;
import com.ztesoft.fusion.FusionCode;
import com.ztesoft.level1.util.SharedPreferencesUtil;

import java.io.File;

/**
 * 文件名称 : DownCompleteReceiver
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 下载完成广播接收器
 * <p>
 * 创建时间 : 2017/3/24 9:48
 * <p>
 */
public class DownCompleteReceiver extends BroadcastReceiver {
    private DownloadManager downloadManager;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        // 获取id，当同时下载多个文件时，需要通过id来判断哪个文件下载完成
        this.context = context;
        long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        long downloadId = Long.MIN_VALUE;
        try {
            String value = new SharedPreferencesUtil(context, FusionCode.SHARE_PREFERENCES_NAME)
                    .getString("downloadId", "");
            downloadId = Long.parseLong(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (downloadId == Long.MIN_VALUE) {
            return;
        }
        if (downloadId != completeDownloadId) {
            return;
        }

        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        queryDownloadStatus(completeDownloadId);
    }

    public void queryDownloadStatus(long completeDownloadId) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(completeDownloadId);
        Cursor c = downloadManager.query(query);
        if (c != null && c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));

            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    // 暂停
                    break;

                case DownloadManager.STATUS_PENDING:
                    //
                    break;

                case DownloadManager.STATUS_RUNNING:
                    // 正在下载，不做任何事情
                    break;

                case DownloadManager.STATUS_SUCCESSFUL:
                    PromptUtils.instance.displayToastId(context, false, R.string.download_done);
                    openApk();
                    // 完成
                    break;

                case DownloadManager.STATUS_FAILED:
                    PromptUtils.instance.displayToastId(context, false, R.string.download_false);
                    // 清除已下载的内容，重新下载
                    downloadManager.remove(completeDownloadId);
                    break;
            }
        }
    }

    /**
     * 下载完成，打开程序
     */
    private void openApk() {
        File file = new File(Environment.getExternalStorageDirectory() + FusionCode
                .AUTOUPDATE_LOCALPATH + FusionCode.AUTOUPDATE_FILENAME);

        Intent it = new Intent();
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.setAction(android.content.Intent.ACTION_VIEW);
        it.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(it);
    }
}