package de.mvhs.zeit;

import java.util.Date;

import de.mvhs.zeit.db.ZeiterfassungTable;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ZeiterfassungActivity extends Activity {
	private long _id = -1;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Initialisierung der Oberflächenelemente
        Button cmdStart = (Button) findViewById(R.id.cmdStart);
        Button cmdEnd = (Button)findViewById(R.id.cmdEnd);
        EditText txtStart = (EditText)findViewById(R.id.txtStart);
        EditText txtEnd = (EditText)findViewById(R.id.txtEnd);
        
        //  Initialisierung der Zustände
        txtStart.setText("");
        txtEnd.setText("");
        cmdEnd.setEnabled(false);
        
        // Auf Klicks reagieren
        cmdStart.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				SaveStartTime();
			}
		});
        
        cmdEnd.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				SaveEndTime();
			}
		});
    }
    
    /**
     * Startzeit aufnehmen
     */
    private void SaveStartTime()
    {
    		EditText txtStart = (EditText)findViewById(R.id.txtStart);
    		EditText txtEnd = (EditText)findViewById(R.id.txtEnd);
    		Button cmdEnd = (Button)findViewById(R.id.cmdEnd);
    		Button cmdStart = (Button)findViewById(R.id.cmdStart);
    		
    		Date dtmNow = new Date();
    		txtStart.setText(dtmNow.toString());
    		
    		cmdEnd.setEnabled(true);
    		cmdStart.setEnabled(false);
    		txtEnd.setText("");
    		
    		// Speichern in der Datenbank
    		ZeiterfassungTable ze = new ZeiterfassungTable(this);
    		_id = ze.SaveStartTime(dtmNow);
    }
    
    /**
     * Endzeit aufnehmen
     */
    private void SaveEndTime()
    {
    		EditText txtEnd = (EditText)findViewById(R.id.txtEnd);
    		Button cmdEnd = (Button)findViewById(R.id.cmdEnd);
    		Button cmdStart = (Button)findViewById(R.id.cmdStart);
    		
		Date dtmNow = new Date();
		txtEnd.setText(dtmNow.toString());
		
		cmdEnd.setEnabled(false);
		cmdStart.setEnabled(true);
		
		// Aktualisieren in der Datenbank
		ZeiterfassungTable ze = new ZeiterfassungTable(this);
		ze.SaveEndTime(_id, dtmNow);
    }
    
    /**
     * Initialisierung des Menüs
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    		MenuInflater inflater = getMenuInflater();
    		inflater.inflate(R.menu.main, menu);
    		
    		return super.onCreateOptionsMenu(menu);
    }
    
    /**
     * Auf Menü-Klicks reagieren
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    		if (item.getItemId() == R.id.opt_exit) {
				this.finish(); // Beenden der App
			}
    		else if (item.getItemId() == R.id.opt_list) {
				final Intent listIntent = new Intent(this, RecordListActivity.class);
				this.startActivity(listIntent); // Starten der Auflistung
			}
    		
	    	return super.onOptionsItemSelected(item);
    }
}