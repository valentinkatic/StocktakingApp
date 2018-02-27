package eu.fiskaljdoo.stocktaking.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import eu.fiskaljdoo.stocktaking.R;
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

        db = new DatabaseHandler(this);
        results = db.getAllResults();

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
            for (int i=0; i<results.size(); i++){
                if (results.get(i).getArticle().getCode().equals(r.getArticle().getCode())){
                    results.set(i, r);
                    adapterResults.notifyItemChanged(i + 1);
                    break;
                }
            }
        }
    };
}
