package de.mvhs.android.arbeitszeiterfassung;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import de.mvhs.android.arbeitszeiterfassung.db.ZeitabschnittContract;
import de.mvhs.android.arbeitszeiterfassung.db.ZeitabschnittContract.Zeitabschnitte;

public class EditActivity extends Activity {
	// Variablen
	private long _Id = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_edit);

		// Extras auslesen
		if (getIntent().getExtras() != null) {
			if (getIntent().getExtras().containsKey("ID")) {
				_Id = getIntent().getLongExtra("ID", -1);
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		// Prüfen, ob Daten da sind
		if (_Id > 0) {
			Uri uri = ContentUris.withAppendedId(
					ZeitabschnittContract.Zeitabschnitte.CONTENT_URI, _Id);

			Cursor data = getContentResolver().query(uri, null, null, null,
					null);

			if (data != null && data.moveToFirst()) {
				// UI Elemente initialisieren
				EditText startTime = (EditText) findViewById(R.id.txt_start_time);
				EditText endTime = (EditText) findViewById(R.id.txt_end_time);
				EditText pause = (EditText) findViewById(R.id.txt_pause);
				EditText comment = (EditText) findViewById(R.id.txt_comment);

				// Daten ausgeben
				// -- Startzeit
				String startString = data.getString(data
						.getColumnIndex(Zeitabschnitte.Columns.START));
				startTime.setText(startString);

				// -- Endzeit
				if (!data.isNull(data
						.getColumnIndex(Zeitabschnitte.Columns.STOP))) {
					String endString = data.getString(data
							.getColumnIndex(Zeitabschnitte.Columns.STOP));
					endTime.setText(endString);
				} else {
					endTime.setText("");
				}

				// -- Pause
				long pauseInt = data.getLong(data
						.getColumnIndex(Zeitabschnitte.Columns.PAUSE));
				pause.setText(String.valueOf(pauseInt));

				// -- Kommentar
				if (!data.isNull(data
						.getColumnIndex(Zeitabschnitte.Columns.COMMENT))) {
					String commentString = data.getString(data
							.getColumnIndex(Zeitabschnitte.Columns.COMMENT));
					comment.setText(commentString);
				} else {
					comment.setText("");
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.mnu_edit, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mnu_delete:
			deleteData();
			break;

		case R.id.mnu_cancel:
			this.finish();

		case R.id.mnu_save:
			saveData();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void saveData() {
		// UI Elemente initialisieren
		EditText startTime = (EditText) findViewById(R.id.txt_start_time);
		EditText endTime = (EditText) findViewById(R.id.txt_end_time);
		EditText pause = (EditText) findViewById(R.id.txt_pause);
		EditText comment = (EditText) findViewById(R.id.txt_comment);

		// Werte auslesen
		String startString = startTime.getText().toString();
		String endString = endTime.getText().toString();
		String commentString = comment.getText().toString();

		long pauseInt = 0;
		String pauseString = pause.getText().toString();
		try {
			pauseInt = Long.parseLong(pauseString);
		} catch (NumberFormatException e) {
			// Konvertierung fehlgeschlagen
		}

		ContentValues values = new ContentValues();
		values.put(Zeitabschnitte.Columns.START, startString);
		values.put(Zeitabschnitte.Columns.STOP, endString);
		values.put(Zeitabschnitte.Columns.PAUSE, pauseInt);
		values.put(Zeitabschnitte.Columns.COMMENT, commentString);

		// Daten speichern
		Uri uri = ContentUris.withAppendedId(Zeitabschnitte.CONTENT_URI, _Id);
		getContentResolver().update(uri, values, null, null);

		// Bearbeitung abschließen
		this.finish();
	}

	private void deleteData() {
		AlertDialog.Builder delDialog = new AlertDialog.Builder(this);
		delDialog
				.setTitle(R.string.dlg_delete_title)
				.setMessage(R.string.dlg_delete_message)
				.setPositiveButton(R.string.dlg_delte_delete,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// Löschen des Datensatzes
								Uri uri = ContentUris
										.withAppendedId(
												ZeitabschnittContract.Zeitabschnitte.CONTENT_URI,
												_Id);

								getContentResolver().delete(uri, null, null);

								EditActivity.this.finish();
							}
						})
				.setNegativeButton(R.string.dlg_delete_cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create().show();
	}
}
