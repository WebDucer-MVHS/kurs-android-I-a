package de.mvhs.android.zeiterfassung;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;

import de.mvhs.android.zeiterfassung.db.TimeContract;

public class EditActivity extends AppCompatActivity {
    public final static String ID_KEY = "TimeDataId";

    private final static long _NOT_SET = -1;
    private long _id = _NOT_SET;

    private Calendar _startDateTime = null;
    private Calendar _endDateTime = null;
    private int _pause = 0;
    private String _comment = null;

    private EditText _startDate = null;
    private EditText _startTime = null;

    private DateFormat _dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT);
    private DateFormat _timeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_grid);

        _id = getIntent().getLongExtra(ID_KEY, _NOT_SET);

        Log.i("EditActivity", "ID: " + _id);

        // Suchen der Elemente
        _startDate = (EditText) findViewById(R.id.StartDateValue);
        _startTime = (EditText) findViewById(R.id.StartTimeValue);
    }

    @Override
    protected void onStart() {
        super.onStart();

        _startDateTime = Calendar.getInstance();
        _endDateTime = Calendar.getInstance();

        if(_id != _NOT_SET){
            // Laden der Daten aus der Datenbank
            loadDbData();
        }

        updateUI();

        _startDate.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Starten des Datumsdialoges
                DatePickerDialog dialog = new DatePickerDialog(
                        EditActivity.this, // Context
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                _startDateTime.set(year, month, dayOfMonth);
                                updateUI();
                            }
                        }, // Callback
                        _startDateTime.get(Calendar.YEAR), // Jahr
                        _startDateTime.get(Calendar.MONTH), // Monat
                        _startDateTime.get(Calendar.DAY_OF_MONTH)); // Tag im Monat
                dialog.setTitle("Startdatum auswählen");
                dialog.show();

                return true;
            }
        });

        _startTime.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                boolean is24h = android.text.format.DateFormat.is24HourFormat(EditActivity.this);

                TimePickerDialog dialog = new TimePickerDialog(
                        EditActivity.this, // Context
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                _startDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                _startDateTime.set(Calendar.MINUTE, minute);
                                updateUI();
                            }
                        }, // Callback
                        _startDateTime.get(Calendar.HOUR_OF_DAY), // 24h Stunden
                        _startDateTime.get(Calendar.MINUTE), // Minuten
                        is24h // 24h?
                );

                dialog.show();

                return true;
            }
        });
    }

    private void updateUI() {
        _startDate.setText(_dateFormatter.format(_startDateTime.getTime()));
        _startTime.setText(_timeFormatter.format(_startDateTime.getTime()));
    }

    private void loadDbData() {
        Uri dataUri = ContentUris.withAppendedId(TimeContract.TimeData.CONTENT_URI, _id);
        Cursor data = getContentResolver().query(dataUri, null, null, null, null);

        if (data == null && data.getCount() == 0){
            return;
        }

        data.moveToFirst();
        // Startzeit
        String startDbValue = data.getString(
                data.getColumnIndex(TimeContract.TimeData.Columns.START)
        );
        try {
            _startDateTime = TimeContract.Converters.parseFromDb(startDbValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Endzeit
        int columnIndex = data.getColumnIndex(TimeContract.TimeData.Columns.END);
        if(!data.isNull(columnIndex)){
            String endDbValue = data.getString(columnIndex);
            try {
                _endDateTime = TimeContract.Converters.parseFromDb(endDbValue);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
















}
