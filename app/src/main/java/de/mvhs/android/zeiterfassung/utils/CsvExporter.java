package de.mvhs.android.zeiterfassung.utils;

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

import de.mvhs.android.zeiterfassung.R;
import de.mvhs.android.zeiterfassung.db.ZeitContract;

/**
 * Created by kurs on 29.04.15.
 */
public class CsvExporter extends AsyncTask<Void, Integer, Void> {
    private Context _context;
    private ProgressDialog _dialog;

    public CsvExporter(Context context){
        _context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        _dialog = new ProgressDialog(_context);
        _dialog.setTitle(R.string.export_dialog_title);
        _dialog.setMessage(_context.getString(R.string.export_dialog_message));
        _dialog.setCancelable(false);
        _dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        _dialog.setButton(ProgressDialog.BUTTON_NEGATIVE, _context.getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancel(false);
            }
        });

        // Dialog anzeigen
        _dialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        if(_dialog == null || values == null || values.length != 1){
            return;
        }

        // Fortschritt melden
        _dialog.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (_dialog == null){
            return;
        }

        // Dialog schließen
        _dialog.dismiss();
        _dialog = null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        // Daten werden aufgeräumt
        if(_dialog != null){
            _dialog.setMessage(_context.getString(R.string.export_dialog_cancel_message));
        }

        // Datei löschen
        File exportFileName = new File(new File(Environment.getExternalStorageDirectory(), "export"), "ZeitDaten.csv");
        if(exportFileName.exists()){
            exportFileName.delete();
        }

        // Dialog schließen
        if(_dialog != null){
            _dialog.dismiss();
            _dialog = null;
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        // Laden der Daten aus der Datenbank
        Cursor data = _context.getContentResolver()
                .query(ZeitContract.ZeitDaten.CONTENT_URI, null, null, null, ZeitContract.ZeitDaten.Columns.START_TIME + " DESC");

        // Prüfen, ob Daten vorhanden sind
        if(data == null || data.getCount() == 0 || isCancelled()){
            if(data != null){
                data.close();
            }

            return null;
        }

        if(_dialog != null){
            _dialog.setMax(data.getCount() + 1); // +1 für Spaltennamen-Zeile
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

            publishProgress(1); // Spaltennamen geschrieben

            while (data.moveToNext() && !isCancelled()){
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

                publishProgress(data.getPosition() + 2); // Aktuelle Zeile geschrieben
                Thread.sleep(10l);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
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
