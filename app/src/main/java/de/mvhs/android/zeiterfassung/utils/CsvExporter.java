package de.mvhs.android.zeiterfassung.utils;

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
 * Created by kurs on 29.04.15.
 */
public class CsvExporter extends AsyncTask<Void, Integer, Void> {
    private Context _context;

    public CsvExporter(Context context){

        _context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        // Laden der Daten aus der Datenbank
        Cursor data = _context.getContentResolver()
                .query(ZeitContract.ZeitDaten.CONTENT_URI, null, null, null, ZeitContract.ZeitDaten.Columns.START_TIME + " DESC");

        // Prüfen, ob Daten vorhanden sind
        if(data == null || data.getCount() == 0){
            if(data != null){
                data.close();
            }

            return null;
        }

        // Export-Datei definieren
        File sdCardPath = Environment.getExternalStorageDirectory();

        // Prüfen, ob externer Bereich beschreibbar ist
        if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            return null;
        }

        // Vollständiger Dateipfad
        File exportPath = new File(sdCardPath, "export");
        File exportFile = new File(exportPath, "ZeitDaten.csv");

        // Prüfen, ob Verzeichnis bereits existiert
        if(!exportPath.exists()){
            exportPath.mkdirs();
        }

        BufferedWriter writer = null;

        try {
            // Schreiber initialisieren
            writer = new BufferedWriter(new FileWriter(exportFile));

            // Lesen der verfügbaren Spalten aus Cursor
            String[] columns = data.getColumnNames();

            // Eine zeile für CSV
            StringBuilder line = new StringBuilder();

            // Spaltennamen ausgeben
            for (String columnName : columns){
                line.append(columnName)
                        .append(",");
            }

            // Neue Zeile anfangen
            line.append("\n");

            // Zeile wegschreiben
            writer.append(line);

            while (data.moveToNext()){
                // Zeile zurücksetzen
                line.delete(0, line.length());

                // Spaltenwerte auslesen
                for (int i = 0; i < columns.length; i++) {
                    // NULL Prüfung
                    if(data.isNull(i)){
                        line.append("<NULL>")
                                .append(",");
                    } else {
                        line.append(data.getString(i))
                                .append(",");
                    }
                }

                // Neue Zeile anfangen
                line.append("\n");

                // Wegschreiben der zeile
                writer.append(line);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(writer != null){
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (data != null){
                data.close();
            }
        }

        return null;
    }
}
