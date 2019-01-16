/*
    beevrr-android
    github.com/01mu
 */

package com.herokuapp.beevrr.beevrr.Fragments.Discussion;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.herokuapp.beevrr.beevrr.AdapterHelpers.Discussion;
import com.herokuapp.beevrr.beevrr.Adapters.DiscussionsAdapter;
import com.herokuapp.beevrr.beevrr.Fragments.Dashboard.DashboardFragment;
import com.herokuapp.beevrr.beevrr.Methods;
import com.herokuapp.beevrr.beevrr.Preferences;
import com.herokuapp.beevrr.beevrr.R;
import com.herokuapp.beevrr.beevrr.Retrofit.APIClient;
import com.herokuapp.beevrr.beevrr.Retrofit.APIInterface;
import com.jayway.jsonpath.JsonPath;

import java.util.ArrayList;
import java.util.HashMap;
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

    Activity activity;
    Preferences preferences;
    APIInterface apiService;
    View view;
    Toolbar toolbar;

    private LinearLayout progressBar;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter discussionsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<Discussion> discussions = new ArrayList<>();
    private int page = 0;

    private FragmentManager fm;

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
        HashMap<String, String> voteCounts = new HashMap<>();

        String userName;
        String proposition;
        String argument;
        String currentPhase;
        String time;
        String winner;

        int userID;
        int discussionID;
        int replyCount;
        int voteCount;
        int score;

        String result = String.valueOf(response.body());
        String status = JsonPath.read(result, "$['status']");

        boolean isLoggedIn;

        final String[] types = {"pa_for", "pa_against", "pa_undecided", "pv_for", "pv_against",
            "pa_for_per", "pa_against_per", "pa_undecided_per", "pv_for_per", "pv_against_per",
            "for_change", "against_change"};

        if (status.compareTo("success") == 0) {
            jsonDiscussions = JsonPath.read(result, "$['discussions']");

            for (Object disc : jsonDiscussions) {
                userName = JsonPath.read(disc, "$['user_name']").toString();
                proposition = JsonPath.read(disc, "$['proposition']").toString();
                argument = JsonPath.read(disc, "$['argument']").toString();
                currentPhase = JsonPath.read(disc, "$['current_phase']").toString();
                time = JsonPath.read(disc, "$['post_date']").toString();
                userID = JsonPath.read(disc, "$['user_id']");
                discussionID = JsonPath.read(disc, "$['id']");
                replyCount = JsonPath.read(disc, "$['reply_count']");
                voteCount = JsonPath.read(disc, "$['vote_count']");
                score = JsonPath.read(disc, "$['score']");
                winner = JsonPath.read(disc, "$['winner']");

                for (String type : types) {
                    voteCounts.put(type, JsonPath.read(disc, "$['" + type + "']")
                            .toString());
                }

                discussions.add(new Discussion(userName, proposition, argument, currentPhase, time,
                        userID, discussionID, replyCount, voteCount, score, voteCounts, winner));
            }

            isLoggedIn = JsonPath.read(result, "$['logged_in']");

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_discussions, container, false);

        setHasOptionsMenu(true);

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

        activity = getActivity();
        preferences = new Preferences(activity);
        apiService = APIClient.getClient(activity).create(APIInterface.class);
        Methods.setToolbarTitle(activity, toolbar, "Beevrr");
        fm = getFragmentManager();
        discussionsAdapter = new DiscussionsAdapter(getContext(), discussions, fm);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.toolbar_discussions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_submit_discussion:
                Methods.checkLoggedIn(preferences, view, activity, apiService,
                        new AddDiscussionFragment(), fm);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        //((AppCompatActivity) getActivity()).getSupportActionBar().setCustomView(R.menu.toolbar_discussions);
    }

    @Override
    public void onStop() {
        super.onStop();
        //((AppCompatActivity) getActivity()).getSupportActionBar().setCustomView(R.menu.toolbar_empty);
    }
}
