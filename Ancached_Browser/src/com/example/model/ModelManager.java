package com.example.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.http.util.EncodingUtils;
import android.util.Log;
import com.example.struct.Featuer;
import com.example.struct.TrackLogItem;

public class ModelManager {
	private static String HAO = "m.hao123.com";
	private static final String MODEL_PATH = "sdcard/Ancached_Browser/data/model2.dat";
	static HashMap<String, Integer> model_sites = new HashMap<String, Integer>();
	static HashMap<String, Integer> model_types = new HashMap<String, Integer>();
	static int MODEL_ROWS;
	static int TYPE_SIZE;
	static int MODEL_COLUMNS;
	private static double[][] model;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void trainModel(List<TrackLogItem> result) {
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
	
	public static int urlTransform(String url){
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
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumFractionDigits(4);
		String mid_model = "";
		for (int i = 0; i < MODEL_ROWS; i++){
			for (int j = 0; j < MODEL_COLUMNS; j++){
				mid_model += nf.format(model[i][j]);
				if (j == MODEL_ROWS || j%TYPE_SIZE == MODEL_ROWS){
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
	
	public static double[][] modelRecovery(String mid_model){
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
		return n_model;
	}
	
	public static void getModel() {
		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File modelFile = new File(MODEL_PATH);
			if (!modelFile.exists()){
				try {
					modelFile.createNewFile();
					return;
				} catch (IOException e) {
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
					CacheManager.model = modelRecovery(mid_model);
					fis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	private static void saveModel(double[][] model){
		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File modelFile = new File(MODEL_PATH);
			if (!modelFile.exists()){
				try {
					modelFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				FileOutputStream fout = new FileOutputStream(MODEL_PATH);
				String mid_model = modelTransform(model);
				byte[] bytes = mid_model.getBytes();
			    fout.write(bytes);   
			    fout.close(); 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
