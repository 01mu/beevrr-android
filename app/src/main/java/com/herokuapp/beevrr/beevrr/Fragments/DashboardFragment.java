/*
    beevrr-android
    github.com/01mu
 */

package com.herokuapp.beevrr.beevrr.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.herokuapp.beevrr.beevrr.AdapterHelpers.DashboardStat;
import com.herokuapp.beevrr.beevrr.Adapters.DashboardStatsAdapter;
import com.herokuapp.beevrr.beevrr.Methods;
import com.herokuapp.beevrr.beevrr.Preferences;
import com.herokuapp.beevrr.beevrr.R;
import com.herokuapp.beevrr.beevrr.Retrofit.APIClient;
import com.herokuapp.beevrr.beevrr.Retrofit.APIInterface;
import com.jayway.jsonpath.JsonPath;

import org.apache.commons.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    Preferences preferences;
    APIInterface apiService;
    View view;

    private LinearLayout dashboardParent;
    private TextView bio;
    private Button changeBioButton;
    private Button changePasswordButton;
    private LinearLayout progressBar;
    private CardView viewFullActivity;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter dashboardStatsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private FragmentManager fm;
    private String getBio;

    private String[] types = {"total_responses", "active_responses", "total_votes",
            "active_votes", "total_discussions", "active_discussions"};

    private void initViews() {
        dashboardParent = view.findViewById(R.id.dashboard_parent);
        bio = view.findViewById(R.id.bio);
        changeBioButton = view.findViewById(R.id.change_bio_button);
        changePasswordButton = view.findViewById(R.id.change_password_button);
        progressBar = view.findViewById(R.id.progress_bar);
        viewFullActivity = view.findViewById(R.id.view_full_activity_card);

        mRecyclerView = view.findViewById(R.id.user_stats);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void initButtonListeners() {
        changeBioButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                Fragment fragment = new ChangeBioFragment();

                bundle.putString("bio", getBio);
                fragment.setArguments(bundle);

                Methods.addFragment(fragment, fm);
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Methods.addFragment(new ChangePasswordFragment(), fm);
            }
        });
    }

    private void getDashboard() {
        apiService.dashboard().enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dashResult(response);
                Methods.setCookies(response, preferences);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                dashboardParent.setVisibility(View.GONE);
                Methods.snackbar(view, getActivity(), "Failed to connect!");
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void dashResult(Response<String> response) {
        String count;
        String fixed;

        List<DashboardStat> stats = new ArrayList<>();

        String result = String.valueOf(response.body());
        String status = JsonPath.read(result, "$['status']");

        if (status.compareTo("not_logged_in") == 0) {
            dashboardParent.setVisibility(View.GONE);
            Methods.snackbar(view, getActivity(), "Not logged in!");
            preferences.setLoginStatus(false);
        } else {
            final String userName = JsonPath.read(result, "$['user'][0].user_name");
            final int userID = JsonPath.read(result, "$['user'][0].id");
            getBio = JsonPath.read(result, "$['user'][0].bio");

            for (String type : types) {
                count = JsonPath.read(result, "$['user'][0]." + type).toString();
                fixed = WordUtils.capitalize(type.replace("_", " "));

                stats.add(new DashboardStat(fixed, count));

                if (getBio.length() == 0) {
                    getBio = "[Empty]";
                }

                bio.setText(getBio);
            }

            viewFullActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserActivityFragment userActivityFragment = new UserActivityFragment();
                    Bundle arguments = new Bundle();

                    arguments.putString("header", "Full Activity");
                    arguments.putString("request", "act");
                    arguments.putString("userName", userName);
                    arguments.putInt("userID", userID);

                    userActivityFragment.setArguments(arguments);

                    Methods.addFragment(userActivityFragment, fm);
                }
            });

            dashboardStatsAdapter = new DashboardStatsAdapter(getContext(), stats, fm, userID,
                    userName);

            mRecyclerView.setAdapter(dashboardStatsAdapter);
        }
    }

    public DashboardFragment() {
    }

    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
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
        fm = getFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_dashboard, container, false);

        initViews();
        initButtonListeners();
        getDashboard();

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
