package nl.freelist.dialogs;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TimePicker;
import androidx.fragment.app.DialogFragment;

public class FTimePickerDialog extends DialogFragment
    implements TimePickerDialog.OnTimeSetListener {

  public static final String TAG = "TimePickerDialog";
  private String inputType;

  public FTimePickerDialog(String inputType) {
    this.inputType = inputType;
  }

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
    int hour = 7;
    int minute = 0;

    // Create a new instance of TimePickerDialog and return it
    return new TimePickerDialog(getActivity(), this, hour, minute,
        true);
  }

  public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    String selectedTime =
        Integer.toString(hourOfDay * 3600 + minute * 60);
    listener.onDialogFeedback(selectedTime, inputType);
  }
}