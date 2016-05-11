package de.mvhs.android.zeiterfassung;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private final static String _ONLY_FINISHED = "IFNULL("
            + TimelogContract.Timelog.Columns.END
            + ",'')<>''";
    private final static String[] _COLUMNS = {
            BaseColumns._ID,
            TimelogContract.Timelog.Columns.START,
            TimelogContract.Timelog.Columns.END};
    private final static String[] _UI_COLUMNS = {
            TimelogContract.Timelog.Columns.START,
            TimelogContract.Timelog.Columns.END
    };
    private final static int[] _ROW_VIEWS = {
            R.id.StartValue,
            R.id.EndValue
    };
    private static final int _PERMISSION_REQUEST_ID = 200;
    private ListView _list;
    private SimpleCursorAdapter _adapter;
    private final static int _LOADER_ID = 100;
    private DateFormat _UI_DATE_CONVERTER = DateFormat
            .getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_list);

        // Initialisieren der UI Elemente
        _list = (ListView) findViewById(R.id.TimelogList);

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
        _adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if ((view instanceof TextView) == false) {
                    return false;
                }

                // NULL Prüfung aus der Datenbank
                if (cursor.isNull(columnIndex)) {
                    ((TextView) view).setText("---");
                } else {
                    try {
                        Date date = TimelogContract.Converter
                                .DB_DATE_TIME_FORMATTER.parse(
                                        cursor.getString(columnIndex));

                        ((TextView) view).setText(_UI_DATE_CONVERTER.format(date));
                    } catch (ParseException e) {
                        ((TextView) view).setText("PARSE ERROR");
                    }
                }

                return true;
            }
        });

        // Adapter mit Liste verbinden
        _list.setAdapter(_adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.ExportAction){
            // Abfragen, ob Berechtigung vorhanden ist
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                // Berechtigung erfragen
                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        _PERMISSION_REQUEST_ID);
            } else {
                // Exportieren erlaubt
                CsvExporter exporter = new CsvExporter(this);
                exporter.execute();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case _PERMISSION_REQUEST_ID:
                // Antwort des Benutzers verarbeiten
                if (grantResults.length == 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    CsvExporter exporter = new CsvExporter(this);
                    exporter.execute();
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        getSupportLoaderManager().restartLoader(
                _LOADER_ID, // ID des Loaders
                null, // Zusatzdaten
                this); // Callback-Implementierung (diese Klasse)

        // Registrieren der Listener
        _list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editIntent = new Intent(TimeListActivity.this, EditActivity.class);

                editIntent.putExtra("ID", id);

                startActivity(editIntent);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Deregistrieren der Listener
        _list.setOnItemClickListener(null);
        getSupportLoaderManager().destroyLoader(_LOADER_ID);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;

        switch (id) {
            case _LOADER_ID:
                loader = new CursorLoader(
                        getBaseContext(), // Context
                        TimelogContract.Timelog.CONTENT_URI, // URI für ContentProvider
                        _COLUMNS, // Spalten
                        _ONLY_FINISHED, // Filter (WHERE)
                        null, // Parameter für Filter
                        TimelogContract.Timelog.Columns.START + " DESC"); // Soriterung
                break;
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case _LOADER_ID:
                _adapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case _LOADER_ID:
                _adapter.swapCursor(null);
                break;
        }
    }
}
