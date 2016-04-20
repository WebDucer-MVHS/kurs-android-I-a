package db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.widget.ZoomControls;

/**
 * Created by kurs on 20.04.16.
 */
public class TimelogProvider extends ContentProvider {
    // Klassenvariablen
    private DbHelper _dbHelper;

    private final static UriMatcher _URI_MATCHER =
            new UriMatcher(UriMatcher.NO_MATCH);

    // Initialisierung des URI Matchers
    static {
        // URI für die Auflistung
        _URI_MATCHER.addURI(TimelogContract.AUTHORITY, // Provider Basis-URI
                TimelogContract.Timelog.CONTENT_DIRECTORY, // Unterordner
                TimelogTable.ITEM_LIST_ID); // Integer-ID
        // URI für einen Eintrag
        _URI_MATCHER.addURI(TimelogContract.AUTHORITY,
                TimelogContract.Timelog.CONTENT_DIRECTORY + "/#",
                TimelogTable.ITEM_ID);
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

        Cursor returnData = null;

        switch (uriType){
            case TimelogTable.ITEM_LIST_ID:
                SQLiteDatabase listDb = _dbHelper.getReadableDatabase();
                returnData = listDb.query(
                        TimelogTable.TABLE_NAME, // Tabelenname
                        projection, // Spalten, die interessant sind
                        selection, // Filter (WHERE)
                        selectionArgs, // Filter Parameter
                        null, // Gruppierung
                        null, // Having
                        sortOrder); // Sortierung
                break;

            case TimelogTable.ITEM_ID:
                long id = ContentUris.parseId(uri);
                String where = BaseColumns._ID + "=?";
                String[] whereArgs = new String[]{String.valueOf(id)};
                SQLiteDatabase singleDb = _dbHelper.getReadableDatabase();
                returnData = singleDb.query(
                        TimelogTable.TABLE_NAME, // Tabellenname
                        projection, // Spalten
                        where, // Filter für eine bestimmte ID
                        whereArgs, // ID als Parameter
                        null, // Gruppierung
                        null, // Having
                        null); // Sortierung
                break;
        }

        return returnData;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int uriType = _URI_MATCHER.match(uri);

        String returnType = null;

        switch (uriType) {
            case TimelogTable.ITEM_LIST_ID:
                returnType = TimelogContract.Timelog.CONTENT_TYPE;
                break;

            case TimelogTable.ITEM_ID:
                returnType = TimelogContract.Timelog.CONTENT_ITEM_TYPE;
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
            case TimelogTable.ITEM_LIST_ID:
            case TimelogTable.ITEM_ID:
                SQLiteDatabase db = _dbHelper.getWritableDatabase();
                id = db.insert(TimelogTable.TABLE_NAME, null, values);
                break;

            default:
                throw new IllegalArgumentException("Unbekannte URI: " + uri);
        }

        if (id != -1){
            // URI generieren
            returnUri = ContentUris.withAppendedId(
                    TimelogContract.Timelog.CONTENT_URI, id);

            // Benachrichtigen, dass neuer Datensatz hinzugefügt wurde
            getContext().getContentResolver().notifyChange(returnUri, null);
        }

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
