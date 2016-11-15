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

import de.mvhs.android.zeiterfassung.db.TimeDataContract;

/**
 * Created by eugen on 09.11.16.
 */

public class CsvExporter extends AsyncTask<Void, Integer, Void> {
  private final Context _context;
  private ProgressDialog _dialog;
  private File _exportPath;
  private File _exportFile;
  private File _sdCardPath;

  public CsvExporter(Context context) {
    _context = context;
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();

    // Dialog anzeigen
    _dialog = new ProgressDialog(_context);
    _dialog.setTitle(_context.getString(R.string.ExportDialogTitle));
    _dialog.setMessage(_context.getString(R.string.ExportDialogMessage));
    _dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    _dialog.setCancelable(false);
    _dialog.setButton(DialogInterface.BUTTON_NEGATIVE, _context.getString(R.string.CancelButton), new OnCanceled());
    _dialog.show();
  }

  @Override
  protected void onPostExecute(Void aVoid) {
    super.onPostExecute(aVoid);

    // Dialog schließen
    _dialog.dismiss();
    _dialog = null;
  }

  @Override
  protected void onProgressUpdate(Integer... values) {
    super.onProgressUpdate(values);
    _dialog.setProgress(values[0]);
  }

  @Override
  protected void onCancelled() {
    super.onCancelled();

    // Datei löschen, wenn der Export abgebrochen wurde
    if (_exportFile != null && _exportFile.exists()) {
      _exportFile.delete();
    }
  }

  @Override
  protected Void doInBackground(Void... params) {
    // Laden der Daten aus der Datenbank
    Cursor data = _context.getContentResolver().query(
        TimeDataContract.TimeData.CONTENT_URI,
        null,
        null,
        null,
        null
    );

    // Prüfen der Daten
    if (data == null) {
      return null;
    }

    // Maximale Anzahl der Datensätze setzen
    int count = data.getCount();
    _dialog.setMax(count + 1); // +1 für die Spaltenzeile

    // Verzeichnis der SD-Karte bestimmen
    _sdCardPath = Environment.getExternalStorageDirectory();

    // Prüfen auf die Schreibrechte / Beschreibbarkeit
    if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
      return null;
    }

    // Export-Unterverzeichnis
    _exportPath = new File(_sdCardPath, "export");

    // Export Datei
    _exportFile = new File(_exportPath, "TimeDataLog.csv");

    // Prüfen, ob alle Unterverzeichnisse bereits da sind
    if (!_exportPath.exists()) {
      _exportPath.mkdirs();
    }

    // Export abbrechen, falls vom Benutzer gewünscht
    if (isCancelled()) {
      return null;
    }

    // Writer für die Datei
    BufferedWriter writer = null;

    try {
      // Writer initialisieren
      writer = new BufferedWriter(new FileWriter(_exportFile));

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

      // Aktuellen Status senden
      publishProgress(1); // Spalten-Zeile

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

        // Aktuellen Status senden
        publishProgress(data.getPosition() + 2); // +1 für 0 basierten Index, +1 für die Spalten-Zeile

        // Verlangsamen des Exports, um wirklich den Dialog sehen zu können
        try {
          Thread.sleep(250);
        } catch (InterruptedException e) {
          e.printStackTrace();
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

  class OnCanceled implements DialogInterface.OnClickListener {

    @Override
    public void onClick(DialogInterface dialog, int which) {
      cancel(false);
    }
  }
}
