package de.mvhs.android.zeiterfassung;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
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

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}
}
