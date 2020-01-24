package nl.freelist.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import nl.freelist.freelist.R;

public class DurationPickerDialog extends DialogFragment {

  public static final String TAG = "DurationPickerDialog";

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
  public void onStart() {
    super.onStart();
    TextInputLayout textInputLayoutHours = getDialog().findViewById(R.id.text_input_layout_hours);
    textInputLayoutHours.requestFocus(); //To be able to type right away
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getDialog().getWindow().setSoftInputMode(
        LayoutParams.SOFT_INPUT_STATE_VISIBLE); //To keep sofinput from disappearing
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // Use the Builder class for convenient dialog construction
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater layoutInflater = LayoutInflater.from(getContext());
    View view = layoutInflater.inflate(R.layout.picker_duration, null);
    builder.setView(view);
    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        // User clicked OK button
        Log.d(TAG, "OK clicked");
        TextInputEditText textInputeditTextHours = getDialog()
            .findViewById(R.id.edit_text_hours);
        TextInputEditText textInputEditTextMinutes = getDialog()
            .findViewById(R.id.edit_text_minutes);
        String hours = textInputeditTextHours.getText().toString();
        String minutes = textInputEditTextMinutes.getText().toString();
        if (hours.equals("")) {
          hours = "0";
        }
        if (minutes.equals("")) {
          minutes = "0";
        }
        String durationString = String
            .valueOf(Integer.valueOf(hours) * 3600 + Integer.valueOf(minutes) * 60);
        listener.onDialogPositiveClick(durationString, "duration");
      }
    });
    // Create the AlertDialog object and return it
    return builder.create();
  }

}
