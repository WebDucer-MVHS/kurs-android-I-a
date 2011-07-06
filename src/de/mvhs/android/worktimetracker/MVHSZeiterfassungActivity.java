package de.mvhs.android.worktimetracker;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MVHSZeiterfassungActivity extends Activity {
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
        
        // Auf letzten Eintrag prüfen
        Cursor cursor =
			db.rawQuery(TimeTrackingTable.SQL_LAST_UNCOMPLETED, null);
		if (cursor.moveToNext() == true){
			String strStartTime = cursor.getString(
				cursor.getColumnIndex(TimeTrackingTable.START_TIME));
			try{
				Date dtmStartTime = sdFormat.parse(strStartTime);
				
				txtStartTime.setText(dtmStartTime.toString());
				btnStart.setEnabled(false);
		        btnEnd.setEnabled(true);
			}
			catch (Exception ex){
				//
			}
		}
        
    }
    
    /**
     * OnClick Event für Buttons
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
				txtStart.setText(dtmNowTime.toString());
				oButton.setEnabled(false);
				Button btnEnd = (Button)findViewById(R.id.btnEnd);
				btnEnd.setEnabled(true);
				
				// DB Platzhalter füllen
				SQLiteStatement stInsert =
						db.compileStatement(TimeTrackingTable.SQL_INSERT_START_DATE);
				stInsert.bindString(1, sdFormat.format(dtmNowTime));
				stInsert.executeInsert();
				
				break;

			case R.id.btnEnd:
				EditText txtEnd = (EditText)findViewById(R.id.txtEndTime);
				txtEnd.setText(dtmNowTime.toString());
				oButton.setEnabled(false);
				Button btnStart = (Button)findViewById(R.id.btnStart);
				btnStart.setEnabled(true);
				
				Cursor cursor =
					db.rawQuery(TimeTrackingTable.SQL_LAST_UNCOMPLETED, null);
				if (cursor.moveToNext() == true){
					int id = cursor.getInt(
						cursor.getColumnIndex(TimeTrackingTable.ID));
					
					// Zu aktulisierende Werte füllen
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
}