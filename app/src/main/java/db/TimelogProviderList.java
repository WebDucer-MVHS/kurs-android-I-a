package db;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class TimelogProviderList extends TimelogProviderActionHandler {
  public static final String contentType = TimelogContract.Timelog.CONTENT_TYPE;

  public TimelogProviderList( DbHelper dbHelper, Uri uri ) {
    super( dbHelper, uri );
  }

  @Override
  public int update( ContentValues values, String selection, String[] selectionArgs ) {
    SQLiteDatabase db = this.dbHelper.getWritableDatabase();
    return db.update( TimelogTable.TABLE_NAME, values, selection, selectionArgs );
  }

  @Override
  public int delete( String selection, String[] selectionArgs ) {
    SQLiteDatabase db = this.dbHelper.getWritableDatabase();
    return db.delete( TimelogTable.TABLE_NAME, selection, selectionArgs );
  }

  @Nullable
  @Override
  public Uri insert( ContentValues values ) {
    SQLiteDatabase db = this.dbHelper.getWritableDatabase();
    long newElementId = db.insert( TimelogTable.TABLE_NAME, null, values );

    return ( newElementId == -1 ) ?
              null :
              ContentUris.withAppendedId( TimelogContract.Timelog.CONTENT_URI, newElementId );
  }

  @Nullable
  @Override
  public Cursor query( String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
    SQLiteDatabase db = dbHelper.getReadableDatabase();

    return db.query( TimelogTable.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );
  }
}
