package com.ztesoft.utils;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.ztesoft.R;
import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.util.SharedPreferencesUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 文件名称 : DownLoadManagerUtil
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 下载工具类，该类主要为app更新使用，以后也可改造用于下载大文件(只支持3.0以后的系统使用)
 * <p>
 * 创建时间 : 2017/3/24 9:53
 * <p>
 */
public class DownLoadManagerUtil {

    private Context context;
    private VersionCheckUtil.DownloadEntity downloadEntity;

    private DownloadManager downloadManager;

    private long downloadId;

    public DownLoadManagerUtil(Context context, VersionCheckUtil.DownloadEntity downloadEntity) {

        this.context = context;
        this.downloadEntity = downloadEntity;

        init();

    }

    private void init() {

        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Request request = new Request(Uri.parse(downloadEntity.getUri()));

        // 设置可用的网络类型
        request.setAllowedNetworkTypes(Request.NETWORK_MOBILE | Request.NETWORK_WIFI);

        // 设置是否允许漫游网络 建立请求 默认true
        request.setAllowedOverRoaming(true);

        request.setVisibleInDownloadsUi(true);

        // 设置状态栏中显示Notification，下载完，知道点击才会消失
        request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);

        // 设置Notification的标题
        request.setTitle(downloadEntity.getTitle());

        // 设置Notification的描述
        request.setDescription(downloadEntity.getDescription());

        // 设置下载的目录
        request.setDestinationInExternalPublicDir(downloadEntity.getPathName(), downloadEntity
                .getFileName());

        // 设置请求的Mime
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        request.setMimeType(mimeTypeMap.getMimeTypeFromExtension(downloadEntity.getUri()));

        try {

            downloadId = downloadManager.enqueue(request);
            new SharedPreferencesUtil(context, Level1Bean.SHARE_PREFERENCES_NAME).putString
                    ("downloadId", String.valueOf(downloadId));

        } catch (Exception e) {

            Toast toast = Toast.makeText(context, R.string.upError, Toast.LENGTH_LONG);
            toast.show();
            execToast(toast);
            return;
        }

        Query query = new Query();
        query.setFilterById(downloadId);
        query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) { // 有下载记录
            downloadManager.remove(downloadId);
        }
    }

    private void execToast(final Toast toast) {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                toast.show();
            }
        }, 3400);
    }
}
