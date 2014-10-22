package de.mvhs.android.zeiterfassung.db;

import android.database.sqlite.SQLiteDatabase;

public final class ZeitenTabelle {
	// Klassenvariablen
	private static final String _CREATE_TABLE = "CREATE TABLE zeiten (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
			+ "Startzeit TEXT NOT NULL, Endzeit TEXT)";

	public final static String TABLE_NAME = "zeiten";

	public static final void onCreateTable(SQLiteDatabase db) {
		db.execSQL(_CREATE_TABLE);
	}

	public static final void onUpgradeTable(SQLiteDatabase db, int oldVersion,
			int newVersion) {

	}
}
