package com.example.model;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class CacheHelper {
	
	public static List<String> cachedList = new ArrayList<String>();
	/**
	 * @return : ≈–∂œurl «∑Ò“—±ªª∫¥Ê
	 */
	public static String getUrl(String url) {
		// TODO Auto-generated method stub
		if (url.contains("sina") && url.contains("&clicktime")){
			url = url.substring(0, url.indexOf("&clicktime"));
		}
		else if (url.contains("qq")){
			url = url.replaceAll("sid=[^&]*&*", "");
		}
		Log.i("url", url);
		return url;
	}
	public static boolean checkUrl(String url) {
		// TODO Auto-generated method stub
		if (cachedList.contains(url)){
			return true;
		}
		return false;
	}

}
