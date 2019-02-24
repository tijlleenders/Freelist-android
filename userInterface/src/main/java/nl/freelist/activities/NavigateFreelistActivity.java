package nl.freelist.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import nl.freelist.androidCrossCuttingConcerns.MySettings;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.freelist.R;
import nl.freelist.recyclerviewHelpers.FreelistEntryAdapter;
import nl.freelist.recyclerviewHelpers.ItemClickListener;
import nl.freelist.viewModelPerActivity.NavigateEntriesViewModel;

public class NavigateFreelistActivity extends AppCompatActivity implements ItemClickListener {

  private static final String TAG = "NavigateFreelistActivity";
  private String myUuid;

  private NavigateEntriesViewModel navigateEntriesViewModel;
  private FreelistEntryAdapter adapter;
  private RecyclerView recyclerView;
  private TextView breadcrumb0;
  private TextView breadcrumb1;
  private TextView breadcrumb2;
  private TextView breadcrumbDivider_0_1;
  private TextView breadcrumbDivider_1_2;
  private BottomAppBar bottomAppBar;

  public NavigateFreelistActivity() {
  }

  @Override
  protected void onResume() {
    Log.d(TAG, "onResume called.");
    super.onResume();
    updateView();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate called.");
    super.onCreate(savedInstanceState);

    initializeSharedPreferences();

    setContentView(R.layout.activity_navigate_freelist);

    navigateEntriesViewModel = ViewModelProviders.of(this)
        .get(NavigateEntriesViewModel.class);
    navigateEntriesViewModel.setParentUuid(myUuid);

    initializeViews();

    setupActionBars();

    setupFloatingActionButton();

    setupSwipeActions();
  }

  private void initializeSharedPreferences() {
    MySettings mySettings = new MySettings(this);
    myUuid = mySettings.getUuid();
  }

