package de.mvhs.android.zeiterfassung.dialogs;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.util.Calendar;

public class TimeLongClick implements View.OnLongClickListener {
    @NonNull
    private final Context _context;
    @NonNull
    private final TextView _outputView;
    @NonNull
    private final Calendar _date;
    private final String _title;
    final boolean is24h;
    private final DateFormat _timeFormatter;

    public TimeLongClick(@NonNull Context context, @NonNull TextView outputView, @NonNull Calendar date, String title) {
        _context = context;
        _outputView = outputView;
        _date = date;
        _title = title;
        is24h = android.text.format.DateFormat.is24HourFormat(context);
        _timeFormatter = android.text.format.DateFormat.getTimeFormat(context);

        _outputView.setText(_timeFormatter.format(_date.getTime()));
    }

    @Override
    public boolean onLongClick(View v) {

        TimePickerDialog dialog = new TimePickerDialog(
            _context, // Context
            new TimeSelected(), // Callback
            _date.get(Calendar.HOUR_OF_DAY), // 24h Stunden
            _date.get(Calendar.MINUTE), // Minuten
            is24h // 24h?
        );
        dialog.setTitle(_title);

        dialog.show();
        return false;
    }

    public class TimeSelected implements TimePickerDialog.OnTimeSetListener {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            _date.set(Calendar.HOUR_OF_DAY, hourOfDay);
            _date.set(Calendar.MINUTE, minute);
            _outputView.setText(_timeFormatter.format(_date.getTime()));
        }
    }
}
