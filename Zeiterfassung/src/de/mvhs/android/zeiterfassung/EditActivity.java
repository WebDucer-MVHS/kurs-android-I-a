package de.mvhs.android.zeiterfassung;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import de.mvhs.android.zeiterfassung.db.ZeitContracts;

public class EditActivity extends Activity {
  public final static String ID_KEY         = "ID";
  private long               _Id            = -1;

  private final DateFormat   _DateFormatter = DateFormat.getDateInstance(DateFormat.SHORT);
  private final DateFormat   _TimeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_edit);

    getActionBar().setDisplayHomeAsUpEnabled(true);
    getActionBar().setDisplayShowHomeEnabled(true);

    // Auslesen der ID, falls diese übergeben wurde
    if (getIntent().getExtras() != null) {
      _Id = getIntent().getLongExtra(ID_KEY, -1);
    }

    if (_Id > 0) {
      loadData();
    }
  }

  private void loadData() {
    Uri dataUri = ContentUris.withAppendedId(ZeitContracts.Zeit.CONTENT_URI, _Id);
    Cursor data = getContentResolver().query(dataUri, null, null, null, null);

    if (data != null && data.moveToFirst()) {
      String startValue = data.getString(data.getColumnIndex(ZeitContracts.Zeit.Columns.START));

      EditText startDate = (EditText) findViewById(R.id.StartDate);
      EditText startTime = (EditText) findViewById(R.id.StartTime);
      EditText endDate = (EditText) findViewById(R.id.EndDate);
      EditText endTime = (EditText) findViewById(R.id.EndTime);

      // Konvertieren der Startzeit
      try {
        Date startDateTime = ZeitContracts.Converters.DB_FORMATTER.parse(startValue);
        startDate.setText(_DateFormatter.format(startDateTime));
        startTime.setText(_TimeFormatter.format(startDateTime));

      } catch (ParseException e) {
        startDate.setText("");
        startTime.setText("");
      }

      // Prüfen, ob die Endzeit eingertagen ist
      if (!data.isNull(data.getColumnIndex(ZeitContracts.Zeit.Columns.END))) {
        String endValue = data.getString(data.getColumnIndex(ZeitContracts.Zeit.Columns.END));

        try {
          Date endDateTime = ZeitContracts.Converters.DB_FORMATTER.parse(endValue);
          endDate.setText(_DateFormatter.format(endDateTime));
          endTime.setText(_TimeFormatter.format(endDateTime));
        } catch (ParseException e) {
          endDate.setText("");
          endTime.setText("");
        }
      } else {
        endDate.setText("");
        endTime.setText("");
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.edit_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
      case R.id.mnu_cancel:
        this.finish();
        break;

      case R.id.mnu_delete:
        if (_Id > 0) {
          Uri deleteUri = ContentUris.withAppendedId(ZeitContracts.Zeit.CONTENT_URI, _Id);
          getContentResolver().delete(deleteUri, null, null);
        }
        this.finish();
        break;

      case R.id.mnu_save:
        // TO DO
        break;

      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}
