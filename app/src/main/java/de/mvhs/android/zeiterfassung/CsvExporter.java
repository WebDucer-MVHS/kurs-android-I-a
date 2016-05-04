package de.mvhs.android.zeiterfassung;

import android.content.Context;
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
public class CsvExporter extends AsyncTask<Void, Void, Void> {
    private Context _context;

    public CsvExporter(Context context){
        _context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // Ausführung vor der Hintergrundaufgabe (in UI Thread)
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        // Ausführung nach Beendigung der Hintergrundaufgabe (in UI Thread)
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

        // Zwischenstand der Hintergrundaufgabe (in UI Thread)
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        // Abbruch durch den Benutzer
        // Aufräumarbeiten !!!
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
        if (data == null || data.getCount() == 0) {
            return null;
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
                if (line.length() > 0){
                    line.append(";");
                }

                line.append(column);
            }

            // Neue Zeile hinzufügen
            line.append("\n");

            // Zeile in die Datei schreiben
            writer.append(line);

            while (data.moveToNext()){
                // Leeren der Zeileninhalte
                line.delete(0, line.length());

                // Spaltenwerte auslesen
                for (int columnIndex = 0; columnIndex < columns.length; columnIndex++){
                    if (line.length() > 0){
                        line.append(";");
                    }

                    if(data.isNull(columnIndex)){
                        line.append("<NULL>");
                    } else {
                        line.append(data.getString(columnIndex));
                    }
                }

                // Neue Zeile
                line.append("\n");

                // In die Datei speichern
                writer.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Resourcen für Writer freigeben
            if (writer != null){
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Cursor freigeben
            if (data != null){
                data.close();
            }
        }

        return null;
    }
}
