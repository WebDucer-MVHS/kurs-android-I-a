package de.mvhs.android.zeiterfassung;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;

public class CsvExportTask extends AsyncTask<Cursor, Integer, Void> {
	private final ProgressDialog _Dialog;

	public CsvExportTask(ProgressDialog progressDialog) {
		_Dialog = progressDialog;
	}

	@Override
	protected void onPreExecute() {
		if (_Dialog != null) {
			_Dialog.show();
		}
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(Void result) {
		if (_Dialog != null) {
			_Dialog.dismiss();
		}
		super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		if (values != null && values.length == 1 && _Dialog != null) {
			_Dialog.setProgress(values[0]);
		}
		super.onProgressUpdate(values);
	}

	@Override
	protected Void doInBackground(Cursor... exportCursor) {
		// Prüfung des Parameters
		if (exportCursor != null && exportCursor.length == 1) {
			Cursor data = exportCursor[0];

			publishProgress(0);

			// Dateinamen für den Export definieren
			File externalDirectory = Environment.getExternalStorageDirectory();
			File exportDirectory = new File(externalDirectory, "export");
			File exportFile = new File(exportDirectory, "zeit.csv");

			if (externalDirectory.exists()) {
				BufferedWriter writer = null;

				try {
					exportDirectory.mkdirs();

					writer = new BufferedWriter(new FileWriter(exportFile));

					String[] columnNames = data.getColumnNames();
					StringBuilder line = new StringBuilder();

					// Spaltenüberschriften ausgeben
					for (String column : columnNames) {
						if (line.length() > 0) {
							line.append(";");
						}
						line.append(column);
					}
					// Neue Zeile einfügen
					line.append("\n");

					// Speichern in die Datei
					writer.append(line);

					// Cursor zurücksetzen
					data.moveToPosition(-1);
					while (data.moveToNext()) {
						// Zurücksetzen des String Builders
						line.delete(0, line.length());

						// Spaltenwerte ausgeben
						for (int i = 0; i < columnNames.length; i++) {
							if (line.length() > 0) {
								line.append(";");
							}

							if (data.isNull(i)) {
								// Nichts tun
							} else {
								// Wert ausgeben
								line.append(data.getString(i));
							}
						}

						// Neue Zeile hinzufügen
						line.append("\n");

						// Daten Speichern
						writer.append(line);

						publishProgress(data.getPosition() + 1);
					}

				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					// Datei wieder freigeben
					if (writer != null) {
						try {
							writer.flush();
							writer.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return null;
	}

}
