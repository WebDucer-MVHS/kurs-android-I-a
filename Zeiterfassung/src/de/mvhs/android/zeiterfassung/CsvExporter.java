package de.mvhs.android.zeiterfassung;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import de.mvhs.android.zeiterfassung.db.Contract.Zeiten;

public class CsvExporter extends AsyncTask<Void, Integer, String> {
	private final Context _context;
	private ProgressDialog _dialog;

	public CsvExporter(Context context) {
		_context = context;
	}

	public CsvExporter(Context context, ProgressDialog dialog) {
		_context = context;
		_dialog = dialog;
	}

	@Override
	protected void onPreExecute() {

		if (_dialog == null) {
			_dialog = new ProgressDialog(_context);
			_dialog.setTitle(R.string.ExportTitle);
			_dialog.setMessage(_context.getString(R.string.ExportMessage));
			_dialog.setCancelable(false);
			// Anzeige als drehender Kreis
			// _dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			// Anzeige mit Fortschritt
			_dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		}

		_dialog.show();

		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		if (_dialog != null) {
			_dialog.dismiss();
			_dialog = null;
		}

		if (isCancelled() == false) {
			// Erfolgsdialog anzeigen
			AlertDialog.Builder builder = new AlertDialog.Builder(_context);
			builder.setTitle(R.string.ExportSuccessTitle).setMessage(result)
					.setPositiveButton(R.string.OkButtonText, null);

			builder.create().show();
		}

		super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		if (values != null && values.length == 1 && _dialog != null) {
			_dialog.setProgress(values[0]);
		}

		super.onProgressUpdate(values);
	}

	@Override
	protected void onCancelled(String result) {
		// Aufräumen der Daten
		File sdcardPath = Environment.getExternalStorageDirectory();
		File exportPath = new File(sdcardPath, "export");
		File exportFile = new File(exportPath, "zeiten.csv");

		if (exportFile.exists()) {
			exportFile.delete();
		}

		if (_dialog != null) {
			_dialog.dismiss();
			_dialog = null;
		}

		if (isCancelled() == true) {
			// Erfolgsdialog anzeigen
			AlertDialog.Builder builder = new AlertDialog.Builder(_context);
			builder.setTitle(R.string.ExportCanceled).setMessage(result)
					.setPositiveButton(R.string.OkButtonText, null);

			builder.create().show();
		}

		super.onCancelled(result);
	}

	@Override
	protected String doInBackground(Void... inputData) {

		String endDialogMessage = null;

		// Laden der Daten aus der Datenbank
		Cursor data = _context.getContentResolver().query(Zeiten.CONTENT_URI,
				null, null, null, Zeiten.Columns.START_TIME + " DESC");

		// Prüfen der Daten
		if (data != null) {
			// Setzen des Maximums (+1 für Kopfzeile)
			if (_dialog != null) {
				_dialog.setMax(data.getCount() + 1);
			}

			// Datei für den Export definieren
			File sdcardPath = Environment.getExternalStorageDirectory();

			// Prüfen, dass das externe Verzeichnis da und beschreibbar ist
			if (!Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())) {
				this.cancel(false);

				return _context.getString(R.string.ExportErrorNoSdCard);
			}

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

				// Fortschritt melden
				this.publishProgress(1);

				while (data.moveToNext() && isCancelled() == false) {
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

					// Fortschritt melden (+2, da 0 basierter Index + Kopfzeile)
					this.publishProgress(data.getPosition() + 2);

					// Künstliche Pause
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
					}
				}

				endDialogMessage = _context
						.getString(R.string.ExportSuccessful);

			} catch (IOException e) {
				endDialogMessage = _context
						.getString(R.string.ExportErrorFileSystem);
				cancel(false);
			} finally {
				// Datei-Resourcen freigeben
				if (writer != null) {
					try {
						writer.flush();
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				// Datenbank-Resourcen freigeben
				if (data != null) {
					data.close();
				}

				if (isCancelled() == true) {
					endDialogMessage = _context
							.getString(R.string.ExportCanceledMessage);
				}
			}

		}

		return endDialogMessage;
	}
}
