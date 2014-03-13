package com.example.cache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * db helper
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-10-21
 */
public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(Context context){
        super(context, DbConstants.DB_NAME, null, DbConstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
        	//Image Cache
            db.execSQL(DbConstants.CREATE_IMAGE_SDCARD_CACHE_TABLE_SQL.toString());
            db.execSQL(DbConstants.CREATE_IMAGE_SDCARD_CACHE_TABLE_INDEX_SQL.toString());
            //Http Cache
            db.execSQL(DbConstants.CREATE_HTTP_CACHE_TABLE_SQL.toString());//HttpCache table °üÀ¨table name ID url response exipres create_time type
            db.execSQL(DbConstants.CREATE_HTTP_CACHE_TABLE_INDEX_SQL.toString());//HttpCache Index table  
            db.execSQL(DbConstants.CREATE_HTTP_CACHE_TABLE_UNIQUE_INDEX.toString());//HttpCache Unique Index table
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
