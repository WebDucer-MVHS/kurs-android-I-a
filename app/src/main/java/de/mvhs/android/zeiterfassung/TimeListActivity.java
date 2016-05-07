package de.mvhs.android.zeiterfassung;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import db.TimelogContract;

/**
 * Created by kurs on 27.04.16.
 */
public class TimeListActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor>,
    ExportProgressDialog.ExportProgressDialogListener {
  private final static String _ONLY_FINISHED = "IFNULL("
      + TimelogContract.Timelog.Columns.END
      + ",'')<>''";
  private final static String[] _COLUMNS = {
      BaseColumns._ID,
      TimelogContract.Timelog.Columns.START,
      TimelogContract.Timelog.Columns.END };
  private final static String[] _UI_COLUMNS = {
      TimelogContract.Timelog.Columns.START,
      TimelogContract.Timelog.Columns.END
  };
  private final static int[] _ROW_VIEWS = {
      R.id.StartValue,
      R.id.EndValue
  };
  private ListView _list;
  private SimpleCursorAdapter _adapter;
  private final static int _LOADER_ID = 100;
  private DateFormat _UI_DATE_CONVERTER = DateFormat
      .getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT );
  private CsvExporter _exporter;

  @Override
  protected void onCreate( Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_time_list );

    // Initialisieren der UI Elemente
    _list = (ListView) findViewById( R.id.TimelogList );

    // Initialisierung des Adapters
    _adapter = new SimpleCursorAdapter(
        getBaseContext(), // Context
        //android.R.layout.simple_list_item_2, // View für die Darstellung der Daten
        R.layout.list_row_layout, // Eigenes Layout für die Zeile
        null, // Daten
        _UI_COLUMNS, // Spalten für die Darstellung
        //new int[]{android.R.id.text1,
        //    android.R.id.text2}, // Views für die Darstellung
        _ROW_VIEWS, // IDs aus eigenem Layout
        SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER // Auf Änderungen registrieren
    );

    // Formatieren der Daten aus der Datenbank
    _adapter.setViewBinder( new SimpleCursorAdapter.ViewBinder() {
      @Override
      public boolean setViewValue( View view, Cursor cursor, int columnIndex ) {
        if( ( view instanceof TextView ) == false ) {
          return false;
        }

        // NULL Prüfung aus der Datenbank
        if( cursor.isNull( columnIndex ) ) {
          ( (TextView) view ).setText( "---" );
        } else {
          try {
            Date date = TimelogContract.Converter
                .DB_DATE_TIME_FORMATTER.parse(
                    cursor.getString( columnIndex ) );

            ( (TextView) view ).setText( _UI_DATE_CONVERTER.format( date ) );
          } catch( ParseException e ) {
            ( (TextView) view ).setText( "PARSE ERROR" );
          }
        }

        return true;
      }
    } );

    // Adapter mit Liste verbinden
    _list.setAdapter( _adapter );
  }

  @Override
  public boolean onCreateOptionsMenu( Menu menu ) {
    getMenuInflater().inflate( R.menu.menu_list, menu );
    return super.onCreateOptionsMenu( menu );
  }


  @Override
  public boolean onOptionsItemSelected( MenuItem item ) {
    if( item.getItemId() == R.id.ExportAction ) {
      _exporter = new CsvExporter( TimeListActivity.this );
      _exporter.execute();

      return true;
    }

    return super.onOptionsItemSelected( item );
  }

  public void exportDone() {
    _exporter = null;
  }

  @Override
  protected void onStart() {
    super.onStart();

    getSupportLoaderManager().restartLoader(
        _LOADER_ID, // ID des Loaders
        null, // Zusatzdaten
        this ); // Callback-Implementierung (diese Klasse)
  }

  @Override
  public Loader<Cursor> onCreateLoader( int id, Bundle args ) {
    CursorLoader loader = null;

    switch( id ) {
      case _LOADER_ID:
        loader = new CursorLoader(
            getBaseContext(), // Context
            TimelogContract.Timelog.CONTENT_URI, // URI für ContentProvider
            _COLUMNS, // Spalten
            _ONLY_FINISHED, // Filter (WHERE)
            null, // Parameter für Filter
            TimelogContract.Timelog.Columns.START + " DESC" ); // Soriterung
        break;
    }

    return loader;
  }

  @Override
  public void onLoadFinished( Loader<Cursor> loader, Cursor data ) {
    switch( loader.getId() ) {
      case _LOADER_ID:
        _adapter.swapCursor( data );
        break;
    }
  }

  @Override
  public void onLoaderReset( Loader<Cursor> loader ) {
    switch( loader.getId() ) {
      case _LOADER_ID:
        _adapter.swapCursor( null );
        break;
    }
  }

  @Override
  public void onDialogNegativeClick( DialogFragment dialog ) {
    if( _exporter != null ) {
      _exporter.cancel( false );
    }
    Log.i( "dialog", "Cancel clicked" );
  }

}
