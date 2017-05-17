package de.mvhs.android.zeiterfassung.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Kurs on 29.03.2017.
 */

public final class TimeTable {
    // Konstanten
    public final static int ITEM_LIST_ID = 100;
    public final static int ITEM_ID = 101;
    public final static int OPEN_ITEM_ID = 102;
    public final static String TABLE_NAME = "time";

    private final static String _CREATE_TABLE =
            "CREATE TABLE [time] ([_id] INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , [start_time] TEXT NOT NULL ,"
                    +"[end_time] TEXT,"
                    + "[pause] INTEGER NOT NULL DEFAULT 0,"
                    + "[comment] TEXT)";

    private final static String _MIGRATE_1_TO_2 = "ALTER TABLE [time] ADD COLUMN [pause] INTEGER NOT NULL  DEFAULT 0";
    private final static String _MIGRATE_2_TO_3 = "ALTER TABLE [time] ADD COLUMN [comment] TEXT";


    public static void onCreate(SQLiteDatabase db){
        db.execSQL(_CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion){
        switch (oldVersion){
            case 1:
                db.execSQL(_MIGRATE_1_TO_2);

            case 2:
                db.execSQL(_MIGRATE_2_TO_3);
        }
    }
}
