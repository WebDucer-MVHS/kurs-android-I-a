package de.mvhs.android.zeiterfassung;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import de.mvhs.android.zeiterfassung.db.ZeitContract;
import de.mvhs.android.zeiterfassung.utils.Converter;


public class TimeTrackingActivity extends ActionBarActivity {
   // Klassenvariablen
   // UI Elemente
   private EditText _startTime = null;
   private EditText _endTime = null;
   private Button _startCommand = null;
   private Button _endCommand = null;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_time_tracking);

      // Suchen der UI Elemente aus dem Layout
      _startTime = (EditText) findViewById(R.id.StartTime);
      _endTime = (EditText) findViewById(R.id.EndTime);
      _startCommand = (Button) findViewById(R.id.StartCommand);
      _endCommand = (Button) findViewById(R.id.EndCommand);

      // Deaktivieren der Bearbeitung in EditText-Feldern
      _startTime.setKeyListener(null);
      _endTime.setKeyListener(null);
   }

   @Override
   protected void onStart() {
      super.onStart();

      // Registrieren der Listener (Events)
      _startCommand.setOnClickListener(new OnStartButtonClicked());
      _endCommand.setOnClickListener(new OnEndButtonClicked());

      // Ende Button nach dem Start deaktivieren
      _endCommand.setEnabled(false);

      // Daten aus der Datenbank lesen
      initData();
   }

   private void initData() {
      Cursor data = getContentResolver().query(ZeitContract.ZeitDaten.EMPTY_CONTENT_URI, new String[] {ZeitContract.ZeitDaten.Columns.START_TIME}, null, null, null);

      // Offener Datensatz vorhanden
      if (data != null && data.moveToFirst()) {
         String startDateString = data.getString(0); // weil wir nur eine Spalte abfragen
         try {
            Date startDate = ZeitContract.Converters.DB_DATE_TIME_FORMATTER.parse(startDateString);

            _startTime.setText(Converter.toDateTimeString(startDate));
            _startCommand.setEnabled(false);
            _endCommand.setEnabled(true);
         } catch (ParseException e) {
            // Startdatum liegt im falschen Format vor.
            Toast.makeText(this, R.string.start_date_invalid_format, Toast.LENGTH_LONG).show();
         }
      }
   }

   @Override
   protected void onStop() {
      super.onStop();

      // Deregistrieren der Listender (Events)
      _startCommand.setOnClickListener(null);
      _endCommand.setOnClickListener(null);
   }


   // Interne Klassen
   private class OnStartButtonClicked implements View.OnClickListener {

      @Override
      public void onClick(View v) {
         // Deaktivieren des Buttons
         _startCommand.setEnabled(false);

         Calendar jetzt = Calendar.getInstance();

         ContentValues values = new ContentValues();
         values.put(ZeitContract.ZeitDaten.Columns.START_TIME, ZeitContract.Converters.DB_DATE_TIME_FORMATTER.format(jetzt.getTime()));

         getContentResolver().insert(ZeitContract.ZeitDaten.CONTENT_URI, values);

         // Logik nach dem Klicken des Buttons
         _startTime.setText(Converter.toDateTimeString(jetzt.getTime()));

         // Aktivieren des Ende Buttons
         _endCommand.setEnabled(true);
      }
   }

   private class OnEndButtonClicked implements View.OnClickListener {

      @Override
      public void onClick(View v) {
         // Dekativieren des Buttons
         _endCommand.setEnabled(false);

         Calendar jetzt = Calendar.getInstance();

         ContentValues values = new ContentValues();
         values.put(ZeitContract.ZeitDaten.Columns.END_TIME, ZeitContract.Converters.DB_DATE_TIME_FORMATTER.format(jetzt.getTime()));

         getContentResolver().update(ZeitContract.ZeitDaten.EMPTY_CONTENT_URI, values, null, null);

         // Logik nach dem Klicken des Buttons
         _endTime.setText(Converter.toDateTimeString(jetzt.getTime()));

         // Aktivieren des Start Buttons
         _startCommand.setEnabled(true);
      }
   }
}
