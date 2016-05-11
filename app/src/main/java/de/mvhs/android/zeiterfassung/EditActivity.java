package de.mvhs.android.zeiterfassung;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
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

import db.TimelogContract;

/**
 * Created by eugen on 03.05.16.
 */
public class EditActivity extends AppCompatActivity {
    private long _id = -1;
    private EditText _startDate;
    private EditText _startTime;
    private EditText _endDate;
    private EditText _endTime;
    private EditText _comment;
    private EditText _pause;

    private Calendar _start = Calendar.getInstance();

    private DateFormat _DATE_UI_FORMATTER =
            DateFormat.getDateInstance(DateFormat.SHORT);
    private DateFormat _TIME_UI_FORMATTER =
            DateFormat.getTimeInstance(DateFormat.SHORT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_relative);

        // Auslesen der Zusatzinformationen aus Bundle
        if (getIntent().hasExtra("ID")){
            _id = getIntent().getLongExtra("ID", -1);
        }

        _startDate = (EditText) findViewById(R.id.StartDate);
        _startDate.setKeyListener(null);
        _startTime = (EditText) findViewById(R.id.StartTime);
        _startTime.setKeyListener(null);
        _endDate = (EditText) findViewById(R.id.EndDate);
        _endTime = (EditText) findViewById(R.id.EndTime);
        _comment = (EditText) findViewById(R.id.Comment);
        _pause = (EditText) findViewById(R.id.Pause);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Laden der Daten
        if (_id > 0){
            LoadData();
        }

        // Listener zuordner
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
        TimePickerDialog dialog = new TimePickerDialog(
                this, // Context
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        _start.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        _start.set(Calendar.MINUTE, minute);

                        _startTime.setText(_TIME_UI_FORMATTER.format(_start.getTime()));
                    }
                }, // Callback
                _start.get(Calendar.HOUR_OF_DAY), // Uhrzeit (24h Format)
                _start.get(Calendar.MINUTE), // Minuten
                true); // 24h oder 12 am/pm Anzeige

        dialog.show();
    }

    private void showDateDialog() {
        DatePickerDialog dialog = new DatePickerDialog(
                this, // Context
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        _start.set(year, monthOfYear, dayOfMonth);

                        _startDate.setText(_DATE_UI_FORMATTER.format(_start.getTime()));
                    }
                }, // Callback
                _start.get(Calendar.YEAR), // Jahr
                _start.get(Calendar.MONTH), // Monat
                _start.get(Calendar.DAY_OF_MONTH) // Tag des Monats
        );

        dialog.show();
    }

    private void LoadData() {
        // Uri generieren
        Uri query = ContentUris.withAppendedId(TimelogContract.Timelog.CONTENT_URI, _id);

        // Daten lesen
        Cursor data = getContentResolver().query(query, null, null, null, null);

        // Daten prüfen
        if (data != null && data.moveToFirst()){
            String dbStartDate = data.getString(
                    data.getColumnIndex(
                            TimelogContract.Timelog.Columns.START));

            // Konvertieren
            try {
                Date convertedDate = TimelogContract.Converter
                        .DB_DATE_TIME_FORMATTER.parse(dbStartDate);

                _startDate.setText(_DATE_UI_FORMATTER.format(convertedDate));
                _startTime.setText(_TIME_UI_FORMATTER.format(convertedDate));

                _start.setTime(convertedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }
}
