package com.example.ancached_browser;

import java.util.ArrayList;
import java.util.List;
import com.example.anacched_browser.R;
import com.example.model.CacheManager;
import com.example.model.MyDBHelper;
import com.example.model.TrackLogItem;
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
		webView.loadUrl(url);
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				Log.i("url", url);
				address.setText(url);
				view.loadUrl(url);
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
//	            	//hitPages.add(item);
//	            	//dbHelper.insertTable(item);
//	            	
//	            	//title处理
//	            	
//	            	//网页预测、缓存
//					new Thread(new Runnable() {	
//						@Override					
//						public void run() {
//							// TODO Auto-generated method stub
//							String topic = CacheManager.getTopic(hitPages);
//							List<PageItem> items = CacheManager.topicMap.get(topic);
//							//预测下一个链接
//							String nextUrl = CacheManager.getUrl(items);
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
							String pre_url = CacheManager.getTopic(hitPages);
							Log.i("pre_url", pre_url);
						}
					}).start();
	            	//判断是否为首页，如果是则需保存映射
	            	siteUrl = CacheManager.checkUrl(url, item.getTitle());
	            	if (siteUrl != null){
	            		view.loadUrl("javascript:window.handler.show(document.getElementsByTagName('html')[0].innerHTML);");
	            	}
            	}
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
		String year = Integer.toString(t.year);
		String month = Integer.toString(t.month+1);
		String date = Integer.toString(t.monthDay);
		String hour = Integer.toString(t.hour);
		String minute = Integer.toString(t.minute);
		String second = Integer.toString(t.second);
		stime += year + "-" + month + "-" + date + "-" +
				hour + "-" + minute + "-" + second;
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
