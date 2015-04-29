package de.mvhs.android.zeiterfassung.utils;

import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Date;

import de.mvhs.android.zeiterfassung.db.ZeitContract;

/**
 * Created by eugen on 25.04.15.
 */
public class ListViewBinder implements SimpleCursorAdapter.ViewBinder {
   @Override
   public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
      if (view instanceof TextView) {
         if (cursor.isNull(columnIndex)) {
            ((TextView) view).setText("---");
         } else {
            String value = cursor.getString(columnIndex);
            try {
               Date dateValue = ZeitContract.Converters.DB_DATE_TIME_FORMATTER.parse(value);
               ((TextView) view).setText(Converter.toDateTimeString(dateValue));
            } catch (ParseException e) {
               ((TextView) view).setText("ERROR");
            }
         }

         return true;
      }

      return false;
   }
}
