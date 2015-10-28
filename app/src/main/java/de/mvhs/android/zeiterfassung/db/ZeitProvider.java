package de.mvhs.android.zeiterfassung.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by kurs on 21.10.15.
 */
public class ZeitProvider extends ContentProvider {
  /* Klassenvariablen */
  private DbHelper _dbHelper;
  public static final String _WHERE_ID = "_id=?";
  public static final String _EMPTY_WHERE = "IFNULL(EndZeit,'')=''";

  private final static UriMatcher _URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

  static {
    // URI für die Liste der Zeiten
    _URI_MATCHER.addURI(ZeitContract.AUTHORITY,
        ZeitContract.ZeitDaten.CONTENT_DIRECTORY,
        ZeitenTabelle.ITEM_LIST_ID);
    // URI für einen Zeiten-Eintrag
    _URI_MATCHER.addURI(ZeitContract.AUTHORITY,
        ZeitContract.ZeitDaten.CONTENT_DIRECTORY + "/#",
        ZeitenTabelle.ITEM_ID);
    // URI für den leeren Datensatz
    _URI_MATCHER.addURI(ZeitContract.AUTHORITY,
        ZeitContract.ZeitDaten.EMPTY_CONTENT_DIRECTORY,
        ZeitenTabelle.EMPTY_ITEM_ID);
  }

  @Override
  public boolean onCreate() {
    _dbHelper = new DbHelper(getContext());
    return true;
  }

  @Nullable
  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    final int uriType = _URI_MATCHER.match(uri);

    Cursor resultData = null;

    switch (uriType) {
      case ZeitenTabelle.EMPTY_ITEM_ID:
        SQLiteDatabase emptyDb = _dbHelper.getReadableDatabase();
        resultData = emptyDb.query(ZeitenTabelle.TABLE_NAME, projection, _EMPTY_WHERE, null, null, null, null);
        break;

      case ZeitenTabelle.ITEM_ID:
        SQLiteDatabase singleDb = _dbHelper.getReadableDatabase();
        long id = ContentUris.parseId(uri);
        String[] whereArgs = new String[]{String.valueOf(id)};
        resultData = singleDb.query(ZeitenTabelle.TABLE_NAME, projection, _WHERE_ID, whereArgs, null, null, null);
        break;

      case ZeitenTabelle.ITEM_LIST_ID:
        SQLiteDatabase db = _dbHelper.getReadableDatabase();
        resultData = db.query(ZeitenTabelle.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        break;

      default:
        throw new IllegalArgumentException("Unbekannte URI: " + uri);
    }

    if (resultData != null) {
      // Registrierung für die Benachrichtigung
      resultData.setNotificationUri(getContext().getContentResolver(), uri);
    }

    return resultData;
  }

  @Nullable
  @Override
  public String getType(Uri uri) {
    final int uriType = _URI_MATCHER.match(uri);

    String returnType = null;

    switch (uriType) {
      case ZeitenTabelle.ITEM_LIST_ID:
        returnType = ZeitContract.ZeitDaten.CONTENT_TYPE;
        break;

      case ZeitenTabelle.ITEM_ID:
      case ZeitenTabelle.EMPTY_ITEM_ID:
        returnType = ZeitContract.ZeitDaten.CONTENT_ITEM_TYPE;
        break;
    }

    return returnType;
  }

  @Nullable
  @Override
  public Uri insert(Uri uri, ContentValues values) {
    final int uriType = _URI_MATCHER.match(uri);
    Uri returnUri = null;
    long id = -1;

    switch (uriType) {
      case ZeitenTabelle.ITEM_LIST_ID:
      case ZeitenTabelle.ITEM_ID:
      case ZeitenTabelle.EMPTY_ITEM_ID:
        SQLiteDatabase db = _dbHelper.getWritableDatabase();
        id = db.insert(ZeitenTabelle.TABLE_NAME, null, values);
        break;

      default:
        throw new IllegalArgumentException("Unbekannte URI: " + uri);
    }

    if (id != -1) {
      // Uri generieren
      returnUri = ContentUris.withAppendedId(ZeitContract.ZeitDaten.CONTENT_URI, id);

      // Benachrichtigung, dass die Daten sich geändert haben
      getContext().getContentResolver().notifyChange(returnUri, null);
    }

    return returnUri;
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    final int uriType = _URI_MATCHER.match(uri);
    int deletedItems = 0;

    switch (uriType) {
      case ZeitenTabelle.EMPTY_ITEM_ID:
        SQLiteDatabase emptyDb = _dbHelper.getWritableDatabase();
        deletedItems = emptyDb.delete(ZeitenTabelle.TABLE_NAME, _EMPTY_WHERE, null);
        break;

      case ZeitenTabelle.ITEM_ID:
        SQLiteDatabase singleDb = _dbHelper.getWritableDatabase();
        long id = ContentUris.parseId(uri);
        String[] whereArgs = new String[]{String.valueOf(id)};
        deletedItems = singleDb.delete(ZeitenTabelle.TABLE_NAME, _WHERE_ID, whereArgs);
        break;

      case ZeitenTabelle.ITEM_LIST_ID:
        SQLiteDatabase db = _dbHelper.getWritableDatabase();
        deletedItems = db.delete(ZeitenTabelle.TABLE_NAME, selection, selectionArgs);
        break;

      default:
        throw new IllegalArgumentException("Unbekannte URI: " + uri);
    }

    if (deletedItems > 0) {
      // Benachrichtigung, dass die Daten sich geändert haben
      getContext().getContentResolver().notifyChange(uri, null);
    }

    return deletedItems;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    final int uriType = _URI_MATCHER.match(uri);

    int updatedItems = 0;

    switch (uriType) {
      case ZeitenTabelle.EMPTY_ITEM_ID:
        SQLiteDatabase emptyDb = _dbHelper.getWritableDatabase();
        updatedItems = emptyDb.update(ZeitenTabelle.TABLE_NAME, values, _EMPTY_WHERE, null);
        break;

      case ZeitenTabelle.ITEM_ID:
        SQLiteDatabase singleDb = _dbHelper.getWritableDatabase();
        long id = ContentUris.parseId(uri);
        String[] whereArgs = new String[]{String.valueOf(id)};
        updatedItems = singleDb.update(ZeitenTabelle.TABLE_NAME, values, _WHERE_ID, whereArgs);
        break;

      case ZeitenTabelle.ITEM_LIST_ID:
        SQLiteDatabase db = _dbHelper.getWritableDatabase();
        updatedItems = db.update(ZeitenTabelle.TABLE_NAME, values, selection, selectionArgs);
        break;

      default:
        throw new IllegalArgumentException("Unbekannte URI: " + uri);
    }

    if (updatedItems > 0) {
      // Benachrichtigung, dass die Daten sich geändert haben
      getContext().getContentResolver().notifyChange(uri, null);
    }

    return updatedItems;
  }
}
