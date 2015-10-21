package de.mvhs.android.zeiterfassung;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

import de.mvhs.android.zeiterfassung.db.DbHelper;
import de.mvhs.android.zeiterfassung.db.ZeitContract;

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

                ContentValues values = new ContentValues();
                values.put("StartZeit", new Date().toString());

                // Einfügen über Content Provider
                Uri neuerDatensatz = getContentResolver().insert(ZeitContract.ZeitDaten.CONTENT_URI, values);

                long id = ContentUris.parseId(neuerDatensatz);
            }
        });
    }
}
