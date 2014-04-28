package de.mvhs.android.zeiterfassung;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;

public class CsvAsyncTaskExporter extends AsyncTask<Cursor, Integer, Void> {

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
