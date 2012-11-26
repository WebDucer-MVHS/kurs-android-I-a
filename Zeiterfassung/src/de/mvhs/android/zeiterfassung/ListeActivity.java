package de.mvhs.android.zeiterfassung;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;

public class ListeActivity extends ListActivity {
  /* Klassen-Variablen */
  private DBHelper       _DBHelper;
  private SQLiteDatabase _Db;
  private Cursor         _Cursor;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_liste);
  }

  @Override
  protected void onStart() {
    super.onStart();
    // Initialisierung der Datenbank und der Daten
    _DBHelper = new DBHelper(this);
    _Db = _DBHelper.getReadableDatabase();

    LoadData();

    registerForContextMenu(getListView());
  }

  @Override
  protected void onStop() {
    super.onStop();
    // Schließen aller Ressourcen
    setListAdapter(null);

    if (_Cursor != null) {
      _Cursor.close();
    }
    if (_Db != null) {
      _Db.close();
    }
    if (_DBHelper != null) {
      _DBHelper.close();
    }

    unregisterForContextMenu(getListView());
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getActionBar().setHomeButtonEnabled(true);
    getActionBar().setDisplayHomeAsUpEnabled(true);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        Intent homeIntent = new Intent(this, MainActivity.class);
        startActivity(homeIntent);

        break;

      default:
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    if (v.getId() == android.R.id.list) {
      this.getMenuInflater().inflate(R.menu.list_context, menu);
    }
    super.onCreateContextMenu(menu, v, menuInfo);
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

    switch (item.getItemId()) {
      case R.id.ctx_edit:

        break;
      case R.id.ctx_delete:
        // Abfrage, ob wirklich gelöscht werden soll
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Löschen ...").setMessage("Wollen Sie den Datenasatz wirklich löschen?")
                .setNegativeButton("NEIN!", new DialogInterface.OnClickListener() {

                  public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                  }
                }).setPositiveButton("Ja", new DialogInterface.OnClickListener() {

                  public void onClick(DialogInterface dialog, int which) {
                    ZeitTabelle.LoescheDatensatz(_Db, info.id);

                    LoadData();
                  }
                });

        builder.create().show();

        break;
      case R.id.ctx_export:
        // Fortschrittsdialog erzeugen
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Exportiere ...");
        dialog.setMessage("Daten werden auf SD-Karten exportiet!");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        final CSVExporter export = new CSVExporter("export", dialog);

        dialog.setCancelable(true);
        dialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "Abbrechen", new DialogInterface.OnClickListener() {

          public void onClick(DialogInterface dialog, int which) {
            export.cancel(true);
          }
        });

        // Daten aus der DB lesen
        Cursor data = ZeitTabelle.LiefereAlleDaten(_Db);

        // Export der Daten
        export.execute(new Cursor[] { data });

        break;

      default:
        break;
    }
    return super.onContextItemSelected(item);
  }

  private void LoadData() {
    _Cursor = ZeitTabelle.LiefereAlleDaten(_Db);

    SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, // Context
            R.layout.list_row2, // Layout für die Zeile
            _Cursor, // Daten
            new String[] { ZeitTabelle.STARTZEIT, ZeitTabelle.ENDZEIT }, // Anzuzeigende Spalten
            new int[] { R.id.start, R.id.ende }, // IDs der Views, in die die Daten der Spalten platziert werden
            0); // Flag

    setListAdapter(adapter);
  }
}
