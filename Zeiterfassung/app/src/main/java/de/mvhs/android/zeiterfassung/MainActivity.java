package de.mvhs.android.zeiterfassung;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import de.mvhs.android.zeiterfassung.db.DbHelper;
import de.mvhs.android.zeiterfassung.db.TimeContract;

import static android.R.attr.data;

public class MainActivity extends AppCompatActivity {

    private Button _startCommand;
    private Button _endCommand;
    private EditText _startTime;
    private EditText _endTime;
    private Uri _data;

    private DateFormat _formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_linear);

        _startCommand = (Button) findViewById(R.id.StartCommand);
        _endCommand = (Button) findViewById(R.id.EndCommand);

        _startTime = (EditText) findViewById(R.id.StartTimeValue);
        _endTime = (EditText) findViewById(R.id.EndTimeValue);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Registrieren der Events
        _startCommand.setOnClickListener(new OnStartClicked());
        _endCommand.setOnClickListener(new OnStopClicked());
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Deregistrieren der Events
        _startCommand.setOnClickListener(null);
        _endCommand.setOnClickListener(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.MenuItemList:
                Log.d("MainActivity", "Auflistung!");
                Intent listIntent = new Intent(this, TimeDataListActivity.class);
                startActivity(listIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class OnStartClicked implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Log.d("MainActivity", "Start gecklickt!");

            Calendar currentTime = Calendar.getInstance();
            String currentTimeDb = TimeContract.Converters.formatForDb(currentTime);
            ContentValues values = new ContentValues();
            values.put(TimeContract.TimeData.Columns.START, currentTimeDb);

            // Datensatz einfügen
            _data = getContentResolver().insert(TimeContract.TimeData.CONTENT_URI, values);
            Log.d("MainActivity", _data.toString());

            _startTime.setText(_formatter.format(currentTime.getTime()));
        }
    }

    public class OnStopClicked implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Log.d("MainActivity", "Ende geklickt!");

            Calendar currentTime = Calendar.getInstance();
            String currentTimeDb = TimeContract.Converters.formatForDb(currentTime);
            ContentValues values = new ContentValues();
            values.put(TimeContract.TimeData.Columns.END, currentTimeDb);

            // Datensatz aktualisieren
            if(_data != null){
                int updated = getContentResolver().update(_data, values, null, null);

                Log.d("MainActivity", String.valueOf(updated));
            }

            _endTime.setText(_formatter.format(currentTime.getTime()));
        }
    }




















}
