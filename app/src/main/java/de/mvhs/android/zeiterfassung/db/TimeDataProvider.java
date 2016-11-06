package de.mvhs.android.zeiterfassung.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Kurs on 19.10.2016.
 */

public class TimeDataProvider extends ContentProvider {
  // Helper für den Zugriff auf die Datenbank
  private DbHelper _dbHelper;

  // Filter
  private final static String _FILER_BY_ID = BaseColumns._ID + "=?";
  private final static String _NOT_FINISHED = "IFNULL("
      + TimeDataContract.TimeData.Columns.END
      + ",'')=''";

  // Mapping der URIs
  private final static UriMatcher _URI_MATCHER =
      new UriMatcher(UriMatcher.NO_MATCH);

  static {
    _URI_MATCHER.addURI(
        TimeDataContract.AUTHORITY, // Basis URI
        TimeDataContract.TimeData.CONTENT_DIRECTORY, // Unterordner
        TimeTable.ITEM_LIST_ID); // Eindeutige ID

    _URI_MATCHER.addURI(
        TimeDataContract.AUTHORITY, // Basis URI
        TimeDataContract.TimeData.CONTENT_DIRECTORY + "/#", // Unterordner mit ID
        TimeTable.ITEM_ID); // Eindeutige ID

    _URI_MATCHER.addURI(
        TimeDataContract.AUTHORITY, // Basis URI
        TimeDataContract.TimeData.NOT_FINISHED_CONTENT_DIRECTORY, // Unterordner für offenen Datensatz
        TimeTable.NOT_FINISHED_ITEM_ID); // Eindeutige ID
  }


  @Override
  public boolean onCreate() {
    _dbHelper = new DbHelper(getContext());
    return true;
  }

  @Nullable
  @Override
  public Cursor query(@NonNull  Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    final int uriType = _URI_MATCHER.match(uri);

    Cursor data;

    SQLiteDatabase db = _dbHelper.getReadableDatabase();

    switch (uriType) {
      case TimeTable.ITEM_LIST_ID:
        data = db.query(TimeTable.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        break;

      case TimeTable.ITEM_ID:
        long id = ContentUris.parseId(uri);
        String[] idArgs = new String[]{String.valueOf(id)};
        data = db.query(TimeTable.TABLE_NAME, projection, _FILER_BY_ID, idArgs, null, null, null);
        break;

      case TimeTable.NOT_FINISHED_ITEM_ID:
        data = db.query(TimeTable.TABLE_NAME, projection, _NOT_FINISHED, null, null, null, null);
        break;

      default:
        throw new IllegalArgumentException("Unbekannte URI: " + uri);
    }

    if (data != null) {
      data.setNotificationUri(getContext().getContentResolver(), uri);
    }

    return data;
  }

  @Nullable
  @Override
  public String getType(@NonNull Uri uri) {
    final int uriType = _URI_MATCHER.match(uri);

    String type = null;
    switch (uriType) {
      case TimeTable.ITEM_LIST_ID:
        type = TimeDataContract.TimeData.CONTENT_TYPE;
        break;

      case TimeTable.ITEM_ID:
      case TimeTable.NOT_FINISHED_ITEM_ID:
        type = TimeDataContract.TimeData.CONTENT_ITEM_TYPE;
        break;
    }

    return type;
  }

  @Nullable
  @Override
  public Uri insert(@NonNull Uri uri, ContentValues values) {
    final int uriType = _URI_MATCHER.match(uri);

    Uri insertUri = null;

    long id;

    switch (uriType) {
      case TimeTable.ITEM_ID:
      case TimeTable.ITEM_LIST_ID:
      case TimeTable.NOT_FINISHED_ITEM_ID:
        SQLiteDatabase db = _dbHelper.getWritableDatabase();
        id = db.insert(TimeTable.TABLE_NAME, null, values);
        break;

      default:
        throw new IllegalArgumentException("Unbekannte URI: " + uri);
    }

    if (id != 0) {
      // URI generieren
      insertUri = ContentUris.withAppendedId(
          TimeDataContract.TimeData.CONTENT_URI, // URI zu Tabelle
          id); // ID des Datensatzes

      // Benachrichtigen der interessierten Stellen
      getContext().getContentResolver().notifyChange(insertUri, null);
    }

    return insertUri;
  }

  @Override
  public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
    final int uriType = _URI_MATCHER.match(uri);

    int deleted;
    SQLiteDatabase db = _dbHelper.getWritableDatabase();

    switch (uriType) {
      case TimeTable.ITEM_LIST_ID:
        deleted = db.delete(TimeTable.TABLE_NAME, selection, selectionArgs);
        break;

      case TimeTable.ITEM_ID:
        // Extrahieren der ID aus der URI
        long id = ContentUris.parseId(uri);
        // Erstellen der Ergumente
        String[] idArgs = new String[]{String.valueOf(id)};
        // Löschen mit selbst definierten Argumenten
        deleted = db.delete(TimeTable.TABLE_NAME, _FILER_BY_ID, idArgs);
        break;

      case TimeTable.NOT_FINISHED_ITEM_ID:
        deleted = db.delete(TimeTable.TABLE_NAME, _NOT_FINISHED, null);
        break;

      default:
        throw new IllegalArgumentException("Unbekannte URI: " + uri);
    }

    if (deleted > 0) {
      getContext().getContentResolver().notifyChange(uri, null);
    }

    return deleted;
  }

  @Override
  public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    final int uriType = _URI_MATCHER.match(uri);

    int updatedItems;
    SQLiteDatabase db = _dbHelper.getWritableDatabase();

    switch (uriType) {
      case TimeTable.ITEM_LIST_ID:
        updatedItems = db.update(TimeTable.TABLE_NAME, values, selection, selectionArgs);
        break;

      case TimeTable.ITEM_ID:
        long id = ContentUris.parseId(uri);
        String[] idArgs = new String[]{String.valueOf(id)};
        updatedItems = db.update(TimeTable.TABLE_NAME, values, _FILER_BY_ID, idArgs);
        break;

      case TimeTable.NOT_FINISHED_ITEM_ID:
        updatedItems = db.update(TimeTable.TABLE_NAME, values, _NOT_FINISHED, null);
        break;

      default:
        throw new IllegalArgumentException("Unbekannte URI: " + uri);
    }

    if (updatedItems > 0) {
      getContext().getContentResolver().notifyChange(uri, null);
    }

    return updatedItems;
  }
}
