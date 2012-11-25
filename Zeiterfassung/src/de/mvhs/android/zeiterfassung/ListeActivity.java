package de.mvhs.android.zeiterfassung;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
    _Cursor = ZeitTabelle.LiefereAlleDaten(_Db);

    SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, // Context
            R.layout.list_row2, // Layout für die Zeile
            _Cursor, // Daten
            new String[] { ZeitTabelle.STARTZEIT, ZeitTabelle.ENDZEIT }, // Anzuzeigende Spalten
            new int[] { R.id.start, R.id.ende }, // IDs der Views, in die die Daten der Spalten platziert werden
            0); // Flag

    setListAdapter(adapter);
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
}
