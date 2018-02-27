package eu.fiskaljdoo.stocktaking.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eu.fiskaljdoo.stocktaking.R;
import eu.fiskaljdoo.stocktaking.models.Result;

/**
 * Created by Valentin on 27.2.2018..
 */

public class AdapterResults extends RecyclerView.Adapter<AdapterResults.ViewHolder> {

    private List<Result> results = new ArrayList<>();
    private ResultClickListener resultClickListener;

    public AdapterResults(List<Result> results) {
        this.results = results;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_result, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position == 0){
            return;
        }
        Result current = results.get(position-1);

        holder.tv_barcode.setText(current.getArticle().getCode());
        holder.tv_name.setText(current.getArticle().getName());
        holder.tv_amount.setText(String.format(Locale.getDefault(), "%s", new DecimalFormat("###,##0.##").format(current.getAmount())));

        if (position == results.size()){
            holder.separator.setVisibility(View.GONE);
        }

        holder.parent_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultClickListener.onClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return results.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout parent_view;
        private TextView tv_barcode, tv_name, tv_amount;
        private View separator;

        public ViewHolder(View itemView) {
            super(itemView);

            parent_view = itemView.findViewById(R.id.parent_view);
            tv_barcode = itemView.findViewById(R.id.tv_barcode);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            separator = itemView.findViewById(R.id.separator);
        }
    }

    public void setResultClickListener(ResultClickListener mResultClickListener){
        resultClickListener = mResultClickListener;
    }

    public interface ResultClickListener{
        void onClicked(int position);
    }

}
