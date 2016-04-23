package de.mvhs.android.zeiterfassung;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import db.DbHelper;
import db.TimelogContract;

public class MainActivity extends AppCompatActivity {
  private Uri recordUriWithEndDateMissing = null;

  private DateFormat _UI_DATE_FORMATTER = DateFormat.getDateTimeInstance(
      DateFormat.SHORT,
      DateFormat.SHORT
  );

  @Override
  protected void onStart() {
    super.onStart();
    final String whereEndDateIsNull = TimelogContract.Timelog.Columns.END + " IS NULL";
    final String orderByIdDesc = BaseColumns._ID + " DESC";

    final Cursor emptyEndDateRecords = getContentResolver().query( TimelogContract.Timelog.CONTENT_URI, null, whereEndDateIsNull, null, orderByIdDesc );

    final Button startButton = (Button) findViewById( R.id.StartCommand );
    final Button endButton = (Button) findViewById( R.id.EndCommand );

    if( emptyEndDateRecords != null && emptyEndDateRecords.getCount() > 0 ) {
      int startDateColumnIndex = emptyEndDateRecords.getColumnIndex( TimelogContract.Timelog.Columns.START );
      emptyEndDateRecords.moveToFirst();
      String startDateValue = emptyEndDateRecords.getString( startDateColumnIndex);

      int idColumnIndex = emptyEndDateRecords.getColumnIndex( BaseColumns._ID );
      long recordId = emptyEndDateRecords.getLong( idColumnIndex );
      this.recordUriWithEndDateMissing = ContentUris.withAppendedId( TimelogContract.Timelog.CONTENT_URI, recordId );

      try {
        Date startTime = TimelogContract.Converter.DB_DATE_TIME_FORMATTER
            .parse( startDateValue );

        String start = _UI_DATE_FORMATTER.format( startTime );

        final EditText startTimeText = (EditText) findViewById( R.id.StartTime );
        startTimeText.setText( startDateValue );

      } catch( ParseException e ) {
        e.printStackTrace();
      }


      endButton.setEnabled( true );
    } else {
      startButton.setEnabled( true );
    }
  }


  @Override
  protected void onCreate( Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_main_relative );

    final Button startButton = (Button) findViewById( R.id.StartCommand );
    final Button endButton = (Button) findViewById( R.id.EndCommand );

    // disable start and end buttons until onStart() has had a chance to run
    startButton.setEnabled( false );
    endButton.setEnabled( false );

    setupStartButton( startButton );
    setupEndButton( endButton );
  }

  protected void setupStartButton( final Button button ) {
    // Suchen der Elemente vom Layout
    final EditText startTime = (EditText) findViewById( R.id.StartTime );
    final Button endButton = (Button) findViewById( R.id.EndCommand );

    button.setOnClickListener( new View.OnClickListener() {
      @Override
      public void onClick( View v ) {
        button.setEnabled( false );
        Date now = new Date();
        startTime.setText( _UI_DATE_FORMATTER.format( now ) );

        ContentValues value = new ContentValues();
        value.put( TimelogContract.Timelog.Columns.START,
            TimelogContract.Converter.DB_DATE_TIME_FORMATTER
                .format( now ) );

        MainActivity.this.recordUriWithEndDateMissing = getContentResolver().insert( TimelogContract.Timelog.CONTENT_URI,
            value );

        Log.i( "MainActivity", "Inserted start time record " + String.valueOf( MainActivity.this.recordUriWithEndDateMissing + " with start time " + String.valueOf( now )));

        endButton.setEnabled( true );
      }
    } );
  }

  protected void setupEndButton( final Button button ) {
    final Button startButton = (Button) findViewById( R.id.StartCommand );
    startButton.setEnabled( false );
    final EditText endTime = (EditText) findViewById( R.id.EndTime );

    button.setOnClickListener( new View.OnClickListener() {
      @Override
      public void onClick( View v ) {
        button.setEnabled( false );
        Date now = new Date();
        endTime.setText( _UI_DATE_FORMATTER.format( now ));

        if( MainActivity.this.recordUriWithEndDateMissing == null ) {
          // uhoh, this should never happen.
          throw new RuntimeException( "Missing end time record uri is null..." );
        }

        ContentValues value = new ContentValues();
        value.put( TimelogContract.Timelog.Columns.END, TimelogContract.Converter.DB_DATE_TIME_FORMATTER.format( now ) );
        if( getContentResolver().update( MainActivity.this.recordUriWithEndDateMissing, value, null, null ) != 1 ) {
          throw new RuntimeException( "Something went wrong with updating " + String.valueOf( MainActivity.this.recordUriWithEndDateMissing  ) + "...");
        }
        Log.i( "MainActivity", "Updated end time record " + String.valueOf( MainActivity.this.recordUriWithEndDateMissing + " with end time " + String.valueOf( now )));

        startButton.setEnabled( true );
      }
    } );
  }
}
