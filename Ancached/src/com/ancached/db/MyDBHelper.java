package com.ancached.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;   
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
  
public class MyDBHelper extends SQLiteOpenHelper{    
    private static final String SQL_NAME = "sdcard/Ancached/data/tracklogs.db";
    private static final String MAIN_DATA_TABLE_NAME = "history";
    private static final String MAIN_DATA_URL = "url";//表的4个字段  
    private static final String MAIN_DATA_TITLE = "title";  
    private static final String MAIN_DATA_VTIME = "vtime";  
    private static final String MAIN_DATA_NET = "net";
    private static final String MAIN_DATA_LOC = "location";
      
    //构造方法  
    public MyDBHelper(Context context) {  
        super(context, SQL_NAME, null, 1);  
    }  
  
    @Override  
    public void onCreate(SQLiteDatabase db) {  
        //建表  
        String mainDataSQL = "create table if not exists " + MAIN_DATA_TABLE_NAME + "("  
                + MAIN_DATA_URL + " nvarchar(200), "  
                + MAIN_DATA_TITLE + " nvarchar(200), "  
                + MAIN_DATA_VTIME + " varchar(100), "  
                + MAIN_DATA_NET + " int, "
        		+ MAIN_DATA_LOC + " nvarchar(200));";
        db.execSQL(mainDataSQL); 
        db.close();
    }
    
    public SQLiteDatabase getDb() {
    	File name = new File(SQL_NAME);
		return SQLiteDatabase.openOrCreateDatabase(name, null);
	}
    
    public List<TrackLogItem> getData() {  
        String mainDataSQL = "select * from "+MAIN_DATA_TABLE_NAME+"";  
        File name = new File(SQL_NAME);  
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(name, null);
        Cursor cursor = db.rawQuery(mainDataSQL, null);   
        List<TrackLogItem> dataList = new ArrayList<TrackLogItem>();  
        if (cursor != null) {   
            while (cursor.moveToNext()) {
            	TrackLogItem data = new TrackLogItem(cursor.getString(0), cursor.getString(1), 
    					cursor.getString(2), cursor.getInt(3), cursor.getString(4));
                dataList.add(data);  
            }  
        }  
        cursor.close();  
        db.close();  
          
        return dataList;  
    } 
    
    public void insertTable(TrackLogItem item){
    	File name = new File(SQL_NAME);
    	SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(name, null);
		String sql = "insert into history values(?,?,?,?,?)";
		Object[] args = new Object[]{item.getUrl(), item.getTitle(), 
				item.getvTime().getStr(), item.getNetState(), item.getLocation()};
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
  
}  
