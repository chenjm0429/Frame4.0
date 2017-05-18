package com.ztesoft.level1.text;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.widget.TextView;

import com.ztesoft.level1.util.NumericalUtil;

import java.math.BigDecimal;

/**
 * 文件名称 : MagicTextView
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 翻牌数字效果
 * <p>
 * 创建时间 : 2017/5/2 10:20
 * <p>
 */
public class MagicTextView extends TextView {
    private double mValue;//目标值
    // 当前显示的值
    private double mCurValue;
    private double mRate;
    private int delayMillis = 50;

    private boolean minusFlag = false;//是否有负号
    private boolean percentFlag = false;//是否有百分号
    private boolean thousandFlag = false;//是否有千分号
    private int nummm = 0;//小数位数

    public MagicTextView(Context context) {
        super(context);
        this.setGravity(Gravity.RIGHT);
    }

    /**
     * 设置要显示的数值
     *
     * @param value
     */
    public void setValue(String value) {
        //分析value格式
        if (value.startsWith("-")) {//负数
            value = value.substring(1);
            minusFlag = true;
        }
        if (value.endsWith("%")) {//百分号
            value = value.substring(0, value.length() - 1);
            percentFlag = true;
        }
        if (value.contains(",")) {//千分位
            value = value.replaceAll(",", "");
            thousandFlag = true;
        }
        if (value.contains(".")) {//小数
            nummm = value.length() - value.indexOf(".") - 1;
        }
        mCurValue = 0;
        mValue = Double.parseDouble(value);
        mRate = Math.abs((mValue / 20.00));
        Message msg = mHandler.obtainMessage();
        msg.what = 1;
        mHandler.sendMessage(msg);
    }

    /**
     * 调整显示格式
     *
     * @param mValue 显示值
     * @return
     */
    private String getFormat(String mValue) {
        String value = mValue;
        if (thousandFlag) {
            value = NumericalUtil.getInstance().setThousands(value);
        }
        if (minusFlag) {
            value = "-" + value;
        }
        if (percentFlag) {
            value += "%";
        }

        return value;
    }

    public void setMRate(double mRate) {
        this.mRate = mRate;
    }

    public void setDelayMillis(int delayMillis) {
        this.delayMillis = delayMillis;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    if (mCurValue < Math.abs(mValue)) {
                        BigDecimal b = new BigDecimal(mCurValue);
                        String tt = b.setScale(nummm, BigDecimal.ROUND_HALF_UP)
                                .stripTrailingZeros().toPlainString();//防止科学计数法
                        mCurValue = b.setScale(nummm, BigDecimal.ROUND_HALF_UP)
                                .stripTrailingZeros().doubleValue();
                        setText(getFormat(tt));
                        mCurValue += mRate;
                        mHandler.sendEmptyMessageDelayed(1, delayMillis);
                    } else {
                        BigDecimal b = new BigDecimal(mValue);
                        String tt = b.setScale(nummm, BigDecimal.ROUND_HALF_UP)
                                .stripTrailingZeros().toPlainString();
                        setText(getFormat(tt));
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
