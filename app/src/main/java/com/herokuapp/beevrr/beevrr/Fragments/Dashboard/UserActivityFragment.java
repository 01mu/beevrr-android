/*
    beevrr-android
    github.com/01mu
 */

package com.herokuapp.beevrr.beevrr.Fragments.Dashboard;

import android.app.Activity;
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

import com.herokuapp.beevrr.beevrr.AdapterHelpers.UserActivity;
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

public class UserActivityFragment extends Fragment {
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
    private RecyclerView.Adapter userActivitiesAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private LinearLayout progressBar;

    private String request;
    private int userID;
    private List<UserActivity> userActivities = new ArrayList<>();
    private int page = 0;

    private void initViews() {
        progressBar = view.findViewById(R.id.progress_bar);

        mRecyclerView = view.findViewById(R.id.user_activity_recycler);
        mRecyclerView.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void getUserActivities(int page) {
        apiService.userInfo(userID, request, page).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                activitiesResult(response);
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

    private void activitiesResult(Response<String> response) {
        String result = String.valueOf(response.body());
        String status = JsonPath.read(result, "$['status']");

        List<Object> jsonActivities;

        String action;
        String opinion;
        String proposition;
        String date;

        if (status.compareTo("success") == 0) {
            jsonActivities = JsonPath.read(result, "$['activities']");

            for (Object activity : jsonActivities) {
                action = JsonPath.read(activity, "$['thing']").toString();
                opinion = JsonPath.read(activity, "$['type']").toString();
                proposition = JsonPath.read(activity, "$['prop']").toString();
                date = JsonPath.read(activity, "$['date']").toString();

                userActivities.add(new UserActivity(action, opinion, proposition, date));
            }

            if (page == 0) {
                mRecyclerView.setAdapter(userActivitiesAdapter);
            } else {
                userActivitiesAdapter.notifyDataSetChanged();
            }
        } else if (status.compareTo("end_pagination") == 0) {
            page = -1;
        } else {
            Methods.snackbar(view, getActivity(), "Error!");
        }
    }

    public UserActivityFragment() {
    }

    public static UserActivityFragment newInstance(String param1, String param2) {
        UserActivityFragment fragment = new UserActivityFragment();
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
        view = inflater.inflate(R.layout.fragment_user_activity, container, false);

        initViews();
        getUserActivities(0);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && page != -1) {
                    getUserActivities(++page);
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

        request = arguments.getString("request");
        userID = arguments.getInt("userID");

        userActivitiesAdapter = new UserActivitiesAdapter(getContext(), userActivities);
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
    public void onResume() {
        super.onResume();
        page = 0;
        userActivities.clear();
    }
}
