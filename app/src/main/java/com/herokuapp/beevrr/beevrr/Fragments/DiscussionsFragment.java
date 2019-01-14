/*
    beevrr-android
    github.com/01mu
 */

package com.herokuapp.beevrr.beevrr.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.herokuapp.beevrr.beevrr.AdapterHelpers.Discussion;
import com.herokuapp.beevrr.beevrr.AdapterHelpers.UserActivity;
import com.herokuapp.beevrr.beevrr.Adapters.DiscussionsAdapter;
import com.herokuapp.beevrr.beevrr.Adapters.UserActivitiesAdapter;
import com.herokuapp.beevrr.beevrr.Methods;
import com.herokuapp.beevrr.beevrr.Preferences;
import com.herokuapp.beevrr.beevrr.R;
import com.herokuapp.beevrr.beevrr.Retrofit.APIClient;
import com.herokuapp.beevrr.beevrr.Retrofit.APIInterface;
import com.jayway.jsonpath.JsonPath;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscussionsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    Preferences preferences;
    APIInterface apiService;
    View view;

    private LinearLayout progressBar;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter discussionsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<Discussion> discussions = new ArrayList<>();
    private int page = 0;

    private void initViews() {
        progressBar = view.findViewById(R.id.progress_bar);

        mRecyclerView = view.findViewById(R.id.discussions_recycler);
        mRecyclerView.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void getDiscussions(int page) {
        apiService.discussions(page).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                discussionsResult(response);
                Methods.setCookies(response, preferences);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Methods.snackbar(view, getActivity(), "Failed to connect!");
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void discussionsResult(Response<String> response) {
        List<Object> jsonDiscussions;

        String userName;
        String proposition;
        String argument;
        String currentPhase;
        String time;

        int userID;
        int discussionID;
        int replyCount;
        int voteCount;
        int score;

        String result = String.valueOf(response.body());
        String status = JsonPath.read(result, "$['status']");

        if (status.compareTo("success") == 0) {
            jsonDiscussions = JsonPath.read(result, "$['discussions']");

            for (Object activity : jsonDiscussions) {
                userName = JsonPath.read(activity, "$['user_name']").toString();
                proposition = JsonPath.read(activity, "$['proposition']").toString();
                argument = JsonPath.read(activity, "$['argument']").toString();
                currentPhase = JsonPath.read(activity, "$['current_phase']").toString();
                time = JsonPath.read(activity, "$['post_date']").toString();
                userID = JsonPath.read(activity, "$['user_id']");
                discussionID = JsonPath.read(activity, "$['id']");
                replyCount = JsonPath.read(activity, "$['reply_count']");
                voteCount = JsonPath.read(activity, "$['vote_count']");
                score = JsonPath.read(activity, "$['score']");

                discussions.add(new Discussion(userName, proposition, argument, currentPhase, time,
                        userID, discussionID, replyCount, voteCount, score));
            }

            if (page == 0) {
                mRecyclerView.setAdapter(discussionsAdapter);
            } else {
                discussionsAdapter.notifyDataSetChanged();
            }
        } else if (status.compareTo("end_pagination") == 0) {
            page = -1;
        } else {
            Methods.snackbar(view, getActivity(), "Error!");
        }
    }

    public DiscussionsFragment() {
    }

    public static DiscussionsFragment newInstance(String param1, String param2) {
        DiscussionsFragment fragment = new DiscussionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        preferences = new Preferences(getActivity());
        apiService = APIClient.getClient(getActivity()).create(APIInterface.class);

        discussionsAdapter = new DiscussionsAdapter(getContext(), discussions);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_discussions, container, false);

        initViews();
        getDiscussions(0);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && page != -1) {
                    getDiscussions(++page);
                }
            }
        });

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
