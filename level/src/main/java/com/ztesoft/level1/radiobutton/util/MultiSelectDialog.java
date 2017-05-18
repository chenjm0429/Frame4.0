package com.ztesoft.level1.radiobutton.util;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ztesoft.level1.Level1Util;
import com.ztesoft.level1.R;

/**
 * 文件名称 : DateDialog
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 时间选择器弹出框
 * <p>
 * 创建时间 : 2017/5/5 14:36
 * <p>
 */
public class MultiSelectDialog extends Dialog {

    private Context context;

    private TextView mTitleText;
    private String title;

    private LinearLayout mContainerLayout;

    private TextView mCancelText, mConfirmText;

    public MultiSelectDialog(Context context) {
        this(context, R.style.prompt_style);
    }

    public MultiSelectDialog(Context context, int theme) {
        super(context, theme);

        this.context = context;

        setContentView(R.layout.dialog_multi_select);

        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) (Level1Util.getDeviceWidth(context) * 0.9);
        window.setAttributes(lp);

        initParam();
    }

    private void initParam() {
        mTitleText = (TextView) findViewById(R.id.title);

        mContainerLayout = (LinearLayout) findViewById(R.id.container_layout);

        mCancelText = (TextView) findViewById(R.id.cancel_text);
        mConfirmText = (TextView) findViewById(R.id.confiem_text);
    }

    public void setView(View view) {
        mContainerLayout.removeAllViews();
        mContainerLayout.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout
                .LayoutParams.MATCH_PARENT);
    }

    public void setPositiveButton(View.OnClickListener listener) {
        mConfirmText.setOnClickListener(listener);
    }

    public void setNegativeButton(View.OnClickListener listener) {
        mCancelText.setOnClickListener(listener);
    }

    public void setTitle(String title) {
        this.title = title;

        mTitleText.setText(title);
    }
}

