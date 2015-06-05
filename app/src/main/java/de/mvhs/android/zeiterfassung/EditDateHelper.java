package de.mvhs.android.zeiterfassung;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

import de.mvhs.android.zeiterfassung.utils.Converter;

/**
 * Created by eugen on 05.06.15.
 */
public class EditDateHelper implements View.OnLongClickListener {

   private final Context _context;
   private final Calendar _date;
   private final TextView _outputView;
   private final DatePickerDialog.OnDateSetListener _dateSelectedListener = new DateSelected();

   public EditDateHelper(Context context, Calendar date, TextView outputView) {
      _context = context;
      _date = date;
      _outputView = outputView;
   }


   @Override
   public boolean onLongClick(View v) {
      DatePickerDialog dialog = new DatePickerDialog(_context, _dateSelectedListener, _date.get(Calendar.YEAR), _date.get(Calendar.MONTH), _date.get(Calendar.DAY_OF_MONTH));

      dialog.show();

      return true;
   }

   private class DateSelected implements DatePickerDialog.OnDateSetListener {

      @Override
      public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
         _date.set(year, monthOfYear, dayOfMonth);

         _outputView.setText(Converter.toDateString(_date.getTime()));
      }
   }
}
