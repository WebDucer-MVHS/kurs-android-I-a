package de.mvhs.android.zeiterfassung;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;
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
					// Ausgabe der Startzeit
					_startDate.setText(_UI_DATE_FORMATTER.format(startTime));
					_startTime.setText(_UI_TIME_FORMATTER.format(startTime));

					Date endTime = null;
					if (!data.isNull(data
							.getColumnIndex(Zeiten.Columns.END_TIME))) {
						endTime = Converters.DB_DATE_TIME_FORMATTER
								.parse(data.getString(data
										.getColumnIndex(Zeiten.Columns.END_TIME)));
						// Ausgabe der Endzeit
						_endDate.setText(_UI_DATE_FORMATTER.format(endTime));
						_endTime.setText(_UI_TIME_FORMATTER.format(endTime));
					}
				} catch (ParseException e) {
				}
			}
		}

		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
}
