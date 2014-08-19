package com.example.module2;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class IDatabase extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "iDatabase";
	private static final String TABLE_SAVED_FILES = "saved_files";
	private static final String KEY_ID = "_id";
	private static final String KEY_NAME = "name";
	private static final String KEY_DATE = "Date";
	private static final String KEY_ENTRIES = "Entries";
	private static final String KEY_ATTRIBUTES = "Attributes";
	
	public static ContentValues eventReg;

	public IDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_SAVED_FILES_TABLE = "CREATE TABLE " + TABLE_SAVED_FILES + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
				+ KEY_DATE + " TEXT," + KEY_ENTRIES + " TEXT," 
				+ KEY_ATTRIBUTES + " TEXT" + ")";
		db.execSQL(CREATE_SAVED_FILES_TABLE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED_FILES);
		onCreate(db);
	}
	
	public void initResources(){
		eventReg = new ContentValues();
        eventReg.put("waitbtn", R.drawable.wait);
        eventReg.put("smsbtn", android.R.drawable.sym_action_chat);
        eventReg.put("callbtn", android.R.drawable.sym_action_call);
        eventReg.put("alertbtn", R.drawable.alert);
        eventReg.put("repeatbtn", R.drawable.repeat);
        eventReg.put("ifbtn", R.drawable.ificon);
        eventReg.put("imosebtn", R.drawable.border);
	}
	
	public int getResourceImage(String rsc){
		return eventReg.getAsInteger(rsc);		
	}

}
