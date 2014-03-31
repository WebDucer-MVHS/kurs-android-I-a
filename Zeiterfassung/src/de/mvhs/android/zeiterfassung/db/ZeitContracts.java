package de.mvhs.android.zeiterfassung.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ZeitContracts {
	/**
	 * Basispfad für den Content-Provider
	 */
	public final static String BASE_PATH = "de.mvhs.android.zeiterfassung.zeitprovider";

	/**
	 * Authority des Content Providers (eideutige kennung innerhalb der Android
	 * Betriebssystems
	 */
	public final static Uri AUTHORITY_URI = Uri.parse("content://" + BASE_PATH);

	/**
	 * Klasse für die Beschreibung der Tabelle
	 */
	public final static class Zeit {
		/**
		 * Pfad zur Tabelle
		 */
		public final static String CONTENT_DIRECTORY = "zeit";

		/**
		 * Uri für den Zugriff auf die Daten der Tabelle
		 */
		public final static Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, CONTENT_DIRECTORY);

		/**
		 * Datentyp für die Auflistung der Zeit-Einträge
		 */
		public final static String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/" + CONTENT_DIRECTORY;
		/**
		 * Datentyp für einen einzelnen Datensatz
		 */
		public final static String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/" + CONTENT_DIRECTORY;

		/**
		 * Verfügbare Spalten der Tabelle Zeit
		 */
		public static interface Columns extends BaseColumns {
			/**
			 * Spaltenname für die Start-Spalte
			 */
			public final static String START = "start_time";

			/**
			 * Spaltenname für die Ende-Spalte
			 */
			public final static String END = "end_time";
		}

		/**
		 * Verhinder die Initialisierung der Klasse
		 */
		private Zeit() {

		}
	}

	/**
	 * Verhindert die Initialisierung der Klasse
	 */
	private ZeitContracts() {

	}

}
