package de.mvhs.android.zeiterfassung.db;

import android.database.sqlite.SQLiteDatabase;

public class ZeitTable {
	private final static String _CREATE_TABLE = "CREATE TABLE zeit ("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
			+ "start_time TEXT NOT NULL," + "end_time TEXT,"
			+ "pause INTEGER NOT NULL DEFAULT 0," + "comment TEXT)";

	public static void onCreate(SQLiteDatabase db) {
		// Initialisierung der Tabelle (Erstellung)
		db.execSQL(_CREATE_TABLE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		// Migration der Tabelle
	}
}
