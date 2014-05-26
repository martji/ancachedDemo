package com.ancached.prefetching;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.example.struct.FeedBack;
import com.example.struct.Item;

public class Prefetch {
	// params
	static String deviceId = "";
	static String url = "";
	static String site = "";
	static String topic = "";
	static String description = "";
	static int launchTag = 0;
	static int pageType = 0;
	static long time = 0;
	static FeedBack fb = new FeedBack();
	static HashMap<String,String> fetchedMap=new HashMap<String,String>();//map for the fetched result
	
	public static HashMap<String, String> getFetchedMap() {
		return fetchedMap;
	}

	public void setFetchedMap(HashMap<String, String> fetchedMap) {
		Prefetch.fetchedMap = fetchedMap;
	}

	public static void getNextUrls(String site, String topic, String title,
			 ArrayList<Item> items){
		Prefetch.site = site;
		Prefetch.topic = topic;
		Prefetch.description = title;
		Prefetch.getFeedBack(items);
	}
	
	private static void getFeedBack(ArrayList<Item> items) {
		// TODO Auto-generated method stub
		String result = "";
		URL urlCon;
		//encode Chinese with URLEncoder
		try {
			Prefetch.time=System.currentTimeMillis();
			String itemstr = "";
			for (int i = 0; i < items.size(); i++){
				itemstr += items.get(i).getUrl() + " " + items.get(i).getValue();
				itemstr += i != items.size() - 1?"\n":"";
			}
			String EncodedURL=
					"http://112.124.46.148:5001/axis2/services/prediction/slruFromPhone?deviceId="
							+ deviceId + "&&url=" + url 
							+ "&&items=" + URLEncoder.encode(itemstr, "UTF-8")
							+ "&&site=" + URLEncoder.encode(site, "UTF-8")
							+ "&&topic=" + topic 
							+ "&&description=" + URLEncoder.encode(description, "UTF-8") 
							+ "&&launchTag=" + launchTag
							+ "&&pageType=" + pageType + "&&time=" + time;
			urlCon = new URL(EncodedURL);
			HttpURLConnection urlConnection=(HttpURLConnection)urlCon.openConnection();
			urlConnection.setRequestProperty("contentType", "UTF-8");
			BufferedReader bin = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
			String temp = "";
			while ((temp = bin.readLine()) != null) {
				result += temp;
			}
			Document doc =Jsoup.parse(result);
			Element ele=doc.body();
			Elements eles=ele.children();
			Element resultNode=eles.first();
			result=resultNode.text();
			Prefetch.launchTag = 0;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(result!="")
			Prefetch.fb = JSON.parseObject(result,FeedBack.class);
	}

	public static void getFeedBack() {
		String result = "";
		URL urlCon;
		//encode Chinese with URLEncoder
		try {
			Prefetch.time=System.currentTimeMillis();
			String EncodedURL=
					"http://112.124.46.148:5001/axis2/services/prediction/slru?deviceId="
							+ deviceId + "&&url=" + url + "&&site=" + URLEncoder.encode(site, "UTF-8")
							+ "&&topic=" + topic + "&&description="
							+ URLEncoder.encode(description, "UTF-8") + "&&launchTag=" + launchTag
							+ "&&pageType=" + pageType + "&&time=" + time;
			urlCon = new URL(EncodedURL);
			HttpURLConnection urlConnection=(HttpURLConnection)urlCon.openConnection();
			urlConnection.setRequestProperty("contentType", "UTF-8");
			BufferedReader bin = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
			String temp = "";
			while ((temp = bin.readLine()) != null) {
				result += temp;
			}
			Document doc =Jsoup.parse(result);
			Element ele=doc.body();
			Elements eles=ele.children();
			Element resultNode=eles.first();
			result=resultNode.text();
			Prefetch.launchTag = 0;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(result!="")
			Prefetch.fb = JSON.parseObject(result,FeedBack.class);
	}
	
	public String getDeviceId() {
		return deviceId;
	}

	public static void setDeviceId(String deviceId) {
		Prefetch.deviceId = deviceId;
	}

	public static String getUrl() {
		return url;
	}

	public static void setUrl(String url) {
		Prefetch.url = url;
	}

	public String getSite() {
		return site;
	}

	public static void setSite(String site) {
		Prefetch.site = site;
	}

	public String getTopic() {
		return topic;
	}

	public static void setTopic(String topic) {
		Log.i("topic", topic);
		Prefetch.topic = topic;
	}

	public String getDescription() {
		return description;
	}

	public static void setDescription(String description) {
		Prefetch.description = description;
	}

	public int getLaunchTag() {
		return launchTag;
	}

	public static void setLaunchTag(int launchTag) {
		Prefetch.launchTag = launchTag;
	}

	public int getPageType() {
		return pageType;
	}

	public static void setPageType(int pageType) {
		Prefetch.pageType = pageType;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		Prefetch.time = time;
	}

	public static FeedBack getFb() {
		return fb;
	}

	public void setFb(FeedBack fb) {
		Prefetch.fb = fb;
	}
}
