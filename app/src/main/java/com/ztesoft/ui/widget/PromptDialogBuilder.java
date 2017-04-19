package com.ztesoft.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnShowListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ztesoft.R;

/**
 * 文件名称 : PromptDialogBuilder
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 整个应用弹框构造器
 * <p>
 * 创建时间 : 2017/3/23 16:26
 * <p>
 */
public class PromptDialogBuilder {
    /**
     * 上下文
     */
    private Context mContext;

    /**
     * 整个布局
     */
    private View mView;

    /**
     * 头部布局
     */
    private RelativeLayout mTitleLayout;

    /**
     * 头部信息
     */
    private TextView mTitleTv;

    /**
     * 关闭按钮
     */
    private ImageView mCloseImage;

    /**
     * 按钮布局
     */
    private LinearLayout mButtonLayout;
    ;

    /**
     * 提示信息
     */
    private TextView mMessageView;

    /**
     * 按钮组
     */
    private ViewGroup mButtonGroup;

    /**
     * 确定按钮
     */
    private Button mConfirmButton;

    /**
     * 取消按钮
     */
    private Button mCancelButton;

    /**
     * 布局
     */
    private LinearLayout mViewContainer;

    /**
     * 确定按钮响应
     */
    private OnClickListener mConfirmClickListener;

    /**
     * 取消按钮响应
     */
    private OnClickListener mCancelClickListener;

    /**
     * 当弹框显示的时候，点击返回键盘，执行响应
     */
    private OnCancelListener mOnCancelListener;

    /**
     * 显示弹框事件响应
     */
    private OnShowListener mOnShowListener;

    /**
     * 是否显示确定按钮
     */
    private boolean mShowConfirmBtn = false;

    /**
     * 是否显示取消按钮
     */
    private boolean mShowCancelBtn = false;

    /**
     * 是否显示消息布局
     */
    private boolean mShowMessageView;

    /**
     * 是否可以取消弹框
     */
    private boolean mIsCancelable = true;

    /**
     * 是否点击弹框外部可以取消
     */
    private boolean mIsCancelableOnTouchOutside = false;

    /**
     * 是否显示头部
     */
    private boolean mIsShowTitle = true;

    /**
     * 是否设置背景
     */
    private boolean mIsHasBackground = true;

    /**
     * 弹框风格定义
     */
    private int mDialogStyle = R.style.prompt_style;

    /**
     * 构造方法
     */
    public PromptDialogBuilder(Context context) {
        mContext = context;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mView = inflater.inflate(R.layout.layout_prompt, null);

        mTitleLayout = (RelativeLayout) mView.findViewById(R.id.prompt_title_layout);
        mTitleTv = (TextView) mView.findViewById(R.id.prompt_title);
        mCloseImage = (ImageView) mView.findViewById(R.id.prompt_close);

        mButtonLayout = (LinearLayout) mView.findViewById(R.id.button_layout);

        mViewContainer = (LinearLayout) mView.findViewById(R.id.view_container);
        mMessageView = (TextView) mView.findViewById(R.id.prompt_message);

        mButtonGroup = (ViewGroup) mView.findViewById(R.id.prompt_button_group);
        mConfirmButton = (Button) mView.findViewById(R.id.confirm);
        mCancelButton = (Button) mView.findViewById(R.id.cancel);
    }

    /**
     * 设置是否设置背景
     */
    public PromptDialogBuilder setIsHasBackground(boolean isHasBackground) {
        mIsHasBackground = isHasBackground;
        return this;
    }

    /**
     * 设置标题
     */
    public PromptDialogBuilder setTitle(int id) {
        mTitleTv.setText(id);
        return this;
    }

    /**
     * 设置标题
     */
    public PromptDialogBuilder setTitle(String title) {
        mTitleTv.setText(title);
        return this;
    }

    /**
     * 是否显示标题
     */
    public PromptDialogBuilder setIsShowTitle(boolean isShowTitle) {
        mIsShowTitle = isShowTitle;
        return this;
    }

