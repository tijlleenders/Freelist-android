package nl.freelist.recyclerviewHelpers;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import nl.freelist.activities.AddEditEntryActivity;
import nl.freelist.freelist.R;
import nl.freelist.presentationConstants.ActivityConstants;
import nl.freelist.viewModelPerEntity.ViewModelEntry;

public class FreelistEntryAdapter extends RecyclerView.Adapter<FreelistEntryAdapter.EntryHolder> {

  private static final String TAG = "FreelistEntryAdapter";

  private List<ViewModelEntry> entries = new ArrayList<>();
  private ItemClickListener onItemClickListener;

  public int getCurrentId() {
    return currentId;
  }

  private int currentId = 0;

  public FreelistEntryAdapter(ItemClickListener clickListener) {
    Log.d(TAG, "FreelistEntryAdapter called.");
    onItemClickListener = clickListener;
  }

  @NonNull
  @Override
  public EntryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Log.d(TAG, "onCreateViewHolder called.");
    View itemView;
    if (viewType == ActivityConstants.STACK_ENTRY_VIEW_TYPE) {
      itemView =
          LayoutInflater.from(parent.getContext())
              .inflate(R.layout.main_entry_stack, parent, false);
    } else if (viewType == ActivityConstants.SELECTED_ENTRY_VIEW_TYPE) {
      itemView =
          LayoutInflater.from(parent.getContext())
              .inflate(R.layout.main_entry_selected, parent, false);
    } else if (viewType == ActivityConstants.SINGLE_ENTRY_UPSTREAM_VIEW_TYPE) {
      itemView =
          LayoutInflater.from(parent.getContext())
              .inflate(R.layout.main_entry_single_upstream, parent, false);
    } else {
      itemView =
          LayoutInflater.from(parent.getContext())
              .inflate(R.layout.main_entry_single_downstream, parent, false);
    }
    return new EntryHolder(itemView);
  }

  @Override
  public int getItemViewType(int position) {
    Log.d(TAG, "getItemViewType called.");
    ViewModelEntry entry = getEntryAt(position);
    int type = entry.getType();
    if (currentId == entry.getId()) {
      type = ActivityConstants.SELECTED_ENTRY_VIEW_TYPE;
    }
    return type;
  }

  @Override
  public void onBindViewHolder(@NonNull EntryHolder entryHolder, int position) {
    Log.d(TAG, "onBindViewHolder called.");
    ViewModelEntry currentEntry = entries.get(position);
    entryHolder.textViewTitle.setText(currentEntry.getTitle());
    entryHolder.textViewDescription.setText(currentEntry.getDescription());
    entryHolder.textViewDuration.setText(currentEntry.getDurationString());
  }

  @Override
  public int getItemCount() {
    Log.d(TAG, "getItemCount called.");
    return entries.size();
  }

  public void setEntries(List<ViewModelEntry> entries) {
    Log.d(TAG, "setEntries called.");
    this.entries = entries;
    notifyDataSetChanged(); // change later for onInsert onDelete (not efficient and no animations)
  }

  public void setCurrentId(int id) {

    Log.d(TAG, "setCurrentId called.");
    this.currentId = id;
  }

  public ViewModelEntry getEntryAt(int position) {
    Log.d(TAG, "getEntryAt called.");
    return entries.get(position);
  }

  class EntryHolder extends RecyclerView.ViewHolder {

    private final TextView textViewTitle;
    private final TextView textViewDescription;
    private final TextView textViewDuration;

    EntryHolder(@NonNull View itemView) {
      super(itemView);
      Log.d(TAG, "EntryHolder called sets OnClick and OnLongClickListener.");
      textViewTitle = itemView.findViewById(R.id.text_view_title);
      textViewDescription = itemView.findViewById(R.id.text_view_description);
      textViewDuration = itemView.findViewById(R.id.text_view_duration);

      // Using lambda as below is more efficient than new inner class for every call
      itemView
          .setOnClickListener(view -> onItemClickListener.onItemClick(view, getAdapterPosition()));

      itemView.setOnLongClickListener( //Todo: replace with lambda like with onItemClick?
          new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
              Intent intent = new Intent(v.getContext(), AddEditEntryActivity.class);
              intent.putExtra(
                  ActivityConstants.EXTRA_REQUEST_TYPE_EDIT, ActivityConstants.EDIT_ENTRY_REQUEST);
              int position = getAdapterPosition();
              ViewModelEntry entry =
                  getEntryAt(
                      position); // Not_to_do: make DataEntry parcelable (not serializable as this is heavy
              // on system) and pass whole entry to edit Activity.
              // Actually, decided only to pass the ID and then construct a ViewModel in the other activity based on this ID
              intent.putExtra(ActivityConstants.EXTRA_ENTRY_ID, Integer.toString(entry.getId()));
              ((Activity) v.getContext())
                  .startActivityForResult(intent, ActivityConstants.EDIT_ENTRY_REQUEST);
              ((Activity) v.getContext())
                  .overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
              return false;
            }
          }
      );
    }
  }
}
