package eu.fiskaljdoo.stocktaking.activities;

import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import eu.fiskaljdoo.stocktaking.R;
import eu.fiskaljdoo.stocktaking.adapters.AdapterStocktaking;
import eu.fiskaljdoo.stocktaking.adapters.AdapterResults;
import eu.fiskaljdoo.stocktaking.data.DatabaseHandler;
import eu.fiskaljdoo.stocktaking.dialogs.DialogEditResult;
import eu.fiskaljdoo.stocktaking.models.Result;
import eu.fiskaljdoo.stocktaking.utils.Tools;

public class ActivityResults extends AppCompatActivity {

    private DatabaseHandler db;

    private List<Result> results;

    private RecyclerView recyclerView;
    private AdapterResults adapterResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHandler(this);
        results = db.getStocktakingResults(db.getLastResult().getStocktakingNumber());

        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        setAdapter();

    }

    private void setAdapter(){
        adapterResults = new AdapterResults(results);
        recyclerView.setAdapter(adapterResults);

        adapterResults.setResultClickListener(new AdapterResults.ResultClickListener() {
            @Override
            public void onClicked(int position) {
                Tools.startEditResultDialog(getSupportFragmentManager(), results.get(position - 1), onDismissListener);
            }
        });
    }

    DialogEditResult.OnDismissListener onDismissListener = new DialogEditResult.OnDismissListener() {
        @Override
        public void onDismiss(Result r) {
            if (r == null){
                return;
            }
            for (int i=0; i<results.size(); i++){
                if (results.get(i).getArticle().getCode().equals(r.getArticle().getCode())){
                    results.set(i, r);
                    adapterResults.notifyItemChanged(i + 1);
                    break;
                }
            }
        }
    };

    private void startStocktakingPickerDialog(){
        Tools.startInventurePickerDialog(getSupportFragmentManager(), new AdapterStocktaking.StocktakingClickListener() {
            @Override
            public void onClicked(int position, int in) {
                if (position == 1){
                    results = db.getAllResults();
                } else {
                    results = db.getStocktakingResults(in);
                }
                setAdapter();
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_select_stocktaking_number:
                startStocktakingPickerDialog();
                break;
            default:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
