package de.mvhs.android.zeiterfassung.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Kurs on 19.10.2016.
 */

public class TimeTable {
  private final static String _CREATE_TABLE =
      "CREATE TABLE [time_data] ([_id] INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,"
          + "[start_time] TEXT NOT NULL,"
          + " [end_time] TEXT,"
          + " [pause_time] INTEGER NOT NULL DEFAULT 0,"
          + " [comment] TEXT)";

  private final static String _UPGRADE_1_TO_3 =
      "ALTER TABLE [time_data] ADD COLUMN [pause_time] INTEGER NOT NULL  DEFAULT 0";

  private final static String _UPGRADE_3_TO_4 =
      "ALTER TABLE [time_data] ADD COLUMN [comment] TEXT";

  public final static String TABLE_NAME = "time_data";

  public final static int ITEM_LIST_ID = 100;
  public final static int ITEM_ID = 101;
  public final static int NOT_FINISHED_ITEM_ID = 102;

  public static void onCreate(SQLiteDatabase db) {
    // Erzeigen der Tabelle
    db.execSQL(_CREATE_TABLE);
  }

  public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    switch (oldVersion) {
      case 1:
      case 2:
        // Migrationsschritt
        db.execSQL(_UPGRADE_1_TO_3);
      case 3:
        db.execSQL(_UPGRADE_3_TO_4);
    }
  }
}
