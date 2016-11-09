package de.mvhs.android.zeiterfassung;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;

import de.mvhs.android.zeiterfassung.db.TimeDataContract;

/**
 * Created by Kurs on 26.10.2016.
 */

public class ListDataActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {
  private ListView _dataList;
  private SimpleCursorAdapter _adapter;

  private final static int _LOADER = 100;
  private final static String[] _VIEW_COLUMNS = new String[]{
      TimeDataContract.TimeData.Columns.START,
      TimeDataContract.TimeData.Columns.END
  };
  private final static int[] _LAYOUT_VIEW_IDS = new int[]{
      R.id.StartTimeValue,
      R.id.EndTimeValue
  };

  private DateFormat _UI_DATE_TIME_FORMATTER = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_data);

    _dataList = (ListView) findViewById(R.id.DataList);

    // Adapter initialisieren
    _adapter = new SimpleCursorAdapter(
        this, // Context
        R.layout.row_data_item, // Layout für die Zeile
        null, // Daten
        _VIEW_COLUMNS, // Spalten aus den Daten
        _LAYOUT_VIEW_IDS, // IDs der Views im Layout
        SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER // Benachrichtigung
    );

    // Verarbeitung der Datum und Uhrzeit Werte
    _adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
      @Override
      public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        if ((view instanceof TextView) == false) {
          return false;
        }

        TextView textView = (TextView) view;

        // NULL Prüfung aus der Datenbank
        if (cursor.isNull(columnIndex)) {
          textView.setText("---");
        } else {
          try {
            // Konvertierung aus der Datenbank
            String dateString = cursor.getString(columnIndex);
            Calendar date = TimeDataContract.Converter.parseFromDb(dateString);

            textView.setText(_UI_DATE_TIME_FORMATTER.format(date.getTime()));
          } catch (ParseException e) {
            textView.setText("PARSE ERROR");
          }
        }

        return true;
      }
    });

    // Adpter zuordnen
    _dataList.setAdapter(_adapter);
  }

  @Override
  protected void onStart() {
    super.onStart();
    getSupportLoaderManager()
        .restartLoader(
            _LOADER, // ID des Loaders
            null, // Zusatzdaten, falls notwendig
            this); // Context

    _dataList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent editIntent = new Intent(ListDataActivity.this, EditActivity.class);
        editIntent.putExtra(EditActivity.ID_KEY, _adapter.getItemId(position));
        startActivity(editIntent);
      }
    });
  }

  @Override
  protected void onStop() {
    super.onStop();
    getSupportLoaderManager()
        .destroyLoader(_LOADER);

    _dataList.setOnItemClickListener(null);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    CursorLoader loader = null;

    switch (id) {
      case _LOADER:
        loader = new CursorLoader(
            this, // Context
            TimeDataContract.TimeData.CONTENT_URI, // URI zu Daten
            null, // Spalten (SELECT *)
            null, // Filer (WHERE)
            null, // Filter Parameter
            null // Sortierung (ORDER BY)
        );
        break;
    }

    return loader;
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    switch (loader.getId()) {
      case _LOADER:
        _adapter.swapCursor(data);
        break;
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    switch (loader.getId()) {
      case _LOADER:
        _adapter.swapCursor(null);
        break;
    }
  }
}
