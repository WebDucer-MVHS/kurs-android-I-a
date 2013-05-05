package de.mvhs.android.arbeitszeiterfassung;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
            new String[] { // Spalten, die in der Liste angezeigt werden
            // sollen
                ZeitabschnittContract.Zeitabschnitte.Columns.START, ZeitabschnittContract.Zeitabschnitte.Columns.STOP }, new int[] { // Layout IDs, in denen die
            // Werte der Spalten angezeigt
            // werden sollen
                R.id.txt_start_time, R.id.txt_end_time }, SimpleCursorAdapter.FLAG_AUTO_REQUERY); // FLAG

    // Aktuallisierung der Liste erfolgt automatsch bei FLAG_REGISTER_CONTENT_OBSERVER nur wenn die Daten über Loader geladen werden.
    // In anderen Fällen soll mit FLAG_AUTO_REQUERY (verlatet) weiter gearbeitet werden.

    setListAdapter(_Adapter);

    // Home Button aktivieren
    getActionBar().setDisplayHomeAsUpEnabled(true);
    getActionBar().setDisplayShowHomeEnabled(true);
  }

  @Override
  protected void onStart() {
    super.onStart();

    Cursor data = getContentResolver().query(
            ZeitabschnittContract.Zeitabschnitte.CONTENT_URI, // URI zur
            // Tabelle
            new String[] { BaseColumns._ID, ZeitabschnittContract.Zeitabschnitte.Columns.START, ZeitabschnittContract.Zeitabschnitte.Columns.STOP }, null,
            null, null);

    _Adapter.swapCursor(data);

    // Liste für Kontext-Menü registrieren
    registerForContextMenu(getListView());
  }

  @Override
  protected void onStop() {
    super.onStop();

    _Adapter.swapCursor(null);

    // Liste für Kontextmenü deregistrieren
    unregisterForContextMenu(getListView());
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

    if (v == getListView()) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.menu_list, menu);
    }

    super.onCreateContextMenu(menu, v, menuInfo);
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

  @Override
  public boolean onContextItemSelected(final MenuItem item) {
    final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

    switch (item.getItemId()) {
      case R.id.mnu_delete:
        // Nachfrage-Dialog aufbauen
        AlertDialog.Builder delDialog = new AlertDialog.Builder(this);
        delDialog.setTitle(R.string.dlg_delete_title).setMessage(R.string.dlg_delete_message)
                .setPositiveButton(R.string.dlg_delte_delete, new DialogInterface.OnClickListener() {

                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    // Löschen des Datensatzes
                    Uri uri = ContentUris.withAppendedId(ZeitabschnittContract.Zeitabschnitte.CONTENT_URI, info.id);

                    getContentResolver().delete(uri, null, null);
                  }
                }).setNegativeButton(R.string.dlg_delete_cancel, new DialogInterface.OnClickListener() {

                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                  }
                }).create().show();

        break;

      case R.id.mnu_show:

        break;

      case R.id.mnu_edit:
        Intent editIntent = new Intent(this, EditActivity.class);
        editIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // ID an die Nachricht anhängen
        editIntent.putExtra("ID", info.id);

        startActivity(editIntent);

        break;

      default:
        break;
    }
    return super.onContextItemSelected(item);
  }
}
