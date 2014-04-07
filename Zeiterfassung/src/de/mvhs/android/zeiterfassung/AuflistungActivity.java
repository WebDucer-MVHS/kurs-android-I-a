package de.mvhs.android.zeiterfassung;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import de.mvhs.android.zeiterfassung.db.ZeitContracts;

public class AuflistungActivity extends ListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_list);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(true);
	}

	@Override
	protected void onStart() {
		super.onStart();

		// Laden der Daten
		// -- Einen Adapter initialisieren
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, // Context
				android.R.layout.simple_list_item_2, // Layout für die Zeile
				null, // Cursor Daten
				new String[] { ZeitContracts.Zeit.Columns.START,
						ZeitContracts.Zeit.Columns.END }, new int[] {
						android.R.id.text1, android.R.id.text2 }, // UI-Elemente
																	// in denen
																	// diese
																	// Werte
																	// dargestellt
																	// werden
																	// sollen
				0);

		// Laden der Daten
		Cursor data = getContentResolver().query(
				ZeitContracts.Zeit.CONTENT_URI, null, null, null, null);

		// Zuordnung des Adapters zur Liste
		getListView().setAdapter(adapter);

		// Daten an Adapter übergeben
		adapter.swapCursor(data);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();

			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
