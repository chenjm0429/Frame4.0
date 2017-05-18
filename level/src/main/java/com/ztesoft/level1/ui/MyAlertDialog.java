package com.ztesoft.level1.ui;

import com.ztesoft.level1.Level1Util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyAlertDialog extends Dialog {

    Context context;
    AlertDialogLayout alayout;

    public MyAlertDialog(Context context) {
        this(context, android.R.style.Theme_Panel);
    }

    public MyAlertDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		this.setCanceledOnTouchOutside(true);

        alayout = new AlertDialogLayout(context);
        this.setContentView(alayout);
    }

    public MyAlertDialog setPositiveButton(String text, android.view.View.OnClickListener
            listener) {
        RelativeLayout buttonView = alayout.getButtonView();
        buttonView.setVisibility(View.VISIBLE);
        TextView b = (TextView) buttonView.getChildAt(0);
        b.setVisibility(View.VISIBLE);
        b.setText(text);
        b.setOnClickListener(listener);

        return this;
    }

    public MyAlertDialog setPositiveButton(int textId, android.view.View.OnClickListener listener) {
        return setPositiveButton(context.getString(textId), listener);
    }

    public MyAlertDialog setNegativeButton(String text, android.view.View.OnClickListener
            listener) {
        RelativeLayout buttonView = alayout.getButtonView();
        buttonView.setVisibility(View.VISIBLE);
        TextView b = (TextView) buttonView.getChildAt(1);
        b.setVisibility(View.VISIBLE);
        b.setText(text);
        b.setOnClickListener(listener);

        return this;
    }

    public MyAlertDialog setNegativeButton(int textId, android.view.View.OnClickListener listener) {
        return setNegativeButton(context.getString(textId), listener);
    }

    public MyAlertDialog setIcon(int iconId) {
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

    public void setMessage(String message) {
        alayout.getMsgView().setVisibility(View.VISIBLE);
        alayout.getMsgView().setText(message);
    }

    public void setMessage(int messageId) {
        setMessage(context.getString(messageId));
    }

    public MyAlertDialog setView(View view) {
        alayout.getBodyView().setVisibility(View.VISIBLE);
        alayout.getBodyView().addView(view, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout
                .LayoutParams.FILL_PARENT);
        return this;
    }

    public MyAlertDialog setItems(String[] strs, android.view.View.OnClickListener listener) {
        LinearLayout view = new LinearLayout(context);
        view.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < strs.length; i++) {
            TextView tv = new TextView(context);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(Color.BLACK);
            tv.setBackgroundColor(Color.WHITE);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            tv.setText(strs[i]);
            tv.setTag(strs[i]);
            tv.setOnClickListener(listener);
            view.addView(tv, android.view.ViewGroup.LayoutParams.FILL_PARENT, Level1Util.dip2px
                    (context, 40));
        }

        alayout.getBodyView().setVisibility(View.VISIBLE);
        alayout.getBodyView().addView(view);
        return this;
    }

    public void setBackgroudColor(int color) {
        alayout.setBackgroundColor(color);
    }

    public void setBackgroundDrawable(Drawable background) {
        alayout.setBackgroundDrawable(background);
    }

    class AlertDialogLayout extends LinearLayout {
        private ImageView iconView;
        private TextView titleView;
        private TextView msgView;
        private LinearLayout bodyView;
        private RelativeLayout buttonView;

        public AlertDialogLayout(Context context) {
            super(context);
            int pad = Level1Util.dip2px(context, 8);
            int padmin = Level1Util.dip2px(context, 1);
            LinearLayout alLayout = new LinearLayout(context);
            alLayout.setGravity(Gravity.CENTER);
            alLayout.setBackgroundColor(Color.parseColor("#ebe0e0"));
            alLayout.setPadding(padmin, padmin, padmin, padmin);
            alLayout.setOrientation(LinearLayout.VERTICAL);
            this.addView(alLayout);
            this.setPadding(padmin, padmin, padmin, padmin);
            LinearLayout titleLayout = new LinearLayout(context);
            titleLayout.setGravity(Gravity.CENTER);

            titleLayout.setBackgroundColor(Color.parseColor("#0091d5"));

            iconView = new ImageView(context);
            iconView.setVisibility(View.GONE);
            iconView.setAdjustViewBounds(true);
            iconView.setScaleType(ScaleType.FIT_CENTER);
            iconView.setMaxWidth(100);
            titleLayout.addView(iconView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            titleView = new TextView(context);
            titleView.setTextColor(Color.WHITE);
            titleView.setVisibility(View.GONE);
            titleView.setGravity(Gravity.LEFT);
            titleView.setPadding(pad, pad, 0, pad);
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            titleLayout.addView(titleView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            alLayout.addView(titleLayout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            msgView = new TextView(context);
            msgView.setTextColor(Color.BLACK);
            msgView.setVisibility(View.GONE);
            msgView.setGravity(Gravity.LEFT);
            msgView.setPadding(pad, pad, 0, pad);
//			msgView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            msgView.setBackgroundColor(Color.WHITE);
            alLayout.addView(msgView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            bodyView = new LinearLayout(context);
            bodyView.setBackgroundColor(Color.WHITE);
            bodyView.setVisibility(View.GONE);
            alLayout.addView(bodyView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

//			View v = new View(context);
//			v.setBackgroundColor(Color.parseColor("#0091d5"));
//			alLayout.addView(v,LayoutParams.FILL_PARENT,padmin);

            buttonView = new RelativeLayout(context);
            buttonView.setBackgroundColor(Color.WHITE);
//			if ("qh_userpwd".equals(MainApplication.LOGINTYPE)){
//				buttonView.setBackgroundColor(ThemeColorBean.button_font);
//			}
            TextView posButton = new TextView(context);
            posButton.setPadding(5 * pad, pad, 5 * pad, pad);

            posButton.setBackgroundColor(Color.parseColor("#0091d5"));

            posButton.setTextColor(Color.WHITE);
            posButton.setVisibility(View.GONE);
            TextView negButton = new TextView(context);
            negButton.setPadding(5 * pad, pad, 5 * pad, pad);

            negButton.setBackgroundColor(Color.parseColor("#0091d5"));
            negButton.setTextColor(Color.WHITE);
            negButton.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(new LayoutParams
                    (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//			if ("qh_userpwd".equals(MainApplication.LOGINTYPE)){
//				posButton.setLayoutParams(params);
//			}else{
            negButton.setLayoutParams(params);
//			}
            buttonView.addView(posButton);
            buttonView.addView(negButton);
            buttonView.setPadding(pad, pad, pad, pad);

            buttonView.setVisibility(View.GONE);
            buttonView.setGravity(Gravity.CENTER);
            alLayout.addView(buttonView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

        }

        public TextView getTitleView() {
            return titleView;
        }

        public void setTitleView(TextView titleView) {
            this.titleView = titleView;
        }

        public LinearLayout getBodyView() {
            return bodyView;
        }

        public void setBodyView(LinearLayout bodyView) {
            this.bodyView = bodyView;
        }

        public TextView getMsgView() {
            return msgView;
        }

        public void setMsgView(TextView msgView) {
            this.msgView = msgView;
        }

        public RelativeLayout getButtonView() {
            return buttonView;
        }

        public void setButtonView(RelativeLayout buttonView) {
            this.buttonView = buttonView;
        }

        public ImageView getIconView() {
            return iconView;
        }

        public void setIconView(ImageView iconView) {
            this.iconView = iconView;
        }
    }

}
