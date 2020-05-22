package nl.freelist.activities;

import static nl.freelist.freelist.R.drawable;
import static nl.freelist.freelist.R.id;
import static nl.freelist.freelist.R.layout;
import static nl.freelist.freelist.R.menu;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
  private String personId;
  private String parentOfThis;
  private int lastSavedSchedulerEventSequenceNumber = -1;
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

    setContentView(layout.activity_navigate_freelist);

    navigateEntriesViewModel = ViewModelProviders.of(this).get(NavigateEntriesViewModel.class);

    MySettings mySettings = new MySettings(this);
    personId = mySettings.getId();

    navigateEntriesViewModel.setParentId(personId);
    navigateEntriesViewModel.setPersonId(personId);

    initializeViews();

    setupActionBars();

    setupFloatingActionButton();

    setupSwipeActions();
  }

  private void initializeViews() {
    Log.d(TAG, "initializeViews called.");
    breadcrumb0 = findViewById(id.breadcrumb_level_0_text);
    breadcrumb1 = findViewById(id.breadcrumb_level_1_text);
    breadcrumb2 = findViewById(id.breadcrumb_level_2_text);
    breadcrumbDivider_0_1 = findViewById(id.breadcrumb_divider_0_1);
    breadcrumbDivider_1_2 = findViewById(id.breadcrumb_divider_1_2);
    recyclerView = findViewById(id.recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setHasFixedSize(true);
    adapter = new FreelistEntryAdapter(this);
    recyclerView.setAdapter(adapter);
    bottomAppBar = findViewById(id.bottom_app_bar);
  }

  private void setupSwipeActions() {
    Log.d(TAG, "setupSwipeActions called.");
    new ItemTouchHelper(
        new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
          @Override
          public boolean onMove(
              @NonNull RecyclerView recyclerView,
              @NonNull RecyclerView.ViewHolder viewHolder,
              @NonNull RecyclerView.ViewHolder viewHolder1) {
            // drag and drop functionality
            return false;
          }

          @Override
          public void onChildDraw(
              @NonNull Canvas c,
              @NonNull RecyclerView recyclerView,
              @NonNull ViewHolder viewHolder,
              float dX,
              float dY,
              int actionState,
              boolean isCurrentlyActive) {

            final ColorDrawable background = new ColorDrawable(0xFFFAFAFA);
            background.setBounds(
                viewHolder.itemView.getRight() + (int) dX,
                viewHolder.itemView.getTop(),
                viewHolder.itemView.getRight(),
                viewHolder.itemView.getBottom());
            background.draw(c);

            Drawable firstIcon =
                ContextCompat.getDrawable(getApplicationContext(), drawable.ic_line_chart);
            Drawable secondIcon =
                ContextCompat.getDrawable(getApplicationContext(), drawable.ic_score);
            Drawable thirdIcon =
                ContextCompat.getDrawable(getApplicationContext(), drawable.ic_bar_chart);
            // iconDimensions
            int iconHeight = 100;
            int iconWidth = 100;
            int iconMargin = 2;

            // disable swiping too much?
            if (-dX > ((iconWidth + iconMargin) * 5)) {
              dX = -((iconWidth + iconMargin) * 5);
            }

            int iconTop =
                viewHolder.itemView.getTop() + (viewHolder.itemView.getHeight() - iconHeight) / 2;
            int iconBottom = iconTop + iconHeight;

            int firstIconLeft =
                (int) (viewHolder.itemView.getRight() - iconMargin - iconWidth * 1.5);
            int firstIconRight =
                (int) (viewHolder.itemView.getRight() - iconMargin - iconWidth * 0.5);
            firstIcon.setBounds(firstIconLeft, iconTop, firstIconRight, iconBottom);
            firstIcon.draw(c);

            int secondIconLeft = viewHolder.itemView.getRight() - iconMargin * 2 - iconWidth * 3;
            int secondIconRight = viewHolder.itemView.getRight() - iconMargin * 2 - iconWidth * 2;
            secondIcon.setBounds(secondIconLeft, iconTop, secondIconRight, iconBottom);
            secondIcon.draw(c);

            int thirdIconLeft =
                (int) (viewHolder.itemView.getRight() - iconMargin * 3 - iconWidth * 4.5);
            int thirdIconRight =
                (int) (viewHolder.itemView.getRight() - iconMargin * 3 - iconWidth * 3.5);
            thirdIcon.setBounds(thirdIconLeft, iconTop, thirdIconRight, iconBottom);
            thirdIcon.draw(c);

            super.onChildDraw(
                c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
          }

          @Override
          public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            //                adapter.notifyItemChanged(viewHolder.getAdapterPosition());
          }
        })
        .attachToRecyclerView(recyclerView);

    new ItemTouchHelper(
        new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
          @Override
          public boolean onMove(
              @NonNull RecyclerView recyclerView,
              @NonNull RecyclerView.ViewHolder viewHolder,
              @NonNull RecyclerView.ViewHolder viewHolder1) {
            // drag and drop functionality
            return false;
          }

          @Override
          public void onChildDraw(
              @NonNull Canvas c,
              @NonNull RecyclerView recyclerView,
              @NonNull ViewHolder viewHolder,
              float dX,
              float dY,
              int actionState,
              boolean isCurrentlyActive) {

            final ColorDrawable background = new ColorDrawable(0xFFFAFAFA);
            background.setBounds(
                viewHolder.itemView.getLeft(),
                viewHolder.itemView.getTop(),
                (int) dX,
                viewHolder.itemView.getBottom());
            background.draw(c);

            Drawable firstIcon =
                ContextCompat.getDrawable(getApplicationContext(), drawable.ic_done_outline);
            Drawable secondIcon =
                ContextCompat.getDrawable(getApplicationContext(), drawable.ic_thumb_up);
            Drawable thirdIcon =
                ContextCompat.getDrawable(getApplicationContext(), drawable.ic_thumb_down);
            Drawable fourthIcon =
                ContextCompat.getDrawable(getApplicationContext(), drawable.ic_edit);
            Drawable fifthIcon =
                ContextCompat.getDrawable(getApplicationContext(), drawable.ic_delete);

            int iconHeight = 80;
            int iconWidth = 80;
            int iconMargin = 2;

            // disable swiping too much?
            if (dX > (iconWidth + iconMargin) * 8.2) {
              dX = (iconWidth + iconMargin) * 8.2F;
            }

            int iconTop =
                viewHolder.itemView.getTop() + (viewHolder.itemView.getHeight() - iconHeight) / 2;
            int iconBottom = iconTop + iconHeight;

            int firstIconLeft =
                (int) (viewHolder.itemView.getLeft() + iconMargin + iconWidth * 0.5);
            int firstIconRight =
                (int) (viewHolder.itemView.getLeft() + iconMargin + iconWidth * 1.5);
            firstIcon.setBounds(firstIconLeft, iconTop, firstIconRight, iconBottom);
            firstIcon.draw(c);

            int secondIconLeft = viewHolder.itemView.getLeft() + iconMargin * 2 + iconWidth * 2;
            int secondIconRight = viewHolder.itemView.getLeft() + iconMargin * 2 + iconWidth * 3;
            secondIcon.setBounds(secondIconLeft, iconTop, secondIconRight, iconBottom);
            secondIcon.draw(c);

            int thirdIconLeft =
                (int) (viewHolder.itemView.getLeft() + iconMargin * 2 + iconWidth * 3.5);
            int thirdIconRight =
                (int) (viewHolder.itemView.getLeft() + iconMargin * 2 + iconWidth * 4.5);
            thirdIcon.setBounds(thirdIconLeft, iconTop, thirdIconRight, iconBottom);
            thirdIcon.draw(c);

            int fourthIconLeft = viewHolder.itemView.getLeft() + iconMargin * 2 + iconWidth * 5;
            int fourthIconRight = viewHolder.itemView.getLeft() + iconMargin * 2 + iconWidth * 6;
            fourthIcon.setBounds(fourthIconLeft, iconTop, fourthIconRight, iconBottom);
            fourthIcon.draw(c);

            int fifthIconLeft =
                (int) (viewHolder.itemView.getLeft() + iconMargin * 2 + iconWidth * 6.5);
            int fifthIconRight =
                (int) (viewHolder.itemView.getLeft() + iconMargin * 2 + iconWidth * 7.5);
            fifthIcon.setBounds(fifthIconLeft, iconTop, fifthIconRight, iconBottom);
            fifthIcon.draw(c);

            super.onChildDraw(
                c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
          }

          @Override
          public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            // adapter.notifyItemChanged(viewHolder.getAdapterPosition());
          }
        })
        .attachToRecyclerView(recyclerView);
  }

  private void setupFloatingActionButton() {
    Log.d(TAG, "setupFloatingActionButton called.");
    FloatingActionButton buttonAddEntry = findViewById(id.button_add_entry);
    buttonAddEntry.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent intent = new Intent(NavigateFreelistActivity.this, AddEditEntryActivity.class);
            intent.putExtra(Constants.EXTRA_REQUEST_TYPE_ADD, Constants.ADD_ENTRY_REQUEST);
            intent.putExtra(
                Constants.EXTRA_ENTRY_PARENT_ID, navigateEntriesViewModel.getParentId());
            intent.putExtra(
                Constants.EXTRA_SCHEDULER_EVENT_SEQUENCE_NUMBER,
                lastSavedSchedulerEventSequenceNumber);
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
    Log.d(TAG, "lastSchedulerEventSequenceNumber: " + lastSavedSchedulerEventSequenceNumber);
    navigateEntriesViewModel
        .getViewModelEntries()
        .subscribeOn(Schedulers.single())
        .observeOn(Schedulers.single())
        .subscribe(
            viewModelEntries -> {
              // update RecyclerView
              runOnUiThread(
                  new Runnable() {
                    @Override
                    public void run() {
                      adapter.setEntries(viewModelEntries.getViewModelEntryList());
                      lastSavedSchedulerEventSequenceNumber =
                          viewModelEntries.getLastAppliedSchedulerSequenceNumber();
                      Log.d(
                          TAG,
                          "lastSchedulerEventSequenceNumber updated with result from viewModelEntries: "
                              + lastSavedSchedulerEventSequenceNumber);
                      //                      Toast.makeText(
                      //                          NavigateFreelistActivity.this,
                      //                          "navigateEntriesViewModel recyclerView
                      // refreshed!",
                      //                          Toast.LENGTH_SHORT)
                      //                          .show();
                    }
                  });
            });
  }

  private void updateBreadcrumb() {
    Log.d(TAG, "updateBreadcrumb called.");
    navigateEntriesViewModel
        .getBreadcrumbEntries()
        .subscribeOn(Schedulers.single())
        .observeOn(Schedulers.single())
        .subscribe(
            entries -> {
              // update RecyclerView
              runOnUiThread(
                  new Runnable() {
                    @Override
                    public void run() {
                      initializeBreadcrumb(entries);
                      //                      Toast.makeText(
                      //                          NavigateFreelistActivity.this,
                      //                          "navigateEntriesViewModel breadcrumb refreshed!",
                      //                          Toast.LENGTH_SHORT)
                      //                          .show();
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
        breadcrumb0.setOnClickListener(view -> updateRecyclerViewWithParentUuid(personId));
        parentOfThis = personId;
        breadcrumbDivider_0_1.setText(">");
        breadcrumb1.setText(entries.get(0).getTitle());
        breadcrumb1.setOnClickListener(
            view -> updateRecyclerViewWithParentUuid(entries.get(0).getEntryId()));
        breadcrumbDivider_1_2.setText("");
        breadcrumb2.setText("");
        break;
      case 2:
        if (entries.get(0).getParentEntryId().equals(personId)) {
          breadcrumb0.setText("Home");
        } else {
          breadcrumb0.setText("...");
        }
        breadcrumb0.setOnClickListener(
            view -> updateRecyclerViewWithParentUuid(entries.get(0).getParentEntryId()));
        breadcrumbDivider_0_1.setText(">");
        breadcrumb1.setText(entries.get(0).getTitle());
        breadcrumb1.setOnClickListener(
            view -> updateRecyclerViewWithParentUuid(entries.get(0).getEntryId()));
        parentOfThis = entries.get(0).getEntryId();
        breadcrumbDivider_1_2.setText(">");
        breadcrumb2.setText(entries.get(1).getTitle());
        breadcrumb2.setOnClickListener(
            view -> updateRecyclerViewWithParentUuid(entries.get(1).getEntryId()));
        break;
      default:
        breadcrumb0.setText("Home");
        breadcrumb0.setOnClickListener(view -> updateRecyclerViewWithParentUuid(personId));
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

    // TopAppBar
    getSupportActionBar().setTitle("My Freelists");
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    // override onCreateOptionsMenu and onOptionsItemSelected for TopAppBar
    bottomAppBar.replaceMenu(menu.bottom_app_bar_menu);
    bottomAppBar
        .getMenu()
        .findItem(id.bottom_app_bar_freelists)
        .getIcon()
        .setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
    bottomAppBar.setOnMenuItemClickListener(
        new BottomAppBar.OnMenuItemClickListener() {
          @Override
          public boolean onMenuItemClick(MenuItem menuItem) {
            int id = menuItem.getItemId();
            switch (id) {
              case R.id.bottom_app_bar_freelists:
                Toast.makeText(
                    NavigateFreelistActivity.this,
                    "Freelists already selected",
                    Toast.LENGTH_SHORT)
                    .show();
                return true;
              case R.id.bottom_app_bar_calendar:
                Toast.makeText(
                    NavigateFreelistActivity.this, "Calendar selected", Toast.LENGTH_SHORT)
                    .show();
                Intent navigateFreelistIntent =
                    new Intent(NavigateFreelistActivity.this, CalendarActivity.class);
                startActivity(navigateFreelistIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
              case R.id.bottom_app_bar_search:
                Toast.makeText(NavigateFreelistActivity.this, "Search selected", Toast.LENGTH_SHORT)
                    .show();
                Intent searchIntent =
                    new Intent(NavigateFreelistActivity.this, SearchActivity.class);
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
      case id.delete_all_entries:
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
      case id.settings:
        Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(NavigateFreelistActivity.this, SettingsActivity.class);
        startActivity(intent);
        return true;
      case id.undo:
        Toast.makeText(this, "Undo selected", Toast.LENGTH_SHORT).show();
        return true;
      case android.R.id.home:
        if (parentOfThis != null) {
          navigateEntriesViewModel.setParentId(parentOfThis);
        } else {
          navigateEntriesViewModel.setParentId(personId);
        }
        updateView();
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void updateRecyclerViewWithParentUuid(String parentToSet) {
    navigateEntriesViewModel.setParentId(parentToSet);
    updateView();
  }

  @Override
  public void onItemClick(View view, int position) {
    Log.d(TAG, "onItemClick called.");
    String parentToSet;
    Log.d(TAG, "navigate down clicked");
    parentToSet = adapter.getEntryAt(position).getEntryId();
    navigateEntriesViewModel.setParentId(parentToSet);
    updateView();
  }
}
