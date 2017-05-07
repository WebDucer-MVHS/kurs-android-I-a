package de.mvhs.android.zeiterfassung;

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

import de.mvhs.android.zeiterfassung.db.TimeContract;

/**
 * Created by Kurs on 03.05.2017.
 */

public class CsvExporter extends AsyncTask<Void, Integer, Void> {
    private final Context _context;

    private ProgressDialog _dialog;

    public CsvExporter(Context context){
        _context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        _dialog = new ProgressDialog(_context);
        _dialog.setTitle(_context.getString(R.string.ExportDialogTitle));
        _dialog.setMessage(_context.getString(R.string.ExportDialogMessage));
        _dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        _dialog.setCanceledOnTouchOutside(false);
        _dialog.setCancelable(true);
        _dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancel(false);
            }
        });
        _dialog.setButton(DialogInterface.BUTTON_NEGATIVE, _context.getString(R.string.ButtonCancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancel(false);
            }
        });

        _dialog.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (_dialog != null && _dialog.isShowing()) {
            _dialog.dismiss();
            _dialog.setOnCancelListener(null);
            _dialog = null;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (values != null && values.length == 1){
            _dialog.setProgress(values[0]);
        }
        super.onProgressUpdate(values);
    }

    @Override
    protected Void doInBackground(Void... params) {
        // Daten aus der Datenbank auslesen
        Cursor data = _context.getContentResolver()
                .query(TimeContract.TimeData.CONTENT_URI, null, null, null, null);

        // Prüfen, ob Daten da sind
        int maxCount = data == null ? 0 : data.getCount();
        if (maxCount == 0){
            return null;
        }

        // Max. in Dialog setzen
        _dialog.setMax(maxCount + 1); // +1 für Spaltenüberschriften

        // Writer für die Datei
        BufferedWriter writer = null;

        // Pfad für den Export
        File sdCard = Environment.getExternalStorageDirectory();

        // Prüfen, ob man darauf schreiben kann
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            return null;
        }

        // Export Unterverzeichnis
        File exportDirectory = new File(sdCard, "export");

        // Unterverzeichnis erzeugen, falls nicht vorhanden
        if (!exportDirectory.exists()){
            exportDirectory.mkdirs();
        }

        File exportFile = new File(exportDirectory, "time_data.csv");

        try {
            // Writer initialisieren
            writer = new BufferedWriter(new FileWriter(exportFile));

            // Auslesen der Spalten
            String[] columns = data.getColumnNames();

            // Representation einer Zeile in CSV
            StringBuilder line = new StringBuilder();

            // Zusammenfassen der Spalten in einer zeile
            for (String column : columns) {
                if (line.length() > 0){
                    line.append(";");
                }
                line.append(column);
            }

            // Neue Zeile hinzufügen
            line.append("\n");

            // Schreibenin die Datei
            writer.append(line);

            // Erste Zeile exportiert
            publishProgress(1);

            while (data.moveToNext() && !isCancelled()){
                // Zeile leeren
                line.delete(0, line.length());

                // Inhalt der Spalten ausgeben
                for (int columnIndex = 0; columnIndex < columns.length; columnIndex++) {
                    if (line.length() > 0){
                        line.append(";");
                    }

                    if(data.isNull(columnIndex)){
                        line.append("<NULL>");
                    } else {
                        line.append(data.getString(columnIndex));
                    }
                }

                line.append("\n");

                writer.append(line);

                // Datenzeile exportiert
                publishProgress(data.getPosition() + 2); // +1 für Überschriftenzeile +1 für 0-basierten Index

                Thread.sleep(500);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.flush();
                writer.close();

                data.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Löschen der Datei, wenn Abbruch durch den Benutzer
            if (isCancelled()){
                exportFile.delete();
            }
        }

        return null;
    }

















}