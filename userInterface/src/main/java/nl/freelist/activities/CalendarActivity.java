package nl.freelist.activities;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import nl.freelist.dialogs.NoticeDialogListener;
import nl.freelist.dialogs.ScheduleForwardPickerDialog;
import nl.freelist.freelist.R;
import nl.freelist.recyclerviewHelpers.CalendarEntryAdapter;
import nl.freelist.recyclerviewHelpers.ItemClickListener;
import nl.freelist.viewModelPerActivity.CalendarViewModel;

public class CalendarActivity extends AppCompatActivity implements ItemClickListener,
    NoticeDialogListener {

  private static final String TAG = "CalendarActivity";
  private String personId;

  private CalendarEntryAdapter adapter;
  private RecyclerView recyclerView;

  private CalendarViewModel calendarViewModel;
  private BottomAppBar bottomAppBar;

  public CalendarActivity() {
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
    setContentView(R.layout.activity_calendar);

    initializeSharedPreferences();

    calendarViewModel = ViewModelProviders.of(this).get(CalendarViewModel.class);
    calendarViewModel.setPersonId(personId);

    initializeViews();

    setupActionBars();
    setupSwipeActions();

    setupFloatingActionButton();
  }

  private void initializeSharedPreferences() {
    MySettings mySettings = new MySettings(this);
    personId = mySettings.getId();
  }

  private void initializeViews() {
    Log.d(TAG, "initializeViews called.");
    recyclerView = findViewById(R.id.recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setHasFixedSize(true);
    adapter = new CalendarEntryAdapter(this);
    recyclerView.setAdapter(adapter);
    bottomAppBar = findViewById(R.id.bottom_app_bar);
  }

  private void updateView() {
    updateRecyclerView();
  }

  private void updateRecyclerView() {
    Log.d(TAG, "updateRecyclerView called.");
    calendarViewModel
        .getViewModelEntries()
        .subscribeOn(Schedulers.single())
        .observeOn(Schedulers.single())
        .subscribe(
            calendarEntries -> {
              // update RecyclerView
              runOnUiThread(
                  new Runnable() {
                    @Override
                    public void run() {
                      adapter.setEntries(calendarEntries.getViewModelEntryList());
                    }
                  });
            });
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
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_line_chart);
            Drawable secondIcon =
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_score);
            Drawable thirdIcon =
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_bar_chart);
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
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_done_outline);
            Drawable secondIcon =
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_thumb_up);
            Drawable thirdIcon =
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_thumb_down);
            Drawable fourthIcon =
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_edit);
            Drawable fifthIcon =
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_delete);

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

  private void setupActionBars() {
    Log.d(TAG, "setupActionBars called.");

    // TopAppBar
    setTitle("My Calendar");
    // override onCreateOptionsMenu and onOptionsItemSelected for TopAppBar

    bottomAppBar.replaceMenu(R.menu.bottom_app_bar_menu);

    bottomAppBar
        .getMenu()
        .findItem(R.id.bottom_app_bar_freelists)
        .getIcon()
        .setAlpha(120);

    bottomAppBar
        .getMenu()
        .findItem(R.id.bottom_app_bar_calendar)
        .getIcon()
        .setAlpha(220);

    bottomAppBar
        .getMenu()
        .findItem(R.id.bottom_app_bar_search)
        .getIcon()
        .setAlpha(120);

    bottomAppBar.setOnMenuItemClickListener(
        new BottomAppBar.OnMenuItemClickListener() {
          @Override
          public boolean onMenuItemClick(MenuItem menuItem) {
            int id = menuItem.getItemId();
            switch (id) {
              case R.id.bottom_app_bar_freelists:
                Intent intent = new Intent(CalendarActivity.this, NavigateFreelistActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
              case R.id.bottom_app_bar_calendar:
                return true;
              case R.id.bottom_app_bar_search:
                Intent searchIntent = new Intent(CalendarActivity.this, SearchActivity.class);
                startActivity(searchIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }
            return false;
          }
        });
  }

  private void setupFloatingActionButton() {
    Log.d(TAG, "setupFloatingActionButton called.");
    FloatingActionButton buttonAddEntry = findViewById(R.id.button_add_entry);
    buttonAddEntry.setColorFilter(Color.parseColor("#15b790"));
    buttonAddEntry.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            //            Intent intent = new Intent(CalendarActivity.this,
            // AddEditEntryActivity.class);
            //            intent.putExtra(Constants.EXTRA_REQUEST_TYPE_ADD,
            // Constants.ADD_ENTRY_REQUEST);
            //            intent.putExtra(
            //                Constants.EXTRA_ENTRY_PARENT_ID, calendarViewModel.getParentId());
            //            intent.putExtra(
            //                Constants.EXTRA_SCHEDULER_EVENT_SEQUENCE_NUMBER,
            //                lastSavedSchedulerEventSequenceNumber);
            //            startActivityForResult(intent, Constants.ADD_ENTRY_REQUEST);
            //            overridePendingTransition(android.R.anim.fade_in,
            // android.R.anim.fade_out);
          }
        });
  }

  @Override
  public void onItemClick(View view, int position) {
    Log.d(TAG, "onItemClick at position " + position + "called.");
    ScheduleForwardPickerDialog scheduleForwardPickerDialog = ScheduleForwardPickerDialog.Create();
    scheduleForwardPickerDialog.show(getSupportFragmentManager(), "testDialog");
    //    adapter.getEntryAt(position)
  }

  @Override
  public void onDialogFeedback(String input, String inputType) {

  }

  @Override
  public void onPreferredDaysChange(Bundle checkBoxStates) {

  }
}
