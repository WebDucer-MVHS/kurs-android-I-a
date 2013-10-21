package de.mvhs.android.zeiterfassung;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
  private boolean _IsStarted = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Suchen der Elemente in eingebundenen Layout
    Button starten = (Button) findViewById(R.id.button_start);
    Button beenden = (Button) findViewById(R.id.button_end);

    // Zuordnen des Listneners zum Element
    starten.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        onStartClicked();
      }
    });

    beenden.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        onEndClicked();
      }
    });

    // Alternative
    // starten.setOnClickListener(onClicked);
    // beenden.setOnClickListener(onClicked);

    // Aktivieren / Deaktivieren
    _IsStarted = savedInstanceState == null ? false : savedInstanceState.getBoolean("IsStarted", false);
    changeButtonState();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putBoolean("IsStarted", _IsStarted);
    super.onSaveInstanceState(outState);
  }

  // Was passiert, wenn Start Button geclickt wird
  private void onStartClicked() {
    // Textbox suchen
    EditText start_zeit = (EditText) findViewById(R.id.text_start_time);

    // Aktuelle Uhrzeit bestimmen
    Date jetzt = new Date();

    // Ausgabe der aktuellen Zeit
    start_zeit.setText(jetzt.toString());

    _IsStarted = true;
    changeButtonState();
  }

  // Was passiert, wenn Beenden Button geclickt wird
  private void onEndClicked() {
    // Textbox suchen
    EditText end_zeit = (EditText) findViewById(R.id.text_end_time);

    // Aktuelle Uhrzeit bestimmen
    Date jetzt = new Date();

    // Ausgabe der aktuellen Zeit
    end_zeit.setText(jetzt.toString());

    _IsStarted = false;
    changeButtonState();
  }

  private void changeButtonState() {
    // Buttons suchen
    Button starten = (Button) findViewById(R.id.button_start);
    Button beenden = (Button) findViewById(R.id.button_end);

    // Aktivieren / Deaktivieren
    if (_IsStarted) {
      starten.setEnabled(false);
      beenden.setEnabled(true);
    } else {
      beenden.setEnabled(false);
      starten.setEnabled(true);
    }
  }

  // Alternative
  // private OnClickListener onClicked = new OnClickListener() {
  //
  // @Override
  // public void onClick(View v) {
  // // Aktuelle Uhrzeit bestimmen
  // Date jetzt = new Date();
  //
  // // Textbox
  // EditText ausgabe = null;
  //
  // if (v.getId() == R.id.button_start) {
  // ausgabe = (EditText) findViewById(R.id.text_start_time);
  // _IsStarted = true;
  // } else if (v.getId() == R.id.button_end) {
  // ausgabe = (EditText) findViewById(R.id.text_end_time);
  // _IsStarted = false;
  // }
  //
  // ausgabe.setText(jetzt.toString());
  // changeButtonState();
  // }
  // };
}
