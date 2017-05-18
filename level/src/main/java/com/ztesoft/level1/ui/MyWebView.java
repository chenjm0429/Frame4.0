package com.ztesoft.level1.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebView extends WebView {
    boolean init = true;
    boolean loadFinish = false;

    public boolean isLoadFinish() {
        return loadFinish;
    }

    public MyWebView(Context context) {
        super(context);
        this.setScrollContainer(false);
        this.getSettings().setJavaScriptEnabled(true);
        //html5自适应屏幕标签 
        this.getSettings().setUseWideViewPort(true);
        this.getSettings().setLoadWithOverviewMode(true);
        //
        this.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        this.getSettings().setDefaultTextEncodingName("GBK");
        this.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        this.removeJavascriptInterface("searchBoxJavaBridge_");
        this.removeJavascriptInterface("accessibility");
        this.removeJavascriptInterface("ccessibilityaversal");
        this.setWebChromeClient(new WebChromeClient());
        this.setWebViewClient(new MyWebViewClient());
    }

    class MyWebViewClient extends WebViewClient {
        boolean init = true;

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.removeJavascriptInterface("searchBoxJavaBridge_");
            view.removeJavascriptInterface("accessibility");
            view.removeJavascriptInterface("ccessibilityaversal");
            view.loadUrl(url);
            return true;

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.removeJavascriptInterface("searchBoxJavaBridge_");
            view.removeJavascriptInterface("accessibility");
            view.removeJavascriptInterface("ccessibilityaversal");
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            view.removeJavascriptInterface("searchBoxJavaBridge_");
            view.removeJavascriptInterface("accessibility");
            view.removeJavascriptInterface("ccessibilityaversal");
            super.onPageFinished(view, url);
//			if(init){
//				init=false;
//				view.loadUrl("javascript:re()");   
//			}else{
//				loadFinish=true; 
//			}
        }
    }
}
