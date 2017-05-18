package com.ztesoft.level1.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ztesoft.level1.R;
import com.ztesoft.level1.gridview.GridAdapter;
import com.ztesoft.level1.gridview.MyGridView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class Fenxiang {

    private String wechat_app_id;
    private String yixin_app_id;
    private Activity act;

    private com.ztesoft.level1.ui.MyAlertDialog mad;
    private ArrayList<JSONObject> lstImageItem = null;

    public final static int Fenxiang_message = 1;
    public final static int Fenxiang_WECHAT = 2;
    public final static int Fenxiang_WECHATMOMENTS = 3;
    public final static int Fenxiang_EMAIL = 4;

    private boolean cancel = false;

    /**
     * @param fff Fenxiang中的常量组成int数组，如Fenxiang_message
     */
    public Fenxiang(Activity act, int[] fff) {
        this.act = act;
        xx(fff);
    }

    //拼接九宫格数据
    private void xx(int[] fff) {
        lstImageItem = new ArrayList<JSONObject>();
        try {
            if (fff == null || fff.length == 0) {
                fff = new int[]{1, 4};
            }
            for (int i = 0; i < fff.length; i++) {
                switch (fff[i]) {
                    case 1:
                        JSONObject obj1 = new JSONObject();
                        obj1.put("picCode", "logo_shortmessage");
                        obj1.put("rptName", "彩信");
                        obj1.put("rptCode", fff[i]);
                        obj1.put("delFlag", "1");
                        lstImageItem.add(obj1);
                        break;
                    case 2:
                        JSONObject obj2 = new JSONObject();
                        obj2.put("picCode", "logo_wechat");
                        obj2.put("rptName", "微信");
                        obj2.put("rptCode", fff[i]);
                        obj2.put("delFlag", "1");
                        lstImageItem.add(obj2);
                        break;

                    case 3:
                        JSONObject obj21 = new JSONObject();
                        obj21.put("picCode", "logo_wechatmoments");
                        obj21.put("rptName", "微信朋友圈");
                        obj21.put("rptCode", fff[i]);
                        obj21.put("delFlag", "1");
                        lstImageItem.add(obj21);
                        break;

                    case 4:
                        JSONObject obj3 = new JSONObject();
                        obj3.put("picCode", "logo_email");
                        obj3.put("rptName", "邮件");
                        obj3.put("rptCode", fff[i]);
                        obj3.put("delFlag", "1");
                        lstImageItem.add(obj3);
                        break;

                    case 5:
                        JSONObject obj4 = new JSONObject();
                        obj4.put("picCode", "logo_yixin");
                        obj4.put("rptName", "易信");
                        obj4.put("rptCode", fff[i]);
                        obj4.put("delFlag", "1");
                        lstImageItem.add(obj4);
                        break;

                    case 6:
                        JSONObject obj5 = new JSONObject();
                        obj5.put("picCode", "logo_yixinmoments");
                        obj5.put("rptName", "易信朋友圈");
                        obj5.put("rptCode", fff[i]);
                        obj5.put("delFlag", "1");
                        lstImageItem.add(obj5);
                        break;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (lstImageItem == null || lstImageItem.size() == 0) {
            xx(null);
        }
    }

    /**
     * 如需要微信分享，需要在该方法之前调用setWechat_app_id方法
     * 如需要易信分享，需要在该方法之前调用setYixin_app_id方法
     *
     * @param imagePath 分享的图片路径
     * @param text      分享的文本内容（部分支持）
     */
    public void share(final String imagePath, final String text) {
        ImageAdapter myGrid = new ImageAdapter(act, lstImageItem);
        MyGridView mm = new MyGridView(act, myGrid);
        mm.setNumColumns(3);
        mm.setPageListener(new MyGridView.G_PageListener() {

            @Override
            public void addClick() {
            }

            @Override
            public void itemClick(int arg2) {
                JSONObject obj = lstImageItem.get(arg2);
                ddd(obj.optInt("rptCode"), imagePath, text);
                mad.dismiss();
            }

            @Override
            public void delClick(JSONObject obj) {
            }

            @Override
            public void moving() {
            }
        });
        mm.create();

        mad = new com.ztesoft.level1.ui.MyAlertDialog(act);
        mad.setTitle("分享");
        mad.setView(mm);
        if (cancel) {
            mad.setCanceledOnTouchOutside(true);
        }
        mad.show();
    }

    class ImageAdapter extends GridAdapter {
        public ImageAdapter(Context context, ArrayList<JSONObject> lstImageItem) {
            super(context, lstImageItem);
        }

        @Override
        public View addBodyView(JSONObject obj) throws JSONException {
            LinearLayout result = new LinearLayout(context);
            result.setOrientation(LinearLayout.VERTICAL);
            result.setGravity(Gravity.CENTER);
            result.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));

            ImageView img = new ImageView(context);
            int resId = R.drawable.logo_email;
            try {
                resId = R.drawable.class.getDeclaredField(obj.optString("picCode")).getInt(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            img.setImageResource(resId);
            img.setScaleType(ImageView.ScaleType.FIT_CENTER);
            result.addView(img);

            TextView text = new TextView(context);
            text.setText(obj.optString("rptName"));
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            text.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            text.setTextColor(Color.BLACK);
            text.setSingleLine();
            text.setEllipsize(TruncateAt.END);
            result.addView(text, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

            return result;
        }

        @Override
        public boolean isAllowsDel(JSONObject obj) {
            return false;
        }

        @Override
        public String getItemName(int position) {
            return "";
        }

        @Override
        public void setLayoutStyle(RelativeLayout layout, int position) throws JSONException {
        }
    }

    //触发分享功能
    public void ddd(int type, String imagePath, String text) {
        File file = new File(imagePath);
        switch (type) {
            case Fenxiang_message:
                try {
                    Intent sendMSGIntent = sendMMS(file, text);
                    //指定到彩信页面,小米、三星没有com.android.mms.ui.ComposeMessageActivity类
//				sendMSGIntent.setClassName("com.android.mms", "com.android.mms.ui
// .ComposeMessageActivity");
                    sendMSGIntent.setPackage("com.android.mms");
                    act.startActivity(Intent.createChooser(sendMSGIntent, "请选择彩信"));
                } catch (Exception e) {
                    act.startActivity(Intent.createChooser(sendMMS(file, text), "请选择彩信"));
                }
                break;
            case Fenxiang_EMAIL:
                Uri uri = Uri.parse("mailto:");
                Intent intent = new Intent(Intent.ACTION_SEND, uri);
                intent.putExtra(Intent.EXTRA_SUBJECT, "这是邮件的主题部分"); // 主题
                intent.putExtra(Intent.EXTRA_TEXT, "这是邮件的正文部分"); // 正文
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                intent.setType("mailto/*");
                act.startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
                break;
            case Fenxiang_WECHAT:
            case Fenxiang_WECHATMOMENTS:
                com.ztesoft.level1.share.WECHATUtil wechatUtil = new com.ztesoft.level1.share
                        .WECHATUtil(act, wechat_app_id);
                wechatUtil.sendImage(imagePath, type);
                break;
            default:
                break;
        }
    }

    private Intent sendMMS(File file, String text) {
        Intent sendMSGIntent = new Intent(Intent.ACTION_SEND);
        sendMSGIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendMSGIntent.putExtra("sms_body", text);
        // 添加照片等附件
        sendMSGIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));// 
        // dirPath为一个sdcard地址如："/sdcard/Myshares/Myphotos/m.jpg"
        // 设置文件类型
        String str2 = "image/*";
        String str3 = file.getName().trim().toLowerCase();
        if (str3.endsWith("png"))
            str2 = "image/png";
        else if ((str3.endsWith("jpg")) || (str3.endsWith("jpeg")))
            str2 = "image/jpeg";
        else if (str3.endsWith("gif"))
            str2 = "image/gif";
        else
            str2 = "*/*";
        sendMSGIntent.setType(str2);
        return sendMSGIntent;
    }

    /**
     * 点击空白区域，弹出框是否消失；默认不消失
     *
     * @param cancel
     */
    public void setDialogCanceledOnTouchOutside(boolean cancel) {
        this.cancel = cancel;
    }

    public void setWechat_app_id(String wechat_app_id) {
        this.wechat_app_id = wechat_app_id;
    }

    public void setYixin_app_id(String yixin_app_id) {
        this.yixin_app_id = yixin_app_id;
    }

}
