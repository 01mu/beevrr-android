/*
    beevrr-android
    github.com/01mu
 */

package com.herokuapp.beevrr.beevrr.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.herokuapp.beevrr.beevrr.AdapterHelpers.DashboardStat;
import com.herokuapp.beevrr.beevrr.AdapterHelpers.Discussion;
import com.herokuapp.beevrr.beevrr.AdapterHelpers.DiscussionResponse;
import com.herokuapp.beevrr.beevrr.Methods;
import com.herokuapp.beevrr.beevrr.Preferences;
import com.herokuapp.beevrr.beevrr.R;
import com.herokuapp.beevrr.beevrr.Retrofit.APIInterface;
import com.jayway.jsonpath.JsonPath;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResponsesAdapter
        extends RecyclerView.Adapter<ResponsesAdapter.ProductViewHolder> {

    private Context mCtx;
    private List<DiscussionResponse> responses;

    private Animation animation = new AlphaAnimation(0.3f, 1.0f);
    private APIInterface apiService;
    private Preferences preferences;
    private View view;
    private Activity activity;

    private boolean firstAction = false;

    public ResponsesAdapter(Context mCtx, List<DiscussionResponse> responses,
                            APIInterface apiService, Preferences preferences,
                            View view, Activity activity) {
        this.mCtx = mCtx;
        this.responses = responses;
        this.apiService = apiService;
        this.preferences = preferences;
        this.view = view;
        this.activity = activity;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.adapter_responses, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, final int position) {
        DiscussionResponse discussionResponse = responses.get(holder.getAdapterPosition());

        holder.responseText.setText(discussionResponse.getResponse());
        holder.userName.setText("by " + discussionResponse.getUserName());
        holder.time.setText(" " + discussionResponse.getTime());
        holder.score.setText(" | " + Integer.toString(discussionResponse.getScore()) + " likes");
        holder.opinion.setText(" | " + discussionResponse.getOpinion());
        holder.id = discussionResponse.getId();
        holder.scoreValue = discussionResponse.getScore();
    }

    @Override
    public int getItemCount() {
        return responses.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView responseText;
        TextView userName;
        TextView time;
        TextView score;
        TextView opinion;

        int id;
        int scoreValue;

        public ProductViewHolder(final View itemView) {
            super(itemView);

            responseText = itemView.findViewById(R.id.response_text);
            userName = itemView.findViewById(R.id.user_name);
            time = itemView.findViewById(R.id.time);
            score = itemView.findViewById(R.id.score);
            opinion = itemView.findViewById(R.id.opinion);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemView.startAnimation(animation);

                    getResponseLike(id, score, scoreValue);
                }
            });
        }
    }

    private void getResponseLike(int id, final TextView score, final int scoreValue) {
        apiService.responseLike(id).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String result = String.valueOf(response.body());
                String snackMessage = "";
                int newVal;

                try {
                    String status = JsonPath.read(result, "$['status']");

                    if (status.compareTo("success") == 0) {
                        switch ((String) JsonPath.read(result, "$['type']")) {
                            case "liked":
                                if (firstAction) {
                                    firstAction = false;
                                    newVal = scoreValue;
                                } else {
                                    firstAction = true;
                                    newVal = scoreValue + 1;
                                }

                                snackMessage = "Response liked!";
                                break;
                            default:
                                if (firstAction) {
                                    firstAction = false;
                                    newVal = scoreValue;
                                } else {
                                    firstAction = true;
                                    newVal = scoreValue - 1;
                                }

                                snackMessage = "Response unliked!";
                                break;
                        }

                        score.setText(" | " + Integer.toString(newVal) + " likes");
                    }
                } catch (Exception e) {
                    snackMessage = "Please wait before attempting to like again!";
                }

                Methods.snackbar(view, activity, snackMessage);
                Methods.setCookies(response, preferences);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Methods.snackbar(view, activity, "Failed to connect!");
            }
        });
    }
}