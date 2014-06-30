package com.example.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.util.Log;

import com.example.struct.PageItem;

public class HtmlHelper {

	private final static HashMap<String, String> DOMAIN_MAP = new HashMap<String, String>();	
	private static List<String> TOPIC = new ArrayList<String>();
	
	private static String SOHU = "m.sohu.com";
	private static String YI163 = "3g.163.com/touch";
	private static String SINA = "sina.cn";
	private static String IFENG = "i.ifeng.com";
	private static String TECENT = "info.3g.qq.com";
	
	public static HashMap<String, String> structMap = new HashMap<String, String>();
	
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
		
		TOPIC.add("news");TOPIC.add("sports");TOPIC.add("finance");TOPIC.add("tech");
		TOPIC.add("ent");TOPIC.add("mil");TOPIC.add("auto");TOPIC.add("others");
	}
	
	public static void parse(String address, String data) {
		// TODO Auto-generated method stub
		if (!structMap.containsKey(address)){
			return;
		}
		String regEx = structMap.get(address);
		int regSize = regEx.split("|").length;
		Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(data);
        boolean result = m.find();
        String topic = "top";
        while(result) {
        	String url = m.group(1);
    		String title = m.group(2);
    		if (url != null){
    			topic = DOMAIN_MAP.containsKey(title) ? DOMAIN_MAP.get(title) : "others";
    		}
    		else {
    			for (int i = 1; i < regSize && url == null; i++){
    				url = m.group(2*i + 1);
    				title = m.group(2*i + 2);
    			}
    		}
    		url = url.substring(url.indexOf("href"));
    		Log.i("url", url);
    		url = url.replace("&amp;", "&");
    		if (url.contains("\" ")){
    			url = url.substring(url.indexOf("\"")+1, url.indexOf("\" "));
    		} else if (url.contains("\"")){
    			int start = url.indexOf("\"") + 1;
    			url = url.substring(start, url.length()-1);
			}
    		if (address.equals(SINA)){
    			if (url.contains("'")){
    				url = url.substring(url.indexOf("'")+1, 
    						url.substring(url.indexOf("'")+1).indexOf("'"));
    			}
    		} else if (address.equals(SOHU)){
    			if (!url.contains("http")){
    				url = SOHU + url;
    			}
    		} else if (address.equals(TECENT)){
    			
    		} else if (address.equals(IFENG)){
        		if (url.startsWith("/")){
        			url = IFENG + url;
        		}
    		} else if (address.equals(YI163)){
    			
    		}
    		Log.i("url", url);
    		PageItem pItem = new PageItem(url, title);
			pItem.setType(address + "-" + topic);
			CacheManager.urlMap.put(url, pItem);
    		if (title.length() >= 5 && url.length() < 200){
        		System.out.println(topic + " -- " + url + " -- " + title);
				CacheManager.insertTopicMap(topic, pItem);
    		}
    		result = m.find();
        }
	}
}
