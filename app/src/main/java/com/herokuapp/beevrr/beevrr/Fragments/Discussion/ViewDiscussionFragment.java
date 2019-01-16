/*
    beevrr-android
    github.com/01mu
 */

package com.herokuapp.beevrr.beevrr.Fragments.Discussion;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.herokuapp.beevrr.beevrr.AdapterHelpers.Discussion;
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

public class ViewDiscussionFragment extends Fragment {
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

    private TextView proposition;
    private TextView argument;

    private TextView currentPhase;
    private TextView nextPhase;
    private TextView preArgFor;
    private TextView forChange;
    private TextView preArgAgainst;
    private TextView againstChange;
    private TextView preArgUndecided;
    private TextView winner;
    private TextView postArgFor;
    private TextView responses;
    private TextView postArgAgainst;
    private TextView votes;

    private CardView actionCard;
    private TextView did;
    private TextView res;

    private TextView user;
    private TextView date;

    private Discussion discussion;

    private void initViews() {
        proposition = view.findViewById(R.id.proposition);
        argument = view.findViewById(R.id.argument);

        currentPhase = view.findViewById(R.id.current_phase);
        nextPhase = view.findViewById(R.id.next_phase);
        preArgFor = view.findViewById(R.id.pre_arg_for);
        forChange = view.findViewById(R.id.for_change);
        preArgAgainst = view.findViewById(R.id.pre_arg_against);
        againstChange = view.findViewById(R.id.against_change);
        preArgUndecided = view.findViewById(R.id.pre_arg_undecided);
        winner = view.findViewById(R.id.winner);
        postArgFor = view.findViewById(R.id.post_arg_for);
        responses = view.findViewById(R.id.responses);
        postArgAgainst = view.findViewById(R.id.post_arg_against);
        votes = view.findViewById(R.id.votes);

        actionCard = view.findViewById(R.id.action_card);
        did = view.findViewById(R.id.action_did);
        res = view.findViewById(R.id.action_res);

        user = view.findViewById(R.id.user);
        date = view.findViewById(R.id.date);
    }

    private void getDiscussionView() {
        apiService.discussionView(discussion.getDiscussionID()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                discussionViewResult(response);
                Methods.setCookies(response, preferences);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Methods.snackbar(view, getActivity(), "Failed to connect!");
            }
        });
    }

    private void discussionViewResult(Response<String> response) {
        String result = String.valueOf(response.body());
        String status = JsonPath.read(result, "$['status']");

        String actionDid;

        if (status.compareTo("success") == 0) {
            nextPhase.setText("next: " + JsonPath.read(result, "$['next_phase']"));


            try {
                List<Object> res = JsonPath.read(result, "$['action']");
            } catch (Exception e) {
                did.setText(JsonPath.read(result, "$['action']['did']").toString());
                res.setText(JsonPath.read(result, "$['action']['res']").toString());
                actionCard.setVisibility(View.VISIBLE);
            }

        } else {
            Methods.snackbar(view, getActivity(), "Error!");
        }
    }

    public ViewDiscussionFragment() {
    }

    public static ViewDiscussionFragment newInstance(String param1, String param2) {
        ViewDiscussionFragment fragment = new ViewDiscussionFragment();
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
        view = inflater.inflate(R.layout.fragment_view_discussion, container, false);

        setHasOptionsMenu(true);

        initViews();
        getDiscussionView();

        proposition.setText(discussion.getProposition());
        argument.setText(discussion.getArgument());

        HashMap<String, String> voteCounts = discussion.getVoteCounts();

        currentPhase.setText("phase: " + discussion.getCurrentPhase());
        nextPhase.setText("next:");
        preArgFor.setText("pre-arg for: " + voteCounts.get("pa_for"));
        forChange.setText("for change: " + voteCounts.get("for_change"));
        preArgAgainst.setText("pre-arg against: " + voteCounts.get("pa_against"));
        againstChange.setText("against change: " + voteCounts.get("against_change"));
        preArgUndecided.setText("pre-arg undecided: " + voteCounts.get("pa_undecided"));
        winner.setText("winner: " + discussion.getWinner());
        postArgFor.setText("post-arg for: " + voteCounts.get("pv_for"));
        responses.setText("responses: " + Integer.toString(discussion.getReplyCount()));
        postArgAgainst.setText("post-arg against: " + voteCounts.get("pv_against"));
        votes.setText("votes: " + Integer.toString(discussion.getVoteCount()));
        user.setText(discussion.getUserName());
        date.setText(" " + discussion.getTime());

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
        //Methods.setToolbarTitle(activity, toolbar, "Discussion");
        discussion = (Discussion) getArguments().getSerializable("discussion");
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
        inflater.inflate(R.menu.toolbar_empty, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        //((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public void onStop() {
        super.onStop();
        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }
}
