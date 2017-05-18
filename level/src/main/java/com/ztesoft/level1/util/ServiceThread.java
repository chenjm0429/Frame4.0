package com.ztesoft.level1.util;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.ztesoft.level1.Level1Bean;
import com.ztesoft.level1.R;
import com.ztesoft.level1.util.encrypt.AES256Help;
import com.ztesoft.level1.util.encrypt.Base64Help;
import com.ztesoft.level1.util.encrypt.DESHelp;
import com.ztesoft.level1.util.encrypt.RC4Help;
import com.ztesoft.level1.util.encrypt.TranspositionHelp;

import org.apache.http.HttpStatus;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.Locale;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class ServiceThread extends Thread {
//    private static String header = "";

    private Context ctx = null;
    private String httpUrl = "";
    private JSONObject param;

    private int encryFlag = 0;
    private String encryKey;
    private int threadId = 0;
    private int connTimeout = 10000;
    private int readTimeout = 20000;
    private ServiceHandler serviceHandler;
    private boolean isAddRadomCode = false;  //是否增加随机码功能，默认不增加
    private String radomCode = UUID.randomUUID().toString();
    //请求服务前带入的数据，请求成功后需要一并返回给请求调用者
    private Object backObj = null;

    /**
     * 是否需要对加密文件进行转义操作，当服务端为新框架时，需进行此项操作
     */
    private boolean isNeedEncode = false;

    /**
     * 服务访问线程
     *
     * @param httpUrl 服务url
     * @param param   传递的参数
     * @param ctx     上下文
     */
    public ServiceThread(String httpUrl, JSONObject param, Context ctx) {
        if (!httpUrl.startsWith("http")) {
            httpUrl = Level1Bean.SERVICE_PATH + httpUrl;
        }
        this.httpUrl = httpUrl;
        this.param = param;
        this.ctx = ctx;
    }

    @Override
    public void run() {
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        HttpURLConnection conn = null;
        try {
            NetworkUtil netWork = new NetworkUtil(ctx);
            if (!netWork.isNetworkAvailable()) {
                throw new com.ztesoft.level1.exception.MyException(ctx.getString(R.string
                        .error_network));
            }
            param.put("mobileType", "android-phone");
            Locale locale = ctx.getResources().getConfiguration().locale;
            param.put("locales", locale.getLanguage());
            param.put("radomCode", radomCode);
            try {
                String systemVersion = Build.VERSION.INCREMENTAL;  //先取对应厂商的版本号
                if (TextUtils.isEmpty(systemVersion)) {
                    systemVersion = Build.VERSION.RELEASE;  //如取不到，再取android版本号
                }

                param.put("mobileBrand", android.os.Build.MANUFACTURER); // 手机品牌
                param.put("mobileSys", Build.VERSION.RELEASE); // android系统版本
                param.put("systemVersion", systemVersion); // 系统版本
                param.put("mobileModel", android.os.Build.MODEL); // 手机型号
            } catch (Exception e) {
                param.put("systemVersion", "获取错误");
                param.put("mobileModel", "获取错误");
            }
            conn = netWork.getURLConnection(httpUrl);
            if (httpUrl.startsWith("https")) {
                SSLContext sc = SSLContext.getInstance("TLS");
                KeyStore tks = KeyStore.getInstance("BKS", "BC");
                tks.load(ctx.getResources().openRawResource(R.raw.client), "linkage".toCharArray());
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(tks);
                KeyManagerFactory keyManager = KeyManagerFactory.getInstance("X509");
                keyManager.init(tks, "linkage".toCharArray());
                sc.init(keyManager.getKeyManagers(), tmf.getTrustManagers(), null);
                // sc.init(null, tmf.getTrustManagers(),
                // null);
                X509HostnameVerifier hostnameVerifier = SSLSocketFactory
                        .ALLOW_ALL_HOSTNAME_VERIFIER;
                HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
                ((HttpsURLConnection) conn).setSSLSocketFactory(sc.getSocketFactory());
            }
//            conn.setRequestProperty("session", header);
            conn.setRequestProperty("currTimeMillis", "" + System.currentTimeMillis());
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(connTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("contentType", "utf-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            OutputStream outStream = conn.getOutputStream();
            String input = param.toString();
            if (1 == encryFlag) {
                input = TranspositionHelp.getInstance().encrypt(input, encryKey);
            } else if (2 == encryFlag) {
                input = DESHelp.getInstance().encrypt(input, encryKey);
            } else if (3 == encryFlag) {
                input = Base64Help.getInstance().encrypt(input);
            } else if (4 == encryFlag) {
                input = AES256Help.encrypt(input, encryKey);
            } else if (5 == encryFlag) {
                input = RC4Help.encrypt(input, encryKey);
            }

            if (isNeedEncode() && encryFlag != 0) {
                input = URLEncoder.encode(input, "UTF-8");
            }

            outStream.write(input.getBytes("utf-8"));
            outStream.flush();
            outStream.close();

//            if (conn.getHeaderField("session") != null)
//                header = conn.getHeaderField("session");

            StringBuffer result = new StringBuffer();
            if (conn.getResponseCode() == HttpStatus.SC_OK) {
                handler.sendEmptyMessage(9);

                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(conn
                        .getInputStream()));
                String line;
                while ((line = bufferReader.readLine()) != null) {
                    result.append(line);
                }
                if (bufferReader != null) {
                    bufferReader.close();
                }
            } else {// 包括 返回超时
                throw new com.ztesoft.level1.exception.MyException(ctx.getString(R.string
                        .error_serivce));
            }

            String str = result.toString();
            if ("".equals(str)) {
                throw new com.ztesoft.level1.exception.MyException("null###########");
            }

            if (isNeedEncode() && encryFlag != 0) {
                str = URLDecoder.decode(str, "UTF-8");
            }

            if (1 == encryFlag) {
                str = TranspositionHelp.getInstance().decrypt(str, encryKey);
            } else if (2 == encryFlag) {
                str = DESHelp.getInstance().decrypt(str, encryKey);
            } else if (3 == encryFlag) {
                str = Base64Help.getInstance().decrypt(str);
            } else if (4 == encryFlag) {
                str = AES256Help.decrypt(str, encryKey);
            } else if (5 == encryFlag) {
                str = RC4Help.decrypt(str, encryKey);
            }

            b.putString("state", str);
            result = null;
            System.gc();

            msg.setData(b);
            msg.what = 0;
            handler.sendMessage(msg);
        } catch (com.ztesoft.level1.exception.MyException e) {// 已知错误
            msg.what = 1;
            b.putString("state", e.getMessage());
            msg.setData(b);
            handler.sendMessage(msg);
        } catch (SocketTimeoutException e) {// 链接超时
            msg.what = 1;
            b.putString("state", ctx.getString(R.string.error_serivce));
            msg.setData(b);
            handler.sendMessage(msg);
        } catch (Exception e) {// 未知错误
            msg.what = 1;
            b.putString("state", ctx.getString(R.string.error_noknow));
            msg.setData(b);
            handler.sendMessage(msg);
        } finally {
            try {
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 9) {
                serviceHandler.begin(ServiceThread.this);
            } else if (msg.what == 1) {
                serviceHandler.fail(ServiceThread.this, "1", msg.getData().getString("state"));
            } else {
                try {
                    JSONObject obj = new JSONObject(msg.getData().getString("state"));
                    if ("0".equals(obj.optString("isSucess"))) {  //失败
                        serviceHandler.fail(ServiceThread.this, "2", obj.optString
                                ("errorMessgae", ctx.getString(R.string.error_serivce)));
                        
                    } else if ("1".equals(obj.optString("isSucess"))) {  //成功
                        if (isAddRadomCode && !radomCode.equals(obj.optString("radomCode"))) {
                            serviceHandler.fail(ServiceThread.this, "2", ctx.getString(R.string
                                    .error_safe));
                        } else {
                            serviceHandler.success(ServiceThread.this, obj);
                        }
                    }
                } catch (JSONException e) {
                    serviceHandler.fail(ServiceThread.this, "2", ctx.getString(R.string
                            .error_json));
                }
            }
        }
    };

    public interface ServiceHandler {
        /**
         * 开始接收服务端返回数据
         *
         * @param st 自身，可用于获取链接url，传递参数，线程号等信息
         */
        void begin(ServiceThread st);

        /**
         * 服务端成功返回
         *
         * @param st      自身，可用于获取链接url，传递参数，线程号等信息
         * @param dataObj 返回数据（需要对flag属性再次判断）
         */
        void success(ServiceThread st, JSONObject dataObj);

        /**
         * 未能得到服务端响应
         *
         * @param st           自身，可用于获取链接url，传递参数，线程号等信息
         * @param errorCode    错误编码,1开头的可以重试，2开头的直接提示
         * @param errorMessage 错误原因
         */
        void fail(ServiceThread st, String errorCode, String errorMessage);
    }

    public void setServiceHandler(ServiceHandler serviceHandler) {
        this.serviceHandler = serviceHandler;
    }

    public void setEncryFlag(int encryFlag) {
        this.encryFlag = encryFlag;
    }

    public void setEncryKey(String encryKey) {
        this.encryKey = encryKey;
    }

    /**
     * 链接服务超时时间
     *
     * @param connTimeout 毫秒数
     */
    public void setConnTimeout(int connTimeout) {
        this.connTimeout = connTimeout;
    }

    /**
     * 返回数据超时时间
     *
     * @param readTimeout 毫秒数
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public JSONObject getParam() {
        return param;
    }

    public int getThreadId() {
        return threadId;
    }

    public void setBackObj(Object obj) {
        this.backObj = obj;
    }

    public Object getBackObj() {
        return backObj;
    }

    public boolean isAddRadomCode() {
        return isAddRadomCode;
    }

    public void setAddRadomCode(boolean isAddRadomCode) {
        this.isAddRadomCode = isAddRadomCode;
    }

    public boolean isNeedEncode() {
        return isNeedEncode;
    }

    public void setNeedEncode(boolean isNeedEncode) {
        this.isNeedEncode = isNeedEncode;
    }
}
