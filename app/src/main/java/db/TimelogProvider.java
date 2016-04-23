package db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.sql.Time;

public class TimelogProvider extends ContentProvider {
  // Klassenvariablen
  private DbHelper _dbHelper;

  private final static UriMatcher _URI_MATCHER =
      new UriMatcher( UriMatcher.NO_MATCH );

  // Initialisierung des URI Matchers
  static {
    // URI für die Auflistung
    _URI_MATCHER.addURI( TimelogContract.AUTHORITY, // Provider Basis-URI
        TimelogContract.Timelog.CONTENT_DIRECTORY, // Unterordner
        TimelogTable.ITEM_LIST_ID ); // Integer-ID
    // URI für einen Eintrag
    _URI_MATCHER.addURI( TimelogContract.AUTHORITY,
        TimelogContract.Timelog.CONTENT_DIRECTORY + "/#",
        TimelogTable.ITEM_ID );
  }

  @Override
  public boolean onCreate() {
    _dbHelper = new DbHelper( getContext() );
    return true;
  }

  @Nullable
  @Override
  public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
    Cursor returnData = null;
    TimelogProviderActionHandler actionHandler = buildActionHandler( uri );

    if( actionHandler != null ) {
      returnData = actionHandler.query( projection, selection, selectionArgs, sortOrder );
    }

    return returnData;
  }

  @Nullable
  @Override
  public String getType( Uri uri ) {
    final int uriType = _URI_MATCHER.match( uri );

    String returnType = null;

    switch( uriType ) {
      case TimelogTable.ITEM_LIST_ID:
        returnType = TimelogProviderList.contentType;
        break;

      case TimelogTable.ITEM_ID:
        returnType = TimelogProviderListItem.contentType;
        break;
    }

    return returnType;
  }

  @Nullable
  @Override
  public Uri insert( Uri uri, ContentValues values ) {
    TimelogProviderActionHandler actionHandler = buildOrRaiseActionHandler( uri  );;
    Uri returnUri = actionHandler.insert( values );

    if( returnUri != null ) {
      // Benachrichtigen, dass neuer Datensatz hinzugefügt wurde
      getContext().getContentResolver().notifyChange( returnUri, null );
    }

    return returnUri;
  }

  @Override
  public int delete( Uri uri, String selection, String[] selectionArgs ) {
    TimelogProviderActionHandler actionHandler = buildOrRaiseActionHandler( uri );

    return actionHandler.delete( null, null );
  }

  @Override
  public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs ) {
    TimelogProviderActionHandler actionHandler = buildOrRaiseActionHandler(  uri  );

    return actionHandler.update( values, selection, selectionArgs );
  }

  @Nullable
  private TimelogProviderActionHandler buildActionHandler( Uri uri ) {
    final int uriType = _URI_MATCHER.match( uri );
    TimelogProviderActionHandler actionHandler = null;

    switch( uriType ) {
      case TimelogTable.ITEM_LIST_ID:
        actionHandler = new TimelogProviderList( _dbHelper, uri );
        break;

      case TimelogTable.ITEM_ID:
        actionHandler = new TimelogProviderListItem( _dbHelper, uri );
        break;
    }
    return actionHandler;
  }

  private TimelogProviderActionHandler buildOrRaiseActionHandler( Uri uri ) {
    TimelogProviderActionHandler handler = buildActionHandler( uri );

    if( handler == null )
      throw new IllegalArgumentException( "Unbekannte URI: " + uri );

    return handler;
  }
}
