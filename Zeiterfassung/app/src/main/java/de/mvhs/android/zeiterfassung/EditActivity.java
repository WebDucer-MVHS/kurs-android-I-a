package de.mvhs.android.zeiterfassung;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import java.text.ParseException;
import java.util.Calendar;

import de.mvhs.android.zeiterfassung.db.TimeContract;
import de.mvhs.android.zeiterfassung.dialogs.DateLongClick;
import de.mvhs.android.zeiterfassung.dialogs.TimeLongClick;

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

    private EditText _endDate = null;
    private EditText _endTime = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_grid);

        _id = getIntent().getLongExtra(ID_KEY, _NOT_SET);

        Log.i("EditActivity", "ID: " + _id);

        // Suchen der Elemente
        _startDate = (EditText) findViewById(R.id.StartDateValue);
        _startTime = (EditText) findViewById(R.id.StartTimeValue);

        _endDate = (EditText) findViewById(R.id.EndDateValue);
        _endTime = (EditText) findViewById(R.id.EndTimeValue);

        // Eingaben per tastatur verhindern
        _startDate.setKeyListener(null);
        _startTime.setKeyListener(null);
        _endDate.setKeyListener(null);
        _endTime.setKeyListener(null);
    }

    public String getActivityTitle(){
        return getString(R.string.ExportDialogTitle);
    }

    @Override
    protected void onStart() {
        super.onStart();

        _startDateTime = Calendar.getInstance();
        _endDateTime = Calendar.getInstance();

        if (_id != _NOT_SET) {
            // Laden der Daten aus der Datenbank
            loadDbData();
        }

        _startDate.setOnLongClickListener(new DateLongClick(this, _startDate, _startDateTime, getString(R.string.SelectStartDateDialogTitle)));
        _endDate.setOnLongClickListener(new DateLongClick(this, _endDate, _endDateTime, getString(R.string.SelectEndDateDialogTitle)));

        _startTime.setOnLongClickListener(new TimeLongClick(this, _startTime, _startDateTime, getString(R.string.SelectStartTimeDialogTitle)));
        _endTime.setOnLongClickListener(new TimeLongClick(this, _endTime, _endDateTime, getString(R.string.SelectEndTimeDialogTitle)));
    }

    @Override
    protected void onStop() {
        super.onStop();
        _startDate.setOnLongClickListener(null);
        _startTime.setOnLongClickListener(null);
        _endDate.setOnLongClickListener(null);
        _endTime.setOnLongClickListener(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // Zurück-Pfeil in der Navigationsleiste
            case android.R.id.home:
                saveData();
                return false;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        // Hardware-Button
        saveData();
        super.onBackPressed();
    }

    private void loadDbData() {
        Uri dataUri = ContentUris.withAppendedId(TimeContract.TimeData.CONTENT_URI, _id);
        Cursor data = getContentResolver().query(dataUri, null, null, null, null);

        if (data == null && data.getCount() == 0) {
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
        if (!data.isNull(columnIndex)) {
            String endDbValue = data.getString(columnIndex);
            try {
                _endDateTime = TimeContract.Converters.parseFromDb(endDbValue);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveData() {
        ContentValues values = new ContentValues();
        values.put(TimeContract.TimeData.Columns.START, TimeContract.Converters.formatForDb(_startDateTime));
        values.put(TimeContract.TimeData.Columns.END, TimeContract.Converters.formatForDb(_endDateTime));

        if (_id == _NOT_SET){
            // Neuen Datensatz hinzufügen
            getContentResolver().insert(TimeContract.TimeData.CONTENT_URI, values);
        } else {
            // Datensatz aktualisieren
            Uri updateUri = ContentUris.withAppendedId(TimeContract.TimeData.CONTENT_URI, _id);
            getContentResolver().update(updateUri, values, null, null);
        }
    }

}
