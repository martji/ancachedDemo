package com.example.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.http.util.EncodingUtils;
import com.example.struct.Featuer;
import com.example.struct.PageItem;
import com.example.struct.TrackLogItem;
import android.util.Log;

public class CacheManager {

	/**
	 * @param args
	 */
	private static String HAO = "m.hao123.com";
	private static String SINA = "sina.cn";
	private static String IFENG = "i.ifeng.com";
	private static String TECENT = "info.3g.qq.com";
	private static String WANGYI = "3g.163.com/touch";
	private static String SOHU = "m.sohu.com";
	private static List<String> SITES = new ArrayList<String>();
	private static Map<String, String> TITLES = new HashMap<String, String>();
	private static Map<String, String> SITESTITLE = new HashMap<String, String>();	
	private final static HashMap<String, String> DOMAIN_MAP = new HashMap<String, String>();
	private final static List<String> DOMAIN_TOPIC = new ArrayList<String>();
	
	private static final String MODEL_PATH = "sdcard/Ancached_Browser/data/model.dat";
	private static HashMap<String, Integer> model_sites = new HashMap<String, Integer>();
	private static HashMap<String, Integer> model_types = new HashMap<String, Integer>();
	private static final int MODEL_ROWS = 6;
	private static final int TYPE_SIZE = 8;
	private static final int MODEL_COLUMNS = 73;
	private static double[][] model = new double[MODEL_ROWS][MODEL_COLUMNS];
		
	public static boolean mapStatus = false;
	public static Map<String, PageItem> urlMap = new HashMap<String, PageItem>();
	public static Map<String, List<PageItem>> topicMap = new HashMap<String, List<PageItem>>();
	
	static {
		SITESTITLE.put(SINA, "手机新浪网");SITESTITLE.put(IFENG, "手机凤凰网");SITESTITLE.put(SOHU, "搜狐网");
		SITESTITLE.put(TECENT, "手机腾讯网");SITESTITLE.put(WANGYI, "手机网易网");
		SITESTITLE.put(HAO, "hao123导航-上网从这里开始");

		DOMAIN_MAP.put("新闻", "news");DOMAIN_MAP.put("体育", "sports");
		DOMAIN_MAP.put("财经", "finance");DOMAIN_MAP.put("科技", "tech");
		DOMAIN_MAP.put("娱乐", "ent");DOMAIN_MAP.put("军事", "mil");
		DOMAIN_MAP.put("汽车", "auto");
		DOMAIN_TOPIC.add("news");DOMAIN_TOPIC.add("sports");DOMAIN_TOPIC.add("finance");
		DOMAIN_TOPIC.add("tech");DOMAIN_TOPIC.add("ent");DOMAIN_TOPIC.add("mil");
		DOMAIN_TOPIC.add("auto");DOMAIN_TOPIC.add("others");

		model_sites.put(SINA, 0);model_sites.put(IFENG, 1);
		model_sites.put(SOHU, 2);model_sites.put(TECENT, 3);model_sites.put(WANGYI, 4);
		model_sites.put("others", 5);model_sites.put(TECENT+"-index", 6);
		model_types.put("index", 0);model_types.put("news", 1);model_types.put("sports", 2);
		model_types.put("finance", 3);model_types.put("tech", 4);model_types.put("ent", 5);
		model_types.put("mil", 6);model_types.put("auto", 7);model_types.put("others", 8);

		SITES.add(SINA);SITES.add(IFENG);SITES.add(TECENT);
		SITES.add(SOHU);SITES.add(WANGYI);SITES.add(HAO);
		TITLES.put("手机新浪网", SINA);TITLES.put("手机凤凰网", IFENG);TITLES.put("搜狐网", SOHU);
		TITLES.put("手机腾讯网", TECENT);TITLES.put("手机网易网", WANGYI);
		TITLES.put("hao123导航-上网从这里开始", HAO);
	}
	
