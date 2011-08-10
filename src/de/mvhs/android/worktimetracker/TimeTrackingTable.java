package de.mvhs.android.worktimetracker;

public class TimeTrackingTable {
	/*
	 * Tabellenname
	 */
	public static final String TABLE_NAME = "time_tracking";
	
	/*
	 * Spalten
	 */
	public static final String ID = "_id";
	public static final String START_TIME = "start_time";
	public static final String END_TIME = "end_time";
	
	/*
	 * Datenbank Definition
	 */
	// Erzeigen der Tabelle
	public static final String SQL_CREATE = "CREATE TABLE " +
			TABLE_NAME + "(" +
			ID + " INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE ," +
			START_TIME + " TEXT," +
			END_TIME + " TEXT)";
	
	// Lšschen der Tabelle
	public static final String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;
	
	/*
	 * Aktione auf der Datenbank
	 */
	public static final String SQL_SELECT_BY_ID = "SELECT * " +
		" FROM " + TABLE_NAME +
		" WHERE " + ID + " = ?";
	
	public static final String SQL_LAST_UNCOMPLETED = "SELECT * " +
		" FROM " + TABLE_NAME +
		" WHERE " + END_TIME + " IS NULL";
	
	public static final String SQL_INSERT_START_DATE = "INSERT INTO " +
		TABLE_NAME + "(" +
			START_TIME + ")" +
		" VALUES(?)";
	
	public static final String SQL_LIST_RECORDS = "SELECT " +
		ID + "," +
		START_TIME + "," +
		END_TIME +
		" FROM " +
		TABLE_NAME +
		" WHERE " +
		END_TIME + " IS NOT NULL" +
		" ORDER BY " +
		START_TIME + " DESC";
}
