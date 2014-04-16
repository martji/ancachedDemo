package com.example.ancached_browser;

import java.util.ArrayList;
import java.util.List;
import com.example.anacched_browser.R;
import com.example.model.CacheHelper;
import com.example.model.CacheManager;
import com.example.model.MyDBHelper;
import com.example.struct.TrackLogItem;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;

public class WebViewActivity extends Activity{
	
	private TextView address;
	private Button refresh;
	private ProgressBar progressBar;
	private WebView webView;
	
	private MyDBHelper dbHelper;
	private static List<TrackLogItem> hitPages = null;

	public static List<TrackLogItem> getHitPages() {
		return hitPages;
	}
	public static String siteUrl = "";

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_webview);
		
		dbHelper = new MyDBHelper(this);
		SQLiteDatabase db = dbHelper.getDb();
		dbHelper.onCreate(db);
		
		webView = (WebView)findViewById(R.id.webview);
		address = (TextView)findViewById(R.id.address);
		refresh = (Button)findViewById(R.id.btn_refresh);
		progressBar = (ProgressBar)findViewById(R.id.progressBar1);
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		webView.addJavascriptInterface(new Handler(), "handler");
		webView.requestFocus();
		settings.setBuiltInZoomControls(true);
		settings.setAppCacheEnabled(true);
		settings.setAllowFileAccess(true);
		settings.setDomStorageEnabled(true);
		settings.setCacheMode(WebSettings.LOAD_DEFAULT);
		
		hitPages = new ArrayList<TrackLogItem>();
	    hitPages.add(new TrackLogItem());
	    dbHelper.insertTable(new TrackLogItem());
	    
		String url = "http://m.hao123.com";
		address.setText("Hao123");
		webView.loadUrl(url);//此处可修改成加载静态页面
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				Log.i("url", url);
				address.setText(url);
				String n_url = CacheHelper.getUrl(url);
				if (CacheHelper.checkUrl(n_url)){
					//load data from cache
				}
				else {
					view.loadUrl(url);
				}
				return true;
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// TODO Auto-generated method stub
				Toast.makeText(WebViewActivity.this, "Oh no! " + description,
						Toast.LENGTH_SHORT).show();
			}
			
			public void onPageStarted (WebView view, String url, Bitmap favicon){
				super.onPageStarted(view, url, favicon);	
//				//开始加载页面的时候记录
//				url = CacheHelper.getUrl(url);
//				String page_title = CacheManager.getTitle(url);
//				String page_vt = getTime();
//            	int page_netState = getNetState();
//            	String page_loc = getLocation();
//            	TrackLogItem item = new TrackLogItem(url, page_title, 
//            			page_vt, page_netState, page_loc);
//				
//				//item检查、记录、包括预处理、title的处理
//				item = CacheManager.checkItem(hitPages, item);
//            	if (item != null){
//	            	hitPages.add(item);
//	            	//dbHelper.insertTable(item);
//	            	
//	            	//title处理
//	            	
//	            	//网页预测、缓存
//					new Thread(new Runnable() {	
//						@Override					
//						public void run() {
//							// TODO Auto-generated method stub
//							//预测下一个链接
//							String nextUrl = CacheManager.getUrl(hitPages);
//							Log.i("nextUrl", nextUrl);
//							
//							//缓存网页内容
//						}
//					}).start();
//            	}
			}
			
			public void onPageFinished (WebView view, String url){
				address.setText(view.getTitle());
				String page_url = view.getUrl();
            	String page_title = view.getTitle();
            	String page_vt = getTime();
            	int page_netState = getNetState();
            	String page_loc = getLocation();
            	TrackLogItem item = new TrackLogItem(page_url, page_title, 
            			page_vt, page_netState, page_loc);
            	item = CacheManager.checkItem(hitPages, item);
            	if (item != null){
	            	hitPages.add(item);
	            	dbHelper.insertTable(item);
            	
	            	new Thread(new Runnable() {	
						@Override
						public void run() {
							// TODO Auto-generated method stub
							String nextUrl = CacheManager.getUrl(hitPages);
							Log.i("nextUrl", nextUrl);
						}
					}).start();
	            	//判断是否为首页，如果是则需保存映射
	            	siteUrl = CacheManager.checkUrl(url, item.getTitle());
	            	if (siteUrl != null){
	            		view.loadUrl("javascript:window.handler.show(document.getElementsByTagName('html')[0].innerHTML);");
	            	}
            	}
            	
//            	siteUrl = CacheManager.checkUrl(url, view.getTitle());
//            	if (siteUrl != null){
//            		view.loadUrl("javascript:window.handler.show(document.getElementsByTagName('html')[0].innerHTML);");
//            	}
            }
		});
		
		webView.setWebChromeClient(new WebChromeClient() {
			@SuppressWarnings("static-access")
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					progressBar.setVisibility(view.GONE);
				} else {
					if (progressBar.getVisibility() == view.GONE)
						progressBar.setVisibility(view.VISIBLE);
					progressBar.setProgress(newProgress);
				}
				super.onProgressChanged(view, newProgress);
		    } 
		});
		
		refresh.setOnClickListener(new OnClickListener() {	
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String url = (String) address.getText();
				webView.loadUrl(url);
			}
		});
	}
	
	class Handler {
		@JavascriptInterface
		public void show(String data) {
			CacheManager.parseSite(WebViewActivity.siteUrl, data);
		}
	}
	
	@Override   
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
        // TODO Auto-generated method stub
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }  
        return super.onKeyDown(keyCode, event);
    }
	
	public String getTime(){
		String stime = "";
		Time t=new Time();
		t.setToNow();
		String[] date = new String[5];
		String year = Integer.toString(t.year);
		date[0] = Integer.toString(t.month+1);
		date[1] = Integer.toString(t.monthDay);
		date[2] = Integer.toString(t.hour);
		date[3] = Integer.toString(t.minute);
		date[4] = Integer.toString(t.second);
		for (int i = 0; i < 5; i++){
			if (date[i].length() == 1){
				date[i] = "0" + date[i];
			}
		}
		stime += year + "-" + date[0] + "-" + date[1] + " " +
				date[2] + ":" + date[3] + ":" + date[4];
		return stime;
	}
	
	public int getNetState(){
		ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if(wifi==State.CONNECTED){
        	return 1;
        }
        else if(mobile==State.CONNECTED){
        	return 2;
        }
		return 0;
	}
	
	public String getLocation(){
		if (MainActivity.location != null){
			return MainActivity.location.getAddrStr();
		}
		return "";
	}
}
