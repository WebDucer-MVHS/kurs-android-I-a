package de.mvhs.android.zeiterfassung;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;

import db.TimelogContract;

public class MainActivity extends AppCompatActivity {
    // Logging Tag
    private final static String _TAG = MainActivity.class.getSimpleName();

    // UI Formatter für Datum und Uhrzeit
    private DateFormat _UI_DATE_FORMATTER = DateFormat.getDateTimeInstance(
            DateFormat.SHORT,
            DateFormat.SHORT
    );

    // Spalten für die Initialisierung
    private final static String[] _INIT_DB_COLUMNS = {
            TimelogContract.Timelog.Columns.START
    };
    // Index für die Initialisierung
    private final static int _INIT_START_COLUMN_INDEX = 0;

    // UI Elemente
    private Button _startCommand;
    private EditText _startTime;
    private Button _endCommand;
    private EditText _endTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_relative);

        // Suchen der Elemente vom Layout
        _startCommand = (Button) findViewById(R.id.StartCommand);
        _endCommand = (Button) findViewById(R.id.EndCommand);
        _startTime = (EditText) findViewById(R.id.StartTime);
        _startTime.setKeyListener(null); // Deaktivieren der Tastatureingaben
        _endTime = (EditText) findViewById(R.id.EndTime);
        _endTime.setKeyListener(null); // Deaktivoeren der Tastatureingaben
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Menü aus XML laden
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.TimelogListAction:
                // Starten der neuen Activity
                Intent listIntent = new Intent(getBaseContext(),
                        TimeListActivity.class);

                startActivity(listIntent);
                return true;

            case R.id.NewTimeLogAction:
                Intent editIntent = new Intent(getBaseContext(),
                        EditActivity.class);

                startActivity(editIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Registrierung der Listener
        _startCommand.setOnClickListener(new StartButtonClicked());
        _endCommand.setOnClickListener(new EndButtonClicked());

        // Initialisierung aus den Datenbank-Daten
        InitDbSate();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Deregistrierung der Listener
        _startCommand.setOnClickListener(null);
        _endCommand.setOnClickListener(null);
    }

    private void InitDbSate() {
        Cursor notFinishedData = getContentResolver().query(
                TimelogContract.Timelog.NOT_FINISHED_URI, // Nicht geschlossener Datensatz
                _INIT_DB_COLUMNS, // Spalten
                null, // kein Filter
                null, // keine Parameter
                null); // keine Sortierung

        if (notFinishedData != null && notFinishedData.moveToFirst()) { // Ein Eintrag ist vorhanden
            setButtonState(true);
            Calendar startTime = Calendar.getInstance();
            String startString = notFinishedData.getString(_INIT_START_COLUMN_INDEX);
            try {
                startTime.setTime(
                        TimelogContract.Converter.DB_DATE_TIME_FORMATTER.parse(startString));
                _startTime.setText(_UI_DATE_FORMATTER.format(startTime.getTime()));
            } catch (ParseException e) {
                Log.e(_TAG, "Wert konnte nicht in ein Datum umgewandelt werden!", e);
            }
        } else { // Kein Eintrag gefunden
            setButtonState(false);
            _startTime.setText("");
            _endTime.setText("");
        }
    }

    private void setButtonState(boolean isStarted) {
        _startCommand.setEnabled(!isStarted);
        _endCommand.setEnabled(isStarted);
    }

    private class StartButtonClicked implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Calendar now = Calendar.getInstance();
            ContentValues values = new ContentValues();
            values.put(TimelogContract.Timelog.Columns.START,
                    TimelogContract.Converter.DB_DATE_TIME_FORMATTER.format(now.getTime()));

            Uri insertUri = getContentResolver().insert(
                    TimelogContract.Timelog.CONTENT_URI, values);
            if (insertUri != null) { // Wert konnte gespeichert werden
                _startTime.setText(_UI_DATE_FORMATTER.format(now.getTime()));
                setButtonState(true);
            } else { // Beim Speichern ist etwas schied gelaufen
                Toast.makeText(MainActivity.this, R.string.error_on_save, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class EndButtonClicked implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Calendar now = Calendar.getInstance();
            ContentValues values = new ContentValues();
            values.put(TimelogContract.Timelog.Columns.END,
                    TimelogContract.Converter.DB_DATE_TIME_FORMATTER.format(now.getTime()));

            int updatedItems = getContentResolver().update(
                    TimelogContract.Timelog.NOT_FINISHED_URI, values, null, null);
            if (updatedItems == 1) { // Wert konnte gespeichert werden
                _endTime.setText(_UI_DATE_FORMATTER.format(now.getTime()));
                setButtonState(false);
            } else { // Beim Speichern ist etwas schied gelaufen
                Toast.makeText(MainActivity.this, R.string.error_on_save, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
