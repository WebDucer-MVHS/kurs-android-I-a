/**
 * Activity für die Bearbeitung eines Zeitantrages
 */
package de.mvhs.zeit;

import java.text.ParseException;
import java.util.Date;
import de.mvhs.zeit.db.DBHelper;
import de.mvhs.zeit.db.ZeiterfassungTable;
import android.app.TabActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TabHost;
import android.widget.TimePicker;

public class EditRecordActivity extends TabActivity {
	/**
	 * ID des zu bearbeitenden Eintrages
	 */
	private long _ID = -1;
	
	/**
	 * Tab Host der Activity
	 */
	private TabHost _Tabhost;
	
	/**
	 * Verbindung zur Hilfsklasse der Tabelle in der Datenbank
	 */
	ZeiterfassungTable _ZT = new ZeiterfassungTable(this);
	
	/**
	 * key für die Übergabe der ID an die Activity
	 */
	public final static String ID = "id";
	
	/**
	 * Initialisierung der Activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.edit_record);
		
		// Initialisierung der Tabs
		_Tabhost = getTabHost(); // Initialisierung des Tabhostes aus dem Layout
		_Tabhost.addTab(_Tabhost.newTabSpec("tab_start") // Hinzufügen des ersten Tabs
				.setIndicator(getString(R.string.tab_start)) // Beschriftung des Tabs
				//.setIndicator("Label", icon) // Initialisierung mit Icon und Beschriftung
				.setContent(R.id.tab1) // Zuordnung des Inhaltes zu dem Tab
				);
		_Tabhost.addTab(_Tabhost.newTabSpec("tab_end")
				.setIndicator(getString(R.string.tab_end))
				.setContent(R.id.tab2)
				);
		_Tabhost.setCurrentTab(0); // Setzen des ersten Tabs als Aktiv beim Öffnen der Activity
		
		Bundle extras = getIntent().getExtras(); // Extrahieren der Übergebenen Parameter
		if (extras != null) {
			_ID = extras.getLong(ID); // Speichern des Parameters im Feld
		}
		
		super.onCreate(savedInstanceState);
		
		Init();
	}
	
	/**
	 * Initialisierung des Menüs
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.edit_record_menu, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * Reagieren auf den Klick des Menüeintrages
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.opt_save:
			RunSave();
			
			break;

		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Initialisierung der Activity
	 */
	private void Init()
	{
		Cursor cursor = _ZT.GetRecordById(_ID);
		
		if (cursor.moveToNext() == true) { // Prüfen, ob ein Eintrag vorhanden ist
			// Asulesen der Werte aus der Datenzeile
			String strStart = cursor.getString( // Auslesen eines String-Wertes einer Spalte aus dem Zeiger
				cursor.getColumnIndex(ZeiterfassungTable.COLUMN_START)); // Bestimmen des Indexes einer Spalte
			String strEnd = cursor.getString(
				cursor.getColumnIndex(ZeiterfassungTable.COLUMN_END));
			
			Date dtmStart = null;
			Date dtmEnd = null;
			try {
				// Konvertieren der Strings in ein Datum
				dtmStart = DBHelper.DB_DATE_FORMAT.parse(strStart);
				dtmEnd = DBHelper.DB_DATE_FORMAT.parse(strEnd);
				
				// ÜExtraktion der Views aus dem Layout
				DatePicker dpStartDate = (DatePicker)findViewById(R.id.startDate);
				DatePicker dpEndDate = (DatePicker)findViewById(R.id.endDate);
				TimePicker tpStartTime = (TimePicker)findViewById(R.id.startTime);
				TimePicker tpEndTime = (TimePicker)findViewById(R.id.endTime);
				
				// Zuweisung der Werte an die Views
				dpStartDate.init(
						dtmStart.getYear() + 1900,
						dtmStart.getMonth(),
						dtmStart.getDate(),
						null);
				dpEndDate.init(
						dtmEnd.getYear() + 1900,
						dtmEnd.getMonth(),
						dtmEnd.getDate(),
						null);
				
				tpStartTime.setCurrentHour(dtmStart.getHours());
				tpStartTime.setCurrentMinute(dtmStart.getMinutes());
				
				tpEndTime.setCurrentHour(dtmEnd.getHours());
				tpEndTime.setCurrentMinute(dtmEnd.getMinutes());
				
			} catch (ParseException e) {
			}
		}
	}
	
	/**
	 * Speichern der geänderten Werte
	 */
	private void RunSave()
	{
		// Extrahieren der Views aus dem Layout
		DatePicker dpStartDate = (DatePicker)findViewById(R.id.startDate);
		DatePicker dpEndDate = (DatePicker)findViewById(R.id.endDate);
		TimePicker tpStartTime = (TimePicker)findViewById(R.id.startTime);
		TimePicker tpEndTime = (TimePicker)findViewById(R.id.endTime);
		
		// Aktualisieren der eingegebenen Daten
		dpStartDate.clearFocus();
		dpEndDate.clearFocus();
		tpStartTime.clearFocus();
		tpEndTime.clearFocus();
		
		// Konvertieren der Daten in Date
		Date dtmStart = new Date(
				dpStartDate.getYear() - 1900,
				dpStartDate.getMonth(),
				dpStartDate.getDayOfMonth(),
				tpStartTime.getCurrentHour(),
				tpStartTime.getCurrentMinute());
		
		Date dtmEnd = new Date(
				dpEndDate.getYear() - 1900,
				dpEndDate.getMonth(),
				dpEndDate.getDayOfMonth(),
				tpEndTime.getCurrentHour(),
				tpEndTime.getCurrentMinute());
		
		// Speichern von Änderungen
		_ZT.SaveRecord(_ID, dtmStart, dtmEnd);
		
		// Beenden der Activity
		this.finish();
	}

}
