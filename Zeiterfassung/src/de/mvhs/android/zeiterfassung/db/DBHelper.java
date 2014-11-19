package de.mvhs.android.zeiterfassung.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	// Klassenvariablen
	private static final int _DB_VERSION = 3;
	private static final String _DB_NAME = "zeiterfassung.db";

	public DBHelper(Context context) {
		super(context, _DB_NAME, null, _DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		ZeitenTabelle.onCreateTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		ZeitenTabelle.onUpgradeTable(db, oldVersion, newVersion);
	}

}
