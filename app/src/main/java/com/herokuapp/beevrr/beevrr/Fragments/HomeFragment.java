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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.herokuapp.beevrr.beevrr.Preferences;
import com.herokuapp.beevrr.beevrr.Retrofit.APIClient;
import com.herokuapp.beevrr.beevrr.Retrofit.APIInterface;
import com.herokuapp.beevrr.beevrr.R;
import com.jayway.jsonpath.JsonPath;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private String xsrfToken;
    private String laravelSession;

    Preferences preferences;

    View view;

    APIInterface apiService;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

    private void login(String username, String password) {
        Call<String> call = apiService.login(username, password);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,@NonNull Response<String> response) {
                String result = String.valueOf(response.body());
                String status = JsonPath.read(result, "$['status']");

                if(status.compareTo("failure") > 0) {
                    Headers headers = response.headers();

                    xsrfToken = headers.values("Set-Cookie").get(0);
                    laravelSession = headers.values("Set-Cookie").get(1);

                    xsrfToken = xsrfToken.substring(xsrfToken.indexOf("=") + 1);
                    laravelSession = laravelSession.substring((laravelSession.indexOf("=")) + 1);

                    preferences.setXSRFToken(xsrfToken);
                    preferences.setLaravelSession(laravelSession);

                   Snackbar.make(view, "Logged in!", Snackbar.LENGTH_SHORT)
                            .show();
                } else {
                    Log.d("dtag", result);
                }
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }
        });
    }

    private void details() {
        Call<String> call = apiService.details();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,@NonNull Response<String> response) {
                String result = String.valueOf(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        Button login = view.findViewById(R.id.login);
        final Button details = view.findViewById(R.id.details);

        final TextView viewUsername = view.findViewById(R.id.username);
        final TextView viewPassword = view.findViewById(R.id.password);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String username = viewUsername.getText().toString();
                String password = viewPassword.getText().toString();

                if(username.length() > 0 && password.length() > 0) {
                    login(username, password);
                }
            }
        });

        details.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                details();
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
