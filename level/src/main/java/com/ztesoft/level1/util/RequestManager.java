package com.ztesoft.level1.util;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 文件名称 : RequestManager
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : okhttp数据请求
 * <p>
 * 创建时间 : 2017/4/17 15:49
 * <p>
 */
public class RequestManager {

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse
            ("application/x-www-form-urlencoded; charset=utf-8");  //mdiatype 这个需要和服务端保持一致

    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; " +
            "charset=utf-8");  //mdiatype 这个需要和服务端保持一致

    private static volatile RequestManager mInstance;//单利引用

    public static final int TYPE_GET = 0;//get请求
    public static final int TYPE_POST_JSON = 1;//post请求参数为json
    public static final int TYPE_POST_FORM = 2;//post请求参数为表单

    private OkHttpClient mOkHttpClient;//okHttpClient 实例
    private Handler okHttpHandler;//全局处理子线程和M主线程通信

    private JSONObject otherHeaderObject;  //Header中需要加入定制化内容时，可设置该属性

    private Handler synHandler;  //同步请求时，传入的Hanlder，用来和主线程通信

    /**
     * 初始化RequestManager
     */
    public RequestManager(Context context) {
        //初始化OkHttpClient
        mOkHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS) //设置超时时间
                .readTimeout(10, TimeUnit.SECONDS) //设置读取超时时间
                .writeTimeout(10, TimeUnit.SECONDS) //设置写入超时时间
                .build();
        //初始化Handler
        okHttpHandler = new Handler(context.getMainLooper());
    }

    /**
     * 获取单例引用
     *
     * @return
     */
    public static RequestManager getInstance(Context context) {
        RequestManager inst = mInstance;
        if (inst == null) {
            synchronized (RequestManager.class) {
                inst = mInstance;
                if (inst == null) {
                    inst = new RequestManager(context.getApplicationContext());
                    mInstance = inst;
                }
            }
        }
        return inst;
    }

    /**
     * okHttp同步请求统一入口
     *
     * @param actionUrl   接口地址
     * @param requestType 请求类型
     * @param params      请求参数
     */
    public void requestSyn(String actionUrl, int requestType, JSONObject params) {

        Message msg = synHandler.obtainMessage();

        switch (requestType) {
            case TYPE_GET:
                requestGetBySyn(actionUrl, params, msg);
                break;
            case TYPE_POST_JSON:
                requestPostBySyn(actionUrl, params, msg);
                break;
            case TYPE_POST_FORM:
                requestPostBySynWithForm(actionUrl, params, msg);
                break;
        }

        synHandler.sendMessage(msg);
    }

    /**
     * okHttp get同步请求
     *
     * @param actionUrl 接口地址
     * @param params    请求参数
     */
    private void requestGetBySyn(String actionUrl, JSONObject params, Message msg) {
        StringBuilder tempParams = new StringBuilder();
        try {
            //处理参数
            int pos = 0;
            Iterator<String> iterator = params.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();

                if (pos > 0) {
                    tempParams.append("&");
                }

                //对参数进行URLEncoder
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(params.optString
                        (key), "utf-8")));
                pos++;
            }

            //补全请求地址
            String requestUrl = String.format("%s?%s", actionUrl, tempParams.toString());
            //创建一个请求
            Request request = addHeaders().url(requestUrl).build();
            //创建一个Call
            final Call call = mOkHttpClient.newCall(request);
            //执行请求
            final Response response = call.execute();

            Bundle bundle = new Bundle();
            String string;
            if (response.isSuccessful()) {
                string = response.body().string();
                msg.what = 0;
            } else {
                string = "服务器错误";
                msg.what = 1;
            }
            bundle.putString("state", string);
            msg.setData(bundle);

        } catch (Exception e) {
            Bundle bundle = new Bundle();
            bundle.putString("state", "JSON格式错误");
            msg.what = 2;
            msg.setData(bundle);
        }
    }

    /**
     * okHttp post同步请求
     *
     * @param actionUrl 接口地址
     * @param params    请求参数
     */
    private void requestPostBySyn(String actionUrl, JSONObject params, Message msg) {
        try {
            //处理参数
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;

            Iterator<String> iterator = params.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();

                if (pos > 0) {
                    tempParams.append("&");
                }

                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(params.optString
                        (key), "utf-8")));
                pos++;
            }

            //生成参数
            String paramsStr = tempParams.toString();
            //创建一个请求实体对象 RequestBody
            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, paramsStr);
            //创建一个请求
            final Request request = addHeaders().url(actionUrl).post(body).build();
            //创建一个Call
            final Call call = mOkHttpClient.newCall(request);
            //执行请求
            Response response = call.execute();

            Bundle bundle = new Bundle();
            String string;
            if (response.isSuccessful()) {  //请求执行成功
                string = response.body().string();
                msg.what = 0;
            } else {
                string = "服务器错误";
                msg.what = 1;
            }
            bundle.putString("state", string);
            msg.setData(bundle);

        } catch (Exception e) {
            Bundle bundle = new Bundle();
            bundle.putString("state", "JSON格式错误");
            msg.what = 2;
            msg.setData(bundle);
        }
    }

    /**
     * okHttp post同步请求表单提交
     *
     * @param actionUrl 接口地址
     * @param params    请求参数
     */
    private void requestPostBySynWithForm(String actionUrl, JSONObject params, Message msg) {
        try {
            //创建一个FormBody.Builder
            FormBody.Builder builder = new FormBody.Builder();

            Iterator<String> iterator = params.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                //追加表单信息
                builder.add(key, params.optString(key));
            }

            //生成表单实体对象
            RequestBody formBody = builder.build();
            //创建一个请求
            final Request request = addHeaders().url(actionUrl).post(formBody).build();
            //创建一个Call
            final Call call = mOkHttpClient.newCall(request);
            //执行请求
            Response response = call.execute();
            Bundle bundle = new Bundle();
            String string;
            if (response.isSuccessful()) {  //请求执行成功
                string = response.body().string();
                msg.what = 0;
            } else {
                string = "服务器错误";
                msg.what = 1;
            }
            bundle.putString("state", string);
            msg.setData(bundle);

        } catch (Exception e) {
            Bundle bundle = new Bundle();
            bundle.putString("state", "JSON格式错误");
            msg.what = 2;
            msg.setData(bundle);
        }
    }

    /**
     * okHttp异步请求统一入口
     *
     * @param actionUrl   接口地址
     * @param requestType 请求类型
     * @param params      请求参数
     * @param callBack    请求返回数据回调
     * @param <T>         数据泛型
     **/
    public <T> Call requestAsyn(String actionUrl, int requestType, JSONObject params,
                                ReqCallBack<T> callBack) {
        Call call = null;
        switch (requestType) {
            case TYPE_GET:
                call = requestGetByAsyn(actionUrl, params, callBack);
                break;
            case TYPE_POST_JSON:
                call = requestPostByAsyn(actionUrl, params, callBack);
                break;
            case TYPE_POST_FORM:
                call = requestPostByAsynWithForm(actionUrl, params, callBack);
                break;
        }
        return call;
    }

    /**
     * okHttp get异步请求
     *
     * @param actionUrl 接口地址
     * @param params    请求参数
     * @param callBack  请求返回数据回调
     * @param <T>       数据泛型
     * @return
     */
    private <T> Call requestGetByAsyn(String actionUrl, JSONObject params, final ReqCallBack<T>
            callBack) {
        StringBuilder tempParams = new StringBuilder();
        try {
            int pos = 0;

            Iterator<String> iterator = params.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();

                if (pos > 0) {
                    tempParams.append("&");
                }

                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(params.optString
                        (key), "utf-8")));
                pos++;
            }

            String requestUrl = String.format("%s?%s", actionUrl, tempParams.toString());
            final Request request = addHeaders().url(requestUrl).build();
            final Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    failedCallBack(1, "访问失败", callBack);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        successCallBack((T) string, callBack);
                    } else {
                        failedCallBack(1, "服务器错误", callBack);
                    }
                }
            });
            return call;

        } catch (Exception e) {
            failedCallBack(2, "JSON格式错误", callBack);
            return null;
        }
    }

    /**
     * okHttp post异步请求
     *
     * @param actionUrl 接口地址
     * @param params    请求参数
     * @param callBack  请求返回数据回调
     * @param <T>       数据泛型
     * @return
     */
    private <T> Call requestPostByAsyn(String actionUrl, JSONObject params, final ReqCallBack<T>
            callBack) {
        try {
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;

            Iterator<String> iterator = params.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();

                if (pos > 0) {
                    tempParams.append("&");
                }

                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(params.optString
                        (key), "utf-8")));
                pos++;
            }

            String paramsStr = tempParams.toString();
            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, paramsStr);
            final Request request = addHeaders().url(actionUrl).post(body).build();
            final Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    failedCallBack(1, "访问失败", callBack);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        successCallBack((T) string, callBack);
                    } else {
                        failedCallBack(1, "服务器错误", callBack);
                    }
                }
            });
            return call;
        } catch (Exception e) {
            failedCallBack(2, "JSON格式错误", callBack);
            return null;
        }
    }

    /**
     * okHttp post异步请求表单提交
     *
     * @param actionUrl 接口地址
     * @param params    请求参数
     * @param callBack  请求返回数据回调
     * @param <T>       数据泛型
     * @return
     */
    private <T> Call requestPostByAsynWithForm(String actionUrl, JSONObject params, final
    ReqCallBack<T> callBack) {
        try {
            FormBody.Builder builder = new FormBody.Builder();

            Iterator<String> iterator = params.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();

                builder.add(key, params.optString(key));
            }

            RequestBody formBody = builder.build();
            final Request request = addHeaders().url(actionUrl).post(formBody).build();
            final Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    failedCallBack(1, "访问失败", callBack);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        successCallBack((T) string, callBack);
                    } else {
                        failedCallBack(1, "服务器错误", callBack);
                    }
                }
            });
            return call;
        } catch (Exception e) {
            failedCallBack(2, "JSON格式错误", callBack);
            return null;
        }
    }

    /**
     * 统一为请求添加头信息
     *
     * @return
     */
    private Request.Builder addHeaders() {
        Request.Builder builder = new Request.Builder()
                .addHeader("Connection", "keep-alive")
                .addHeader("platform", "2")
                .addHeader("phoneModel", Build.MODEL)
                .addHeader("systemVersion", Build.VERSION.RELEASE);

        if (null != otherHeaderObject && otherHeaderObject.length() > 0) {
            Iterator<String> iterator = otherHeaderObject.keys();

            while (iterator.hasNext()) {
                String key = iterator.next();

                builder.addHeader(key, otherHeaderObject.optString(key));
            }
        }

        return builder;
    }

    /**
     * 统一同意处理成功信息
     *
     * @param result
     * @param callBack
     * @param <T>
     */
    private <T> void successCallBack(final T result, final ReqCallBack<T> callBack) {
        okHttpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqSuccess(result);
                }
            }
        });
    }

    /**
     * 统一处理失败信息
     *
     * @param errorMsg
     * @param callBack
     * @param <T>
     */
    private <T> void failedCallBack(final int errorCode, final String errorMsg, final ReqCallBack<T>
            callBack) {
        okHttpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqFailed(errorCode, errorMsg);
                }
            }
        });
    }

    public interface ReqCallBack<T> {
        /**
         * 响应成功
         */
        void onReqSuccess(T result);

        /**
         * 响应失败
         *
         * @param errorCode 错误码，0表示成功，1表示已知错误，2其它错误
         * @param errorMsg
         */
        void onReqFailed(int errorCode, String errorMsg);
    }

    public void setSynHandler(Handler synHandler) {
        this.synHandler = synHandler;
    }

    public void setOtherHeaderObject(JSONObject otherHeaderObject) {
        this.otherHeaderObject = otherHeaderObject;
    }
}
