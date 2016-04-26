package db;

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
 * Content Provider für die App
 */
public class TimelogProvider extends ContentProvider {
    // Helper für die Datenbank
    private DbHelper _dbHelper;

    // Filter für die ID-Spalte
    private static final String _WHERE_ID = BaseColumns._ID + "=?";
    // Filter für den nicht beendeten Datensatz
    private static final String _WHERE_NOT_FINISHED = "IFNULL("
            + TimelogContract.Timelog.Columns.END + ",'')=''";
    // Nicht gesetzte ID
    private static final long _NO_ID = -1;

    // Map für die Auflösung der URIs
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
                TimelogContract.Timelog.CONTENT_DIRECTORY + "/#", // # steht für beliebige Zahl
                TimelogTable.ITEM_ID);
        // URI für nicht beendeten Datensatz
        _URI_MATCHER.addURI(TimelogContract.AUTHORITY,
                TimelogContract.Timelog.NOT_FINISHED_DIRECTORY,
                TimelogTable.NOT_FINISHED_ITEM_ID);
    }

    @Override
    public boolean onCreate() {
        _dbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int uriType = _URI_MATCHER.match(uri);

        String returnType = null;

        switch (uriType) {
            case TimelogTable.ITEM_LIST_ID:
                returnType = TimelogContract.Timelog.CONTENT_TYPE;
                break;

            case TimelogTable.ITEM_ID:
            case TimelogTable.NOT_FINISHED_ITEM_ID:
                returnType = TimelogContract.Timelog.CONTENT_ITEM_TYPE;
                break;
        }

        return returnType;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final int uriType = _URI_MATCHER.match(uri);

        Uri returnUri = null;
        long id;

        switch (uriType) {
            case TimelogTable.ITEM_LIST_ID:
            case TimelogTable.ITEM_ID:
            case TimelogTable.NOT_FINISHED_ITEM_ID:
                SQLiteDatabase db = _dbHelper.getWritableDatabase();
                id = db.insert(TimelogTable.TABLE_NAME, null, values);
                break;

            default:
                throw new IllegalArgumentException("Unbekannte URI: " + uri);
        }

        if (id != _NO_ID) {
            // URI generieren
            returnUri = ContentUris.withAppendedId(
                    TimelogContract.Timelog.CONTENT_URI, id);

            // Benachrichtigen, dass neuer Datensatz hinzugefügt wurde
            getContext().getContentResolver().notifyChange(returnUri, null);
        }

        return returnUri;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final int uriType = _URI_MATCHER.match(uri);

        Cursor returnData;
        SQLiteDatabase db = _dbHelper.getReadableDatabase();

        switch (uriType) {
            case TimelogTable.ITEM_LIST_ID:
                returnData = db.query(
                        TimelogTable.TABLE_NAME, // Tabelle
                        projection, // Spalten, die interessant sind
                        selection, // Filter (WHERE)
                        selectionArgs, // Filter Parameter
                        null, // Gruppierung
                        null, // Having
                        sortOrder); // Sortierung
                break;

            case TimelogTable.ITEM_ID:
                long id = ContentUris.parseId(uri);
                String[] whereArgs = new String[]{String.valueOf(id)};
                returnData = db.query(
                        TimelogTable.TABLE_NAME, // Tabelle
                        projection, // Spalten
                        _WHERE_ID, // Filter für eine bestimmte ID
                        whereArgs, // ID als Parameter
                        null, // Gruppierung
                        null, // Having
                        null); // Sortierung
                break;

            case TimelogTable.NOT_FINISHED_ITEM_ID:
                returnData = db.query(
                        TimelogTable.TABLE_NAME, // Tabelle
                        projection, // Spalten
                        _WHERE_NOT_FINISHED, // Filter für nicht beendeten Datensatz
                        null, // keine Parameter
                        null, // keine Gruppierung
                        null, // keine Having
                        null); // keine Sortierung
                break;

            default:
                throw new IllegalArgumentException("Unbekannte URI: " + uri);
        }

        if (returnData != null) {
            // Registrieung für die Benachrichtigungen über Änderungen
            returnData.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return returnData;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final int uriType = _URI_MATCHER.match(uri);

        int deletedItems = 0;
        SQLiteDatabase db = _dbHelper.getWritableDatabase();

        switch (uriType) {
            case TimelogTable.ITEM_LIST_ID:
                deletedItems = db.delete(
                        TimelogTable.TABLE_NAME, // Tabelle
                        selection, // Filter (WHERE)
                        selectionArgs); // Parameter für den Filter
                break;

            case TimelogTable.ITEM_ID:
                long id = ContentUris.parseId(uri);
                String[] whereArgs = {String.valueOf(id)};
                deletedItems = db.delete(
                        TimelogTable.TABLE_NAME, // Tabelle
                        _WHERE_ID, // Filter auf die ID Spalte
                        whereArgs); // Parameter - ID
                break;

            case TimelogTable.NOT_FINISHED_ITEM_ID:
                deletedItems = db.delete(
                        TimelogTable.TABLE_NAME, // Tabelle
                        _WHERE_NOT_FINISHED, // Filter für nicht beendeten Datensatz
                        null); // keine Parameter
                break;

            default:
                throw new IllegalArgumentException("Unbekannte URI: " + uri);
        }

        if (deletedItems > 0) {
            // Benachrichtigung über die Datenänderungen
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deletedItems;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int uriType = _URI_MATCHER.match(uri);

        int updatedItems = 0;
        SQLiteDatabase db = _dbHelper.getWritableDatabase();

        switch (uriType) {
            case TimelogTable.ITEM_LIST_ID:
                updatedItems = db.update(
                        TimelogTable.TABLE_NAME, // Tabelle
                        values, // Werte, die aktuallisiert werden sollen
                        selection, // Filter (WHERE)
                        selectionArgs); // Filter Parameter
                break;

            case TimelogTable.ITEM_ID:
                long id = ContentUris.parseId(uri);
                String[] whereArgs = {String.valueOf(id)};
                updatedItems = db.update(
                        TimelogTable.TABLE_NAME, // Tabelle
                        values, // Zu aktualisierende Werte
                        _WHERE_ID, // Filter auf die ID
                        whereArgs); // ID Parameter
                break;

            case TimelogTable.NOT_FINISHED_ITEM_ID:
                updatedItems = db.update(
                        TimelogTable.TABLE_NAME, // Tabelle
                        values, // Zu aktualisierende Werte
                        _WHERE_NOT_FINISHED, // Filter für nicht beendeten Datensatz
                        null); // keine Parameter
                break;

            default:
                throw new IllegalArgumentException("Unbekannte URI: " + uri);
        }

        if (updatedItems > 0) {
            // Benachrichtigung über die Datenänderungen
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updatedItems;
    }
}
