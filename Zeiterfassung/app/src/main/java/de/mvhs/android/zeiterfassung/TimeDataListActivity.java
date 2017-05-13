package de.mvhs.android.zeiterfassung;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import de.mvhs.android.zeiterfassung.db.TimeContract;

/**
 * Created by Kurs on 26.04.2017.
 */

public class TimeDataListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ListView _list;
    private SimpleCursorAdapter _adapter;

    private static final int _LOADER_ID = 100;

    private static final String _FILTER = "IFNULL(" + TimeContract.TimeData.Columns.END + ",'')<>''";
    private static final String _SORT_ORDER = TimeContract.TimeData.Columns.START + " DESC";
    private static final String[] _COLUMNS = new String[] {
        TimeContract.TimeData.Columns._ID,
            TimeContract.TimeData.Columns.START,
            TimeContract.TimeData.Columns.END
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_data_list);

        _list = (ListView) findViewById(R.id.TimeDataList);
        _adapter = new SimpleCursorAdapter(this, // Context
                R.layout.row_time_data_list, // layout für Zeile
                null, // Daten
                new String[]{
                        TimeContract.TimeData.Columns.START,
                        TimeContract.TimeData.Columns.END}, // Spalten aus Ergebnis
                new int[]{R.id.StartTimeValue, R.id.EndTimeValue}, // IDs der Views
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        _list.setAdapter(_adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getSupportLoaderManager()
                .restartLoader(_LOADER_ID, null, this);

        // Event für die Auswahl registrieren
        _list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent newIntent = new Intent(TimeDataListActivity.this, EditActivity.class);
                newIntent.putExtra(EditActivity.ID_KEY, id);
                startActivity(newIntent);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        getSupportLoaderManager().destroyLoader(_LOADER_ID);
        _list.setOnItemSelectedListener(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.MenuItemNew:
                Intent newIntent = new Intent(this, EditActivity.class);
                startActivity(newIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;

        switch (id){
            case _LOADER_ID:
                loader = new CursorLoader(
                        this, // Context
                        TimeContract.TimeData.CONTENT_URI, // Uri für Provider
                        _COLUMNS, // Spalten
                        _FILTER, // Filter
                        null, // Filter Parameter
                        _SORT_ORDER); // Sortierung
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()){
            case _LOADER_ID:
                _adapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()){
            case _LOADER_ID:
                _adapter.swapCursor(null);
                break;
        }
    }
}
