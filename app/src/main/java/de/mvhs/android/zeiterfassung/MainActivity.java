package de.mvhs.android.zeiterfassung;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private EditText _startValue;
    private Button _startCommand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Suchen der Elemente im Layout
        _startValue = (EditText) findViewById(R.id.StartTimeValue);
        _startCommand = (Button) findViewById(R.id.StartCommand);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Event registrieren
        _startCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Was soll der Button machen
                _startValue.setText(new Date().toString());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Event deregistrieren
        _startCommand.setOnClickListener(null);
    }
}
