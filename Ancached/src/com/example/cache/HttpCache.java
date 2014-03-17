package com.example.cache;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.example.ancached.R;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class HttpCache extends Activity {
	private Map<String, HttpResponse> cache;// 存储cache
	private EditText httpUrl;
	private Button httpGet;
	private TextView httpCacheInfo;
	private TextView httpCacheContent;
	private int type = -1;
	private HttpCacheDao httpCacheDao;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_httpcache);
		Context context = getApplicationContext();
		cache = new ConcurrentHashMap<String, HttpResponse>();// 解决死锁
		httpCacheDao=new HttpCacheDaoImpl(SqliteUtils.getInstance(context));
		
		new DbHelper(context);

		httpUrl = (EditText) findViewById(R.id.http_cache_url);
		httpGet = (Button) findViewById(R.id.http_cache_get);
		httpCacheInfo = (TextView) findViewById(R.id.http_cache_info);
		httpCacheContent = (TextView) findViewById(R.id.http_cache_content);
		httpGet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String url = httpUrl.getText().toString();
				url = StringUtils.isEmpty(url) ? httpUrl.getHint().toString()
						: url;
				httpGet(url, new HttpCacheListener() {

					protected void onPreGet() {
						httpCacheInfo.setText("");
						httpCacheContent.setText("waiting......");
					}

					@SuppressWarnings("deprecation")
					protected void onPostGet(HttpResponse httpResponse,
							boolean isInCache) {
						if (httpResponse != null) {
							StringBuilder sb = new StringBuilder(256);
							sb.append("is in Cache:").append(isInCache)
									.append("\r\n");
							if (isInCache) {
								sb.append("expires:")
										.append(new Date(httpResponse
												.getExpiredTime())
												.toGMTString()).append("\r\n");
							}
							httpCacheInfo.setText(sb.toString());
							httpCacheContent.setText(httpResponse
									.getResponseBody());
						} else {
							httpCacheInfo.setText("");
							httpCacheContent.setText("response is null.");
						}
					}
				});
			}

		});
	}

	public HttpResponse httpGet(HttpRequest request) {// 获取页面
		String url;
		if (request == null || StringUtils.isEmpty(url = request.getUrl())) {
			return null;
		}
		HttpResponse cacheResponse = null;
		boolean isNoCache = false, isNoStore = false;
		Log.v("mark", "signpost 0");
		String requestCacheControl = request
				.getRequestProperty(HttpConstants.CACHE_CONTROL);

		if (!StringUtils.isEmpty(requestCacheControl)) {
			String[] requestCacheControls = requestCacheControl.split(",");
			if (!ArrayUtils.isEmpty(requestCacheControls)) {
				List<String> requestCacheControlList = new ArrayList<String>();
				for (String s : requestCacheControls) {
					if (s == null) {
						continue;
					}
					requestCacheControlList.add(s.trim());// trim()不错的方法，android
															// 4.3后存在的新机制trim
				}
				if (requestCacheControlList.contains("no-cache")) {
					isNoCache = true;
				}
				if (requestCacheControlList.contains("no-store")) {
					isNoStore = true;
				}
			}
		}
		if (!isNoCache) {
			Log.v("mark","signpost 1");
			cacheResponse = getFromCache(url);// 从缓存中获取
			Log.v("mark","signpost 3");
		}
		return cacheResponse == null ? (isNoStore ? HttpUtils.httpGet(url)
				: putIntoCache(HttpUtils.httpGet(url))) : cacheResponse;
	}

	public HttpResponse httpGet(String url) {
		return httpGet(new HttpRequest(url));
	}

	public HttpResponse httpGet(String url, HttpRequest request) {// 获取页面
		return null;
	}

	public void httpGet(String url, HttpCacheListener listener) {
		new HttpCacheStringAsyncTask(listener).execute(url);
	}

	private static abstract class HttpCacheListener {
		protected void onPreGet() {

		}

		protected void onPostGet(HttpResponse httpResponse, boolean isIncache) {

		}
	}

	private class HttpCacheStringAsyncTask extends
			AsyncTask<String, Void, HttpResponse> {

		private HttpCacheListener listener;

		public HttpCacheStringAsyncTask(HttpCacheListener listener) {
			this.listener = listener;
		}

		@Override
		protected HttpResponse doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			// Log.v("debug", "doInBackGround");
			if (ArrayUtils.isEmpty(arg0)) {
				return null;
			}
			return httpGet(arg0[0]);
		}

		protected void onPreExecute() {
			if (listener != null) {
				listener.onPreGet();
			}
		}

		protected void onPostExecute(HttpResponse httpResponse) {
			if (listener != null) {
				listener.onPostGet(httpResponse, httpResponse == null ? false
						: httpResponse.isInCache());
			}
		}
	}

	private HttpResponse putIntoCache(HttpResponse httpResponse) {
		Log.v("putIntoCache","running");
		String url;
		if (httpResponse == null || (url = httpResponse.getUrl()) == null) {
			return null;
		}
		if (type != -1 && type == httpResponse.getType()) {
			cache.put(url, httpResponse);
		}
		return (httpCacheDao.insertHttpResponse(httpResponse) == -1) ? null
				: httpResponse;
	}

	private HttpResponse getFromCache(String url) {
		if (StringUtils.isEmpty(url)) {
			return null;
		}

		HttpResponse cacheResponse = cache.get(url);
		Log.v("mark","signpost 2");
		if (cacheResponse == null) {
			cacheResponse = httpCacheDao.getHttpResponse(url);
			Log.v("cacheResponse","null");
		}
		return (cacheResponse == null || cacheResponse.isExpired()) ? null
				: cacheResponse.setInCache(true);
	}
}
