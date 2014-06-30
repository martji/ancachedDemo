package com.example.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ancached.params.CfgHelper;
import com.ancached.prefetching.MyPrefetch;
import com.ancached.prefetching.Prefetch;
import com.ancached.prefetching.UtilMethods;
import com.example.ancached_browser.WebViewActivity;
import com.example.struct.Item;
import com.example.struct.PageItem;
import com.example.struct.Seed;
import com.example.struct.TrackLogItem;
import android.util.Log;

public class CacheManager {

	/**
	 * @param args
	 */
	private static String HAO = "m.hao123.com";
	
	private static List<String> SITES = new ArrayList<String>();
	private static Map<String, String> TITLES = new HashMap<String, String>();
	private static Map<String, String> SITESTITLE = new HashMap<String, String>();
	private static List<String> DOMAIN_TOPIC = new ArrayList<String>();
	
	private static int MODEL_ROWS;
	private static int TYPE_SIZE;
	private static int MODEL_COLUMNS;
	static double[][] model;
		
	public static boolean mapStatus = true;
	public static Map<String, PageItem> urlMap = new HashMap<String, PageItem>();
	public static Map<String, List<PageItem>> topicMap = new HashMap<String, List<PageItem>>();
	private static Map<String, List<String>> sortedTopicMap = new HashMap<String, List<String>>();
	
	public final static double WEIGHT_THRESHOLD = 0.6;
	private static final int ITEM_COUNT = 6;
	private static final int PAGE_COUNT = 22;
	
//	static {
//		SITESTITLE.put(HAO, "hao123导航-上网从这里开始");
//		model_sites.put(TECENT+"-index", 6);
//		SITES.add(HAO);
//	}
	
	public static void init(InputStream is) {
		CfgHelper cfgHelper = new CfgHelper();
		cfgHelper.init(is);
		SITES = cfgHelper.getSites();
		TITLES = cfgHelper.getTitles();
		SITESTITLE = cfgHelper.getSiteMap();
		cfgHelper.getModelSites();
		DOMAIN_TOPIC = cfgHelper.getTopics();
		cfgHelper.getModelTypes();
		
		ModelManager.model_sites = cfgHelper.getModelSites();
		ModelManager.model_types = cfgHelper.getModelTypes();
		ModelManager.MODEL_ROWS = MODEL_ROWS = SITES.size() + 1;
		ModelManager.TYPE_SIZE = TYPE_SIZE = DOMAIN_TOPIC.size();
		ModelManager.MODEL_COLUMNS = MODEL_COLUMNS 
				= MODEL_ROWS+1 + TYPE_SIZE*TYPE_SIZE*(TYPE_SIZE+1) + TYPE_SIZE;
		model = new double[MODEL_ROWS][MODEL_COLUMNS];
		for (int i = 0; i < MODEL_ROWS; i++){
			for (int j = 0; j < MODEL_COLUMNS; j++){
				model[i][j] = 0;
			}
		}
	}
	
	public static String getUrl(){
		int tmp = 0;
		for (int i = 0; i < MODEL_ROWS; i++){
			if (model[i][0] > model[tmp][0]){
				tmp = i;
			}
		}
		return SITES.get(tmp);
	}
	
	public static TrackLogItem checkItem(List<TrackLogItem> hitPages, TrackLogItem item) {
		TrackLogItem lastItem = hitPages.get(hitPages.size() - 1);
		if (compare(lastItem, item)){
			return null;
		}
		String url = item.getUrl();
		String title = item.getTitle();
		url = parseUrl(url, title);
		Log.e("new_url", url);
		item.setUrl(url);	
		return item;
	}
	
	private static boolean compare(TrackLogItem item1, TrackLogItem item2){
		String url1 = item1.getOriurl();
		String url2 = item2.getOriurl();
		if (url1.equals(url2) || item1.getTitle().equals(item2.getTitle())){
			return true;
		}
		return false;
	}
	
	public static String parseUrl(String url, String title){
		String n_url = "";
		if (url.contains("homesites")){
			n_url = HAO;
		} else if (url.contains("file")){
			url = url.substring(url.lastIndexOf("/")+1);
			url = CacheHelper.urlList.get(url);
			return parseUrl(url, title);
		} else if ((n_url = CacheManager.checkUrl(url, title)) != null){
			return n_url;
		}
		else {
			n_url = CacheManager.urlMap.containsKey(url) ? 
					CacheManager.urlMap.get(url).getType() : "others";
		}
		return n_url;
	}

