package de.mvhs.android.zeiterfassung;

import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import de.mvhs.android.zeiterfassung.db.DBHelper;

public class MainActivity extends Activity {
	private boolean _IsStarted = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		if (savedInstanceState != null) {
			_IsStarted = savedInstanceState.getBoolean("CurrentState", false);
		}

		setButtonState();
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
				_IsStarted = true;
				setButtonState();

				DBHelper helper = new DBHelper(MainActivity.this);
				SQLiteDatabase db = helper.getWritableDatabase();

				ContentValues values = new ContentValues();
				values.put("start_time", new Date().toString());

				db.insert("zeit", null, values);

				db.close();
			}
		});

		commandEnd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Verhalten beim Click auf den Ende-Button
				EditText endTime = (EditText) findViewById(R.id.EndTime);
				endTime.setText(new Date().toString());
				_IsStarted = false;
				setButtonState();
			}
		});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("CurrentState", _IsStarted);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStop() {
		// Buttons registrieren
		Button commandStart = (Button) findViewById(R.id.StartCommand);
		Button commandEnd = (Button) findViewById(R.id.EndCommand);

		// Click Event deregistrieren
		commandStart.setOnClickListener(null);
		commandEnd.setOnClickListener(null);
		super.onStop();
	}

	private void setButtonState() {
		Button commandStart = (Button) findViewById(R.id.StartCommand);
		Button commandEnd = (Button) findViewById(R.id.EndCommand);

		commandStart.setEnabled(_IsStarted == false);
		commandEnd.setEnabled(_IsStarted);
	}
}
