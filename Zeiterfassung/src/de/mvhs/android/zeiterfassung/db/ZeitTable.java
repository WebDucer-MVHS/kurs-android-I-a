package de.mvhs.android.zeiterfassung.db;

import android.database.sqlite.SQLiteDatabase;

public class ZeitTable {
	private final static String _CREATE_TABLE = "CREATE TABLE zeit ("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
			+ "start_time TEXT NOT NULL," + "end_time TEXT)";

	/**
	 * ID für die Auflistung URI im Content Provider
	 */
	public final static int ITEMS = 100;
	/**
	 * ID für einen einzelnen Datensatz in Content Proveder
	 */
	public final static int ITEM_ID = 105;

	/**
	 * Name der Tabelle in der Datenbank
	 */
	public final static String TABLE_NAME = "zeit";

	public static void onCreate(SQLiteDatabase db) {
		// Initialisierung der Tabelle (Erstellung)
		db.execSQL(_CREATE_TABLE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		// Migration der Tabelle
	}
}
