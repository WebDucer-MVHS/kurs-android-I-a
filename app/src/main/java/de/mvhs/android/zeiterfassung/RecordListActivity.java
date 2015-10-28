package de.mvhs.android.zeiterfassung;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import de.mvhs.android.zeiterfassung.db.ZeitContract;


/**
 * Created by kurs on 28.10.15.
 */
public class RecordListActivity extends AppCompatActivity {
    /* Klassenvariablen */
    private ListView _list;
    private SimpleCursorAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Einbinden des Layouts
        setContentView(R.layout.activity_list);

        _list = (ListView) findViewById(R.id.RecordList);

        // Initialisieren des Adapters
        _adapter = new SimpleCursorAdapter(
                getBaseContext(), // Context
                android.R.layout.simple_list_item_2, // Layout für die Zeile
                null, // Daten für die Darstellung
                new String[]{ZeitContract.ZeitDaten.Columns.START, ZeitContract.ZeitDaten.Columns.END}, // Spalten
                new int[]{android.R.id.text1, android.R.id.text2}, // View IDs, in denen die Daten angezeigt werden
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );

        _list.setAdapter(_adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Cursor data = getContentResolver().query(ZeitContract.ZeitDaten.CONTENT_URI,
                null, null, null, null);

        _adapter.swapCursor(data);
    }
}
