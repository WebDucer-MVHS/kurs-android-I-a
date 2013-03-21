package de.mvhs.android.arbeitszeiterfassung;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
  // Klassen-Variablen
  private boolean _IsOpen = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // UI Elemente binden
    final Button cmdStartEnd = (Button) findViewById(R.id.cmd_start_stop);
    final EditText txtStartTime = (EditText) findViewById(R.id.txt_start_time);
    final EditText txtEndTime = (EditText) findViewById(R.id.txt_end_time);
    
    if (_IsOpen) {
      cmdStartEnd.setText(R.string.cmd_end);
    } else {
      cmdStartEnd.setText(R.string.cmd_start);
    }

    cmdStartEnd.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Date dtmNow = new Date();

        if (_IsOpen) {
          txtEndTime.setText(dtmNow.toString());
          _IsOpen = false;
          cmdStartEnd.setText(R.string.cmd_start);
        } else {
          txtStartTime.setText(dtmNow.toString());
          _IsOpen = true;
          cmdStartEnd.setText(R.string.cmd_end);
          txtEndTime.setText("");
        }
      }
    });
  }
}
