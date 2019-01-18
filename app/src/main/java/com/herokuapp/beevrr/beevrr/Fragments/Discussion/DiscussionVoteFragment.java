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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.RadioGroup;

import com.herokuapp.beevrr.beevrr.Methods;
import com.herokuapp.beevrr.beevrr.Preferences;
import com.herokuapp.beevrr.beevrr.R;
import com.herokuapp.beevrr.beevrr.Retrofit.APIClient;
import com.herokuapp.beevrr.beevrr.Retrofit.APIInterface;
import com.jayway.jsonpath.JsonPath;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscussionVoteFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Activity activity;
    private Preferences preferences;
    private APIInterface apiService;
    private View view;

    private RadioGroup responseType;
    private Button submitVote;

    private String currentPhase;
    private int discussionID;
    private String voteTypeString;
    private int viewChoice;

    private void initViews() {
        responseType = view.findViewById(R.id.vote_type);
        submitVote = view.findViewById(R.id.submit_vote_button);
    }

    private void initRadioListeners() {
        responseType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.vote_for:
                        voteTypeString = "for";
                        break;
                    case R.id.vote_against:
                        voteTypeString = "against";
                        break;
                    default:
                        voteTypeString = "undecided";
                        break;
                }
            }
        });
    }

    private void initButtonListeners() {
        submitVote.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                submitVote.setEnabled(false);
                postVoteSubmit();
                submitVote.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });
    }

    private void postVoteSubmit() {
        apiService.submitVote(currentPhase, discussionID,
                voteTypeString).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String result = String.valueOf(response.body());
                String snackMessage;

                try {
                    String status = JsonPath.read(result, "$['status']");

                    if (status.compareTo("not_logged_in") == 0) {
                        snackMessage = "Not logged in!";
                        submitVote.setEnabled(true);
                    } else if (status.compareTo("failure") == 0) {
                        snackMessage = "Vote submission failed!";
                        submitVote.setEnabled(true);
                    } else {
                        snackMessage = "Vote submitted!";
                        Methods.setFragment(new DiscussionsFragment(), getFragmentManager());
                    }
                } catch (Exception e) {
                    snackMessage = "Please wait before submitting a vote!";
                    submitVote.setEnabled(true);
                }

                Methods.snackbar(view, activity, snackMessage);
                Methods.setCookies(response, preferences);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Methods.snackbar(view, activity, "Failed to connect!");
                submitVote.setEnabled(false);
            }
        });
    }

    public DiscussionVoteFragment() {
    }

    public static DiscussionVoteFragment newInstance(String param1, String param2) {
        DiscussionVoteFragment fragment = new DiscussionVoteFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(viewChoice, container, false);

        setHasOptionsMenu(true);

        initViews();
        initRadioListeners();
        initButtonListeners();

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

        Bundle arguments = getArguments();

        activity = getActivity();
        preferences = new Preferences(activity);
        apiService = APIClient.getClient(activity).create(APIInterface.class);

        discussionID = arguments.getInt("id");
        currentPhase = arguments.getString("currentPhase");

        if (currentPhase.compareTo("pre-argument") == 0) {
            viewChoice = R.layout.fragment_discussion_vote_pre;
        } else {
            viewChoice = R.layout.fragment_discussion_vote_post;
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }
}
