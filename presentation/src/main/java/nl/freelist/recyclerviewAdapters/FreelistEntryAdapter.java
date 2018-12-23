package nl.freelist.recyclerviewAdapters;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import nl.freelist.activities.AddEditEntryActivity;
import nl.freelist.freelist.R;
import nl.freelist.presentationConstants.ActivityConstants;
import nl.freelist.viewModelPerEntity.ViewModelEntry;

public class FreelistEntryAdapter extends RecyclerView.Adapter<FreelistEntryAdapter.EntryHolder> {

  private List<ViewModelEntry> entries = new ArrayList<>();

  @NonNull
  @Override
  public EntryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    if (viewType == ActivityConstants.PARENT_ENTRY_VIEW) {
      View itemView =
          LayoutInflater.from(parent.getContext())
              .inflate(R.layout.entry_parent_item, parent, false);
      return new EntryHolder(itemView);
    } else if (viewType == ActivityConstants.MULTIPLE_ENTRY_VIEW) {
      View itemView =
          LayoutInflater.from(parent.getContext())
              .inflate(R.layout.entry_multiple_item, parent, false);
      return new EntryHolder(itemView);
    } else {
      View itemView =
          LayoutInflater.from(parent.getContext())
              .inflate(R.layout.entry_single_item, parent, false);
      return new EntryHolder(itemView);
    }
  }

  @Override
  public int getItemViewType(int position) {
    ViewModelEntry entry = entries.get(position);
    //Todo: get the itemType form DataEntry class
    if (position == 0) {
      return ActivityConstants.PARENT_ENTRY_VIEW;
    } else if (position == 1) {
      return ActivityConstants.MULTIPLE_ENTRY_VIEW;
    } else {
      return ActivityConstants.SINGLE_ENTRY_VIEW;
    }
  }

  @Override
  public void onBindViewHolder(@NonNull EntryHolder entryHolder, int position) {
    ViewModelEntry currentEntry = entries.get(position);
    entryHolder.textViewTitle.setText(currentEntry.getTitle());
    entryHolder.textViewDescription.setText(currentEntry.getDescription());
    entryHolder.textViewDuration.setText(currentEntry.getDuration());
  }

  @Override
  public int getItemCount() {
    return entries.size();
  }

  public void setEntries(List<ViewModelEntry> entries) {
    this.entries = entries;
    notifyDataSetChanged(); // change later for onInsert onDelete (not efficient and no animations)
  }

  public ViewModelEntry getEntryAt(int position) {
    return entries.get(position);
  }

  class EntryHolder extends RecyclerView.ViewHolder {

    private final TextView textViewTitle;
    private final TextView textViewDescription;
    private final TextView textViewDuration;

    EntryHolder(@NonNull View itemView) {
      super(itemView);
      textViewTitle = itemView.findViewById(R.id.text_view_title);
      textViewDescription = itemView.findViewById(R.id.text_view_description);
      textViewDuration = itemView.findViewById(R.id.text_view_duration);

      itemView.setOnClickListener(
          new OnClickListener() {
            @Override
            public void onClick(View v) {

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
            }
          });
    }
  }
}
