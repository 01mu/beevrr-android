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
import com.herokuapp.beevrr.beevrr.AdapterHelpers.Discussion;
import com.herokuapp.beevrr.beevrr.AdapterHelpers.UserActivity;
import com.herokuapp.beevrr.beevrr.R;

import org.w3c.dom.Text;

import java.util.List;

public class DiscussionsAdapter
        extends RecyclerView.Adapter<DiscussionsAdapter.ProductViewHolder> {

    private Context mCtx;
    private List<Discussion> discussions;

    public DiscussionsAdapter(Context mCtx, List<Discussion> discussions) {
        this.mCtx = mCtx;
        this.discussions = discussions;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.adapter_discussions, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, final int position) {
        Discussion discussion = discussions.get(holder.getAdapterPosition());

        holder.proposition.setText(discussion.getProposition());
        holder.userName.setText("by " + discussion.getUserName());
        holder.proposition.setText(discussion.getProposition());
        holder.time.setText(" " + discussion.getTime());
        holder.score.setText(" | " + Integer.toString(discussion.getScore()) + " likes");
        holder.currentPhase.setText(" | " + discussion.getCurrentPhase());
    }

    @Override
    public int getItemCount() {
        return discussions.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView proposition;
        TextView userName;
        TextView time;
        TextView score;
        TextView currentPhase;

        public ProductViewHolder(View itemView) {
            super(itemView);

            proposition = itemView.findViewById(R.id.proposition);
            userName = itemView.findViewById(R.id.user_name);
            time = itemView.findViewById(R.id.time);
            score = itemView.findViewById(R.id.score);
            currentPhase = itemView.findViewById(R.id.current_phase);
        }
    }
}