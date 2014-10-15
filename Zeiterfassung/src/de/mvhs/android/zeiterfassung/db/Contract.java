package de.mvhs.android.zeiterfassung.db;

import android.provider.BaseColumns;

public final class Contract {
	public static final class Zeiten {
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
