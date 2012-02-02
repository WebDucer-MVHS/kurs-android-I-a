/**
 * Activity für die Auflistung aller Zeiteinträge
 */

package de.mvhs.zeit;

import de.mvhs.zeit.db.ZeiterfassungTable;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;

public class RecordListActivity extends ListActivity {
	//
	// Klassenvariablen
	//
	/**
	 * Verbindung zur Hilfsklasse der Tabelle in der Datenbank
	 */
	private ZeiterfassungTable _ZT = new ZeiterfassungTable(this);
	/**
	 * Verwendeter Cursor für das Füllen der Liste
	 */
	private Cursor _Cursor;
	/**
	 * Verwendeter Adapter für die Verwaltung der Liste
	 */
	private SimpleCursorAdapter _Adapter;
	
	/**
	 * Initialisierung der Activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.list_activity);
	}
	
	/**
	 * Wird aufgeruffen, wenn die Activity in den Vordergrund kommt
	 */
	@Override
	protected void onStart() {
		super.onStart();
		
		Init();
	}
	
	/**
	 * Initialisierung der Angezeigten Daten
	 */
	private void Init()
	{
		if (_Cursor == null || _Adapter == null)
		{
			_Cursor = _ZT.GetClosedRecords();
			
			_Adapter = new SimpleCursorAdapter(
				this, // Kontext
				R.layout.list_item, // Layout für einzelne Zeile
				_Cursor, // Cursor auf die darzustellende Daten
				new String[]{ // Spalten, die angezeigt werden sollen
					ZeiterfassungTable.COLUMN_START,
					ZeiterfassungTable.COLUMN_END},
				new int[]{ // in welchen Views diese Spalten angezeigt werden
					R.id.item_start,
					R.id.item_end});
			
			this.startManagingCursor(_Cursor);
			this.setListAdapter(_Adapter);
			
			registerForContextMenu(findViewById(android.R.id.list));
		}
		else
		{
			_Cursor.requery(); // Aktualisieren des Cursors
			_Adapter.notifyDataSetChanged(); // Benachrichtigen des Adapters
		}
	}
	
	/**
	 * Initialisierung des Kontextmenüs (bei jedem Aufruf)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == android.R.id.list) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.record_list_context, menu);
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	/**
	 * Reagieren auf Kontextmenü
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info =
			(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		
		switch (item.getItemId()) {
		case R.id.ctx_edit:
			Intent intentEdit = new Intent(this, EditRecordActivity.class); // Initialisierung des Intents
			intentEdit.putExtra(EditRecordActivity.ID, info.id); // Übergabe von Parametern an den Intent
			startActivity(intentEdit); // Aufruf der neuen Activity
			
			break;
		case R.id.ctx_delete:
			_ZT.DeleteRecord(info.id); // Löschen des Datensatzes
			_Cursor.requery(); // Aktualisieren des Cursors
			_Adapter.notifyDataSetChanged(); // Benachrichtigen des Adapters
			
			break;

		default:
			break;
		}
		
		
		return super.onContextItemSelected(item);
	}
}
