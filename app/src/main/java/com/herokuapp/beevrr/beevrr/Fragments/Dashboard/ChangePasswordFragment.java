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

public class ChangePasswordFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Activity activity;
    private Preferences preferences;
    private APIInterface apiService;
    private View view;

    private TextView changePWFieldOld;
    private TextView changePWFieldOldC;
    private TextView changePWFieldNew;
    private TextView changePWFieldNewC;
    private Button changePWSubmit;

    private void initViews() {
        changePWFieldOld = view.findViewById(R.id.old_password);
        changePWFieldOldC = view.findViewById(R.id.confirm_old_password);
        changePWFieldNew = view.findViewById(R.id.new_password);
        changePWFieldNewC = view.findViewById(R.id.confirm_new_password);
        changePWSubmit = view.findViewById(R.id.change_pw_submit);
    }

    private void initButtonListeners() {
        changePWSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                postPasswordChange();
                changePWSubmit.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });
    }

    private void postPasswordChange() {
        apiService.changePassword(changePWFieldOld.getText().toString(),
                changePWFieldOldC.getText().toString(), changePWFieldNew.getText().toString(),
                changePWFieldNewC.getText().toString()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String result = String.valueOf(response.body());
                String status = JsonPath.read(result, "$['status']");
                String snackMessage;

                if (status.compareTo("not_logged_in") == 0) {
                    snackMessage = "Not logged in!";
                } else if (status.compareTo("failure") == 0) {
                    snackMessage = "Password change failed!";
                } else {
                    snackMessage = "Password changed! Logged out.";
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

    public ChangePasswordFragment() {

    }

    public static ChangePasswordFragment newInstance(String param1, String param2) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
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
        view = inflater.inflate(R.layout.fragment_change_password, container, false);

        initViews();
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

        activity = getActivity();
        preferences = new Preferences(activity);
        apiService = APIClient.getClient(activity).create(APIInterface.class);
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
