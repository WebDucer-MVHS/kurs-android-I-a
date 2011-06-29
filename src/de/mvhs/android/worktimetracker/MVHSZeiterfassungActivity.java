package de.mvhs.android.worktimetracker;

import java.util.Date;

import android.app.Activity;
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
    }
    
    /**
     * OnClick Event für Buttons
     */
    public void onButtonClick(final View vButton){
    		Date dtmNowTime = new Date();
    		Button oButton = (Button)vButton;
    		
    		switch (oButton.getId()) {
			case R.id.btnStart:
				EditText txtStart = (EditText)findViewById(R.id.txtStartTime);
				txtStart.setText(dtmNowTime.toString());
				oButton.setEnabled(false);
				Button btnEnd = (Button)findViewById(R.id.btnEnd);
				btnEnd.setEnabled(true);
				
				break;

			case R.id.btnEnd:
				EditText txtEnd = (EditText)findViewById(R.id.txtEndTime);
				txtEnd.setText(dtmNowTime.toString());
				oButton.setEnabled(false);
				Button btnStart = (Button)findViewById(R.id.btnStart);
				btnStart.setEnabled(true);
				
				break;
			default:
				break;
			}
    }
}