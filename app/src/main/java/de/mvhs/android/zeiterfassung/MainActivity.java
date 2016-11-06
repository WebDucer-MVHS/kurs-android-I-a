package de.mvhs.android.zeiterfassung;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.TintContextWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import de.mvhs.android.zeiterfassung.db.DbHelper;
import de.mvhs.android.zeiterfassung.db.TimeDataContract;

public class MainActivity extends AppCompatActivity {

  private EditText _startValue;
  private Button _startCommand;
  private EditText _endValue;
  private Button _endCommand;

  private DateFormat _UI_DATE_TIME_FORMATTER = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Linear Layout
    //setContentView(R.layout.activity_main_linear);

    // Relative layout
    //setContentView(R.layout.activity_main_relative);

    // Grid layout
    setContentView(R.layout.activity_main_grid);

    // Suchen der Elemente im Layout
    _startValue = (EditText) findViewById(R.id.StartTimeValue);
    _startCommand = (Button) findViewById(R.id.StartCommand);
    _endValue = (EditText) findViewById(R.id.EndTimeValue);
    _endCommand = (Button) findViewById(R.id.EndCommand);
  }

  @Override
  protected void onStart() {
    super.onStart();

    // Initialisierung aus der Datenbank heraus
    initFromDb();

    // Event registrieren
    _startCommand.setOnClickListener(new OnStartButtonClicked());
    _endCommand.setOnClickListener(new OnEndButtonClicked());
  }

  @Override
  protected void onStop() {
    super.onStop();

    // Event deregistrieren
    _startCommand.setOnClickListener(null);
    _endCommand.setOnClickListener(null);
  }

  private void initFromDb() {
    // Beide Buttons deaktivieren
    _startCommand.setEnabled(false);
    _endCommand.setEnabled(false);

    // Offenen Datensatz aus der Datenbank auslesen
    Cursor data = getContentResolver().query(
        TimeDataContract.TimeData.NOT_FINISHED_CONTENT_URI, // Uri
        new String[]{TimeDataContract.TimeData.Columns.START}, // Spalten
        null, // Filter
        null, // Filter Argumente
        null); // Sortierung

    // Prüfen auf einen Datensatz
    if (data.moveToFirst()) {
      try {
        Calendar startTime = TimeDataContract.Converter.parseFromDb(data.getString(0));
        // Ausgabe der Startzeit
        _startValue.setText(_UI_DATE_TIME_FORMATTER.format(startTime.getTime()));
        _endValue.setText("");

        // Beenden Button aktivieren
        _endCommand.setEnabled(true);
      } catch (ParseException e) {
        // Datum kann nicht konvertiert werden
        _startValue.setText("PARSE ERROR");
      }
    } else {
      _startValue.setText("");
      _endValue.setText("");

      // Start Button aktivieren
      _startCommand.setEnabled(true);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.MenuListData:
        Intent listIntent = new Intent(this, ListDataActivity.class);
        startActivity(listIntent);
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  class OnStartButtonClicked implements View.OnClickListener {

    @Override
    public void onClick(View v) {
      // Start Button deaktivieren
      _startCommand.setEnabled(false);

      // Aktuelles Datum
      Calendar nowDateTime = Calendar.getInstance();

      // Einfügen über Provider
      ContentValues value = new ContentValues();
      value.put(TimeDataContract.TimeData.Columns.START, TimeDataContract.Converter.formatForDb(nowDateTime));
      getContentResolver().insert(TimeDataContract.TimeData.CONTENT_URI, value);

      // Datum ausgeben
      _startValue.setText(_UI_DATE_TIME_FORMATTER.format(nowDateTime.getTime()));

      // Beenden Button aktivieren
      _endCommand.setEnabled(true);
    }
  }

  class OnEndButtonClicked implements View.OnClickListener {

    @Override
    public void onClick(View v) {
      // Ende Button deaktivieren
      _endCommand.setEnabled(false);

      // Aktuelles Datum
      Calendar nowDateTime = Calendar.getInstance();

      // Einfügen über Provider
      ContentValues value = new ContentValues();
      value.put(TimeDataContract.TimeData.Columns.END, TimeDataContract.Converter.formatForDb(nowDateTime));
      getContentResolver().update(TimeDataContract.TimeData.NOT_FINISHED_CONTENT_URI, value, null, null);

      // Datum ausgeben
      _endValue.setText(_UI_DATE_TIME_FORMATTER.format(nowDateTime.getTime()));

      // Start Button aktivieren
      _startCommand.setEnabled(true);
    }
  }
}
