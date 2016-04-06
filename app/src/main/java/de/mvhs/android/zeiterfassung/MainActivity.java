package de.mvhs.android.zeiterfassung;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Suchen der Elemente vom Layout
        Button startCommand = (Button) findViewById(R.id.StartCommand);
        final EditText startTime = (EditText) findViewById(R.id.StartTime);

        startCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime.setText(String.valueOf(new Date()));
            }
        });
    }
}
