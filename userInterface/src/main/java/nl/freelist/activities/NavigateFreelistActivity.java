package nl.freelist.activities;

import static nl.freelist.freelist.R.drawable;
import static nl.freelist.freelist.R.id;
import static nl.freelist.freelist.R.layout;
import static nl.freelist.freelist.R.menu;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
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
import nl.freelist.androidCrossCuttingConcerns.MySettings;
import nl.freelist.data.dto.ViewModelEntries;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.freelist.R;
import nl.freelist.recyclerviewHelpers.FreelistEntryAdapter;
import nl.freelist.recyclerviewHelpers.ItemClickListener;
import nl.freelist.viewModelPerActivity.NavigateEntriesViewModel;

public class NavigateFreelistActivity extends AppCompatActivity implements ItemClickListener {

  private static final String TAG = "NavigateFreelistActivity";
  private String personId;
  private String parentSet;
  private NavigateEntriesViewModel navigateEntriesViewModel;
  private FreelistEntryAdapter adapter;
  private RecyclerView recyclerView;
  private TextView breadcrumb0;
  private TextView breadcrumb1;
  private TextView breadcrumb2;
  private TextView breadcrumbDivider_0_1;
  private TextView breadcrumbDivider_1_2;
  private BottomAppBar bottomAppBar;
  private ViewModelEntries viewModelEntries;

  public NavigateFreelistActivity() {
  }

