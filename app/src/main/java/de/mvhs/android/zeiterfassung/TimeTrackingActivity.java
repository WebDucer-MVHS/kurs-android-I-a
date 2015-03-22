package de.mvhs.android.zeiterfassung;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;


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

         // Logik nach dem Klicken des Buttons
         _startTime.setText(String.valueOf(new Date()));

         // Aktivieren des Ende Buttons
         _endCommand.setEnabled(true);
      }
   }

   private class OnEndButtonClicked implements View.OnClickListener {

      @Override
      public void onClick(View v) {
         // Dekativieren des Buttons
         _endCommand.setEnabled(false);

         // Logik nach dem Klicken des Buttons
         _endTime.setText(String.valueOf(new Date()));

         // Aktivieren des Start Buttons
         _startCommand.setEnabled(true);
      }
   }
}
