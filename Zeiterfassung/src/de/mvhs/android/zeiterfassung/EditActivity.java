package de.mvhs.android.zeiterfassung;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import de.mvhs.android.zeiterfassung.db.ZeitContract;
import de.mvhs.android.zeiterfassung.db.ZeitContract.Zeit;

public class EditActivity extends Activity {
  private long                    _CURRENT_ID  = -1;

  // Date Formater
  private final static DateFormat _DATE_FORMAT = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
  private final static DateFormat _TIME_FORMAT = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_edit);

    // Home Button aktivieren
    getActionBar().setDisplayShowHomeEnabled(true);
    getActionBar().setDisplayHomeAsUpEnabled(true);

    // Auslesen der Zusatzinformationen
    if (getIntent() != null && getIntent().getExtras() != null) {
      _CURRENT_ID = getIntent().getExtras().getLong("ID", -1);
    }
  }

  @Override
  protected void onStart() {
    super.onStart();

    // Initialisieren mit den Daten aus der Datenbank
    if (_CURRENT_ID > 0) {
      Uri currentDataUri = ContentUris.withAppendedId(Zeit.CONTENT_URI, _CURRENT_ID);
      Cursor currentData = getContentResolver().query(currentDataUri, null, null, null, null);

      // Profen, dass ui der ID Daten existieren
      if (currentData != null && currentData.moveToFirst()) {
        // Start Zeit
        String startTime = currentData.getString(currentData.getColumnIndex(Zeit.Columns.START));
        // End Zeit
        String endTime = "";
        if (currentData.isNull(currentData.getColumnIndex(Zeit.Columns.END)) == false) {
          endTime = currentData.getString(currentData.getColumnIndex(Zeit.Columns.END));
        }

        // Initialisieren der UI-Elemente
        EditText startDateField = (EditText) findViewById(R.id.StartDateText);
        EditText startTimeField = (EditText) findViewById(R.id.StartTimeText);
        EditText endDateField = (EditText) findViewById(R.id.EndDateText);
        EditText endTimeField = (EditText) findViewById(R.id.EndTimeText);

        // Konvertierung in Date-Objekte
        Date startDateObject = null;
        Date endDateObject = null;

        try {
          startDateObject = ZeitContract.DB_DATE_FORMAT.parse(startTime);

          if (endTime != null && "".equals(endTime) == false) {
            endDateObject = ZeitContract.DB_DATE_FORMAT.parse(endTime);
          }
        } catch (ParseException e) {
          // nichts tun
        }

        // Daten and die UI übergeben
        if (startDateObject != null) {
          startDateField.setText(_DATE_FORMAT.format(startDateObject));
          startTimeField.setText(_TIME_FORMAT.format(startDateObject));
        }

        if (endDateObject != null) {
          endDateField.setText(_DATE_FORMAT.format(endDateObject));
          endTimeField.setText(_TIME_FORMAT.format(endDateObject));
        }
      }
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        Intent homeIntent = new Intent(this, MainActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(homeIntent);
        break;

      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}