  @Override
  protected void onResume() {
    Log.d(TAG, "onResume called.");
    super.onResume();
    setupActionBars();
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

    initializeViews();
    setupActionBars();
    setupFloatingActionButton();
    setupSwipeActions();
    refreshViewModel();
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
                viewHolder.itemView.getTop()
                    + (viewHolder.itemView.getHeight() - iconHeight) / 2;
            int iconBottom = iconTop + iconHeight;

            int firstIconLeft =
                (int) (viewHolder.itemView.getRight() - iconMargin - iconWidth * 1.5);
            int firstIconRight =
                (int) (viewHolder.itemView.getRight() - iconMargin - iconWidth * 0.5);
            firstIcon.setBounds(firstIconLeft, iconTop, firstIconRight, iconBottom);
            firstIcon.draw(c);

            int secondIconLeft =
                viewHolder.itemView.getRight() - iconMargin * 2 - iconWidth * 3;
            int secondIconRight =
                viewHolder.itemView.getRight() - iconMargin * 2 - iconWidth * 2;
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

            int iconHeight = 80;
            int iconWidth = 80;
            int iconMargin = 2;
            float swipeLimit = 8.2F;
            float right = dX;

            int from = Color.argb(255, 21, 183, 145);
            int to = Color.argb(255, 250, 250, 250);
            float swipeFraction =
                (dX * dX) / ((swipeLimit * (iconWidth + iconMargin)) * (swipeLimit * (iconWidth
                    + iconMargin)));
            if (swipeFraction > 1) {
              swipeFraction = 1;
            }
            int color = (int) new ArgbEvaluator().evaluate(swipeFraction, from, to);

            final ColorDrawable background = new ColorDrawable(color);
            background.setBounds(
                viewHolder.itemView.getPaddingLeft(),
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

            // disable swiping too much?
            if (dX > (iconWidth + iconMargin) * swipeLimit) {
              dX = (iconWidth + iconMargin) * swipeLimit;
            }

            int iconTop =
                viewHolder.itemView.getTop()
                    + (viewHolder.itemView.getHeight() - iconHeight) / 2;
            int iconBottom = iconTop + iconHeight;

            int firstIconLeft =
                (int) (viewHolder.itemView.getLeft() + iconMargin + iconWidth * 0.5);
            int firstIconRight =
                (int) (viewHolder.itemView.getLeft() + iconMargin + iconWidth * 1.5);
            firstIcon.setBounds(firstIconLeft, iconTop, firstIconRight, iconBottom);
            firstIcon.draw(c);

            int secondIconLeft = viewHolder.itemView.getLeft() + iconMargin * 2 + iconWidth * 2;
            int secondIconRight =
                viewHolder.itemView.getLeft() + iconMargin * 2 + iconWidth * 3;
            secondIcon.setBounds(secondIconLeft, iconTop, secondIconRight, iconBottom);
            secondIcon.draw(c);

            int thirdIconLeft =
                (int) (viewHolder.itemView.getLeft() + iconMargin * 2 + iconWidth * 3.5);
            int thirdIconRight =
                (int) (viewHolder.itemView.getLeft() + iconMargin * 2 + iconWidth * 4.5);
            thirdIcon.setBounds(thirdIconLeft, iconTop, thirdIconRight, iconBottom);
            thirdIcon.draw(c);

            int fourthIconLeft = viewHolder.itemView.getLeft() + iconMargin * 2 + iconWidth * 5;
            int fourthIconRight =
                viewHolder.itemView.getLeft() + iconMargin * 2 + iconWidth * 6;
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
    buttonAddEntry.setColorFilter(Color.parseColor("#15b790"));
    buttonAddEntry.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent intent = new Intent(NavigateFreelistActivity.this, AddEditEntryActivity.class);
            intent.putExtra(Constants.EXTRA_REQUEST_TYPE_ADD, Constants.ADD_ENTRY_REQUEST);
            intent.putExtra(Constants.EXTRA_ENTRY_PARENT_ID, parentSet);
            intent.putExtra(
                Constants.EXTRA_SCHEDULER_EVENT_SEQUENCE_NUMBER,
                viewModelEntries.getLastAppliedSchedulerSequenceNumber());
            startActivityForResult(intent, Constants.ADD_ENTRY_REQUEST);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
          }
        });
  }

  private void updateView() {
    if (parentSet != null && parentSet.equals(personId)) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    } else {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    if (viewModelEntries != null) {
      adapter.setEntries(viewModelEntries.getViewModelEntryList(parentSet));
      adapter.refresh();
      updateBreadcrumb();
    }
  }

  private void refreshViewModel() {
    navigateEntriesViewModel
        .getViewModelEntries(personId)
        .subscribeOn(Schedulers.single())
        .observeOn(Schedulers.single())
        .subscribe(
            viewModelEntriesResult -> {
              runOnUiThread(
                  new Runnable() {
                    @Override
                    public void run() {
                      viewModelEntries = viewModelEntriesResult;
                      if (parentSet == null) {
                        parentSet = viewModelEntries.getPersonId();
                      }
                      updateView();
                      Log.d(
                          TAG,
                          "lastSchedulerEventSequenceNumber updated with result from viewModelEntries: "
                              + viewModelEntries.getLastAppliedSchedulerSequenceNumber());
                    }
                  });
            });
  }

  private void updateBreadcrumb() {
    Log.d(TAG, "updateBreadcrumb called.");
    breadcrumb0.setText("Home");
    breadcrumb0.setOnClickListener(
        view -> {
          parentSet = personId;
          updateView();
        });
    if (parentSet.equals(personId)) {
      breadcrumbDivider_0_1.setText("");
      breadcrumb1.setText("");
      breadcrumbDivider_1_2.setText("");
      breadcrumb2.setText("");
    } else {
      breadcrumbDivider_0_1.setText(">");
      if (viewModelEntries.getEntry(parentSet).getParentEntryId().equals(personId)) {
        breadcrumb1.setText(viewModelEntries.getEntry(parentSet).getTitle());
        breadcrumbDivider_1_2.setText("");
        breadcrumb2.setText("");
      } else {
        breadcrumb1.setText("...");
        breadcrumbDivider_1_2.setText(">");
        breadcrumb2.setText(viewModelEntries.getEntry(parentSet).getTitle());
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.d(TAG, "onActivityResult called.");
    super.onActivityResult(requestCode, resultCode, data);
    refreshViewModel();
  }

  private void setupActionBars() {
    Log.d(TAG, "setupActionBars called.");

    // TopAppBar
    getSupportActionBar().setTitle("My Freelists");

    // override onCreateOptionsMenu and onOptionsItemSelected for TopAppBar
    bottomAppBar.replaceMenu(menu.bottom_app_bar_menu);

    bottomAppBar.getMenu().findItem(id.bottom_app_bar_freelists).getIcon().setAlpha(220);

    bottomAppBar.getMenu().findItem(id.bottom_app_bar_calendar).getIcon().setAlpha(120);

    bottomAppBar.getMenu().findItem(id.bottom_app_bar_search).getIcon().setAlpha(120);

    bottomAppBar.setOnMenuItemClickListener(
        new BottomAppBar.OnMenuItemClickListener() {
          @Override
          public boolean onMenuItemClick(MenuItem menuItem) {
            int id = menuItem.getItemId();
            switch (id) {
              case R.id.bottom_app_bar_freelists:
                return true;
              case R.id.bottom_app_bar_calendar:
                Intent navigateFreelistIntent =
                    new Intent(NavigateFreelistActivity.this, CalendarActivity.class);
                startActivity(navigateFreelistIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
              case R.id.bottom_app_bar_search:
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
            .subscribeOn(Schedulers.single())
            .observeOn(Schedulers.single())
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
                            parentSet = personId;
                            refreshViewModel();
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
        parentSet = viewModelEntries.getEntry(parentSet).getParentEntryId();
        updateView();
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onItemClick(View view, int position) {
    Log.d(TAG, "onItemClick called.");
    Log.d(TAG, "navigate down clicked");
    parentSet = adapter.getEntryAt(position).getEntryId();
    updateView();
  }
}
