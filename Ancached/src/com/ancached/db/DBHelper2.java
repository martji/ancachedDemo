package com.ancached.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import android.content.Context;

public class DBHelper2 {
	public static final String DATABASE_FILENAME = "google_analytics_2v.db";
	public static final String PACKAGE_NAME = "cn.trineaandroid.demo/";
	public static final String DATABASE_BASE = "data/data/";
	public static final String DATABASE_CACHE = "data/cache/";
	private final Context mCtx;
	
	public DBHelper2(Context ctx) {
		this.mCtx = ctx;
	}
	
	public String GetDataBasePath() {
		// String packageName = context.getPackageName();
		// Log.i("PackName", packageName);
		// String DB_PATH = String.format("/data/data/%1$s/databases/",
		// packageName);
		String DB_PATH = DATABASE_BASE + PACKAGE_NAME + DATABASE_FILENAME;
		String DB_NAME = DATABASE_CACHE + PACKAGE_NAME + DATABASE_FILENAME;
		if ((new File(DB_NAME)).exists() == false) {
			try {
				File f = new File(DB_NAME);
				if (!f.exists()) {
					f.mkdir();
				}
				InputStream is = this.mCtx.getAssets().open(DB_PATH);
				OutputStream os = new FileOutputStream(DB_NAME);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = is.read(buffer)) > 0) {
					os.write(buffer, 0, length);
				}
				os.flush();
				os.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return DB_PATH;
	}
}
