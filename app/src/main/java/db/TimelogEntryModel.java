package db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class TimelogEntryModel {
  private static ContentResolver contentResolver;
  private Uri uri;
  private long id;
  private Date startDate;
  private Date endDate;
  private String comment;

  private DateFormat _UI_DATE_FORMATTER = DateFormat.getDateTimeInstance(
      DateFormat.SHORT,
      DateFormat.SHORT
  );

  public static void setContentResolver( ContentResolver resolver ) {
    TimelogEntryModel.contentResolver = resolver;
  }

  public TimelogEntryModel( Date startDate, Date endDate, String comment ) {
    Log.d( "Model", "Value constructor called");
    this.id = -1;
    this.uri = null;
    this.startDate = startDate;
    this.endDate = endDate;
    this.comment = comment;
  }

  private TimelogEntryModel() {}

  // Assumes cursor is not null and points to the correct provider record
  public TimelogEntryModel( final Cursor cursor ) throws ParseException {
    Log.d( "Model", "Cursor constructor called");
    final int idColumnIndex = cursor.getColumnIndex( BaseColumns._ID );
    final int startDateColumnIndex = cursor.getColumnIndex( TimelogContract.Timelog.Columns.START );
    final int endDateColumnIndex = cursor.getColumnIndex( TimelogContract.Timelog.Columns.END );
    final int commentColumnIndex = cursor.getColumnIndex( TimelogContract.Timelog.Columns.COMMENT );

    this.id = cursor.getLong( idColumnIndex );
    this.uri = ContentUris.withAppendedId( TimelogContract.Timelog.CONTENT_URI, this.id );
    this.startDate = parseDbDate( cursor.getString( startDateColumnIndex ) );
    this.endDate = parseDbDate( cursor.getString( endDateColumnIndex ) );
    this.comment = cursor.getString( commentColumnIndex );
  }

  public boolean save() {
    return ( this.uri == null || this.id == -1 ) ? insert() : update();
  }

  protected boolean insert() {
    ContentValues values = new ContentValues();
    values.put( TimelogContract.Timelog.Columns.START, getDbStartDateString() );
    values.put( TimelogContract.Timelog.Columns.END, getDbEndDateString() );
    values.put( TimelogContract.Timelog.Columns.COMMENT, this.comment );

    this.uri = TimelogEntryModel.contentResolver.insert( TimelogContract.Timelog.CONTENT_URI, values );
    this.id = ContentUris.parseId( this.uri );

    // FIXME: Error handling; how do we detect an insert failed?
    // TODO: return false on error
    return true;
  }

  protected boolean update() {
    ContentValues values = new ContentValues();
    values.put( TimelogContract.Timelog.Columns.START, getDbStartDateString() );
    values.put( TimelogContract.Timelog.Columns.END, getDbEndDateString() );
    values.put( TimelogContract.Timelog.Columns.COMMENT, this.comment );

    return ( TimelogEntryModel.contentResolver.update( this.uri, values, null, null) == 1 );
  }

  public String getUiStartDateString() {
    return _UI_DATE_FORMATTER.format( this.startDate );
  }

  public String getUiEndDateString() {
    return (this.endDate == null) ? null :
                                    _UI_DATE_FORMATTER.format( this.endDate );
  }

  protected String getDbStartDateString() {
    return TimelogContract.Converter.DB_DATE_TIME_FORMATTER.format( this.startDate );
  }

  protected String getDbEndDateString() {
    return (this.endDate == null) ? null :
                                    TimelogContract.Converter.DB_DATE_TIME_FORMATTER.format( this.endDate );
  }

  public final Date getStartDate() {
    return this.startDate;
  }

  public final Date getEndDate() {
    return this.endDate;
  }

  public final String getComment() {
    return this.comment;
  }

  public final Uri getUri() {
    return this.uri;
  }

  public void setStartDate( Date newStartDate ) {
    if( newStartDate == null ) {
      throw new IllegalArgumentException( "StartDate cannot be set to null" );
    }
    this.startDate = newStartDate;
  }

  public void setEndDate( Date newEndDate ) {
    this.endDate = newEndDate;
  }

  public void setComment( String newComment ) {
    this.comment = newComment;
  }

  private Date parseDbDate( String dbDate ) throws ParseException {
    return ( dbDate == null ) ? null :
                                TimelogContract.Converter.DB_DATE_TIME_FORMATTER.parse( dbDate );
  }
}
