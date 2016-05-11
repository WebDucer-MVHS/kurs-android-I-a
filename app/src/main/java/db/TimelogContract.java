package db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by kurs on 13.04.16.
 */
public class TimelogContract {
    /**
     * Eindeutiger Name für den ContentProvider
     */
    public final static String AUTHORITY = "de.mvhs.android.zeiterfassung.provider";

    /**
     * URI zu dem ContentProvider
     */
    public final static Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    /**
     * Converter für die Datenbank
     */
    public static class Converter {
        private final static String _DB_DATE_TIME_PATTERN =
                "yyyy-MM-dd'T'HH:mm";

        /**
         * Konverter für die Speicherng des Datums
         * und der Uhrzeit in der Datenbank nach ISO 8601
         */
        public final static DateFormat DB_DATE_TIME_FORMATTER =
                new SimpleDateFormat(_DB_DATE_TIME_PATTERN, Locale.GERMANY);
    }

    public static class Timelog {
        /**
         * Unterverzeichnis der Daten
         */
        private final static String _DATA_DIRECTORY = "timelog";

        /**
         * Unterverzeichnis für nicht beedete Aufzeichnung
         */
        private final static String _NOT_FINISHED_DIRECTORY = "not-finished";

        /**
         * Verzeichnis in ContentProvider
         */
        public final static String CONTENT_DIRECTORY = _DATA_DIRECTORY;

        /**
         * Verzeichnis für nicht beendete Aufzeichnung
         */
        public final static String NOT_FINISHED_DIRECTORY = _DATA_DIRECTORY + "/" + _NOT_FINISHED_DIRECTORY;

        /**
         * URI zu der Tabelle
         */
        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(AUTHORITY_URI, CONTENT_DIRECTORY);

        /**
         * URI für nicht beendeten Datensatz
         */
        public static final Uri NOT_FINISHED_URI =
                Uri.withAppendedPath(CONTENT_URI, _NOT_FINISHED_DIRECTORY);

        /**
         * Datentyp für die Auflistung
         */
        public final static String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + _DATA_DIRECTORY;

        /**
         * Datentyp für Eintrag
         */
        public final static String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + _DATA_DIRECTORY;

        /**
         * Spalten
         */
        public interface Columns extends BaseColumns {
            /**
             * Startzeit in ISO 8601 Format (e.g.: 2016-11-23T18:17)
             */
            String START = "start_time";

            /**
             * Endzeit in ISO 8601 Format
             */
            String END = "end_time";

            /**
             * Kommentar zum Eintrag
             */
            String COMMENT = "comment";

            /**
             * Pausen-Spalte (DEFAULT-Werte: 0)
             */
            String PAUSE = "pause";
        }
    }
}
