package de.mvhs.android.zeiterfassung;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by eugen on 09.11.15.
 */
public class EditActivity extends AppCompatActivity {
  @Override
  public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
    super.onCreate(savedInstanceState, persistentState);

    setContentView(R.layout.activity_edit);
  }
}
