package de.mvhs.android.zeiterfassung.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Kurs on 05.04.2017.
 */

public final class TimeContract {
    /**
     * Authority des Providers
     */
    public final static String AUTHORITY = "de.mvhs.android.zeiterfassung.provider";

    /**
     * Basis URI für den Provider
     */
    public final static Uri AUTHORITY_BASE_URI = Uri.parse("content://" + AUTHORITY);

    /**
     * Daten zu dem Zugrifspunkt für die Zeitdaten
     */
    public static class TimeData {
        /**
         * Unterverzeichnis für die Zeitdaten
         */
        public final static String CONTENT_DIRECTORY = "time_data";

        /**
         * Unterverzeichnis für den noch offenen Datensatz
         */
        public final static String OPEN_DIRECTORY = CONTENT_DIRECTORY + "/open";

        /**
         * Uri für die Zeitdaten
         */
        public final static Uri CONTENT_URI =
                Uri.withAppendedPath(AUTHORITY_BASE_URI, CONTENT_DIRECTORY);

        /**
         * Uri für den offenen Datensatz
         */
        public final static Uri OPEN_URI = Uri.withAppendedPath(AUTHORITY_BASE_URI, OPEN_DIRECTORY);

        /**
         * Datentyp für die Auflistung der Zeitdaten
         */
        public final static String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/" + AUTHORITY
                        + "." + CONTENT_DIRECTORY;

        /**
         * Datentyp für einen einzigen Eintrag aus Zeitdaten
         */
        public final static String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/" + AUTHORITY
                        + "." + CONTENT_DIRECTORY;

        public interface Columns extends BaseColumns {
            /**
             * Startzeit der Zeiterfassung in ISO 8601 Format (z.B.: 2017-01-28T18:30)
             */
            String START = "start_time";

            /**
             * Endzeit der Zeiterfassung in ISO 8601 Format (z.B.: 2017-01-28T18:30)
             */
            String END = "end_time";

            /*
             * Pause in Minuten, Standard: 0
             */
            String PAUSE = "pause";

            /*
             * Notitz zur Arbeitszeit
             */
            String COMMENT = "comment";
        }
    }

    /**
     * Konverter für die interaktion mit dem Provider
     */
    public static class Converters {
        // ISO 8601 Format
        private final static String _DB_PATTERN = "yyyy-MM-dd'T'HH:mm";

        private final static DateFormat _DB_FORMATTER =
                new SimpleDateFormat(_DB_PATTERN, Locale.GERMANY);

        /**
         * Konvertierung des Datums und der Uhrzeit in ein Format für die Datenbank
         * @param date
         * @return ISO 8601 Format
         */
        public static String formatForDb(Calendar date) {
            return _DB_FORMATTER.format(date.getTime());
        }

        public static Calendar parseFromDb(String dbDate) throws ParseException {
            Calendar date = Calendar.getInstance();
            date.setTime(_DB_FORMATTER.parse(dbDate));
            return date;
        }
    }

















}
