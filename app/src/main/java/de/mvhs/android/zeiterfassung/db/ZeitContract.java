package de.mvhs.android.zeiterfassung.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by kurs on 15.04.15.
 */
public final class ZeitContract {
    /* Base Path */
    private final static String _BASE_PATH = "de.mvhs.android.zeiterfassung";

    /* Authority */
    public final static String AUTHORITY = _BASE_PATH + ".provider";

    /* Authority URI */
    public final static Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    /**
     * Kontrakt für die Tabelle Zeiten
     */
    public final static class ZeitDaten {
        /**
         * Unterverzeichnis für die Tabellendaten
         */
        private final static String _DATA_DIRECTORY = "zeiten";

        /**
         * Verzeichnis in ContentProvider
         */
        public final static String CONTENT_DIRECTORY = _DATA_DIRECTORY;

        /**
         * Datentyp für Liste der Zeiten
         */
        public final static String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + _DATA_DIRECTORY;

        /**
         * Datentyp für einen einzigen Eintrag
         */
        public final static String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + _DATA_DIRECTORY;

        /**
         * Zeiten URI
         */
        public final static Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, CONTENT_DIRECTORY);

        /**
         * Spalten der Tabelle
         */
        public static interface Columns extends BaseColumns {
            /**
             * Startzeit Spalte in ISO-8601 Format ("2015-10-25T18:13")
             */
            public final static String START_TIME = "start_time";

            /**
             * Endzeit Spalte in ISO-8601 Format ("2015-10-25T23:45")
             */
            public final static String END_TIME = "end_time";
        }
    }

    public final static class Converters {
        private final static String _DB_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm";

        /**
         * Formatter für die Zeit zur Speicherung in der Datenbank (und auslesen aus der Datenbank)
         */
        public final static DateFormat DB_DATE_TIME_FORMATTER = new SimpleDateFormat(_DB_DATE_TIME_PATTERN, Locale.GERMANY);
    }


}
