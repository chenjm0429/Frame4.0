package com.ztesoft.level1.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ztesoft.level1.R;

public class MyProgressDialog extends Dialog {

    Context context;
    AlertDialogLayout alayout;

    public MyProgressDialog(Context context) {
        this(context, android.R.style.Theme_Panel);
    }

    public MyProgressDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setCancelable(false);

        alayout = new AlertDialogLayout(context);
        this.setContentView(alayout);
    }

    public MyProgressDialog setIcon(int iconId) {
        alayout.getIconView().setVisibility(View.VISIBLE);
        alayout.getIconView().setImageResource(iconId);
        return this;
    }

    public void setTitle(String pTitle) {
        alayout.getTitleView().setVisibility(View.VISIBLE);
        alayout.getTitleView().setText(pTitle);
    }

    public void setTitle(int titleId) {
        setTitle(context.getString(titleId));
    }

    class AlertDialogLayout extends LinearLayout {
        private ImageView iconView;
        private TextView titleView;

        public AlertDialogLayout(Context context) {
            super(context);
            this.setGravity(Gravity.CENTER);
            this.setBackgroundColor(Color.parseColor("#a2000000"));
            this.setPadding(15, 15, 15, 15);

            RelativeLayout titleLayout = new RelativeLayout(context);

            iconView = new ImageView(context);
            iconView.setAdjustViewBounds(true);
            iconView.setScaleType(ScaleType.FIT_CENTER);
            iconView.setImageResource(R.drawable.load);
            iconView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate));

            titleView = new TextView(context);
            titleView.setGravity(Gravity.CENTER);
//			titleView.setBackgroundColor(Color.YELLOW);
            titleView.setTextColor(Color.WHITE);
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(new LayoutParams
                    (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            titleView.setLayoutParams(params);

            titleLayout.addView(iconView);
            titleLayout.addView(titleView);
            this.addView(titleLayout);
        }

        public TextView getTitleView() {
            return titleView;
        }

        public ImageView getIconView() {
            return iconView;
        }
    }
}
