package eu.fiskaljdoo.stocktaking.activities;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.Result;

import eu.fiskaljdoo.stocktaking.data.DatabaseHandler;
import eu.fiskaljdoo.stocktaking.dialogs.DialogEditResult;
import eu.fiskaljdoo.stocktaking.models.Article;
import eu.fiskaljdoo.stocktaking.utils.Tools;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ActivitySimpleScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final String TAG = "ActivitySimpleScanner";
    private ZXingScannerView mScannerView;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        db = new DatabaseHandler(this);
        setContentView(mScannerView);                // Set the scanner view as the content view
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

        // Do something with the result here
        Log.v(TAG, rawResult.getText()); // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
    }
}