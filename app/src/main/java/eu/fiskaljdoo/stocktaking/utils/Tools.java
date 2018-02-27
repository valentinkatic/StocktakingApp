package eu.fiskaljdoo.stocktaking.utils;

import android.os.Build;
import android.support.v4.app.FragmentManager;

import eu.fiskaljdoo.stocktaking.dialogs.DialogEditResult;
import eu.fiskaljdoo.stocktaking.models.Result;

/**
 * Created by Valentin on 27.2.2018..
 */

public class Tools {

    public static boolean needRequestPermission() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    public static void startEditResultDialog(FragmentManager fm, Result result, DialogEditResult.OnDismissListener onDismissListener){
        DialogEditResult dialogEditResult = DialogEditResult.newInstance(result);
        dialogEditResult.show(fm, "dialogEditResult");
        dialogEditResult.setOnDismissListener(onDismissListener);
    }

}
