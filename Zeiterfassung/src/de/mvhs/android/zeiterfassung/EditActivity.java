package de.mvhs.android.zeiterfassung;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import de.mvhs.android.zeiterfassung.db.ZeitContracts;

public class EditActivity extends Activity {
	private long _Id = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_edit);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(true);

		if (getIntent().getExtras() != null) {
			_Id = getIntent().getLongExtra("ID", -1);
		}

		if (_Id > 0) {
			loadData();
		}
	}

	private void loadData() {
		Uri dataUri = ContentUris.withAppendedId(
				ZeitContracts.Zeit.CONTENT_URI, _Id);
		Cursor data = getContentResolver().query(dataUri, null, null, null,
				null);

		if (data != null && data.moveToFirst()) {
			String startValue = data.getString(data
					.getColumnIndex(ZeitContracts.Zeit.Columns.START));

			EditText startDate = (EditText) findViewById(R.id.StartDate);
			startDate.setText(startValue);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();

			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
