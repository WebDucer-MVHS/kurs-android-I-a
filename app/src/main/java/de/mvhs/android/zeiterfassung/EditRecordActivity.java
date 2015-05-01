package de.mvhs.android.zeiterfassung;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.text.ParseException;
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

      long id = getIntent().getLongExtra("ID", -1);

      if (id > 0) {
         // Laden eines vorhandenen Datensatzes
         initFromDatabase(id);
      }
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menu_edit_record, menu);
      return super.onCreateOptionsMenu(menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()){
         case android.R.id.home:
            break;

         case R.id.action_cancel:
            break;

         case R.id.action_delete:
            break;
      }
      return super.onOptionsItemSelected(item);
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
}
