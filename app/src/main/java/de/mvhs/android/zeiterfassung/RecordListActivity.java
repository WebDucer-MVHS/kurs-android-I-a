package de.mvhs.android.zeiterfassung;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import de.mvhs.android.zeiterfassung.db.ZeitContract;


/**
 * Created by kurs on 28.10.15.
 */
public class RecordListActivity extends AppCompatActivity {
  /* Klassenvariablen */
  private ListView _list;
  private SimpleCursorAdapter _adapter;
  private final DateFormat _DATE_CONVERTER = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Einbinden des Layouts
    setContentView(R.layout.activity_list);

    _list = (ListView) findViewById(R.id.RecordList);

    // Initialisieren des Adapters
    _adapter = new SimpleCursorAdapter(
        getBaseContext(), // Context
        //android.R.layout.simple_list_item_2, // Layout für die Zeile
        R.layout.list_row, // Eigenes Laxout für die zweite Zeile
        null, // Daten für die Darstellung
        new String[]{ZeitContract.ZeitDaten.Columns.START, ZeitContract.ZeitDaten.Columns.END}, // Spalten
        new int[]{android.R.id.text1, android.R.id.text2}, // View IDs, in denen die Daten angezeigt werden
        SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
    );

    _adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
      @Override
      public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        if(!(view instanceof TextView)){
          return false;
        }

        if(cursor.isNull(columnIndex)){
          ((TextView)view).setText("---");
        } else{
          try {
            Date date = ZeitContract.Converters.DB_DATE_TIME_FORMATTER.parse(cursor.getString(columnIndex));
            ((TextView)view).setText(_DATE_CONVERTER.format(date));
          } catch (ParseException e) {
            ((TextView)view).setText("ERROR");
          }
        }

        return true;
      }
    });

    _list.setAdapter(_adapter);
  }

  @Override
  protected void onStart() {
    super.onStart();

    Cursor data = getContentResolver().query(ZeitContract.ZeitDaten.CONTENT_URI,
        null, null, null, null);

    _adapter.swapCursor(data);
  }
}
