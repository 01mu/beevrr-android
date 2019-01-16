/*
    beevrr-android
    github.com/01mu
 */

package com.herokuapp.beevrr.beevrr.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.herokuapp.beevrr.beevrr.AdapterHelpers.DashboardStat;
import com.herokuapp.beevrr.beevrr.Fragments.User.UserActivityFragment;
import com.herokuapp.beevrr.beevrr.Methods;
import com.herokuapp.beevrr.beevrr.R;

import java.util.List;

public class DashboardStatsAdapter
        extends RecyclerView.Adapter<DashboardStatsAdapter.ProductViewHolder> {

    private Context mCtx;
    private List<DashboardStat> stats;
    private FragmentManager fm;
    private int userID;
    private String userName;

    private Animation animation = new AlphaAnimation(0.3f, 1.0f);
    private Bundle arguments = new Bundle();
    private UserActivityFragment userActivityFragment =  new UserActivityFragment();

    public DashboardStatsAdapter(Context mCtx, List<DashboardStat> stats, FragmentManager fm,
                                 int userID, String userName) {
        this.mCtx = mCtx;
        this.stats = stats;
        this.fm = fm;
        this.userID = userID;
        this.userName = userName;

        animation.setDuration(250);
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.adapter_dashboard_stats, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, final int position) {
        DashboardStat stat = stats.get(holder.getAdapterPosition());

        holder.cardHeader.setText(stat.getHeader() + ": ");
        holder.count.setText(stat.getCount());
        holder.header = stat.getHeader();
    }

    @Override
    public int getItemCount() {
        return stats.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView cardHeader;
        TextView count;

        String header;
        String request;

        public ProductViewHolder(final View itemView) {
            super(itemView);

            cardHeader = itemView.findViewById(R.id.type);
            count = itemView.findViewById(R.id.count);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemView.startAnimation(animation);

                    switch(header) {
                        case "Total Responses":
                            request = "tot_res";
                            break;
                        case "Active Responses":
                            request = "act_res";
                            break;
                        case "Total Votes":
                            request = "tot_vot";
                            break;
                        case "Active Votes":
                            request = "act_vot";
                            break;
                        case "Total Discussions":
                            request = "tot_dis";
                            break;
                        default:
                            request = "act_dis";
                            break;

                    }

                    arguments.putString("header", header);
                    arguments.putString("request", request);
                    arguments.putString("userName", userName);
                    arguments.putInt("userID", userID);

                    userActivityFragment.setArguments(arguments);

                    Methods.addFragment(userActivityFragment, fm);
                }
            });
        }
    }
}