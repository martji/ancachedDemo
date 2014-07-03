package com.example.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.ancached.params.Params;
import com.ancached.prefetching.Prefetch;
import android.os.Environment;
import android.util.Log;

public class CacheHelper {
	public static Map<String, String> cachedList = new ConcurrentHashMap<String, String>();
	public static Map<String, String> urlList = new HashMap<String, String>();
	public static Map<String, String> localsite = new HashMap<String, String>();
	public static Map<String, String> prefix = new HashMap<String, String>();
	public static Map<String, HashMap<String, String>> cachedPages = new HashMap<String, HashMap<String,String>>();

	public static String APP_PATH = "file:///"
			+ Environment.getExternalStorageDirectory().getPath()
			+ "/Ancached_Browser/";
	private static final String PAGE_DIR = "/sdcard/Ancached_Browser/file";
	private static final String RES_DIR = "/sdcard/Ancached_Browser/res";
	private static final String CSS_DIR = "/sdcard/Ancached_Browser/css";
	private static final Semaphore semaphore = new Semaphore(1);

	public static void init() {
		initPage();
		initRes();
	}

	public static void initPage() {
		File fileDir = new File(PAGE_DIR);
		if (!fileDir.exists()){
			fileDir.mkdir();
		}
		File[] files = fileDir.listFiles();
		Log.d("fileList", files.length + "");
		if (files != null) {
			Date current_dt = new Date();
			for (File file : files) {
				Date dt = new Date(file.lastModified());
				long time = current_dt.getTime() - dt.getTime();
				if (file.length() == 0) {
					file.delete();
				} else if (time / 1000 / 60 / 60 / 24 >= 1) {
					file.delete();
				} else {
					String fileName = APP_PATH + "file/" + file.getName();
					Log.d("fileName", fileName);
					if (urlList.containsKey(fileName)) {
						cachedList.put(urlList.get(fileName), fileName);
						//MyDBHelper.insertCachedTable(fileName, urlList.get(fileName));
					}
				}
			}
		}
	}

	public static void initRes() {
		File fileDir = new File(RES_DIR);
		if (!fileDir.exists()){
			fileDir.mkdir();
		}
		File[] files = fileDir.listFiles();
		if (files != null) {
			for (File file : files) {
				String fileName = APP_PATH + "res/" + file.getName();
				if (urlList.containsKey(fileName)) {
					cachedList.put(urlList.get(fileName), fileName);
					//MyDBHelper.insertCachedTable(fileName, urlList.get(fileName));
				}
			}
		}
		File cssDir = new File(CSS_DIR);
		if (!cssDir.exists()){
			cssDir.mkdir();
		}
		files = cssDir.listFiles();
		if (files != null) {
			int count = 0;
			for (File file : files) {
				String fileName = APP_PATH + "css/" + file.getName();
				if (urlList.containsKey(fileName)) {
					cachedList.put(urlList.get(fileName), fileName);
					//MyDBHelper.insertCachedTable(fileName, urlList.get(fileName));
				}
				if (fileName.contains("unparsed")) {
					if (Params.getNET_STATE() == 1 && count < 1) {
						String ftpAddress = APP_PATH + "css";
						String url = urlList.get(fileName);
						Thread dealWithCss = new Thread(new getCssRESThread(url, 
								fileName.replace(ftpAddress, CSS_DIR), fileName));
						dealWithCss.start();
						count ++;
					}
				}
			}
		}
	}
	
	public static String getLocalUrl(String url) {
		return cachedList.get(url);
	}
	
	public static String getUrl(String url) {
		if (url.contains("Ancached_Browser/file")){
			url = urlList.get(url);
		}
		if (url.contains("sina") && url.contains("&clicktime")) {
			url = url.substring(0, url.indexOf("&clicktime"));
		} else if (url.contains("qq")) {
			url = url.replaceAll("sid=[^&]*&*", "");
		}
		Log.i("url", url);
		return url;
	}


	public static String checkUrl(String url) {
		if (cachedList.containsKey(url)) {
			return url;
		} else {
			url = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
			if (cachedList.containsKey(url)) {
				return url;
			}
		}
		return null;
	}

	private static boolean containsCssJsPic(String url) {
		if (url.contains(".jpg") || url.contains(".css") || url.contains(".js")
				|| url.contains(".png") || url.contains(".gif")
				|| url.contains(".jpeg"))
			return true;
		else
			return false;
	}

	private static String resType(String url) {
		if (url.contains(".jpg")) {
			return "jpg";
		} else if (url.contains(".css")) {
			return "css";
		} else if (url.contains(".js")) {
			return "js";
		} else if (url.contains(".png")) {
			return "png";
		} else if (url.contains(".jpeg")) {
			return "jpeg";
		} else {
			return "gif";
		}
	}