	/**
	 * 
	 * @param url 当前点击的链接
	 * @return 下一次可能访问的主题
	 */
	public static String getTopic(String url) {
		// TODO Auto-generated method stub
		int urlNum = urlTransform(url);
		int site = urlNum / 9;
		int topic = urlNum % 9;
		int index = 0;
		for (int i = topic*8+1, j = 0; j < 8; j++){
			if (model[site][i+j] > model[site][i+index]){
				index = j;
			}
		}
		String nextTopic = DOMAIN_TOPIC.get(index);
		return nextTopic;
	}
	
	/**
	 * 
	 * @return 初始化时调用，返回启动会可能最先访问的网站？第二可能
	 */
	public static String getUrl(){
		int tmp = 0;
		for (int i = 0; i < MODEL_ROWS; i++){
			if (model[i][0] > model[tmp][0]){
				tmp = i;
			}
		}
		return SITES.get(tmp);
	}
	
	public static String getUrl(List<TrackLogItem> hitPages) {
		// TODO Auto-generated method stub
		TrackLogItem item = hitPages.get(hitPages.size()-1);
		String url = item.getUrl();
		if (url.equals(HAO)){
			return "";
		}
		else {
			String topic = CacheManager.getTopic(url);
			Log.i("nextUrl_topic", topic);
			List<PageItem> items = CacheManager.topicMap.get(topic);
			return getUrlInner(url, items);
		}
	}
	
	/**
	 * 
	 * @param url 本次点击的链接
	 * @param items 链接集合
	 * @return 接下来可能访问的链接
	 */
	public static String getUrlInner(String url, List<PageItem> items) {
		// TODO Auto-generated method stub
		String nextUrl = HAO;
		return nextUrl;
	}
	
