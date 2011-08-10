package de.mvhs.android.worktimetracker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MVHSZeiterfassungActivity extends Activity {
	
	/*
	 * Klassenvariablen
	 */
	private DateFormat _dfDate = DateFormat.getDateInstance(DateFormat.SHORT);
	private DateFormat _dfTime = DateFormat.getTimeInstance(DateFormat.MEDIUM);
	
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Initialisieren der Formularelemente
        Button btnStart = (Button)findViewById(R.id.btnStart);
        Button btnEnd = (Button)findViewById(R.id.btnEnd);
        EditText txtStartTime = (EditText)findViewById(R.id.txtStartTime);
        EditText txtEndTime = (EditText)findViewById(R.id.txtEndTime);
        
        // Formularelemente manipulieren
        btnStart.setEnabled(true);
        btnEnd.setEnabled(false);
        
        txtStartTime.setEnabled(false);
        txtEndTime.setEnabled(false);
        
        // DB Initialisieren
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // Auf letzten Eintrag pr�fen
        Cursor cursor =
			db.rawQuery(TimeTrackingTable.SQL_LAST_UNCOMPLETED, null);
		if (cursor.moveToNext() == true){
			String strStartTime = cursor.getString(
				cursor.getColumnIndex(TimeTrackingTable.START_TIME));
			try{
				Date dtmStartTime = sdFormat.parse(strStartTime);
				
				txtStartTime.setText(_dfDate.format(dtmStartTime) + " " + _dfTime.format(dtmStartTime));
				btnStart.setEnabled(false);
		        btnEnd.setEnabled(true);
			}
			catch (Exception ex){
				//
			}
		}
        
    }
    
    /**
     * OnClick Event f�r Buttons
     */
    public void onButtonClick(final View vButton){
    		Date dtmNowTime = new Date();
    		Button oButton = (Button)vButton;
    		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
    		
    		switch (oButton.getId()) {
			case R.id.btnStart:
				EditText txtStart = (EditText)findViewById(R.id.txtStartTime);
				txtStart.setText(_dfDate.format(dtmNowTime) + " " + _dfTime.format(dtmNowTime));
				oButton.setEnabled(false);
				Button btnEnd = (Button)findViewById(R.id.btnEnd);
				btnEnd.setEnabled(true);
				
				// DB Platzhalter f�llen
				SQLiteStatement stInsert =
						db.compileStatement(TimeTrackingTable.SQL_INSERT_START_DATE);
				stInsert.bindString(1, sdFormat.format(dtmNowTime));
				stInsert.executeInsert();
				
				break;

			case R.id.btnEnd:
				EditText txtEnd = (EditText)findViewById(R.id.txtEndTime);
				txtEnd.setText(_dfDate.format(dtmNowTime) + " " + _dfTime.format(dtmNowTime));
				oButton.setEnabled(false);
				Button btnStart = (Button)findViewById(R.id.btnStart);
				btnStart.setEnabled(true);
				
				Cursor cursor =
					db.rawQuery(TimeTrackingTable.SQL_LAST_UNCOMPLETED, null);
				if (cursor.moveToNext() == true){
					int id = cursor.getInt(
						cursor.getColumnIndex(TimeTrackingTable.ID));
					
					// Zu aktulisierende Werte f�llen
					ContentValues updateData = new ContentValues();
					updateData.put(TimeTrackingTable.END_TIME, sdFormat.format(dtmNowTime));
					
					// Update
					db.update(
						TimeTrackingTable.TABLE_NAME,
						updateData,
						"_id=?",
						new String[]{String.valueOf(id)});
				}
				
				break;
			default:
				break;
			}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    		getMenuInflater().inflate(R.menu.main_menu, menu);
    		
    		return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    		switch (item.getItemId()) {
			case R.id.opt_close:
				this.finish();
				
				break;
				
			case R.id.opt_list:
				final Intent listRecordActivity = new Intent(this, RecordList.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);;
				startActivity(listRecordActivity);
				
				break;
			case R.id.opt_clear:
				EditText txtStart = (EditText)findViewById(R.id.txtStartTime);
				EditText txtEnd = (EditText)findViewById(R.id.txtEndTime);
				
				Button cmdStart = (Button)findViewById(R.id.btnStart);
				Button cmdEnd = (Button)findViewById(R.id.btnEnd);
				
				txtStart.setText("");
				txtEnd.setText("");
				
				cmdStart.setEnabled(true);
				cmdEnd.setEnabled(false);
				
				break;
			default:
				break;
			}
    	
    	
    		return super.onOptionsItemSelected(item);
    }
}