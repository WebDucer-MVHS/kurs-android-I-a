package de.mvhs.android.arbeitszeiterfassung.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract für den Zugriff auf den Content Provider der App
 */
public class ZeitabschnittContract {
  /**
   * Basisname für den Content Provider
   */
  private final static String _BASE_PATH    = "de.mvhs.android.arbeitszeiterfassung.provider";

  /* Public Fields */
  /**
   * Authority des Content Providers
   */
  public final static String  AUTHORITY     = _BASE_PATH;

  /**
   * Basis URI für den Content Provider
   */
  public final static Uri     AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

  // Initialisierung der Klasse verhindern
  private ZeitabschnittContract() {
  }

  /* Tabellendefinitionen */
  /**
   * Definition der Tabelle "Zeitabschnitte"
   */
  public final static class Zeitabschnitte {
    // Private Fields
    /**
     * Unterverzeichnis der Tabelle innerhalb des Content Providers
     */
    public final static String DIRECTORY        = "zeit";

    // Public Fields
    /**
     * URI für den Zugriff auf die Tabelle
     */
    public final static Uri     CONTENT_URI       = Uri.withAppendedPath(AUTHORITY_URI, DIRECTORY);

    /**
     * Datentyp für die Auflistung
     */
    public final static String  CONTENT_TYPE      = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + DIRECTORY;

    /**
     * Datentyp für einen einzelnen Eintrag
     */
    public final static String  CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + DIRECTORY;

    // Initialisierung verhindern
    private Zeitabschnitte() {
    }

    // Spaltendefinition der Tabelle
    /**
     * Definition der verfügbaren Spalten der Tabelle
     */
    public static interface Columns extends BaseColumns {
      /**
       * Startzeit in ISO-8601 Format (z.B.: 2013-03-21T18:00) [String]
       */
      public final static String START   = "start";
      /**
       * Endzeit in ISO-8601 Format (z.B.: 2013-03-21T20:35) [String]
       */
      public final static String STOP    = "stop";
      /**
       * Pausenzeit in Minuten [long]
       */
      public final static String PAUSE   = "pause";
      /**
       * Freikommentar zu der Arbeitszeit [String]
       */
      public final static String COMMENT = "kommentar";
    }
  }

}
