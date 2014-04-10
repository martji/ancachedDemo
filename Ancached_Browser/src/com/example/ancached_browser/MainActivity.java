package com.example.ancached_browser;

import java.util.Date;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.anacched_browser.R;
import com.example.model.CacheManager;
import com.example.model.MyDBHelper;
import com.example.service.MyService;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class MainActivity extends Activity {

	MyDBHelper dbHelper;
	private boolean state = false;
	
	private LocationClient mLocClient;
	private Vibrator mVibrator01;
	TextView mTv;
	public static String deviceID = "";
	public static boolean TOKENIZER_STATE = false;
	public static boolean LOGS_STATE = false;
	public static BDLocation location = null;
	
	private MyService.MyBinder myBinder;
	private ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			myBinder = (MyService.MyBinder) service;
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mTv = (TextView)findViewById(R.id.textview);
		mTv.setVisibility(View.GONE);
		getDeviceID();
		
		Intent bindIntent = new Intent(MainActivity.this, MyService.class);
	    bindService(bindIntent, connection, BIND_AUTO_CREATE);
		
		new Thread(new Runnable() {	
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Date curDate = new Date(System.currentTimeMillis());
//				WebServiceManager.getTokens2("科比大战詹姆斯");
				Date endDate = new Date(System.currentTimeMillis());
				long diff = endDate.getTime() - curDate.getTime();
				Log.e("webservice_result", Long.toString(diff));
			}
		}).start();
		
		new Thread(new Runnable() {	
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mLocClient = ((Location)getApplication()).mLocationClient;
				((Location)getApplication()).mTv = mTv;
				mVibrator01 =(Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE);
				((Location)getApplication()).mVibrator01 = mVibrator01;
				setLocationOption(5*60*1000);
				mLocClient.start();
			}
		}).start();
		
		new Thread(new Runnable() {		
			@Override
			public void run() {
				// TODO Auto-generated method stub
				MyDBHelper.checkDir();
				dbHelper = new MyDBHelper(MainActivity.this);
				SQLiteDatabase db = dbHelper.getDb();
				dbHelper.onCreate(db);
//				List<TrackLogItem> result = dbHelper.getData();
//				CacheManager.trainModel(result);
				CacheManager.getModel();
				state = true;
			}
		}).start();
		
		while(!state){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onBackPressed() {
		new AlertDialog.Builder(this).setTitle("确认退出吗？")
			.setIcon(android.R.drawable.ic_dialog_info)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					myBinder.saveLogs();
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					MainActivity.this.finish();
				}
			})
			.setNegativeButton("返回", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
						
				}
			}).show();
	} 
	
	private void setLocationOption(int time) {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); 
		option.setCoorType("bd09ll"); 
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setPoiExtraInfo(true);
		option.setAddrType("all");
		option.setScanSpan(time);
		option.setPriority(LocationClientOption.GpsFirst);
		option.setPoiNumber(10);
		option.disableCache(true);
		mLocClient.setLocOption(option);
	}
	
	public void getDeviceID() {
		TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE); 
        deviceID  = TelephonyMgr.getDeviceId(); 
	}
}
