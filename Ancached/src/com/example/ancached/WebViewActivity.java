package com.example.ancached;

import java.util.ArrayList;
import java.util.List;
import com.ancached.db.MyDBHelper;
import com.ancached.db.TrackLogItem;
import com.ancached.model.CacheManager;
import com.ancached.model.Tokenizer;
import com.baidu.location.BDLocation;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class WebViewActivity extends Activity{
	
	private MyDBHelper dbHelper;
	
	private EditText address;
	private Button go;
	private WebView webView, bg_webView;
	
	public static BDLocation location = null;
	
	private List<TrackLogItem> hitPages = null;
	
	private Handler mHandler;
	private static final int PRE_SUCCESS = 0;  
    private static final int PRE_FAILURE = 1;
    private static boolean TOKENIZER_STATE = false;
	
	@SuppressLint("SetJavaScriptEnabled")
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		
		dbHelper = new MyDBHelper(this);
		SQLiteDatabase db = dbHelper.getDb();
		dbHelper.onCreate(db);
		
		address = (EditText)findViewById(R.id.address);
		go = (Button)findViewById(R.id.btnGo);
		webView = (WebView)findViewById(R.id.webView);
		bg_webView = (WebView)findViewById(R.id.bg_webView);
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setBuiltInZoomControls(true);
		settings.setAppCacheEnabled(true);
		settings.setAllowFileAccess(true);
		settings.setDomStorageEnabled(true);
		settings.setCacheMode(WebSettings.LOAD_DEFAULT);
		
		settings = bg_webView.getSettings();
		settings.setAppCacheEnabled(true);
		settings.setAllowFileAccess(true);
		settings.setCacheMode(WebSettings.LOAD_DEFAULT);

//        webView.clearCache(false);
		
		new Thread(new Runnable() {	
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Tokenizer.init();
				TOKENIZER_STATE = true;
			}
		}).start();
		
		mHandler = new Handler() {  
	        public void handleMessage (Message msg) {//此方法在ui线程运行  
	            switch(msg.what) {  
	            case PRE_SUCCESS:
	                bg_webView.loadUrl((String) msg.obj);
	                break;
	            case PRE_FAILURE:
	                break;  
	            }  
	        }  
	    }; 
	    hitPages = new ArrayList<TrackLogItem>();
	    hitPages.add(new TrackLogItem());
	    dbHelper.insertTable(new TrackLogItem());
        address.setText("http://m.hao123.com/");
		go.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				 String url = address.getText().toString();
				 webView.loadUrl(url);
				 webView.setWebViewClient(new WebViewClient(){  
			            @Override  
			            public boolean shouldOverrideUrlLoading(WebView view, String url) {  
			                // TODO Auto-generated method stub  
			            	Log.i("url", url);
			            	address.setText(url);
			                view.loadUrl(url);// 使用当前WebView处理跳转  
			                return true;//true表示此事件在此处被处理，不需要再广播  
			            }  
			            @Override   //转向错误时的处理  
			            public void onReceivedError(WebView view, int errorCode,  
			                    String description, String failingUrl) {
			                // TODO Auto-generated method stub  
			                Toast.makeText(WebViewActivity.this, 
			                		"Oh no! " + description, Toast.LENGTH_SHORT).show();
			            }
			            
			            public void onPageFinished (WebView view, String url){
			            	String page_url = view.getUrl();
			            	String page_title = view.getTitle();
			            	String page_vt = getTime();
			            	int page_netState = getNetState();
			            	String page_loc = getLocation();
			            	TrackLogItem item = new TrackLogItem(page_url, page_title, 
			            			page_vt, page_netState, page_loc);
			            	if (CacheManager.checkItem(TOKENIZER_STATE, hitPages, item) != null){
			            		hitPages.add(item);
			            		dbHelper.insertTable(item);	
		            	
				            	//prefetch thread
				            	new Thread(new Runnable() {
									@SuppressWarnings("unused")
									@Override
									public void run() {
										// TODO Auto-generated method stub
//										List<TrackLogItem> result = dbHelper.getData();
//										String pre_url = CacheManager.getUrl(hitPages, result);
										String pre_url = "sina.cn";
										Log.e("preUrl", pre_url);
										if (pre_url == null){
											mHandler.obtainMessage(PRE_FAILURE).sendToTarget();
											return;
										}
										else {
											mHandler.obtainMessage(PRE_SUCCESS,pre_url).sendToTarget();
										}
									}
								}).start();
			            	}
			            }
			        });  
			}
		});
	}
	@Override   //默认点回退键，会退出Activity，需监听按键操作，使回退在WebView内发生  
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
        // TODO Auto-generated method stub
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }  
        return super.onKeyDown(keyCode, event);
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
	
	public String getLocation(){
		if (location != null){
			return location.getAddrStr();
		}
		return "";
	}
}