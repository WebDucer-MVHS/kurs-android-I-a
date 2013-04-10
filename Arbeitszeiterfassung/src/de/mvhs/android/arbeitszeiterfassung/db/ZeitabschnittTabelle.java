package de.mvhs.android.arbeitszeiterfassung.db;

import android.database.sqlite.SQLiteDatabase;

public class ZeitabschnittTabelle {
  // Klassenvariablen
  /**
   * Interne ID der Tabelle
   */
  private final static int    _TABLE_ID  = 100;

  // Scripts
  /**
   * SQL-Script zum Erstellen einer neuen Tabelle
   */
  private final static String _CREATE    = "CREATE  TABLE zeitabschnitt (" + "_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ," + "start TEXT NOT NULL,"
                                                 + "stop TEXT," + "pause INTEGER NOT NULL DEFAULT 0," + "kommentar TEXT)";

  /**
   * SQL-Script zum Löschen der Tabelle
   */
  private final static String _DROP      = "DROP TABLE zeitabschnitt";

  /**
   * Name der Tabelle in der Datenbank
   */
  public final static String  TABLE_NAME = "zeitabschnitt";
  /**
   * ID für die Liste
   */
  public final static int     ITEMS      = _TABLE_ID + 1;
  /**
   * ID für eine Eintrag
   */
  public final static int     ITEM_ID    = _TABLE_ID + 2;

  // Methoden
  /**
   * Erstellen der Tabelle
   * 
   * @param db
   *          Datenbank
   */
  public static void createTable(SQLiteDatabase db) {
    db.execSQL(_CREATE);
  }

  /**
   * Aktualisieren der Tabelle
   * 
   * @param db
   *          Datenbank
   * @param oldVersion
   *          alte Version
   * @param newVersion
   *          neue Version
   */
  public static void updateTable(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL(_DROP);
    createTable(db);
  }
}
