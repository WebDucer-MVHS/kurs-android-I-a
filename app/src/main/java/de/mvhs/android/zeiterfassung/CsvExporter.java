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

import de.mvhs.android.zeiterfassung.db.ZeitContract;

/**
 * Created by kurs on 11.11.15.
 */
public class CsvExporter extends AsyncTask<Void, Integer, Void> {

  private final Context _context;
  private ProgressDialog _dialog;

  public CsvExporter(Context context) {
    _context = context;
  }

  @Override
  protected void onPreExecute() {
    // Starten des Fortschrittsdialoges
    _dialog = new ProgressDialog(_context);
    _dialog.setTitle(R.string.eaxort_dialog_title); // Dialog Titel
    _dialog.setMessage(_context.getString(R.string.export_dialog_message)); // Dialog Text
    _dialog.setCancelable(false); // Dialog kann nicht über Touch neben dem Dialog geschlossen werden
    _dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // Fortschritsdialog
    _dialog.setButton(ProgressDialog.BUTTON_NEGATIVE, _context.getString(R.string.export_dialog_cancel), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        cancel(false); // Abbruch anleiten
      }
    });

    // Dialog anzeigen
    _dialog.show();

    super.onPreExecute();
  }

  @Override
  protected void onPostExecute(Void aVoid) {
    super.onPostExecute(aVoid);

    // Dialog schließen, falls offen
    if (_dialog != null && _dialog.isShowing()) {
      _dialog.dismiss();
      _dialog = null;
    }
  }

  @Override
  protected void onProgressUpdate(Integer... values) {
    super.onProgressUpdate(values);

    // Prüfen des Wertes
    if (_dialog == null || values == null || values.length != 1) {
      return;
    }

    // Aktuellen Fortschritt im Dialog anzeigen
    _dialog.setProgress(values[0]);
  }

  @Override
  protected void onCancelled() {
    super.onCancelled();

    // Abbruch vorbereiten
    if (_dialog != null) {
      _dialog.setMessage(_context.getString(R.string.export_dialog_cancel_message));
    }

    // Datei löschen
    File exportFileName = new File(new File(Environment.getExternalStorageDirectory(), "export"), "ZeitDaten.csv");
    if (exportFileName.exists()) {
      exportFileName.delete();
    }

    // Dialog schließen
    if (_dialog != null) {
      _dialog.dismiss();
      _dialog = null;
    }
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

    final int count = data.getCount();
    int lastProgress = 0;

    if (count == 0) {
      data.close();

      return null;
    }

    // Dialog mit maximaler Anzahl der Datensätze befüllen (reale Zahl)
    if (_dialog != null) {
      _dialog.setMax(count + 1); // +1 für die Spaltenüberschriften-Zeile
    }

    // Dialog mit maximaler Anzahl befüllen (100 %)
    // if(_dialog != null){
    //   _dialog.setMax(100); // 100 %
    // }

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

      publishProgress(1); // Spaltenüberschriften

      while (data.moveToNext() && !isCancelled()) {
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

        // Fortschritt melden
        publishProgress(data.getPosition() + 2); // 0 basierter index + 1 Zeile für die Überschriften

        // Fortschritt in %
//        int current = (data.getPosition() + 1) / count * 100;
//        if(current > lastProgress){
//          lastProgress = current;
//          publishProgress(lastProgress);
//        }
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
      if (data != null) {
        data.close();
      }
    }

    return null;
  }
}
