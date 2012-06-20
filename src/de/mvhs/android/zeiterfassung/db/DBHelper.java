package de.mvhs.android.zeiterfassung.db;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	// Variablen
	private static final String DATABASE_NAME = "database.db";
	private static final int DATABASE_VERSION = 1;
	/**
	 * Formater für die Formatierung und Parsen des Datums aus und in die Datenbank
	 */
	public static final SimpleDateFormat DB_DATE_FORMAT =
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

	/**
	 * Standard-Constructor
	 * @param context
	 * App Context
	 */
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(WorktimeTable.CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(WorktimeTable.DROP_TABLE);
		onCreate(db);
	}

}
