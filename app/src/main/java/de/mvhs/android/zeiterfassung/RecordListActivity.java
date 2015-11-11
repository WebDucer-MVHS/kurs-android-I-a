package de.mvhs.android.zeiterfassung;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import de.mvhs.android.zeiterfassung.db.ZeitContract;


/**
 * Created by kurs on 28.10.15.
 */
public class RecordListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    /* Klassenvariablen */
    private ListView _list;
    private SimpleCursorAdapter _adapter;
    private final DateFormat _DATE_CONVERTER = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    private final static int _LOADER_ID = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Einbinden des Layouts
        setContentView(R.layout.activity_list);

        _list = (ListView) findViewById(R.id.RecordList);

        // Initialisieren des Adapters
        _adapter = new SimpleCursorAdapter(
                getBaseContext(), // Context
                //android.R.layout.simple_list_item_2, // Layout für die Zeile
                R.layout.list_row, // Eigenes Laxout für die zweite Zeile
                null, // Daten für die Darstellung
                new String[]{ZeitContract.ZeitDaten.Columns.START, ZeitContract.ZeitDaten.Columns.END}, // Spalten
                new int[]{android.R.id.text1, android.R.id.text2}, // View IDs, in denen die Daten angezeigt werden
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );

        _adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (!(view instanceof TextView)) {
                    return false;
                }

                if (cursor.isNull(columnIndex)) {
                    ((TextView) view).setText("---");
                } else {
                    try {
                        Date date = ZeitContract.Converters.DB_DATE_TIME_FORMATTER.parse(cursor.getString(columnIndex));
                        ((TextView) view).setText(_DATE_CONVERTER.format(date));
                    } catch (ParseException e) {
                        ((TextView) view).setText("ERROR");
                    }
                }

                return true;
            }
        });

        _list.setAdapter(_adapter);

        getSupportLoaderManager().restartLoader(_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;

        switch (id) {
            case _LOADER_ID:
                loader = new CursorLoader(
                        getBaseContext(), // Context
                        ZeitContract.ZeitDaten.CONTENT_URI, // URI für Content Provider
                        null, // Spalten (NULL = alle)
                        null, // Filter (WHERE Bedingung)
                        null, // Filter Argumente
                        null); // Sortierung
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

    // Einbinden des Menüs

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Klick auf Menüeintrag verarbeiten
        switch (item.getItemId()) {
            case R.id.ExportAction:
                CsvExporter exporter = new CsvExporter(getBaseContext());
                exporter.execute();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
