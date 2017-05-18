package com.ztesoft.level1.util;

import java.io.File;

import com.ztesoft.level1.Level1Bean;

import android.content.Intent;
import android.net.Uri;

public class OpenFileIntents {
	
	/**
	 * 打开文件intent
	 * @param path		文件路径
	 * @return
	 */
	public static Intent openFile(String filePath){
		Intent myIntent = new Intent(Intent.ACTION_VIEW);
		myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(Level1Bean.SD_ROOTPATH+filePath));
		if(filePath.endsWith(".pdf")){
			myIntent.setDataAndType(uri, "application/pdf");
		}else if(filePath.endsWith(".xls")){
			myIntent.setDataAndType(uri, "application/vnd.ms-excel");
		}else if(filePath.endsWith(".xlsx")){
			myIntent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		}else if(filePath.endsWith(".ppt")||filePath.endsWith(".pps")){
			myIntent.setDataAndType(uri, "application/vnd.ms-powerpoint");
		}else if(filePath.endsWith(".pptx")){
			myIntent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.presentationml.presentation");
		}else if(filePath.endsWith(".doc")){
			myIntent.setDataAndType(uri, "application/msword");
		}else if(filePath.endsWith(".docx")){
			myIntent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		}else if(filePath.endsWith(".apk")){
			myIntent.setDataAndType(uri, "application/vnd.android.package-archive");
		}else if(filePath.endsWith(".png")||filePath.endsWith(".jpg")){
			myIntent.setDataAndType(uri, "image/*");
		}else if(filePath.endsWith(".mp4")||filePath.endsWith(".3gp")||filePath.endsWith(".avi")){
			myIntent.putExtra("oneshot", 0);
			myIntent.putExtra("configchange", 0);
			myIntent.setDataAndType(uri, "video/*");
		}else if(filePath.endsWith(".mp3")||filePath.endsWith(".wav")||filePath.endsWith(".mpga")){
			myIntent.putExtra("oneshot", 0);
			myIntent.putExtra("configchange", 0);
			myIntent.setDataAndType(uri, "audio/*");
		}else{//如果不进行赋值，则运行时会报错，故此处默认赋值为textIntent
			myIntent.setDataAndType(uri, "text/plain");
		}
		return myIntent;
	}
}
