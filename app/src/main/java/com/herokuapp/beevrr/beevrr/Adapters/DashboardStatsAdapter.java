package com.herokuapp.beevrr.beevrr.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.herokuapp.beevrr.beevrr.AdapterHelpers.DashboardStat;
import com.herokuapp.beevrr.beevrr.R;

import java.util.List;

public class DashboardStatsAdapter
        extends RecyclerView.Adapter<DashboardStatsAdapter.ProductViewHolder> {

    private Context mCtx;
    private List<DashboardStat> stats;

    public DashboardStatsAdapter(Context mCtx, List<DashboardStat> stats) {
        this.mCtx = mCtx;
        this.stats = stats;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.dashboard_stats_adapter, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, final int position) {
        DashboardStat stat = stats.get(holder.getAdapterPosition());
        holder.type.setText(stat.getType());
        holder.count.setText(stat.getCount());
    }

    @Override
    public int getItemCount() {
        return stats.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView type;
        TextView count;

        public ProductViewHolder(View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.type);
            count = itemView.findViewById(R.id.count);
        }
    }
}