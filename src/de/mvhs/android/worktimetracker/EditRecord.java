/**
 * 
 */
package de.mvhs.android.worktimetracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.TabActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.Time;
import android.widget.DatePicker;
import android.widget.TabHost;
import android.widget.TimePicker;

/**
 * @author kurs
 *
 */
public class EditRecord extends TabActivity {
	/**
	 * Klassenvariblen
	 */
	private TabHost _TabHost;
	private long _ID = -1;
	/**
	 * On Create
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.edit_record);
		
		_TabHost = getTabHost();
		_TabHost.addTab(
			_TabHost.newTabSpec("tab_start")
			.setIndicator(getString(R.string.tab_start))
			.setContent(R.id.tab1));
		_TabHost.addTab(
				_TabHost.newTabSpec("tab_end")
				.setIndicator(getString(R.string.tab_end))
				.setContent(R.id.tab2));
		_TabHost.setCurrentTab(0);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			_ID = extras.getLong("id");
		}
		
		LoadData();
		
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * Daten laden
	 */
	private void LoadData(){
		// Initialisierung der Oberflächenelemente
		DatePicker dpStart = (DatePicker)findViewById(R.id.startDate);
		DatePicker dpEnd = (DatePicker)findViewById(R.id.endDate);
		TimePicker tpStart = (TimePicker)findViewById(R.id.startTime);
		TimePicker tpEnd = (TimePicker)findViewById(R.id.endTime);
		
		// Laden des Datensatzes
		DBHelper dbHelper = new DBHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
			TimeTrackingTable.SQL_SELECT_BY_ID,
			new String[]{String.valueOf(_ID)});
		
		// Füllen der Elemente
		if (cursor.moveToNext() == true) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			try {
				Date startDate = sdf.parse(cursor.getString(cursor.getColumnIndex(TimeTrackingTable.START_TIME)));
				Date endDate = sdf.parse(cursor.getString(cursor.getColumnIndex(TimeTrackingTable.END_TIME)));
				
				dpStart.init(startDate.getYear() + 1900, startDate.getMonth(), startDate.getDate(), null);
				dpEnd.init(endDate.getYear() + 1900, endDate.getMonth(), endDate.getDate(), null);
				tpStart.setCurrentHour(startDate.getHours());
				tpStart.setCurrentMinute(startDate.getMinutes());
				tpEnd.setCurrentHour(endDate.getHours());
				tpEnd.setCurrentMinute(endDate.getMinutes());
			} catch (ParseException e) {
				//
			}
			
		}
		
		// Schließen der DB
		db.close();
	}
	
	/**
	 * Beenden der Activity
	 */
	@Override
	protected void onDestroy() {
		// Auslesen der eingegebenen Daten
		DatePicker dpStart = (DatePicker)findViewById(R.id.startDate);
		DatePicker dpEnd = (DatePicker)findViewById(R.id.endDate);
		TimePicker tpStart = (TimePicker)findViewById(R.id.startTime);
		TimePicker tpEnd = (TimePicker)findViewById(R.id.endTime);
		Date startDate = new Date(
			dpStart.getYear() - 1900,
			dpStart.getMonth(),
			dpStart.getDayOfMonth(),
			tpStart.getCurrentHour(),
			tpStart.getCurrentMinute());
		Date endDate = new Date(
			dpEnd.getYear() - 1900,
			dpEnd.getMonth(),
			dpEnd.getDayOfMonth(),
			tpEnd.getCurrentHour(),
			tpEnd.getCurrentMinute());
		
		// Speichern der neuen Daten in der DB
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DBHelper dbHelper = new DBHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(TimeTrackingTable.START_TIME, sdf.format(startDate));
		values.put(TimeTrackingTable.END_TIME, sdf.format(endDate));
		
		db.update(
			TimeTrackingTable.TABLE_NAME,
			values,
			TimeTrackingTable.ID + "=?",
			new String[]{String.valueOf(_ID)});
		
		// DB schließen
		db.close();
		
		super.onDestroy();
	}
}
