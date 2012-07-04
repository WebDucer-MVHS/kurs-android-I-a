package de.mvhs.android.zeiterfassung;

import java.text.DateFormat;
import java.util.Date;

import de.mvhs.android.zeiterfassung.db.WorktimeTable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

public class RecordEditActivity extends Activity {
	// Variablen
	public static final String ID_KEY = "ID";
	private long _ID = -1;
	private Date _StartTime;
	private Date _EndTime;
	private WorktimeTable _Table = new WorktimeTable(this);
	private static final DateFormat			_TFmedium			=
			DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_edit);
		
		// Auslesen der ID aus der Nachricht, falls vorhanden
		if (this.getIntent().getExtras() != null) {
			Bundle extra = this.getIntent().getExtras();
			if (extra.containsKey(ID_KEY)) {
				this._ID = extra.getLong(ID_KEY);
			}
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();		
		// Laden der Elemente
		EditText startTime = (EditText)findViewById(R.id.txt_start_time);
		EditText endTime = (EditText)findViewById(R.id.txt_end_time);
		
		// Initialisierung der Elemente
		startTime.setKeyListener(null);
		endTime.setKeyListener(null);
		
		// Laden der Daten
		_StartTime = _Table.getStartDate(_ID);
		_EndTime = _Table.getEndDate(_ID);
		
		// Inhalte setzen
		startTime.setText(_TFmedium.format(_StartTime));
		endTime.setText(_TFmedium.format(_EndTime));
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.save_cancel_delete, menu);
		this.getActionBar().setHomeButtonEnabled(true);
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent homeIntent = new Intent(this, MainActivity.class);
			startActivity(homeIntent);
			
			break;

		case R.id.opt_cancel:
			// Aktuelle Activity beenden
			this.finish();
			
			break;
			
		case R.id.opt_delete:
			// Abfrage, ob wirklich gelöscht werden soll
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			
			builder.setTitle(R.string.dlg_confirm_title)
				.setMessage(R.string.dlg_confirm_message)
				.setIcon(R.drawable.ic_menu_delete)
				.setNegativeButton(R.string.dlg_cancel,
					new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})
				.setPositiveButton(R.string.dlg_delete,
					new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						_Table.deleteWorktime(_ID);
					}
				});
			
			builder.create().show();
			
			break;
			
		case R.id.opt_save:
			// Speichern des Datensatzen und beenden der Activity
			_Table.updateWorktime(_ID, _StartTime, _EndTime);
			this.finish();
			
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
