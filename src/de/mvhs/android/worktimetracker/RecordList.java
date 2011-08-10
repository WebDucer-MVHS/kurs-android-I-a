/**
 * 
 */
package de.mvhs.android.worktimetracker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;

/**
 * @author kurs
 *
 */
public class RecordList extends ListActivity {
	/**
	 * Klassenvariablen
	 */
	private int _Position = 1;
	private ProgressDialog _ProgressDialog;
	private final int _ExportDialog = 0;
	
	final Handler _handler = new Handler(){
		public void handleMessage(Message message){
			int total = message.arg1;
			_ProgressDialog.setProgress(total);
			
			if (total >= 100) {
				_ProgressDialog.dismiss();
			}
		}
	};
	
	/**
	 * On Create
	 */
	@Override
 	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setContentView(R.layout.record_list);
		
		LoadData();
	}
	
	/**
	 * Kontext Menü einbinden
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.context_menu, menu);
		
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	/**
	 * Auswerten des Kontext Menüs
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterView.AdapterContextMenuInfo info =
				(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		long id = info.id;
		_Position = info.position;
		
		switch (item.getItemId()) {
		case R.id.ctx_delete:
			DBHelper dbHelper = new DBHelper(this);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.delete(
				TimeTrackingTable.TABLE_NAME,
				TimeTrackingTable.ID + "=?",
				new String[]{String.valueOf(id)});
			db.close();
			
			LoadData();
			break;
			
		case R.id.ctx_edit:
			Intent intentEdit = new Intent(this, EditRecord.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentEdit.putExtra("id", id);
			startActivity(intentEdit);
			
			break;

		default:
			break;
		}
		
		return super.onContextItemSelected(item);
	}
	
	/**
	 * Laden der Daten
	 */
	private void LoadData(){
		// Initialisierung der Datenbank
		DBHelper dbHelper = new DBHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Cursor cursor = db.rawQuery(TimeTrackingTable.SQL_LIST_RECORDS, null);
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2,
				cursor,
				new String[]{TimeTrackingTable.START_TIME, TimeTrackingTable.END_TIME},
				new int[]{android.R.id.text1, android.R.id.text2});
		
		startManagingCursor(cursor);
		
		setListAdapter(adapter);
		
		if (_Position <= cursor.getCount()) {
			setSelection(_Position);
		}
		
		registerForContextMenu(findViewById(android.R.id.list));
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu){
    		getMenuInflater().inflate(R.menu.menu_export, menu);
    		
    		return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    		switch (item.getItemId()) {
			case R.id.opt_export:
				showDialog(_ExportDialog);
				
				break;
				
			default:
				break;
			}
    	
    	
    		return super.onOptionsItemSelected(item);
    }
    
    /**
     * Initialisierung des Dialoges
     */
    protected Dialog onCreateDialog(int id){
    		switch (id) {
			case _ExportDialog:
				_ProgressDialog = new ProgressDialog(RecordList.this);
				_ProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				_ProgressDialog.setMessage(getString(R.string.msg_exporting));
				
				return _ProgressDialog;

			default:
				return null;
			}
    }
    
    /**
     * Dialog vorbereiten
     */
    protected void onPrepareDialog(int id, Dialog dialog){
    		switch (id) {
			case _ExportDialog:
				DBHelper dbHelper = new DBHelper(this);
				SQLiteDatabase db = dbHelper.getReadableDatabase();
				Cursor cursor = db.rawQuery(TimeTrackingTable.SQL_LIST_RECORDS, null);
				
				ExportCSV exportThread = new ExportCSV(_handler, cursor, "wtt_records.csv");
				exportThread.start();
				
				break;

			default:
				break;
			}
    }
    
    /**
     * Hintergrundthread
     */
    private class ExportCSV extends Thread{
    		/**
    		 * Klassenvariablen
    		 */
    		private Cursor _Cursor;
    		private String _FileName;
    		private Handler _Handler;

    		private int _Total = 100;
    		private int _Current = 0;
    		
    		/**
    		 * Konstruktor
    		 */
    		public ExportCSV(Handler handler, Cursor cursor, String fileName){
    			_Handler = handler;
    			_Cursor = cursor;
    			_FileName = fileName;
    		}
    		
    		/**
    		 * Thread starten
    		 */
    		public void run(){
    			// Progress
    			int count = _Cursor.getCount();
    			String[] columnNames = _Cursor.getColumnNames();
    			int progress = 0;
    			double progressStep = 100d / count;
    			Message message = _Handler.obtainMessage();
    			
    			OutputStream writeFile = null;
    			File path = Environment.getExternalStorageDirectory();
    			path = new File(path, "export");
    			
    			File fileExport = new File(path, _FileName);
    			
    			String line = "";
    			
    			for (String columnName : columnNames) {
					line += columnName + ",";
				}
    			line = line.substring(0, line.length() - 1) + "\n";
    			
    			try {
				path.mkdirs();
				
				writeFile = new FileOutputStream(fileExport);
				writeFile.write(line.getBytes());
				
				_Cursor.moveToPosition(-1);
				while (_Cursor.moveToNext()) {
					line = "";
					
					for (String columnName : columnNames) {
						line += _Cursor.getString(_Cursor.getColumnIndex(columnName)) + ",";
					}
					line = line.substring(0, line.length() - 1) + "\n";
					
					writeFile.write(line.getBytes());
					
					// Progress Überwachen und melden
					if ((progress * progressStep) > _Current) {
						message = _Handler.obtainMessage();
						message.arg1 = (int)(progress * progressStep);
						_Handler.sendMessage(message);
						_Current = (int)(progress * progressStep);
					}
					progress++;
				}
				
				writeFile.flush();
				writeFile.close();
				
			} catch (Exception e) {
					//
			}
    			finally{
    				message = _Handler.obtainMessage();
    				message.arg1 = _Total;
    				_Handler.sendMessage(message);
    			}
    			
    		}
    }
}
