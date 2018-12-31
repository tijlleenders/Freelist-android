package nl.freelist.recyclerviewHelpers;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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

public class ChooseEntryAdapter extends RecyclerView.Adapter<ChooseEntryAdapter.EntryHolder> {

  private List<ViewModelEntry> entries = new ArrayList<>();
  private ItemClickListener onItemClickListener;

  public int getCurrentId() {
    return currentId;
  }

  private int currentId = 0;

  public ChooseEntryAdapter(ItemClickListener clickListener) {
    onItemClickListener = clickListener;
  }

  @NonNull
  @Override
  public EntryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView;
    if (viewType == ActivityConstants.NODE_ENTRY_VIEW_TYPE) {
      itemView =
          LayoutInflater.from(parent.getContext())
              .inflate(R.layout.choose_entry_parent_item, parent, false);
    } else if (viewType == ActivityConstants.MULTIPLE_ENTRY_VIEW_TYPE) {
      itemView =
          LayoutInflater.from(parent.getContext())
              .inflate(R.layout.choose_entry_multiple_item, parent, false);
    } else if (viewType == ActivityConstants.NODE_SELECTED_ENTRY_VIEW_TYPE) {
      itemView =
          LayoutInflater.from(parent.getContext())
              .inflate(R.layout.choose_selected_entry_parent_item, parent, false);
    } else if (viewType == ActivityConstants.MULTIPLE_SELECTED_ENTRY_VIEW_TYPE) {
      itemView =
          LayoutInflater.from(parent.getContext())
              .inflate(R.layout.choose_selected_entry_multiple_item, parent, false);
    } else if (viewType == ActivityConstants.LEAF_SELECTED_ENTRY_VIEW_TYPE) {
      itemView =
          LayoutInflater.from(parent.getContext())
              .inflate(R.layout.choose_selected_entry_leaf_item, parent, false);
    } else {
      itemView =
          LayoutInflater.from(parent.getContext())
              .inflate(R.layout.choose_entry_leaf_item, parent, false);
    }
    return new EntryHolder(itemView);
  }

  @Override
  public int getItemViewType(int position) {
    ViewModelEntry entry = getEntryAt(position);
    int type = entry.getType();
    if (currentId == entry.getId()) {
      switch (type) {
        case ActivityConstants.NODE_ENTRY_VIEW_TYPE:
          type = ActivityConstants.NODE_SELECTED_ENTRY_VIEW_TYPE;
          break;
        case ActivityConstants.MULTIPLE_ENTRY_VIEW_TYPE:
          type = ActivityConstants.MULTIPLE_SELECTED_ENTRY_VIEW_TYPE;
          break;
        case ActivityConstants.LEAF_ENTRY_VIEW_TYPE:
          type = ActivityConstants.LEAF_SELECTED_ENTRY_VIEW_TYPE;
          break;
        default:
          break;
      }
    }
    return type;
  }

  @Override
  public void onBindViewHolder(@NonNull EntryHolder entryHolder, int position) {
    ViewModelEntry currentEntry = entries.get(position);
    entryHolder.textViewTitle.setText(currentEntry.getTitle());
  }

  @Override
  public int getItemCount() {
    return entries.size();
  }

  public void setEntries(List<ViewModelEntry> entries) {
    this.entries = entries;
    notifyDataSetChanged(); // change later for onInsert onDelete (not efficient and no animations)
  }

  public void setCurrentId(int id) {
    this.currentId = id;
  }

  public ViewModelEntry getEntryAt(int position) {
    return entries.get(position);
  }

  class EntryHolder extends RecyclerView.ViewHolder {

    private final TextView textViewTitle;

    EntryHolder(@NonNull View itemView) {
      super(itemView);
      textViewTitle = itemView.findViewById(R.id.text_view_title);

      // Using lambda as below is more efficient than new inner class for every call
      itemView
          .setOnClickListener(view -> {
            onItemClickListener.onItemClick(view, getAdapterPosition());
          });

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
