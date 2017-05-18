package com.ztesoft.level1.util;

import android.os.Handler;
import android.os.Message;

/**
 * 文件名称 : DownloadThread
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 下载线程
 * <p>
 * 创建时间 : 2017/5/22 16:52
 * <p>
 */
public class DownloadThread implements Runnable {

    private String urlStr = null;
    private String path = null;
    private String fileName = null;
    private Handler handler = null;

    /**
     * @param urlStr   http开头的文件地址，中文需转码.如：URLEncoder.encode("测试.pdf","UTF-8"))
     * @param path     SD卡目录的相对路径，如"/MobileBI_RC/Update"
     * @param fileName 保存后的文件名，如"test1.pdf"
     * @param handler  用于通知下载结果及下载进度(成功或失败)
     */
    public DownloadThread(String urlStr, String path, String fileName, Handler handler) {
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        this.urlStr = urlStr;
        this.path = path;
        this.fileName = fileName;
        this.handler = handler;
    }

    @Override
    public void run() {
        com.ztesoft.level1.util.HttpDownloadUtil httpDlu = new com.ztesoft.level1.util
                .HttpDownloadUtil();
        int i = httpDlu.httpDownFile(urlStr, path, fileName, true, handler);

        Message message = handler.obtainMessage();
        message.what = i;
        message.obj = path + fileName;
        handler.sendMessage(message);
    }
}