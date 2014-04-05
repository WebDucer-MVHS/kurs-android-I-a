package de.mvhs.android.zeiterfassung.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class ZeitProvider extends ContentProvider {
  private static final String     _EQUALS      = "=?";

  // Klassenvariablen
  /* Instanz unseres Helpers */
  private DBHelper                _DBHelper    = null;

  /* Lookup für verwaltete URIs */
  private final static UriMatcher _URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

  static {
    _URI_MATCHER.addURI(ZeitContracts.BASE_PATH, ZeitContracts.Zeit.CONTENT_DIRECTORY, ZeitTable.ITEMS); // Uri
    // für
    // Auflistung
    _URI_MATCHER.addURI(ZeitContracts.BASE_PATH, ZeitContracts.Zeit.CONTENT_DIRECTORY + "/#", ZeitTable.ITEM_ID); // Uri
    // für
    // einen
    // Datensatz
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
        id = _DBHelper.getWritableDatabase().insert(ZeitTable.TABLE_NAME, null, values);
        break;
  
      default:
        throw new IllegalArgumentException("Uri ist nicht bekannt und kann nicht verarbeitet werden! [" + uri + "]");
    }
  
    if (id > 0) {
      returnUri = ContentUris.withAppendedId(ZeitContracts.Zeit.CONTENT_URI, id);
      // Benachrichtigung der anderen Verwerter des Content Providers über
      // Änderungen
      getContext().getContentResolver().notifyChange(returnUri, null);
    }
  
    return returnUri;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    int updatedItems = 0;
  
    final int uriType = _URI_MATCHER.match(uri);
  
    switch (uriType) {
      case ZeitTable.ITEMS:
        updatedItems = _DBHelper.getWritableDatabase().update(ZeitTable.TABLE_NAME, values, selection, selectionArgs);
        break;
  
      case ZeitTable.ITEM_ID:
        long id = ContentUris.parseId(uri);
        String where = ZeitContracts.Zeit.Columns._ID + _EQUALS;
  
        String[] whereArgs = new String[] { String.valueOf(id) };
  
        updatedItems = _DBHelper.getWritableDatabase().update(ZeitTable.TABLE_NAME, values, where, whereArgs);
        break;
  
      default:
        throw new IllegalArgumentException("Uri ist nicht bekannt und kann nicht verarbeitet werden! [" + uri + "]");
    }
  
    if (updatedItems > 0) {
      getContext().getContentResolver().notifyChange(uri, null);
    }
  
    return updatedItems;
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    Cursor returnData = null;
  
    final int uriType = _URI_MATCHER.match(uri);
  
    switch (uriType) {
      case ZeitTable.ITEMS:
        returnData = _DBHelper.getReadableDatabase().query(ZeitTable.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        break;
  
      case ZeitTable.ITEM_ID:
        long id = ContentUris.parseId(uri);
        String where = ZeitContracts.Zeit.Columns._ID + _EQUALS;
  
        String[] whereArgs = new String[] { String.valueOf(id) };
  
        returnData = _DBHelper.getReadableDatabase().query(ZeitTable.TABLE_NAME, projection, where, whereArgs, null, null, null);
        break;
  
      default:
        throw new IllegalArgumentException("Uri ist nicht bekannt und kann nicht verarbeitet werden! [" + uri + "]");
    }
  
    return returnData;
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    int deletedItems = 0;

    final int uriType = _URI_MATCHER.match(uri);

    switch (uriType) {
      case ZeitTable.ITEMS:
        deletedItems = _DBHelper.getWritableDatabase().delete(ZeitTable.TABLE_NAME, selection, selectionArgs);
        break;

      case ZeitTable.ITEM_ID:
        long id = ContentUris.parseId(uri);
        String where = ZeitContracts.Zeit.Columns._ID + _EQUALS;

        String[] whereArgs = new String[] { String.valueOf(id) };

        deletedItems = _DBHelper.getWritableDatabase().delete(ZeitTable.TABLE_NAME, where, whereArgs);
        break;

      default:
        throw new IllegalArgumentException("Uri ist nicht bekannt und kann nicht verarbeitet werden! [" + uri + "]");
    }

    if (deletedItems > 0) {
      getContext().getContentResolver().notifyChange(uri, null);
    }

    return deletedItems;
  }

}