	public static TrackLogItem checkItem(List<TrackLogItem> hitPages, TrackLogItem item) {
		// TODO Auto-generated method stub
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void trainModel(List<TrackLogItem> result) {
		// TODO Auto-generated method stub
		Date curDate = new Date(System.currentTimeMillis());
		HashMap<Featuer, List<List<String>>> vPath = new HashMap<Featuer, List<List<String>>>();
		for (int i = 0; i < result.size(); i++){
			TrackLogItem item = result.get(i);
			Log.e("url_routers", item.getUrl() + item.getTitle());
			if (item.getUrl().equals("----")){
				if (++i < result.size() && result.get(i).getUrl().equals(HAO)){
					item = result.get(i);
					Featuer featuer = new Featuer(item.getvTime(), item.getLocation(), item.getNetState());
					List<List<String>> path = new ArrayList<List<String>>();
					for (i = i+1; i < result.size(); i++){
						if (result.get(i).getUrl().equals("----")){
							i--;
							break;
						}
						if (model_sites.containsKey(result.get(i).getUrl())){
							List<String> rt = new ArrayList<String>();
							rt.add(result.get(i).getUrl());
							for (i = i + 1; i < result.size(); i++) {
								if (result.get(i).getUrl().equals(HAO)) {
									break;
								}
								if (!model_sites.containsKey(result.get(i).getUrl())) {
									rt.add(result.get(i).getUrl());
									if (result.get(i + 1).getUrl().equals(getParent(result.get(i).getUrl()))) {
										i++;
									}
								}
							}
							path.add(rt);
						}
					}
					if (path!=null && path.size() > 0){
						vPath.put(featuer, path);
					}
				}			
			}
		}
		
		int[][] mid_model = new int[MODEL_ROWS][MODEL_COLUMNS];
		for (int i = 0; i < MODEL_ROWS; i++){
			for (int j = 0; j < MODEL_COLUMNS; j++){
				mid_model[i][j] = 0; 
			}
		}
		List<List<Integer>> routers = new ArrayList<List<Integer>>();
		List<Integer> router_s = new ArrayList<Integer>();
		Iterator iter = vPath.entrySet().iterator();
		while (iter.hasNext()){
			Map.Entry entry = (Map.Entry) iter.next();
			router_s.add(-1);
			List<List<String>> path = (List<List<String>>) entry.getValue();
			for (int j = 0; j < path.size(); j++){
				List<Integer> router = new ArrayList<Integer>();
				List<String> rt = path.get(j);
				router_s.add(urlTransform(rt.get(0)));
				for (int k = 0; k < rt.size(); k++){
					int level = urlTransform(rt.get(k));
					router.add(level);
				}
				routers.add(router);
			}
		}
		for (int i = 0; i < routers.size(); i++){
			List<Integer> router = routers.get(i);
			for (int j = 0; j < router.size()-1; j++){
				mid_model[router.get(0)/9][router.get(j)%9*TYPE_SIZE+router.get(j+1)%9] ++;
			}
		}
		DecimalFormat df = new DecimalFormat("#.0000");
		int tmp = 0;
		for (int i = 0; i < router_s.size()-1; i++){
			if (router_s.get(i) != -1){
				mid_model[router_s.get(i)/9][0] ++;
				tmp ++;
			}
		}
		for(int i = 0; i < MODEL_ROWS; i++){
			model[i][0] = Double.parseDouble(df.format((double)mid_model[i][0]/tmp));
			for(int j = 0; j < TYPE_SIZE; j++){
				int tmp_s = 0;
				for (int n = j*TYPE_SIZE,k=1; k<=TYPE_SIZE; k++){
					tmp_s += mid_model[i][n+k];
				}
				for (int n = j*TYPE_SIZE,k=1; k<=TYPE_SIZE; k++){
					if (tmp_s == 0){
						model[i][n+k] = 0;
					}
					else{
						model[i][n+k] = Double.parseDouble(df.format((double)mid_model[i][n+k]/tmp_s));
					}
				}
			}
		}
		saveModel(model);
		Date endDate = new Date(System.currentTimeMillis());
		long diff = endDate.getTime() - curDate.getTime();
		Log.e("cost", Long.toString(diff));
	}

	private static String getParent(String page){
		if (page.contains("-")){
			return page.split("-")[0];
		}
		return page;
	}
	
	private static boolean compare(TrackLogItem item1, TrackLogItem item2){
		if (item1.getUrl().equals(item2.getUrl()) || item1.getTitle().equals(item2.getTitle())){
			return true;
		}
		return false;
	}
	
	public static double[][] getModel() {
		// TODO Auto-generated method stub
		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File modelFile = new File(MODEL_PATH);
			if (!modelFile.exists()){
				try {
					modelFile.createNewFile();
					return null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				try {
					FileInputStream fis = new FileInputStream(modelFile);
					int length = fis.available();   
					byte[] buffer = new byte[length];
					fis.read(buffer);
					String mid_model = EncodingUtils.getString(buffer, "UTF-8");
					model = modelRecovery(mid_model);
					fis.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return model;
	}
	private static void saveModel(double[][] model){
		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File modelFile = new File(MODEL_PATH);
			if (!modelFile.exists()){
				try {
					modelFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				FileOutputStream fout = new FileOutputStream(MODEL_PATH);
				String mid_model = modelTransform(model);
				byte[] bytes = mid_model.getBytes();
			    fout.write(bytes);   
			    fout.close(); 
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static int urlTransform(String url){
		int tmp = 0;
		if (url.contains("-")){
			String site = url.split("-")[0];
			String type = url.split("-")[1];
			tmp += model_sites.get(site)*model_types.size();
			if (model_types.containsKey(type)){
				tmp += model_types.get(type);
			}
			else {
				tmp += model_types.size() - 1;
			}
		}
		else {
			String site = url;
			if (site == ""){
				return -1;
			}
			if (model_sites.containsKey(site)){
				tmp += model_sites.get(site)*model_types.size();
			}
			else {
				tmp += model_sites.get("others")*model_types.size();
			}
		}
		return tmp;
	}
	
	private static String modelTransform(double[][] model) {
		// TODO Auto-generated method stub
		String mid_model = "";
		for (int i = 0; i < MODEL_ROWS; i++){
			for (int j = 0; j < MODEL_COLUMNS; j++){
				mid_model += Double.toString(model[i][j]);
				if (j == 0 || j%TYPE_SIZE == 0){
					mid_model += "\n";
				}
				else {
					mid_model += j-1 != MODEL_COLUMNS - 1 ? "\t" : "";
				}
			}
			mid_model += i != MODEL_ROWS - 1 ? "\n" : "";
		}
		return mid_model;
	}
	private static double[][] modelRecovery(String mid_model){
		double[][] n_model = new double[MODEL_ROWS][MODEL_COLUMNS];
		String[] rows = mid_model.split("\n");
		for (int i = 0, s = 0, t = 0; i < rows.length; i++){
			if (rows[i] == null || rows[i].length() == 0){
				s++;
				t = 0;
			}
			else {
				String[] columns = rows[i].split("\t");
				for (int j = 0; j < columns.length; j++){
					n_model[s][t++] = Double.parseDouble(columns[j]);
				}
			}			
		}
		return model;
	}

	public static String parseUrl(String url, String title){
		int index = 0;
		String n_url = "";
		if (url.contains(HAO)){
			n_url = HAO;
		}
		else if (url.contains("file")){
			url = url.substring(url.lastIndexOf("/")+1);
			url = CacheHelper.urlList.get(url);
			return parseUrl(url, title);
		}
		else if (url.contains(SINA)){
			index = url.indexOf(SINA);
			if (index > 7){
				n_url = SINA + "-" + url.substring(7, index-1);
			}
			else {
				n_url = SINA;
			}
		}
		else if (url.contains(IFENG)){
			index = 19;
			Log.i("debug", url);
			String type = url.substring(index).split("/")[0];
			if (type != ""){
				n_url = IFENG + "-" + type;
			}
			else {
				n_url = IFENG;
			}
		}
		else if (url.contains(TECENT)){
			if (url.contains("aid")){
				index = url.indexOf("aid") + 4;
			}
			else {
				index = url.indexOf("tid") + 4;
			}
			if (index > 0){
				n_url = TECENT + "-" + url.substring(index).split("&")[0];
				if (n_url.contains("_")){
					n_url = n_url.substring(0, n_url.indexOf("_"));
				}
				if (n_url.contains("/")){
					n_url = TECENT + "-" + n_url.substring(n_url.indexOf("/")+1);
				}
			}
			else {
				n_url = TECENT;
			}
		}
		else if (url.contains(SOHU)){
			index = title.indexOf("频道");
			if (index > 0){
				if (DOMAIN_MAP.containsKey(title.substring(index - 2, index))){
					n_url = SOHU + "-" + DOMAIN_MAP.get(title.substring(index - 2, index));
				}
				else {
					n_url = SOHU + "-others";
				}
			}
			else {
				n_url = SOHU;
			}
		}
		else if (url.contains(WANGYI)){
			n_url = WANGYI;
		}
		else {
			n_url = "others";
		}
		return n_url;
	}

	public static String checkUrl(String url, String title) {
		// TODO Auto-generated method stub
		if (SITES.contains(url)){
			return url;
		}
		else if(TITLES.containsKey(title)){
			return TITLES.get(title);
		}
		return null;
	}

	public static void parseSite(String url, String data) {
		// TODO Auto-generated method stub
		urlMap = new HashMap<String, PageItem>();
		topicMap = new HashMap<String, List<PageItem>>();
		Date curDate = new Date(System.currentTimeMillis());
		HtmlHelper.parse(url, data);
		Date endDate = new Date(System.currentTimeMillis());
		long diff = endDate.getTime() - curDate.getTime();
		Log.e("webservice_result", Long.toString(diff));
	}

	public static String getTitle(String url) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		if (!topicMap.containsKey(type)){
			topicMap.put(type, new ArrayList<PageItem>());
		}
		topicMap.get(type).add(item);
	}
}
