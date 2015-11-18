package de.mvhs.android.zeiterfassung;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import de.mvhs.android.zeiterfassung.db.ZeitContract;

/**
 * Created by eugen on 09.11.15.
 */
public class EditActivity extends AppCompatActivity {
  public final static String ID_KEY = "ID";

  private long _id = -1;
  private EditText _startDate;
  private EditText _startTime;

  // Datum und Uhrzeit Formatierung
  private DateFormat _DATE_FORMATTER = DateFormat.getDateInstance(DateFormat.SHORT);
  private DateFormat _TIME_FORMATTER = DateFormat.getTimeInstance(DateFormat.SHORT);

  // Zwischenspeicher
  private Calendar _dbStartDateTime = Calendar.getInstance();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_edit);

    // Auslesen der ID aus den Zusatzdaten
    if(getIntent().hasExtra(ID_KEY)){
      _id = getIntent().getLongExtra(ID_KEY, -1);
    }

    // Initialisieren der UI Elemente
    _startDate = (EditText) findViewById(R.id.StartDate);
    _startTime = (EditText) findViewById(R.id.StartTime);

    // Direktes Bearbeiten unterbinden
    _startDate.setKeyListener(null);
    _startTime.setKeyListener(null);
  }

  @Override
  protected void onStart() {
    super.onStart();

    if(_id > 0){
      Uri query = ContentUris.withAppendedId(ZeitContract.ZeitDaten.CONTENT_URI, _id);

      Cursor data = getContentResolver().query(query, null, null, null, null);

      if(data != null && data.moveToFirst()){
        String dbStartDate = data.getString(data.getColumnIndex(ZeitContract.ZeitDaten.Columns.START));

        try {
          Date convertedDateTime = ZeitContract.Converters.DB_DATE_TIME_FORMATTER.parse(dbStartDate);

          _startDate.setText(_DATE_FORMATTER.format(convertedDateTime));
          _startTime.setText(_TIME_FORMATTER.format(convertedDateTime));

          _dbStartDateTime.setTime(convertedDateTime);
        } catch (ParseException e) {
          e.printStackTrace();
        }
      }

      if(data != null){
        data.close();
      }
    }

    // Dialoge auf LongClick einbinden
    _startDate.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        showDateDialog();
        return true;
      }
    });

    _startTime.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        showTimeDialog();
        return true;
      }
    });
  }

  private void showTimeDialog() {
    TimePickerDialog dialog = new TimePickerDialog(this, // Context
            new TimePickerDialog.OnTimeSetListener() {
              @Override
              public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                _dbStartDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                _dbStartDateTime.set(Calendar.MINUTE, minute);

                _startTime.setText(_TIME_FORMATTER.format(_dbStartDateTime.getTime()));
              }
            }, // Callback
            _dbStartDateTime.get(Calendar.HOUR_OF_DAY), // Uhrzeit (24 Stunden Format)
            _dbStartDateTime.get(Calendar.MINUTE), // Minuten
            true); // Anzeile in 24 Stunden Format oder mit AM/PM

    dialog.show();
  }

  private void showDateDialog() {
    DatePickerDialog dialog = new DatePickerDialog(this, // Context
            new DatePickerDialog.OnDateSetListener() {
              @Override
              public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                _dbStartDateTime.set(year, monthOfYear, dayOfMonth);

                _startDate.setText(_DATE_FORMATTER.format(_dbStartDateTime.getTime()));
              }
            }, // Callback
            _dbStartDateTime.get(Calendar.YEAR), // Initaljahr
            _dbStartDateTime.get(Calendar.MONTH), // Initialmonat
            _dbStartDateTime.get(Calendar.DAY_OF_MONTH)); // Initialtag

    dialog.show();
  }

  @Override
  protected void onStop() {
    super.onStop();

    _startDate.setOnLongClickListener(null);
    _startTime.setOnLongClickListener(null);

    ContentValues values = new ContentValues();
    values.put(
            ZeitContract.ZeitDaten.Columns.START,
            ZeitContract.Converters.DB_DATE_TIME_FORMATTER.format(_dbStartDateTime.getTime()));

    if(_id > 0){
      // Datensatz zum Aktualisieren
      Uri updateUri = ContentUris.withAppendedId(ZeitContract.ZeitDaten.CONTENT_URI, _id);
      getContentResolver().update(updateUri, values, null, null);
    } else{
      // Neuer Datensatz
      getContentResolver().insert(ZeitContract.ZeitDaten.CONTENT_URI, values);
    }
  }
}
