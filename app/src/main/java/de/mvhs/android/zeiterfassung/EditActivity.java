package de.mvhs.android.zeiterfassung;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;

import de.mvhs.android.zeiterfassung.db.TimeDataContract;

/**
 * Created by eugen on 06.11.16.
 */

public class EditActivity extends AppCompatActivity {
  public final static String ID_KEY = "EditItemId";
  private long _id = -1;
  private EditText _startDate;
  private Calendar _startDateTimeValue;
  private DateFormat _dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT);

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_grid);

    // Auslesen der ID aus Metainformationen
    _id = getIntent().getLongExtra(ID_KEY, -1);

    // Initialisieren der UI Elemente
    _startDate = (EditText) findViewById(R.id.StartDateValue);
    _startDate.setKeyListener(null);
  }

  @Override
  protected void onStart() {
    super.onStart();

    LoadData();
  }

  private void LoadData(){
    // Neuer Datensatz
    if (_id == -1){
      return;
    }

    // Vorhandener Datensatz
    Uri dataUri =
            ContentUris.withAppendedId(TimeDataContract.TimeData.CONTENT_URI, _id);

    Cursor data = getContentResolver().query(dataUri, null, null, null, null);

    // Daten lesen
    if (data.moveToFirst()){
      // Startzeit auslesen
      String startValueFromDb =
              data.getString(data.getColumnIndex(TimeDataContract.TimeData.Columns.START));

      try {
        _startDateTimeValue = TimeDataContract.Converter.parseFromDb(startValueFromDb);

        // Ausgabe an der Oberfläche
        _startDate.setText(_dateFormatter.format(_startDateTimeValue.getTime()));
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    data.close();
  }
}
