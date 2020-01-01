package nl.freelist.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import io.reactivex.schedulers.Schedulers;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import nl.freelist.androidCrossCuttingConcerns.MySettings;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.domain.valueObjects.DateTimeRange;
import nl.freelist.domain.valueObjects.Email;
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

    setContentView(R.layout.activity_navigate_freelist);

    navigateEntriesViewModel = ViewModelProviders.of(this)
        .get(NavigateEntriesViewModel.class);

    try {
      initializeSharedPreferences();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }

    navigateEntriesViewModel.setParentUuid(myUuid);

    initializeViews();

    setupActionBars();

    setupFloatingActionButton();

    setupSwipeActions();
  }

  private void initializeSharedPreferences()
      throws Exception {
    MySettings mySettings = new MySettings(this);
    SharedPreferences sharedPreferences;
    Editor editor;
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    //Todo: possible to use MySetting without having to pass the application context to it?
    //use getSharedPreferences(String name, int mode) rather than getPreferences (int mode)

    //If Resource not yet set, create (and persist) Resource with default anonymous@freelist.nl
    if (mySettings.getResourceUuid() == null) {
      if (!sharedPreferences.contains(Constants.SETTINGS_USER_UUID)) {
        editor = sharedPreferences.edit();
        Email email = new Email("anonymous@freelist.nl");
        DateTimeRange lifetimeDateTimeRange = DateTimeRange.Create(
            OffsetDateTime.now(ZoneOffset.UTC),
            OffsetDateTime.now(ZoneOffset.UTC).plusYears(1));

        editor.putString(
            Constants.SETTINGS_USER_UUID,
            UUID.nameUUIDFromBytes(email.getEmailString().getBytes()).toString());
        editor.putString(
            Constants.SETTINGS_RESOURCE_UUID,
            UUID.nameUUIDFromBytes(email.getEmailString().getBytes()).toString());
        //Make sure Resource is created + persisted before committing to SharedPreferences
        //Not asynchronous as it is blocking for application to continue
        if (!navigateEntriesViewModel.createResource(email, email, lifetimeDateTimeRange)
            .isSuccess()) {
          throw new Exception("Couldn't create a Resource");
        }
        editor.commit();
      }
    }
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
    int entriesSize = 0;
    if (entries != null) {
      entriesSize = entries.size();
    }
    switch (entriesSize) {
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
    updateView();
  }


  private void setupActionBars() {
    Log.d(TAG, "setupActionBars called.");

    //TopAppBar
    getSupportActionBar()
        .setHomeAsUpIndicator(R.drawable.ic_close);
    setTitle("My Freelists");
    //override onCreateOptionsMenu and onOptionsItemSelected for TopAppBar

    bottomAppBar.replaceMenu(R.menu.bottom_app_bar_menu);
    bottomAppBar.getMenu().findItem(R.id.bottom_app_bar_freelists).getIcon()
        .setColorFilter(Color.BLACK,
            PorterDuff.Mode.SRC_IN);
    bottomAppBar.setOnMenuItemClickListener(new BottomAppBar.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
          case R.id.bottom_app_bar_freelists:
            Toast.makeText(NavigateFreelistActivity.this, "Freelists already selected",
                Toast.LENGTH_SHORT).show();
            return true;
          case R.id.bottom_app_bar_calendar:
            Toast.makeText(NavigateFreelistActivity.this, "Calendar selected", Toast.LENGTH_SHORT)
                .show();
            Intent navigateFreelistIntent = new Intent(NavigateFreelistActivity.this,
                CalendarActivity.class);
            startActivity(navigateFreelistIntent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
          case R.id.bottom_app_bar_search:
            Toast.makeText(NavigateFreelistActivity.this, "Search selected", Toast.LENGTH_SHORT)
                .show();
            Intent searchIntent = new Intent(NavigateFreelistActivity.this, SearchActivity.class);
            startActivity(searchIntent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return false;
      }
    });
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
        navigateEntriesViewModel
            .deleteAllEntriesFromRepository()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                result -> {
                  // update user with result message
                  runOnUiThread(
                      new Runnable() {
                        @Override
                        public void run() {
                          // do stuff
                          if (result) {
                            Toast.makeText(
                                NavigateFreelistActivity.this,
                                "repository entries destroyed!",
                                Toast.LENGTH_SHORT)
                                .show();
                            updateView();
                          } else {
                            Toast.makeText(
                                NavigateFreelistActivity.this,
                                "repository entries NOT destroyed!",
                                Toast.LENGTH_SHORT)
                                .show();
                          }
                        }
                      });
                });
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
    String parentToSet = adapter.getEntryAt(position).getUuid();
    navigateEntriesViewModel.updateParentUuid(parentToSet);
    updateView();
    adapter.setCurrentId(parentToSet);
  }


}
