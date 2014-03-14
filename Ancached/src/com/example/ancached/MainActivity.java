package com.example.ancached;

import com.ancached.db.DBHelper;
import com.ancached.model.DatabaseHelper;
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
	
	Button creatBtn, webViewBtn;
	TextView showLabel;
	DBHelper dbHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		creatBtn = (Button)findViewById(R.id.btn_creat);
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
		
		DatabaseHelper.checkDir();
		DatabaseHelper.checkDb(this);
		
		creatBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showLabel.setText("Test Database!");
				creatTable();
			}
		});
	}

	protected void creatTable() {
		// TODO Auto-generated method stub
//		String fileName = "data/data/cn.trinea.android.demo/databases/google_analytics_v2.db";
//		dbHelper = new DBHelper(fileName, this);
		
		new DatabaseHelper();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
