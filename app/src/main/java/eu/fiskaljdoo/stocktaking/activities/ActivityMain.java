package eu.fiskaljdoo.stocktaking.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import eu.fiskaljdoo.stocktaking.Constants;
import eu.fiskaljdoo.stocktaking.R;
import eu.fiskaljdoo.stocktaking.data.DatabaseHandler;
import eu.fiskaljdoo.stocktaking.data.SharedPref;
import eu.fiskaljdoo.stocktaking.utils.PermissionUtil;

import static eu.fiskaljdoo.stocktaking.utils.PermissionUtil.CAMERA_PERMISSION;
import static eu.fiskaljdoo.stocktaking.utils.PermissionUtil.STORAGE_PERMISSION;

public class ActivityMain extends AppCompatActivity {

    private SharedPref prefs;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = new SharedPref(this);
        db = new DatabaseHandler(this);

        findViewById(R.id.btn_start_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionUtil.isCameraGranted(ActivityMain.this)) {
                    checkLastInventureNumber();
                } else {
                    requestPermissions(PermissionUtil.PERMISSION_ALL, CAMERA_PERMISSION);
                }
            }
        });

        findViewById(R.id.btn_show_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startShowAllAct();
            }
        });

        findViewById(R.id.btn_backup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionUtil.isStorageGranted(ActivityMain.this)) {
                    backup();
                } else {
                    requestPermissions(PermissionUtil.PERMISSION_ALL, STORAGE_PERMISSION);
                }
            }
        });
    }

    private void checkLastInventureNumber(){
        Constants.inventureNumber = db.getLastResult().getInventureNumber();

        if (Constants.inventureNumber>0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Želite li nastaviti prethodnu inventuru ili započeti novu?");
            try {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.ISO_8601_PATTERN_1), Locale.getDefault());
                SimpleDateFormat vdf = new SimpleDateFormat(getString(R.string.VIEW_DATE_PATTERN), Locale.getDefault());
                c.setTime(sdf.parse(db.getLastResultChangeDate()));
                String message = String.format(Locale.getDefault(), "Zadnji spremljen podatak je bio %s", vdf.format(c.getTime()));
                builder.setMessage(message);
            } catch (ParseException e){
                e.printStackTrace();
            }
            builder.setPositiveButton("Nastavi prethodnu", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Constants.newInventureStarted = false;
                    startScanner();
                }
            });
            builder.setNegativeButton("Započni novu", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Constants.newInventureStarted = true;
                    startScanner();
                }
            });
            builder.create().show();
        } else {
            startScanner();
        }
    }

    private void startScanner(){

        Intent i = new Intent(ActivityMain.this, ActivitySimpleScanner.class);
        startActivity(i);
    }

    private void startShowAllAct(){
        Intent i = new Intent(ActivityMain.this, ActivityResults.class);
        startActivity(i);
    }

    private void backup(){
        db.exportDatabase();
        Toast.makeText(ActivityMain.this, "Backup successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION:
                for (String perm : permissions) {
                    boolean rationale = shouldShowRequestPermissionRationale(perm);
                    prefs.setNeverAskAgain(perm, !rationale);
                }
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLastInventureNumber();
                } else {
                    Toast.makeText(this, "Morate omogućiti kameru kako biste mogli skenirati", Toast.LENGTH_SHORT).show();
                }
                return;
            case STORAGE_PERMISSION:
                for (String perm : permissions) {
                    boolean rationale = shouldShowRequestPermissionRationale(perm);
                    prefs.setNeverAskAgain(perm, !rationale);
                }
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    backup();
                } else {
                    Toast.makeText(this, "Morate omogućiti zapisivanje na uređaj kako bi se backup mogao spremiti", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
}
