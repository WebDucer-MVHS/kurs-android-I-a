package de.mvhs.android.zeiterfassung;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class EditActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_edit);

    getActionBar().setDisplayHomeAsUpEnabled(true);
    getActionBar().setDisplayShowHomeEnabled(true);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        this.finish();

        break;

      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}
