package de.mvhs.android.zeiterfassung;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
  private final DateFormat _DTF = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);
  private EditText         _Startzeit;
  private EditText         _Endzeit;
  private Button           _Start;
  private Button           _Ende;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Initialisierung der Buttons für die Listener-Zuweisung
    _Start = (Button) findViewById(R.id.starten);
    _Ende = (Button) findViewById(R.id.beenden);

    // Initialisierung der Textfelder für die Ausgabe
    _Startzeit = (EditText) findViewById(R.id.startzeit);
    _Endzeit = (EditText) findViewById(R.id.endzeit);

    // Zuweisung der Click-Listener
    _Start.setOnClickListener(new OnClickListener() {

      public void onClick(View v) {
        onStarten();
      }
    });

    _Ende.setOnClickListener(new OnClickListener() {

      public void onClick(View v) {
        onBeenden();
      }
    });

    // Editierbarkeit der Felder deaktivieren
    _Startzeit.setEnabled(false);
    _Endzeit.setEnabled(false);

    pruefeZustand();
  }

  /**
   * Aufbau des Menüs (ActionBar)
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.opt_liste:
        // Toast.makeText(
        // this,
        // "Auflistung kommt!",
        // Toast.LENGTH_LONG).show();

        Intent listIntent = new Intent(this, ListeActivity.class);
        this.startActivity(listIntent);

        break;

      case R.id.opt_close:
        this.finish();
        break;

      default:
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  /**
   * Aufnahme der Startzeit
   */
  private void onStarten() {
    // Aktuelle Uhrzeit
    Date jetzt = new Date();

    // Ausgabe der Zeit
    _Startzeit.setText(_DTF.format(jetzt));

    // Speichern der Zeit in der Datenbank
    DBHelper helper = new DBHelper(this);
    SQLiteDatabase db = helper.getWritableDatabase();

    ZeitTabelle.SpeichereStartzeit(db, jetzt);

    // Ressourcen schließen
    db.close();
    helper.close();

    // Andere Elemente steuern
    _Endzeit.setText("");
    _Start.setEnabled(false);
    _Ende.setEnabled(true);
  }

  /**
   * Aufnahme der Endzeit
   */
  private void onBeenden() {
    // Aktuelle Uhrzeit
    Date jetzt = new Date();

    // Ausgabe der Zeit
    _Endzeit.setText(_DTF.format(jetzt));

    // Speichern der Zeit in der Datenbank
    DBHelper helper = new DBHelper(this);
    SQLiteDatabase db = helper.getWritableDatabase();

    // Leeren Eintrag suchen
    long id = ZeitTabelle.SucheLeereId(db);
    if (id > 0) {
      ZeitTabelle.AktualisiereEndzeit(db, id, jetzt);

      // Andere Elemente steuern
      _Start.setEnabled(true);
      _Ende.setEnabled(false);
    }

    // Ressourcen schließen
    db.close();
    helper.close();
  }

  /**
   * Den Zustand prüfen
   */
  private void pruefeZustand() {
    EditText txtStartzeit = (EditText) findViewById(R.id.startzeit);
    Button cmdStart = (Button) findViewById(R.id.starten);
    Button cmdEnde = (Button) findViewById(R.id.beenden);

    DBHelper helper = new DBHelper(this);
    SQLiteDatabase db = helper.getReadableDatabase();

    // ID des offenen Datensatzes finden
    long id = ZeitTabelle.SucheLeereId(db);

    // Prüfen, ob dieses vorhanden ist
    if (id > 0) {
      // Startzeit auslesen
      Date startZeit = ZeitTabelle.GebeStartzeitAus(db, id);

      // Startzeit ausgeben
      txtStartzeit.setText(_DTF.format(startZeit));

      // Buttons einrichten
      cmdStart.setEnabled(false);
      cmdEnde.setEnabled(true);
    } else {
      txtStartzeit.setText("");
      cmdStart.setEnabled(true);
      cmdEnde.setEnabled(false);
    }

    // Datenbank schließen
    db.close();
    helper.close();

  }
}
