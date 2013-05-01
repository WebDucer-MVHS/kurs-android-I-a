package de.mvhs.android.arbeitszeiterfassung;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import de.mvhs.android.arbeitszeiterfassung.db.ZeitabschnittContract;

public class AuflistungActivity extends ListActivity {

  // Variablen
  private SimpleCursorAdapter _Adapter = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.list_activity);

    _Adapter = new SimpleCursorAdapter(this, // Context
            R.layout.list_row_2, // Layout für eine Zeile
            null, // Cursor
            new String[] { // Spalten, die in der Liste angezeigt werden sollen
            ZeitabschnittContract.Zeitabschnitte.Columns.START, ZeitabschnittContract.Zeitabschnitte.Columns.STOP }, new int[] { // Layout IDs, in denen die
                                                                                                                                 // Werte der Spalten angezeigt
                                                                                                                                 // werden sollen
            R.id.txt_start_time, R.id.txt_end_time }, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER); // FLAG

    getListView().setAdapter(_Adapter);

    // Home Button aktivieren
    getActionBar().setDisplayHomeAsUpEnabled(true);
    getActionBar().setDisplayShowHomeEnabled(true);
  }

  @Override
  protected void onStart() {
    super.onStart();

    Cursor data = getContentResolver().query(
            ZeitabschnittContract.Zeitabschnitte.CONTENT_URI, // URI zur Tabelle
            new String[] { BaseColumns._ID, ZeitabschnittContract.Zeitabschnitte.Columns.START, ZeitabschnittContract.Zeitabschnitte.Columns.STOP }, null,
            null, null);

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
        startActivity(homeIntent);
        break;

      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}
