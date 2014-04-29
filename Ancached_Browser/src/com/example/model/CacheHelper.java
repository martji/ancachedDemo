package com.example.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import android.os.Environment;
import android.util.Log;

public class CacheHelper {
	
	public static Map<String, String> cachedList = new HashMap<String, String>();
	public static Map<String, String> urlList = new HashMap<String, String>();
	static {
		//cachedList.put("http://sina.cn/", "sina.htm");
		urlList.put("homesites.htm", "http://m.hao123.com");
	}
	public static String path = "file:///" + Environment.getExternalStorageDirectory().getPath() 
			+ "/Ancached_Browser/file/";
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
		if (cachedList.containsKey(url)){
			return true;
		}
		return false;
	}
	public static String getLocalUrl(String url) {
		// TODO Auto-generated method stub
		return path + cachedList.get(url);
	}
	
	public static void getHTML(String address){
		try {
			URL url = new URL(address);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setReadTimeout(5000);
			InputStream inStream= connection.getInputStream();
			
			String localaddress = address.replace("http://", "");
			localaddress = localaddress.replace("?", "");
			localaddress = localaddress.replace("/", "");
			localaddress += ".htm";
			File fileName = new File("sdcard/Ancached_Browser/file/" + localaddress);
			FileOutputStream fout = new FileOutputStream(fileName);
			byte[] buffer = new byte[1024];
			int len=0;
			while((len=inStream.read(buffer))!=-1){
				fout.write(buffer, 0, len);
			}
			inStream.close();
			fout.close();
			cachedList.put(address, localaddress);
			urlList.put(localaddress, address);
			
//			ByteArrayOutputStream data=new ByteArrayOutputStream();
//			byte[] buffer = new byte[1024];
//			int len=0;
//			while((len=inStream.read(buffer))!=-1){
//				data.write(buffer, 0, len);
//			}
//			inStream.close();
//			String context = new String(data.toByteArray(),"utf-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
