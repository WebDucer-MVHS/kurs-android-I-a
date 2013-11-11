package de.mvhs.android.zeiterfassung;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import de.mvhs.android.zeiterfassung.db.ZeitProvider;

public class EntryListActivity extends ListActivity {
  private SimpleCursorAdapter _Adapter = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_entry_list);

    _Adapter = new SimpleCursorAdapter(this, // Context
            android.R.layout.simple_list_item_2, // Layout für Zeile
            null, // Daten als Cursor
            new String[] { ZeitProvider.Columns.START, ZeitProvider.Columns.END }, // Darzustellende Spalten
            new int[] { android.R.id.text1, android.R.id.text2 }, // In
            // welchen
            // Zeilen-Elementen
            // diese
            // Daten
            // dargestellt
            // werden
            // sollen
            0);

    this.setListAdapter(_Adapter);

    // Home Button aktivieren
    getActionBar().setDisplayShowHomeEnabled(true);
    getActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @Override
  protected void onStart() {
    super.onStart();

    Cursor data = getContentResolver().query(ZeitProvider.CONTENT_URI, // URI
            null, // Auszuwählende Spalten
            null, // Bedingung
            null, // Parameter der Bedingung
            null); // Sortierung

    _Adapter.swapCursor(data);
  }

  @Override
  protected void onStop() {
    super.onStop();

    _Adapter.swapCursor(null);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        Intent homeIntent = new Intent(this, MainActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(homeIntent);
        break;

      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}
