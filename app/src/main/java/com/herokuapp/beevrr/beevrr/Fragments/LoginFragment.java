/*
    beevrr-android
    github.com/01mu
 */

package com.herokuapp.beevrr.beevrr.Fragments;

import android.content.ClipData;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.herokuapp.beevrr.beevrr.MainActivity;
import com.herokuapp.beevrr.beevrr.Preferences;
import com.herokuapp.beevrr.beevrr.R;
import com.herokuapp.beevrr.beevrr.Retrofit.APIClient;
import com.herokuapp.beevrr.beevrr.Retrofit.APIInterface;
import com.jayway.jsonpath.JsonPath;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    Preferences preferences;
    APIInterface apiService;
    View view;

    Button login;

    TextView viewUsername;
    TextView viewPassword;

    private String xsrfToken;
    private String laravelSession;

    private void login() {
        apiService.login(viewUsername.getText().toString(),
                viewPassword.getText().toString()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                saveCookies(response);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }
        });
    }

    private void saveCookies(Response<String> response) {
        String snackMessage;

        String result = String.valueOf(response.body());
        String status = JsonPath.read(result, "$['status']");

        if (status.compareTo("failure") > 0) {
            Headers headers = response.headers();

            xsrfToken = headers.values("Set-Cookie").get(0);
            laravelSession = headers.values("Set-Cookie").get(1);

            xsrfToken = xsrfToken.substring(xsrfToken.indexOf("=") + 1);
            laravelSession = laravelSession.substring((laravelSession.indexOf("=")) + 1);

            preferences.setXSRFToken(xsrfToken);
            preferences.setLaravelSession(laravelSession);

            snackMessage = "Logged in!";
        } else {
            snackMessage = "Login failed!";
            preferences.setLoginStatus(false);
        }

        Snackbar.make(view, snackMessage, Snackbar.LENGTH_SHORT).show();
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

        preferences = new Preferences(getActivity());
        apiService = APIClient.getClient(getActivity()).create(APIInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);

        login = view.findViewById(R.id.login);

        viewUsername = view.findViewById(R.id.username);
        viewPassword = view.findViewById(R.id.password);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                login();

                login.onEditorAction(EditorInfo.IME_ACTION_DONE);
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
