/*
    beevrr-android
    github.com/01mu
 */

package com.herokuapp.beevrr.beevrr.Fragments.Auth;

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

public class LoginFragment extends Fragment {
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

    Button login;
    TextView viewUsername;
    TextView viewPassword;

    private void initViews() {
        login = view.findViewById(R.id.login);
        viewUsername = view.findViewById(R.id.username);
        viewPassword = view.findViewById(R.id.password);
    }

    private void initButtonListeners() {
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                postLogin();
                login.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });
    }

    private void postLogin() {
        String user = viewUsername.getText().toString();
        String pw = viewPassword.getText().toString();

        apiService.login(user, pw).enqueue(new Callback<String>() {
            String snackMessage;
            String result;
            String status;

            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                result = String.valueOf(response.body());
                status = JsonPath.read(result, "$['status']");

                if (status.compareTo("failure") > 0) {
                    snackMessage = "Logged in!";
                    preferences.setLoginStatus(true);
                } else {
                    snackMessage = "Login failed!";
                    preferences.setLoginStatus(false);
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

    public LoginFragment() {
    }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        view = inflater.inflate(R.layout.fragment_login, container, false);

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
        Methods.setToolbarTitle(activity, toolbar, "Login");
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
