/**
 * 
 */
package de.mvhs.android.worktimetracker;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

/**
 * @author kurs
 *
 */
public class RecordList extends ListActivity {

	@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setContentView(R.layout.record_list);
		
		// Initialisierung der Datenbank
		DBHelper dbHelper = new DBHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery(TimeTrackingTable.SQL_LIST_RECORDS, null);
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2,
				cursor,
				new String[]{TimeTrackingTable.START_TIME, TimeTrackingTable.END_TIME},
				new int[]{android.R.id.text1, android.R.id.text2});
		
		setListAdapter(adapter);
	}

}
