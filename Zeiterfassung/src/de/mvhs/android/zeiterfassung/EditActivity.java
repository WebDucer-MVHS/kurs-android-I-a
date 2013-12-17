package de.mvhs.android.zeiterfassung;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import de.mvhs.android.zeiterfassung.db.ZeitContract;
import de.mvhs.android.zeiterfassung.db.ZeitContract.Zeit;

public class EditActivity extends Activity {
  private long                    _CURRENT_ID  = -1;

  // Date Formater
  private final static DateFormat _DATE_FORMAT = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
  private final static DateFormat _TIME_FORMAT = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);

  // UI Elemente
  private EditText                _StartDateField;
  private EditText                _StartTimeField;
  private EditText                _EndDateField;
  private EditText                _EndTimeField;
  private EditText                _PauseField;
  private EditText                _CommentFiled;

  // DB Werte
  private Calendar                _StartDate   = null;
  private Calendar                _EndDate     = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_edit);

    // Home Button aktivieren
    getActionBar().setDisplayShowHomeEnabled(true);
    getActionBar().setDisplayHomeAsUpEnabled(true);

    // Auslesen der Zusatzinformationen
    if (getIntent() != null && getIntent().getExtras() != null) {
      _CURRENT_ID = getIntent().getExtras().getLong("ID", -1);
    }

    initUiElements();
  }

  @Override
  protected void onStart() {
    super.onStart();

    // Initialisieren mit den Daten aus der Datenbank
    if (_CURRENT_ID > 0) {
      Uri currentDataUri = ContentUris.withAppendedId(Zeit.CONTENT_URI, _CURRENT_ID);
      Cursor currentData = getContentResolver().query(currentDataUri, null, null, null, null);

      // Profen, dass ui der ID Daten existieren
      if (currentData != null && currentData.moveToFirst()) {
        // Start Zeit
        String startTime = currentData.getString(currentData.getColumnIndex(Zeit.Columns.START));
        // End Zeit
        String endTime = "";
        if (currentData.isNull(currentData.getColumnIndex(Zeit.Columns.END)) == false) {
          endTime = currentData.getString(currentData.getColumnIndex(Zeit.Columns.END));
        }

        // Konvertierung in Date-Objekte
        try {
          _StartDate = Calendar.getInstance();
          _StartDate.setTime(ZeitContract.DB_DATE_FORMAT.parse(startTime));

          if (endTime != null && "".equals(endTime) == false) {
            _EndDate = Calendar.getInstance();
            _EndDate.setTime(ZeitContract.DB_DATE_FORMAT.parse(endTime));
          }
        } catch (ParseException e) {
          // nichts tun
        }

        // Daten and die UI übergeben
        if (_StartDate != null) {
          _StartDateField.setText(_DATE_FORMAT.format(_StartDate.getTime()));
          _StartTimeField.setText(_TIME_FORMAT.format(_StartDate.getTime()));
        }

        if (_EndDate != null) {
          _EndDateField.setText(_DATE_FORMAT.format(_EndDate.getTime()));
          _EndTimeField.setText(_TIME_FORMAT.format(_EndDate.getTime()));
        }
      }
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        Intent homeIntent = new Intent(this, MainActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(homeIntent);
        break;

      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      // Nachfrage beim Benutzer zur Speicherung der Daten
      if (saveNotSaveOrCancel()) {
        KeyEvent.changeFlags(event, KeyEvent.FLAG_CANCELED);
      }
    }
    return super.onKeyDown(keyCode, event);
  }

  private boolean saveNotSaveOrCancel() {
    boolean canceled = true;

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.save_title).setMessage(R.string.save_dilog_message)
            .setPositiveButton(R.string.save_button, new DialogInterface.OnClickListener() {

              @Override
              public void onClick(DialogInterface dialog, int which) {
                // Daten speichern und Activity beenden

                EditActivity.this.finish();
              }
            }).setNegativeButton(R.string.not_save_button, new DialogInterface.OnClickListener() {

              @Override
              public void onClick(DialogInterface dialog, int which) {
                // Daten nicht speichern, aber Activity beenden

                EditActivity.this.finish();
              }
            }).setNeutralButton(R.string.cancel_button, new DialogInterface.OnClickListener() {

              @Override
              public void onClick(DialogInterface dialog, int which) {
                // Zurück - Knopf Aktion abbrechen => nichts tun
              }
            }).create().show();

    return canceled;
  }

  private void initUiElements() {
    // Initialisierung der UI Elemente
    _StartDateField = (EditText) findViewById(R.id.StartDateText);
    _StartTimeField = (EditText) findViewById(R.id.StartTimeText);
    _EndDateField = (EditText) findViewById(R.id.EndDateText);
    _EndTimeField = (EditText) findViewById(R.id.EndTimeText);
    _PauseField = (EditText) findViewById(R.id.PauseText);
    _CommentFiled = (EditText) findViewById(R.id.CommentText);

    // Aussschalten der manuellen Bearbeitung der Feldwerte
    _StartDateField.setKeyListener(null);
    _StartTimeField.setKeyListener(null);
    _EndDateField.setKeyListener(null);
    _EndTimeField.setKeyListener(null);

    initDialogs();
  }

  private void initDialogs() {
    // Start Datum
    _StartDateField.setOnLongClickListener(new OnLongClickListener() {

      @Override
      public boolean onLongClick(View v) {
        DatePickerDialog dateDialog = new DatePickerDialog(EditActivity.this, _StartDateSetListener, _StartDate.get(Calendar.YEAR), _StartDate
                .get(Calendar.MONTH), _StartDate.get(Calendar.DAY_OF_MONTH));
        dateDialog.show();
        return true;
      }
    });
  }

  /* Listener */
  private DatePickerDialog.OnDateSetListener _StartDateSetListener = new OnDateSetListener() {

                                                                     @Override
                                                                     public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                                                       _StartDate.set(Calendar.YEAR, year);
                                                                       _StartDate.set(Calendar.MONTH, monthOfYear);
                                                                       _StartDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                                                       _StartDateField.setText(_DATE_FORMAT.format(_StartDate.getTime()));
                                                                     }
                                                                   };
}
