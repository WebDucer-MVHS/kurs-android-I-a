package de.mvhs.android.zeiterfassung.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

public class ZeitenProvider extends ContentProvider {

    /* Klassen variablen */
    // SQLite Helper
    private DBHelper _dbHelper;

    // Mapping-Klasse
    private final static UriMatcher _URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        _URI_MATCHER.addURI(ZeitContract.AUTHORITY, ZeitContract.ZeitDaten.CONTENT_DIRECTORY, ZeitenTable.ITEM_LIST_ID);
        _URI_MATCHER.addURI(ZeitContract.AUTHORITY, ZeitContract.ZeitDaten.CONTENT_DIRECTORY + "/#", ZeitenTable.ITEM_ID);
    }

    @Override
    public boolean onCreate() {
        _dbHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int uriType = _URI_MATCHER.match(uri);

        int deletedItems = 0;

        switch (uriType){
            case ZeitenTable.ITEM_LIST_ID:
                SQLiteDatabase db = _dbHelper.getWritableDatabase();
                deletedItems = db.delete(ZeitenTable.TABLE_NAME, selection, selectionArgs);
                break;

            case ZeitenTable.ITEM_ID:
                SQLiteDatabase dbSingle = _dbHelper.getWritableDatabase();
                long id = ContentUris.parseId(uri);
                String where = BaseColumns._ID + "=?";
                String[] whereArgs = new String[]{String.valueOf(id)};
                deletedItems = dbSingle.delete(ZeitenTable.TABLE_NAME, where, whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unbekannte URI: " + uri);
        }

        return deletedItems;
    }

    @Override
    public String getType(Uri uri) {
        final int uriType = _URI_MATCHER.match(uri);

        String returnType = null;

        switch (uriType){
            case ZeitenTable.ITEM_LIST_ID:
                returnType = ZeitContract.ZeitDaten.CONTENT_TYPE;
                break;
            case ZeitenTable.ITEM_ID:
                returnType = ZeitContract.ZeitDaten.CONTENT_ITEM_TYPE;
                break;
        }

        return returnType;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int uriType = _URI_MATCHER.match(uri);

        Uri insertUri = null;
        long id = -1;

        switch (uriType){
            case ZeitenTable.ITEM_LIST_ID:
            case ZeitenTable.ITEM_ID:
                SQLiteDatabase db = _dbHelper.getWritableDatabase();
                id = db.insert(ZeitenTable.TABLE_NAME, null, values);
                break;

            default:
                throw new IllegalArgumentException("Unbekannte URI: " + uri);
        }

        if(id != -1){
            insertUri = ContentUris.withAppendedId(ZeitContract.ZeitDaten.CONTENT_URI, id);

            // Benachrichtigung über Datenänderung
            getContext().getContentResolver().notifyChange(insertUri, null);
        }

        return insertUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
