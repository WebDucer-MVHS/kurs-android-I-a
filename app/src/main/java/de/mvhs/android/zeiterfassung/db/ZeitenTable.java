package de.mvhs.android.zeiterfassung.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by kurs on 25.03.15.
 */
public class ZeitenTable {
   /**
    * ID für Liste von Zeiten
    */
   public final static int ITEM_LIST_ID = 100;
   /**
    * ID für einen Zeiten Eintrag
    */
   public final static int ITEM_ID = 101;

   /**
    * ID für den leeren Datansatz
    */
   public final static int EMPTY_ITEM_ID = 102;

   /**
    * Tabellenname
    */
   public final static String TABLE_NAME = "Zeiten";

   private final static String _CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "([_id] INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL," + "[start_time] TEXT NOT NULL," + "[end_time] TEXT," + "[pause] INTEGER NOT NULL DEFAULT 0," + "[comment] TEXT)";

   private final static String _MIGRATE_TO_VERSION_2 = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN [pause] INTEGER NOT NULL DEFAULT 0";

   private final static String _MIGRATE_TO_VERSION_3 = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN [comment] TEXT";

   private final static String _DROP_TABLE = "DROP TABLE " + TABLE_NAME;

   public static void onCreateTable(SQLiteDatabase db) {
      db.execSQL(_CREATE_TABLE);
   }

   public static void onUpdateTable(SQLiteDatabase db, int oldVersion, int newVersion) {
      switch (oldVersion) {
         case 1:
            db.execSQL(_MIGRATE_TO_VERSION_2);
         case 2:
            db.execSQL(_MIGRATE_TO_VERSION_3);
      }
   }
}
