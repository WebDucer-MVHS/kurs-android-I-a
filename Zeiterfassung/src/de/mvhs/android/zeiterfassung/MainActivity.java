package de.mvhs.android.zeiterfassung;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import de.mvhs.android.zeiterfassung.db.Contract;
import de.mvhs.android.zeiterfassung.db.Contract.Zeiten;

public class MainActivity extends Activity {

	private Button _startButton = null;
	private Button _endButton = null;
	private EditText _startTime = null;
	private EditText _endTime = null;

	// Listener
	private View.OnClickListener _onStartClicked = new OnStartButtonClicked();
	private View.OnClickListener _onEndClicked = new OnEndButtonClicked();

	// Columns
	private final static String[] _START_TIME_COLUMN = { Zeiten.Columns.START_TIME };

	// Formatter
	private final static DateFormat _UI_DATE_TIME_FORMATTER = DateFormat
			.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initialisierung der UI-Elemente nach Einbinden des Layouts
		_startButton = (Button) findViewById(R.id.StartCommand);
		_endButton = (Button) findViewById(R.id.EndCommand);
		_startTime = (EditText) findViewById(R.id.StartTime);
		_endTime = (EditText) findViewById(R.id.EndTime);

		// Bearbeitung der Textbob verhindern
		_startTime.setKeyListener(null);
		_endTime.setKeyListener(null);
	}

	@Override
	protected void onStart() {
		super.onStart();

		// Laden des eventuell offenen Datensatzes
		Cursor data = getContentResolver().query(Contract.Zeiten.NOT_ENDED_URI,
				_START_TIME_COLUMN, null, null, null);

		// Ein Datensatz ist vorhanden
		if (data != null && data.moveToFirst()) {
			// Auslesen der Startzeit
			String startTimeValue = data.getString(0);

			// Parsen der Startzeit
			Date startTimeDate = null;
			try {
				startTimeDate = Contract.Converters.DB_DATE_TIME_FORMATTER
						.parse(startTimeValue);

				// Ausgabe der Startzeit
				_startTime.setText(_UI_DATE_TIME_FORMATTER
						.format(startTimeDate));
				_endTime.setText(R.string.TextPlaceHolder);
			} catch (ParseException ex) {
				// Fehlerausgabe
				Toast.makeText(this, R.string.WrongStartTimeFormat,
						Toast.LENGTH_LONG).show();
			}

			_startButton.setEnabled(false);
			_endButton.setEnabled(true);
		}
		// Kein Datensatz vorhanden
		else {
			_startTime.setText(R.string.TextPlaceHolder);
			_endTime.setText(R.string.TextPlaceHolder);
			_startButton.setEnabled(true);
			_endButton.setEnabled(false);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Registrieren der Listener
		_startButton.setOnClickListener(_onStartClicked);
		_endButton.setOnClickListener(_onEndClicked);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Deregistrieren der Listener
		_startButton.setOnClickListener(null);
		_endButton.setOnClickListener(null);
	}

	private class OnStartButtonClicked implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			Calendar jetzt = Calendar.getInstance();

			ContentValues values = new ContentValues();
			values.put(Zeiten.Columns.START_TIME,
					Contract.Converters.DB_DATE_TIME_FORMATTER.format(jetzt
							.getTime()));

			getContentResolver().insert(Zeiten.CONTENT_URI, values);

			_startTime.setText(_UI_DATE_TIME_FORMATTER.format(jetzt.getTime()));
			_endTime.setText(R.string.TextPlaceHolder);

			_startButton.setEnabled(false);
			_endButton.setEnabled(true);
		}

	}

	private class OnEndButtonClicked implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			Calendar jetzt = Calendar.getInstance();

			ContentValues values = new ContentValues();
			values.put(Zeiten.Columns.END_TIME,
					Contract.Converters.DB_DATE_TIME_FORMATTER.format(jetzt
							.getTime()));

			getContentResolver().update(Zeiten.NOT_ENDED_URI, values, null,
					null);

			_endTime.setText(_UI_DATE_TIME_FORMATTER.format(jetzt.getTime()));

			_startButton.setEnabled(true);
			_endButton.setEnabled(false);
		}

	}
}
