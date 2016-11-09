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

import de.mvhs.android.zeiterfassung.db.TimeDataContract;

/**
 * Created by eugen on 09.11.16.
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

    // Dialog anzeigen
    _dialog = new ProgressDialog(_context);
    _dialog.setTitle("Export ...");
    _dialog.setMessage("Daten werden exportiert!");
    _dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
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

    // Verzeichnis der SD-Karte bestimmen
    File sdCardPath = Environment.getExternalStorageDirectory();

    // Prüfen auf die Schreibrechte / Beschreibbarkeit
    if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
      return null;
    }

    // Export-Unterverzeichnis
    File exportPath = new File(sdCardPath, "export");

    // Export Datei
    File exportFile = new File(exportPath, "TimeDataLog.csv");

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
}
