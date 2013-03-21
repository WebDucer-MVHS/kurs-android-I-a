package de.mvhs.android.arbeitszeiterfassung.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
  // Klassenvariablen
  /**
   * Dateiname der Datenbank
   */
  private final static String _DB_NAME    = "zeitdb.db";
  /**
   * Aktuelle Datenbank Version
   */
  private final static int    _DB_VERSION = 1;

  public DBHelper(Context context) {
    super(context, _DB_NAME, null, _DB_VERSION);
  }

  /**
   * Wird aufgerufen, wenn noch keine Datenbank existiert
   */
  @Override
  public void onCreate(SQLiteDatabase db) {
    ZeitabschnittTabelle.createTable(db);
  }

  /**
   * Wird aufgerufen, wenn die aktuelle Datenbank-Version höher ist, als die Version der Datenbank auf dem Gerät
   * 
   * @param db
   *          Datenbank
   * @param oldVersion
   *          alte Version
   * @param newVersion
   *          neue Version
   */
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    ZeitabschnittTabelle.updateTable(db, oldVersion, newVersion);
  }

}
