package de.mvhs.android.zeiterfassung;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;

public class CsvAsyncTaskExporter extends AsyncTask<Cursor, Integer, Void> {
  private final Context  _context;
  private ProgressDialog _dialog = null;

  // Steuerung der Fortschrittsanzeige
  public CsvAsyncTaskExporter(Context context) {
    _context = context;
  }

  @Override
  protected void onPreExecute() {
    // Initialisierung der Fortschrittsanzeige
    _dialog = new ProgressDialog(_context);
    _dialog.setTitle("CSV Export ...");
    _dialog.setMessage("Daten werden als CSV exportiert!");
    _dialog.setIcon(R.drawable.ic_menu_set_as);
    _dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // Anzeige mit Fortschrittsbalken
    _dialog.show();

    super.onPreExecute();
  }

  @Override
  protected void onPostExecute(Void result) {
    super.onPostExecute(result);

    if (_dialog != null) {
      _dialog.dismiss();
      _dialog = null;
    }
  }

  @Override
  protected void onProgressUpdate(Integer... values) {
    if (values != null && values.length == 1 && _dialog != null) {
      _dialog.setProgress(values[0]);
    }
    super.onProgressUpdate(values);
  }

  @Override
  protected Void doInBackground(Cursor... params) {
    if (params != null && params.length == 1) {
      Cursor exportData = params[0];

      // Datei für den Export definieren
      File sdcardDirectory = Environment.getExternalStorageDirectory();
      File exportDirectory = new File(sdcardDirectory, "export");
      File exportFile = new File(exportDirectory, "zeit.csv");

      if (!exportDirectory.exists()) {
        exportDirectory.mkdirs();
      }

      BufferedWriter writer = null;

      try {
        // Writer initialisiern
        writer = new BufferedWriter(new FileWriter(exportFile));

        // Lesen der Spalten Namen aus Cursor
        String[] columnNames = exportData.getColumnNames();

        _dialog.setMax(exportData.getCount());

        // Melden des Fortschritts
        publishProgress(0);

        // Puffer für eine CSV zeile
        StringBuilder line = new StringBuilder();

        for (String columnName : columnNames) {
          if (line.length() > 0) {
            line.append(';');
          }
          line.append(columnName);
        }

        line.append('\n');

        // Schreiben der Zeile in die Datei
        writer.append(line);

        // Vor dem ersten Datensatz platzieren
        exportData.moveToPosition(-1);

        // Solange lesen, solange die Daten da sind
        while (exportData.moveToNext()) {
          // Zurücksetzen des Puffers
          line.delete(0, line.length());

          // Spaltenwerte auslesen
          for (int i = 0; i < columnNames.length; i++) {
            if (line.length() > 0) {
              line.append(';');
            }

            // Prüfen, ob Wert in der Spalte da ist
            if (exportData.isNull(i)) {
              line.append("<NULL>");
            } else {
              line.append(exportData.getString(i));
            }
          }

          // Neue Zeile hinzufügen
          line.append('\n');

          // Speichern der Daten
          writer.append(line);

          // Fortschritt melden
          Thread.sleep(5); // Künstliche Pause, um den Fortschritsdialog sehen zu können
          publishProgress(exportData.getPosition());
        }

      } catch (Exception e) {
      } finally {
        if (writer != null) {
          try {
            writer.flush();
            writer.close();
          } catch (IOException e) {
          }
        }
      }
    }

    return null;
  }

}
