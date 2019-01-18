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
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.herokuapp.beevrr.beevrr.AdapterHelpers.Discussion;
import com.herokuapp.beevrr.beevrr.Methods;
import com.herokuapp.beevrr.beevrr.Preferences;
import com.herokuapp.beevrr.beevrr.R;
import com.herokuapp.beevrr.beevrr.Retrofit.APIClient;
import com.herokuapp.beevrr.beevrr.Retrofit.APIInterface;
import com.jayway.jsonpath.JsonPath;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscussionViewFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Activity activity;
    private Preferences preferences;
    private APIInterface apiService;
    private View view;
    private FragmentManager fm;

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
    private boolean canVote = false;
    private boolean canRespond = false;
    private int menuType = R.menu.toolbar_view_discussion_out;

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
        String actionNotice = "";

        if (status.compareTo("success") == 0) {
            nextPhase.setText("next: " + JsonPath.read(result, "$['next_phase']"));

            try {
                List<String> e = JsonPath.read(result, "$['action']");
            } catch (Exception e) {
                did.setText(JsonPath.read(result, "$['action']['did']").toString());
                res.setText(JsonPath.read(result, "$['action']['res']").toString());
                actionCard.setVisibility(View.VISIBLE);
            }

            if ((int) JsonPath.read(result, "$['can_vote']") == 1) {
                canVote = true;
                actionNotice = "You can vote on this discussion!";
            }

            if ((int) JsonPath.read(result, "$['can_reply']") == 1) {
                canRespond = true;
                actionNotice = "You can respond to this discussion!";
            }

            if (canVote || canRespond) {
                Methods.snackbar(view, getActivity(), actionNotice);
            }

            if (JsonPath.read(result, "$['logged_in']")) {
                menuType = R.menu.toolbar_view_discussion;
            } else {
                menuType = R.menu.toolbar_view_discussion_out;
            }

            setHasOptionsMenu(true);
        } else {
            Methods.snackbar(view, getActivity(), "Error!");
        }
    }

    private void getDiscussionLike() {
        apiService.discussionLike(discussion.getDiscussionID()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String result = String.valueOf(response.body());
                String snackMessage = "";

                try {
                    String status = JsonPath.read(result, "$['status']");

                    if (status.compareTo("success") == 0) {
                        switch ((String) JsonPath.read(result, "$['type']")) {
                            case "liked":
                                snackMessage = "Discussion liked!";
                                break;
                            default:
                                snackMessage = "Discussion unliked!";
                                break;
                        }
                    }
                } catch (Exception e) {
                    snackMessage = "Please wait before attempting to like again!";
                }

                Methods.snackbar(view, getActivity(), snackMessage);
                Methods.setCookies(response, preferences);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Methods.snackbar(view, getActivity(), "Failed to connect!");
            }
        });
    }

    public DiscussionViewFragment() {
    }

    public static DiscussionViewFragment newInstance(String param1, String param2) {
        DiscussionViewFragment fragment = new DiscussionViewFragment();
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
        HashMap<String, String> voteCounts = discussion.getVoteCounts();

        view = inflater.inflate(R.layout.fragment_discussion_view, container, false);

        initViews();
        getDiscussionView();

        proposition.setText(discussion.getProposition());
        argument.setText(discussion.getArgument());

        currentPhase.setText("phase: " + discussion.getCurrentPhase());
        nextPhase.setText("next: ...");
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
        fm = getFragmentManager();

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
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(menuType, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment go;
        Bundle arguments = new Bundle();
        arguments.putInt("id", discussion.getDiscussionID());

        switch (item.getItemId()) {
            case R.id.menu_respond_discussion:
                if (canRespond) {
                    go = new DiscussionRespondFragment();
                    go.setArguments(arguments);

                    Methods.addFragment(go, fm);
                } else {
                    Methods.snackbar(view, getActivity(), "Cannot respond!");
                }
                break;
            case R.id.menu_vote_discussion:
                if (canVote) {
                    arguments.putString("currentPhase", discussion.getCurrentPhase());

                    go = new DiscussionVoteFragment();
                    go.setArguments(arguments);

                    Methods.addFragment(go, fm);
                } else {
                    Methods.snackbar(view, getActivity(), "Cannot vote!");
                }
                break;
            case R.id.menu_like_discussion:
                getDiscussionLike();
                break;
            case R.id.menu_for:
                arguments.putString("responseType", "for");

                go = new DiscussionResponsesFragment();
                go.setArguments(arguments);

                Methods.addFragment(go, fm);
                break;
            default:
                arguments.putString("responseType", "against");

                go = new DiscussionResponsesFragment();
                go.setArguments(arguments);

                Methods.addFragment(go, fm);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        canVote = false;
        canRespond = false;
    }
}
