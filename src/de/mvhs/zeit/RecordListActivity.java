/**
 * Activity für die Auflistung aller Zeiteinträge
 */

package de.mvhs.zeit;

import de.mvhs.zeit.db.ZeiterfassungTable;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
	 * Ausgewähle ID
	 */
	private long _SelectedId = -1;
	
	/**
	 * Progress Dialog
	 */
	private ProgressDialog _ExportDialog;
	
	/**
	 * ID des Export-Dialoges
	 */
	private final int _ExportDialogId = 0;
	
	/**
	 * Handler für den Fortschritt
	 */
	final Handler _handler = new Handler(){
		public void handleMessage(Message message)
		{
			int total = message.arg1;
			_ExportDialog.setProgress(total);
			
			if (total >= 100) {
				_ExportDialog.dismiss();
			}
		}
	};
	
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
			_SelectedId = info.id;
			runDelete();
			
			break;
		case R.id.ctx_export:
			showDialog(_ExportDialogId);
			
			break;
		default:
			break;
		}
		
		
		return super.onContextItemSelected(item);
	}
	
	/**
	 * Löschen eines Datensatzes mit vorherigen Bestätigung
	 */
	private void runDelete()
	{
		// Dialog für die Benutzerbestätigung
		AlertDialog.Builder confirmDialog = new AlertDialog.Builder(this);
		// Zusammenstellen des Dialoges
		confirmDialog.setMessage(R.string.dlg_confirm_message)
			.setTitle(R.string.dlg_confirm_title)
			.setPositiveButton(getString(R.string.dlg_yes), new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					_ZT.DeleteRecord(_SelectedId); // Löschen des Datensatzes
					_Cursor.requery(); // Aktualisieren des Cursors
					_Adapter.notifyDataSetChanged(); // Benachrichtigen des Adapters
				}
			})
			.setNegativeButton(getString(R.string.dlg_no), new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					
				}
			});
		
		// Aufrufen des Dialoges
		AlertDialog dialog = confirmDialog.create();
		dialog.show();
	}
	
	/**
	 * Initialisierung des Fortschrittsdialoges
	 */
	protected Dialog onCreateDialog(int id)
	{
		if (id == _ExportDialogId) {
			_ExportDialog = new ProgressDialog(RecordListActivity.this);
			_ExportDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // Fortschritsanzeige
			_ExportDialog.setMessage(getString(R.string.dlg_export_message));
			
			return _ExportDialog;
		}
		else {
			return null;
		}
	}
	
	/**
	 * Vorbereitung zum Export
	 */
	protected void onPrepareDialog(int id, Dialog dialog)
	{
		if (id == _ExportDialogId) {
			Cursor exportData = _ZT.GetClosedRecords();
			
			CsvExport export = new CsvExport(_handler, exportData, "export_data.csv");
			export.start();
		}
	}
}
