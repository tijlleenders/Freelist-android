package nl.freelist.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import nl.freelist.freelist.R;

public class ScheduleForwardPickerDialog extends DialogFragment {

  public static final String TAG = "ScheduleForwardPickerDialog";
  // Use this instance of the interface to deliver action events
  NoticeDialogListener listener;
  //  private ScheduleForwardAdapter adapter;
  private RecyclerView recyclerView;

  public static ScheduleForwardPickerDialog Create() {
    ScheduleForwardPickerDialog frag = new ScheduleForwardPickerDialog();
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
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onStart() {
    super.onStart();
    //setup views + add listeners

  }


  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // Use the Builder class for convenient dialog construction
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = requireActivity().getLayoutInflater();
    builder.setView(inflater.inflate(R.layout.picker_schedule_forward, null));
    return builder.create();
  }
}
