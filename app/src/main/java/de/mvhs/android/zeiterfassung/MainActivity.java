package de.mvhs.android.zeiterfassung;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

import de.mvhs.android.zeiterfassung.db.DbHelper;

public class MainActivity extends AppCompatActivity {

  private EditText _startValue;
  private Button _startCommand;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Linear Layout
    //setContentView(R.layout.activity_main_linear);

    // Relative layout
    //setContentView(R.layout.activity_main_relative);

    // Grid layout
    setContentView(R.layout.activity_main_grid);

    // Suchen der Elemente im Layout
    _startValue = (EditText) findViewById(R.id.StartTimeValue);
    _startCommand = (Button) findViewById(R.id.StartCommand);
  }

  @Override
  protected void onStart() {
    super.onStart();

    // Event registrieren
    _startCommand.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // Was soll der Button machen
        _startValue.setText(new Date().toString());

        // Daten in die Datenbank speichern
        DbHelper helper = new DbHelper(MainActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        // Rohes SQL
        String sql = "INSERT INTO [main].[time_data] ([start_time]) VALUES (?1)";
        db.execSQL(sql, new Object[]{new Date().toString()});

        // Methode
        ContentValues value = new ContentValues();
        value.put("start_time", new Date().toString());
        db.insert("time_data", null, value);

        // Precompiled
        SQLiteStatement insert = db.compileStatement(sql);
        insert.bindString(1, new Date().toString());
        insert.executeInsert();

        db.close();
        helper.close();
      }
    });
  }

  @Override
  protected void onStop() {
    super.onStop();

    // Event deregistrieren
    _startCommand.setOnClickListener(null);
  }
}
