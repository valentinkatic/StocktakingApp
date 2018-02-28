package eu.fiskaljdoo.stocktaking.activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

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

    private int selectedInventureNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHandler(this);
        selectedInventureNumber = db.getLastResult().getInventureNumber();
        results = db.getInventureResults(selectedInventureNumber);

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

    private void startNumberPickerDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Broj inventure");
        builder.setNegativeButton("Odustani", null);
        builder.setPositiveButton("Postavi", null);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_quantity_picker, null);
        final NumberPicker np = dialogView.findViewById(R.id.numberPicker);
        List<Integer> inventureNumbers = db.getInventureNumbers();
        np.setMaxValue(inventureNumbers.size() > 0 ? inventureNumbers.get(inventureNumbers.size()-1) : 0);
        np.setMinValue(inventureNumbers.size() > 0 ? inventureNumbers.get(0) : 0);
        np.setValue(selectedInventureNumber);
        np.setWrapSelectorWheel(false);

        builder.setView(dialogView);
        final AlertDialog dialog = builder.show();

        Button setButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedInventureNumber = np.getValue();
                results = db.getInventureResults(selectedInventureNumber);
                setAdapter();
                dialog.dismiss();
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
            case R.id.action_select_inventure_number:
                startNumberPickerDialog();
                break;
            default:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
