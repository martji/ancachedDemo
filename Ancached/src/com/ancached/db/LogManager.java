package com.ancached.db;


public class LogManager {

	/**
	 * @param args
	 */

	public static void pushLog(TrackLogItem item) {
		// TODO Auto-generated method stub
		DatabaseHelper dbHelper = new DatabaseHelper();
		dbHelper.insertTable(item);
		dbHelper.closeDb();
	}
	
	
}
