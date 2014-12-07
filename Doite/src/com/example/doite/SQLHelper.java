package com.example.doite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLHelper extends SQLiteOpenHelper {

	
	private static final int version = 7;
	private static final String SQL = "create table event ("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "ename varchar(200) ,"
			+ "edate varchar(50),"
			+ "etime varchar(50),"
			+ "eflag integer default 1"
			+ ")";
	
	public SQLHelper(Context context ) {
		super(context, "event", null, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String sql = "DROP TABLE IF EXISTS event";  
        db.execSQL(sql);  
        db.execSQL(SQL);
	}

}
