package de.mvhs.android.zeiterfassung;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import de.mvhs.android.zeiterfassung.db.DbHelper;
import de.mvhs.android.zeiterfassung.db.TimeContract;

import static android.R.attr.data;

public class MainActivity extends AppCompatActivity {

    private final static int _PERMISSION_REQUEST = 100;

    private Button _startCommand;
    private Button _endCommand;
    private EditText _startTime;
    private EditText _endTime;

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

        _startCommand.setEnabled(false);
        _endCommand.setEnabled(false);
        initFromDb();
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
        switch (item.getItemId()) {
            case R.id.MenuItemList:
                Log.d("MainActivity", "Auflistung!");
                Intent listIntent = new Intent(this, TimeDataListActivity.class);
                startActivity(listIntent);
                return true;

            case R.id.MenuItemExport:
                //Log.d("MainActivity", "Export gestartet");
                //new CsvExporter(this).execute();

                // Berechtigung abfragen
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED){
                    new CsvExporter(this).execute();
                } else {
                    // Berechtigung nachfragen
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, _PERMISSION_REQUEST);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == _PERMISSION_REQUEST && grantResults.length == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                new CsvExporter(this).execute();
            }
        } else {
            return;
        }
    }

    public class OnStartClicked implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Log.d("MainActivity", "Start gecklickt!");

            _startCommand.setEnabled(false);

            Calendar currentTime = Calendar.getInstance();
            String currentTimeDb = TimeContract.Converters.formatForDb(currentTime);
            ContentValues values = new ContentValues();
            values.put(TimeContract.TimeData.Columns.START, currentTimeDb);

            // Datensatz einfügen
            getContentResolver().insert(TimeContract.TimeData.CONTENT_URI, values);

            _startTime.setText(_formatter.format(currentTime.getTime()));

            _endCommand.setEnabled(true);
        }
    }

    public class OnStopClicked implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Log.d("MainActivity", "Ende geklickt!");

            _endCommand.setEnabled(false);

            Calendar currentTime = Calendar.getInstance();
            String currentTimeDb = TimeContract.Converters.formatForDb(currentTime);
            ContentValues values = new ContentValues();
            values.put(TimeContract.TimeData.Columns.END, currentTimeDb);

            // Datensatz aktualisieren
            getContentResolver().update(TimeContract.TimeData.OPEN_URI, values, null, null);

            _endTime.setText(_formatter.format(currentTime.getTime()));

            _startCommand.setEnabled(true);
        }
    }

    private void initFromDb() {
        // Offenen Datensatz suchen
        String[] columns = new String[]{TimeContract.TimeData.Columns.START};
        Cursor data = getContentResolver().query(
            TimeContract.TimeData.OPEN_URI, // Spezial-URI
            columns, // Spalten
            null, // Filter
            null, // Filter Argumente
            null); // Sortierung

        // Mindestens ein Datensatz vorhanden
        if (data.moveToFirst()){
            String startDbValue = data.getString(0);
            try {
                Calendar startDate = TimeContract.Converters.parseFromDb(startDbValue);
                _startTime.setText(_formatter.format(startDate.getTime()));
            } catch (ParseException e) {
                _startTime.setText(R.string.WrongFormatMessage);
                e.printStackTrace();
            }

            _endTime.setText("");
            // Buttons setzen
            _endCommand.setEnabled(true);
        } else {
            // Kein offener Datensatz da
            _startTime.setText("");
            _endTime.setText("");
            _startCommand.setEnabled(true);
        }
    }


}
