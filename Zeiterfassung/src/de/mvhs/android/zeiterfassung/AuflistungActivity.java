package de.mvhs.android.zeiterfassung;

import android.app.ListActivity;
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
				R.layout.row_list, // Layout für die Zeile
				null, // Cursor Daten
				new String[] { ZeitContracts.Zeit.Columns.START,
						ZeitContracts.Zeit.Columns.END }, new int[] {
						R.id.Text1, R.id.Text2 }, // UI-Elemente
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

		registerForContextMenu(getListView());
	}

	@Override
	protected void onStop() {
		unregisterForContextMenu(getListView());
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_export, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();

			break;

		case R.id.mnu_export:
			// Laden der Daten aus der Datenbank
			Cursor exportData = getContentResolver().query(
					ZeitContracts.Zeit.CONTENT_URI, null, null, null, null);

			// Exporter initialisieren
			CsvAsyncTaskExporter exporter = new CsvAsyncTaskExporter();

			// Export starten
			exporter.execute(exportData);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == android.R.id.list) {
			getMenuInflater().inflate(R.menu.list_edit_menu, menu);
		}

		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mnu_edit:
			// Bestimmen des ausgewählten Eintrages
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();

			Intent editIntent = new Intent(this, EditActivity.class);
			// ID an das Intent mit übergeben
			editIntent.putExtra("ID", info.id);
			startActivity(editIntent);

			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}

}
