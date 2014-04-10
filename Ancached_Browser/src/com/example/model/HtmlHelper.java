package com.example.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.util.Log;

public class HtmlHelper {

	private final static HashMap<String, String> DOMAIN_MAP = new HashMap<String, String>();
	static {
		DOMAIN_MAP.put("新闻", "news");
		DOMAIN_MAP.put("体育", "sports");
		DOMAIN_MAP.put("财经", "finance");
		DOMAIN_MAP.put(" I", "tech");
		DOMAIN_MAP.put("科技", "tech");
		DOMAIN_MAP.put("娱乐", "ent");
		DOMAIN_MAP.put("军事", "mil");
		DOMAIN_MAP.put("汽车", "auto");
		DOMAIN_MAP.put("历史", "history");
	}
	private static List<String> TOPIC = new ArrayList<String>();
	static {
		TOPIC.add("news");TOPIC.add("sports");TOPIC.add("finance");TOPIC.add("tech");
		TOPIC.add("ent");TOPIC.add("mil");TOPIC.add("auto");TOPIC.add("others");
	}
	private static String SOHU = "m.sohu.com";
	private static String YI163 = "3g.163.com/touch";
	private static String SINA = "sina.cn";
	private static String IFENG = "i.ifeng.com";
	private static String TECENT = "info.3g.qq.com";
	
	
	public static void parse(String url, String data) {
		// TODO Auto-generated method stub
		HtmlHelper htmlHelper = new HtmlHelper();
		if (url.equals(SOHU)){
			htmlHelper.getUrlSetSOHU(data);
		}
		else if (url.equals(YI163)){
			htmlHelper.getUrlSet163(data);
		}
		else if (url.equals(TECENT)){
			htmlHelper.getUrlSetTencent(data);
		}
		else {
			htmlHelper.getUrlSet(data, url);
		}
	}
	
	private void getUrlSet(String data, String site){
		String regEx = "<a([^<]*)>([^<]*)</a>"; 
		Pattern p = Pattern.compile(regEx); 
        Matcher m = p.matcher(data); 
        boolean result = m.find();
        while(result) {
        	String url = m.group(1);
    		String title = m.group(2);
        	if (title.length() > 5){
        		if (site.equals(IFENG)){
	        		url = url.substring(url.indexOf("\"")+1, url.lastIndexOf("\""));
	        		if (url.startsWith("/")){
	        			String type = url.substring(1,url.substring(1).indexOf("/"));
	        			url = url.replace("&amp;", "&");
	        			url = IFENG + url;
	        			PageItem pItem = new PageItem(url, title);
	        			pItem.setType(type);
	        			CacheManager.urlMap.put(url, pItem);
        				CacheManager.insertTopicMap(type, pItem);
	        			Log.e("list", title);
	        		}
        		}
        		else if (site.equals(SINA)){
        			if (url.contains("\"")){
        				url = url.substring(url.indexOf("\"")+1, url.lastIndexOf("\""));
        			}
        			else {
        				url = url.substring(url.indexOf("'")+1, url.lastIndexOf("'"));
        			}
        			int tmp = url.indexOf(".sina");
        			if (tmp > 8){
		        		String type = url.substring(8,tmp);
		        		url = url.replace("&amp;", "&");
		        		PageItem pItem = new PageItem(url, title);
		        		pItem.setType(type);
		        		CacheManager.urlMap.put(url, pItem);
        				CacheManager.insertTopicMap(type, pItem);
		        		Log.e("list", title);
        			}
	        		
        		}
        	}
        	result = m.find();
        }
	}
	
	private void getUrlSetTencent(String data){
		String regEx = "<a([^<]*) class=[^<]*>([^<]*)<span class=[^<]*>[^<]*</span></a>|<a([^<]*)>([^<]*)</a>";
		Pattern p = Pattern.compile(regEx); 
        Matcher m = p.matcher(data); 
        boolean result = m.find();
        while(result) {
        	String url = m.group(1);
    		String title = m.group(2);
        	if (url == null){
        		url = m.group(3);
        		title = m.group(4);
        	}
        	if (title.length() > 5){
        		url = url.substring(url.indexOf("\"")+1, url.lastIndexOf("\""));
        		if (url.contains("aid")){
	        		String type = url.substring(url.indexOf("aid=")).
	        				substring(4, url.substring(url.indexOf("aid=")).indexOf("&"));
	        		if (type.contains("_")){
	        			type = type.substring(0, type.indexOf("_"));
	        			if (type.equals("mobile")){
	        				type = "tech";
	        			}
	        			if (type.equals("stock")){
	        				type = "finance";
	        			}
	        		}
	        		if (TOPIC.contains(type)){
	        			url = url.replace("&amp;", "&");
		        		PageItem pItem = new PageItem(url, title);
		        		pItem.setType(type);
		        		CacheManager.urlMap.put(url, pItem);
        				CacheManager.insertTopicMap(type, pItem);
		        		Log.e("list", title);
	        		}
        		}
        	}
        	result = m.find();
        }
	}
	