  private void initializeViews() {
    Log.d(TAG, "initializeViews called.");
    breadcrumb0 = findViewById(R.id.breadcrumb_level_0_text);
    breadcrumb1 = findViewById(R.id.breadcrumb_level_1_text);
    breadcrumb2 = findViewById(R.id.breadcrumb_level_2_text);
    breadcrumbDivider_0_1 = findViewById(R.id.breadcrumb_divider_0_1);
    breadcrumbDivider_1_2 = findViewById(R.id.breadcrumb_divider_1_2);
    recyclerView = findViewById(R.id.recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setHasFixedSize(true);
    adapter = new FreelistEntryAdapter(this);
    recyclerView.setAdapter(adapter);
    bottomAppBar = findViewById(R.id.bottom_app_bar);
  }

  private void setupSwipeActions() {
    Log.d(TAG, "setupSwipeActions called.");
    new ItemTouchHelper(
        new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
          @Override
          public boolean onMove(
              @NonNull RecyclerView recyclerView,
              @NonNull RecyclerView.ViewHolder viewHolder,
              @NonNull RecyclerView.ViewHolder viewHolder1) {
            // drag and drop functionality
            return false;
          }

          @Override
          public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            // viewHolder.getAdapterPosition() //where did we swipe?
            navigateEntriesViewModel.delete(
                adapter.getEntryAt(viewHolder.getAdapterPosition()));
            Toast.makeText(NavigateFreelistActivity.this, "Entry deleted", Toast.LENGTH_SHORT)
                .show();
          }


        })
        .attachToRecyclerView(recyclerView);
  }

  private void setupFloatingActionButton() {
    Log.d(TAG, "setupFloatingActionButton called.");
    FloatingActionButton buttonAddEntry = findViewById(R.id.button_add_entry);
    buttonAddEntry.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent intent = new Intent(NavigateFreelistActivity.this, AddEditEntryActivity.class);
            intent.putExtra(
                Constants.EXTRA_REQUEST_TYPE_ADD, Constants.ADD_ENTRY_REQUEST);
            intent.putExtra(
                Constants.EXTRA_ENTRY_PARENT_ID, navigateEntriesViewModel.getParentUuid());
            startActivityForResult(intent, Constants.ADD_ENTRY_REQUEST);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
          }
        });
  }

  private void setupActionBars() {
    Log.d(TAG, "setupActionBars called.");
    getSupportActionBar()
        .setHomeAsUpIndicator(R.drawable.ic_close);
    setTitle("My Freelists");
    bottomAppBar.replaceMenu(R.menu.bottom_app_bar_menu);
  }


  private void updateView() {
    updateRecyclerView();
    updateBreadcrumb();
  }

  private void updateRecyclerView() {
    Log.d(TAG, "updateRecyclerView called.");
    navigateEntriesViewModel
        .getAllChildrenEntries()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(
            entries -> {
              // update RecyclerView
              runOnUiThread(
                  new Runnable() {
                    @Override
                    public void run() {
                      adapter.setEntries(entries);
                      Toast.makeText(
                          NavigateFreelistActivity.this,
                          "navigateEntriesViewModel recyclerView refreshed!",
                          Toast.LENGTH_SHORT)
                          .show();
                    }
                  });
            });
  }

  private void updateBreadcrumb() {
    Log.d(TAG, "updateBreadcrumb called.");
    navigateEntriesViewModel
        .getBreadcrumbEntries()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(
            entries -> {
              // update RecyclerView
              runOnUiThread(
                  new Runnable() {
                    @Override
                    public void run() {
                      initializeBreadcrumb(entries);
                      Toast.makeText(
                          NavigateFreelistActivity.this,
                          "navigateEntriesViewModel breadcrumb refreshed!",
                          Toast.LENGTH_SHORT)
                          .show();
                    }
                  });
            });
  }

  private void initializeBreadcrumb(List<ViewModelEntry> entries) {
    switch (entries.size()) {
      case 1:
        breadcrumb0.setText("Home");
        breadcrumb0.setOnClickListener(view -> updateRecyclerViewWithParentUuid(myUuid));
        breadcrumbDivider_0_1.setText(">");
        breadcrumb1.setText(entries.get(0).getTitle());
        breadcrumb1
            .setOnClickListener(view -> updateRecyclerViewWithParentUuid(entries.get(0).getUuid()));
        breadcrumbDivider_1_2.setText("");
        breadcrumb2.setText("");
        break;
      case 2:
        if (entries.get(0).getParentUuid().equals(myUuid)) {
          breadcrumb0.setText("Home");
        } else {
          breadcrumb0.setText("...");
        }
        breadcrumb0.setOnClickListener(
            view -> updateRecyclerViewWithParentUuid(entries.get(0).getParentUuid()));
        breadcrumbDivider_0_1.setText(">");
        breadcrumb1.setText(entries.get(0).getTitle());
        breadcrumb1
            .setOnClickListener(view -> updateRecyclerViewWithParentUuid(entries.get(0).getUuid()));
        breadcrumbDivider_1_2.setText(">");
        breadcrumb2.setText(entries.get(1).getTitle());
        breadcrumb2
            .setOnClickListener(view -> updateRecyclerViewWithParentUuid(entries.get(1).getUuid()));
        break;
      default:
        breadcrumb0.setText("Home");
        breadcrumb0.setOnClickListener(view -> updateRecyclerViewWithParentUuid(myUuid));
        breadcrumbDivider_0_1.setText("");
        breadcrumb1.setText("");
        breadcrumbDivider_1_2.setText("");
        breadcrumb2.setText("");
        break;
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.d(TAG, "onActivityResult called.");
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == Constants.ADD_ENTRY_REQUEST && resultCode == RESULT_OK) {
      Toast.makeText(
          this,
          "Entry " + data.getStringExtra(Constants.EXTRA_TITLE) + " saved!",
          Toast.LENGTH_SHORT)
          .show();
      updateView();
    } else if (requestCode == Constants.ADD_ENTRY_REQUEST && resultCode != RESULT_OK) {
      Toast.makeText(this, "Entry not saved.", Toast.LENGTH_SHORT).show();
    } else if (requestCode == Constants.EDIT_ENTRY_REQUEST && resultCode == RESULT_OK) {
      Toast.makeText(
          this,
          "Entry " + data.getStringExtra(Constants.EXTRA_TITLE) + " edited!",
          Toast.LENGTH_SHORT)
          .show();
      updateView();
    } else if (requestCode == Constants.EDIT_ENTRY_REQUEST && resultCode != RESULT_OK) {
      Toast.makeText(
          this,
          "Entry " + data.getStringExtra(Constants.EXTRA_TITLE) + " not edited!",
          Toast.LENGTH_SHORT)
          .show();
    }
    updateView();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    Log.d(TAG, "onCreateOptionsMenu called.");
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Log.d(TAG, "onOptionsItemSelected called.");
    switch (item.getItemId()) {
      case R.id.delete_all_entries:
        navigateEntriesViewModel.deleteAllEntries();
        Toast.makeText(this, "All entries deleted", Toast.LENGTH_SHORT).show();
        return true;
      case R.id.settings:
        Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(NavigateFreelistActivity.this, SettingsActivity.class);
        startActivity(intent);
        return true;
      case R.id.undo:
        Toast.makeText(this, "Undo selected", Toast.LENGTH_SHORT).show();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void updateRecyclerViewWithParentUuid(String parentToSet) {
    navigateEntriesViewModel.updateParentUuid(parentToSet);
    updateView();
    adapter.setCurrentId(parentToSet);
  }

  @Override
  public void onItemClick(View view, int position) {
    Log.d(TAG, "onItemClick called.");
    int viewType = adapter.getItemViewType(position);
    String parentToSet = adapter.getEntryAt(position).getUuid();
    navigateEntriesViewModel.updateParentUuid(parentToSet);
    updateView();
    adapter.setCurrentId(parentToSet);
  }


}
