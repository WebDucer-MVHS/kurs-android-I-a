package de.mvhs.android.zeiterfassung.db;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class Contract {
	/**
	 * Basispfad für den Content Provider
	 */
	public final static String AUTHORITY = "de.mvhs.android.zeiterfassung.zeitprovider";

	/**
	 * Authority URI des Content Providers (eindeutige Kennung innerhalb des
	 * Android Betriebssystems)
	 */
	public final static Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

	public final static class Converters {
		// Format String für die Datenbank
		private final static String _DB_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm";

		/**
		 * Formatter zum Speichern von Start und Enddatum in der Datenbank
		 */
		public final static DateFormat DB_DATE_TIME_FORMATTER = new SimpleDateFormat(
				_DB_DATE_TIME_PATTERN, Locale.GERMANY);
	}

	/**
	 * Tabelle Zeiten
	 */
	public static final class Zeiten {

		/**
		 * Relativer Pfad zur Tabelle Zeiten
		 */
		public final static String CONTENT_DIRECTORY = "zeiten";

		/**
		 * Uri für den Zugriff auf die Daten der Tabelle Zeiten
		 */
		public final static Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, CONTENT_DIRECTORY);

		/**
		 * Dytepentyp für die Auflistung der Zeiten (mime-type)
		 */
		public final static String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/" + CONTENT_DIRECTORY;

		/**
		 * Datentyp für einen einzigen Eintrag aus der zeiten Tabelle
		 */
		public final static String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/" + CONTENT_DIRECTORY;

		/**
		 * Spalten der Tabelle Zeiten
		 */
		public interface Columns extends BaseColumns {
			/**
			 * Spalte mit der Startzeit der Aufzeichnung in ISO-8601 Format<br>
			 * z.B.: '2014-10-15T19:45'
			 */
			public static final String START_TIME = "Startzeit";

			/**
			 * Spalte mit der Endzeit der Aufzeichnung in ISO-8601 Format<br>
			 * z.B.: '2014-10-15T19:45'
			 */
			public static final String END_TIME = "Endzeit";
		}
	}
}
