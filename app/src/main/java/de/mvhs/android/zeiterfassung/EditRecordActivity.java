package de.mvhs.android.zeiterfassung;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import de.mvhs.android.zeiterfassung.db.ZeitContract;
import de.mvhs.android.zeiterfassung.utils.Converter;


public class EditRecordActivity extends ActionBarActivity {
   private EditText _startDate;
   private EditText _startTime;
   private EditText _endDate;
   private EditText _endTime;
   private EditText _pause;
   private EditText _comment;

   // Rohdaten
   private Calendar _start = Calendar.getInstance();
   private Calendar _end = Calendar.getInstance();
   private long _id = -1;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_edit_record);

      // Init UI elements
      _startDate = (EditText) findViewById(R.id.StartDate);
      _startDate.setKeyListener(null);
      _startTime = (EditText) findViewById(R.id.StartTime);
      _startTime.setKeyListener(null);
      _endDate = (EditText) findViewById(R.id.EndDate);
      _endDate.setKeyListener(null);
      _endTime = (EditText) findViewById(R.id.EndTime);
      _endTime.setKeyListener(null);
      _pause = (EditText) findViewById(R.id.Pause);
      _comment = (EditText) findViewById(R.id.Comment);

      _id = getIntent().getLongExtra("ID", -1);

      if (_id > 0) {
         // Laden eines vorhandenen Datensatzes
         initFromDatabase(_id);
      }
   }

   @Override
   protected void onStart() {
      super.onStart();

      // Registrierung der Listener
      _startDate.setOnLongClickListener(new EditDateHelper(this, _start, _startDate));
      _startTime.setOnLongClickListener(new EditTimeHelper(this, _start, _startTime));
      _endDate.setOnLongClickListener(new EditDateHelper(this, _end, _endDate));
      _endTime.setOnLongClickListener(new EditTimeHelper(this, _end, _endTime));
   }

   @Override
   protected void onStop() {
      super.onStop();

      // Deregistrieren der Listener
      _startDate.setOnLongClickListener(null);
      _startTime.setOnLongClickListener(null);
      _endTime.setOnLongClickListener(null);
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menu_edit_record, menu);

      // Entfernen von Delete, wenn ein neuer Datensatz
      if (_id <= 0) {
         menu.removeItem(R.id.action_delete);
      }

      return super.onCreateOptionsMenu(menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case android.R.id.home:
            saveRecord(_id);
            break;

         case R.id.action_cancel:
            onBackPressed();
            break;

         case R.id.action_delete:
            deleteRecord(_id);
            break;
      }
      return super.onOptionsItemSelected(item);
   }

   @Override
   public void onBackPressed() {
      onSupportNavigateUp();
   }

   private void initFromDatabase(long id) {
      setTitle(getString(R.string.activity_title_edit));

      Uri uri = ContentUris.withAppendedId(ZeitContract.ZeitDaten.CONTENT_URI, id);
      Cursor data = getContentResolver().query(uri, null, null, null, null);

      if (data == null || !data.moveToFirst()) {
         return;
      }

      // Startzeit
      int columnIndex = data.getColumnIndex(ZeitContract.ZeitDaten.Columns.START_TIME);
      String startValue = data.getString(columnIndex);
      try {
         Date startDate = ZeitContract.Converters.DB_DATE_TIME_FORMATTER.parse(startValue);

         _start.setTimeInMillis(startDate.getTime());

         _startDate.setText(Converter.toDateString(startDate));
         _startTime.setText(Converter.toTimeString(startDate));
      } catch (ParseException e) {
         e.printStackTrace();
      }

      // Endzeit
      columnIndex = data.getColumnIndex(ZeitContract.ZeitDaten.Columns.END_TIME);
      String endValue = data.isNull(columnIndex) ? null : data.getString(columnIndex);
      if (endValue != null) {
         try {
            Date endDate = ZeitContract.Converters.DB_DATE_TIME_FORMATTER.parse(endValue);

            _end.setTimeInMillis(endDate.getTime());

            _endDate.setText(Converter.toDateString(endDate));
            _endTime.setText(Converter.toTimeString(endDate));
         } catch (ParseException e) {
            e.printStackTrace();
         }
      } else {
         _endDate.setText("");
         _endTime.setText("");
      }

      // Pause
      columnIndex = data.getColumnIndex(ZeitContract.ZeitDaten.Columns.PAUSE);
      int pause = data.getInt(columnIndex);
      _pause.setText(String.valueOf(pause));

      // Kommentar
      columnIndex = data.getColumnIndex(ZeitContract.ZeitDaten.Columns.COMMENT);
      String comment = data.isNull(columnIndex) ? null : data.getString(columnIndex);
      _comment.setText(comment == null ? "" : comment);
   }

   private void deleteRecord(final long id) {
      // Abfragedialog erstellen
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle(getString(R.string.button_delete)).setIcon(R.drawable.ic_menu_delete).setMessage(getString(R.string.delete_dialog_message)).setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            // Löschlogik
            Uri uri = ContentUris.withAppendedId(ZeitContract.ZeitDaten.CONTENT_URI, id);
            getContentResolver().delete(uri, null, null);
            onSupportNavigateUp();
         }
      }).setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            // Abbrechen
            dialog.dismiss();
         }
      });

      // Dialog anzeigen
      builder.create().show();
   }

   private void saveRecord(final long id) {
      // Prüfung, ob Werte vorhanden sind
      if (!hasValues()) {
         onSupportNavigateUp();
         return;
      }

      // Pause auslesen
      String pauseString = _pause.getText().toString();
      int pauseValue = 0;
      if (isEmptyOrNull(pauseString)) {
         pauseValue = 0;
      } else {
         try {
            pauseValue = Integer.parseInt(pauseString);
         } catch (NumberFormatException ex) {
            ex.printStackTrace();
            pauseValue = 0;
         }
      }

      ContentValues values = new ContentValues();
      values.put(ZeitContract.ZeitDaten.Columns.START_TIME, ZeitContract.Converters.DB_DATE_TIME_FORMATTER.format(_start.getTime()));
      values.put(ZeitContract.ZeitDaten.Columns.END_TIME, ZeitContract.Converters.DB_DATE_TIME_FORMATTER.format(_end.getTime()));
      values.put(ZeitContract.ZeitDaten.Columns.PAUSE, pauseValue);
      values.put(ZeitContract.ZeitDaten.Columns.COMMENT, _comment.getText().toString());


      // Update, Aktualisierung des Eintrags
      if (_id > 0) {
         Uri updateUri = ContentUris.withAppendedId(ZeitContract.ZeitDaten.CONTENT_URI, id);
         getContentResolver().update(updateUri, values, null, null);

      }
      // Insert, neuer Eintrag
      else {
         getContentResolver().insert(ZeitContract.ZeitDaten.CONTENT_URI, values);
      }


      onSupportNavigateUp();
   }

   private boolean hasValues() {
      if (isEmptyOrNull(_startDate.getText().toString())) {
         return false;
      }

      if (isEmptyOrNull(_startTime.getText().toString())) {
         return false;
      }

      if (isEmptyOrNull(_endDate.getText().toString())) {
         return false;
      }

      if (isEmptyOrNull(_endTime.getText().toString())) {
         return false;
      }

      return true;
   }

   private boolean isEmptyOrNull(String value) {
      if (value == null) {
         return true;
      }

      if ("".equals(value)) {
         return true;
      }

      if (" ".equals(value)) {
         return true;
      }

      return false;
   }
}
