package com.ancached.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.http.util.EncodingUtils;
import android.util.Log;
import com.ancached.db.TrackLogItem;

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
	private final static List<String> TOP10_WEBSITE = new ArrayList<String>(Arrays.asList(SINA, 
			IFENG, TECENT, WANGYI, SOHU));
	private final static HashMap<String, String> DOMAIN_MAP = new HashMap<String, String>();
	static {
		DOMAIN_MAP.put("新闻", "news");
		DOMAIN_MAP.put("体育", "sports");
		DOMAIN_MAP.put("财经", "finance");
		DOMAIN_MAP.put("科技", "tech");
		DOMAIN_MAP.put("娱乐", "ent");
		DOMAIN_MAP.put("军事", "mil");
		DOMAIN_MAP.put("汽车", "auto");
	}
	private static final String MODEL_PATH = "sdcard/Ancached/data/model.dat";
	private static HashMap<String, Integer> model_sites = new HashMap<String, Integer>();
	private static HashMap<String, Integer> model_types = new HashMap<String, Integer>();
	static {
		model_sites.put(HAO, 0);model_sites.put(SINA, 1);model_sites.put(IFENG, 2);
		model_sites.put(SOHU, 3);model_sites.put(TECENT, 4);model_sites.put(WANGYI, 5);
		model_sites.put("others", 6);
		model_types.put("index", 0);model_types.put("news", 1);model_types.put("sports", 2);
		model_types.put("finance", 3);model_types.put("tech", 4);model_types.put("ent", 5);
		model_types.put("mil", 6);model_types.put("auto", 7);model_types.put("others", 8);
	}
	private static final int MODEL_SIZE = 63;
	private static double[][] model = new double[MODEL_SIZE][MODEL_SIZE];
	private static List<String> oldTitles = new ArrayList<String>();
	
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
	public static TrackLogItem checkItem(boolean tokenizer_state, List<TrackLogItem> hitPages, TrackLogItem item) {
		// TODO Auto-generated method stub
		TrackLogItem lastItem = hitPages.get(hitPages.size() - 1);
		if (lastItem.getUrl().equals(item.getUrl()) || lastItem.getTitle().equals(item.getTitle())){
			return null;
		}
		//url解析
		String url = item.getUrl();
		String title = item.getTitle();
		url = parseUrl(url, title);
		Log.e("new_url", url);
		item.setUrl(url);
		
		//关键词提取
		if (tokenizer_state){
			if (oldTitles != null && oldTitles.size() > 0){
				for (int i = 0; i < oldTitles.size(); i++){
					Interest.update(oldTitles.get(i));
				}
				oldTitles = null;
			}
			Interest.update(title);
		}
		else {
			oldTitles.add(title);
		}
		return item;
	}

	//根据本次已访问的页面和历史记录判断
	public static String getUrl(List<TrackLogItem> hitPages,
			List<TrackLogItem> result) {
		// TODO Auto-generated method stub
		model = getModel();
		trainModel(result);
		return TOP10_WEBSITE.get(0);
	}
	
	public static void trainModel(List<TrackLogItem> result) {
		// TODO Auto-generated method stub
		//训练模型，1阶markov
		int[][] mid_model = new int[MODEL_SIZE][MODEL_SIZE];
		List<Integer> routers = new ArrayList<Integer>();
		for (int i = 1; i < result.size(); i++){
			if (!compare(result.get(i), result.get(i-1))){
				String url = parseUrl(result.get(i).getUrl(), result.get(i).getTitle());
				Log.e("url_int", url);
				routers.add(urlTransform(url));
			}		
		}
		for(int i = 0; i < routers.size() - 1; i++){
			mid_model[routers.get(i)][routers.get(i+1)] ++;
		}
		DecimalFormat df = new DecimalFormat("#.0000");
		for (int i = 0; i < MODEL_SIZE; i++){
			int tmp = 0;
			for (int j = 0; j < MODEL_SIZE; j++){
				tmp += mid_model[i][j];
			}
			for (int j = 0; j < MODEL_SIZE; j++){
				if (tmp == 0){
					model[i][j] = 0;
				}
				else {
					model[i][j] = Double.parseDouble(df.format((double)mid_model[i][j]/tmp));
				}
			}
		}
		saveModel(model);
	}

	private static boolean compare(TrackLogItem item1, TrackLogItem item2){
		if (item1.getUrl().equals(item2.getUrl()) || item1.getTitle().equals(item2.getTitle())){
			return true;
		}
		return false;
	}
	
	private static double[][] getModel() {
		// TODO Auto-generated method stub
		double model[][] = new double[MODEL_SIZE][MODEL_SIZE];
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
		for (int i = 0; i < MODEL_SIZE; i++){
			for (int j = 0; j < MODEL_SIZE; j++){
				mid_model += Double.toString(model[i][j]);
				mid_model += j != MODEL_SIZE - 1 ? "-" : "";
			}
			mid_model += i != MODEL_SIZE - 1 ? "/" : "";
		}
		return mid_model;
	}
	private static double[][] modelRecovery(String mid_model){
		double[][] model = new double[MODEL_SIZE][MODEL_SIZE];
		String[] rows = mid_model.split("/");
		for (int i = 0; i < MODEL_SIZE; i++){
			String[] columns = rows[i].split("-");
			for (int j = 0; j < MODEL_SIZE; j++){
				model[i][j] = Double.parseDouble(columns[j]);
			}
			
		}
		return model;
	}

	//url归一化处理
	public static String parseUrl(String url, String title){
		int index = 0;
		String n_url = "";
		if (url.contains(HAO)){
			n_url = HAO;
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
				n_url = SOHU + "-" + DOMAIN_MAP.get(title.substring(index - 2, index));
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
}
