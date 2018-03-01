package eu.fiskaljdoo.stocktaking.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Locale;

import eu.fiskaljdoo.stocktaking.Constants;
import eu.fiskaljdoo.stocktaking.R;
import eu.fiskaljdoo.stocktaking.data.DatabaseHandler;
import eu.fiskaljdoo.stocktaking.models.Article;
import eu.fiskaljdoo.stocktaking.models.Result;

/**
 * Created by Valentin on 27.2.2018..
 */

public class DialogEditResult extends DialogFragment {

    public static final String resultTAG = "result";

    private DatabaseHandler db;
    private Result result;

    public static DialogEditResult newInstance(Result result){
        DialogEditResult d = new DialogEditResult();
        Bundle b = new Bundle();
        b.putParcelable(resultTAG, result);
        d.setArguments(b);
        return d;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        db = new DatabaseHandler(getActivity());
        result = getArguments().getParcelable(resultTAG);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_result, null);

        ((TextView) dialogView.findViewById(R.id.tv_barcode)).setText(result.getArticle().getCode());
        final EditText et_name = dialogView.findViewById(R.id.et_name);
        final EditText et_amount = dialogView.findViewById(R.id.et_amount);
        final EditText et_price = dialogView.findViewById(R.id.et_price);

        et_name.setText(result.getArticle().getName());
        et_amount.setText(String.format(Locale.getDefault(), "%s", new DecimalFormat("###,##0.##").format(result.getAmount())));
        et_price.setText(String.format(Locale.getDefault(), "%s", new DecimalFormat("###,##0.##").format(result.getArticle().getPrice())));

        builder.setView(dialogView);
        builder.setTitle("Unesite podatke");
        builder.setPositiveButton("Spremi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Article a = result.getArticle();
                a.setName(et_name.getText().toString());
                a.setPrice(Double.parseDouble(et_price.getText().toString()));
                db.updateArticle(a);

                Result r = new Result();
                if (Constants.stocktakingNumber == 0){
                    r.setStocktakingNumber(++Constants.stocktakingNumber);
                } else {
                    r.setStocktakingNumber(Constants.newStocktakingStarted ? ++Constants.stocktakingNumber : Constants.stocktakingNumber);
                    Constants.newStocktakingStarted = false;
                }
                r.setId(result.getId());
                r.setArticle(a);
                r.setAmount(Double.parseDouble(et_amount.getText().toString()));
                r = db.updateResult(r);

                Toast.makeText(getActivity(),"Uspješno spremljeno", Toast.LENGTH_SHORT).show();

                onDismissListener.onDismiss(r);
            }
        });
        builder.setNegativeButton("Odustani", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onDismissListener.onDismiss(null);
            }
        });

        return builder.create();
    }

    private OnDismissListener onDismissListener;

    public interface OnDismissListener {
        void onDismiss(Result r);
    }

    public void setOnDismissListener(OnDismissListener mOnDismissListener){
        onDismissListener = mOnDismissListener;
    }
}
