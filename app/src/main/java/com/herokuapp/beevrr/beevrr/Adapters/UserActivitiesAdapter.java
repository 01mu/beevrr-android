/*
    beevrr-android
    github.com/01mu
 */

package com.herokuapp.beevrr.beevrr.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.herokuapp.beevrr.beevrr.AdapterHelpers.DashboardStat;
import com.herokuapp.beevrr.beevrr.AdapterHelpers.UserActivity;
import com.herokuapp.beevrr.beevrr.R;

import org.w3c.dom.Text;

import java.util.List;

public class UserActivitiesAdapter
        extends RecyclerView.Adapter<UserActivitiesAdapter.ProductViewHolder> {

    private Context mCtx;
    private List<UserActivity> activities;

    public UserActivitiesAdapter(Context mCtx, List<UserActivity> activities) {
        this.mCtx = mCtx;
        this.activities = activities;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.adapter_user_activities, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, final int position) {
        UserActivity activity = activities.get(holder.getAdapterPosition());

        holder.action.setText(activity.getAction());
        holder.opinion.setText(" " +  activity.getOpinion());
        holder.proposition.setText(activity.getProposition());
        holder.date.setText(activity.getDate());
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView action;
        TextView opinion;
        TextView proposition;
        TextView date;

        public ProductViewHolder(View itemView) {
            super(itemView);

            action = itemView.findViewById(R.id.action);
            opinion = itemView.findViewById(R.id.opinion);
            proposition = itemView.findViewById(R.id.proposition);
            date = itemView.findViewById(R.id.date);
        }
    }
}