package com.example.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.example.struct.TrackLogItem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBHelper extends SQLiteOpenHelper {
	private static final String PATH = "sdcard/Ancached_Browser/data/";
	private static final String SQL_NAME = "sdcard/Ancached_Browser/data/tracklogs.db";
	private static final String MAIN_DATA_TABLE_NAME = "history";
	private static final String CACHE_TABLE_NAME = "cachedlist";
	private static final String MAIN_DATA_OURL = "oriurl";
	private static final String MAIN_DATA_URL = "url";
	private static final String MAIN_DATA_TITLE = "title";
	private static final String MAIN_DATA_VTIME = "vtime";
	private static final String MAIN_DATA_NET = "net";
	private static final String MAIN_DATA_LOC = "location";

	// 构造方法
	public MyDBHelper(Context context) {
		super(context, SQL_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 建表
		String mainDataSQL = "create table if not exists "
				+ MAIN_DATA_TABLE_NAME + "(" + MAIN_DATA_OURL
				+ " nvarchar(200), " + MAIN_DATA_URL + " nvarchar(200), "
				+ MAIN_DATA_TITLE + " nvarchar(200), " + MAIN_DATA_VTIME
				+ " varchar(100), " + MAIN_DATA_NET + " int, " + MAIN_DATA_LOC
				+ " nvarchar(200));";
		db.execSQL(mainDataSQL);

		String cacheSQL = "create table if not exists " + CACHE_TABLE_NAME + "("  
        		+ "ftpaddress nvarchar(200), "
                + "address nvarchar(200));";
        db.execSQL(cacheSQL);
		db.close();
	}

	public SQLiteDatabase getDb() {
		File name = new File(SQL_NAME);
		return SQLiteDatabase.openOrCreateDatabase(name, null);
	}

	public List<TrackLogItem> getData() {
		String mainDataSQL = "select * from " + MAIN_DATA_TABLE_NAME + "";
		File name = new File(SQL_NAME);
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(name, null);
		Cursor cursor = db.rawQuery(mainDataSQL, null);
		List<TrackLogItem> dataList = new ArrayList<TrackLogItem>();
		if (cursor != null) {
			while (cursor.moveToNext()) {
				TrackLogItem data = new TrackLogItem(cursor.getString(0),
						cursor.getString(1), cursor.getString(2),
						cursor.getString(3), cursor.getInt(4),
						cursor.getString(5));
				dataList.add(data);
			}
		}
		cursor.close();
		db.close();

		return dataList;
	}

	public List<TrackLogItem> getData(int size) {
		String mainDataSQL = "select * from " + MAIN_DATA_TABLE_NAME
				+ " order by vtime desc limit " + Integer.toString(size);
		File name = new File(SQL_NAME);
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(name, null);
		Cursor cursor = db.rawQuery(mainDataSQL, null);
		List<TrackLogItem> dataList = new ArrayList<TrackLogItem>();
		if (cursor != null) {
			while (cursor.moveToNext()) {
				TrackLogItem data = new TrackLogItem(cursor.getString(0),
						cursor.getString(1), cursor.getString(2),
						cursor.getString(3), cursor.getInt(4),
						cursor.getString(5));
				dataList.add(data);
			}
		}
		cursor.close();
		db.close();

		return dataList;
	}

	public static void insertCachedTable(String ftpaddress, String address){
    	File name = new File(SQL_NAME);
    	SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(name, null);
		String sql = "insert into cachedlist values(?,?)";
		Object[] args = new Object[]{ftpaddress, address};
		db.execSQL(sql, args);
		db.close();
    }
	public void initUrlList(){
		String sql = "select * from cachedlist";
		File name = new File(SQL_NAME);
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(name, null);
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String ftpaddress = cursor.getString(0);
				String address = cursor.getString(1);
				CacheHelper.urlList.put(ftpaddress, address);
			}
		}
		cursor.close();
		db.close();
	}
	
	
	public void insertTable(TrackLogItem item) {
		File name = new File(SQL_NAME);
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(name, null);
		String sql = "insert into history values(?,?,?,?,?,?)";
		Object[] args = new Object[] { item.getOriurl(), item.getUrl(),
				item.getTitle(), item.getvTime().getStr(), item.getNetState(),
				item.getLocation() };
		db.execSQL(sql, args);
		Log.e("url", item.getUrl());
		Log.e("title", item.getTitle());
		Log.e("vtime", item.getvTime().getStr());
		Log.e("net", Integer.toString(item.getNetState()));
		Log.e("location", item.getLocation());
		db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public static void checkDir() {
		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File dir = new File(PATH);
			if (!dir.exists()) {
				File dir2 = new File("sdcard/Ancached_Browser/");
				if (!dir2.exists()) {
					dir2.mkdir();
				}
				dir.mkdir();
			}
		}
	}
}
