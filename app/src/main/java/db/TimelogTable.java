package db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by kurs on 13.04.16.
 */
public class TimelogTable {
    private static final String _CREATE_TABLE =
            "CREATE TABLE [timelog] ([_id] INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,"
                    + " [start_time] TEXT NOT NULL , [end_time] TEXT,"
                    + " [comment] TEXT, [pause] INTEGER NOT NULL DEFAULT 0)";

    private static final String _MIGRATION_1_TO_2 =
            "ALTER TABLE [timelog] ADD COLUMN [pause] INTEGER NOT NULL DEFAULT 0";

    public final static int ITEM_LIST_ID = 100;
    public final static int ITEM_ID = 101;
    public final static int NOT_FINISHED_ITEM_ID = 102;

    public final static String TABLE_NAME = "timelog";

    public static void createTable(SQLiteDatabase db) {
        db.execSQL(_CREATE_TABLE);
    }

    public static void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion){
        switch (oldVersion){
            case 1:
                // Migration von Version 1 auf 2
                db.execSQL(_MIGRATION_1_TO_2);

            case 2:
                // Migration von Version 2 auf 3
        }
    }
}
