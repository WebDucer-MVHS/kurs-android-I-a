package de.mvhs.android.zeiterfassung.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Kurs on 19.10.2016.
 */

public class TimeTable {
    private final static String _CREATE_TABLE =
            "CREATE TABLE [time_data] ([_id] INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , [start_time] TEXT NOT NULL , [end_time] TEXT)";

    public final static String TABLE_NAME = "time_data";

    public final static int ITEM_LIST_ID = 100;
    public final static int ITEM_ID = 101;

    public static void onCreate(SQLiteDatabase db){
        // Erzeigen der Tabelle
        db.execSQL(_CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
