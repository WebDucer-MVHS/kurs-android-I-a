package de.mvhs.android.zeiterfassung.db;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * Created by kurs on 21.10.15.
 */
public final class ZeitContract {
  /* Basis Pfad */
  private final static String _BASE_PATH = "de.mvhs.android.zeiterfassung";

  /* Authority */
  public final static String AUTHORITY = _BASE_PATH + ".provider";

  /* Authority URI */
  public final static Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

  /**
   * Klasse für die Definition der Tabellen-Daten
   */
  public final static class ZeitDaten {
    /**
     * Unterverzeichnis der Daten
     */
    private final static String _DATA_DIRECTORY = "zeiten";

    /**
     * Unterverzeichnis für leeren Datensatz
     */
    private final static String _EMPTY_DIRECTORY = "leer";

    /**
     * Verzeichnis in Content Provider
     */
    public final static String CONTENT_DIRECTORY = _DATA_DIRECTORY;

    /**
     * Verzeichnis in Content Provider für leeren Datensatz
     */
    public final static String EMPTY_CONTENT_DIRECTORY = _EMPTY_DIRECTORY;

    /**
     * URI in Content Provider
     */
    public final static Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, CONTENT_DIRECTORY);

    /**
     * URI in Content Provider für leeren Datensatz
     */
    public final static Uri EMPTY_CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, EMPTY_CONTENT_DIRECTORY);

    /**
     * Datentyp für die Liste der Daten
     */
    public final static String CONTENT_TYPE =
        ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + _DATA_DIRECTORY;

    /**
     * Datentyp für einen Datensatz
     */
    public final static String CONTENT_ITEM_TYPE =
        ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + _DATA_DIRECTORY;
  }
}
