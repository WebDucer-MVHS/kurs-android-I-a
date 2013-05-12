package de.mvhs.android.arbeitszeiterfassung;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import de.mvhs.android.arbeitszeiterfassung.db.ZeitabschnittContract;
import de.mvhs.android.arbeitszeiterfassung.db.ZeitabschnittContract.Zeitabschnitte;

public class MainActivity extends Activity {
  // Klassen-Variablen
  private final static String[] _PROJECTION     = new String[] { BaseColumns._ID, Zeitabschnitte.Columns.START };
  private final static String   _SELECTION      = "IFNULL(TRIM(" + Zeitabschnitte.Columns.STOP + "),'')=''";
  private final DateFormat      _DateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // UI Elemente binden
    final Button cmdStartEnd = (Button) findViewById(R.id.cmd_start_stop);

    // Click-Event zuweisen
    cmdStartEnd.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        onButtonClicked();
      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();
    // UI Elemente binden
    final Button cmdStartEnd = (Button) findViewById(R.id.cmd_start_stop);
    final EditText txtStart = (EditText) findViewById(R.id.txt_start_time);
    final EditText txtEnd = (EditText) findViewById(R.id.txt_end_time);

    // Prüfen, ob ein unvollständiger Datensatz in der Datenbank vorliegt (ohne Endzeit)
    Cursor data = getContentResolver().query(Zeitabschnitte.CONTENT_URI, _PROJECTION, _SELECTION, null, null);

    if (data != null && data.moveToFirst()) {
      // Offener Datensatz vorhanden
      cmdStartEnd.setText(R.string.cmd_end);
      txtEnd.setText("");

      int startIndex = data.getColumnIndex(Zeitabschnitte.Columns.START);

      String startTime = data.getString(startIndex);
      try {
        Date start = ZeitabschnittContract.DB_DATE_FORMATTER.parse(startTime);
        txtStart.setText(_DateTimeFormat.format(start));
      } catch (ParseException e) {
        txtStart.setText(startTime);
      }
    } else {
      // Kein offenter Datensatz gefunden
      cmdStartEnd.setText(R.string.cmd_start);
      txtEnd.setText("");
      txtStart.setText("");
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    // Menü entfalten
    inflater.inflate(R.menu.main_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.mnu_list:
        Intent listActivity = new Intent(this, AuflistungActivity.class);
        listActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(listActivity);

        // Toast.makeText(
        // this,
        // "Auflistung noch nicht implementiert!",
        // Toast.LENGTH_LONG)
        // .show();
        break;

      default:
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  private void onButtonClicked() {
    // UI Elemente binden
    final Button cmdStartEnd = (Button) findViewById(R.id.cmd_start_stop);
    final EditText txtStart = (EditText) findViewById(R.id.txt_start_time);
    final EditText txtEnd = (EditText) findViewById(R.id.txt_end_time);

    // Prüfen, ob ein unvollständiger Datensatz in der Datenbank vorliegt (ohne Endzeit)
    Date jetztDate = Calendar.getInstance().getTime();
    String jetzt = ZeitabschnittContract.DB_DATE_FORMATTER.format(jetztDate);
    Cursor data = getContentResolver().query(Zeitabschnitte.CONTENT_URI, _PROJECTION, _SELECTION, null, null);

    if (data != null && data.moveToFirst()) {
      // Offener Datensatz bereits vorhanden, Datensatz mit Enddatum aktualisieren
      long id = data.getLong(data.getColumnIndex(BaseColumns._ID));
      Uri uri = ContentUris.withAppendedId(Zeitabschnitte.CONTENT_URI, id);

      ContentValues values = new ContentValues();
      values.put(Zeitabschnitte.Columns.STOP, jetzt);

      int updates = getContentResolver().update(uri, values, null, null);
      if (updates == 1) {
        // Aktuallisierung erfolgreich abgeschlossen
        cmdStartEnd.setText(R.string.cmd_start);
        txtEnd.setText(_DateTimeFormat.format(jetztDate));
      } else {
        // Meldung an den Benutzer
        Toast.makeText(this, R.string.error_not_saved, Toast.LENGTH_LONG).show();
      }
    } else {
      // Einen neuen Datensatz anlegen
      ContentValues values = new ContentValues();
      values.put(Zeitabschnitte.Columns.START, jetzt);

      Uri uri = getContentResolver().insert(Zeitabschnitte.CONTENT_URI, values);
      if (uri != null) {
        // Speichervorgang erfolgreich abgeschlossen
        cmdStartEnd.setText(R.string.cmd_end);
        txtStart.setText(_DateTimeFormat.format(jetztDate));
        txtEnd.setText("");
      } else {
        // Meldung an den Benutzer
        Toast.makeText(this, R.string.error_not_saved, Toast.LENGTH_LONG).show();
      }
    }
  }
}
