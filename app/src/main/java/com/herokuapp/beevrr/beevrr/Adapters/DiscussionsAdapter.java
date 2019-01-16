/*
    beevrr-android
    github.com/01mu
 */

package com.herokuapp.beevrr.beevrr.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.herokuapp.beevrr.beevrr.AdapterHelpers.DashboardStat;
import com.herokuapp.beevrr.beevrr.AdapterHelpers.Discussion;
import com.herokuapp.beevrr.beevrr.AdapterHelpers.UserActivity;
import com.herokuapp.beevrr.beevrr.Fragments.Discussion.ViewDiscussionFragment;
import com.herokuapp.beevrr.beevrr.Fragments.User.UserActivityFragment;
import com.herokuapp.beevrr.beevrr.Methods;
import com.herokuapp.beevrr.beevrr.R;

import org.w3c.dom.Text;

import java.util.List;

public class DiscussionsAdapter
        extends RecyclerView.Adapter<DiscussionsAdapter.ProductViewHolder> {

    private Context mCtx;
    private List<Discussion> discussions;
    private FragmentManager fm;

    private Animation animation = new AlphaAnimation(0.3f, 1.0f);
    private Bundle arguments = new Bundle();
    private ViewDiscussionFragment viewDiscussionFragment =  new ViewDiscussionFragment();

    public DiscussionsAdapter(Context mCtx, List<Discussion> discussions, FragmentManager fm) {
        this.mCtx = mCtx;
        this.discussions = discussions;
        this.fm = fm;
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

        holder.discussionToView = discussion;
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

        Discussion discussionToView;

        public ProductViewHolder(final View itemView) {
            super(itemView);

            proposition = itemView.findViewById(R.id.proposition);
            userName = itemView.findViewById(R.id.user_name);
            time = itemView.findViewById(R.id.time);
            score = itemView.findViewById(R.id.score);
            currentPhase = itemView.findViewById(R.id.current_phase);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemView.startAnimation(animation);

                    arguments.putSerializable("discussion", discussionToView);

                    viewDiscussionFragment.setArguments(arguments);

                    Methods.addFragment(viewDiscussionFragment, fm);
                }
            });
        }
    }
}