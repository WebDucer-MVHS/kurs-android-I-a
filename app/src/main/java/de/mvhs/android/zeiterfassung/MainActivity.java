package de.mvhs.android.zeiterfassung;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import de.mvhs.android.zeiterfassung.db.ZeitContract;

public class MainActivity extends AppCompatActivity {
  // Klassenvaribalen
  private Button _startCommand;
  private Button _endCommand;
  private EditText _startField;
  private EditText _endField;

  private final DateFormat _UI_DATE_TIME_FORMATTER =
          DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // UI Elemente in dem Layout suchen
    _startCommand = (Button) findViewById(R.id.StartCommand);
    _startField = (EditText) findViewById(R.id.StartTimeField);
    _endCommand = (Button) findViewById(R.id.EndCommand);
    _endField = (EditText) findViewById(R.id.EndTimeField);
  }

  @Override
  protected void onStart() {
    super.onStart();

    // Registrieren der Events
    _startCommand.setOnClickListener(new OnStartClicked());
    _endCommand.setOnClickListener(new OnEndClicked());

    // Überprüfen der gespeicherten Daten
    _startCommand.setEnabled(false);
    _endCommand.setEnabled(false);
    initFromDb();
  }

  @Override
  protected void onStop() {
    super.onStop();
    // Deregistrieren der Events
    _startCommand.setOnClickListener(null);
    _endCommand.setOnClickListener(null);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Menü aus XML laden
    getMenuInflater().inflate(R.menu.main_menu, menu);

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Verarbeiten der Menüeinträge
    switch (item.getItemId()){
      case R.id.RecordList:
        // Starten der neuen Activity
        Intent listActivity = new Intent(getBaseContext(), RecordListActivity.class);

        startActivity(listActivity);
        return true;

      case R.id.NewRecord:
        Intent editIntent = new Intent(getBaseContext(), EditActivity.class);

        startActivity(editIntent);

        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void initFromDb() {
    Cursor data = getContentResolver().query(
        ZeitContract.ZeitDaten.EMPTY_CONTENT_URI, // URI
        null, // Spalten
        null, // Bedingung
        null, // Argumente für die Bedingung
        null); // Sortierung

    if (data != null && data.moveToFirst()) {
      // Einen leeren Datensatz gefunden
      String startTime = data.getString(data.getColumnIndex(ZeitContract.ZeitDaten.Columns.START));

      // Convertierung des Strings in ein Datum
      try {
        Date start = ZeitContract.Converters.DB_DATE_TIME_FORMATTER.parse(startTime);
        _startField.setText(_UI_DATE_TIME_FORMATTER.format(start));
      } catch (ParseException e) {
        e.printStackTrace();
        _startField.setText("Falscher Format in der Datenbank");
      }

      _endCommand.setEnabled(true);
    } else {
      // Keinen leeren Datensatz gefunden
      _startCommand.setEnabled(true);
    }

    // Cursor beenden
    if(data != null){
      data.close();
    }
  }

  private class OnStartClicked implements View.OnClickListener {

    @Override
    public void onClick(View v) {
      _startCommand.setEnabled(false);

      Calendar startTime = Calendar.getInstance();
      ContentValues values = new ContentValues();
      values.put(ZeitContract.ZeitDaten.Columns.START,
              ZeitContract.Converters.DB_DATE_TIME_FORMATTER.format(startTime.getTime()));

      getContentResolver().insert(ZeitContract.ZeitDaten.CONTENT_URI, values);

      _startField.setText(_UI_DATE_TIME_FORMATTER.format(startTime.getTime()));
      _endField.setText("");

      _endCommand.setEnabled(true);
    }
  }

  private class OnEndClicked implements View.OnClickListener {

    @Override
    public void onClick(View v) {
      _endCommand.setEnabled(false);

      Calendar endTime = Calendar.getInstance();
      ContentValues values = new ContentValues();
      values.put(ZeitContract.ZeitDaten.Columns.END,
              ZeitContract.Converters.DB_DATE_TIME_FORMATTER.format(endTime.getTime()));

      /*Cursor data = getContentResolver().query(ZeitContract.ZeitDaten.EMPTY_CONTENT_URI, null, null, null, null);

      if(data != null && data.moveToFirst()){
        long id = data.getLong(data.getColumnIndex(BaseColumns._ID));
        Uri updateUri = ContentUris.withAppendedId(ZeitContract.ZeitDaten.CONTENT_URI, id);
        getContentResolver().update(updateUri, values, null, null);
      }*/

      getContentResolver().update(ZeitContract.ZeitDaten.EMPTY_CONTENT_URI, values, null, null);

      _endField.setText(_UI_DATE_TIME_FORMATTER.format(endTime.getTime()));

      _startCommand.setEnabled(true);

      // Cursor beenden
      /*if(data != null){
        data.close();
      }*/
    }
  }
}
