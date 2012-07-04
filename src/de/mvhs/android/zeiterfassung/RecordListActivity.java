package de.mvhs.android.zeiterfassung;

import de.mvhs.android.zeiterfassung.db.DBHelper;
import de.mvhs.android.zeiterfassung.db.WorktimeTable;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;

public class RecordListActivity extends ListActivity {
	
	private DBHelper _Helper;
	private SQLiteDatabase _DB;
	private Cursor _Cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_list);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent homeIntent = new Intent(this, MainActivity.class);
			this.startActivity(homeIntent);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		registerForContextMenu(getListView());
		
		_Helper = new DBHelper(this); // Initialisierung des Helpers
		_DB = _Helper.getReadableDatabase(); // Initialisierung der Datenbank
		
		// Laden der Daten
		loadData();
	}
	
	private void loadData(){
		// Daten laden
		_Cursor = _DB.query(
			WorktimeTable.TABLE_NAME, // Tabelle
			null, // Spalten => null = Alle Spalten
			WorktimeTable.COLUMN_END_TIME + " IS NOT NULL AND " + WorktimeTable.COLUMN_END_TIME + " <>''", // Where Bedingung
			null, // Parameter
			null, // GroupBy
			null, // Having
			WorktimeTable.COLUMN_START_TIME + " DESC"); // Sortierung
			
		// Adapter initialisieren
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
			this, // Context
			R.layout.row_2_cols, // layout für die Darstellung einer Zeile
			_Cursor, // Daten, die dargestellt werden sollen
			new String[]{ // Spalten, die darhestellet werden sollen
				WorktimeTable.COLUMN_START_TIME,
				WorktimeTable.COLUMN_END_TIME},
			new int[]{ // Wo sollen diese Spalten angezeigt werden
				R.id.text_1,
				R.id.text_2},
				0); // Flag
		
		// Adapter zuweisen
		setListAdapter(adapter);
	}
	
	@Override
	protected void onStop() {
		// Datenbank sauber schließen
		setListAdapter(null);
		unregisterForContextMenu(getListView());
		
		if (_Cursor != null) {
			_Cursor.close();
		}
		if (_DB != null) {
			_DB.close();
		}
		if (_Helper != null) {
			_Helper.close();
		}
		super.onStop();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		
		if (v.getId() == android.R.id.list) {
			this.getMenuInflater().inflate(R.menu.edit_delete, menu);
		}
		
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		final AdapterView.AdapterContextMenuInfo info =
				(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		
		switch (item.getItemId()) {
		case R.id.ctx_delete:
			// Abfrage, ob wirklich gelöscht werden soll
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			
			builder.setTitle(R.string.dlg_confirm_title)
				.setMessage(R.string.dlg_confirm_message)
				.setIcon(R.drawable.ic_menu_delete)
				.setNegativeButton(R.string.dlg_cancel,
					new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})
				.setPositiveButton(R.string.dlg_delete,
					new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						WorktimeTable table = new WorktimeTable(RecordListActivity.this);
						table.deleteWorktime(info.id);
						
						loadData();
					}
				});
			
			builder.create().show();
			
			
			
			break;
		case R.id.ctx_edit:
			Intent editIntent = new Intent(this, RecordEditActivity.class);
			editIntent.putExtra(RecordEditActivity.ID_KEY, info.id);
			// Übergeben der ID an die neue Activity
			startActivity(editIntent);
			
			break;
			
		case R.id.ctx_export:
			CSVExporter export = new CSVExporter("export");
			WorktimeTable table = new WorktimeTable(this);
			
			// Daten aus der DB lesen
			Cursor data = table.getExportList();
			
			// Export der Daten
			export.execute(new Cursor[]{data});
			
			break;
		default:
			break;
		}
		
		return super.onContextItemSelected(item);
	}
}
