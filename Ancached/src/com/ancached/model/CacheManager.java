package com.ancached.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ancached.db.TrackLogItem;

public class CacheManager {

	/**
	 * @param args
	 */
	private final static List<String> TOP10_WEBSITE = new ArrayList<String>(Arrays.asList("http://m.sina.cn",
			"http://m.baidu.com", "http://sina.cn", "http://info.3g.qq.com", "http://3g.163.com/touch/",
			"http://m.sohu.com", "http://i.ifeng.com", "http://m.taobao.com"));
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static String getUrl(TrackLogItem item) {
		// TODO Auto-generated method stub
		return "";
	}

	//根据浏览记录预测下一个页面
	public static String getUrl(List<TrackLogItem> result) {
		// TODO Auto-generated method stub
		return "http://m.hao123.com/";
	}

	//对item进行检查，无效的信息进行去除
	public static TrackLogItem checkItem(TrackLogItem item) {
		// TODO Auto-generated method stub
		return item;
	}

	//根据本次已访问的页面和历史记录判断
	public static String getUrl(List<TrackLogItem> hitPages,
			List<TrackLogItem> result) {
		// TODO Auto-generated method stub
		return TOP10_WEBSITE.get(0);
	}

}
