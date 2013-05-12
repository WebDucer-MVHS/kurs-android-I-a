package de.mvhs.android.arbeitszeiterfassung;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import de.mvhs.android.arbeitszeiterfassung.db.ZeitabschnittContract;
import de.mvhs.android.arbeitszeiterfassung.db.ZeitabschnittContract.Zeitabschnitte;

public class EditActivity extends Activity {
  // Variablen
  private long              _Id                = -1;
  private boolean           _ReadOnly          = true;
  private Calendar          _StartDateTime     = null;
  private Calendar          _EndDateTime       = null;
  private final DateFormat  _DateFormatter     = DateFormat.getDateInstance(DateFormat.SHORT);
  private final DateFormat  _TimeFormatter     = DateFormat.getTimeInstance(DateFormat.SHORT);
  // UI Elemente
  EditText                  _Start             = null;
  EditText                  _Ende              = null;
  EditText                  _StartZeit         = null;
  EditText                  _EndeZeit          = null;
  EditText                  _Pause             = null;
  EditText                  _Comment           = null;

  // Listener für Datum und Uhrzeit Einstellung
  private OnDateSetListener _StartDateListener = new OnDateSetListener() {

                                                 @Override
                                                 public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                                   _StartDateTime.set(Calendar.YEAR, year);
                                                   _StartDateTime.set(Calendar.MONTH, monthOfYear);
                                                   _StartDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                                   _Start.setText(_DateFormatter.format(_StartDateTime.getTime()));
                                                 }
                                               };
  private OnTimeSetListener _StartTimeListener = new OnTimeSetListener() {

                                                 @Override
                                                 public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                   _StartDateTime.set(Calendar.HOUR, hourOfDay);
                                                   _StartDateTime.set(Calendar.MINUTE, minute);

                                                   _StartZeit.setText(_TimeFormatter.format(_StartDateTime.getTime()));
                                                 }
                                               };
  private OnDateSetListener _EndDateListener   = new OnDateSetListener() {

                                                 @Override
                                                 public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                                   _EndDateTime.set(Calendar.YEAR, year);
                                                   _EndDateTime.set(Calendar.MONTH, monthOfYear);
                                                   _EndDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                                   _Ende.setText(_DateFormatter.format(_EndDateTime.getTime()));
                                                 }
                                               };
  private OnTimeSetListener _EndTimeListener   = new OnTimeSetListener() {

                                                 @Override
                                                 public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                   _EndDateTime.set(Calendar.HOUR, hourOfDay);
                                                   _EndDateTime.set(Calendar.MINUTE, minute);

                                                   _EndeZeit.setText(_TimeFormatter.format(_EndDateTime.getTime()));
                                                 }
                                               };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_edit);

    // Extras auslesen
    if (getIntent().getExtras() != null) {
      if (getIntent().getExtras().containsKey("ID")) {
        _Id = getIntent().getLongExtra("ID", -1);
      }
      if (getIntent().getExtras().containsKey("ReadOnly")) {
        _ReadOnly = getIntent().getBooleanExtra("ReadOnly", true);
      }
    }

    // UI Elemente initialisieren
    _Start = (EditText) findViewById(R.id.txt_start);
    _Ende = (EditText) findViewById(R.id.txt_end);
    _StartZeit = (EditText) findViewById(R.id.txt_start_time);
    _EndeZeit = (EditText) findViewById(R.id.txt_end_time);
    _Pause = (EditText) findViewById(R.id.txt_pause);
    _Comment = (EditText) findViewById(R.id.txt_comment);

    if (_ReadOnly) {
      getActionBar().setDisplayHomeAsUpEnabled(true);
      getActionBar().setDisplayShowHomeEnabled(true);
    }

    initDialogs();
  }

  @Override
  protected void onStart() {
    super.onStart();

    // ReadOnly
    _Start.setEnabled(!_ReadOnly);
    _StartZeit.setEnabled(!_ReadOnly);
    _Ende.setEnabled(!_ReadOnly);
    _EndeZeit.setEnabled(!_ReadOnly);
    _Pause.setEnabled(!_ReadOnly);
    _Comment.setEnabled(!_ReadOnly);

    _Start.setKeyListener(null);
    _StartZeit.setKeyListener(null);
    _Ende.setKeyListener(null);
    _EndeZeit.setKeyListener(null);

    // Prüfen, ob Daten da sind
    if (_Id > 0) {
      Uri uri = ContentUris.withAppendedId(ZeitabschnittContract.Zeitabschnitte.CONTENT_URI, _Id);

      Cursor data = getContentResolver().query(uri, null, null, null, null);

      if (data != null && data.moveToFirst()) {
        // Daten aus der Datenbank ausgeben
        String startString = data.getString(data.getColumnIndex(Zeitabschnitte.Columns.START));
        try {
          _StartDateTime = Calendar.getInstance();
          _StartDateTime.setTime(ZeitabschnittContract.DB_DATE_FORMATTER.parse(startString));
          _Start.setText(_DateFormatter.format(_StartDateTime.getTime()));
          _StartZeit.setText(_TimeFormatter.format(_StartDateTime.getTime()));
        } catch (ParseException e) {
          _StartDateTime = null;
        }

        // -- Ende
        if (data.isNull(data.getColumnIndex(Zeitabschnitte.Columns.STOP))) {
          _Ende.setText("");
        } else {
          String endString = data.getString(data.getColumnIndex(Zeitabschnitte.Columns.STOP));
          try {
            _EndDateTime = Calendar.getInstance();
            _EndDateTime.setTime(ZeitabschnittContract.DB_DATE_FORMATTER.parse(endString));
            _Ende.setText(_DateFormatter.format(_EndDateTime.getTime()));
            _EndeZeit.setText(_TimeFormatter.format(_EndDateTime.getTime()));
          } catch (ParseException e) {
            _EndDateTime = null;
          }
        }

        // -- Pause
        _Pause.setText(String.valueOf(data.getInt(data.getColumnIndex(Zeitabschnitte.Columns.PAUSE))));

        // -- Kommentar
        if (data.isNull(data.getColumnIndex(Zeitabschnitte.Columns.COMMENT))) {
          _Comment.setText("");
        } else {
          _Comment.setText(data.getString(data.getColumnIndex(Zeitabschnitte.Columns.COMMENT)));
        }

      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    if (!_ReadOnly) {
      getMenuInflater().inflate(R.menu.mnu_edit, menu);
    }

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        this.finish();
        break;

      case R.id.mnu_delete:
        deleteData();
        break;

      case R.id.mnu_cancel:
        this.finish();

      case R.id.mnu_save:
        saveData();
        break;
      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  private void saveData() {
    // UI Elemente initialisieren
    EditText startTime = (EditText) findViewById(R.id.txt_start_time);
    EditText endTime = (EditText) findViewById(R.id.txt_end_time);
    EditText pause = (EditText) findViewById(R.id.txt_pause);
    EditText comment = (EditText) findViewById(R.id.txt_comment);

    // Werte auslesen
    String startString = startTime.getText().toString();
    String endString = endTime.getText().toString();
    String commentString = comment.getText().toString();

    long pauseInt = 0;
    String pauseString = pause.getText().toString();
    try {
      pauseInt = Long.parseLong(pauseString);
    } catch (NumberFormatException e) {
      // Konvertierung fehlgeschlagen
    }

    ContentValues values = new ContentValues();
    values.put(Zeitabschnitte.Columns.START, ZeitabschnittContract.DB_DATE_FORMATTER.format(_StartDateTime.getTime()));
    values.put(Zeitabschnitte.Columns.STOP, ZeitabschnittContract.DB_DATE_FORMATTER.format(_EndDateTime.getTime()));
    values.put(Zeitabschnitte.Columns.PAUSE, pauseInt);
    values.put(Zeitabschnitte.Columns.COMMENT, commentString);

    // Daten speichern
    Uri uri = ContentUris.withAppendedId(Zeitabschnitte.CONTENT_URI, _Id);
    getContentResolver().update(uri, values, null, null);

    // Bearbeitung abschließen
    this.finish();
  }

  private void deleteData() {
    AlertDialog.Builder delDialog = new AlertDialog.Builder(this);
    delDialog.setTitle(R.string.dlg_delete_title).setMessage(R.string.dlg_delete_message)
            .setPositiveButton(R.string.dlg_delte_delete, new DialogInterface.OnClickListener() {

              @Override
              public void onClick(DialogInterface dialog, int which) {
                // Löschen des Datensatzes
                Uri uri = ContentUris.withAppendedId(Zeitabschnitte.CONTENT_URI, _Id);

                getContentResolver().delete(uri, null, null);

                EditActivity.this.finish();
              }
            }).setNegativeButton(R.string.dlg_delete_cancel, new DialogInterface.OnClickListener() {

              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
              }
            }).create().show();
  }

  private void initDialogs() {
    _Start.setOnLongClickListener(new OnLongClickListener() {

      @Override
      public boolean onLongClick(View v) {
        // Date Dialog aufrufen
        DatePickerDialog dateDialog = new DatePickerDialog(EditActivity.this, _StartDateListener, _StartDateTime.get(Calendar.YEAR), _StartDateTime
                .get(Calendar.MONTH), _StartDateTime.get(Calendar.DAY_OF_MONTH));
        dateDialog.show();
        return true;
      }
    });
    _StartZeit.setOnLongClickListener(new OnLongClickListener() {

      @Override
      public boolean onLongClick(View v) {
        // 24 Stunden im Sytem eingestellt
        boolean is24 = android.text.format.DateFormat.is24HourFormat(EditActivity.this);
        // Time Dialog aufrufen
        TimePickerDialog timeDialog = new TimePickerDialog(EditActivity.this, _StartTimeListener, _StartDateTime.get(Calendar.HOUR_OF_DAY), _StartDateTime
                .get(Calendar.MINUTE), is24);
        timeDialog.show();
        return true;
      }
    });
    _Ende.setOnLongClickListener(new OnLongClickListener() {

      @Override
      public boolean onLongClick(View v) {
        // Date Dialog aufrufen
        DatePickerDialog dateDialog = new DatePickerDialog(EditActivity.this, _EndDateListener, _EndDateTime.get(Calendar.YEAR), _EndDateTime
                .get(Calendar.MONTH), _EndDateTime.get(Calendar.DAY_OF_MONTH));
        dateDialog.show();
        return true;
      }
    });
    _EndeZeit.setOnLongClickListener(new OnLongClickListener() {

      @Override
      public boolean onLongClick(View v) {
        // 24 Stunden im Sytem eingestellt
        boolean is24 = android.text.format.DateFormat.is24HourFormat(EditActivity.this);
        // Time Dialog aufrufen
        TimePickerDialog timeDialog = new TimePickerDialog(EditActivity.this, _EndTimeListener, _EndDateTime.get(Calendar.HOUR_OF_DAY), _EndDateTime
                .get(Calendar.MINUTE), is24);
        timeDialog.show();
        return true;
      }
    });
  }
}
