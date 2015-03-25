package de.mvhs.android.zeiterfassung.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by kurs on 25.03.15.
 */
public class ZeitenTable {
    private final static String _CREATE_TABLE = "CREATE TABLE Zeiten "
            + "([_id] INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,"
            + "[start_time] TEXT NOT NULL ,"
            + "[end_time] TEXT)";

    private final static String _DROP_TABLE = "DROP TABLE Zeiten;";

    public static void onCreateTable(SQLiteDatabase db){
        db.execSQL(_CREATE_TABLE);
    }

    public static void onUpdateTable(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(_DROP_TABLE);
        onCreateTable(db);
    }
}
