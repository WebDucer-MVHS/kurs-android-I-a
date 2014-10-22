package de.mvhs.android.zeiterfassung.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import de.mvhs.android.zeiterfassung.db.Contract.Zeiten;

public class ZeitProvider extends ContentProvider {

	/**
	 * Lookup für verwaltete URIs
	 */
	private final static UriMatcher _URI_MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);

	private final static int _ZEIT_ITEMS = 10;
	private final static int _ZEIT_ITEM_ID = 20;

	static {
		_URI_MATCHER.addURI(Contract.AUTHORITY, Zeiten.CONTENT_DIRECTORY,
				_ZEIT_ITEMS);
		_URI_MATCHER.addURI(Contract.AUTHORITY,
				Zeiten.CONTENT_DIRECTORY + "/#", _ZEIT_ITEM_ID);
	}

	// Instanz des Helpers für den Zugriff auf die Datenbank
	private DBHelper _dbHelper;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int deletedItems = 0;

		final int uriType = _URI_MATCHER.match(uri);

		switch (uriType) {
		case _ZEIT_ITEM_ID:
			long id = ContentUris.parseId(uri);
			String where = BaseColumns._ID + "=?";
			String[] whereArgs = { String.valueOf(id) };

			deletedItems = _dbHelper.getWritableDatabase().delete(
					ZeitenTabelle.TABLE_NAME, where, whereArgs);

			break;

		case _ZEIT_ITEMS:
			deletedItems = _dbHelper.getWritableDatabase().delete(
					ZeitenTabelle.TABLE_NAME, selection, selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown uri: [" + uri + "]");
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
		case _ZEIT_ITEMS:
			returnType = Zeiten.CONTENT_TYPE;
			break;

		case _ZEIT_ITEM_ID:
			returnType = Zeiten.CONTENT_ITEM_TYPE;
			break;
		}

		return returnType;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri returnUri = null;

		final int uriType = _URI_MATCHER.match(uri);

		switch (uriType) {
		case _ZEIT_ITEMS:
		case _ZEIT_ITEM_ID:
			SQLiteDatabase db = _dbHelper.getWritableDatabase();
			long id = db.insert(ZeitenTabelle.TABLE_NAME, null, values);
			returnUri = ContentUris.withAppendedId(Zeiten.CONTENT_URI, id);

			break;

		default:
			throw new IllegalArgumentException("Unknown uri: [" + uri + "]");
		}

		if (returnUri != null) {
			getContext().getContentResolver().notifyChange(returnUri, null);
		}

		return returnUri;
	}

	@Override
	public boolean onCreate() {
		_dbHelper = new DBHelper(getContext());

		return _dbHelper != null;
	}

	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
