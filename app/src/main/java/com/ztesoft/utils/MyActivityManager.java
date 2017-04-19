package com.ztesoft.utils;

import android.app.Activity;

import java.util.Stack;

/**
 * 文件名称 : MyActivityManager
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : activity管理类
 * <p>
 * 创建时间 : 2017/3/23 14:54
 * <p>
 */
public class MyActivityManager {

    private static Stack<Activity> activityStack;
    private static MyActivityManager instance;

    private MyActivityManager() {
    }

    public static MyActivityManager getScreenManager() {
        if (instance == null) {
            instance = new MyActivityManager();
        }
        return instance;
    }

    // 退出栈顶Activity
    public void popActivity(Activity activity) {
        if (activity != null) {
            // 在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作
            activity.finish();
            activityStack.remove(activity);
            activity = null;
        }
    }

    // 获得当前栈顶Activity
    public Activity currentActivity() {
        Activity activity = null;
        if (!activityStack.empty())
            activity = activityStack.lastElement();
        return activity;
    }

    // 将当前Activity推入栈中
    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    // 退出栈中所有Activity
    public void popAllActivityExceptOne(Class<?> cls) {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            if (activity.getClass().equals(cls)) {
                break;
            }
            popActivity(activity);
        }
    }

    // 退出栈中所有Activity
    public void popAllActivity() {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            popActivity(activity);
        }
    }

    // 获取堆栈
    public Stack<Activity> getStack() {

        return activityStack;
    }
}
