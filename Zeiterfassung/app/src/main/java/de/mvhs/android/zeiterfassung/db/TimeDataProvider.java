package de.mvhs.android.zeiterfassung.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Kurs on 05.04.2017.
 */

public class TimeDataProvider extends ContentProvider {
    private DbHelper _dbHelper;

    private final static UriMatcher _URI_MATCHER =
            new UriMatcher(UriMatcher.NO_MATCH);

    static {
        _URI_MATCHER.addURI(TimeContract.AUTHORITY, // Authority
                TimeContract.TimeData.CONTENT_DIRECTORY, // unterordner
                TimeTable.ITEM_LIST_ID); // ID für die Auflösung

        _URI_MATCHER.addURI(TimeContract.AUTHORITY,
                TimeContract.TimeData.CONTENT_DIRECTORY + "/#",
                TimeTable.ITEM_ID);
    }

    @Override
    public boolean onCreate() {
        _dbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int uriType = _URI_MATCHER.match(uri);

        switch (uriType){
            case TimeTable.ITEM_LIST_ID:
                return TimeContract.TimeData.CONTENT_TYPE;

            case TimeTable.ITEM_ID:
                return TimeContract.TimeData.CONTENT_ITEM_TYPE;
        }

        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int uriType = _URI_MATCHER.match(uri);
        Uri insertUri = null;

        SQLiteDatabase db = _dbHelper.getWritableDatabase();
        long id = -1;

        switch (uriType){
            case TimeTable.ITEM_LIST_ID:
            case TimeTable.ITEM_ID:
                id = db.insert(TimeTable.TABLE_NAME, null, values);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (id != -1){
            insertUri = ContentUris.withAppendedId(
                    TimeContract.TimeData.CONTENT_URI, // Provider Unterordner fürdiese Daten
                    id); // ID des neuen Datensatzes

            // Benachrichtigung über die Änderungen
            getContext().getContentResolver().notifyChange(insertUri, null);
        }

        return insertUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int uriType = _URI_MATCHER.match(uri);
        int deletedItems = 0;

        SQLiteDatabase db = _dbHelper.getWritableDatabase();

        switch (uriType){
            case TimeTable.ITEM_LIST_ID:
                deletedItems = db.delete(TimeTable.TABLE_NAME, selection, selectionArgs);
                break;

            case TimeTable.ITEM_ID:
                // ID aus der URI auslesen
                long id = ContentUris.parseId(uri);
                // Filter definieren
                String where = TimeContract.TimeData.Columns._ID + "=?";
                // Filter Parameter
                String[] whereArgs = new String[]{String.valueOf(id)};
                // Datensatz löschen
                deletedItems = db.delete(TimeTable.TABLE_NAME, where, whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if(deletedItems > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }



        return deletedItems;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
