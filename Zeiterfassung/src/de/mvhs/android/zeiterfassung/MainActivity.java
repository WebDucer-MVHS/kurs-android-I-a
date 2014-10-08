package de.mvhs.android.zeiterfassung;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    @Override
    protected void onStart() {
    		super.onStart();
    		
    		// Suchen der UI Elemente
    		Button cmdStart = (Button)findViewById(R.id.StartCommand);
    		Button cmdEnd = (Button)findViewById(R.id.EndCommand);
    		final EditText edStart = (EditText)findViewById(R.id.StartTime);
    		EditText edEnd = (EditText)findViewById(R.id.EndTime);
    		
    		// Reagiren auf den Klick des Buttons
    		cmdStart.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Date jetzt = new Date();
					
					edStart.setText(jetzt.toString());
				}
			});
    		
    		
    		
    		
    }
}
