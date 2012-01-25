package de.mvhs.zeit.db;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
	
	/**
	 * Auswahl eines Datensatzes
	 */
	private final String SELECT_RECORD_BY_ID = "SELECT " +
		COLUMN_ID + "," +
		COLUMN_START + "," +
		COLUMN_END +
		" FROM " +
		TABLE_ZEITERFASSUNG +
		" WHERE " +
		COLUMN_ID + " =?";
	
	/**
	 * Auswählen aller abgeschlossener Einträge
	 */
	private static final String _SELECT_CLOSED_RECORDS = "SELECT " +
		COLUMN_ID + "," +
		COLUMN_START + "," +
		COLUMN_END +
		" FROM " +
		TABLE_ZEITERFASSUNG +
		" WHERE " +
		COLUMN_END + " IS NOT NULL " +
		" ORDER BY " +
		COLUMN_START;
	
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
		
		ContentValues updateValues = new ContentValues();
		updateValues.put(COLUMN_END, strEnd);
		
		count = db.update(TABLE_ZEITERFASSUNG,
			updateValues,
			COLUMN_ID + "=?",
			new String[]{String.valueOf(id)});
		
		db.close();
		
		return count;
	}
	
	/**
	 * Alle geschlossenen Einträge
	 * @return
	 * Cursor auf die Daten
	 */
	public Cursor GetClosedRecords()
	{
		SQLiteDatabase db = _dbHelper.getReadableDatabase();
		return db.rawQuery(_SELECT_CLOSED_RECORDS, null);
	}
	
	/**
	 * Löschen eines Datensatzes
	 * @param id
	 * ID des zu löschenden Datensatzes
	 */
	public void DeleteRecord(long id)
	{
		SQLiteDatabase db = _dbHelper.getWritableDatabase();
		db.delete(TABLE_ZEITERFASSUNG,
				COLUMN_ID + " = ?",
				new String[]{String.valueOf(id)});
	}
	
	/**
	 * Speicher des Eintrages
	 * @param id
	 * ID des Eintrages
	 * @param dtmStart
	 * Startzeit
	 * @param dtmEnd
	 * Endzeit
	 * @return
	 * Anzahl der Aktualisierungen (OK: 1)
	 */
	public long SaveRecord(long id, Date dtmStart, Date dtmEnd)
	{
		long count = 0;
		
		String strStart = DBHelper.DB_DATE_FORMAT.format(dtmStart);
		String strEnd = DBHelper.DB_DATE_FORMAT.format(dtmEnd);
		
		SQLiteDatabase db = _dbHelper.getWritableDatabase();
		
		ContentValues updateValues = new ContentValues();
		updateValues.put(COLUMN_END, strEnd);
		updateValues.put(COLUMN_START, strStart);
		
		count = db.update(TABLE_ZEITERFASSUNG,
			updateValues,
			COLUMN_ID + "=?",
			new String[]{String.valueOf(id)});
		
		db.close();
		
		return count;
	}
	
	/**
	 * Einen Datensatz laden
	 * @param id
	 * ID des Datensatzes
	 * @return
	 * Cursor auf den Datensatz
	 */
	public Cursor GetRecordById(long id)
	{
		SQLiteDatabase db = _dbHelper.getReadableDatabase();
		
		return db.rawQuery(
				SELECT_RECORD_BY_ID,
				new String[]{String.valueOf(id)});
	}
}