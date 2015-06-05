package de.mvhs.android.zeiterfassung;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import de.mvhs.android.zeiterfassung.utils.Converter;

/**
 * Created by eugen on 05.06.15.
 */
public class EditTimeHelper implements View.OnLongClickListener {

   private Context _context;
   private final Calendar _time;
   private final TextView _outputView;
   private final TimePickerDialog.OnTimeSetListener _timeSelectedListener = new TimeSelected();

   public EditTimeHelper(Context context, Calendar time, TextView outputView) {
      _context = context;
      _time = time;
      _outputView = outputView;
   }

   @Override
   public boolean onLongClick(View v) {
      boolean is24 = android.text.format.DateFormat.is24HourFormat(_context);

      TimePickerDialog dialog = new TimePickerDialog(_context, _timeSelectedListener, _time.get(Calendar.HOUR_OF_DAY), _time.get(Calendar.MINUTE), is24);

      dialog.show();

      return true;
   }

   private class TimeSelected implements TimePickerDialog.OnTimeSetListener {
      @Override
      public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
         _time.set(Calendar.HOUR_OF_DAY, hourOfDay);
         _time.set(Calendar.MINUTE, minute);

         _outputView.setText(Converter.toTimeString(_time.getTime()));
      }
   }
}
