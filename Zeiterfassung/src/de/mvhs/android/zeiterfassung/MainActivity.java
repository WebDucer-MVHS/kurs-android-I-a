package de.mvhs.android.zeiterfassung;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Referenz auf die Buttons im Layout suchen
        Button starten = (Button)findViewById(R.id.starten);
        Button beenden = (Button)findViewById(R.id.beenden);
        
        // Listener für "Starten"-Button definieren
        starten.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				onStarten();
			}
		});
        
        // Listener für "Beenden"-Button definieren
        beenden.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				onBeenden();
			}
		});
    }
    
    private void onStarten(){
    		// Textfeld Referenz im Layout suchen
    		EditText startzeit = (EditText)findViewById(R.id.startzeit);
    		
    		// Aktuelle zeit bestimmen
    		Date jetzt = new Date();
    		
    		// Zeit ausgeben
    		startzeit.setText(jetzt.toString());
    }
    
    private void onBeenden(){
    		// Textfeld Referenz im Layout suchen
    		EditText endzeit = (EditText)findViewById(R.id.endzeit);
    		
    		// Aktuelle Zeit bestimmen
    		Date jetzt = new Date();
    		
    		// Zeit ausgeben
    		endzeit.setText(jetzt.toString());
    }
}
