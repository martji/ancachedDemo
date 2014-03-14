package com.ancached.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Vector;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
public class DBHelper {

	/**
	 * @param args
	 */
	public final static String URL = "data/data/com.example.ancached/datas";
	private static final String DB_FILE_NAME = "trackLogs.db";
	private String fileName;
	private Context context;
	private SQLiteDatabase db;
	
	
	public DBHelper(String fileName,Context context){
		this.fileName = fileName;
		this.context = context;
		readFile();
		File file = new File(URL, DB_FILE_NAME);
		db = SQLiteDatabase.openOrCreateDatabase(file, null);
	}

	@SuppressWarnings("deprecation")
	public Boolean readFile(){
		Boolean res = false;
		try {
			FileInputStream fin = new FileInputStream(this.fileName);
			FileOutputStream fos = this.context.openFileOutput(DB_FILE_NAME, Context.MODE_WORLD_READABLE);
			byte[] buffer = new byte[1024 * 1024];
			int count = 0;
			while ((count = fin.read(buffer)) > 0){
				fos.write(buffer, 0, count);
			}
			res = true;
			fos.close();
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public Vector<String> sqlexec(String sql, String[] args) {
		Vector<String> result = new Vector<String>();
		Cursor cursor = db.rawQuery(sql, args);
		while(cursor.moveToNext()){
			result.add(cursor.toString());
		}
		return result;
	}
}
