package nl.freelist.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager.LayoutParams;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import nl.freelist.domain.crossCuttingConcerns.TimeHelper;
import nl.freelist.freelist.R;

public class DurationPickerDialog extends DialogFragment {

  public static final String TAG = "DurationPickerDialog";

  // Use this instance of the interface to deliver action events
  NoticeDialogListener listener;

  public static DurationPickerDialog Create(Bundle argsBundle) {
    DurationPickerDialog frag = new DurationPickerDialog();
    frag.setArguments(argsBundle);
    return frag;
  }

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
      throw new ClassCastException(
          getActivity().toString() + " must implement NoticeDialogListener");
    }
  }

  @Override
  public void onStart() {
    super.onStart();
    TextInputLayout textInputLayoutHours = getDialog().findViewById(R.id.text_input_layout_hours);

    Long duration = getArguments().getLong("duration");
    TextInputEditText textInputEditTextHours = getDialog().findViewById(R.id.edit_text_hours);
    textInputEditTextHours.setText(Integer.toString(TimeHelper.getStandaloneHoursFrom(duration)));
    TextInputEditText textInputEditTextMinutes = getDialog().findViewById(R.id.edit_text_minutes);
    textInputEditTextMinutes.setText(
        Integer.toString(TimeHelper.getStandaloneMinutesFrom(duration)));

    textInputLayoutHours.requestFocus(); // To be able to type right away
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getDialog()
        .getWindow()
        .setSoftInputMode(
            LayoutParams.SOFT_INPUT_STATE_VISIBLE); // To keep sofinput from disappearing
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // Use the Builder class for convenient dialog construction
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = requireActivity().getLayoutInflater();
    builder
        .setView(inflater.inflate(R.layout.picker_duration, null)
        );
    return builder.create();
  }
}
