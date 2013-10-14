package de.mvhs.android.zeiterfassung;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Suchen der Elemente in eingebundenen Layout
		Button starten = (Button) findViewById(R.id.button_start);
		Button beenden = (Button) findViewById(R.id.button_end);

		// Zuordnen des Listneners zum Element
		starten.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onStartClicked();
			}
		});
	}

	// Was passiert, wenn Start Button gelickt wird
	private void onStartClicked() {
		// Textbox suchen
		EditText start_zeit = (EditText) findViewById(R.id.text_start_time);

		// Aktuelle Uhrzeit bestimmen
		Date jetzt = new Date();

		// Ausgabe der aktuellen Zeit
		start_zeit.setText(jetzt.toString());
	}
}
