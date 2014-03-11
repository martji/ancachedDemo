package com.example.ancached;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
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
	
	private EditText address;
	private Button go;
	private WebView webView;
	
	@SuppressLint("SetJavaScriptEnabled")
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		
		address = (EditText)findViewById(R.id.editText1);
		go = (Button)findViewById(R.id.btnGo);
		webView = (WebView)findViewById(R.id.webView);
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setBuiltInZoomControls(true);
		settings.setAppCacheEnabled(true);
		settings.setAllowFileAccess(true);
		settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        webView.clearCache(false);
		
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
			                view.loadUrl(url);// ʹ�õ�ǰWebView������ת  
			                return true;//true��ʾ���¼��ڴ˴�����������Ҫ�ٹ㲥  
			            }  
			            @Override   //ת�����ʱ�Ĵ���  
			            public void onReceivedError(WebView view, int errorCode,  
			                    String description, String failingUrl) {
			                // TODO Auto-generated method stub  
			                Toast.makeText(WebViewActivity.this, 
			                		"Oh no! " + description, Toast.LENGTH_SHORT).show();  
			            }
			        });  
			}
		});
	}
	@Override   //Ĭ�ϵ���˼������˳�Activity�����������������ʹ������WebView�ڷ���  
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
        // TODO Auto-generated method stub
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }  
        return super.onKeyDown(keyCode, event);
    }
}