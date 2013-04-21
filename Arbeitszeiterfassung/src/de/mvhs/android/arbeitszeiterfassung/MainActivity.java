package de.mvhs.android.arbeitszeiterfassung;

import java.util.Calendar;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import de.mvhs.android.arbeitszeiterfassung.db.ZeitabschnittContract;
import de.mvhs.android.arbeitszeiterfassung.db.ZeitabschnittContract.Zeitabschnitte;

public class MainActivity extends Activity {
  // Klassen-Variablen
  private final static String[] _PROJECTION = new String[] { BaseColumns._ID, Zeitabschnitte.Columns.START };
  private final static String   _SELECTION  = "IFNULL(TRIM(" + Zeitabschnitte.Columns.STOP + "),'')=''";

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

      String startTime = data.getString(data.getColumnIndex(Zeitabschnitte.Columns.START));
      txtStart.setText(startTime);
    } else {
      // Kein offenter Datensatz gefunden
      cmdStartEnd.setText(R.string.cmd_start);
      txtEnd.setText("");
      txtStart.setText("");
    }
  }

  private void onButtonClicked() {
    // UI Elemente binden
    final Button cmdStartEnd = (Button) findViewById(R.id.cmd_start_stop);
    final EditText txtStart = (EditText) findViewById(R.id.txt_start_time);
    final EditText txtEnd = (EditText) findViewById(R.id.txt_end_time);

    // Prüfen, ob ein unvollständiger Datensatz in der Datenbank vorliegt (ohne Endzeit)
    String jetzt = ZeitabschnittContract.DB_DATE_FORMATTER.format(Calendar.getInstance().getTime());
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
        txtEnd.setText(jetzt);
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
        txtStart.setText(jetzt);
        txtEnd.setText("");
      } else {
        // Meldung an den Benutzer
        Toast.makeText(this, R.string.error_not_saved, Toast.LENGTH_LONG).show();
      }
    }
  }
}
