package de.mvhs.android.zeiterfassung;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import de.mvhs.android.zeiterfassung.db.ZeitContract;

/**
 * Created by kurs on 11.11.15.
 */
public class CsvExporter extends AsyncTask<Void, Integer, Void> {

    private Context _context;

    public CsvExporter(Context context) {
        _context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected Void doInBackground(Void... params) {
        // Laden der Daten
        Cursor data = _context.getContentResolver().query(
                ZeitContract.ZeitDaten.CONTENT_URI, null, null, null, null);

        // Prüfen der Daten
        if (data == null) {
            return null;
        }

        if (data.getCount() == 0) {
            data.close();

            return null;
        }

        // Exportverzeichnis auf der SD-Karte
        File sdCardPath = Environment.getExternalStorageDirectory();

        // Prüfen auf die Schreibrechte / Beschreibbarkeit
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return null;
        }

        // Export Unterverzeichnis
        File exportPath = new File(sdCardPath, "export");
        // Export-Datei
        File exportFile = new File(exportPath, "ZeitDaten.csv");

        // Prüfen, ob Unterverzeichnis vorhanden ist
        if (!exportPath.exists()) {
            exportPath.mkdirs();
        }

        // Writer für die Datei
        BufferedWriter writer = null;

        try {
            // Writer initialisieren
            writer = new BufferedWriter(new FileWriter(exportFile));

            // Lesen der Spaltennamen
            String[] columns = data.getColumnNames();

            // Eine Zeile für CSV
            StringBuilder line = new StringBuilder();

            // Zusammensetzen der Zeile
            for (String columnName : columns) {
                if (line.length() > 0) {
                    line.append(";");
                }

                line.append(columnName);
            }

            // Neue Zeile hinzufügen
            line.append("\n");

            // Zeile schreiben
            writer.append(line);

            while (data.moveToNext()) {
                // Zeile zurücksetzen
                line.delete(0, line.length());

                // Spaltenwerte auslesen
                for (int columnIndex = 0; columnIndex < columns.length; columnIndex++) {
                    if (line.length() > 0) {
                        line.append(";");
                    }

                    // NULL Prüfung
                    if (data.isNull(columnIndex)) {
                        line.append("<NULL>");
                    } else {
                        line.append(data.getString(columnIndex));
                    }
                }

                // Neue Zeile
                line.append("\n");

                // Zeile schreiben
                writer.append(line);
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

            // Resourcen der Datenbank freigeben
            if(data != null){
                data.close();
            }
        }

        return null;
    }
}
