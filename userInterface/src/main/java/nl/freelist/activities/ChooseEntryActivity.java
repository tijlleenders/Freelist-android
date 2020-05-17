package nl.freelist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import nl.freelist.androidCrossCuttingConcerns.MySettings;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.freelist.R;
import nl.freelist.recyclerviewHelpers.ChooseEntryAdapter;
import nl.freelist.recyclerviewHelpers.ItemClickListener;
import nl.freelist.viewModelPerActivity.NavigateEntriesViewModel;

public class ChooseEntryActivity extends AppCompatActivity implements ItemClickListener {

  private NavigateEntriesViewModel navigateEntriesViewModel;
  private ChooseEntryAdapter adapter;
  private RecyclerView recyclerView;
  private static final String TAG = "ChooseEntryActivity";
  private String idToExclude;
  private String myUuid;

  public ChooseEntryActivity() {
  }

  @Override
  protected void onResume() {
    Log.d(TAG, "onResume called.");
    super.onResume();
    updateRecyclerView();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate called.");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_choose_entry);

    navigateEntriesViewModel = ViewModelProviders.of(this)
        .get(NavigateEntriesViewModel.class);

    initializeSharedPreferences();

    //always reset navigateEntriesViewModel at root to avoid choosing itself or descendants as a parent (infinite loop)
    navigateEntriesViewModel.setParentId(myUuid);

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      idToExclude = bundle.getString(Constants.EXTRA_ENTRY_ID);
    }
    recyclerView = findViewById(R.id.recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setHasFixedSize(true);

    adapter = new ChooseEntryAdapter(this);

    recyclerView.setAdapter(adapter);

    ActionBar actionbar = getSupportActionBar();
//    actionbar.setDisplayHomeAsUpEnabled(true);
    actionbar.setTitle("Choose a parent (or none)");
  }

  private void initializeSharedPreferences() {
    MySettings mySettings = new MySettings(this);
    myUuid = mySettings.getId();
  }

  private void updateRecyclerView() {
    Log.d(TAG, "updateRecyclerView called.");
    navigateEntriesViewModel
        .getViewModelEntries()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(
            viewModelEntries -> {
              // update RecyclerView
              runOnUiThread(
                  new Runnable() {
                    @Override
                    public void run() {
                      List<ViewModelEntry> customList = new ArrayList<>(
                          viewModelEntries.getViewModelEntryList());
                      customList
                          .removeIf(
                              viewModelEntry -> viewModelEntry.getEntryId().equals(idToExclude));
                      adapter.setEntries(customList);
                      Toast.makeText(
                          ChooseEntryActivity.this,
                          "ChooseParentActivityViewModel refreshed!",
                          Toast.LENGTH_SHORT)
                          .show();
                    }
                  });
            });
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    Log.d(TAG, "onCreateOptionsMenu called.");
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.choose_entry_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Log.d(TAG, "onOptionsItemSelected called.");
    switch (item.getItemId()) {
      case R.id.save_entry:
        Intent data = new Intent();
        String selectedParent = adapter.getCurrentUuid();
        if (selectedParent != null) {
          data.putExtra(Constants.EXTRA_ENTRY_ID, selectedParent);
        } else {
          data.putExtra(Constants.EXTRA_ENTRY_ID, myUuid);
        }
        setResult(RESULT_OK, data);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
        return true;
      case android.R.id.home:
        this.finish();
        return true;
      default:
        return true;
    }
  }

  @Override
  public void onItemClick(View view, int position) {
    Log.d(TAG, "onItemClick called.");
    int viewType = adapter.getItemViewType(position);
    String parentToSet = adapter.getEntryAt(position).getEntryId();
    navigateEntriesViewModel.setParentId(parentToSet);
    updateRecyclerView();
    adapter.setCurrentUuid(parentToSet);
  }


}
