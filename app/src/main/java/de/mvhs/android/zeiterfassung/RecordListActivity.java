package de.mvhs.android.zeiterfassung;

import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import de.mvhs.android.zeiterfassung.db.ZeitContract;


public class RecordListActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    // Klassenvaribalen
    private ListView _recordList = null;
    private SimpleCursorAdapter _adapter = null;
    private final static String[] _projection = {
            ZeitContract.ZeitDaten.Columns._ID,
            ZeitContract.ZeitDaten.Columns.START_TIME,
            ZeitContract.ZeitDaten.Columns.END_TIME
    };
    private final static String[] _columns = {
            ZeitContract.ZeitDaten.Columns.START_TIME,
            ZeitContract.ZeitDaten.Columns.END_TIME
    };
    private final static String _sortOrder = ZeitContract.ZeitDaten.Columns.START_TIME + " DESC";

    // Loader ID
    private final static int _LOADER_ID = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        // Initalisierung der UI Elemente
        _recordList = (ListView) findViewById(R.id.RecordList);

        // Initialisierung des Adapters
        _adapter = new SimpleCursorAdapter(this, // Context
                android.R.layout.simple_list_item_2, // Layout
                null, // Cursor Daten
                _columns, // Spalten
                new int[]{android.R.id.text1, android.R.id.text2}, // Layout Views
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER // Flags
        );

        // Liste mit Adapter verbinden
        _recordList.setAdapter(_adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Laden der Daten aus der Datenbank
        /*Cursor data = getContentResolver().query(
                ZeitContract.ZeitDaten.CONTENT_URI, // URI zur Tabelle
                _projection, // Spalten, die abgefragt werden
                null, // Filter
                null, // Filter Argumente
                _sortOrder); // Sortierung

        _adapter.swapCursor(data);*/

        // Mit loader die Daten im Hintergrund laden
       getSupportLoaderManager().restartLoader(_LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_record_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;

        switch (id){
            case _LOADER_ID:
                loader = new CursorLoader(this, // Context
                        ZeitContract.ZeitDaten.CONTENT_URI, // URI für ContentProvider
                        _projection, // Spalten, die geladen werden sollen
                        null, // Filter
                        null, // Filter Parameter
                        _sortOrder
                        );
                break;
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        final int loaderId = loader.getId();

        switch (loaderId){
            case _LOADER_ID:
                _adapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        final int loaderId = loader.getId();

        switch (loaderId){
            case _LOADER_ID:
                _adapter.swapCursor(null);
                break;
        }
    }
}
