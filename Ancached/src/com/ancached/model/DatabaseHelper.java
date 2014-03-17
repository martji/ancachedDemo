package com.ancached.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import com.ancached.db.TrackLogItem;
import com.example.ancached.R;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseHelper {

	private static final String PATH = "sdcard/Ancached/data/";
	private static final String DBNAME = "tracklogs.db";
	private SQLiteDatabase db;
	/**
	 * @param args
	 */
	public DatabaseHelper(){
		this.setDb(openDatabase());
	}
	
	public SQLiteDatabase openDatabase() {
		SQLiteDatabase database = null;
		String dbpath = PATH + DBNAME;
		database = SQLiteDatabase.openOrCreateDatabase(dbpath, null);
		return database;
	}

	public Vector<String> sqlexec(String sql, String[] args) {
		Vector<String> result = new Vector<String>();
		Cursor cursor = db.rawQuery(sql, args);
		while(cursor.moveToNext()){
			result.add(cursor.toString());
		}
		return result;
	}
	
	public void insertTable(TrackLogItem item){
		String sql = "insert into tracklog values(?,?,?,?)";
		Object[] args = new Object[]{item.getUrl(), item.getTitle(), 
				item.getvTime().getStr(), item.getNetState()};
		Log.e("url", item.getUrl());
		Log.e("title", item.getTitle());
		Log.e("vtime", item.getvTime().getStr());
		Log.e("net", Integer.toString(item.getNetState()));
		db.execSQL(sql, args);
	}
	
	public Vector<TrackLogItem> selectTable() {
		String sql = "select * from tracklog";
		Vector<TrackLogItem> items = new Vector<TrackLogItem>();
		Cursor cursor = db.rawQuery(sql, null);
		while(cursor.moveToNext()){
			items.add(new TrackLogItem(cursor.getString(0), cursor.getString(1), 
					cursor.getString(2), cursor.getInt(3)));
		}
		return items;
	}
	
	public SQLiteDatabase getDb() {
		return db;
	}

	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}
	
	public static void checkDir() {
		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File dir = new File(PATH);
			if (!dir.exists()) {
				File dir2 = new File("sdcard/Ancached/");
				if(!dir2.exists()){
					dir2.mkdir();
				}
				dir.mkdir();
			}
		}
	}
	
	public static void checkDb(Context context){
		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			String dbpath = PATH + DBNAME;
			if (!new File(dbpath).exists()) {
				try {
					InputStream fin = context.getResources().openRawResource(
							R.raw.tracklogs);
					FileOutputStream fos = new FileOutputStream(dbpath);
					byte[] buffer = new byte[1024 * 1024];
					int count = 0;
					while ((count = fin.read(buffer)) > 0) {
						fos.write(buffer, 0, count);
					}
					fos.close();
					fin.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void closeDb() {
		// TODO Auto-generated method stub
		this.db.close();
	}
}
