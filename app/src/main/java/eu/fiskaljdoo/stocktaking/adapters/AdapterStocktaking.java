package eu.fiskaljdoo.stocktaking.adapters;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import eu.fiskaljdoo.stocktaking.R;
import eu.fiskaljdoo.stocktaking.models.Stocktaking;

/**
 * Created by Valentin on 1.3.2018..
 */

public class AdapterStocktaking extends RecyclerView.Adapter<AdapterStocktaking.ViewHolder> {

    private Context context;
    private List<Stocktaking> stocktakingList = new ArrayList<>();
    private StocktakingClickListener stocktakingClickListener;

    public AdapterStocktaking(Context context, List<Stocktaking> stocktakingList) {
        this.context = context;
        this.stocktakingList = stocktakingList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_stocktaking, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (position == 0){
            return;
        } else if (position == 1){
            holder.tv_number.setVisibility(View.GONE);
            holder.tv_date.setText("Prikaži sve inventure");

            holder.parent_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stocktakingClickListener.onClicked(holder.getAdapterPosition(), 0);
                }
            });
        } else {
            final Stocktaking current = stocktakingList.get(position - 2);
            holder.tv_number.setText(current.getNumber() + "");

            try {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.ISO_8601_PATTERN_1), Locale.getDefault());
                SimpleDateFormat vdf = new SimpleDateFormat(context.getString(R.string.VIEW_DATE_PATTERN), Locale.getDefault());
                c.setTime(sdf.parse(current.getDate()));
                holder.tv_date.setText(vdf.format(c.getTime()));
            } catch (ParseException e){
                e.printStackTrace();
                holder.tv_date.setText("");
            }

            holder.parent_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stocktakingClickListener.onClicked(holder.getAdapterPosition(), current.getNumber());
                }
            });
        }
        if (position == stocktakingList.size()+1) {
            holder.separator.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return stocktakingList.size() + 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout parent_view;
        private TextView tv_number, tv_date;
        private View separator;

        public ViewHolder(View itemView) {
            super(itemView);

            parent_view = itemView.findViewById(R.id.parent_view);
            tv_number = itemView.findViewById(R.id.tv_number);
            tv_date = itemView.findViewById(R.id.tv_date);
            separator = itemView.findViewById(R.id.separator);
        }
    }

    public void setStocktakingClickListener(StocktakingClickListener mStocktakingClickListener){
        stocktakingClickListener = mStocktakingClickListener;
    }

    public interface StocktakingClickListener extends Parcelable {
        void onClicked(int position, int in);
    }

}
