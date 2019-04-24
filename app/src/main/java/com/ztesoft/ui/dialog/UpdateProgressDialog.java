package com.ztesoft.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ztesoft.R;
import com.ztesoft.level1.Level1Util;

/**
 * 文件名称 : UpdateProgressDialog
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 更新时的进度条弹出框
 * <p>
 * 创建时间 : 2018/8/27 11:29
 * <p>
 */
public class UpdateProgressDialog extends Dialog {

    private ProgressBar progressBar;
    private TextView mTipText;

    public UpdateProgressDialog(Context context) {
        this(context, R.style.no_bg_dialog_style);
    }

    public UpdateProgressDialog(Context context, int theme) {
        super(context, theme);

        setContentView(R.layout.dialog_update_progress);

        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = Level1Util.getDeviceWidth(context) * 4 / 5; // 宽度
        dialogWindow.setAttributes(lp);

        this.setCanceledOnTouchOutside(false);

        progressBar = findViewById(R.id.progress_bar);
        mTipText = findViewById(R.id.tip_text);
    }

    public void setProgress(int progress) {
        progressBar.setProgress(progress);
        mTipText.setText(progress + "%");
    }
}
