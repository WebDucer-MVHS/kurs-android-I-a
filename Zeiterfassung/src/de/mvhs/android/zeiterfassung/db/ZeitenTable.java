package de.mvhs.android.zeiterfassung.db;

import android.database.sqlite.SQLiteDatabase;

public class ZeitenTable {
  /* Statische Felder */
  /**
   * Tabellenname
   */
  public final static String  TABLE_NAME          = "zeiten";

  public final static int     ITEMS               = 100;

  public final static int     ITEM_ID             = 110;

  /**
   * Kommentar Spalte für den Eintrag
   */
  public final static String  COMMENT             = "comment";

  private final static String _CREATE_TABLE       = "CREATE TABLE zeiten " + "(_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ," + "start TEXT NOT NULL ,"
                                                          + "end TEXT," + "pause INTEGER NOT NULL DEFAULT 0," + "comment TEXT)";

  private final static String _ADD_PAUSE_COLUMN   = "ALTER TABLE zeiten " + "ADD COLUMN pause INTEGER NOT NULL DEFAULT 0";
  private final static String _ADD_COMMENT_COLUMN = "ALTER TABLE zeiten " + "ADD COLUMN comment TEXT";

  /**
   * Erstellen der Tabelle
   * 
   * @param db
   *          Datenbak
   */
  public static void onCreate(SQLiteDatabase db) {
    db.execSQL(_CREATE_TABLE);
  }

  /**
   * Aktualisieren der Tabelle
   * 
   * @param db
   *          Datenbank
   * @param oldVersion
   *          aktueller Version der Datenbank
   * @param newVersion
   *          benötigte Version der Datenbank
   */
  public static void onUpdate(SQLiteDatabase db, int oldVersion, int newVersion) {
    // Migrationspfad
    switch (oldVersion) {
      case 1:
        db.execSQL(_ADD_PAUSE_COLUMN);
        db.execSQL(_ADD_COMMENT_COLUMN);

      case 2:
        // Zweite Version
        break;
    }
  }
}
