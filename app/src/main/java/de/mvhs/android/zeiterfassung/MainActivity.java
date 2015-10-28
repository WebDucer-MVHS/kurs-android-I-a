package de.mvhs.android.zeiterfassung;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;

import de.mvhs.android.zeiterfassung.db.ZeitContract;

public class MainActivity extends AppCompatActivity {
  // Klassenvaribalen
  private Button _startCommand;
  private Button _endCommand;
  private EditText _startField;
  private EditText _endField;

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

  private void initFromDb() {
    Cursor data = getContentResolver().query(
        ZeitContract.ZeitDaten.EMPTY_CONTENT_URI, // URI
        null, // Spalten
        null, // Bedingung
        null, // Argumente für die Bedingung
        null); // Sortierung

    if (data != null && data.moveToFirst()) {
      // Einen leeren Datensatz gefunden
      String startTime = data.getString(data.getColumnIndex("StartZeit"));
      _startField.setText(startTime);
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
      values.put("StartZeit", startTime.getTime().toString());
      getContentResolver().insert(ZeitContract.ZeitDaten.CONTENT_URI, values);
      _startField.setText(startTime.getTime().toString());
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
      values.put("EndZeit", endTime.getTime().toString());

      Cursor data = getContentResolver().query(ZeitContract.ZeitDaten.EMPTY_CONTENT_URI, null, null, null, null);

      if(data != null && data.moveToFirst()){
        long id = data.getLong(data.getColumnIndex(BaseColumns._ID));
        Uri updateUri = ContentUris.withAppendedId(ZeitContract.ZeitDaten.CONTENT_URI, id);
        getContentResolver().update(updateUri, values, null, null);
      }

      _endField.setText(endTime.getTime().toString());

      _startCommand.setEnabled(true);

      // Cursor beenden
      if(data != null){
        data.close();
      }
    }
  }
}
