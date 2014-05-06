package de.mvhs.android.zeiterfassung.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	// Datenbank-Datei-Name
	private final static String _DB_NAME = "zeit.db";

	// Aktuelle Datenbank-Version (benötigte Version)
	private final static int _DB_VERSION = 3;

	public DBHelper(Context context) {
		super(context, _DB_NAME, null, _DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		ZeitTable.onCreate(db);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		ZeitTable.onUpgrade(db, oldVersion, newVersion);
	}

}
