package com.example.ancached;

import java.util.Vector;
import com.ancached.db.DBHelper;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	Button creatBtn, insertBtn, selectBtn, updateBtn, webViewBtn;
	TextView showLabel;
	DBHelper dbHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		creatBtn = (Button)findViewById(R.id.btn_creat);
		insertBtn = (Button)findViewById(R.id.btn_insert);
		selectBtn = (Button)findViewById(R.id.btn_select);
		updateBtn = (Button)findViewById(R.id.btn_update);
		webViewBtn = (Button)findViewById(R.id.btn_webview);
		showLabel = (TextView)findViewById(R.id.lbl);
		final Context context = this;
		
		webViewBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				 Intent intent = new Intent(context ,WebViewActivity.class);
				 startActivity(intent);
			}
		});
		setAction();
	}

	private void setAction() {
		// TODO Auto-generated method stub
		creatBtn.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showLabel.setText("Creat Table!");
				creatTable();	
			}
		});
		
		insertBtn.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		selectBtn.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showLabel.setText("Select Table!");
				selectTable();
			}
		});
		
		updateBtn.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	protected void creatTable() {
		// TODO Auto-generated method stub
		String fileName = "data/data/cn.trinea.android.demo/databases/google_analytics_v2.db";
		dbHelper = new DBHelper(fileName, this);
	}
	
	protected Vector<String> selectTable() {
		// TODO Auto-generated method stub
		String sql = "select * from android_metadata";
		return dbHelper.sqlexec(sql, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
