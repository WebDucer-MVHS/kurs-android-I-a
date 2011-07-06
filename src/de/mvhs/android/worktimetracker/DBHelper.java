/**
 * 
 */
package de.mvhs.android.worktimetracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * @author kurs
 *
 */
public class DBHelper extends SQLiteOpenHelper {

	/* Klassenvariblen */
	private static final String DB_NAME = "wttdatabase.db";
	private static final int DB_VERSION = 1;
	
	/* Konstruktor */
	public DBHelper(Context context){
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TimeTrackingTable.SQL_CREATE);

	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(TimeTrackingTable.SQL_DROP);
		db.execSQL(TimeTrackingTable.SQL_CREATE);

	}

}
