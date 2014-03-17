package com.example.cache;

import java.util.Arrays;
import java.util.LinkedList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.ancached.R;

public class MainActivity extends Activity {

	private static final String[] listName = { "HttpCache Demo",
			"ImageCache Demo","Prefetching" };// ¹¦ÄÜÃû
	private static final int listNo = listName.length - 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.huang_activity_main);
		// root.upgradeRootPermission(getPackageCodePath());
		LinkedList<String> mListItems = new LinkedList<String>();
		mListItems.addAll(Arrays.asList(listName));
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mListItems);
		ListView demoListView = (ListView) findViewById(R.id.listView1);
		demoListView.setAdapter(adapter);
		demoListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (arg2 == listNo - 2) {
					startActivity(HttpCache.class);
				} else if (arg2 == listNo - 1) {
					startActivity(ImageCacheDemo.class);
				} else if (arg2 == listNo) {
					startActivity(Prefetching.class);
				}
			}

		});
	}

	private void startActivity(Class<?> cls) {
		Intent intent = new Intent(MainActivity.this, cls);
		startActivity(intent);
	}
}
