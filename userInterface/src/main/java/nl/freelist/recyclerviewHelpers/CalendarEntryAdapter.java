package nl.freelist.recyclerviewHelpers;

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
import java.util.UUID;
import nl.freelist.data.dto.ViewModelAppointment;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.freelist.R;

public class CalendarEntryAdapter extends RecyclerView.Adapter<CalendarEntryAdapter.EntryHolder> {

  private static final String TAG = "CalendarEntryAdapter";

  private List<ViewModelAppointment> entries = new ArrayList<>();
  private ItemClickListener onItemClickListener;

  public String getCurrentUuid() {
    return currentUuid;
  }

  private String currentUuid = UUID.nameUUIDFromBytes("tijl.leenders@gmail.com".getBytes())
      .toString();

  public CalendarEntryAdapter(ItemClickListener clickListener) {
    Log.d(TAG, "CalendarEntryAdapter called.");
    onItemClickListener = clickListener;
  }

  @NonNull
  @Override
  public EntryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Log.d(TAG, "onCreateViewHolder called.");
    View itemView;
    if (viewType == Constants.CALENDAR_ENTRY_DATE_VIEW_TYPE) {
      itemView =
          LayoutInflater.from(parent.getContext())
              .inflate(R.layout.calendar_entry_date, parent, false);
    } else {
      itemView =
          LayoutInflater.from(parent.getContext())
              .inflate(R.layout.calendar_entry_freelist, parent, false);
    }
    return new EntryHolder(itemView);
  }

  @Override
  public int getItemViewType(int position) {
    Log.d(TAG, "getItemViewType called.");
    ViewModelAppointment viewModelAppointment = getEntryAt(position);
    int type = viewModelAppointment.getType();
    return type;
  }

  @Override
  public void onBindViewHolder(@NonNull EntryHolder entryHolder, int position) {
    Log.d(TAG, "onBindViewHolder called.");
    ViewModelAppointment currentEntry = entries.get(position);
    entryHolder.textViewTitle.setText(currentEntry.getTitle());
    if (currentEntry.getType() == Constants.CALENDAR_ENTRY_TODO_VIEW_TYPE) {
      entryHolder.textViewDuration.setText(currentEntry.getDurationString());
      entryHolder.textViewTime.setText(currentEntry.getTime());
    }
  }

  @Override
  public int getItemCount() {
    Log.d(TAG, "getItemCount called.");
    return entries.size();
  }

  public void setEntries(List<ViewModelAppointment> entries) {
    Log.d(TAG, "setEntries called.");
    this.entries = entries;
    notifyDataSetChanged(); // change later for onInsert onDelete (not efficient and no animations)
  }

  public void setCurrentId(String uuid) {

    Log.d(TAG, "setCurrentId called.");
    this.currentUuid = uuid;
  }

  public ViewModelAppointment getEntryAt(int position) {
    Log.d(TAG, "getEntryAt called.");
    return entries.get(position);
  }

  class EntryHolder extends RecyclerView.ViewHolder {

    private final TextView textViewTitle;
    private final TextView textViewDuration;
    private final TextView textViewTime;

    EntryHolder(@NonNull View itemView) {
      super(itemView);
      Log.d(TAG, "EntryHolder called sets OnClick and OnLongClickListener.");
      textViewTitle = itemView.findViewById(R.id.text_view_title);
      textViewDuration = itemView.findViewById(R.id.text_view_duration);
      textViewTime = itemView.findViewById(R.id.text_view_time);

      // Using lambda as below is more efficient than new inner class for every call
      itemView
          .setOnClickListener(view -> onItemClickListener.onItemClick(view, getAdapterPosition()));

      itemView.setOnLongClickListener( //Todo: replace with lambda like with onItemClick?
          new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//              Intent intent = new Intent(v.getContext(), AddEditEntryActivity.class);
//              intent.putExtra(
//                  Constants.EXTRA_REQUEST_TYPE_EDIT, Constants.EDIT_ENTRY_REQUEST);
//              int position = getAdapterPosition();
//              ViewModelAppointment calendarEntry =
//                  getEntryAt(
//                      position);
//              intent.putExtra(Constants.EXTRA_ENTRY_ID, calendarEntry.getUuid());
//              ((Activity) v.getContext())
//                  .startActivityForResult(intent, Constants.EDIT_ENTRY_REQUEST);
//              ((Activity) v.getContext())
//                  .overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
              return false;
            }
          }
      );
    }
  }
}
