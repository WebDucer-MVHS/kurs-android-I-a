package de.mvhs.android.zeiterfassung.db;

import android.database.sqlite.SQLiteDatabase;

public final class ZeitenTabelle {
	// Klassenvariablen
	private static final String _CREATE_TABLE = "CREATE TABLE zeiten (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
			+ "Startzeit TEXT NOT NULL, Endzeit TEXT)";

	// Scripte für die Erweiterung der Datenbank
	private final static String _UPGRADE_1_TO_2 = "ALTER TABLE zeiten ADD COLUMN comment TEXT;";
	private final static String _UPGRADE_2_TO_3 = "ALTER TABLE zeiten ADD COLUMN pause INTEGER NOT NULL DEFAULT 0;";

	public final static String TABLE_NAME = "zeiten";

	public static final void onCreateTable(SQLiteDatabase db) {
		db.execSQL(_CREATE_TABLE);
	}

	public static final void onUpgradeTable(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		switch (oldVersion) {
		case 1:
			// Upgrade 1
			db.execSQL(_UPGRADE_1_TO_2);

		case 2:
			// Upgrade 2
			db.execSQL(_UPGRADE_2_TO_3);
		}
	}
}
