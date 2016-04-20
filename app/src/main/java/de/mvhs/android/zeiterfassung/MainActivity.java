package de.mvhs.android.zeiterfassung;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import db.DbHelper;
import db.TimelogContract;

public class MainActivity extends AppCompatActivity {
    private DateFormat _UI_DATE_FORMATTER = DateFormat.getDateTimeInstance(
            DateFormat.SHORT,
            DateFormat.SHORT
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_relative);

        // Suchen der Elemente vom Layout
        Button startCommand = (Button) findViewById(R.id.StartCommand);
        final EditText startTime = (EditText) findViewById(R.id.StartTime);

        startCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime.setText(_UI_DATE_FORMATTER.format(new Date()));

                ContentValues value = new ContentValues();
                value.put(TimelogContract.Timelog.Columns.START,
                        TimelogContract.Converter.DB_DATE_TIME_FORMATTER
                                .format(new Date()));

                getContentResolver().insert(TimelogContract.Timelog.CONTENT_URI,
                        value);
            }
        });

        Button endCommand = (Button) findViewById(R.id.EndCommand);
        final EditText endTime = (EditText)findViewById(R.id.EndTime);

        endCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lesen der Datensätze
                Cursor data = getContentResolver().query(
                        TimelogContract.Timelog.CONTENT_URI, // URI
                        null, // Welche Spalten
                        null, // Filter
                        null, // Filter Parameter
                        TimelogContract.Timelog.Columns._ID + " DESC"); // Sortierung

                if (data.moveToFirst()){
                    long id = data.getLong(data.getColumnIndex(BaseColumns._ID));
                    String start = data.getString(
                            data.getColumnIndex(TimelogContract.Timelog.Columns.START));

                    try {
                        Date startTime = TimelogContract.Converter.DB_DATE_TIME_FORMATTER
                                .parse(start);

                        start = _UI_DATE_FORMATTER.format(startTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    endTime.setText("ID: " + id + ", Start: "  +start);
                }
            }
        });
    }
}
