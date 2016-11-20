package de.mvhs.android.zeiterfassung;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;

import de.mvhs.android.zeiterfassung.db.TimeDataContract;

/**
 * Created by eugen on 06.11.16.
 */

public class EditActivity extends AppCompatActivity {
  public final static String ID_KEY = "EditItemId";
  private final static String[] _COLUMNS = {
      TimeDataContract.TimeData.Columns.START, // Index 0
      TimeDataContract.TimeData.Columns.END, // Index 1
      TimeDataContract.TimeData.Columns.PAUSE, // Index 2
      TimeDataContract.TimeData.Columns.COMMENT // Index 3
  };
  private final static int _START_INDEX = 0;
  private final static int _END_INDEX = 1;
  private final static int _PAUSE_INDEX = 2;
  private final static int _COMMENT_INDEX = 3;

  private long _id = -1;
  private Calendar _startDateTimeValue;
  private Calendar _endDatTimeValue;
  private EditText _startDate;
  private DateFormat _dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT);
  private DateFormat _timeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT);
  private EditText _startTime;
  private EditText _endDate;
  private EditText _endTime;
  private EditText _pause;
  private EditText _comment;

  private boolean _cancelled = false;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_grid);

    // Auslesen der ID aus Metainformationen
    _id = getIntent().getLongExtra(ID_KEY, -1);

    // Initialisieren der UI Elemente
    _startDate = (EditText) findViewById(R.id.StartDateValue);
    _startDate.setKeyListener(null);
    _startTime = (EditText) findViewById(R.id.StartTimeValue);
    _startTime.setKeyListener(null);
    _endDate = (EditText) findViewById(R.id.EndDateValue);
    _endDate.setKeyListener(null);
    _endTime = (EditText) findViewById(R.id.EndTimeValue);
    _endTime.setKeyListener(null);
    _pause = (EditText) findViewById(R.id.PauseValue);
    _comment = (EditText) findViewById(R.id.CommentValue);
  }

  @Override
  protected void onStart() {
    super.onStart();

    // Unterscheidung, neuer oder vorhandener Datensatz
    if (_id == -1) {
      initNewEntry();
    } else {
      loadData();
    }
  }

  // Speichern beim Verlassen der Activity


  @Override
  protected void onStop() {
    // Speichern, nur wenn nicht abgebrochen
    if (!_cancelled) {
      saveData();
    }

    super.onStop();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.mneu_edit, menu);
    // Löschen nur bei vorhandenen Einträgen erlauben
    if (_id == -1) {
      MenuItem deleteButton = menu.findItem(R.id.MenuItemDelete);
      deleteButton.setVisible(false);
    }

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.MenItemCancel:
        _cancelled = true;
        this.finish();
        return true;

      case R.id.MenuItemDelete:
        // Löschen-Logik
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
            .setTitle(R.string.DeleteDialogTitle)
            .setMessage(R.string.DeleteDialogMessage)
            .setNegativeButton(R.string.DialogCancelButton, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
              }
            })
            .setPositiveButton(R.string.DialogDeleteButton, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                // Jetzt in der Datenbank löschen
                Uri deleteUri =
                    ContentUris.withAppendedId(TimeDataContract.TimeData.CONTENT_URI, _id);

                getContentResolver().delete(deleteUri, null, null);
                _cancelled = true;
                EditActivity.this.finish();
              }
            });

        builder.create().show();
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void loadData() {
    // Vorhandener Datensatz
    Uri dataUri =
        ContentUris.withAppendedId(TimeDataContract.TimeData.CONTENT_URI, _id);

    Cursor data = getContentResolver().query(dataUri, _COLUMNS, null, null, null);

    // Daten lesen
    if (data.moveToFirst()) {
      try {
        // Startzeit auslesen
        String startValueFromDb = data.getString(_START_INDEX);
        // Startzeit umwandeln
        _startDateTimeValue = TimeDataContract.Converter.parseFromDb(startValueFromDb);

        // Ausgabe an der Oberfläche
        _startDate.setText(_dateFormatter.format(_startDateTimeValue.getTime()));
        _startTime.setText(_timeFormatter.format(_startDateTimeValue.getTime()));

        if (data.isNull(_END_INDEX)) {
          _endDate.setText("");
          _endTime.setText("");
        } else {
          // Endzeit auslesen
          String endValueFromDb = data.getString(_END_INDEX);
          // Endzeit umwandeln
          _endDatTimeValue = TimeDataContract.Converter.parseFromDb(endValueFromDb);

          // Ausgabe an der Oberfläche
          _endDate.setText(_dateFormatter.format(_endDatTimeValue.getTime()));
          _endTime.setText(_timeFormatter.format(_endDatTimeValue.getTime()));
        }

        // Pause auslesen
        _pause.setText(String.valueOf(data.getInt(_PAUSE_INDEX)));

        // Kommentar auslesen
        if (data.isNull(_COMMENT_INDEX)) {
          _comment.setText("");
        } else {
          _comment.setText(data.getString(_COMMENT_INDEX));
        }
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    data.close();
  }

  private void initNewEntry() {
    // Titel setzen
    setTitle(R.string.NewDataActivityTitle);

    // Initialisierng für auf die aktuelle Zeit
    _startDateTimeValue = Calendar.getInstance();
    _endDatTimeValue = Calendar.getInstance();

    // Ausgabe an der Oberfläche
    _startDate.setText(_dateFormatter.format(_startDateTimeValue.getTime()));
    _startTime.setText(_timeFormatter.format(_startDateTimeValue.getTime()));

    _endDate.setText(_dateFormatter.format(_endDatTimeValue.getTime()));
    _endTime.setText(_timeFormatter.format(_endDatTimeValue.getTime()));

    _pause.setText("0");

    _comment.setText("");
  }

  private void saveData() {
    // Daten sammeln
    ContentValues values = new ContentValues();
    // Startzeit
    values.put(TimeDataContract.TimeData.Columns.START,
        TimeDataContract.Converter.formatForDb(_startDateTimeValue));
    // Endzeit
    values.put(TimeDataContract.TimeData.Columns.END,
        TimeDataContract.Converter.formatForDb(_endDatTimeValue));
    // Pause
    String pauseStringValue = _pause.getText().toString();
    if (pauseStringValue != null && !pauseStringValue.isEmpty()) {
      // Pause gesetzt
      values.put(TimeDataContract.TimeData.Columns.PAUSE, Integer.parseInt(pauseStringValue));
    } else {
      values.put(TimeDataContract.TimeData.Columns.PAUSE, 0);
    }
    // Kommentar
    String commentValue = _comment.getText().toString();
    if (commentValue != null && !commentValue.isEmpty()) {
      values.put(TimeDataContract.TimeData.Columns.COMMENT, commentValue);
    } else {
      values.put(TimeDataContract.TimeData.Columns.COMMENT, "");
    }

    // Neuen Eintrag hinzugügen, oder vorhandenes aktualisieren
    if (_id == -1) {
      // Neues hinzufügen
      getContentResolver().insert(TimeDataContract.TimeData.CONTENT_URI, values);
    } else {
      // Vorhandenes aktualisieren
      Uri updateUri = ContentUris.withAppendedId(TimeDataContract.TimeData.CONTENT_URI, _id);
      getContentResolver().update(updateUri, values, null, null);
    }
  }
}
