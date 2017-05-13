package de.mvhs.android.zeiterfassung.dialogs;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

public class DateLongClick implements View.OnLongClickListener {
    private final Context _context;
    private final DateFormat _dateFormatter;
    private final TextView _outputView;
    private final Calendar _date;
    private final String _title;

    public DateLongClick(@NonNull Context context, @NonNull TextView outputView, @NonNull Calendar date, String title) {
        _context = context;
        _dateFormatter = android.text.format.DateFormat.getDateFormat(context);
        _outputView = outputView;
        _date = date;
        _title = title;

        _outputView.setText(_dateFormatter.format(_date.getTime()));
    }

    @Override
    public boolean onLongClick(View v) {
        // Starten des Datumsdialoges
        DatePickerDialog dialog = new DatePickerDialog(
            _context, // Context
            new DateSelected(), // Callback
            _date.get(Calendar.YEAR), // Jahr
            _date.get(Calendar.MONTH), // Monat
            _date.get(Calendar.DAY_OF_MONTH)); // Tag im Monat
        dialog.setTitle(_title);
        dialog.show();
        return true;

    }

    public class DateSelected implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            _date.set(year, month, dayOfMonth);
            _outputView.setText(_dateFormatter.format(_date.getTime()));
        }
    }
}
