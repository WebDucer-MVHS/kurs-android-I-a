/**
 * 
 */
package de.mvhs.zeit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import android.database.Cursor;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

/**
 * @author kurs
 * Klasse für den Hintergrundprozess des Exports
 */
public class CsvExport extends Thread {
	// Klassenvariablen
	private Handler _handler; // Handler für die Kommunikation mit dem Dialog
	private Cursor _cursor; // Cursor mit Daten
	private String _fileName; // Dateiname für Export
	
	private int _total = 100; // Maximaler Fortschritt
	private int _current = 0; // Aktueller Fortschritt
	
	private final static char _NL = '\n';
	
	/**
	 * Konstruktor
	 */
	public CsvExport(Handler handler, Cursor cursor, String fileName)
	{
		_handler = handler;
		_cursor = cursor;
		_fileName = fileName;
	}
	
	/**
	 * Ausführungslogik
	 */
	public void run()
	{
		int count = _cursor.getCount(); // Anzahl der Datensätze
		String[] columnNames = _cursor.getColumnNames(); // Spalten
		int[] columnIndex = new int[columnNames.length];
		int progress = 0;
		double progressStep = 100d / count;
		Message message = _handler.obtainMessage();
		
		BufferedWriter writeFile = null;
		File path = Environment.getExternalStorageDirectory(); // Externes Speichermedium (SD-Karte usw.)
		path = new File(path, "export"); // Export-Verzeichnis
		
		File exportFile = new File(path, _fileName); // Export-Datei
		
		StringBuilder line = new StringBuilder();
		for (int i = 0; i < columnNames.length; i++) {
			line.append(",")
				.append(columnNames[i]);
			columnIndex[i] = _cursor.getColumnIndex(columnNames[i]); // Einmaliges Auslesen der Spaltenindexe
		}
		line.append(_NL);
		
		// Operationen an Dateien
		try
		{
			// Erzeugen von Ordnern
			path.mkdirs();
			
			// Spaltenname speichern
			writeFile = new BufferedWriter(new FileWriter(exportFile));
			writeFile.append(line.substring(1));
			
			// Cursor positionieren
			_cursor.moveToPosition(-1);
			while (_cursor.moveToNext()) {
				line.delete(0, line.length());
				
				for (int column : columnIndex) {
					line.append(",")
						.append(_cursor.getString(column));
				}
				line.append(_NL);
				
				// Zeile speichern
				writeFile.append(line.substring(1));
				
				// Fortschritt melden
				if ((progress * progressStep) > _current) {
					message = _handler.obtainMessage();
					message.arg1 = (int)(progress * progressStep);
					_current = message.arg1;
					_handler.sendMessage(message);
				}
				progress++;
			}
			
		} catch (Exception e) {
			//
		}
		finally
		{
			if (writeFile != null) {
				try {
					writeFile.flush();
					writeFile.close();
				} catch (IOException e) {

				}
			}
			message = _handler.obtainMessage();
			message.arg1 = _total;
			_handler.sendMessage(message);
		}
	}
}
