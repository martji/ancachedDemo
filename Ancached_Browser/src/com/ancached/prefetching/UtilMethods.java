package com.ancached.prefetching;

public class UtilMethods {
	//TODO:better way
	public static String checkSite(String url){
		if(url.contains("sina"))
			return "手机新浪网";
		else if(url.contains("sohu"))
			return "搜狐网";
		else if(url.contains("163"))
			return "手机网易网";
		else if(url.contains("ifeng"))
			return "手机凤凰网";
		else if(url.contains("qq"))
			return "手机腾讯网";
		return "unkown";
	}
	
	public static String checkTopicForSina(String url){
		if(url.contains("nc")||url.contains("news"))
			return "news";
		else if(url.contains("sports")||url.contains("nba"))
			return "sports";
		else if(url.contains("finance"))
			return "finance";
		else if(url.contains("ent"))
			return "ent";
		else if(url.contains("tech"))
			return "tech";
		else
			return "unkown";
	}
}
