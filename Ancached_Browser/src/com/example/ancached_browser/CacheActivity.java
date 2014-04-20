package com.example.ancached_browser;

import com.example.ancached_browser.R;
import com.example.model.CacheHelper;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;

public class CacheActivity extends Activity{
	
	private TextView address;
	private Button refresh;
	private ProgressBar progressBar;
	private WebView webView;

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_webview);
		
		webView = (WebView)findViewById(R.id.webview);
		address = (TextView)findViewById(R.id.address);
		refresh = (Button)findViewById(R.id.btn_refresh);
		progressBar = (ProgressBar)findViewById(R.id.progressBar1);
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		webView.requestFocus();
		settings.setBuiltInZoomControls(true);
		settings.setAppCacheEnabled(true);
		settings.setAllowFileAccess(true);
		settings.setDomStorageEnabled(true);
		settings.setCacheMode(WebSettings.LOAD_DEFAULT);
	    
		String path = Environment.getExternalStorageDirectory().getPath();
		@SuppressWarnings("unused")
		String url = "file:///" + path +"/Ancached_Browser/file/hao123.htm";
		address.setText("Hao123");
		webView.loadUrl("http://m.hao123.com");
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
				Toast.makeText(CacheActivity.this, "Oh no! " + description,
						Toast.LENGTH_SHORT).show();
			}
			
			public void onPageStarted (WebView view, String url, Bitmap favicon){
				super.onPageStarted(view, url, favicon);	
			}
			
			public void onPageFinished (WebView view, String url){
				address.setText(view.getTitle());
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
				new Thread(new Runnable() {	
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String url = "http://i.ifeng.com";
						CacheHelper.getHTML(url);
					}
				}).start();		
			}
		});
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
}
