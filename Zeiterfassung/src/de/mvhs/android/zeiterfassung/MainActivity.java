package de.mvhs.android.zeiterfassung;

import java.util.Date;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import de.mvhs.android.zeiterfassung.db.DBHelper;

public class MainActivity extends Activity {

	private boolean _started = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onStart() {
		super.onStart();

		// Suchen der UI Elemente
		final Button cmdStart = (Button) findViewById(R.id.StartCommand);
		final Button cmdEnd = (Button) findViewById(R.id.EndCommand);
		final EditText edStart = (EditText) findViewById(R.id.StartTime);
		final EditText edEnd = (EditText) findViewById(R.id.EndTime);

		// Reagiren auf den Klick des Buttons
		cmdStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				_started = true;
				// Set buttons
				cmdStart.setEnabled(!_started);
				cmdEnd.setEnabled(_started);

				Date jetzt = new Date();

				edStart.setText(jetzt.toString());

				DBHelper dbHelper = new DBHelper(getApplicationContext());
				SQLiteDatabase db = dbHelper.getReadableDatabase();
			}
		});
		cmdEnd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				_started = false;
				// Set buttons
				cmdStart.setEnabled(!_started);
				cmdEnd.setEnabled(_started);

				Date jetzt = new Date();

				edEnd.setText(jetzt.toString());
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();

		Button cmdStart = (Button) findViewById(R.id.StartCommand);
		Button cmdEnd = (Button) findViewById(R.id.EndCommand);
		// Reagiren auf den Klick des Buttons
		cmdStart.setOnClickListener(null);
		cmdEnd.setOnClickListener(null);
	}
}
