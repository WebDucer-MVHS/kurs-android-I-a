package de.mvhs.android.zeiterfassung.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class ZeitProvider extends ContentProvider {
	// Klassenvariablen
	/* Instanz unseres Helpers */
	private DBHelper _DBHelper = null;

	/* Lookup für verwaltete URIs */
	private final static UriMatcher _URI_MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		_URI_MATCHER.addURI(ZeitContracts.BASE_PATH,
				ZeitContracts.Zeit.CONTENT_DIRECTORY, ZeitTable.ITEMS); // Uri
																		// für
																		// Auflistung
		_URI_MATCHER.addURI(ZeitContracts.BASE_PATH,
				ZeitContracts.Zeit.CONTENT_DIRECTORY + "/#", ZeitTable.ITEM_ID); // Uri
																					// für
																					// einen
																					// Datensatz
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int deletedItems = 0;

		final int uriType = _URI_MATCHER.match(uri);

		switch (uriType) {
		case ZeitTable.ITEMS:
			deletedItems = _DBHelper.getWritableDatabase().delete(
					ZeitTable.TABLE_NAME, selection, selectionArgs);
			break;

		case ZeitTable.ITEM_ID:
			long id = ContentUris.parseId(uri);
			String where = ZeitContracts.Zeit.Columns._ID + "=?";

			String[] whereArgs = new String[] { String.valueOf(id) };

			deletedItems = _DBHelper.getWritableDatabase().delete(
					ZeitTable.TABLE_NAME, where, whereArgs);
			break;

		default:
			throw new IllegalArgumentException(
					"Uri ist nicht bekannt und kann nicht verarbeitet werden! ["
							+ uri + "]");
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
		case ZeitTable.ITEMS:
			returnType = ZeitContracts.Zeit.CONTENT_TYPE;
			break;

		case ZeitTable.ITEM_ID:
			returnType = ZeitContracts.Zeit.CONTENT_ITEM_TYPE;
			break;
		}

		return returnType;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri returnUri = null;

		final int uriType = _URI_MATCHER.match(uri);

		long id = -1;

		switch (uriType) {
		case ZeitTable.ITEMS:
		case ZeitTable.ITEM_ID:
			id = _DBHelper.getWritableDatabase().insert(ZeitTable.TABLE_NAME,
					null, values);
			break;

		default:
			throw new IllegalArgumentException(
					"Uri ist nicht bekannt und kann nicht verarbeitet werden! ["
							+ uri + "]");
		}

		if (id > 0) {
			returnUri = ContentUris.withAppendedId(
					ZeitContracts.Zeit.CONTENT_URI, id);
			// Benachrichtigung der anderen Verwerter des Content Providers über
			// Änderungen
			getContext().getContentResolver().notifyChange(returnUri, null);
		}

		return returnUri;
	}

	@Override
	public boolean onCreate() {
		_DBHelper = new DBHelper(getContext());

		return _DBHelper != null;
	}

	@Override
	public void shutdown() {
		if (_DBHelper != null) {
			_DBHelper.close();
			_DBHelper = null;
		}

		super.shutdown();
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
