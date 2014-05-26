package com.example.ancached_browser;

import java.util.Date;
import java.util.Iterator;
import com.ancached.params.Params;
import com.ancached.prefetching.Prefetch;
import com.ancached.prefetching.UtilMethods;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.ancached_browser.R;
import com.example.model.CacheHelper;
import com.example.model.CacheManager;
import com.example.model.MyDBHelper;
import com.example.service.MyService;
import com.example.struct.Seed;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

	// Monitor the network
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				State mobile = cm.getNetworkInfo(
						ConnectivityManager.TYPE_MOBILE).getState();
				State wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
						.getState();
				if (wifi == State.CONNECTED) {
					Params.setNET_STATE(1);
				} else if (mobile == State.CONNECTED) {
					Params.setNET_STATE(2);
				}
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mTv = (TextView)findViewById(R.id.textview);
		mTv.setVisibility(View.GONE);
		getDeviceID();	
		
		Intent bindIntent = new Intent(MainActivity.this, MyService.class);
		bindService(bindIntent, connection, BIND_AUTO_CREATE);
		// Network Filter
		IntentFilter netFilter = new IntentFilter();
		netFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mReceiver, netFilter);
		getNetState(); 
		
		new Thread(new Runnable() {	
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Date curDate = new Date(System.currentTimeMillis());			
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
				dbHelper.initUrlList();
				
				CacheHelper.init();
				CacheManager.getModel();
				final String nextUrl = CacheManager.getUrl();
				Log.i("nextUrl", nextUrl);
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						CacheHelper.getHTML("http://" + nextUrl);
						
						Prefetch.setLaunchTag(1);
						Prefetch.setPageType(0);
						String site = UtilMethods.checkSite(nextUrl);
						String initTopic = CacheManager.getTopic(nextUrl);
						Prefetch.setUrl("http://" + nextUrl);
						Prefetch.setSite(site);
						Prefetch.setTopic(initTopic);
						Prefetch.setDescription(initTopic);
						Prefetch.getFeedBack();
						if (Prefetch.getFb().getSortList() != null) {
							Iterator<Seed> iter = Prefetch.getFb().getSortList().iterator();
							int count = 0;int cc = 0;
							while (iter.hasNext() && (count <= 3) && cc < 5) {
								Seed seed = iter.next();
								Prefetch.getFetchedMap().put(seed.getUrl(),
													seed.getData().getDescription());
								Log.i("cached_url", seed.getUrl());
								CacheHelper.getHTML(seed.getUrl());
								cc ++;
								if (Params.getNET_STATE() != 1)
									count++;
							}
							CacheHelper.showFetchedFiles();
						}
					}
				}).start();

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

	private void getNetState() {
		// TODO Auto-generated method stub
		ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		if (wifi == State.CONNECTED) {
			Params.setNET_STATE(1);
		} else if (mobile == State.CONNECTED) {
			Params.setNET_STATE(2);
		}else{
			Params.setNET_STATE(0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onBackPressed() {
		new AlertDialog.Builder(this).setTitle("ȷ���˳���")
			.setIcon(android.R.drawable.ic_dialog_info)
			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					myBinder.saveLogs();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					MainActivity.this.finish();
				}
			})
			.setNegativeButton("����", new DialogInterface.OnClickListener() {
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
        Params.setDEVICE_ID(TelephonyMgr.getDeviceId());
		Prefetch.setDeviceId(Params.getDEVICE_ID());
	}
}
