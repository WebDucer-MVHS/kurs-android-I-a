package de.mvhs.android.zeiterfassung.db;

import java.text.SimpleDateFormat;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class ZeitProvider extends ContentProvider {
	/* Kalassen-Variblen */
	private DBHelper _DBHelper = null;
	private final static UriMatcher _URI_MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);

	/* Public */
	private final static String _DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm";
	/**
	 * Formatierer für das richtige Datumsformat in der Datenbank
	 */
	public final static SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat(
			_DATE_TIME_PATTERN);
	/**
	 * Basispfad für den Content Provider
	 */
	public final static String BASE_PATH = "de.mvhs.android.zeiterfassung.zeitprovider";

	/**
	 * Authority des Content Providers (eindeutige Kennung innerhalb des
	 * Betriebssystems)
	 */
	public final static Uri AUTHORITY_URI = Uri.parse("content://" + BASE_PATH);

	/**
	 * Pfad zur Tabelle
	 */
	public final static String CONTENT_DIRECTORY = "zeit";

	/**
	 * Uri für den Zugriff auf die Tabelle "Zeit"
	 */
	public final static Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI,
			CONTENT_DIRECTORY);

	/**
	 * Datentyp für die Auflistung der Daten aus der Tabelle "Zeit"
	 */
	public final static String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/" + CONTENT_DIRECTORY;

	/**
	 * Datentyp für einen einzigen Datensatz aus der tabelle "Zeit"
	 */
	public final static String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/" + CONTENT_DIRECTORY;

	/**
	 * Spalten der Tabelle "Zeit"
	 */
	public static interface Columns extends BaseColumns {
		/**
		 * Satrtzeit Spalte im Format '2013-10-17T14:37'
		 */
		public final static String START = "start";

		/**
		 * Endzeit Splate im Format '2013-10-17T18:55'
		 */
		public final static String END = "end";
	}

	static {
		_URI_MATCHER.addURI(BASE_PATH, CONTENT_DIRECTORY, ZeitenTable.ITEMS);
		_URI_MATCHER.addURI(BASE_PATH, CONTENT_DIRECTORY + "/#",
				ZeitenTable.ITEM_ID);
	}

	@Override
	public int delete(Uri uri, String where, String[] whereParams) {
		int deletedItems = 0;

		final int uriType = _URI_MATCHER.match(uri);

		switch (uriType) {
		case ZeitenTable.ITEMS:
			deletedItems = _DBHelper.getWritableDatabase().delete(
					ZeitenTable.TABLE_NAME, where, whereParams);
			break;

		case ZeitenTable.ITEM_ID:
			long id = ContentUris.parseId(uri);
			String idWhere = Columns._ID + "=?";
			String[] idArgs = { String.valueOf(id) };
			deletedItems = _DBHelper.getWritableDatabase().delete(
					ZeitenTable.TABLE_NAME, idWhere, idArgs);
			break;

		default:
			throw new IllegalArgumentException(
					"Ich kenne diese URI nicht :-( '" + uri + "'");
		}

		if (deletedItems > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}

		return deletedItems;
	}

	@Override
	public String getType(Uri uri) {
		String returnType = null;

		final int uriType = _URI_MATCHER.match(uri);

		switch (uriType) {
		case ZeitenTable.ITEMS:
			returnType = CONTENT_TYPE;
			break;

		case ZeitenTable.ITEM_ID:
			returnType = CONTENT_ITEM_TYPE;
			break;
		}

		return returnType;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri insertUri = null;

		final int uriType = _URI_MATCHER.match(uri);
		long id = -1;

		switch (uriType) {
		case ZeitenTable.ITEMS:
		case ZeitenTable.ITEM_ID:
			id = _DBHelper.getWritableDatabase().insert(ZeitenTable.TABLE_NAME,
					null, values);
			break;

		default:
			throw new IllegalArgumentException(
					"Ich kenne diese URI nicht :-( '" + uri + "'");
		}

		if (id > 0) {
			insertUri = ContentUris.withAppendedId(CONTENT_URI, id);
			getContext().getContentResolver().notifyChange(insertUri, null);
		}

		return insertUri;
	}

	@Override
	public boolean onCreate() {
		_DBHelper = new DBHelper(getContext());
		return _DBHelper != null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
