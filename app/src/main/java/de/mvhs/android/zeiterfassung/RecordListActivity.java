package de.mvhs.android.zeiterfassung;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import de.mvhs.android.zeiterfassung.db.ZeitContract;
import de.mvhs.android.zeiterfassung.utils.CsvExporter;
import de.mvhs.android.zeiterfassung.utils.ListViewBinder;


public class RecordListActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    // Klassenvaribalen
    private ListView _recordList = null;
    private SimpleCursorAdapter _adapter = null;
    private final static String[] _projection = {ZeitContract.ZeitDaten.Columns._ID, ZeitContract.ZeitDaten.Columns.START_TIME, ZeitContract.ZeitDaten.Columns.END_TIME};
    private final static String[] _columns = {ZeitContract.ZeitDaten.Columns.START_TIME, ZeitContract.ZeitDaten.Columns.END_TIME};
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
                R.layout.row_two_columns, // Layout
                null, // Cursor Daten
                _columns, // Spalten
                new int[]{android.R.id.text1, android.R.id.text2}, // Layout Views
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER // Flags
        );
        _adapter.setViewBinder(new ListViewBinder());

        // Liste mit Adapter verbinden
        _recordList.setAdapter(_adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        registerForContextMenu(_recordList);
        _recordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editIntent = new Intent(RecordListActivity.this, EditRecordActivity.class);
                editIntent.putExtra("ID", id);

                startActivity(editIntent);
            }
        });

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
    protected void onStop() {
        super.onStop();

        unregisterForContextMenu(_recordList);
        _recordList.setOnItemClickListener(null);
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

        if (id == R.id.action_export){
            CsvExporter exporter = new CsvExporter(this);
            exporter.execute();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.RecordList) {
            getMenuInflater().inflate(R.menu.list_context_menu, menu);
        }

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                // Löschen des aktuellen Datensatzes
                final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                final long id = info.id;

                // Abfragedialog erstellen
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Löschen")
                        .setMessage("Wollen Sie den Datensatz wirklich löschen?")
                        .setPositiveButton("Löschen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Löschlogik
                                Uri uri = ContentUris.withAppendedId(ZeitContract.ZeitDaten.CONTENT_URI, id);
                                getContentResolver().delete(uri, null, null);
                            }
                        })
                        .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Abbrechen
                                dialog.dismiss();
                            }
                        });

                // Dialog anzeigen
                builder.create().show();

                return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;

        switch (id) {
            case _LOADER_ID:
                loader = new CursorLoader(this, // Context
                        ZeitContract.ZeitDaten.CONTENT_URI, // URI für ContentProvider
                        _projection, // Spalten, die geladen werden sollen
                        null, // Filter
                        null, // Filter Parameter
                        _sortOrder);
                break;
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        final int loaderId = loader.getId();

        switch (loaderId) {
            case _LOADER_ID:
                _adapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        final int loaderId = loader.getId();

        switch (loaderId) {
            case _LOADER_ID:
                _adapter.swapCursor(null);
                break;
        }
    }

}
