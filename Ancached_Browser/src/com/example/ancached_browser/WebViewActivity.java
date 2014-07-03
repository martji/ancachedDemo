package com.example.ancached_browser;

import java.util.ArrayList;
import java.util.List;
import com.ancached.params.Params;
import com.ancached.prefetching.Prefetch;
import com.example.ancached_browser.R;
import com.example.model.CacheHelper;
import com.example.model.CacheManager;
import com.example.model.MyDBHelper;
import com.example.struct.TrackLogItem;
import com.example.webservice.WebServiceManager;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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

public class WebViewActivity extends Activity {

	private static final int DEL_MENU_ID = Menu.FIRST;
	private static final int full_MENU_ID = Menu.FIRST + 1;
	private TextView address;
	private Button refresh;
	private ProgressBar progressBar;
	private WebView webView;

	private MyDBHelper dbHelper;
	private static List<TrackLogItem> hitPages = null;

	private final int PREFETCHCOUNTS = 1;
	private List<String> visitedSites;
	private List<String> realRouters;
	private List<String> visitedTitles;
	public static List<String> visitedUrls;

	public static List<TrackLogItem> getHitPages() {
		return hitPages;
	}

	public static String siteUrl = "";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_webview);

		dbHelper = new MyDBHelper(this);
		SQLiteDatabase db = dbHelper.getDb();
		dbHelper.onCreate(db);

		webView = (WebView) findViewById(R.id.webview);
		address = (TextView) findViewById(R.id.address);
		refresh = (Button) findViewById(R.id.btn_refresh);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
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
		visitedSites = new ArrayList<String>();
		realRouters = new ArrayList<String>();
		visitedTitles = new ArrayList<String>();
		visitedUrls = new ArrayList<String>();
		TrackLogItem item = new TrackLogItem();
		hitPages.add(item);
		// dbHelper.insertTable(item);
		Prefetch.setLaunchTag(0);

		String url = "file:///android_asset/homesites.htm";
		address.setText("ÍøÕ¾µ¼º½");
		webView.loadUrl(url);

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.i("url", url);
				address.setText(url);
				String n_url = CacheHelper.getUrl(url);
				visitedUrls.add(n_url);
				n_url = CacheHelper.checkUrl(n_url);
				if (n_url != null) {
					url = CacheHelper.getLocalUrl(n_url);
					view.loadUrl(url);
				} else {
					view.loadUrl(url);
				}
				return true;
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Toast.makeText(WebViewActivity.this, "Oh no! " + description,
						Toast.LENGTH_SHORT).show();
			}

			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				url = CacheHelper.getUrl(url);
				String page_title = CacheManager.getTitle(url);
				String page_vt = getTime();
				int page_netState = Params.getNET_STATE();
				String page_loc = getLocation();
				TrackLogItem item = new TrackLogItem(url, url, page_title,
						page_vt, page_netState, page_loc);
				item = CacheManager.checkItem(hitPages, item);
				if (item != null && page_title != "") {
					hitPages.add(item);
					// dbHelper.insertTable(item);
					getPrefetch(url, item);
				}
			}

			public void onPageFinished(WebView view, String url) {
				address.setText(view.getTitle());
				url = CacheHelper.getUrl(url);
				String page_title = view.getTitle();
				String page_vt = getTime();
				int page_netState = Params.getNET_STATE();
				String page_loc = getLocation();
				TrackLogItem item = new TrackLogItem(url, url, page_title,
						page_vt, page_netState, page_loc);
				item = CacheManager.checkItem(hitPages, item);
				if (item != null) {
					hitPages.add(item);
					// dbHelper.insertTable(item);
					getPrefetch(url, item);
				}
				siteUrl = CacheManager.checkUrl(url, view.getTitle());
				boolean flag = hitPages.get(hitPages.size() - 2).getUrl().contains("hao123");
				if (siteUrl != null && flag) {
					CacheManager.current_site = siteUrl;
					realRouters = new ArrayList<String>();
					realRouters.add(siteUrl);
					Prefetch.setUrl("http://" + siteUrl);	
					if (!visitedSites.contains(siteUrl)) {
						visitedSites.add(siteUrl);
						CacheManager.mapStatus = false;
						view.loadUrl("javascript:window.handler.show(document."
								+ "getElementsByTagName('html')[0].innerHTML);");
						while (!CacheManager.mapStatus) {
							try {
								Thread.sleep(100);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						new Thread(new Runnable() {
							@Override
							public void run() {
								Prefetch.setPageType(0);
								prefetchPages();
							}
						}).start();
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

	protected void getPrefetch(String url, TrackLogItem item) {
		final String isSite = CacheManager.checkUrl(url, item.getTitle());
		if (isSite == null) {
			realRouters.add(item.getUrl());
			visitedTitles.add(item.getTitle());
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (isSite == null && CacheManager.mapStatus) {
					Prefetch.setPageType(1);
					prefetchPages();
				}
			}
		}).start();
	}

	private void prefetchPages() {
		List<String> nextUrls = CacheManager.getUrl(realRouters, hitPages);
		if (nextUrls != null && nextUrls.size() != 0) {
			for (int i = 0, count = 0; i < nextUrls.size()
					&& count < PREFETCHCOUNTS; i++) {
				String nextUrl = nextUrls.get(i);
				if (!CacheHelper.cachedList.containsKey(nextUrl)) {
					if (compareTitle(CacheManager.getTitle(nextUrl))) {
						Log.i("next_title", CacheManager.getTitle(nextUrl));
						CacheHelper.getHTML(nextUrl);
						count++;
					}
				}
			}
			CacheHelper.showFetchedFiles();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, DEL_MENU_ID, 0, R.string.del);
		menu.add(0, full_MENU_ID, 0, R.string.full);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DEL_MENU_ID:
			dbHelper.delUrlList();
			return true;
		case full_MENU_ID:
			if (item.getTitle().equals("È«ÆÁÏÔÊ¾")) {
				this.getWindow().setFlags(
						WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);
				item.setTitle(R.string.unfull);
			} else {
				final WindowManager.LayoutParams attrs = getWindow()
						.getAttributes();
				attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
				getWindow().setAttributes(attrs);
				getWindow().clearFlags(
						WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
				item.setTitle(R.string.full);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class Handler {
		@JavascriptInterface
		public void show(String data) {
			CacheManager.parseSite(WebViewActivity.siteUrl, data);
			CacheManager.mapStatus = true;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private boolean compareTitle(String title) {
		return true;
	}

	public String getTime() {
		String stime = "";
		Time t = new Time();
		t.setToNow();
		String[] date = new String[5];
		String year = Integer.toString(t.year);
		date[0] = Integer.toString(t.month + 1);
		date[1] = Integer.toString(t.monthDay);
		date[2] = Integer.toString(t.hour);
		date[3] = Integer.toString(t.minute);
		date[4] = Integer.toString(t.second);
		for (int i = 0; i < 5; i++) {
			if (date[i].length() == 1) {
				date[i] = "0" + date[i];
			}
		}
		stime += year + "-" + date[0] + "-" + date[1] + " " + date[2] + ":"
				+ date[3] + ":" + date[4];
		return stime;
	}

	public int getNetState() {
		ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		if (wifi == State.CONNECTED) {
			return 1;
		} else if (mobile == State.CONNECTED) {
			return 2;
		}
		return 0;
	}

	public String getLocation() {
		if (MainActivity.location != null) {
			return MainActivity.location.getAddrStr();
		}
		return "";
	}

	public void pushItem(final TrackLogItem item) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<TrackLogItem> items = new ArrayList<TrackLogItem>();
				items.add(item);
				WebServiceManager.pushLog(items);
			}
		}).start();
	}
}
