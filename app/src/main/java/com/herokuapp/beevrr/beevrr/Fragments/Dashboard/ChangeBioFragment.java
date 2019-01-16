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
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.herokuapp.beevrr.beevrr.Methods;
import com.herokuapp.beevrr.beevrr.Preferences;
import com.herokuapp.beevrr.beevrr.R;
import com.herokuapp.beevrr.beevrr.Retrofit.APIClient;
import com.herokuapp.beevrr.beevrr.Retrofit.APIInterface;
import com.jayway.jsonpath.JsonPath;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeBioFragment extends Fragment {
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

    private TextView changeBioField;
    private Button changeBioSubmit;

    private String getBio;

    public ChangeBioFragment() {
    }

    public static ChangeBioFragment newInstance(String param1, String param2) {
        ChangeBioFragment fragment = new ChangeBioFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private void initViews() {
        changeBioField = view.findViewById(R.id.change_bio_field);
        changeBioSubmit = view.findViewById(R.id.change_bio_submit);
    }

    private void initButtonListeners() {
        changeBioSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                postBioChange();
                changeBioSubmit.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });
    }

    private void postBioChange() {
        String newBio = changeBioField.getText().toString();

        apiService.changeBio(newBio).enqueue(new Callback<String>() {
            String snackMessage;
            String result;
            String status;

            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                result = String.valueOf(response.body());
                status = JsonPath.read(result, "$['status']");

                if (status.compareTo("not_logged_in") == 0) {
                    snackMessage = "Not logged in!";
                    preferences.setLoginStatus(false);
                } else if (status.compareTo("failure") == 0) {
                    snackMessage = "Bio change failed!";
                } else {
                    snackMessage = "Bio changed!";
                }

                Methods.setCookies(response, preferences);
                Methods.snackbar(view, getActivity(), snackMessage);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Methods.snackbar(view, getActivity(), "Failed to connect!");
            }
        });
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
        view = inflater.inflate(R.layout.fragment_change_bio, container, false);

        initViews();
        initButtonListeners();

        changeBioField.setText(getBio);

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
        //Methods.setToolbarTitle(activity, toolbar, "Change Bio");
        getBio = getArguments().getString("bio");
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
