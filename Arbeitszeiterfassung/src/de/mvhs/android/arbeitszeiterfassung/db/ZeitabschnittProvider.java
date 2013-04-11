package de.mvhs.android.arbeitszeiterfassung.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

public class ZeitabschnittProvider extends ContentProvider {
	// Variablen
	private final static UriMatcher _URI_MATCHER =
			new UriMatcher(UriMatcher.NO_MATCH);
	
	private DBHelper _DbHelper = null;
	
	static{
		_URI_MATCHER.addURI(
				ZeitabschnittContract.AUTHORITY,
				ZeitabschnittContract.Zeitabschnitte.DIRECTORY,
				ZeitabschnittTabelle.ITEMS);
		_URI_MATCHER.addURI(
				ZeitabschnittContract.AUTHORITY,
				ZeitabschnittContract.Zeitabschnitte.DIRECTORY + "/#",
				ZeitabschnittTabelle.ITEM_ID);
	}

	@Override
	public int delete(Uri uri, String select, String[] selectArgs) {
		int returnValue = 0;
		
		final int uriType = _URI_MATCHER.match(uri);
		
		switch (uriType) {
		case ZeitabschnittTabelle.ITEMS:
			SQLiteDatabase dbAll = _DbHelper.getWritableDatabase();
			returnValue = dbAll.delete(
					ZeitabschnittTabelle.TABLE_NAME, // Tabelle
					select, // WHERE Bedingung
					selectArgs); // Parameter
			
			break;

		case ZeitabschnittTabelle.ITEM_ID:
			long id = ContentUris.parseId(uri);
			SQLiteDatabase db =
					_DbHelper.getWritableDatabase(); // Datenbank mit Schreibzugriff
			String idSelect = BaseColumns._ID + "=?"; // WHERE Bedingung für ID
			String[] idArgs = {String.valueOf(id)}; // Paramerter für ID
			
			returnValue = db.delete(
					ZeitabschnittTabelle.TABLE_NAME, // Tabelle
					idSelect, // WHERE Bedingung
					idArgs); // Parameter
			
			break;
			
		default:
			throw new IllegalArgumentException("Uri nicht bekannt!");
		}
		
		return returnValue;
	}

	@Override
	public String getType(Uri uri) {
		String returnValue = null;
		
		switch (_URI_MATCHER.match(uri)) {
		case ZeitabschnittTabelle.ITEMS:
			returnValue =
				ZeitabschnittContract.Zeitabschnitte.CONTENT_TYPE;
			break;
			
		case ZeitabschnittTabelle.ITEM_ID:
			returnValue =
				ZeitabschnittContract.Zeitabschnitte.CONTENT_ITEM_TYPE;
			break;
		}
		
		return returnValue;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri returnValue = null;
		
		final int uriType = _URI_MATCHER.match(uri);
		
		switch (uriType) {
		case ZeitabschnittTabelle.ITEMS:
		case ZeitabschnittTabelle.ITEM_ID:
			SQLiteDatabase db =
				_DbHelper.getWritableDatabase(); // Datenbank mit Schreibrecht
			
			long id = db.insert(
					ZeitabschnittTabelle.TABLE_NAME, // Tabellenname in der DB
					null,
					values); // Werte, die eingefügt werden sollen
			
			returnValue = ContentUris.withAppendedId(
					ZeitabschnittContract.Zeitabschnitte.CONTENT_URI,
					id);
			
			break;

		default:
			throw new IllegalArgumentException("Uri nicht bekannt!");
		}
		
		return returnValue;
	}

	@Override
	public boolean onCreate() {
		_DbHelper = new DBHelper(getContext());
		return _DbHelper != null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor returnValue = null;
		
		final int uriType = _URI_MATCHER.match(uri);
		
		switch (uriType) {
		case ZeitabschnittTabelle.ITEMS:
			SQLiteDatabase dbAll = _DbHelper.getReadableDatabase();
			
			returnValue = dbAll.query(
					ZeitabschnittTabelle.TABLE_NAME, // Tabellenname
					projection, // Auszuwählende Spalten (SELECT)
					selection, // WHERE Bedingung
					selectionArgs, // Parameter
					null, // GROUP BY Bedingung
					null, // HAVING bedingung
					sortOrder); // Sortierung
			
			break;

		case ZeitabschnittTabelle.ITEM_ID:
			long id = ContentUris.parseId(uri);
			String idSelect = BaseColumns._ID + "=?";
			String[] idArgs = {String.valueOf(id)};
			SQLiteDatabase db = _DbHelper.getReadableDatabase();
			
			returnValue = db.query(
					ZeitabschnittTabelle.TABLE_NAME, // Tabellenname
					projection, // Auszuwählende Spalten (SELECT)
					idSelect, // WHERE Bedingung
					idArgs, // Parameter
					null, // GROUP BY Bedingung
					null, // HAVING bedingung
					sortOrder); // Sortierung
			
			break;
		default:
			throw new IllegalArgumentException("Uri nicht bekannt!");
		}
				
		return returnValue;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int returnValue = 0;
		
		final int uriType = _URI_MATCHER.match(uri);
		
		switch (uriType) {
		case ZeitabschnittTabelle.ITEMS:
			SQLiteDatabase dbAll = _DbHelper.getWritableDatabase();
			returnValue = dbAll.update(
					ZeitabschnittTabelle.TABLE_NAME, // Tabelle
					values, // Zu ändernde Werte
					selection, // WHERE Bedingung
					selectionArgs); // Parameter
			
			break;

		case ZeitabschnittTabelle.ITEM_ID:
			long id = ContentUris.parseId(uri);
			String idSelect = BaseColumns._ID + "=?";
			String[] idArgs = {String.valueOf(id)};
			
			SQLiteDatabase db = _DbHelper.getWritableDatabase();
			
			returnValue = db.update(
					ZeitabschnittTabelle.TABLE_NAME, // Tabelle
					values, // zu aktualisierende Werte
					idSelect, // WHERE Bedingung
					idArgs); // Parameter
			
			break;
		default:
			throw new IllegalArgumentException("Uri nicht bekannt!");
		}
		
		return returnValue;
	}

}
