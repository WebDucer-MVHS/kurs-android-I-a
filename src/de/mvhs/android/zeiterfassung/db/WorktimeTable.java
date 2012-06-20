package de.mvhs.android.zeiterfassung.db;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class WorktimeTable {
	// Variablen
	/**
	 * Tabellenname
	 */
	public static final String TABLE_NAME = "worktime";
	/**
	 * ID Spalte
	 */
	public static final String COLUMN_ID = "_id";
	/**
	 * Startzeit Spalte
	 */
	public static final String COLUMN_START_TIME = "start_time";
	/**
	 * Endzeit Spalte
	 */
	public static final String COLUMN_END_TIME = "end_time";
	
	/**
	 * Script zum erstellen der Tabelle
	 */
	public static final String CREATE_TABLE = "CREATE TABLE "
		+ TABLE_NAME
		+ " ("
		+ COLUMN_ID
		+ " INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , "
		+ COLUMN_START_TIME
		+ " TEXT NOT NULL , "
		+ COLUMN_END_TIME
		+ " TEXT)";
	/**
	 * Script zum Löschen der Tabelle
	 */
	public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
	
	private final Context _CONTEXT;
	
	/**
	 * Standard-Constructor
	 * @param context
	 * App Context
	 */
	public WorktimeTable(Context context){
		_CONTEXT = context;
	}
	
	// Public Methoden
	/**
	 * Anlegen eines neuen Datensatzes
	 * @param startTime Startzeit
	 * @return
	 * ID des neuen Datensatzes
	 */
	public long saveWorktime(Date startTime){
		long returnValue = -1;
		
		ContentValues values = new ContentValues();
		values.put(WorktimeTable.COLUMN_START_TIME,
			DBHelper.DB_DATE_FORMAT.format(startTime));
		
		DBHelper helper = new DBHelper(_CONTEXT);
		SQLiteDatabase db = helper.getWritableDatabase();
		returnValue = db.insert(WorktimeTable.TABLE_NAME, null, values);
		
		// Schließen der Datenbankverbindung
		db.close();
		helper.close();
		
		return returnValue;
	}
	
	/**
	 * Aktualisieren eines Datensatzes mit Endzeit
	 * @param endTime
	 * Endzeit
	 * @param id
	 * ID des zu aktualisierenden Datensatzes
	 * @return
	 * Anzahl der aktualiserten Datensätze
	 */
	public int updateWorktime(long id, Date endTime){
		return -1;
	}
	
	/**
	 * ID eines offenen Datensatzes
	 * @return
	 * ID des gefundenen Datensatzes (-1, wenn keins gefunden wurde)
	 */
	public long getOpenWorktime(){
		return -1;
	}
	
	/**
	 * Startzeit des Datensatzes holen
	 * @param id
	 * ID des Datensatzes
	 * @return
	 * Startdatum, null wenn keins gefunden wurde
	 */
	public Date getStartDate(long id){
		return new Date();
	}
}