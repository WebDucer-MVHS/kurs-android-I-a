package de.mvhs.android.zeiterfassung;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.SimpleCursorAdapter;
import de.mvhs.android.zeiterfassung.db.Contract.Zeiten;

public class RecordListActivity extends ListActivity {
	private final static String _NOT_EMPTY = "IFNULL("
			+ Zeiten.Columns.END_TIME + ",'')<>''";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_list);

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, // Context
				R.layout.list_row_two_values, // layout für die Zeile in
												// der Liste
				null, // Daten => Cursor
				new String[] { Zeiten.Columns.START_TIME,
						Zeiten.Columns.END_TIME }, // Welche Spalten?
				new int[] { android.R.id.text1, android.R.id.text2 }, // In
																		// welchen
																		// Views?
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER); // Flags

		getListView().setAdapter(adapter);

		Cursor data = getContentResolver().query(Zeiten.CONTENT_URI, null,
				_NOT_EMPTY, null, Zeiten.Columns.START_TIME + " DESC");

		adapter.swapCursor(data);

		getActionBar().setDisplayShowHomeEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onStart() {

		registerForContextMenu(getListView());

		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.list_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onStop() {

		unregisterForContextMenu(getListView());

		super.onStop();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		switch (v.getId()) {
		case android.R.id.list:
			getMenuInflater().inflate(R.menu.list_context, menu);
			break;
		}

		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_edit:
			Intent editIntent = new Intent(this, EditRecordActivity.class);

			// Auslesen der ID
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();

			editIntent.putExtra("ID", info.id);

			startActivity(editIntent);
			break;

		default:
			break;
		}

		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// Möglichkeit 1
			// Intent homeIntent = new Intent(this, MainActivity.class);
			//
			// startActivity(homeIntent);

			// Möglichkeit 2
			this.finish();

			break;

		case R.id.action_export:
			// Eportieren der Daten
			csvExport();

			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void csvExport() {
		ProgressDialog dialog = new ProgressDialog(this);
		final CsvExporter exporter = new CsvExporter(this, dialog);

		dialog.setTitle(R.string.ExportTitle);
		dialog.setMessage(getString(R.string.ExportMessage));
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setButton(ProgressDialog.BUTTON_NEGATIVE,
				getString(R.string.CancelButtonText),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						exporter.cancel(false);
					}
				});

		exporter.execute();
	}
}
