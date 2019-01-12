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
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.herokuapp.beevrr.beevrr.AdapterHelpers.DashboardStat;
import com.herokuapp.beevrr.beevrr.Adapters.DashboardStatsAdapter;
import com.herokuapp.beevrr.beevrr.Preferences;
import com.herokuapp.beevrr.beevrr.R;
import com.herokuapp.beevrr.beevrr.Retrofit.APIClient;
import com.herokuapp.beevrr.beevrr.Retrofit.APIInterface;
import com.jayway.jsonpath.JsonPath;

import org.apache.commons.text.WordUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private CardView bioCard;
    private CardView changeBioCard;
    private CardView changePWCard;

    private LinearLayout statsHeader;
    private LinearLayout bioHeader;
    private LinearLayout changeBioHeader;
    private LinearLayout changePWHeader;

    private TextView bio;
    private TextView changeBioField;
    private TextView changePWFieldOld;
    private TextView changePWFieldOldC;
    private TextView changePWFieldNew;
    private TextView changePWFieldNewC;

    private RecyclerView mRecyclerView;

    private Button changeBioSubmit;
    private Button changePWSubmit;

    private RecyclerView.Adapter dashboardStatsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private void initButtonListeners() {
        changeBioSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                postBioChange();
                changeBioSubmit.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        changePWSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                postPWChange();
                changePWSubmit.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });
    }

    private void initViews() {
        bioCard = view.findViewById(R.id.bio_card);
        changeBioCard = view.findViewById(R.id.change_bio_card);
        changePWCard = view.findViewById(R.id.change_pw_card);

        statsHeader = view.findViewById(R.id.stats_header);
        bioHeader = view.findViewById(R.id.bio_header);
        changeBioHeader = view.findViewById(R.id.change_bio_header);
        changePWHeader =  view.findViewById(R.id.change_pw_header);

        bio = view.findViewById(R.id.bio);
        changeBioField = view.findViewById(R.id.change_bio_field);
        changePWFieldOld = view.findViewById(R.id.new_password);
        changePWFieldOldC = view.findViewById(R.id.confirm_new_password);
        changePWFieldNew = view.findViewById(R.id.old_password);
        changePWFieldNewC = view.findViewById(R.id.confirm_old_password);

        mRecyclerView = view.findViewById(R.id.user_stats);

        changeBioSubmit = view.findViewById(R.id.change_bio_submit);
        changePWSubmit = view.findViewById(R.id.change_pw_submit);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void updateVisibility(int type) {
        bioCard.setVisibility(type);
        changeBioCard.setVisibility(type);
        changePWCard.setVisibility(type);

        statsHeader.setVisibility(type);
        bioHeader.setVisibility(type);
        changeBioHeader.setVisibility(type);
        changePWHeader.setVisibility(type);

        bio.setVisibility(type);
        changeBioField.setVisibility(type);
        changePWFieldOld.setVisibility(type);
        changePWFieldOldC.setVisibility(type);
        changePWFieldNew.setVisibility(type);
        changePWFieldNewC.setVisibility(type);

        mRecyclerView.setVisibility(type);
    }

    private void dashResult(Response<String> response) {
        String user_name;
        String biog;

        String count;
        String fixed;

        List<DashboardStat> stats = new ArrayList<>();;

        String[] types = {"total_responses", "active_responses", "total_votes",
                "active_votes", "total_discussions", "active_discussions"};

        String result = String.valueOf(response.body());
        String status = JsonPath.read(result, "$['status']");

        if (status.compareTo("not_logged_in") == 0) {
            Snackbar.make(view, "Not logged in!", Snackbar.LENGTH_SHORT).show();
        } else {
            user_name = JsonPath.read(result, "$['user'][0].user_name");
            biog = JsonPath.read(result, "$['user'][0].bio");

            for(String type: types) {
                DashboardStat thing = new DashboardStat();

                count = JsonPath.read(result, "$['user'][0]." + type).toString();
                fixed = WordUtils.capitalize(type.replace("_", " ")) + ": ";

                thing.setCount(count);
                thing.setType(fixed);

                stats.add(thing);
            }

            if(biog.length() == 0) {
                biog = "[Empty]";
            }

            bio.setText(biog);
            changeBioField.setText(biog);

            dashboardStatsAdapter = new DashboardStatsAdapter(getContext(), stats);
            mRecyclerView.setAdapter(dashboardStatsAdapter);

            updateVisibility(View.VISIBLE);
        }
    }

    private void postBioChange() {
        apiService.changeBio(changeBioField.getText().toString()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String snackMessage;

                String result = String.valueOf(response.body());
                String status = JsonPath.read(result, "$['status']");

                if (status.compareTo("not_logged_in") == 0) {
                    snackMessage = "Not logged in!";
                } else if (status.compareTo("failure") == 0) {
                    snackMessage = "Bio change failed!";
                } else {
                    snackMessage = "Bio changed!";
                }

                Snackbar.make(view, snackMessage, Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }
        });
    }

    private void postPWChange() {
        apiService.changePassword(changePWFieldOld.getText().toString(),
                changePWFieldOldC.getText().toString(),
                changePWFieldNew.getText().toString(),
                changePWFieldNewC.getText().toString()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String snackMessage;

                String result = String.valueOf(response.body());
                String status = JsonPath.read(result, "$['status']");

                if (status.compareTo("not_logged_in") == 0) {
                    snackMessage = "Not logged in!";
                } else if (status.compareTo("failure") == 0) {
                    snackMessage = "Password change failed!";
                } else {
                    snackMessage = "Password changed! Logged out.";
                }

                Snackbar.make(view, snackMessage, Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }
        });
    }

    private void getDashboard() {
        apiService.dashboard().enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                dashResult(response);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }
        });
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_dashboard, container, false);

        initViews();
        initButtonListeners();
        updateVisibility(View.GONE);
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
