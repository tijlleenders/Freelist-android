package nl.freelist.recyclerviewHelpers;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import nl.freelist.data.dto.ViewModelCalendarOption;
import nl.freelist.freelist.R;

public class ChooseCalendarOptionAdapter extends
    RecyclerView.Adapter<ChooseCalendarOptionAdapter.EntryHolder> {

  private static final String TAG = "ChooseEntryAdapter";
  private int selectedPosition = -1;

  private List<ViewModelCalendarOption> entries = new ArrayList<>();
  private ItemClickListener onItemClickListener;

  public ChooseCalendarOptionAdapter(ItemClickListener clickListener) {
    Log.d(TAG, "ChooseEntryAdapter called.");
    onItemClickListener = clickListener;
  }

  @NonNull
  @Override
  public EntryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Log.d(TAG, "onCreateViewHolder called.");
    View itemView;
    switch (viewType) {
      //Todo: how to incorporate Constants.PRIO_ in switch statement?
      default:
        itemView =
            LayoutInflater.from(parent.getContext())
                .inflate(R.layout.choose_prio, parent, false);
        break;
    }
    return new EntryHolder(itemView);
  }

  @Override
  public int getItemViewType(int position) {
    Log.d(TAG, "getItemViewType called.");
    ViewModelCalendarOption entry = getEntryAt(position);
    int type = entry.getType();
    return type;
  }

  @Override
  public void onBindViewHolder(@NonNull EntryHolder entryHolder, int position) {
    Log.d(TAG, "onBindViewHolder called.");
    ViewModelCalendarOption currentEntry = entries.get(position);
    if (selectedPosition == position) {
      entryHolder.itemView
          .setBackgroundColor(Color.parseColor("#a2faba")); //Todo: remove hardcoding of color
    } else {
      entryHolder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
    }
    entryHolder.textViewDate.setText(currentEntry.getScheduledDate());
    entryHolder.textViewTime.setText(currentEntry.getScheduledTime());
    entryHolder.textViewProblems.setText(currentEntry.getNumberOfProblems());
    entryHolder.textViewReschedules.setText(currentEntry.getNumberOfReschedules());
  }

  @Override
  public int getItemCount() {
    Log.d(TAG, "getItemCount called.");
    return entries.size();
  }

  public ViewModelCalendarOption getEntryAt(int position) {
    Log.d(TAG, "getEntryAt called.");
    return entries.get(position);
  }

  public void setPrioEntries(List<ViewModelCalendarOption> prioEntries) {
    Log.d(TAG, "setPrioEntries called.");
    this.entries = prioEntries;
    notifyDataSetChanged(); // change later for onInsert onDelete (not efficient and no animations)
  }

  public int getLastSavedEventSequenceNumberFor(int calendarOptionSelected) {
    ViewModelCalendarOption currentEntry = entries.get(calendarOptionSelected);
    return currentEntry.getEntryLastAppliedEventSequenceNumber();
  }

  public int lastSavedResourceSequenceNumberFor(int calendarOptionSelected) {
    ViewModelCalendarOption currentEntry = entries.get(calendarOptionSelected);
    return currentEntry.getResourceLastAppliedEventSequenceNumber();
  }


  class EntryHolder extends RecyclerView.ViewHolder {

    private final TextView textViewDate;
    private final TextView textViewTime;
    private final TextView textViewProblems;
    private final TextView textViewReschedules;

    EntryHolder(@NonNull View itemView) {
      super(itemView);
      // itemView.setClickable(true);
      // Todo: only if multiple select?
      // in combination with this in onBindViewHolder : viewHolder.parentview.setSelected(mSelectedRows.contains(i));
      Log.d(TAG, "EntryHolder called sets OnClick and OnLongClickListener.");
      textViewDate = itemView.findViewById(R.id.text_view_date);
      textViewTime = itemView.findViewById(R.id.text_view_time);
      textViewProblems = itemView.findViewById(R.id.text_view_problems);
      textViewReschedules = itemView.findViewById(R.id.text_view_reschedules);

      // Using lambda as below is more efficient than new inner class for every call
      itemView
          .setOnClickListener(
              view -> {
                if (selectedPosition == getAdapterPosition()) {
                  selectedPosition = -1;
                } else {
                  selectedPosition = getAdapterPosition();
                }
                onItemClickListener.onItemClick(view, getAdapterPosition());
                notifyDataSetChanged();
              }
          );

      itemView.setOnLongClickListener( //Todo: replace with lambda like with onItemClick?
          //Todo: Why is this even in here?
          new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
              return false;
            }
          }
      );
    }
  }
}
