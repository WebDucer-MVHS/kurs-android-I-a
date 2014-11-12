package de.mvhs.android.zeiterfassung;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class EditRecordActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_edit);

		// Auslesen der ID aus den Extras
		long id = -1;
		if (getIntent().getExtras() != null) {
			id = getIntent().getLongExtra("ID", -1);
		}

		Toast.makeText(this, "ID: " + id, Toast.LENGTH_LONG).show();
	}
}
