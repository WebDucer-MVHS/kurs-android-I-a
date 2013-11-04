package de.mvhs.android.zeiterfassung.db;

import android.database.sqlite.SQLiteDatabase;

public class ZeitenTable {
	/* Statische Felder */
	/**
	 * Tabellenname
	 */
	public final static String TABLE_NAME = "zeiten";

	public final static int ITEMS = 100;

	public final static int ITEM_ID = 110;

	/**
	 * ID Spalte
	 */
	public final static String ID = "_id";

	/**
	 * Startzeit Spalte im Format "2013-10-37T18:17"
	 */
	public final static String START = "start";

	/**
	 * Endzeit Spalte im Format "2013-10-37T18:17"
	 */
	public final static String END = "end";

	private final static String _CREATE_TABLE = "CREATE TABLE zeiten "
			+ "(_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,"
			+ "start TEXT NOT NULL ," + "end TEXT)";

	private final static String _DROP_TABLE = "DROP TABLE " + TABLE_NAME;

	/**
	 * Erstellen der Tabelle
	 * 
	 * @param db
	 *            Datenbak
	 */
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(_CREATE_TABLE);
	}

	/**
	 * Aktualisieren der Tabelle
	 * 
	 * @param db
	 *            Datenbank
	 * @param oldVersion
	 *            aktueller Version der Datenbank
	 * @param newVersion
	 *            benötigte Version der Datenbank
	 */
	public static void onUpdate(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		db.execSQL(_DROP_TABLE);
		onCreate(db);
	}
}
