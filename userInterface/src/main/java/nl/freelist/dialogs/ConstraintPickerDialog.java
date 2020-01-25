package nl.freelist.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import androidx.fragment.app.DialogFragment;
import nl.freelist.freelist.R;

public class ConstraintPickerDialog extends DialogFragment {

  public static final String TAG = "ConstraintPickerDialog";
  private String inputType = "constraint";

  // Use this instance of the interface to deliver action events
  NoticeDialogListener listener;

  // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    // Verify that the host activity implements the callback interface
    try {
      // Instantiate the NoticeDialogListener so we can send events to the host
      listener = (NoticeDialogListener) context;
    } catch (ClassCastException e) {
      // The activity doesn't implement the interface, throw exception
      throw new ClassCastException(getActivity().toString()
          + " must implement NoticeDialogListener");
    }
  }


  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = requireActivity().getLayoutInflater();
    builder.setView(inflater.inflate(R.layout.picker_constraints, null))
        .setMessage("Schedule preference for")
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            // FIRE ZE MISSILES!
            listener.onDialogPositiveClick("input needed", inputType);
          }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            // User cancelled the dialog
            listener.onDialogPositiveClick("input cancel", inputType);
          }
        });
    // Create the AlertDialog object and return it
    return builder.create();
  }

}
