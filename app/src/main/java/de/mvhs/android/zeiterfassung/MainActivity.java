package de.mvhs.android.zeiterfassung;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

import de.mvhs.android.zeiterfassung.db.DbHelper;

public class MainActivity extends AppCompatActivity {
    // Klassenvaribalen
    private Button _startCommand;
    private EditText _startField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI Elemente in dem Layout suchen
        _startCommand = (Button) findViewById(R.id.StartCommand);
        _startField = (EditText) findViewById(R.id.StartTimeField);

        _startCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _startField.setText(new Date().toString());

                DbHelper dbHelper = new DbHelper(getBaseContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put("StartZeit", new Date().toString());

                db.insert("zeit", null, values);

                db.close();
                dbHelper.close();
            }
        });
    }
}
