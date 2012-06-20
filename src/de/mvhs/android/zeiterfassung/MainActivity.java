package de.mvhs.android.zeiterfassung;

import java.util.Date;

import de.mvhs.android.zeiterfassung.db.WorktimeTable;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Referenzierung der Buttons aus dem Layout
        Button start = (Button)findViewById(R.id.cmd_start);
        Button end = (Button)findViewById(R.id.cmd_end);
        
        // Zuweisung der Click-Events zu den Buttons
        start.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				runStartClick();
			}
		});
        end.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				runEndClick();
			}
		});
    }
    
    private void runStartClick(){
    		// Referenzierung auf das Layout
    		EditText startTime = (EditText)findViewById(R.id.txt_start_time);
    		// Aktuelles Datum bestimmen
    		Date dateNow = new Date();
    		
    		// Aktuelle Datum ins Feld schreiben
    		startTime.setText(dateNow.toString());
    		
    		// Speichern in der Datenbank
    		WorktimeTable table = new WorktimeTable(this);
    		table.saveWorktime(dateNow);
    }
    
    private void runEndClick(){
    		// Referenzierung auf das Layout
		EditText endTime = (EditText)findViewById(R.id.txt_end_time);
		// Aktuelles Datum bestimmen
		Date dateNow = new Date();
		
		// Aktuelle Datum ins Feld schreiben
		endTime.setText(dateNow.toString());
    }
}