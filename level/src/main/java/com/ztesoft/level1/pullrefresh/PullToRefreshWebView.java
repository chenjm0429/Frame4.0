package com.ztesoft.level1.pullrefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * 文件名称 : PullToRefreshWebView
 * <p>
 * 作者信息 : chenjianming
 * <p>
 * 文件描述 : 封装了WebView的下拉刷新
 * <p>
 * 创建时间 : 2017/3/24 15:01
 * <p>
 */
public class PullToRefreshWebView extends PullToRefreshBase<WebView> {
    /**
     * 构造方法
     *
     * @param context context
     */
    public PullToRefreshWebView(Context context) {
        this(context, null);
    }

    /**
     * 构造方法
     *
     * @param context context
     * @param attrs   attrs
     */
    public PullToRefreshWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造方法
     *
     * @param context  context
     * @param attrs    attrs
     * @param defStyle defStyle
     */
    public PullToRefreshWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * @see PullToRefreshBase#createRefreshableView(android.content.Context, android.util.AttributeSet)
     */
    @Override
    protected WebView createRefreshableView(Context context, AttributeSet attrs) {
        WebView webView = new WebView(context);
        return webView;
    }

    /**
     * @see PullToRefreshBase#isReadyForPullDown()
     */
    @Override
    protected boolean isReadyForPullDown() {
        return mRefreshableView.getScrollY() == 0;
    }

    /**
     * @see PullToRefreshBase#isReadyForPullUp()
     */
    @Override
    protected boolean isReadyForPullUp() {

        double exactContentHeight = Math.floor(mRefreshableView.getContentHeight() *
                mRefreshableView.getScale());

        return mRefreshableView.getScrollY() >= (exactContentHeight - mRefreshableView.getHeight());
    }
}
