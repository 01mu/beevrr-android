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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.herokuapp.beevrr.beevrr.AdapterHelpers.Discussion;
import com.herokuapp.beevrr.beevrr.AdapterHelpers.DiscussionResponse;
import com.herokuapp.beevrr.beevrr.Adapters.DiscussionsAdapter;
import com.herokuapp.beevrr.beevrr.Adapters.ResponsesAdapter;
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

public class DiscussionResponsesFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Activity activity;
    private Preferences preferences;
    private APIInterface apiService;
    private View view;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter responseAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private LinearLayout progressBar;

    private String responseType;
    private int discussionID;
    private List<DiscussionResponse> responses = new ArrayList<>();
    private int page = 0;

    private void initViews() {
        progressBar = view.findViewById(R.id.progress_bar);

        mRecyclerView = view.findViewById(R.id.respones_recycler);
        mRecyclerView.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void getResponses(int page) {
        apiService.getResponses(responseType, discussionID, page).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                responsesResult(response);
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

    private void responsesResult(Response<String> response) {
        String result = String.valueOf(response.body());
        String status = JsonPath.read(result, "$['status']");

        List<Object> jsonResponses;

        int id;

        String discussionResponse;
        String userName;
        String opinion;
        String time;

        int proposition;
        int userID;
        int score;

        if (status.compareTo("success") == 0) {
            jsonResponses = JsonPath.read(result, "$['responses']");

            for (Object disc : jsonResponses) {
                id = JsonPath.read(disc, "$['id']");

                discussionResponse = JsonPath.read(disc, "$['response']").toString();
                userName = JsonPath.read(disc, "$['user_name']").toString();
                opinion = JsonPath.read(disc, "$['opinion']").toString();
                time = JsonPath.read(disc, "$['date']").toString();

                proposition = JsonPath.read(disc, "$['proposition']");
                userID = JsonPath.read(disc, "$['user_id']");
                score = JsonPath.read(disc, "$['score']");

                responses.add(new DiscussionResponse(id, discussionResponse, userName, opinion,
                        time, proposition, userID, score));
            }

            if (page == 0) {
                mRecyclerView.setAdapter(responseAdapter);
            } else {
                responseAdapter.notifyDataSetChanged();
            }
        } else if (status.compareTo("end_pagination") == 0) {
            page = -1;
        } else {
            Methods.snackbar(view, getActivity(), "Error!");
        }
    }

    public DiscussionResponsesFragment() {
    }

    public static DiscussionResponsesFragment newInstance(String param1, String param2) {
        DiscussionResponsesFragment fragment = new DiscussionResponsesFragment();
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
        view = inflater.inflate(R.layout.fragment_discussion_responses, container,
                false);

        setHasOptionsMenu(true);

        responseAdapter = new ResponsesAdapter(getContext(), responses, apiService, preferences,
                view, activity);

        initViews();
        getResponses(0);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && page != -1) {
                    getResponses(++page);
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

        Bundle arguments = getArguments();

        activity = getActivity();
        preferences = new Preferences(activity);
        apiService = APIClient.getClient(activity).create(APIInterface.class);

        responseType = arguments.getString("responseType");
        discussionID = arguments.getInt("id");
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
        inflater.inflate(R.menu.toolbar_empty, menu);
    }
}
