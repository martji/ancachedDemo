package com.ancached.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import com.example.ancached.R;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
}
