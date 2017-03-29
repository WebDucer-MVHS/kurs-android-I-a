package de.mvhs.android.zeiterfassung;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Date;

import de.mvhs.android.zeiterfassung.db.DbHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_linear);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Speichern in der Datenbank
        DbHelper helper = new DbHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        // Einfügen der Daten
        ContentValues values = new ContentValues();
        values.put("start_time", new Date().toString());
        values.put("end_time", new Date().toString());

        long id = db.insert("time", null, values);

        db.close();
        helper.close();
    }
























}
