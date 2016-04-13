package db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by kurs on 13.04.16.
 */
public class TimelogTable {
    private static final String _CREATE_TABLE =
            "CREATE TABLE [timelog] ([_id] INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,"
                    + " [start_time] TEXT NOT NULL , [end_time] TEXT, [comment] TEXT)";

    public static void createTable(SQLiteDatabase db){
        db.execSQL(_CREATE_TABLE);
    }
}
