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
import com.example.ancached_browser.MainActivity;
import com.example.struct.FeedBack;
import com.example.struct.Item;

public class MyPrefetch {
	String deviceId = "";
	String url = "";
	String site = "";
	String topic = "";
	String description = "";
	int launchTag = 0;
	int pageType = 0;
	long time = 0;
	FeedBack fb = new FeedBack();
	HashMap<String,String> fetchedMap=new HashMap<String,String>();
	
	public MyPrefetch(int pageType) {
		this.launchTag = 0;
		this.deviceId = MainActivity.deviceID;
		this.time = System.currentTimeMillis();
		this.pageType = pageType;
	}

	public void getNextUrls(String site, String topic, String title,
			 ArrayList<Item> items){
		this.site = site;
		this.topic = topic;
		this.description = title;
		getFeedBack(items);
	}
	
	@SuppressWarnings({ })
	public void getFeedBack(ArrayList<Item> items) {
		String result = "";
		URL urlCon;
		try {
			String itemstr = "";
			for (int i = 0; i < items.size(); i++){
				itemstr += items.get(i).getUrl() + " " + items.get(i).getValue().replace("%", "°Ù·ÖºÅ");
				itemstr += i != items.size() - 1?"\n":"";
			}
			String EncodedURL=
					"http://112.124.46.148:5001/axis2/services/prediction/slruFromPhone?"
							+ "deviceId=" + this.deviceId 
							+ "&&url=" + this.url 
							+ "&&items=" + URLEncoder.encode(itemstr, "UTF-8")
							+ "&&site=" + URLEncoder.encode(this.site, "UTF-8")
							+ "&&topic=" + this.topic 
							+ "&&description=" + URLEncoder.encode(this.description, "UTF-8") 
							+ "&&launchTag=" + this.launchTag
							+ "&&pageType=" + this.pageType + "&&time=" + this.time;
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
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(result!="")
			this.fb = JSON.parseObject(result,FeedBack.class);
	}
	
	public HashMap<String, String> getFetchedMap() {
		return fetchedMap;
	}

	public void setFetchedMap(HashMap<String, String> fetchedMap) {
		this.fetchedMap = fetchedMap;
	}
	
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		Log.i("topic", topic);
		this.topic = topic;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getLaunchTag() {
		return launchTag;
	}

	public void setLaunchTag(int launchTag) {
		this.launchTag = launchTag;
	}

	public int getPageType() {
		return pageType;
	}

	public void setPageType(int pageType) {
		this.pageType = pageType;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public FeedBack getFb() {
		return fb;
	}

	public void setFb(FeedBack fb) {
		this.fb = fb;
	}
}