package de.mvhs.android.zeiterfassung.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Kurs on 29.03.2017.
 */

public final class TimeTable {
    // Konstanten
    private final static String _CREATE_TABLE =
            "CREATE TABLE [time] ([_id] INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , [start_time] TEXT NOT NULL , [end_time] TEXT)";

    public static void onCreate(SQLiteDatabase db){
        db.execSQL(_CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion){

    }
}
