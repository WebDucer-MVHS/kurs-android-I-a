package de.mvhs.android.zeiterfassung;

import android.app.ProgressDialog;
import android.content.Context;
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
        _dialog.setTitle("Export ...");
        _dialog.setMessage("Daten werden exportiert!");
        _dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        _dialog.setCanceledOnTouchOutside(false);
        _dialog.setCancelable(false);

        _dialog.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (_dialog != null && _dialog.isShowing()) {
            _dialog.dismiss();
            _dialog = null;
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        // Daten aus der Datenbank auslesen
        Cursor data = _context.getContentResolver()
                .query(TimeContract.TimeData.CONTENT_URI, null, null, null, null);

        // Prüfen, ob Daten da sind
        if (data == null || data.getCount() == 0){
            return null;
        }

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

            while (data.moveToNext()){
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
        }

        return null;
    }

















}