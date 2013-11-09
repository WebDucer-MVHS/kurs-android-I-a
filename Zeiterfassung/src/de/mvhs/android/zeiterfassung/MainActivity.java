package de.mvhs.android.zeiterfassung;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

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
import de.mvhs.android.zeiterfassung.db.DBHelper;
import de.mvhs.android.zeiterfassung.db.ZeitProvider;

public class MainActivity extends Activity {
  private boolean                 _IsStarted      = false;
  private final static String     _EMPTY          = "";

  private final static DateFormat _DateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Suchen der Elemente in eingebundenen Layout
    Button starten = (Button) findViewById(R.id.button_start);
    Button beenden = (Button) findViewById(R.id.button_end);

    // Alternative
    starten.setOnClickListener(onClicked);
    beenden.setOnClickListener(onClicked);

    DBHelper helper = new DBHelper(this);
    helper.getReadableDatabase();
    helper.close();
  }

  @Override
  protected void onStart() {
    super.onStart();

    String where = "IFNULL(" + ZeitProvider.Columns.END + ", '') = ''";
    Cursor data = getContentResolver().query(ZeitProvider.CONTENT_URI, null, where, null, null);

    EditText start = (EditText) findViewById(R.id.text_start_time);
    EditText ende = (EditText) findViewById(R.id.text_end_time);

    if (data != null && data.moveToFirst()) {
      // Ein leerer Eintrag gefunden
      try {
        String startZeit = data.getString(data.getColumnIndex(ZeitProvider.Columns.START));
        Date startTime = ZeitProvider.DB_DATE_FORMAT.parse(startZeit);
        start.setText(_DateTimeFormat.format(startTime));
      } catch (ParseException e) {
        e.printStackTrace();
        start.setText(_EMPTY);
        Toast.makeText(this, "Wrong Date Time Format in the Data Base!", Toast.LENGTH_LONG).show();
      }
      _IsStarted = true;
    } else {
      start.setText(_EMPTY);
      _IsStarted = false;
    }

    ende.setText(_EMPTY);

    changeButtonState();
  }

  private void changeButtonState() {
    // Buttons suchen
    Button starten = (Button) findViewById(R.id.button_start);
    Button beenden = (Button) findViewById(R.id.button_end);

    // Aktivieren / Deaktivieren
    if (_IsStarted) {
      starten.setEnabled(false);
      beenden.setEnabled(true);
    } else {
      beenden.setEnabled(false);
      starten.setEnabled(true);
    }
  }

  private void insertNewItem() {
    // Ausgabe Element suchen
    EditText start = (EditText) findViewById(R.id.text_start_time);

    // Aktuelles Datum bestimmen
    Date jetzt = new Date();
    _IsStarted = true;
    // Daten in die Datenbank spiechern
    ContentValues values = new ContentValues();
    values.put(ZeitProvider.Columns.START, ZeitProvider.DB_DATE_FORMAT.format(jetzt));

    Uri insert = getContentResolver().insert(ZeitProvider.CONTENT_URI, values);

    if (insert == null) {
      Toast.makeText(this, "Insert of new Entry failed", Toast.LENGTH_LONG).show();
    } else {
      Toast.makeText(this, "Entry saved!", Toast.LENGTH_LONG).show();
    }

    start.setText(_DateTimeFormat.format(jetzt));
  }

  private void updateItem() {
    // Ausgabe Element suchen
    EditText ende = (EditText) findViewById(R.id.text_end_time);

    // Aktuelles Datum bestimmen
    Date jetzt = new Date();
    _IsStarted = false;

    // Datensatz suchen
    String where = "IFNULL(" + ZeitProvider.Columns.END + ", '') = ''";
    Cursor data = getContentResolver().query(ZeitProvider.CONTENT_URI, new String[] { BaseColumns._ID }, where, null, null);

    if (data != null && data.moveToFirst()) {
      long id = data.getLong(0);

      // Daten in die Datenbank aktualisieren
      ContentValues values = new ContentValues();
      values.put(ZeitProvider.Columns.END, ZeitProvider.DB_DATE_FORMAT.format(jetzt));
      Uri insertUri = ContentUris.withAppendedId(ZeitProvider.CONTENT_URI, id);

      int updated = getContentResolver().update(insertUri, values, null, null);

      if (updated != 1) {
        Toast.makeText(this, "Update of new Entry failed", Toast.LENGTH_LONG).show();
      } else {
        Toast.makeText(this, "Entry updated!", Toast.LENGTH_LONG).show();
      }

      ende.setText(_DateTimeFormat.format(jetzt));
    }
  }

  // Alternative
  private OnClickListener onClicked = new OnClickListener() {

                                      @Override
                                      public void onClick(View v) {

                                        if (v.getId() == R.id.button_start) {
                                          insertNewItem();

                                        } else if (v.getId() == R.id.button_end) {
                                          updateItem();
                                        }

                                        changeButtonState();
                                      }
                                    };
}
