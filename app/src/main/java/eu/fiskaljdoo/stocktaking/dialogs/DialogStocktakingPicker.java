package eu.fiskaljdoo.stocktaking.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.util.List;

import eu.fiskaljdoo.stocktaking.R;
import eu.fiskaljdoo.stocktaking.adapters.AdapterStocktaking;
import eu.fiskaljdoo.stocktaking.data.DatabaseHandler;
import eu.fiskaljdoo.stocktaking.models.Stocktaking;

/**
 * Created by Valentin on 1.3.2018..
 */

public class DialogStocktakingPicker extends DialogFragment {

    public static final String listenerTAG = "listener";

    public static DialogStocktakingPicker newInstance(AdapterStocktaking.StocktakingClickListener stocktakingClickListener){
        DialogStocktakingPicker dialog = new DialogStocktakingPicker();
        Bundle b = new Bundle();
        b.putParcelable(listenerTAG, stocktakingClickListener);
        dialog.setArguments(b);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Odaberite broj inventure");
        builder.setPositiveButton("U redu", null);

        AdapterStocktaking.StocktakingClickListener listener = getArguments().getParcelable(listenerTAG);
        DatabaseHandler db = new DatabaseHandler(getActivity());
        List<Stocktaking> stocktakingList = db.getStocktaking();
        AdapterStocktaking adapterStocktaking = new AdapterStocktaking(getActivity(), stocktakingList);

        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_stocktaking_picker, null);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterStocktaking);
        adapterStocktaking.setStocktakingClickListener(listener);

        builder.setView(dialogView);

        return builder.create();
    }
}
