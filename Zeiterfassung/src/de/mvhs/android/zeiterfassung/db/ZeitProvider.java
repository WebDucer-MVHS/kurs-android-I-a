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
	private final static int _NOT_ENDED_ITEM = 30;

	private final static String _NOT_ENDED_ITEM_SELECTION = "IFNULL("
			+ Contract.Zeiten.Columns.END_TIME + ",'') = ''";

	static {
		_URI_MATCHER.addURI(Contract.AUTHORITY, Zeiten.CONTENT_DIRECTORY,
				_ZEIT_ITEMS);
		_URI_MATCHER.addURI(Contract.AUTHORITY,
				Zeiten.CONTENT_DIRECTORY + "/#", _ZEIT_ITEM_ID);
		_URI_MATCHER.addURI(Contract.AUTHORITY, Zeiten.NOT_ENDED_DIRECTORY,
				_NOT_ENDED_ITEM);
	}

	// Instanz des Helpers für den Zugriff auf die Datenbank
	private DBHelper _dbHelper;

	@Override
	public boolean onCreate() {
		_dbHelper = new DBHelper(getContext());

		return _dbHelper != null;
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
		case _NOT_ENDED_ITEM:
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
		case _NOT_ENDED_ITEM:
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
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int updatedItems = 0;

		final int uriType = _URI_MATCHER.match(uri);

		switch (uriType) {
		case _ZEIT_ITEM_ID:
			long id = ContentUris.parseId(uri);
			String where = BaseColumns._ID + "=?";
			String[] whereArgs = { String.valueOf(id) };

			updatedItems = _dbHelper.getWritableDatabase().update(
					ZeitenTabelle.TABLE_NAME, values, where, whereArgs);
			break;

		case _NOT_ENDED_ITEM:
			updatedItems = _dbHelper.getWritableDatabase().update(
					ZeitenTabelle.TABLE_NAME, values,
					_NOT_ENDED_ITEM_SELECTION, null);
			break;

		case _ZEIT_ITEMS:
			updatedItems = _dbHelper.getWritableDatabase().update(
					ZeitenTabelle.TABLE_NAME, values, selection, selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown uri: [" + uri + "]");
		}

		if (updatedItems > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}

		return updatedItems;
	}

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

		case _NOT_ENDED_ITEM:
			deletedItems = _dbHelper.getWritableDatabase().delete(
					ZeitenTabelle.TABLE_NAME, _NOT_ENDED_ITEM_SELECTION, null);
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
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor returnData = null;

		final int uriType = _URI_MATCHER.match(uri);

		switch (uriType) {
		case _ZEIT_ITEM_ID:
			long id = ContentUris.parseId(uri);
			String where = BaseColumns._ID + "=?";
			String[] whereArgs = { String.valueOf(id) };

			returnData = _dbHelper.getReadableDatabase().query(
					ZeitenTabelle.TABLE_NAME, projection, where, whereArgs,
					null, null, null);
			break;

		case _NOT_ENDED_ITEM:
			returnData = _dbHelper.getReadableDatabase().query(
					ZeitenTabelle.TABLE_NAME, projection,
					_NOT_ENDED_ITEM_SELECTION, null, null, null, null);
			break;

		case _ZEIT_ITEMS:
			returnData = _dbHelper.getReadableDatabase().query(
					ZeitenTabelle.TABLE_NAME, projection, selection,
					selectionArgs, null, null, sortOrder);
			break;

		default:
			throw new IllegalArgumentException("Unknown uri: [" + uri + "]");
		}

		// Registrierung des Beobachters
		if (returnData != null) {
			returnData.setNotificationUri(getContext().getContentResolver(),
					uri);
		}

		return returnData;
	}

}
