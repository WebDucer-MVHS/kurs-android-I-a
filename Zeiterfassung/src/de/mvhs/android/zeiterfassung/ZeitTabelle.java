package de.mvhs.android.zeiterfassung;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ZeitTabelle {
  // Konstanten
  private final static String          _CREATE_TABLE       = "CREATE TABLE zeit (_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,"
                                                                   + "startzeit TEXT NOT NULL , endzeit TEXT)";

  private final static String          _DROP_TABLE         = "DROP TABLE IF EXISTS zeit";

  private final static String          _SQL_EMPTY_ENDTIME  = "SELECT _id FROM zeit WHERE IFNULL(endzeit,'') = ''";

  private final static String          _SQL_UPDATE_ENDTIME = "UPDATE zeit SET endzeit = ?1 WHERE  _id = ?2";

  private final static String          _SQL_UPDATE_ALL     = "UPDATE zeit SET startzeit = ?1, endzeit = ?2 WHERE  _id = ?3";

  /**
   * Tabellenname
   */
  public final static String           TABELLENNAME        = "zeit";
  /**
   * ID Spalte der Tabelle
   */
  public static final String           ID                  = "_id";
  /**
   * Spalte für die Startzeit
   */
  public static final String           STARTZEIT           = "startzeit";
  /**
   * Spalte für die Endzeit
   */
  public static final String           ENDZEIT             = "endzeit";

  /**
   * Konvertierung für das Datum
   */
  public static final SimpleDateFormat _DF                 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

  /**
   * Erzeugen der neuen Tabelle
   * 
   * @param db
   *          Datenbank-Referenz
   */
  public static void CreateTable(SQLiteDatabase db) {
    db.execSQL(_CREATE_TABLE);
  }

  /**
   * Löschen der Tabelle
   * 
   * @param db
   *          Datenbak-Referenz
   */
  public static void DropTable(SQLiteDatabase db) {
    db.execSQL(_DROP_TABLE);
  }

  /**
   * Speichern der Startzeit als ein neuer Datensatz
   * 
   * @param db
   *          Datenbank Instanz
   * @param startZeit
   *          Startzeit
   * @return ID des neuen Datensatzes
   */
  public static long SpeichereStartzeit(SQLiteDatabase db, Date startZeit) {
    long returnValue = -1;

    // Zeit für Startzeit eintragen
    ContentValues values = new ContentValues();
    values.put(STARTZEIT, _DF.format(startZeit));

    // Einfügen eines neuen Datensatzes
    returnValue = db.insert(TABELLENNAME, null, values);

    return returnValue;
  }

  /**
   * Aktualisieren der Enzeit
   * 
   * @param db
   *          Datenbank Instanz
   * @param endZeit
   *          Endzeit
   * @param id
   *          ID des zu aktualisirenden Datensatzes
   * @return Anzahl der aktualisierten Datensätze
   */
  public static int AktualisiereEndzeit(SQLiteDatabase db, long id, Date endZeit) {
    int returnValue = 0;

    // Vorkompiliertes SQL erzeugen
    // SQLiteStatement updateStatement = db.compileStatement(_SQL_UPDATE_ENDTIME);
    //
    // // Parameter an das SQL binden
    // updateStatement.bindString(1, _DF.format(endZeit));
    // updateStatement.bindLong(2, id);
    //
    // // Aktualisierung durchführen
    // returnValue = updateStatement.executeUpdateDelete();

    /* Kompabilitätskorrektur für Android 2.3.3 */
    ContentValues values = new ContentValues();
    values.put(ENDZEIT, _DF.format(endZeit));
    returnValue = db.update(TABELLENNAME, values, ID + "=?", new String[] { String.valueOf(id) });

    return returnValue;
  }

  public static int AktualisiereDatensatz(SQLiteDatabase db, long id, Date startZeit, Date endZeit) {
    int returnValue = 0;

    // Vorkompiliertes SQL erzeugen
    // SQLiteStatement updateStatement = db.compileStatement(_SQL_UPDATE_ALL);
    //
    // // Parameter an das SQL binden
    // updateStatement.bindString(1, _DF.format(startZeit));
    // updateStatement.bindString(2, _DF.format(endZeit));
    // updateStatement.bindLong(3, id);
    //
    // // Aktualisierung durchführen
    // returnValue = updateStatement.executeUpdateDelete();

    /* Kompabilitätskorrektur für Android 2.3.3 */
    ContentValues values = new ContentValues();
    values.put(ENDZEIT, _DF.format(endZeit));
    values.put(STARTZEIT, _DF.format(startZeit));
    returnValue = db.update(TABELLENNAME, values, ID + "=?", new String[] { String.valueOf(id) });

    return returnValue;
  }

  /**
   * Suche einen Datensatz mit leeren Endzeit
   * 
   * @param db
   *          Datenbank Instanz
   * @return ID des Datensatzes
   */
  public static long SucheLeereId(SQLiteDatabase db) {
    long returnValue = -1;

    // Datenbank nach leeren Eintrag abfragen
    Cursor data = db.rawQuery(_SQL_EMPTY_ENDTIME, null);

    // Prüfen, ob Ergebnisse vorliegen
    if (data.moveToFirst()) {
      // Wenn ja, ID auslesen
      returnValue = data.getLong(data.getColumnIndex(ID));
    }

    return returnValue;
  }

  /**
   * Ausgabe der Startzeit eines Datensatzes als Datum-Objekt
   * 
   * @param db
   *          Datenbank Instanz
   * @param id
   *          ID des Datensatzes
   * @return Startdatum des Datensatzes (NULL, wenn kein Datensatz gefunden wurde)
   */
  public static Date GebeStartzeitAus(SQLiteDatabase db, long id) {
    Date returnValue = null;

    // Auslesen der Startzeit aus der Datenbank
    Cursor data = db.query(TABELLENNAME, // Tabellenname
            new String[] { STARTZEIT }, // Spalten
            ID + "=?", // Bedingung
            new String[] { String.valueOf(id) }, // Parameter für die Bedingung
            null, // Group By
            null, // Having
            null); // Sortierung

    // Prüfen, ob Datensätze im Ergebnis vorhnden sind und nehmen den ersten
    if (data.moveToFirst()) {
      // Auslesen der Startzeit Spalte
      String startzeit = data.getString(data.getColumnIndex(STARTZEIT));
      if (startzeit != null && !startzeit.isEmpty()) {
        // Konvertierung des Strings in ein Datums-Objekt
        try {
          returnValue = _DF.parse(startzeit);
        } catch (ParseException e) {
          e.printStackTrace();
        }
      }
    }

    return returnValue;
  }

  /**
   * Ausgabe der Endzeit eines Datensatzes als Datum-Objekt
   * 
   * @param db
   *          Datenbank Instanz
   * @param id
   *          ID des Datensatzes
   * @return Endzeit des Datensatzes (NULL, wenn kein Datensatz gefunden wurde)
   */
  public static Date GebeEndzeitAus(SQLiteDatabase db, long id) {
    Date returnValue = null;

    // Auslesen der Startzeit aus der Datenbank
    Cursor data = db.query(TABELLENNAME, // Tabellenname
            new String[] { ENDZEIT }, // Spalten
            ID + "=?", // Bedingung
            new String[] { String.valueOf(id) }, // Parameter für die Bedingung
            null, // Group By
            null, // Having
            null); // Sortierung

    // Prüfen, ob Datensätze im Ergebnis vorhnden sind und nehmen den ersten
    if (data.moveToFirst()) {
      // Auslesen der Startzeit Spalte
      String startzeit = data.getString(data.getColumnIndex(ENDZEIT));
      if (startzeit != null && !startzeit.isEmpty()) {
        // Konvertierung des Strings in ein Datums-Objekt
        try {
          returnValue = _DF.parse(startzeit);
        } catch (ParseException e) {
          e.printStackTrace();
        }
      }
    }

    return returnValue;
  }

  public static Cursor LiefereAlleDaten(SQLiteDatabase db) {
    return db.query(TABELLENNAME, null, null, null, null, null, STARTZEIT + " DESC");
  }

  public static int LoescheDatensatz(SQLiteDatabase db, long id) {
    int returnValue = 0;

    returnValue = db.delete(TABELLENNAME, ID + "=?", new String[] { String.valueOf(id) });

    return returnValue;
  }
}
