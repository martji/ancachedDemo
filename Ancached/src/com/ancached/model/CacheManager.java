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

	//���������¼Ԥ����һ��ҳ��
	public static String getUrl(List<TrackLogItem> result) {
		// TODO Auto-generated method stub
		return "http://m.hao123.com/";
	}

	//��item���м�飬��Ч����Ϣ����ȥ��
	public static TrackLogItem checkItem(TrackLogItem item) {
		// TODO Auto-generated method stub
		return item;
	}

	//���ݱ����ѷ��ʵ�ҳ�����ʷ��¼�ж�
	public static String getUrl(List<TrackLogItem> hitPages,
			List<TrackLogItem> result) {
		// TODO Auto-generated method stub
		return TOP10_WEBSITE.get(0);
	}

}
