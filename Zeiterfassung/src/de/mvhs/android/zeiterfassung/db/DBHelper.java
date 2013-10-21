package de.mvhs.android.zeiterfassung.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
  public DBHelper(Context context) {
    super(context, _DB_NAME, null, _DB_VERSION);
  }

  /* Private variablen */
  // Name der Datenbank-Datei
  private final static String _DB_NAME    = "zeit.db";

  // Für die App benötigte Datenbank-Version
  private final static int    _DB_VERSION = 1;

  @Override
  public void onCreate(SQLiteDatabase db) {
    ZeitenTable.onCreate(db);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    ZeitenTable.onUpdate(db, oldVersion, newVersion);
  }

}
