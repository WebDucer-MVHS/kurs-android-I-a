package de.mvhs.android.zeiterfassung;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import de.mvhs.android.zeiterfassung.db.ZeitProvider;

public class EntryListActivity extends ListActivity {
  private SimpleCursorAdapter _Adapter    = null;
  private final static String _SORT_ORDER = ZeitProvider.Columns.START + " DESC";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_entry_list);

    _Adapter = new SimpleCursorAdapter(this, // Context
            R.layout.row_with_2_values, // Layout für Zeile
            null, // Daten als Cursor
            new String[] { ZeitProvider.Columns.START, ZeitProvider.Columns.END }, // Darzustellende Spalten
            new int[] { R.id.StartDateText, R.id.EndDateText }, // In
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
            _SORT_ORDER); // Sortierung

    _Adapter.swapCursor(data);

    registerForContextMenu(getListView());
  }

  @Override
  protected void onStop() {
    super.onStop();

    _Adapter.swapCursor(null);
    unregisterForContextMenu(getListView());
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.list_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        Intent homeIntent = new Intent(this, MainActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(homeIntent);
        break;

      case R.id.mnu_export:
        // Fortschrittsanzeige
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Export ...");
        dialog.setMessage("Daten werden exportiert ...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(true);

        final CsvExportTask task = new CsvExportTask(dialog);
        Cursor data = getContentResolver().query(ZeitProvider.CONTENT_URI, null, null, null, _SORT_ORDER);

        // Max setzen
        dialog.setMax(data.getCount());

        dialog.setButton(ProgressDialog.BUTTON_NEGATIVE, getString(R.string.cancel_button), new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int which) {
            task.cancel(false);
          }
        });

        // Export ausführen
        task.execute(data);
        break;

      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    if (v.getId() == android.R.id.list) {
      this.getMenuInflater().inflate(R.menu.list_context_menu, menu);
    }
    super.onCreateContextMenu(menu, v, menuInfo);
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    long id = -1; // ID des ausgewählten Eintrages
    switch (item.getItemId()) {
      case R.id.mnu_edit:
        Intent editIntent = new Intent(this, EditActivity.class);
        editIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // ID an die Nachricht anhängen

        // Nachricht absenden
        startActivity(editIntent);
        break;

      case R.id.mnu_delete:
        // Uri für das zu löschende Element generieren
        Uri entryToDelete = ContentUris.withAppendedId(ZeitProvider.CONTENT_URI, id);
        // Element über Content-Provider löschen
        getContentResolver().delete(entryToDelete, null, null);
        break;

      default:
        break;
    }
    return super.onContextItemSelected(item);
  }

}
