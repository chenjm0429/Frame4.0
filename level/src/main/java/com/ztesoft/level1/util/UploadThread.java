package com.ztesoft.level1.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ztesoft.level1.Level1Bean;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * 文件名称 : UploadThread
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 数据上传
 * <p>
 * 创建时间 : 2017/5/23 9:16
 * <p>
 */
public class UploadThread implements Runnable {

    private static final String BOUNDARY = UUID.randomUUID().toString(); // 边界标识，随机生成

    private static final String PREFIX = "--";
    private static final String LINE_END = "\r\n";
    private static final String CONTENT_TYPE = "multipart/form-data"; // 内容类型
    private String filePath;
    private String uploadPath;
    private String serverUrl;
    private Handler sh;

    /**
     * @param serverUrl  服务url地址，例如http://192.168.11.200:2590/
     * @param filePath   文件本地路径
     * @param staffId    当前用户编码
     * @param sh
     * @param uploadPath 文件上传目的路径
     */
    public UploadThread(String serverUrl, String filePath, String staffId, Handler sh, String
            uploadPath) {
        this.serverUrl = serverUrl;
        this.filePath = filePath;
        this.sh = sh;
        if (uploadPath != null && !"".equals(uploadPath)) {
            this.uploadPath = uploadPath;
        } else {
            this.uploadPath = staffId;
        }
    }

    @Override
    public void run() {
        File ff = new File(filePath);
        if (!ff.exists()) {
            Message msg = new Message();
            msg.what = Level1Bean.UPLOAD_FAIL;
            msg.obj = "文件不存在";
            sh.sendMessage(msg);
            return;
        }

        HttpURLConnection urlConn = null;
        try {
            URL url = new URL(serverUrl + "/jsp/file_upload.jsp?fileName=" + ff.getName() +
                    "&filePath=" + uploadPath);// 文件上传路径和名称
            urlConn = (HttpURLConnection) url.openConnection();

            urlConn.setDoInput(true); // 允许输入流
            urlConn.setDoOutput(true); // 允许输出流
            urlConn.setUseCaches(false); // 不允许使用缓存
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Charset", "utf-8");  //设置编码
            urlConn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

            OutputStream ds = urlConn.getOutputStream();

            StringBuffer sb = new StringBuffer();
            sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
            sb.append("Content-Disposition:form-data; name=\"fileAddPic" + "\"; filename=\""
                    + System.currentTimeMillis() + ".png" + "\"" + LINE_END);
            sb.append("Content-Type:image/png" + LINE_END); // 这里配置的Content-type很重要的，用于服务器端辨别文件的类型的
            sb.append("Content-Type: application/octet-stream; charset=utf-8" + LINE_END);
            sb.append(LINE_END);
            String param = sb.toString();
            ds.write(param.getBytes());

            sh.sendEmptyMessage(Level1Bean.UPLOAD_PROS);

            byte[] buffer = new byte[1024];
            int length = -1;
            FileInputStream fStream = new FileInputStream(filePath);
            /* 从文件读取数据到缓冲区 */
            while ((length = fStream.read(buffer)) != -1) {
				/* 将数据写入DataOutputStream中 */
                ds.write(buffer, 0, length);
            }
            fStream.close();

            ds.write(LINE_END.getBytes());
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
            ds.write(end_data);
            ds.flush();
            ds.close();

            if (urlConn.getResponseCode() == HttpStatus.SC_OK) {
                String message = urlConn.getHeaderField("reason");
                if (message != null && !"".equals(message)) {// 上传错误，提示错误原因
                    if ("sameNameFile".equals(message)) {
                        message = "该目录已存在同名文件";
                    } else {
                        message = "上传失败";
                    }
                    Message msg = new Message();
                    msg.what = Level1Bean.UPLOAD_FAIL;
                    msg.obj = message;
                    sh.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.what = Level1Bean.UPLOAD_COMPLETE;
                    msg.obj = uploadPath + "/" + ff.getName();
                    Bundle b = new Bundle();
                    b.putString("filePath", filePath);
                    msg.setData(b);
                    sh.sendMessage(msg);
                }
            } else {
                Message msg = new Message();
                msg.what = Level1Bean.UPLOAD_FAIL;
                sh.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = Level1Bean.UPLOAD_FAIL;
            msg.obj = e.toString();
            sh.sendMessage(msg);
        } finally {
            try {
                if (urlConn != null) {
                    urlConn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface UploadReturn {
        void begin();

        void success(String fileUrl);

        void fail(String reason);
    }
}
