package nl.freelist.recyclerviewHelpers;

import static nl.freelist.domain.crossCuttingConcerns.Constants.EVENT_TYPE;

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
import nl.freelist.data.dto.ViewModelEvent;
import nl.freelist.freelist.R;
import nl.freelist.recyclerviewHelpers.EventAdapter.EventHolder;

public class EventAdapter extends RecyclerView.Adapter<EventHolder> {

  private static final String TAG = "EventAdapter";

  private List<ViewModelEvent> viewModelEvents = new ArrayList<>();
  private ItemClickListener onItemClickListener;

  public String getCurrentUuid() {
    return currentUuid;
  }

  private String currentUuid = UUID.nameUUIDFromBytes("tijl.leenders@gmail.com".getBytes())
      .toString();

  public EventAdapter(ItemClickListener clickListener) {
    Log.d(TAG, "EventAdapter called.");
    onItemClickListener = clickListener;
  }

  @NonNull
  @Override
  public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Log.d(TAG, "onCreateViewHolder called.");
    View itemView;
    if (viewType == EVENT_TYPE) {
      itemView =
          LayoutInflater.from(parent.getContext())
              .inflate(R.layout.event, parent, false);
    } else {
      Log.d(TAG, "ViewModelEvent type not recognized!");
      itemView =
          LayoutInflater.from(parent.getContext())
              .inflate(R.layout.event, parent, false);
    }

    return new EventHolder(itemView);
  }

  @Override
  public int getItemViewType(int position) {
    Log.d(TAG, "getItemViewType called.");
    return EVENT_TYPE;
  }

  @Override
  public void onBindViewHolder(@NonNull EventHolder eventHolder, int position) {
    Log.d(TAG, "onBindViewHolder called.");
    ViewModelEvent viewModelEvent = viewModelEvents.get(position);
    eventHolder.textViewTitle.setText(viewModelEvent.getEventMessage());
    eventHolder.textViewTime.setText(viewModelEvent.getOccurredDateTime());
  }

  @Override
  public int getItemCount() {
    Log.d(TAG, "getItemCount called.");
    return viewModelEvents.size();
  }

  public void setViewModelEvents(List<ViewModelEvent> viewModelEvents) {
    Log.d(TAG, "setViewModelEvents called.");
    this.viewModelEvents = viewModelEvents;
    notifyDataSetChanged(); // change later for onInsert onDelete (not efficient and no animations)
  }

  public void setCurrentId(String uuid) {

    Log.d(TAG, "setCurrentId called.");
    this.currentUuid = uuid;
  }

  public ViewModelEvent getEventAt(int position) {
    Log.d(TAG, "getEventAt called.");
    return viewModelEvents.get(position);
  }

  class EventHolder extends RecyclerView.ViewHolder {

    private final TextView textViewTitle;
    private final TextView textViewTime;

    EventHolder(@NonNull View itemView) {
      super(itemView);
      Log.d(TAG, "EventHolder called sets OnClick and OnLongClickListener.");
      textViewTitle = itemView.findViewById(R.id.text_view_title);
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
//              CalendarEntry calendarEntry =
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
