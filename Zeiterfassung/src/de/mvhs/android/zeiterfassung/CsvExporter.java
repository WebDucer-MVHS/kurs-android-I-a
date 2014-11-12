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
import de.mvhs.android.zeiterfassung.db.Contract.Zeiten;

public class CsvExporter extends AsyncTask<Void, Integer, Void> {
	private final Context _context;
	private ProgressDialog _dialog;

	public CsvExporter(Context context) {
		_context = context;
	}

	@Override
	protected void onPreExecute() {

		_dialog = new ProgressDialog(_context);
		_dialog.setTitle("CSV Export ...");
		_dialog.setMessage("Daten werden als CSV exportiert!");
		_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		_dialog.show();

		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(Void result) {
		if (_dialog != null) {
			_dialog.dismiss();
			_dialog = null;
		}

		super.onPostExecute(result);
	}

	@Override
	protected Void doInBackground(Void... arg0) {

		// Laden der Daten aus der Datenbank
		Cursor data = _context.getContentResolver().query(Zeiten.CONTENT_URI,
				null, null, null, Zeiten.Columns.START_TIME + " DESC");

		// Prüfen der Daten
		if (data != null) {

			// Datei für den Export definieren
			File sdcardPath = Environment.getExternalStorageDirectory();
			File exportPath = new File(sdcardPath, "export");
			File exportFile = new File(exportPath, "zeiten.csv");

			// Prüfen, ob export Verzeichnis existiert
			if (!exportPath.exists()) {
				exportPath.mkdirs();
			}

			BufferedWriter writer = null;

			try {
				// Schreiber initialisieren
				writer = new BufferedWriter(new FileWriter(exportFile));

				// Lesen der verfügbaren Spalten in Cursor
				String[] columns = data.getColumnNames();

				// Eine Zeile in CSV
				StringBuilder line = new StringBuilder();

				// Spalten Namen ausgeben
				for (String column : columns) {
					line.append(column).append(';');
				}

				// Neue Zeile anfangen
				line.append('\n');

				// Zeile speichern
				writer.append(line);

				while (data.moveToNext()) {
					// Löschen der alten Zeile
					line.delete(0, line.length());

					// Spaltenwerte auslesen
					for (int i = 0; i < columns.length; i++) {
						// NULL Prüfung des Wertes
						if (data.isNull(i)) {
							line.append("<NULL>").append(';');
						} else {
							line.append(data.getString(i)).append(';');
						}
					}

					// Neue Zeile hinzufügen
					line.append('\n');

					// Daten speichern
					writer.append(line);
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				// Datei-Resourcen freigeben
				if (writer != null) {
					try {
						writer.flush();
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// Datenbank-Resourcen freigeben
				if (data != null) {
					data.close();
				}
			}

		}

		return null;
	}
}
