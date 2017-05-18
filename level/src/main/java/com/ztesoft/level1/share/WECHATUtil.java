package com.ztesoft.level1.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.ztesoft.level1.util.BitmapUtils;

import java.io.File;

public class WECHATUtil {

    private IWXAPI api;

    public WECHATUtil(Context context, String app_id) {
        api = WXAPIFactory.createWXAPI(context, app_id, true);
        api.registerApp(app_id);
    }

    /**
     * 分享文本
     *
     * @param text 文本内容
     * @param type 分享类型
     */
    public boolean sendText(String text, int type) {

        if (TextUtils.isEmpty(text)) {
            return false;
        }

        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = text;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        if (type == Fenxiang.Fenxiang_WECHATMOMENTS) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        } else if (type == Fenxiang.Fenxiang_WECHAT) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        }
        return api.sendReq(req);
    }

    /**
     * 分享图片
     *
     * @param imagePath 图片绝对路径
     * @param type      分享类型
     */
    public boolean sendImage(String imagePath, int type) {
        if (imagePath == null || "".equals(imagePath)) {
            return false;
        }
        File file = new File(imagePath);
        if (!file.exists()) {
            return false;
        }

        WXImageObject imgObj = new WXImageObject();
        imgObj.setImagePath(imagePath);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int num = 1;
        if (width > height) {
            num = width / 100;
        } else {
            num = height / 100;
        }
        width = width / num;
        height = height / num;
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, width, height, true);// 设置缩略图
        bitmap.recycle();
        msg.thumbData = BitmapUtils.bmpToBytes(thumbBmp);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        if (type == Fenxiang.Fenxiang_WECHATMOMENTS) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        } else if (type == Fenxiang.Fenxiang_WECHAT) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        }
        return api.sendReq(req);
    }

    /**
     * 分享图片
     *
     * @param bitmap
     * @param type
     */
    public boolean sendImage(Bitmap bitmap, int type) {
        if (bitmap == null) {
            return false;
        }

        Bitmap tempBitmap = bitmap.copy(Config.ARGB_8888, true);
        WXImageObject imgObj = new WXImageObject(tempBitmap);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int num = 1;
        if (width > height) {
            num = width / 100;
        } else {
            num = height / 100;
        }
        width = width / num;
        height = height / num;
        Bitmap thumbBmp = Bitmap.createScaledBitmap(tempBitmap, width, height, true);// 设置缩略图
        tempBitmap.recycle();
        msg.thumbData = BitmapUtils.bmpToBytes(thumbBmp);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        if (type == Fenxiang.Fenxiang_WECHATMOMENTS) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        } else if (type == Fenxiang.Fenxiang_WECHAT) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        }
        return api.sendReq(req);
    }

    /**
     * 分享网页链接
     *
     * @param imagePath   图片的本地路径
     * @param title       分享标题
     * @param description 分享文本内容
     * @param webPageUrl  网页链接
     */
    public boolean sendWebpage(String imagePath, String title, String description, String
            webPageUrl, int type) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = webPageUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = description;

        File file = new File(imagePath);
        if (!file.exists()) {
            return false;
        }
        Bitmap thumb = BitmapFactory.decodeFile(imagePath);
        if (thumb != null) {
            msg.thumbData = BitmapUtils.bmpToBytes(thumb);
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        if (type == Fenxiang.Fenxiang_WECHATMOMENTS) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        } else if (type == Fenxiang.Fenxiang_WECHAT) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        }
        return api.sendReq(req);
    }

    /**
     * 分享网页链接
     *
     * @param bitmap      二进制图片
     * @param title       分享标题
     * @param description 分享文本内容
     * @param webPageUrl  网页链接
     */
    public boolean sendWebpage(Bitmap bitmap, String title, String description, String
            webPageUrl, int type) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = webPageUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = description;

        Bitmap thumb = bitmap.copy(Config.ARGB_8888, true);
        bitmap.recycle();

        if (thumb != null) {
            msg.thumbData = BitmapUtils.bmpToBytes(thumb);
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        if (type == Fenxiang.Fenxiang_WECHATMOMENTS) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        } else if (type == Fenxiang.Fenxiang_WECHAT) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        }
        return api.sendReq(req);
    }
}
