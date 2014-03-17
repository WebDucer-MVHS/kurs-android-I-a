package de.mvhs.android.zeiterfassung;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		// Buttons registrieren
		Button commandStart = (Button) findViewById(R.id.StartCommand);
		Button commandEnd = (Button) findViewById(R.id.EndCommand);
		
		// Click Event registrieren
		commandStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Verhalten beim Klick auf den Strat Button
				EditText startTime = (EditText) findViewById(R.id.StartTime);
				startTime.setText(new Date().toString());
			}
		});
		
		commandEnd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Verhalten beim Click auf den Ende-Button
				EditText endTime = (EditText) findViewById(R.id.EndTime);
				endTime.setText(new Date().toString());
			}
		});
	}
	
	
	
	
	
	
	
	
	
	
	
}
