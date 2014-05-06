package de.mvhs.android.zeiterfassung;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import de.mvhs.android.zeiterfassung.db.ZeitContracts;

public class EditActivity extends Activity {
	public final static String ID_KEY = "ID";
	private long _Id = -1;

	private Date _startDate;
	private Date _endDate;

	private final DateFormat _DateFormatter = DateFormat
			.getDateInstance(DateFormat.SHORT);
	private final DateFormat _TimeFormatter = DateFormat
			.getTimeInstance(DateFormat.SHORT);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_edit);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(true);

		// Auslesen der ID, falls diese übergeben wurde
		if (getIntent().getExtras() != null) {
			_Id = getIntent().getLongExtra(ID_KEY, -1);
		}

		if (_Id > 0) {
			loadData();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		// Registrierung des Events / Listeners
		EditText startDate = (EditText) findViewById(R.id.StartDate);
		startDate.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				if (_startDate == null) {
					_startDate = new Date();
				}

				DatePickerDialog dp = new DatePickerDialog(EditActivity.this,
						new OnDateSelected(), _startDate.getYear() + 1900,
						_startDate.getMonth(), _startDate.getDate());

				dp.show();

				return true;
			}
		});
	}

	@Override
	protected void onStop() {
		EditText startDate = (EditText) findViewById(R.id.StartDate);
		startDate.setOnLongClickListener(null);
		super.onStop();
	}

	private class OnDateSelected implements OnDateSetListener {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			_startDate = new Date(year - 1900, monthOfYear, dayOfMonth,
					_startDate.getHours(), _startDate.getMinutes());

			EditText startDate = (EditText) findViewById(R.id.StartDate);
			startDate.setText(_DateFormatter.format(_startDate));
		}

	}

	private void loadData() {
		Uri dataUri = ContentUris.withAppendedId(
				ZeitContracts.Zeit.CONTENT_URI, _Id);
		Cursor data = getContentResolver().query(dataUri, null, null, null,
				null);

		if (data != null && data.moveToFirst()) {
			String startValue = data.getString(data
					.getColumnIndex(ZeitContracts.Zeit.Columns.START));

			EditText startDate = (EditText) findViewById(R.id.StartDate);
			EditText startTime = (EditText) findViewById(R.id.StartTime);
			EditText endDate = (EditText) findViewById(R.id.EndDate);
			EditText endTime = (EditText) findViewById(R.id.EndTime);

			// Konvertieren der Startzeit
			try {
				_startDate = ZeitContracts.Converters.DB_FORMATTER
						.parse(startValue);
				startDate.setText(_DateFormatter.format(_startDate));
				startTime.setText(_TimeFormatter.format(_startDate));

			} catch (ParseException e) {
				startDate.setText("");
				startTime.setText("");
			}

			// Prüfen, ob die Endzeit eingertagen ist
			if (!data.isNull(data
					.getColumnIndex(ZeitContracts.Zeit.Columns.END))) {
				String endValue = data.getString(data
						.getColumnIndex(ZeitContracts.Zeit.Columns.END));

				try {
					_endDate = ZeitContracts.Converters.DB_FORMATTER
							.parse(endValue);
					endDate.setText(_DateFormatter.format(_endDate));
					endTime.setText(_TimeFormatter.format(_endDate));
				} catch (ParseException e) {
					endDate.setText("");
					endTime.setText("");
				}
			} else {
				endDate.setText("");
				endTime.setText("");
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
		case R.id.mnu_cancel:
			this.finish();
			break;

		case R.id.mnu_delete:
			if (_Id > 0) {
				delete();
			}
			// this.finish();
			break;

		case R.id.mnu_save:
			// TO DO
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void delete() {

		// Aufbau eines Dialoges
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Löschen ...") // Titel des Dialoges setzen
				.setMessage("Wollen Sie den Datensatz wirklich löschen?") // Nachricht
																			// für
																			// den
																			// Benutzer
				.setIcon(R.drawable.ic_menu_delete) // Icopn für das Dialog
				.setPositiveButton("Löschen", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Uri deleteUri = ContentUris.withAppendedId(
								ZeitContracts.Zeit.CONTENT_URI, _Id);
						getContentResolver().delete(deleteUri, null, null);

						EditActivity.this.finish();
					}
				}) // Button für die positive
					// Antwort
				.setNegativeButton("Abbrechen", null); // Button zum Abbrechen
														// der Aktion

		// Dialog anzeigen
		builder.create().show();
	}
}
