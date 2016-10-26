package de.mvhs.android.zeiterfassung;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.TintContextWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

import de.mvhs.android.zeiterfassung.db.DbHelper;
import de.mvhs.android.zeiterfassung.db.TimeDataContract;

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

        // Provider
        ContentValues value = new ContentValues();
        value.put(TimeDataContract.TimeData.Columns.START, new Date().toString());
        getContentResolver().insert(TimeDataContract.TimeData.CONTENT_URI, value);
      }
    });
  }

  @Override
  protected void onStop() {
    super.onStop();

    // Event deregistrieren
    _startCommand.setOnClickListener(null);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()){
      case R.id.MenuListData:
        Intent listIntent = new Intent(this, ListDataActivity.class);
        startActivity(listIntent);
        break;
    }

    return super.onOptionsItemSelected(item);
  }
}
