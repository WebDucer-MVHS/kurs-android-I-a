package de.mvhs.android.zeiterfassung.db;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ZeitContract {
  /* Public */
  private final static String          _DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm";
  /**
   * Formatierer für das richtige Datumsformat in der Datenbank
   */
  public final static SimpleDateFormat DB_DATE_FORMAT     = new SimpleDateFormat(_DATE_TIME_PATTERN, Locale.ROOT);
  /**
   * Basispfad für den Content Provider
   */
  public final static String           BASE_PATH          = "de.mvhs.android.zeiterfassung.zeitprovider";

  /**
   * Authority des Content Providers (eindeutige Kennung innerhalb des Betriebssystems)
   */
  public final static Uri              AUTHORITY_URI      = Uri.parse("content://" + BASE_PATH);

  public final static class Zeit {

    /**
     * Pfad zur Tabelle
     */
    public final static String CONTENT_DIRECTORY = "zeit";

    /**
     * Uri für den Zugriff auf die Tabelle "Zeit"
     */
    public final static Uri    CONTENT_URI       = Uri.withAppendedPath(AUTHORITY_URI, CONTENT_DIRECTORY);

    /**
     * Datentyp für die Auflistung der Daten aus der Tabelle "Zeit"
     */
    public final static String CONTENT_TYPE      = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_DIRECTORY;

    /**
     * Datentyp für einen einzigen Datensatz aus der tabelle "Zeit"
     */
    public final static String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_DIRECTORY;

    /**
     * Spalten der Tabelle "Zeit"
     */
    public static interface Columns extends BaseColumns {
      /**
       * Satrtzeit Spalte im Format '2013-10-17T14:37'
       */
      public final static String START   = "start";

      /**
       * Endzeit Splate im Format '2013-10-17T18:55'
       */
      public final static String END     = "end";

      /**
       * Pausen Spalte (Integer) in Minuten
       */
      public final static String PAUSE   = "pause";

      /**
       * Kommentar Spalte für den Eintrag
       */
      public final static String COMMENT = "comment";
    }

    /* Verhindern der Initialisierung der Klasse */
    private Zeit() {

    }
  }

  /* Verhindern der Initialisierung der Klasse */
  private ZeitContract() {
  }
}