	private void getUrlSetSOHU(String data) {
		// TODO Auto-generated method stub
		String regEx = "<a([^<]*) class=[^<]*>[^<]*<i[^<]*><img[^<]*></i>[^<]*<p[^<]*>([^<]*)</p>[^<]*</a>|" +
				"<a([^<]*) class=[^<]*><span[^<]*>([^<]*)</span></a>|" +
				"<a([^<]*)><b>([^<]*)</b></a>|" +
				"<a([^<]*)>([^<]*)</a>";
		Pattern p = Pattern.compile(regEx); 
        Matcher m = p.matcher(data); 
        boolean result = m.find();
        List<PageItem> pageItems = new ArrayList<PageItem>();
        while(result) {
        	String url = m.group(1);
    		String title = m.group(2);
        	if (url == null){
        		url = m.group(3);
        		title = m.group(4);
        	}
        	else {
        		Log.e("list", url);
        	}
        	if (url == null){
        		url = m.group(5);
        		title = m.group(6);
        	}
        	if (url == null){
        		url = m.group(7);
        		title = m.group(8);
        	}
        	if (title.length() > 5){
        		if (title.startsWith("进入")){
        			String type = title.substring(2, 4);
        			if (DOMAIN_MAP.containsKey(type)){
        				type = DOMAIN_MAP.get(type);
	        			for (int i = 0; i < pageItems.size(); i++){
	        				PageItem item = pageItems.get(i);
	        				item.setType(type);
	        				CacheManager.urlMap.put(item.getUrl(), item);
	        				CacheManager.insertTopicMap(type, item);
	        				Log.e("list", item.getTitle());
	        			}
        			}
        			pageItems = new ArrayList<PageItem>();
        		}
        		else {
        			if (url.contains("\" class")){
	        			url = url.substring(url.indexOf("\"")+1, url.indexOf("\" class"));
        			}
        			else {
        				url = url.substring(url.indexOf("\"")+1, url.lastIndexOf("\""));
        			}
        			if (!url.contains("http")){
        				url = url.replace("&amp;", "&");
        				url = SOHU + url;
	        			pageItems.add(new PageItem(url, title));
        			}
        		}   		
        	}
            result = m.find();
        }
	}
	
	private void getUrlSet163(String data){
		String regEx = "<a.*href='(.*)'.*>(.*)</a>";
		regEx = "<p class=[^<]*>([^<]*)</p>";
		Pattern p0 = Pattern.compile(regEx); 
        Matcher m0 = p0.matcher(data);
        boolean result0 = m0.find();
        List<String> titleList = new ArrayList<String>();
        while(result0) {
        	titleList.add(m0.group(1));
        	result0 = m0.find();
        }	
		
		regEx = "<a([^<]*)><img[^<]*>([^<]*)</a>|<a([^<]*)>([^<]*)</a>";
		Pattern p = Pattern.compile(regEx); 
        Matcher m = p.matcher(data); 
        boolean result = m.find();
        int index = 0;
        List<PageItem> pageItems = new ArrayList<PageItem>();
        while(result) {
        	String url = m.group(1);
    		String title = m.group(2);
        	if (url == null){
        		url = m.group(3);
        		title = m.group(4);
        		url += "++++";
        	}
        	else if (url.contains("3g.163.com")){
        		title = titleList.get(index++);
			}
        	
        	if (url.contains("3g.163.com") && !url.contains("3g.163.com/links") 
        			&& (title.length()==0 ||title.length() > 5)){
        		if (url.startsWith(" href")){
        			url = url.substring(7,url.lastIndexOf("\""));
        			url = url.replace("&amp;", "&");
        			PageItem item = new PageItem(url, title);
        			pageItems.add(item);
        		}
        		if (title.contains("频道")){
        			String type = title.substring(2,4);
        			if (DOMAIN_MAP.containsKey(type)){
        				type = DOMAIN_MAP.get(type);
        				for (int i = 0; i < pageItems.size(); i++){
        					PageItem item = pageItems.get(i);
        					item.setType(type);
        					CacheManager.urlMap.put(item.getUrl(), item);
	        				CacheManager.insertTopicMap(type, item);
        					Log.e("list", item.getTitle());
        				}
        			}
        			pageItems = new ArrayList<PageItem>();
            	}
        	}  	
        	result = m.find();
        }
	}
}
