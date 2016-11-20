package de.mvhs.android.zeiterfassung.db;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
     * Unterverzeichnis für den nicht beendeten Datensatz
     */
    public final static String NOT_FINISHED_CONTENT_DIRECTORY = CONTENT_DIRECTORY + "/not_finished";

    /**
     * URI für die Daten
     */
    public final static Uri CONTENT_URI =
        Uri.withAppendedPath(AUTHORITY_URI, CONTENT_DIRECTORY);

    /**
     * Uri für den nicht beendeten Datensatz
     */
    public final static Uri NOT_FINISHED_CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, NOT_FINISHED_CONTENT_DIRECTORY);

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

    public interface Columns extends BaseColumns {
      /**
       * Startzeit in ISO 8601 Format (z.B.: 2016-10-19T18:30)
       */
      String START = "start_time";

      /**
       * Endzeit in ISO 8601 Format (z.B.: 2016-10-19T18:30)
       */
      String END = "end_time";

      /**
       * Pausendauer in Minuten (Standard: 0 Minuten)
       */
      String PAUSE = "pause_time";

      /**
       * Kommentar zu der aufgezeichneten Zeit
       */
      String COMMENT = "comment";
    }
  }

  public static class Converter {
    private final static String _DB_ISO8601_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm";

    private final static DateFormat _DB_ISO8601_FORMATTER = new SimpleDateFormat(_DB_ISO8601_DATE_TIME_PATTERN, Locale.GERMANY);

    /**
     * Convert given time in ISO-8601 date time format for database
     *
     * @param date given date
     * @return formatted date time for database
     */
    @NonNull
    public static String formatForDb(@NonNull Calendar date) {
      return _DB_ISO8601_FORMATTER.format(date.getTime());
    }

    /**
     * Parse database date abd time to calendar object
     *
     * @param dbDateTime data time string form database
     * @return caledar instance for the given database date and time
     * @throws ParseException if string not ISO-8601 format
     */
    @NonNull
    public static Calendar parseFromDb(@NonNull String dbDateTime) throws ParseException {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(_DB_ISO8601_FORMATTER.parse(dbDateTime));
      return calendar;
    }
  }
}
