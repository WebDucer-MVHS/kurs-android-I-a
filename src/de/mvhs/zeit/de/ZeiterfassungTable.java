package de.mvhs.zeit.de;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class ZeiterfassungTable {
	//
	// Private Felder
	//
	private DBHelper _dbHelper;
	
	//
	// Tabellenname
	//
	/**
	 * Tabellenname
	 */
	public static final String TABLE_ZEITERFASSUNG = "zeiterfassung";
	
	//
	// Spalten
	//
	/**
	 * ID Spalte
	 */
	public static final String COLUMN_ID = "_id";
	
	/**
	 * Start-Spalte
	 */
	public static final String COLUMN_START = "start";
	
	/**
	 * Ende-Spalte
	 */
	public static final String COLUMN_END = "end";
	
	//
	// TSQL Anweisungen
	//
	/**
	 * Löschen der Tabelle
	 */
	private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " +
			TABLE_ZEITERFASSUNG;
	
	/**
	 * Erzeugen der Tabelle
	 */
	private static final String SQL_CREATE_TABLE = "CREATE TABLE " +
		TABLE_ZEITERFASSUNG + "(" +
		COLUMN_ID + " INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ," +
		COLUMN_START + " TEXT NOT NULL ," +
		COLUMN_END + " TEXT)";
	
	//
	// SQL Anweisungen
	//
	/**
	 * Einfügen der Startzeit
	 */
	private static final String _INSERT_START = "INSERT INTO " +
		TABLE_ZEITERFASSUNG + " (" +
		COLUMN_START + ") VALUES " +
		"(?)"; // Platzhalter für den einzufügenden Wert
	
	//
	// Methoden
	//
	/**
	 * Erzeugen der Tabelle
	 * @param db
	 * SQLite Datenbank
	 */
	public static void CreateTable(SQLiteDatabase db)
	{
		db.execSQL(SQL_CREATE_TABLE);
	}
	
	/**
	 * Löschen der Tabelle
	 * @param db
	 * SQLite Datenbank
	 */
	public static void DropTable(SQLiteDatabase db)
	{
		db.execSQL(SQL_DROP_TABLE);
	}
	
	/**
	 * Konstruktor
	 * @param context
	 * Kontext der App
	 */
	public ZeiterfassungTable(Context context)
	{
		_dbHelper = new DBHelper(context);
	}
	
	/**
	 * Speichern der Startzeit in einen neuen Eintrag
	 * @param dtmStart
	 * Sartzeit
	 * @return
	 * ID unter der der neue Eintrag gespeichert wurde
	 */
	public long SaveStartTime(Date dtmStart)
	{
		long id = 0;
		
		String strStart = DBHelper.DB_DATE_FORMAT.format(dtmStart);
		
		SQLiteDatabase db = _dbHelper.getWritableDatabase();
		SQLiteStatement insert = db.compileStatement(_INSERT_START);
		insert.bindString(1, strStart); // Übergabe des zu speichernden Wertes als Parameter !!! 1-basiert
		
		id = insert.executeInsert();
		
		db.close();
		
		return id;
	}
	
//	private static final String _UPDATE_END = "UPDATE " + TABLE_ZEITERFASSUNG +
//		" SET (" +
//		COLUMN_END + " = ?1 )" +
//		" WHERE " + COLUMN_ID + " = ?2";
	/**
	 * Aktualisierung mit der Endzeit
	 * @param id
	 * ID des zu aktualisierenden Eintrages
	 * @param dtmEnd
	 * Endzeit
	 * @return
	 * Anzahl der aktualisierten Einträge
	 */
	public long SaveEndTime(long id, Date dtmEnd)
	{
		long count = 0;
		
		String strEnd = DBHelper.DB_DATE_FORMAT.format(dtmEnd);
		
		SQLiteDatabase db = _dbHelper.getWritableDatabase();
//		SQLiteStatement update = db.compileStatement(_UPDATE_END);
//		update.bindString(1, strEnd);
//		update.bindLong(2, id);
		
//		update.execute();
		
		ContentValues updateValues = new ContentValues();
		updateValues.put(COLUMN_END, strEnd);
		
		count = db.update(TABLE_ZEITERFASSUNG,
			updateValues,
			COLUMN_ID + "=?",
			new String[]{String.valueOf(id)});
		
		db.close();
		
		return count;
	}
}
