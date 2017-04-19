package com.ztesoft.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.ztesoft.R;

/**
 * 文件名称 : TextLengthLimitWatcher
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 检测输入文字长度的工具类
 * <p>
 * 创建时间 : 2017/3/24 10:24
 * <p>
 */
public class TextLengthLimitWatcher implements TextWatcher {

    /**
     * 最大长度
     */
    private int mLength;

    /**
     * 所属上下文
     */
    private Context mContext;

    /**
     * 加入当输入框内容发生变化的时候做一些定制操作
     */
    private CustomerTextWatcher mCustomerTextWatcher;

    public TextLengthLimitWatcher(int length, Context context) {
        this.mLength = length;
        this.mContext = context;
    }

    public void setTextWatcher(CustomerTextWatcher watcher) {
        this.mCustomerTextWatcher = watcher;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(s) && s.length() >= mLength) {
            PromptUtils.instance.displayToastString(mContext, true, mContext.getResources()
                    .getString(R.string.text_length_limit, String.valueOf(mLength)));
        }

        if (null != mCustomerTextWatcher) {
            mCustomerTextWatcher.customeronTextChanged(s, mLength);
        }
    }

    /**
     * 文件名称 : CustomerTextWatcher
     * <p>
     * 作者信息 : xusheng
     * <p>
     * 文件描述 : CustomerTextWatcher - 当输入框发生变化的时候做一些操作
     * <p>
     * 创建时间 : 2013-11-25 下午3:31:18
     * <p>
     */
    public interface CustomerTextWatcher {
        public void customeronTextChanged(CharSequence s, int length);
    }
}
