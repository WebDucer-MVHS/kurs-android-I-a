package de.mvhs.android.arbeitszeiterfassung;

import java.util.Date;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // UI Elemente binden
        Button cmdStartEnd = (Button)findViewById(R.id.button1);
        final EditText txtStartTime = (EditText)findViewById(R.id.txt_start_time);
        
        cmdStartEnd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Date dtmNow = new Date();
				
				txtStartTime.setText(dtmNow.toString());
			}
		});
    }
    
}
