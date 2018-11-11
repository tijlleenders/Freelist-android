package nl.freelist.userInterfaceHelpers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import nl.freelist.constants.ActivityConstants;
import nl.freelist.database.Entry;
import nl.freelist.freelist.R;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryHolder> {

  private List<Entry> entries = new ArrayList<>();

  @NonNull
  @Override
  public EntryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.entry_item, parent, false);
    return new EntryHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull EntryHolder entryHolder, int position) {
    Entry currentEntry = entries.get(position);
    entryHolder.textViewTitle.setText(currentEntry.getTitle());
    entryHolder.textViewDescription.setText(currentEntry.getDescription());
    entryHolder.textViewDuration.setText(currentEntry.getFormattedDuration());
    entryHolder.textViewDate.setText(currentEntry.getFormattedDate());
  }

  @Override
  public int getItemCount() {
    return entries.size();
  }

  public void setEntries(List<Entry> entries) {
    this.entries = entries;
    notifyDataSetChanged(); // change later for onInsert onDelete (not efficient and no animations)
  }

  public Entry getEntryAt(int position) {
    return entries.get(position);
  }

  class EntryHolder extends RecyclerView.ViewHolder {

    final private TextView textViewTitle;
    final private TextView textViewDescription;
    final private TextView textViewDuration;
    final private TextView textViewDate;

    EntryHolder(@NonNull View itemView) {
      super(itemView);
      textViewTitle = itemView.findViewById(R.id.text_view_title);
      textViewDescription = itemView.findViewById(R.id.text_view_description);
      textViewDuration = itemView.findViewById(R.id.text_view_duration);
      textViewDate = itemView.findViewById(R.id.text_view_date);

      itemView.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {

          Intent intent = new Intent(v.getContext(), AddEditEntryActivity.class);
          intent.putExtra(ActivityConstants.EXTRA_REQUEST_TYPE_EDIT,
              ActivityConstants.EDIT_ENTRY_REQUEST);

          int position = getAdapterPosition();
          Entry entry = getEntryAt(
              position); //todo: make Entry parcelable (not serializable as this is heavy on system) and pass whole entry to edit Activity
          intent.putExtra(ActivityConstants.EXTRA_ENTRY_ID, entry.getId());
          intent.putExtra(ActivityConstants.EXTRA_ENTRY_TITLE, entry.getTitle());
          intent.putExtra(ActivityConstants.EXTRA_ENTRY_DESCRIPTION, entry.getDescription());
          intent.putExtra(ActivityConstants.EXTRA_ENTRY_FORMATTED_DATE, entry.getFormattedDate());
          intent.putExtra(ActivityConstants.EXTRA_ENTRY_FORMATTED_DURATION, entry.getFormattedDuration());

          ((Activity) v.getContext())
              .startActivityForResult(intent, ActivityConstants.EDIT_ENTRY_REQUEST);
        }
      });
    }
  }
}