	/*
	 * get the suffix of the url
	 */
	@SuppressWarnings("unused")
	private static String getSuffix(String url) {
		String suffix = url.substring(url.lastIndexOf("/") + 1, url.length());
		return suffix;
	}
	
	public static void getCssRES(String originURL, String localAddress, String ftpaddress) {
		ArrayList<String> resList = urlParser(localAddress, 1);
		Iterator<String> iter = resList.iterator();
		ExecutorService exec = Executors.newCachedThreadPool();
		while (iter.hasNext()) {
			String url = iter.next();
			if (url.startsWith("background")) {
				// TODO other web sites except sina.cn
				String urlPrefix = originURL.substring(0,
						originURL.lastIndexOf("/"));
				urlPrefix = urlPrefix.substring(0, urlPrefix.lastIndexOf("/"));
				url = url.substring(url.indexOf("/"));
				url = urlPrefix + url;
			}
			if (!cachedList.containsKey(url)) {
				getResThread grt = new getResThread(localAddress, url, 1);
				cachedList.put(url, url);
				exec.execute(grt);
			}
		}
		exec.shutdown();
		while (!exec.isTerminated()){
			try {
				Thread.sleep(200);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		reviseFile(localAddress, originURL, ftpaddress);
	}
	
	/*
	 * download the res in the css file
	 */
	public static class getCssRESThread implements Runnable {
		String url = "";
		String localaddress = "";
		String ftpaddress = "";

		public getCssRESThread(String address, String localAddress,
				String ftpAddress) {
			this.url = address;
			this.localaddress = localAddress;
			this.ftpaddress = ftpAddress;
		}
		@Override
		public void run() {
			getCssRES(url, localaddress, ftpaddress);
		}
	}

	/*
	 * download the res in the htmlPage
	 */
	public static void getRES(String htmlPath) {
		ArrayList<String> resList  = urlParser(htmlPath, 0);
		Iterator<String> iter = resList.iterator();
		ExecutorService exec = Executors.newCachedThreadPool();
		while (iter.hasNext()) {
			String url = iter.next();
			if (!cachedList.containsKey(url)) {
				getResThread grt = new getResThread(htmlPath, url, 0);
				cachedList.put(url, url);
				exec.execute(grt);
			}
		}
		exec.shutdown();
		while (!exec.isTerminated()){
			try {
				Thread.sleep(200);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		reviseFile(htmlPath, null, null);
	}

	public static class getResThread implements Runnable {
		String url = "";
		String fatherUrl = "";
		int tag = 0;
		public getResThread(String fatherAddress, String Address, int cssTag) {
			this.url = Address;
			this.tag = cssTag;
			this.fatherUrl = fatherAddress;
		}
		@Override
		public void run() {
			try {
				String type = resType(url);
				URL urlAddr = new URL(url);
				HttpURLConnection connection = (HttpURLConnection) urlAddr.openConnection();
				connection.setConnectTimeout(200);
				InputStream inStream = connection.getInputStream();

				semaphore.acquire();
				long time = System.currentTimeMillis();
				String absoluteAddress = "";
				String ftpAddress = "";
				if (type != "css") {
					if (tag == 0) {
						absoluteAddress = RES_DIR + "/" + time + "." + type;
						ftpAddress = APP_PATH + "res/" + time + "." + type;
					} else {
						absoluteAddress = CSS_DIR + "/" + time + "." + type;
						ftpAddress = APP_PATH + "css/" + time + "." + type;
					}
				} else {
					absoluteAddress = CSS_DIR + "/" + time + "_unparsed" + "." + type;
					ftpAddress = APP_PATH + "css/" + time + "_unparsed" + "." + type;
				}
				semaphore.release();

				File file = new File(absoluteAddress);
				if (!file.exists()) {
					file.createNewFile();
				}
				FileOutputStream fout = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = inStream.read(buffer)) != -1) {
					fout.write(buffer, 0, len);
				}
				connection.disconnect();
				inStream.close();
				fout.close();
				cachedList.put(url, ftpAddress);
				if (!cachedPages.containsKey(fatherUrl)){
					cachedPages.put(fatherUrl, new HashMap<String, String>());
				}
				cachedPages.get(fatherUrl).put(url, ftpAddress);
				MyDBHelper.insertCachedTable(ftpAddress, url);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * revise the HTML
	 */
	public static void reviseFile(String localAddress, String originURL, String ftpAddress) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(localAddress));
			String line = "";
			StringBuffer buffer = new StringBuffer();
			while ((line = br.readLine()) != null) {
				buffer.append(line);
			}
			line = buffer.toString();
			br.close();
			Map<String, String> localCachedlist = cachedPages.get(localAddress);
			if (localCachedlist != null && localCachedlist.size() > 0){
				Iterator<Entry<String, String>> iter = localCachedlist.entrySet().iterator();
				while (iter.hasNext()) {
					Entry<String, String> entry = iter.next();
					String key = entry.getKey();
					String value = entry.getValue();
					if(Prefetch.getFetchedMap().containsKey(key)){
						String tmpKey = Prefetch.getFetchedMap().get(key);
						line = line.replace(Prefetch.getFetchedMap().get(key), "<font color=red>"+tmpKey+"</font>");
					}
					else if (key != "") {
						line = line.replace(key, value);
					}
				}
			}
			if (localAddress.contains("unparsed")) {
				String deleteAddress = localAddress;
				localAddress = localAddress.replace("unparsed", "parsed");
				ftpAddress = ftpAddress.replace("unparsed", "parsed");
				cachedList.put(originURL, ftpAddress);
				MyDBHelper.insertCachedTable(ftpAddress, originURL);
				File deleteFile = new File(deleteAddress);
				deleteFile.delete();
			}
			FileOutputStream fos = new FileOutputStream(new File(localAddress));
			Writer os = new OutputStreamWriter(fos, "UTF-8");
			os.write(line);
			os.flush();
			fos.close();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void showFetchedFiles(){
		String url=Prefetch.getUrl();
		String localUrl=cachedList.get(url);
		if(localUrl!=null){
			localUrl=localUrl.replace(APP_PATH + "file/", PAGE_DIR + "/");
			File file=new File(localUrl);
			try {
				Document doc=Jsoup.parse(file, "UTF-8");
				Elements eles=doc.select("[href^=http],[href^=/]");
				for(Element ele:eles){
					String tmpUrl=ele.attr("href").toString();
					if(cachedList.containsKey(tmpUrl)){
						String text=ele.text();
						ele.empty();
						Element fontEle=ele.appendElement("font");
						fontEle.attr("color","red");
						fontEle.text(text);
					}
				}
				FileOutputStream fos = new FileOutputStream(new File(localUrl));
				Writer os = new OutputStreamWriter(fos, "UTF-8");
				os.write(doc.toString());
				os.flush();
				fos.close();
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/*
	 * download the web page
	 */
	public static void getHTML(String address) {
		try {
			if (!cachedList.containsKey(address)) {
				semaphore.acquire();
				long time = System.currentTimeMillis();
				semaphore.release();
				String absoluteAddress = PAGE_DIR + "/" + time + ".html";
				String ftpAddress = APP_PATH + "file/" + time + ".html";
				File file = new File(absoluteAddress);
				
				if (!file.exists())
					file.createNewFile();
				URL url = new URL(address);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				InputStream inStream = connection.getInputStream();
				FileOutputStream fout = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = inStream.read(buffer)) != -1) {
					fout.write(buffer, 0, len);
				}
				connection.disconnect();
				inStream.close();
				fout.flush();
				fout.close();
				cachedList.put(address, ftpAddress);
				MyDBHelper.insertCachedTable(ftpAddress, address);
				urlList.put(ftpAddress,address);
				getRES(absoluteAddress);
			} else {
				String localFTP = cachedList.get(address);
				localFTP = localFTP.replace(APP_PATH + "file/", PAGE_DIR + "/");
				File file = new File(localFTP);
				if (!file.exists()) {
					cachedList.remove(address);
					getHTML(address);
				} else {
					Date dt = new Date(file.lastModified());
					Date current_dt = new Date();
					long time = current_dt.getTime() - dt.getTime();
					if (time / 1000 / 60 / 30 >= 1) {
						file.delete();
						cachedList.remove(address);
						getHTML(address);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * cssTag==1 means css file
	 */
	public static ArrayList<String> urlParser(String address, int cssTag) {
		String urlRegex = "[a-zA-z]+://[^\\s\"')]*";
		if (cssTag == 1){
			urlRegex = "background:url\\([^\\)]*";
		}
		ArrayList<String> res = new ArrayList<String>();
		try {
			BufferedReader bufferReader = new BufferedReader(new FileReader(new File(address)));
			String line = "";
			StringBuffer sBuffer = new StringBuffer();
			while ((line = bufferReader.readLine()) != null) {
				sBuffer.append(line);
			}
			line = sBuffer.toString();
			bufferReader.close();

			Pattern p = Pattern.compile(urlRegex);
			Matcher m = p.matcher(line);
			while (m.find()) {
				String link = m.group();
				if (containsCssJsPic(link)) {
					res.add(link);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
}
