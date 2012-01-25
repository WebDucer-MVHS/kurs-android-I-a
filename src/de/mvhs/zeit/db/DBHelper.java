package de.mvhs.zeit.db;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static final int _VERSION = 1;
	private static final String _DB_NAME = "zeiterfassung.db";
	private static final String _DB_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm";
	public static final SimpleDateFormat DB_DATE_FORMAT =
			new SimpleDateFormat(_DB_DATE_TIME_PATTERN);
	
	/**
	 * Konstruktor
	 * @param context
	 */
	public DBHelper(Context context)
	{
		super(context, _DB_NAME, null, _VERSION);
	}
	
	/**
	 * Bei erstellung einer neuen Datenbank
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		ZeiterfassungTable.CreateTable(db);
	}

	/**
	 * Bei Aktualisierung einer vorhandenen Datenbank
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		ZeiterfassungTable.DropTable(db);
		ZeiterfassungTable.CreateTable(db);
	}

}
