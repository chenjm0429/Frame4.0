package com.ztesoft.level1.util;

import android.os.Handler;

import com.ztesoft.level1.Level1Bean;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 文件名称 : HttpDownloadUtil
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 文件下载公用类
 * <p>
 * 创建时间 : 2017/5/22 16:54
 * <p>
 */
public class HttpDownloadUtil {

    /**
     * 将指定的远程文件，下载到SD卡指定目录
     *
     * @param urlStr   http开头的文件地址
     * @param path     SD卡目录的相对路径，如"/MobileBI_RC/Update"
     * @param fileName 保存后的文件名，如"test1.pdf"
     * @param flag     是否覆盖已有文件(true表示强制覆盖，false比较文件的修改日期判断是否下载
     * @param handler  通过handler提示下载进度
     * @return 0 成功，1 文件已存在，-1 下载失败
     */
    public int httpDownFile(String urlStr, String path, String fileName, boolean flag, Handler
            handler) {
        InputStream inputStream = null;
        HttpURLConnection urlConn = null;
        try {
            URL url = new URL(urlStr);
            urlConn = (HttpURLConnection) url.openConnection();
            com.ztesoft.level1.util.SDCardUtil sdCardUtil = new com.ztesoft.level1.util
                    .SDCardUtil();
            if (!flag && sdCardUtil.isFileExist(path + fileName)) {// 不覆盖且文件已存在
                long localModified = new File(Level1Bean.SD_ROOTPATH + path + fileName)
                        .lastModified();// 可以拿到最后三位
                localModified = localModified / 1000;
                long remoteModified = urlConn.getLastModified();// 似乎最后三位一直未0
                remoteModified = remoteModified / 1000;
                if (remoteModified == localModified) {// 如果服务端文件和本地文件一致，则不下载
                    return Level1Bean.DOWNLOAD_ED;
                }
            }
            inputStream = urlConn.getInputStream();
            int fileSize = urlConn.getContentLength();// 获取文件总字节
            if (inputStream == null)
                return Level1Bean.DOWNLOAD_FAIL;
            File resultFile;
            resultFile = sdCardUtil.write2SDFromInput(path, fileName, inputStream, handler,
                    fileSize);
            if (resultFile == null)
                return Level1Bean.DOWNLOAD_FAIL;
        } catch (Exception e) {
            e.printStackTrace();
            return Level1Bean.DOWNLOAD_FAIL;
        } finally {
            try {
                if (urlConn != null) {
                    urlConn.disconnect();
                }
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Level1Bean.DOWNLOAD_COMPLETE;
    }

    // 不需要提示下载进度
    public int httpDownFile(String urlStr, String path, String fileName, boolean flag) {
        return httpDownFile(urlStr, path, fileName, flag, null);
    }
}
