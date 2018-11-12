package nl.freelist.activities;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import java.util.List;
import nl.freelist.freelist.R;
import nl.freelist.userInterfaceHelpers.EntryAdapter;
import nl.freelist.viewModels.CalendarViewModel;
import nl.freelist.database.Entry;
import nl.freelist.constants.ActivityConstants;

public class CalendarActivity extends AppCompatActivity {

  private CalendarViewModel calendarViewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_calendar);

    FloatingActionButton buttonAddEntry = findViewById(R.id.button_add_entry);
    buttonAddEntry.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(CalendarActivity.this, AddEditEntryActivity.class);
        intent.putExtra(ActivityConstants.EXTRA_REQUEST_TYPE_ADD, ActivityConstants.ADD_ENTRY_REQUEST);
        startActivityForResult(intent, ActivityConstants.ADD_ENTRY_REQUEST);
      }
    });

    RecyclerView recyclerView = findViewById(R.id.recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setHasFixedSize(true);

    final EntryAdapter adapter = new EntryAdapter();
    recyclerView.setAdapter(adapter);

    calendarViewModel = ViewModelProviders.of(this).get(CalendarViewModel.class);
    calendarViewModel.getAllEntries().observe(this, new Observer<List<Entry>>() {
      @Override
      public void onChanged(@Nullable List<Entry> entries) {
        // update RecyclerView
        adapter.setEntries(entries);
        Toast.makeText(CalendarActivity.this, "calendarViewModel onChanged!", Toast.LENGTH_SHORT)
            .show();
      }
    });

    new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
      @Override
      public boolean onMove(@NonNull RecyclerView recyclerView,
          @NonNull RecyclerView.ViewHolder viewHolder,
          @NonNull RecyclerView.ViewHolder viewHolder1) {
        // drag and drop functionality
        return false;
      }

      @Override
      public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        //viewHolder.getAdapterPosition() //where did we swipe?
        calendarViewModel.delete(adapter.getEntryAt(viewHolder.getAdapterPosition()));
        Toast.makeText(CalendarActivity.this, "Entry deleted", Toast.LENGTH_SHORT).show();
      }
    }).attachToRecyclerView(recyclerView);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == ActivityConstants.ADD_ENTRY_REQUEST && resultCode == RESULT_OK) {
      Toast.makeText(this, "Entry " + data.getStringExtra(ActivityConstants.EXTRA_TITLE) + " saved!",
          Toast.LENGTH_SHORT).show();
    } else if (requestCode == ActivityConstants.ADD_ENTRY_REQUEST && resultCode != RESULT_OK) {
      Toast.makeText(this, "Entry not saved.", Toast.LENGTH_SHORT).show();
    } else if (requestCode == ActivityConstants.EDIT_ENTRY_REQUEST && resultCode == RESULT_OK) {
      Toast.makeText(this, "Entry " + data.getStringExtra(ActivityConstants.EXTRA_TITLE) + " edited!",
          Toast.LENGTH_SHORT).show();
    } else if (requestCode == ActivityConstants.EDIT_ENTRY_REQUEST && resultCode != RESULT_OK) {
      Toast.makeText(this, "Entry " + data.getStringExtra(ActivityConstants.EXTRA_TITLE) + " not edited!",
          Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.delete_all_entries:
        calendarViewModel.deleteAllEntries();
        Toast.makeText(this, "All entries deleted", Toast.LENGTH_SHORT).show();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }

  }
}
