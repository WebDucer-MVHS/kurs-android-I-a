package de.mvhs.android.zeiterfassung.db;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Kurs on 19.10.2016.
 */

public class TimeDataContract {
    /**
     * Eindeutiger name des Content Providers
     */
    public final static String AUTHORITY = "de.mvhs.android.zeiterfassung.provider";

    /**
     * URL zum Content Provicer (Basis URI)
     */
    public final static Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    public static class TimeData {
        /**
         * Unterverzeichnis für diese Tabelle
         */
        public final static String CONTENT_DIRECTORY = "time_data";

        /**
         * URI für die Daten
         */
        public final static Uri CONTENT_URI =
                Uri.withAppendedPath(AUTHORITY_URI, CONTENT_DIRECTORY);

        // Datentypen
        /**
         * Datentyp für die Auflistung
         */
        public final static String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_DIRECTORY;

        /**
         * Datentyp für einen einzigen Datensatz
         */
        public final static String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_DIRECTORY;

        public interface Columns extends BaseColumns{
            /**
             * Startzeit in ISO 8601 Format (z.B.: 2016-10-19T18:30)
             */
            String START = "start_time";

            /**
             * Endzeit in ISO 8601 Format (z.B.: 2016-10-19T18:30)
             */
            String END = "end_time";
        }
    }
}
