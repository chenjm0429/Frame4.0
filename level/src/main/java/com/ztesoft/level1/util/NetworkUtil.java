package com.ztesoft.level1.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

	private NetworkInfo info;
	public final static int NET_MOBILE = 1;
	public final static int NET_WIFI = -1;
	public final static int NET_OTHER = 9;

	public NetworkUtil(Context ctx) {
		ConnectivityManager cm = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
//		cm.setNetworkPreference(ConnectivityManager.TYPE_WIFI); // 优先使用wifi网络,2.x版本报错
		info = cm.getActiveNetworkInfo();
	}

	/**
	 * 查看网络链接状态
	 * 
	 * @return		
	 */
	public boolean isNetworkAvailable() {

		if(info==null)
			return false;
		return info.isAvailable();
	}

	public HttpURLConnection getURLConnection(String url) throws MalformedURLException, IOException {
		String proxyHost = android.net.Proxy.getDefaultHost();  
		if (!info.getTypeName().equals("WIFI")&&proxyHost!=null) {
			java.net.Proxy p = new java.net.Proxy(java.net.Proxy.Type.HTTP,  
                    new InetSocketAddress(android.net.Proxy.getDefaultHost(),  
                            android.net.Proxy.getDefaultPort()));  
            return (HttpURLConnection) new URL(url).openConnection(p);  
		}
		return (HttpURLConnection) new URL(url).openConnection();
	}
	
	 /**
     * 获取当前网络类型
     * @return
     */
    public int GetCurrentNetType() {
        int net_type = info.getType();
        
        if (net_type == ConnectivityManager.TYPE_MOBILE) {
            return NET_MOBILE;
        } else if (net_type == ConnectivityManager.TYPE_WIFI) {
            return NET_WIFI;
        }
        
        return NET_OTHER;
    }

}
