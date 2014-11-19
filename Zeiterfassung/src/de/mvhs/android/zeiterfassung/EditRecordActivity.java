package de.mvhs.android.zeiterfassung;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import de.mvhs.android.zeiterfassung.db.Contract.Converters;
import de.mvhs.android.zeiterfassung.db.Contract.Zeiten;

public class EditRecordActivity extends Activity {
	private long _id = -1l;

	// Formatter
	private final static DateFormat _UI_DATE_FORMATTER = DateFormat
			.getDateInstance(DateFormat.MEDIUM);
	private final static DateFormat _UI_TIME_FORMATTER = DateFormat
			.getTimeInstance(DateFormat.SHORT);

	// UI Elemente
	private EditText _startDate = null;
	private EditText _startTime = null;
	private EditText _endDate = null;
	private EditText _endTime = null;
	private EditText _pause = null;
	private EditText _comment = null;

	// Datumsfelder
	private Calendar _startDateTime = null;
	private Calendar _endDateTime = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_edit);

		// Finde der UI Elemente
		_startDate = (EditText) findViewById(R.id.StartDate);
		_startTime = (EditText) findViewById(R.id.StartTime);
		_endDate = (EditText) findViewById(R.id.EndDate);
		_endTime = (EditText) findViewById(R.id.EndTime);
		_pause = (EditText) findViewById(R.id.PauseDuration);
		_comment = (EditText) findViewById(R.id.CommentText);

		// Deaktivieren manueller Änderungen
		_startDate.setKeyListener(null);
		_startTime.setKeyListener(null);
		_endDate.setKeyListener(null);
		_endTime.setKeyListener(null);

		// Auslesen der ID aus den Extras
		if (getIntent().getExtras() != null) {
			_id = getIntent().getLongExtra("ID", -1);
		}

		if (_id == -1) {
			_startDateTime = Calendar.getInstance();
			_endDateTime = Calendar.getInstance();
		}

		Toast.makeText(this, "ID: " + _id, Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onStart() {
		if (_id > 0) {
			// Daten laden
			Uri uri = ContentUris.withAppendedId(Zeiten.CONTENT_URI, _id);
			Cursor data = getContentResolver().query(uri, null, null, null,
					null);

			if (data.moveToFirst()) {
				try {
					Date startTime = Converters.DB_DATE_TIME_FORMATTER
							.parse(data.getString(data
									.getColumnIndex(Zeiten.Columns.START_TIME)));
					_startDateTime = Calendar.getInstance();
					_startDateTime.setTime(startTime);

					// Ausgabe der Startzeit
					_startDate.setText(_UI_DATE_FORMATTER.format(startTime));
					_startTime.setText(_UI_TIME_FORMATTER.format(startTime));

					Date endTime = null;
					if (!data.isNull(data
							.getColumnIndex(Zeiten.Columns.END_TIME))) {
						endTime = Converters.DB_DATE_TIME_FORMATTER
								.parse(data.getString(data
										.getColumnIndex(Zeiten.Columns.END_TIME)));
						_endDateTime = Calendar.getInstance();
						_endDateTime.setTime(endTime);

						// Ausgabe der Endzeit
						_endDate.setText(_UI_DATE_FORMATTER.format(endTime));
						_endTime.setText(_UI_TIME_FORMATTER.format(endTime));
					}
				} catch (ParseException e) {
				}
			}
		}

		// Einbinden der Date und Time Dialoge
		_startDate.setOnLongClickListener(new OnStartDateClicked());
		_startTime.setOnLongClickListener(new OnStartTimeClicked());

		super.onStart();
	}

	private class OnStartDateClicked implements OnLongClickListener {

		@Override
		public boolean onLongClick(View arg0) {
			if (_startDateTime == null) {
				_startDateTime = Calendar.getInstance();
			}

			// Initialisierung des Dialoges
			DatePickerDialog dp = new DatePickerDialog(EditRecordActivity.this,
					new OnStartDateSelected(),
					_startDateTime.get(Calendar.YEAR),
					_startDateTime.get(Calendar.MONTH),
					_startDateTime.get(Calendar.DAY_OF_MONTH));

			dp.show();

			return true;
		}

	}

	private class OnStartDateSelected implements OnDateSetListener {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			_startDateTime.set(year, monthOfYear, dayOfMonth);

			_startDate.setText(_UI_DATE_FORMATTER.format(_startDateTime
					.getTime()));
		}

	}

	private class OnStartTimeClicked implements OnLongClickListener {

		@Override
		public boolean onLongClick(View v) {
			if (_startDateTime == null) {
				_startDateTime = Calendar.getInstance();
			}

			boolean is24 = android.text.format.DateFormat
					.is24HourFormat(EditRecordActivity.this);

			TimePickerDialog tp = new TimePickerDialog(EditRecordActivity.this,
					new OnStartTimeSelected(),
					_startDateTime.get(Calendar.HOUR_OF_DAY),
					_startDateTime.get(Calendar.MINUTE), is24);

			tp.show();

			return true;
		}

	}

	private class OnStartTimeSelected implements OnTimeSetListener {

		@Override
		public void onTimeSet(TimePicker dialog, int hoursOfDay, int minutes) {
			_startDateTime.set(Calendar.HOUR_OF_DAY, hoursOfDay);
			_startDateTime.set(Calendar.MINUTE, minutes);

			_startTime.setText(_UI_TIME_FORMATTER.format(_startDateTime
					.getTime()));
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
}
