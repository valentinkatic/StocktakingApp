package eu.fiskaljdoo.stocktaking.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.zxing.Result;

import eu.fiskaljdoo.stocktaking.R;
import eu.fiskaljdoo.stocktaking.data.DatabaseHandler;
import eu.fiskaljdoo.stocktaking.dialogs.DialogEditResult;
import eu.fiskaljdoo.stocktaking.utils.Tools;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ActivitySimpleScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final String TAG = "ActivitySimpleScanner";
    private ZXingScannerView mScannerView;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScannerView = new ZXingScannerView(this);
        db = new DatabaseHandler(this);

        setContentView(mScannerView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {

        eu.fiskaljdoo.stocktaking.models.Result result = db.getResult(rawResult.getText());
        Tools.startEditResultDialog(getSupportFragmentManager(), result, new DialogEditResult.OnDismissListener() {
            @Override
            public void onDismiss(eu.fiskaljdoo.stocktaking.models.Result r) {
                // If you would like to resume scanning, call this method below:
                mScannerView.resumeCameraPreview(ActivitySimpleScanner.this);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            default:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
