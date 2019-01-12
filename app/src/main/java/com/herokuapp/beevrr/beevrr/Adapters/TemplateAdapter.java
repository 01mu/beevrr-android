package com.herokuapp.beevrr.beevrr.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.herokuapp.beevrr.beevrr.AdapterHelpers.DashboardStat;
import com.herokuapp.beevrr.beevrr.R;

import java.util.List;

public class TemplateAdapter
        extends RecyclerView.Adapter<TemplateAdapter.ProductViewHolder> {

    private Context mCtx;
    private List<DashboardStat> links;

    public TemplateAdapter(Context mCtx, List<DashboardStat> links) {
        this.mCtx = mCtx;
        this.links = links;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.dashboard_stats_adapter, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, final int position) {

    }

    @Override
    public int getItemCount() {
        return links.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        public ProductViewHolder(View itemView) {
            super(itemView);

        }
    }
}