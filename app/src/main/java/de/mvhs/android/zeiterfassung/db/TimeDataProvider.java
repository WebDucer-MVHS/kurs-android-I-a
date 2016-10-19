package de.mvhs.android.zeiterfassung.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

/**
 * Created by Kurs on 19.10.2016.
 */

public class TimeDataProvider extends ContentProvider {
    // Helper für den Zugriff auf die Datenbank
    private DbHelper _dbHelper;

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
    }


    @Override
    public boolean onCreate() {
        _dbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int uriType = _URI_MATCHER.match(uri);

        String type = null;
        switch (uriType) {
            case TimeTable.ITEM_LIST_ID:
                type = TimeDataContract.TimeData.CONTENT_TYPE;
                break;

            case TimeTable.ITEM_ID:
                type = TimeDataContract.TimeData.CONTENT_ITEM_TYPE;
                break;
        }

        return type;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int uriType = _URI_MATCHER.match(uri);

        Uri insertUri = null;

        long id = 0;

        switch (uriType) {
            case TimeTable.ITEM_ID:
            case TimeTable.ITEM_LIST_ID:
                SQLiteDatabase db = _dbHelper.getWritableDatabase();
                id = db.insert(TimeTable.TABLE_NAME, null, values);
                break;

            default:
                throw new IllegalArgumentException("Unbekannte URI: " + uri);
        }

        if (id != 0){
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
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int uriType = _URI_MATCHER.match(uri);

        int deleted = 0;
        SQLiteDatabase db = _dbHelper.getWritableDatabase();

        switch (uriType){
            case TimeTable.ITEM_LIST_ID:
                deleted = db.delete(TimeTable.TABLE_NAME, selection, selectionArgs);
                break;

            case TimeTable.ITEM_ID:
                long id = ContentUris.parseId(uri);
                String where = BaseColumns._ID + "=?";
                String[] whereArgs = new String[]{String.valueOf(id)};
                db.delete(TimeTable.TABLE_NAME, where, whereArgs);
                break;
        }

        if (deleted > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
