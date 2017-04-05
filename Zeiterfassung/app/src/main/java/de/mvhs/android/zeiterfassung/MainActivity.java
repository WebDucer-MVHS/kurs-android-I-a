package de.mvhs.android.zeiterfassung;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import de.mvhs.android.zeiterfassung.db.DbHelper;
import de.mvhs.android.zeiterfassung.db.TimeContract;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_linear);
    }

    @Override
    protected void onStart() {
        super.onStart();

//        // Speichern in der Datenbank
//        DbHelper helper = new DbHelper(this);
//        SQLiteDatabase db = helper.getWritableDatabase();
//
//        // Einfügen der Daten
//        ContentValues values = new ContentValues();
//        values.put("start_time", new Date().toString());
//        values.put("end_time", new Date().toString());
//
//        long id = db.insert("time", null, values);
//
//        db.close();
//        helper.close();

        /* Content Provider */
        // Einfügen der Daten
        ContentValues values = new ContentValues();
        values.put(TimeContract.TimeData.Columns.START,
                TimeContract.Converters.formatForDb(Calendar.getInstance()));
        values.put(TimeContract.TimeData.Columns.END,
                TimeContract.Converters.formatForDb(Calendar.getInstance()));

        // Speichern über Content Provider
        getContentResolver().insert(TimeContract.TimeData.CONTENT_URI, values);
    }
























}
