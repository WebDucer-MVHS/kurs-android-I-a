package de.mvhs.android.zeiterfassung;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;

public class CSVExporter extends AsyncTask<Cursor, Integer, Void> {
	// Klassenvariablen
	private String _ExportFileName;
	
	public CSVExporter(String exportFileName){
		_ExportFileName = exportFileName;
	}

	@Override
	protected Void doInBackground(Cursor... data) {
		// Variablen
		StringBuilder line = new StringBuilder();
		
		// Prüfung, ob Daten da sind
		if (data != null &&
			data.length == 1 &&
			data[0] != null) {
			
			Cursor exportData = data[0];
			
			File exportPath = Environment.getExternalStorageDirectory();
			File exportFile = new File(exportPath, _ExportFileName + ".csv");
			
			if (exportPath.exists()) {
				BufferedWriter writer = null;
				try {
					writer =
						new BufferedWriter(new FileWriter(exportFile));
					
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
					
					// Vor dem ersten Eintrag positionieren
					exportData.moveToPosition(-1);
					while (exportData.moveToNext()) {
						// Löschen des Zeilen-Platzhalters
						line.delete(0, line.length());
						
						for (int i = 0; i < columnNames.length; i++) {
							if (line.length() > 0) {
								line.append(";");
							}
							
							if (exportData.isNull(i)) {
								// Nichts Schreiben
							}
							else {
								// Wert der Spalte schreiben
								line.append(exportData.getString(i));
							}
						}
						
						line.append("\n");
						
						writer.append(line);
					}
					
				} catch (IOException e) {
				}
				finally{
					// Schließen der Datei
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
