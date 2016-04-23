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
import db.TimelogEntryModel;

public class MainActivity extends AppCompatActivity {
  private Uri recordUriWithEndDateMissing = null;
  private TimelogEntryModel currentTimelogEntry = null;

  private DateFormat _UI_DATE_FORMATTER = DateFormat.getDateTimeInstance(
      DateFormat.SHORT,
      DateFormat.SHORT
  );

  @Override
  protected void onStart() {
    super.onStart();

    TimelogEntryModel.setContentResolver( getContentResolver() );

    final Button startButton = (Button) findViewById( R.id.StartCommand );
    final Button endButton = (Button) findViewById( R.id.EndCommand );
    final Cursor emptyEndDateRecord = findRecordWithEmptyEndDate();

    if( emptyEndDateRecord == null ) {
      startButton.setEnabled( true );

    } else {
      try {
        this.currentTimelogEntry = new TimelogEntryModel( emptyEndDateRecord );
        final EditText startTimeText = (EditText) findViewById( R.id.StartTime );
        startTimeText.setText( currentTimelogEntry.getUiStartDateString() );

      } catch( ParseException e ) {
        Log.w( "MainActivity", "Error while creating TimelogEntryModel from record with empty end date: " + String.valueOf( emptyEndDateRecord ));
      }

      endButton.setEnabled( true );
    }
  }

  private Cursor findRecordWithEmptyEndDate() {
    final String whereEndDateIsNull = TimelogContract.Timelog.Columns.END + " IS NULL";
    final String orderByIdDesc = BaseColumns._ID + " DESC";

    final Cursor records = getContentResolver().query( TimelogContract.Timelog.CONTENT_URI, null, whereEndDateIsNull, null, orderByIdDesc );
    Cursor returnRecord = null;

    if( records == null || records.getCount() == 0 ) {
      Log.i( "MainActivity", "Found no records without an end date");

    } else {
      Log.i( "MainActivity", "Got " + records.getCount() + " entries without an end date");
      records.moveToFirst();
      returnRecord = records;
    }

    return returnRecord;
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
        MainActivity.this.currentTimelogEntry = new TimelogEntryModel( new Date(), null, null );

        startTime.setText( MainActivity.this.currentTimelogEntry.getUiStartDateString() );
        MainActivity.this.currentTimelogEntry.save();

        Log.i( "MainActivity", "Inserted start time record " +
                                String.valueOf( MainActivity.this.currentTimelogEntry.getUri() ) +
                                " with start time " +
                                String.valueOf( MainActivity.this.currentTimelogEntry.getStartDate() ));

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

        MainActivity.this.currentTimelogEntry.setEndDate( new Date() );
        endTime.setText( MainActivity.this.currentTimelogEntry.getUiEndDateString() );

        if( !MainActivity.this.currentTimelogEntry.save() ) {
          throw new RuntimeException( "Something went wrong with updating " +
                                       String.valueOf( MainActivity.this.currentTimelogEntry.getUri()  ) +
                                       "...");
        }
        Log.i( "MainActivity", "Updated end time record " +
                                String.valueOf( MainActivity.this.currentTimelogEntry.getUri() ) +
                                " with end time " +
                                MainActivity.this.currentTimelogEntry.getUiEndDateString());

        startButton.setEnabled( true );
      }
    } );
  }
}
