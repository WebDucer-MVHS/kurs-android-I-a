/**
 * 
 */
package de.mvhs.android.worktimetracker;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;

/**
 * @author kurs
 *
 */
public class RecordList extends ListActivity {
	/**
	 * Klassenvariablen
	 */
	private int _Position = 1;
	
	/**
	 * On Create
	 */
	@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setContentView(R.layout.record_list);
		
		LoadData();
	}
	
	/**
	 * Kontext Menü einbinden
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.context_menu, menu);
		
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	/**
	 * Auswerten des Kontext Menüs
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterView.AdapterContextMenuInfo info =
				(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		long id = info.id;
		_Position = info.position;
		
		switch (item.getItemId()) {
		case R.id.ctx_delete:
			DBHelper dbHelper = new DBHelper(this);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.delete(
				TimeTrackingTable.TABLE_NAME,
				TimeTrackingTable.ID + "=?",
				new String[]{String.valueOf(id)});
			db.close();
			
			LoadData();
			break;
			
		case R.id.ctx_edit:
			Intent intentEdit = new Intent(this, EditRecord.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentEdit.putExtra("id", id);
			startActivity(intentEdit);
			
			break;

		default:
			break;
		}
		
		return super.onContextItemSelected(item);
	}
	
	/**
	 * Laden der Daten
	 */
	private void LoadData(){
		// Initialisierung der Datenbank
		DBHelper dbHelper = new DBHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery(TimeTrackingTable.SQL_LIST_RECORDS, null);
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2,
				cursor,
				new String[]{TimeTrackingTable.START_TIME, TimeTrackingTable.END_TIME},
				new int[]{android.R.id.text1, android.R.id.text2});
		
		startManagingCursor(cursor);
		
		setListAdapter(adapter);
		
		if (_Position <= cursor.getCount()) {
			setSelection(_Position);
		}
		
		registerForContextMenu(findViewById(android.R.id.list));
	}
}
