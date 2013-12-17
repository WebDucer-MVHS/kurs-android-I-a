package de.mvhs.android.zeiterfassung.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import de.mvhs.android.zeiterfassung.db.ZeitContract.Zeit;

public class ZeitProvider extends ContentProvider {
  /* Kalassen-Variblen */
  private DBHelper                _DBHelper    = null;
  private final static UriMatcher _URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

  static {
    _URI_MATCHER.addURI(ZeitContract.BASE_PATH, Zeit.CONTENT_DIRECTORY, ZeitenTable.ITEMS);
    _URI_MATCHER.addURI(ZeitContract.BASE_PATH, Zeit.CONTENT_DIRECTORY + "/#", ZeitenTable.ITEM_ID);
  }

  @Override
  public boolean onCreate() {
    _DBHelper = new DBHelper(getContext());
    return _DBHelper != null;
  }

  @Override
  public String getType(Uri uri) {
    String returnType = null;

    final int uriType = _URI_MATCHER.match(uri);

    switch (uriType) {
      case ZeitenTable.ITEMS:
        returnType = Zeit.CONTENT_TYPE;
        break;

      case ZeitenTable.ITEM_ID:
        returnType = Zeit.CONTENT_ITEM_TYPE;
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
        id = _DBHelper.getWritableDatabase().insert(ZeitenTable.TABLE_NAME, null, values);
        break;

      default:
        throw new IllegalArgumentException("Ich kenne diese URI nicht :-( '" + uri + "'");
    }

    if (id > 0) {
      insertUri = ContentUris.withAppendedId(Zeit.CONTENT_URI, id);
      getContext().getContentResolver().notifyChange(insertUri, null);
    }

    return insertUri;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    int updatedItems = 0;

    final int uriType = _URI_MATCHER.match(uri);

    switch (uriType) {
      case ZeitenTable.ITEMS:
        updatedItems = _DBHelper.getWritableDatabase().update(ZeitenTable.TABLE_NAME, values, selection, selectionArgs);
        break;

      case ZeitenTable.ITEM_ID:
        long id = ContentUris.parseId(uri);
        String idWhere = BaseColumns._ID + "=?";
        String[] idArgs = { String.valueOf(id) };
        updatedItems = _DBHelper.getWritableDatabase().update(ZeitenTable.TABLE_NAME, values, idWhere, idArgs);
        break;

      default:
        throw new IllegalArgumentException("Ich kenne diese URI nicht :-( '" + uri + "'");
    }

    if (updatedItems > 0) {
      getContext().getContentResolver().notifyChange(uri, null);
    }

    return updatedItems;
  }

  @Override
  public int delete(Uri uri, String where, String[] whereParams) {
    int deletedItems = 0;

    final int uriType = _URI_MATCHER.match(uri);

    switch (uriType) {
      case ZeitenTable.ITEMS:
        deletedItems = _DBHelper.getWritableDatabase().delete(ZeitenTable.TABLE_NAME, where, whereParams);
        break;

      case ZeitenTable.ITEM_ID:
        long id = ContentUris.parseId(uri);
        String idWhere = BaseColumns._ID + "=?";
        String[] idArgs = { String.valueOf(id) };
        deletedItems = _DBHelper.getWritableDatabase().delete(ZeitenTable.TABLE_NAME, idWhere, idArgs);
        break;

      default:
        throw new IllegalArgumentException("Ich kenne diese URI nicht :-( '" + uri + "'");
    }

    if (deletedItems > 0) {
      getContext().getContentResolver().notifyChange(uri, null);
    }

    return deletedItems;
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    Cursor returnData = null;

    final int uriType = _URI_MATCHER.match(uri);

    switch (uriType) {
      case ZeitenTable.ITEMS:
        returnData = _DBHelper.getReadableDatabase().query(ZeitenTable.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        break;

      case ZeitenTable.ITEM_ID:
        long id = ContentUris.parseId(uri);
        String idWhere = BaseColumns._ID + "=?";
        String[] idArgs = { String.valueOf(id) };
        returnData = _DBHelper.getReadableDatabase().query(ZeitenTable.TABLE_NAME, projection, idWhere, idArgs, null, null, null);
        break;

      default:
        throw new IllegalArgumentException("Ich kenne diese URI nicht :-( '" + uri + "'");
    }

    return returnData;
  }

}
