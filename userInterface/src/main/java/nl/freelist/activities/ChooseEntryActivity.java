package nl.freelist.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import io.reactivex.schedulers.Schedulers;
import java.util.UUID;
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
  private String uuidToExclude;
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

    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    myUuid = sharedPreferences.getString(Constants.SETTINGS_UUID,
        UUID.nameUUIDFromBytes("unknown@freelist.nl".getBytes()).toString());

    //always reset navigateEntriesViewModel at root to avoid choosing itself or descendants as a parent (infinite loop)
    navigateEntriesViewModel.updateParentUuid(myUuid);

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      uuidToExclude = bundle.getString(Constants.EXTRA_ENTRY_ID);
    }
    recyclerView = findViewById(R.id.recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setHasFixedSize(true);

    adapter = new ChooseEntryAdapter(this);

    recyclerView.setAdapter(adapter);

    ActionBar actionbar = getSupportActionBar();
    actionbar
        .setHomeAsUpIndicator(R.drawable.ic_close);
    actionbar.setTitle("Choose a parent (or none)");
  }

  private void updateRecyclerView() {
    Log.d(TAG, "updateRecyclerView called.");
    navigateEntriesViewModel
        .getAllChildrenEntries()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(
            viewModelEntries -> {
              // update RecyclerView
              runOnUiThread(
                  new Runnable() {
                    @Override
                    public void run() {
                      viewModelEntries
                          .removeIf(viewModelEntry -> viewModelEntry.getUuid() == uuidToExclude);
                      adapter.setEntries(viewModelEntries);
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
    menuInflater.inflate(R.menu.add_entry_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Log.d(TAG, "onOptionsItemSelected called.");
    switch (item.getItemId()) {
      case R.id.save_entry:
        Intent data = new Intent();
        String selectedParent = adapter.getCurrentUuid();
        data.putExtra(Constants.EXTRA_ENTRY_ID, selectedParent);
        setResult(RESULT_OK, data);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onItemClick(View view, int position) {
    Log.d(TAG, "onItemClick called.");
    int viewType = adapter.getItemViewType(position);
    String parentToSet = adapter.getEntryAt(position).getUuid();
    navigateEntriesViewModel.updateParentUuid(parentToSet);
    updateRecyclerView();
    adapter.setCurrentUuid(parentToSet);
  }


}
