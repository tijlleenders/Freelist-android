package nl.freelist.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import nl.freelist.domain.crossCuttingConcerns.TimeHelper;
import nl.freelist.freelist.R;

public class DurationPickerDialog extends DialogFragment {

  public static final String TAG = "DurationPickerDialog";
  private TextInputEditText textInputEditTextHours;
  private TextInputEditText textInputEditTextMinutes;
  private TextInputLayout textInputLayoutHours;
  private TextInputLayout textInputLayoutMinutes;
  private long duration;
  private long newDuration;
  private String hoursAfter = "";
  private String minutesAfter = "";

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

  private void sendNewDurationIfChanged() {
    Log.d(TAG, "send duration called old:" + duration);
    newDuration = 0L;
    if (!hoursAfter.equals("")) {
      newDuration += Long.valueOf(hoursAfter) * 3600;
    }
    if (!minutesAfter.equals("")) {
      newDuration += Long.valueOf(minutesAfter) * 60;
    }
    if (duration != newDuration) {
      duration = newDuration;
      Log.d(TAG, "send duration called new:" + newDuration);
      listener.onDialogFeedback(String.valueOf(newDuration), "duration");
    }
  }

  @Override
  public void onStart() {
    super.onStart();
    textInputLayoutHours = getDialog().findViewById(R.id.text_input_layout_hours);
    textInputLayoutMinutes = getDialog().findViewById(R.id.text_input_layout_minutes);
    textInputEditTextHours = getDialog().findViewById(R.id.edit_text_hours);
    textInputEditTextMinutes = getDialog().findViewById(R.id.edit_text_minutes);

    duration = getArguments().getLong("duration");
    int hours = TimeHelper.getStandaloneHoursFrom(duration);
    int minutes = TimeHelper.getStandaloneMinutesFrom(duration);

    textInputEditTextHours.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
          }

          @Override
          public void afterTextChanged(Editable s) {
            hoursAfter = s.toString();
            sendNewDurationIfChanged();
          }
        });

    textInputEditTextMinutes.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
          }

          @Override
          public void afterTextChanged(Editable s) {
            minutesAfter = s.toString();
            sendNewDurationIfChanged();
          }
        });

    textInputLayoutHours.setEndIconOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            textInputEditTextHours.setText("");
            sendNewDurationIfChanged();
          }
        });

    if (hours > 0) {
      textInputEditTextHours.setText(Integer.toString(hours));
    }
    textInputEditTextMinutes = getDialog().findViewById(R.id.edit_text_minutes);
    if (minutes > 0) {
      textInputEditTextMinutes.setText(Integer.toString(minutes));
    }

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
    builder.setView(inflater.inflate(R.layout.picker_duration, null));
    return builder.create();
  }
}
