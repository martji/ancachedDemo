package com.example.ancached;

import java.util.List;
import com.ancached.db.MyDBHelper;
import com.ancached.db.TrackLogItem;
import com.ancached.model.DatabaseHelper;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	Button creatBtn;
	Button webViewBtn;
	TextView mTv;
	MyDBHelper dbHelper;
	
	private Button mTestLocBtn;
	private Button mPoiBtn;
	private LocationClient mLocClient;
	private boolean  mIsStart;
	private Vibrator mVibrator01 =null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTv = (TextView)findViewById(R.id.textview);
		
		DatabaseHelper.checkDir();
		dbHelper = new MyDBHelper(this);
		SQLiteDatabase db = dbHelper.getDb();
		dbHelper.onCreate(db);
		
		mLocClient = ((Location)getApplication()).mLocationClient;
		((Location)getApplication()).mTv = mTv;
		mVibrator01 =(Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE);
		((Location)getApplication()).mVibrator01 = mVibrator01;
		
		creatBtn = (Button)findViewById(R.id.btn_creat);
		webViewBtn = (Button)findViewById(R.id.btn_web);
		mTestLocBtn = (Button) findViewById(R.id.btn_loc);
		mPoiBtn = (Button) findViewById(R.id.btn_poi);
		
		setAction();
	}

	private void setAction() {
		// TODO Auto-generated method stub	
		creatBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				creatTable();
			}
		});
		
		webViewBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!mIsStart) {
					mLocClient.stop();
				}
				setLocationOption(5*60*1000);
				mLocClient.start();
				Intent intent = new Intent(MainActivity.this,
						WebViewActivity.class);
				startActivity(intent);
			}
		});
		
		mTestLocBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!mIsStart) {
					setLocationOption(300);
					mLocClient.start();
					mTestLocBtn.setText("STOP!!");
					mIsStart = true;

				} else {
					mLocClient.stop();
					mTestLocBtn.setText("GetLOC");
					mIsStart = false;
				}
			}
		});
		
		mPoiBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mLocClient.requestPoi();
			}
		});
	}
	

	@SuppressWarnings("unused")
	protected void creatTable() {
		// TODO Auto-generated method stub
		TrackLogItem item = new TrackLogItem("http://m.hao123.com/", 
				"好123导航-上网从这里开始", "2014-3-12-13-21-7", 1);
		dbHelper.insertTable(item);
		List<TrackLogItem> result = dbHelper.getData();	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// 设置相关参数
	private void setLocationOption(int time) {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setPoiExtraInfo(true);
		option.setAddrType("all");
		// 设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
		option.setScanSpan(time);
		// option.setPriority(LocationClientOption.NetWorkFirst); //设置网络优先
		option.setPriority(LocationClientOption.GpsFirst); // 不设置，默认是gps优先
		option.setPoiNumber(10);
		option.disableCache(true);
		mLocClient.setLocOption(option);
	}
}
