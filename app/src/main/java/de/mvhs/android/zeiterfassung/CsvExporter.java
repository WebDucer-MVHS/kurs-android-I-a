package de.mvhs.android.zeiterfassung;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import db.TimelogContract;

/**
 * Created by kurs on 04.05.16.
 */
public class CsvExporter extends AsyncTask<Void, Integer, Void> {
    private Context _context;
    private ProgressDialog _dialog;

    public CsvExporter(Context context) {
        _context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // Initialisierung des Dialoges
        _dialog = new ProgressDialog(_context);
        _dialog.setTitle(_context.getString(R.string.export_dialog_title));
        _dialog.setMessage(_context.getString(R.string.export_dialog_message));
        _dialog.setCancelable(false); // Dialog über Antippen neben dem Dialog abbrechen
        _dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // Fortschritsbalken
        _dialog.setButton(ProgressDialog.BUTTON_NEGATIVE, _context.getString(R.string.export_dialog_cancel), new DatePickerDialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancel(false); // Abbruch anstossen
            }
        });

        // Dialog anzeigen
        _dialog.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        // Dialog schließen, falls geöffnet
        if (_dialog != null && _dialog.isShowing()) {
            _dialog.dismiss();
            _dialog = null;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        // Prüfen der Werte
        if (_dialog == null || values == null || values.length != 1) {
            return;
        }

        // Aktuellen Wert setzen
        _dialog.setProgress(values[0]);
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        // Export-Datei löschen
        File exportFileName = new File(new File(Environment.getExternalStorageDirectory(), "export"), "Timelog.csv");
        if (exportFileName.exists()) {
            exportFileName.delete();
        }

        // Dialog schließen
        if (_dialog != null && _dialog.isShowing()) {
            _dialog.dismiss();
            _dialog = null;
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        // Laden der Daten aus der Datenbank
        Cursor data = _context.getContentResolver().query(
                TimelogContract.Timelog.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Prüfen der Daten
        if (data == null) {
            return null;
        }

        final int count = data.getCount();
        int lastProgress = 0;

        if (count == 0) {
            data.close();
            return null;
        }

        // Dialog mit Maximalwert belegen
        if (_dialog != null) {
            _dialog.setMax(100); // 100%
        }

        // Verzeichnis der SD-Karte bestimmen
        File sdCardPath = Environment.getExternalStorageDirectory();

        // Prüfen auf die Schreibrechte / Beschreibbarkeit
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return null;
        }

        // Export-Unterverzeichnis
        File exportPath = new File(sdCardPath, "export");

        // Export Datei
        File exportFile = new File(exportPath, "Timelog.csv");

        // Prüfen, ob alle Unterverzeichnisse bereits da sind
        if (!exportPath.exists()) {
            exportPath.mkdirs();
        }

        // Writer für die Datei
        BufferedWriter writer = null;

        try {
            // Writer initialisieren
            writer = new BufferedWriter(new FileWriter(exportFile));

            // Lesen der Spalten Namen
            String[] columns = data.getColumnNames();

            // Eine Zeile für CSV
            StringBuilder line = new StringBuilder();

            // Zusammensetzen der Zeile mit Spaltennamen
            for (String column : columns) {
                if (line.length() > 0) {
                    line.append(";");
                }

                line.append(column);
            }

            // Neue Zeile hinzufügen
            line.append("\n");

            // Zeile in die Datei schreiben
            writer.append(line);

            // Fortschritt in %
            int current = 1 / count * 100;
            if(current > lastProgress){
                lastProgress = current;
                publishProgress(lastProgress);
            }

            while (data.moveToNext() && !isCancelled()) {
                // Leeren der Zeileninhalte
                line.delete(0, line.length());

                // Spaltenwerte auslesen
                for (int columnIndex = 0; columnIndex < columns.length; columnIndex++) {
                    if (line.length() > 0) {
                        line.append(";");
                    }

                    if (data.isNull(columnIndex)) {
                        line.append("<NULL>");
                    } else {
                        line.append(data.getString(columnIndex));
                    }
                }

                // Neue Zeile
                line.append("\n");

                // In die Datei speichern
                writer.append(line);

                // Fortschritt in %
                current = 100 * (data.getPosition() + 2) / count;
                if(current > lastProgress){
                    lastProgress = current;
                    publishProgress(lastProgress);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Resourcen für Writer freigeben
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Cursor freigeben
            if (data != null) {
                data.close();
            }
        }

        return null;
    }
}
