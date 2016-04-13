package de.mvhs.android.zeiterfassung;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

import db.DbHelper;

public class MainActivity extends AppCompatActivity {

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
                startTime.setText(String.valueOf(new Date()));

                DbHelper helper = new DbHelper(MainActivity.this);
                SQLiteDatabase db = helper.getWritableDatabase();

                ContentValues value = new ContentValues();
                value.put("start_time", new Date().toString());

                db.insert("timelog", null, value);

                db.close();
                helper.close();
            }
        });
    }
}
