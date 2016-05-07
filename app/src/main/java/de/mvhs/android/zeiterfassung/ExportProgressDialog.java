package de.mvhs.android.zeiterfassung;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

public class ExportProgressDialog extends DialogFragment {

  public interface ExportProgressDialogListener {
    void onDialogNegativeClick( DialogFragment dialog );
  }

  ExportProgressDialogListener listener;

  @NonNull
  public Dialog onCreateDialog( Bundle savedState ) {
    AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
    builder.setMessage( R.string.export_message ).
            setTitle( R.string.export_title ).
            setNegativeButton( R.string.cancel, new DialogInterface.OnClickListener() {
              @Override
              public void onClick( DialogInterface dialog, int which ) {
                listener.onDialogNegativeClick( ExportProgressDialog.this );
              }
            } );
    return builder.create();
  }

  public void onAttach( Activity activity ) {
    super.onAttach( activity );

    try {
      listener = (ExportProgressDialogListener)activity;
    } catch( ClassCastException e ) {
      throw new ClassCastException( activity.toString() + " must implement ExportProgressDialogListener interface" );
    }
  }
}
