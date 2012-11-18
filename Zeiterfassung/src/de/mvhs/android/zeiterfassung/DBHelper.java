package de.mvhs.android.zeiterfassung;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private final static String _DB_NAME = "zeiterfassung1a.db";
	private final static int _DB_VERSION = 1;
	
	public DBHelper(Context context) {
		super(context, _DB_NAME, null, _DB_VERSION);
	}

	/**
	 * Neue Datenbank anlegen
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		ZeitTabelle.CreateTable(db);
	}
	
	/**
	 * Datenbank aktualisieren
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		ZeitTabelle.DropTable(db);
		ZeitTabelle.CreateTable(db);
	}

}
