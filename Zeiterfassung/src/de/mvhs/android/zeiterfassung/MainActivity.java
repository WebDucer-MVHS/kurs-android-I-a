package de.mvhs.android.zeiterfassung;

import java.text.DateFormat;
import java.text.ParseException;
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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import de.mvhs.android.zeiterfassung.db.DBHelper;
import de.mvhs.android.zeiterfassung.db.ZeitContract;
import de.mvhs.android.zeiterfassung.db.ZeitContract.Zeit;

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

    String where = "IFNULL(" + Zeit.Columns.END + ", '') = ''";
    Cursor data = getContentResolver().query(Zeit.CONTENT_URI, null, where, null, null);

    EditText start = (EditText) findViewById(R.id.text_start_time);
    EditText ende = (EditText) findViewById(R.id.text_end_time);

    if (data != null && data.moveToFirst()) {
      // Ein leerer Eintrag gefunden
      try {
        String startZeit = data.getString(data.getColumnIndex(Zeit.Columns.START));
        Date startTime = ZeitContract.DB_DATE_FORMAT.parse(startZeit);
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

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    this.getMenuInflater().inflate(R.menu.main_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.mnu_add:
        Intent addIntent = new Intent(this, EditActivity.class);
        addIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(addIntent);
        break;

      case R.id.mnu_exit:
        this.finish();
        break;

      case R.id.mnu_list:
        Intent listIntent = new Intent(this, EntryListActivity.class);
        listIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(listIntent);
        break;

      default:
        break;
    }
    return super.onOptionsItemSelected(item);
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
    values.put(Zeit.Columns.START, ZeitContract.DB_DATE_FORMAT.format(jetzt));

    Uri insert = getContentResolver().insert(Zeit.CONTENT_URI, values);

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
    String where = "IFNULL(" + Zeit.Columns.END + ", '') = ''";
    Cursor data = getContentResolver().query(Zeit.CONTENT_URI, new String[] { BaseColumns._ID }, where, null, null);

    if (data != null && data.moveToFirst()) {
      long id = data.getLong(0);

      // Daten in die Datenbank aktualisieren
      ContentValues values = new ContentValues();
      values.put(Zeit.Columns.END, ZeitContract.DB_DATE_FORMAT.format(jetzt));
      Uri insertUri = ContentUris.withAppendedId(Zeit.CONTENT_URI, id);

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
