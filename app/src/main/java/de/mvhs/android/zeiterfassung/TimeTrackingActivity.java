package de.mvhs.android.zeiterfassung;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Registrieren der Listener (Events)
        _startCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logik nach dem Klicken des Buttons
                _startTime.setText(String.valueOf(new Date()));
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Deregistrieren der Listender (Events)
        _startCommand.setOnClickListener(null);
    }




















}
