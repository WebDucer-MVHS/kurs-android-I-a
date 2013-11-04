package de.mvhs.android.zeiterfassung;

import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import de.mvhs.android.zeiterfassung.db.DBHelper;
import de.mvhs.android.zeiterfassung.db.ZeitProvider;

public class MainActivity extends Activity {
	private boolean _IsStarted = false;
	private final static String _IS_STARTED_KEY = "IsStarted";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Suchen der Elemente in eingebundenen Layout
		Button starten = (Button) findViewById(R.id.button_start);
		Button beenden = (Button) findViewById(R.id.button_end);

		// Alternative
		starten.setOnClickListener(onClicked);
		beenden.setOnClickListener(onClicked);

		// Aktivieren / Deaktivieren
		_IsStarted = savedInstanceState == null ? false : savedInstanceState
				.getBoolean(_IS_STARTED_KEY, false);
		changeButtonState();

		DBHelper helper = new DBHelper(this);
		helper.getReadableDatabase();
		helper.close();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(_IS_STARTED_KEY, _IsStarted);
		super.onSaveInstanceState(outState);
	}

	private void changeButtonState() {
		// Buttons suchen
		Button starten = (Button) findViewById(R.id.button_start);
		Button beenden = (Button) findViewById(R.id.button_end);

		// Aktivieren / Deaktivieren
		if (_IsStarted) {
			starten.setEnabled(false);
			beenden.setEnabled(true);
		} else {
			beenden.setEnabled(false);
			starten.setEnabled(true);
		}
	}

	// Alternative
	private OnClickListener onClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// Aktuelle Uhrzeit bestimmen
			Date jetzt = new Date();

			// Textbox
			EditText ausgabe = null;

			if (v.getId() == R.id.button_start) {
				ausgabe = (EditText) findViewById(R.id.text_start_time);
				_IsStarted = true;
				// Daten in die Datenbank spiechern
				ContentValues values = new ContentValues();
				values.put(ZeitProvider.Columns.START,
						ZeitProvider.DB_DATE_FORMAT.format(jetzt));

				Uri insert = getContentResolver().insert(
						ZeitProvider.CONTENT_URI, values);

			} else if (v.getId() == R.id.button_end) {
				ausgabe = (EditText) findViewById(R.id.text_end_time);
				_IsStarted = false;

				Intent editIntent = new Intent(MainActivity.this,
						EditActivity.class);
				startActivity(editIntent);
			}

			ausgabe.setText(jetzt.toString());
			changeButtonState();
		}
	};
}
