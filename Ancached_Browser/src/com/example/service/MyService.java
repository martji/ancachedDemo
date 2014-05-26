package com.example.service;

import java.util.List;
import com.example.ancached_browser.WebViewActivity;
import com.example.struct.TrackLogItem;
import com.example.webservice.WebServiceManager;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {

	public static final String TAG = "MyService";

	private MyBinder mBinder = new MyBinder();

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate() executed");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand() executed");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy() executed");
	}

	public class MyBinder extends Binder {
		public void saveLogs() {
			Log.d(TAG, "saveLogs() executed");
			// 执行具体的任务
			new Thread(new Runnable() {	
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try{
						//CacheHelper.writeBack();
						List<TrackLogItem> items = WebViewActivity.getHitPages();
						if (items.size() > 3){
							WebServiceManager.pushLog(items);
						}
					}catch(Exception e){
						Log.e("webservice_result", "insertError");
					}
				}
			}).start();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}
}