	//core method
	public static List<String> getUrl(List<String> realRouters, List<TrackLogItem> hitPages) {
		TrackLogItem item = hitPages.get(hitPages.size()-1);
		String url = item.getUrl();
		String title = item.getTitle();
		if (realRouters == null || realRouters.size() == 0){
			return null;
		} else {
			String topic;
			if (realRouters.size() == 1){
				if (realRouters.get(0).contains(HAO)){
					return null;
				}
				topic = CacheManager.getTopic(realRouters.get(0));
			} else {
				int index = realRouters.size() - 1;
				if (realRouters.get(index).contains(HAO)){
					return null;
				}
				String murl = realRouters.get(index - 1) + "," + realRouters.get(index);
				topic = CacheManager.getTopic(murl);
				try{
					final String currentTopic = realRouters.get(index).split("-")[1];
					if (sortedTopicMap.containsKey(currentTopic)){
						new Thread(new Runnable() {
							@Override
							public void run() {
								List<String> seeds = sortedTopicMap.get(currentTopic);
								for (int i = 0; i < seeds.size(); i++){
									String address = seeds.get(i);
									if (!CacheHelper.cachedList.containsKey(address) &&
											!WebViewActivity.visitedUrls.contains(address)){
										Log.i("next_title", CacheManager.getTitle(address));
										CacheHelper.getHTML(address);
										break;
									}
								}
							}
						}).start();
					} else {
						List<PageItem> items = CacheManager.topicMap.get(currentTopic);
						if (items != null && items.size() > 0){
							List<String> nextUrls = getUrlInner(url, "", currentTopic, items);
							for (int i = 0; i < nextUrls.size(); i++){
								String address = nextUrls.get(i);
								if (!WebViewActivity.visitedUrls.contains(address)){
									Log.i("next_title", CacheManager.getTitle(address));
									CacheHelper.getHTML(address);
									break;
								}
							}
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
			Log.i("nextUrl_topic", topic);
			if (sortedTopicMap.containsKey(topic)){
				getUrlInner(url, title, topic, new ArrayList<PageItem>());
				return sortedTopicMap.get(topic);
			}
			List<PageItem> items = CacheManager.topicMap.get(topic);
			return getUrlInner(url, title, topic, items);
		}
	}
	
	public static String getTopic(String url) {
		if (!url.contains(",")){
			int index = ModelManager.urlTransform(url);
			int i = index/9;
			int j = index%9;
			int start = MODEL_ROWS + j*TYPE_SIZE;
			if (j != 1){
				index = 1;
			} else {
				index = 2;
			}
			for (int t = index; t <= TYPE_SIZE; t++){
				if (model[i][start+t] > model[i][start+index] && t!=j){
					index = t;
				}
			}
			return DOMAIN_TOPIC.get(index-1);
		} else if (url.split(",").length == 2){
			String[] urls = url.split(",");
			int s = ModelManager.urlTransform(urls[0]);
			int t = ModelManager.urlTransform(urls[1]);
			int i = s/9;
			int start = MODEL_ROWS + TYPE_SIZE + s%9*TYPE_SIZE*TYPE_SIZE + (t%9-1)*TYPE_SIZE;
			double tmp = 0;
			int index = 1;
			for (int k = 1; k <= TYPE_SIZE; k++){
				if (model[i][start+k] > tmp && k!=t && k!=s){
					tmp = model[i][start+k];
					index = k;
				}
			}
			return DOMAIN_TOPIC.get(index-1);
		}
		return "";
	}
	
	public static List<String> getUrlInner(String url, String title, String nexttopic, List<PageItem> items) {
		String site, topic;
		site = UtilMethods.checkSite(url);
		if (url.contains("-")){
			topic = url.split("-")[1];
			if (!DOMAIN_TOPIC.contains(topic)){
				topic = "others";
			}
			topic += "-";
		} else {
			topic = "";
		}
		topic += nexttopic;
		ArrayList<Item> mitems = new ArrayList<Item>();
		for (int i = 0, j = 0; i < items.size() && j < PAGE_COUNT; i++){
			if (!(items.get(i).getUrl().length() > 100 || items.get(i).getTitle().length() < 5)){
				mitems.add(new Item(items.get(i).getUrl(), items.get(i).getTitle()));
				j ++;
			}
		}
		Log.i("current_title", title);
		MyPrefetch myPrefetch = new MyPrefetch(Prefetch.getPageType());
		myPrefetch.getNextUrls(site, topic, title, mitems);
		ArrayList<Seed> seeds = myPrefetch.getFb().getSortList();
		try{
			Log.i("result", seeds.get(0).getData().getDescription());
		}catch (Exception e){
			e.printStackTrace();
		}	
		List<String> nextUrls = new ArrayList<String>();
		for (int i = 0, j = 0; i < seeds.size(); i++){
			if (seeds.get(i).getData().getWeight() > WEIGHT_THRESHOLD || j < ITEM_COUNT){
				nextUrls.add(seeds.get(i).getUrl());
				j ++;
			}
		}
		if (nextUrls.size() < ITEM_COUNT){
			for (int i = 0, j = nextUrls.size(); i < mitems.size() && j < ITEM_COUNT; i++){
				if (! nextUrls.contains(mitems.get(i).getUrl())){
					nextUrls.add(mitems.get(i).getUrl());
					j ++;
				}
			}
		}
		sortedTopicMap.put(nexttopic, nextUrls);
		return nextUrls;
	}
	
	public static String checkUrl(String url, String title) {
		if (SITES.contains(url)){
			return url;
		}
		else if(TITLES.containsKey(title)){
			return TITLES.get(title);
		}
		return null;
	}

	public static void parseSite(String url, String data) {
		urlMap = new HashMap<String, PageItem>();
		topicMap = new HashMap<String, List<PageItem>>();
		Date curDate = new Date(System.currentTimeMillis());
		HtmlHelper.parse(url, data);
		Date endDate = new Date(System.currentTimeMillis());
		long diff = endDate.getTime() - curDate.getTime();
		Log.e("webservice_result", Long.toString(diff));
	}

	public static String getTitle(String url) {
		if (urlMap.containsKey(url)){
			return urlMap.get(url).getTitle();
		}
		else {
			for (int i = 0; i < SITES.size(); i++){
				if (url.indexOf(SITES.get(i)) == 7){
					return SITESTITLE.get(SITES.get(i));
				}
			}
		}
		return "";
	}

	public static void insertTopicMap(String type, PageItem item) {
		if (!topicMap.containsKey(type)){
			topicMap.put(type, new ArrayList<PageItem>());
		}
		topicMap.get(type).add(item);
	}
}
