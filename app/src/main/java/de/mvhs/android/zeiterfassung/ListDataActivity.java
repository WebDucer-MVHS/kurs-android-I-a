package de.mvhs.android.zeiterfassung;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import de.mvhs.android.zeiterfassung.db.TimeDataContract;

/**
 * Created by Kurs on 26.10.2016.
 */

public class ListDataActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private ListView _dataList;
    private SimpleCursorAdapter _adapter;

    private final static int _LOADER = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data);

        _dataList = (ListView) findViewById(R.id.DataList);

        // Adapter initialisieren
        _adapter = new SimpleCursorAdapter(
                this, // Context
                android.R.layout.simple_list_item_2, // Layout für die Zeile
                null, // Daten
                new String[]{
                        TimeDataContract.TimeData.Columns.START,
                        TimeDataContract.TimeData.Columns.END
                }, // Spalten aus den Daten
                new int[]{
                        android.R.id.text1, // View für Start
                        android.R.id.text2 // View für Ende
                },
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER // Benachrichtigung
        );

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
    }

    @Override
    protected void onStop() {
        super.onStop();
        getSupportLoaderManager()
                .destroyLoader(_LOADER);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;

        switch (id){
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
        switch (loader.getId()){
            case _LOADER:
                _adapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()){
            case _LOADER:
                _adapter.swapCursor(null);
                break;
        }
    }
}
