package de.mvhs.android.zeiterfassung;

import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import de.mvhs.android.zeiterfassung.db.DBHelper;
import de.mvhs.android.zeiterfassung.db.ZeitContracts;

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
		commandStart.setOnClickListener(new OnStartButtonClicked());

		commandEnd.setOnClickListener(new OnEndButtonClicked());
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

	private final class OnEndButtonClicked implements OnClickListener {
		@Override
		public void onClick(View v) {
			// Verhalten beim Click auf den Ende-Button
			EditText endTime = (EditText) findViewById(R.id.EndTime);
			endTime.setText(new Date().toString());
			_IsStarted = false;
			setButtonState();

			DBHelper helper = new DBHelper(MainActivity.this);
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor data = db.query("zeit", // Tabelle
					new String[] { BaseColumns._ID }, // Spalten
					"IFNULL(end_time,'')=''", // Bedingung
					null, // Argumente für die bedingung
					null, // Grupierung
					null, // Having
					null); // Sortierung
			if (data != null && data.moveToFirst()) {
				// Datensatz gefunden, kann aktualisiert werden
				SQLiteDatabase updateDb = helper.getWritableDatabase();
				long id = data.getLong(0);

				ContentValues values = new ContentValues();
				values.put("end_time", new Date().toString());

				updateDb.update("zeit", values, "_id=?",
						new String[] { String.valueOf(id) });

				updateDb.close();
			}

			// Alles schließen
			if (data != null) {
				data.close();
			}
			db.close();
			helper.close();
		}
	}

	private final class OnStartButtonClicked implements OnClickListener {
		@Override
		public void onClick(View v) {
			// Verhalten beim Klick auf den Strat Button
			EditText startTime = (EditText) findViewById(R.id.StartTime);
			startTime.setText(new Date().toString());
			_IsStarted = true;
			setButtonState();

			// DBHelper helper = new DBHelper(MainActivity.this);
			// SQLiteDatabase db = helper.getWritableDatabase();
			//
			// ContentValues values = new ContentValues();
			// values.put("start_time", new Date().toString());
			//
			// db.insert("zeit", null, values);
			//
			// db.close();

			ContentValues values = new ContentValues();
			values.put(ZeitContracts.Zeit.Columns.START, new Date().toString());

			getContentResolver().insert(ZeitContracts.Zeit.CONTENT_URI, values);
		}

	}
}
