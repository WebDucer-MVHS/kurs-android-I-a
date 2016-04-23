package db;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

public class TimelogProviderListItem extends TimelogProviderActionHandler {
  public static final String contentType = TimelogContract.Timelog.CONTENT_ITEM_TYPE;
  private static final String whereById = BaseColumns._ID + "= ?";

  private long id;

  public TimelogProviderListItem( DbHelper dbHelper, Uri uri ) {
    super( dbHelper, uri );
    this.id = ContentUris.parseId( uri );
  }

  private String[] whereArgsById() {
    return new String[]{ String.valueOf( id ) };
  }

  @Override
  public int update( ContentValues values, String selection, String[] selectionArgs ) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    return db.update( TimelogTable.TABLE_NAME,
                      values,
                      TimelogProviderListItem.whereById,
                      whereArgsById() );
  }

  @Override
  public int delete( String selection, String[] selectionArgs ) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    return db.delete( TimelogTable.TABLE_NAME,
                      TimelogProviderListItem.whereById,
                      whereArgsById() );
  }

  @Nullable
  @Override
  public Uri insert(  ContentValues values ) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    long newElementId = db.insert( TimelogTable.TABLE_NAME, null, values );
    return ContentUris.withAppendedId( TimelogContract.Timelog.CONTENT_URI, newElementId );
  }

  @Nullable
  @Override
  public Cursor query( String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
    SQLiteDatabase db = dbHelper.getReadableDatabase();
    return db.query( TimelogTable.TABLE_NAME,
                     projection,
                     TimelogProviderListItem.whereById,
                     whereArgsById(),
                     null,
                     null,
                     null );
  }

}
