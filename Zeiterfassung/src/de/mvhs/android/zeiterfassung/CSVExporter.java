package de.mvhs.android.zeiterfassung;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;

public class CSVExporter extends AsyncTask<Cursor, Integer, Void> {
  // Klassenvariablen
  private String         _ExportFileName;
  private ProgressDialog _Progress;

  public CSVExporter(String exportFileName, ProgressDialog dialog) {
    _ExportFileName = exportFileName;
    _Progress = dialog;
  }

  @Override
  protected void onCancelled() {
    if (_Progress != null && _Progress.isShowing()) {
      _Progress.dismiss();
    }
    super.onCancelled();
  }

  @Override
  protected void onPreExecute() {
    if (_Progress != null) {
      _Progress.show();
    }
    super.onPreExecute();
  }

  @Override
  protected void onPostExecute(Void result) {
    if (_Progress != null && _Progress.isShowing()) {
      _Progress.dismiss();
    }
    super.onPostExecute(result);
  }

  @Override
  protected void onProgressUpdate(Integer... values) {
    if (values != null && values.length > 0) {
      _Progress.setProgress(values[0]);
    }
    super.onProgressUpdate(values);
  }

  @Override
  protected Void doInBackground(Cursor... data) {
    // Variablen
    StringBuilder line = new StringBuilder();

    // PrŸfung, ob Daten da sind
    if (data != null && data.length == 1 && data[0] != null) {

      Cursor exportData = data[0];

      // Fortschrits-Maximum setzen
      _Progress.setMax(exportData.getCount() + 1);

      File exportPath = Environment.getExternalStorageDirectory();
      File exportFile = new File(exportPath, _ExportFileName + ".csv");

      if (exportPath.exists()) {
        BufferedWriter writer = null;
        try {
          writer = new BufferedWriter(new FileWriter(exportFile));

          // Auslesen der Spaltennamen aus dem Cursor
          String[] columnNames = exportData.getColumnNames();
          for (String column : columnNames) {
            if (line.length() > 0) {
              line.append(";");
            }
            line.append(column);
          }

          // Entfernen des Semikolons
          line.append("\n");

          // Speichern in die Datei
          writer.append(line);

          // Aktualisieren des Fortschritts
          publishProgress(new Integer[] { 1 });

          // Vor dem ersten Eintrag positionieren
          exportData.moveToPosition(-1);
          while (exportData.moveToNext() && isCancelled() == false) {
            // Lšschen des Zeilen-Platzhalters
            line.delete(0, line.length());

            for (int i = 0; i < columnNames.length; i++) {
              if (line.length() > 0) {
                line.append(";");
              }

              if (exportData.isNull(i)) {
                // Nichts Schreiben
              } else {
                // Wert der Spalte schreiben
                line.append(exportData.getString(i));
              }
            }

            line.append("\n");

            writer.append(line);

            // Fortschritt melden
            publishProgress(new Integer[] { exportData.getPosition() + 2 });
          }

        } catch (IOException e) {
        } finally {
          // SchlieÃŸen der Datei
          if (writer != null) {
            try {
              writer.flush();
              writer.close();
            } catch (IOException e) {
            }
          }
        }
      }
    }

    return null;
  }
}