package db;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public abstract class TimelogProviderActionHandler {
  protected DbHelper dbHelper;
  protected Uri uri;

  public TimelogProviderActionHandler( DbHelper dbHelper, Uri uri ) {
    this.dbHelper = dbHelper;
    this.uri = uri;
  }

  public abstract int update( ContentValues values, String selection, String[] selectionArgs );

  public abstract int delete( String selection, String[] selectionArgs );

  public abstract Uri insert( ContentValues values );

  public abstract Cursor query( String[] projection, String selection, String[] selectionArgs, String sortOrder );
}
