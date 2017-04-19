package com.ztesoft.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnShowListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ztesoft.R;
import com.ztesoft.ui.widget.PromptDialogBuilder;

/**
 * 文件名称 : PromptUtils
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 用户交互提示
 * <p>
 * 创建时间 : 2017/3/23 16:25
 * <p>
 */
public enum PromptUtils {

    instance;

    /**
     * toast
     */
    public void displayToastId(Context context, boolean isLong, int msgId) {
        if (isLong) {
            Toast.makeText(context, msgId, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, msgId, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * toast
     */
    public void displayToastString(Context context, boolean isLong, String msg) {
        if (isLong) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 加载弹出框
     */
    public Dialog initLoadingDialog(Context context, String loadingString, int loadingId, boolean
            isCancelable) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_prompt_loading, null);
        final ImageView loadingIV = (ImageView) view.findViewById(R.id.loading_img);
        final TextView loadingTV = (TextView) view.findViewById(R.id.loading_tv);

        RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context,
                R.anim.rotate_refresh_drawable_default);
        // 开始动画
        loadingIV.setAnimation(rotateAnimation);

        if (!TextUtils.isEmpty(loadingString)) {
            loadingTV.setText(loadingString);
        } else {
            loadingTV.setText(loadingId);
        }

        PromptDialogBuilder builder = new PromptDialogBuilder(context);
        builder.setView(view);
        builder.setDialogStyle(R.style.prompt_loading_style);
        builder.setIsShowTitle(false);
        builder.setIsHasBackground(false);
        builder.setIsCancelable(isCancelable);
        builder.setIsCancelableOnTouchOutside(true);
        builder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // 取消事件
            }
        });
        builder.setOnShowListener(new OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                if (null != loadingIV) {
                }
            }
        });

        builder.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                if (null != loadingIV) {
                }
            }
        });

        Dialog loadingDialog = builder.create();

        return loadingDialog;
    }

    /**
     * 一个按钮的提示框
     */
    public Dialog initOneButtonDialog(Context context, int titleId, String msg, int confirmId,
                                      OnClickListener confirmListener) {
        PromptDialogBuilder builder = new PromptDialogBuilder(context);
        builder.setTitle(titleId);
        builder.setMessage(msg);
        builder.setConfirmListener(confirmId, confirmListener);

        Dialog dialog = builder.create();

        return dialog;
    }

    /**
     * 两个按钮的提示框
     */
    public Dialog initTwoButtonDialog(Context context, int titleId, int msgId, int confirmId, int
            cancelId, OnClickListener confirmListener, OnClickListener cancelListener) {
        PromptDialogBuilder builder = new PromptDialogBuilder(context);
        builder.setTitle(titleId);
        builder.setMessage(msgId);
        builder.setConfirmListener(confirmId, confirmListener);
        builder.setCancelListener(cancelId, cancelListener);

        Dialog dialog = builder.create();

        return dialog;
    }

    /**
     * 两个按钮的提示框
     */
    public Dialog initTwoButtonDialog(Context context, int titleId, String msg, int confirmId,
                                      int cancelId, OnClickListener confirmListener,
                                      OnClickListener cancelListener) {
        PromptDialogBuilder builder = new PromptDialogBuilder(context);
        builder.setTitle(titleId);
        builder.setMessage(msg);
        builder.setConfirmListener(confirmId, confirmListener);
        builder.setCancelListener(cancelId, cancelListener);

        Dialog dialog = builder.create();

        return dialog;
    }
}