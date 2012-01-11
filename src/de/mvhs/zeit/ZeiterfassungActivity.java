package de.mvhs.zeit;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ZeiterfassungActivity extends Activity {
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
    }
}