    /**
     * 设置标题显示位置
     */
    public PromptDialogBuilder setTitleLocation(int titlePsition) {
        mTitleTv.setGravity(titlePsition);
        return this;
    }

    /**
     * 设置消息
     */
    public PromptDialogBuilder setMessage(int messageId) {
        mMessageView.setText(messageId);
        mShowMessageView = true;
        return this;
    }

    /**
     * 设置消息
     */
    public PromptDialogBuilder setMessage(String message) {
        mMessageView.setText(message);
        mShowMessageView = true;
        return this;
    }

    /**
     * 设置view
     */
    public PromptDialogBuilder setView(View view) {
        mViewContainer.removeAllViews();
        if (null != view) {
            mViewContainer.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
        }

        return this;
    }

    /**
     * 设置响应事件
     */
    public PromptDialogBuilder setConfirmListener(int textId, final OnClickListener listener) {
        mConfirmButton.setText(textId);
        mConfirmClickListener = listener;
        mShowConfirmBtn = true;
        return this;
    }

    /**
     * 设置响应事件
     */
    public PromptDialogBuilder setCancelListener(int textId, final OnClickListener listener) {
        mCancelButton.setText(textId);
        mCancelClickListener = listener;
        mShowCancelBtn = true;
        return this;
    }

    /**
     * 设置响应事件
     */
    public PromptDialogBuilder setOnCancelListener(final OnCancelListener cancelListener) {
        this.mOnCancelListener = cancelListener;
        return this;
    }

    public PromptDialogBuilder setOnShowListener(final OnShowListener showListener) {
        this.mOnShowListener = showListener;
        return this;
    }

    public PromptDialogBuilder setIsCancelable(boolean isCancelable) {
        this.mIsCancelable = isCancelable;
        return this;
    }

    public PromptDialogBuilder setIsCancelableOnTouchOutside(boolean isCancelableOnTouchOutside) {
        this.mIsCancelableOnTouchOutside = isCancelableOnTouchOutside;
        return this;
    }

    public PromptDialogBuilder setDialogStyle(int style) {
        mDialogStyle = style;
        return this;
    }

    public Dialog create() {
        final Dialog dialog = new Dialog(mContext, mDialogStyle);
        dialog.setContentView(mView);

        if (mIsHasBackground) {
            mButtonLayout.setBackgroundResource(R.drawable.dialog_bg);
        }

        mCloseImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        if (mShowMessageView) {
            mMessageView.setVisibility(View.VISIBLE);
        } else {
            mMessageView.setVisibility(View.GONE);
        }

        boolean showButtonGroup = false;
        if (mShowConfirmBtn) {
            mConfirmButton.setVisibility(View.VISIBLE);
            mConfirmButton.setOnClickListener(dismissDialog(dialog, mConfirmClickListener));
            showButtonGroup = true;
        } else {
            mConfirmButton.setVisibility(View.GONE);
        }

        if (mShowCancelBtn) {
            mCancelButton.setVisibility(View.VISIBLE);
            mCancelButton.setOnClickListener(dismissDialog(dialog, mCancelClickListener));
            showButtonGroup = true;
        } else {
            mCancelButton.setVisibility(View.GONE);
        }

        if (showButtonGroup) {
            mButtonGroup.setVisibility(View.VISIBLE);
        } else {
            mButtonGroup.setVisibility(View.GONE);
        }

        if (mIsShowTitle) {
            mTitleLayout.setVisibility(View.VISIBLE);
        } else {
            mTitleLayout.setVisibility(View.GONE);
        }

        dialog.setCancelable(mIsCancelable);
        dialog.setCanceledOnTouchOutside(mIsCancelableOnTouchOutside);
        dialog.setOnCancelListener(mOnCancelListener);
        dialog.setOnShowListener(mOnShowListener);

        return dialog;
    }

    public Dialog show() {
        Dialog dialog = create();
        dialog.show();

        return dialog;
    }

    private OnClickListener dismissDialog(final Dialog dialog, final OnClickListener listener) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (null != listener) {
                    listener.onClick(v);
                }
            }
        };
    }
}
