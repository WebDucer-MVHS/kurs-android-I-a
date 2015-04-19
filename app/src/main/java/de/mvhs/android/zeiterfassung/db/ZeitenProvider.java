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

   static {
      _URI_MATCHER.addURI(ZeitContract.AUTHORITY, ZeitContract.ZeitDaten.CONTENT_DIRECTORY, ZeitenTable.ITEM_LIST_ID);
      _URI_MATCHER.addURI(ZeitContract.AUTHORITY, ZeitContract.ZeitDaten.CONTENT_DIRECTORY + "/#", ZeitenTable.ITEM_ID);
      _URI_MATCHER.addURI(ZeitContract.AUTHORITY, ZeitContract.ZeitDaten.EMPTY_CONTENT_DIRECTORY, ZeitenTable.EMPTY_ITEM_ID);
   }

   // Nicht geschlossener Eintrag
   private final static String _EMPTY_ENTRY_WHERE = "IFNULL(" + ZeitContract.ZeitDaten.Columns.END_TIME + ",'')=''";
   public static final String _SINGLE_WHERE = BaseColumns._ID + "=?";

   @Override
   public boolean onCreate() {
      _dbHelper = new DBHelper(getContext());
      return true;
   }

   @Override
   public String getType(Uri uri) {
      final int uriType = _URI_MATCHER.match(uri);

      String returnType = null;

      switch (uriType) {
         case ZeitenTable.ITEM_LIST_ID:
            returnType = ZeitContract.ZeitDaten.CONTENT_TYPE;
            break;
         case ZeitenTable.ITEM_ID:
         case ZeitenTable.EMPTY_ITEM_ID:
            returnType = ZeitContract.ZeitDaten.CONTENT_ITEM_TYPE;
            break;
      }

      return returnType;
   }

   @Override
   public Uri insert(Uri uri, ContentValues values) {
      final int uriType = _URI_MATCHER.match(uri);

      Uri insertUri = null;
      long id;

      switch (uriType) {
         case ZeitenTable.ITEM_LIST_ID:
         case ZeitenTable.ITEM_ID:
         case ZeitenTable.EMPTY_ITEM_ID:
            SQLiteDatabase db = _dbHelper.getWritableDatabase();
            id = db.insert(ZeitenTable.TABLE_NAME, null, values);
            break;

         default:
            throw new IllegalArgumentException("Unbekannte URI: " + uri);
      }

      if (id != -1) {
         // Uri für neue eingefügten Datensatz erzeugen
         insertUri = ContentUris.withAppendedId(ZeitContract.ZeitDaten.CONTENT_URI, id);

         // Benachrichtigung über Datenänderung
         getContext().getContentResolver().notifyChange(insertUri, null);
      }

      return insertUri;
   }

   @Override
   public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

      final int uriType = _URI_MATCHER.match(uri);

      Cursor data;

      switch (uriType) {
         case ZeitenTable.ITEM_LIST_ID:
            SQLiteDatabase db = _dbHelper.getReadableDatabase();
            data = db.query(ZeitenTable.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            break;

         case ZeitenTable.ITEM_ID:
            SQLiteDatabase dbSingle = _dbHelper.getReadableDatabase();
            long id = ContentUris.parseId(uri);
            String[] whereArgs = new String[] {String.valueOf(id)};
            data = dbSingle.query(ZeitenTable.TABLE_NAME, projection, _SINGLE_WHERE, whereArgs, null, null, null);
            break;

         case ZeitenTable.EMPTY_ITEM_ID:
            data = _dbHelper.getReadableDatabase().query(ZeitenTable.TABLE_NAME, projection, _EMPTY_ENTRY_WHERE, null, null, null, null);
            break;

         default:
            throw new IllegalArgumentException("Unbekannte URI: " + uri);
      }

      if (data != null) {
         data.setNotificationUri(getContext().getContentResolver(), uri);
      }

      return data;
   }

   @Override
   public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
      final int uriType = _URI_MATCHER.match(uri);

      int updatedItems;

      switch (uriType) {
         case ZeitenTable.ITEM_LIST_ID:
            SQLiteDatabase db = _dbHelper.getWritableDatabase();
            updatedItems = db.update(ZeitenTable.TABLE_NAME, values, selection, selectionArgs);
            break;

         case ZeitenTable.ITEM_ID:
            SQLiteDatabase dbSingle = _dbHelper.getWritableDatabase();
            long id = ContentUris.parseId(uri);
            String[] whereArgs = new String[] {String.valueOf(id)};
            updatedItems = dbSingle.update(ZeitenTable.TABLE_NAME, values, _SINGLE_WHERE, whereArgs);
            break;

         case ZeitenTable.EMPTY_ITEM_ID:
            updatedItems = _dbHelper.getWritableDatabase().update(ZeitenTable.TABLE_NAME, values, _EMPTY_ENTRY_WHERE, null);
            break;

         default:
            throw new IllegalArgumentException("Unbekannte URI: " + uri);
      }

      if (updatedItems > 0) {
         getContext().getContentResolver().notifyChange(uri, null);
      }

      return updatedItems;
   }

   @Override
   public int delete(Uri uri, String selection, String[] selectionArgs) {
      final int uriType = _URI_MATCHER.match(uri);

      int deletedItems;

      switch (uriType) {
         case ZeitenTable.ITEM_LIST_ID:
            SQLiteDatabase db = _dbHelper.getWritableDatabase();
            deletedItems = db.delete(ZeitenTable.TABLE_NAME, selection, selectionArgs);
            break;

         case ZeitenTable.ITEM_ID:
            SQLiteDatabase dbSingle = _dbHelper.getWritableDatabase();
            long id = ContentUris.parseId(uri);
            String[] whereArgs = new String[] {String.valueOf(id)};
            deletedItems = dbSingle.delete(ZeitenTable.TABLE_NAME, _SINGLE_WHERE, whereArgs);
            break;

         case ZeitenTable.EMPTY_ITEM_ID:
            deletedItems = _dbHelper.getWritableDatabase().delete(ZeitenTable.TABLE_NAME, _EMPTY_ENTRY_WHERE, null);
            break;

         default:
            throw new IllegalArgumentException("Unbekannte URI: " + uri);
      }

      if (deletedItems > 0) {
         // Benachrichtigung über Datenänderung
         getContext().getContentResolver().notifyChange(uri, null);
      }

      return deletedItems;
   }
}
