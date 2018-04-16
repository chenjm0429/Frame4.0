package com.ztesoft.level1.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * 文件名称 : NotificationUtil
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 通知服务
 * <p>
 * 创建时间 : 2017/5/22 16:56
 * <p>
 */
public class NotificationUtil {

    private Context context;
    private int pic = android.R.drawable.sym_action_chat;//通知栏图片
    private String noticeTitle = "";//通知栏通知标题
    private String title = "";//通知栏下拉后，展示标题
    private String desc = "";//通知栏下拉后，展示内容
    private Class<?> c = null;//通知栏下拉后，点击跳转
    private Intent intent = new Intent();//通知栏下拉后，点击跳转
    private int threadId = 1;//通知栏进程号，相同进程号的通知会互相覆盖
    private NotificationManager manager;

    public NotificationUtil(Context context) {
        this.context = context;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public NotificationUtil(Context context, int picId, int threadId) {
        this.context = context;
        this.pic = picId;
        this.threadId = threadId;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * 通知启用和更新方法
     */
    public void start() {
        Notification notification = new Notification(pic, noticeTitle, System.currentTimeMillis());
        if (intent == null)
            intent = new Intent();
        if (c != null)
            intent = new Intent(context, c);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent
                .FLAG_UPDATE_CURRENT);
//        notification.setLatestEventInfo(context.getApplicationContext(), title, desc, 
//                contentIntent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//		notification.number=number;//通栏图标上显示数字(必须大于0)，4.0版本无效果
//		notification.defaults |= Notification.DEFAULT_SOUND;
        manager.notify(threadId, notification);
    }

    /**
     * 通知取消方法，一般用不到，点击通知栏后，通知就自动消失了
     */
    public void cancel() {
        manager.cancel(threadId);
    }

    /**
     * 设置通知图标
     *
     * @param pic 通知图标图片ID
     */
    public void setPic(int pic) {
        this.pic = pic;
    }

    /**
     * 设置在通知栏显示的提示
     *
     * @param noticeTitle
     */
    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    /**
     * 设置通知栏下拉后的展现标题
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 设置通知栏下拉后的展现内容
     *
     * @param desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * 设置点击通知后的跳转事件
     *
     * @param c new Intent(context, c)
     */
    public void setC(Class<?> c) {
        this.c = c;
    }

    /**
     * 设置点击通知后的跳转事件
     *
     * @param intent
     */
    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    /**
     * 设置通知编码，同一编码的通知会互相覆盖
     *
     * @param threadId
     */
    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }
}
