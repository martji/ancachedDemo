package com.ancached.prefetching;

public class UtilMethods {
	//TODO:better way
	public static String checkSite(String url){
		if(url.contains("sina"))
			return "�ֻ�������";
		else if(url.contains("sohu"))
			return "�Ѻ���";
		else if(url.contains("163"))
			return "�ֻ�������";
		else if(url.contains("ifeng"))
			return "�ֻ������";
		else if(url.contains("qq"))
			return "�ֻ���Ѷ��";
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
