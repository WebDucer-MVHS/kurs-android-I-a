package de.mvhs.android.zeiterfassung.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by kurs on 14.10.15.
 */
public final class ZeitenTabelle {
    // Klassenvariablen
    private static final String _CREATE_TABLE =
            "CREATE TABLE [zeit] ([_id] INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,"
                    + "[StartZeit] TEXT NOT NULL , [EndZeit] TEXT)";

    public final static int ITEM_LIST_ID = 100;
    public final static int ITEM_ID = 101;
    public final static String TABLE_NAME = "zeit";

    public static void createTable(SQLiteDatabase db){
        // Erzeugen der Tabelle
        db.execSQL(_CREATE_TABLE);
    }

    public static void upgradeTable(SQLiteDatabase db, int oldVersion){
        // Migration
    }
}
