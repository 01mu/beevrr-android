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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.herokuapp.beevrr.beevrr.Preferences;
import com.herokuapp.beevrr.beevrr.R;
import com.herokuapp.beevrr.beevrr.Retrofit.APIClient;
import com.herokuapp.beevrr.beevrr.Retrofit.APIInterface;
import com.jayway.jsonpath.JsonPath;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    Preferences preferences;
    APIInterface apiService;
    View view;

    EditText userName;
    EditText password;
    EditText passwordConfirm;

    Button register;

    private void registerUser() {
        final String newUser = userName.getText().toString();

        apiService.register(newUser,
                password.getText().toString(),
                passwordConfirm.getText().toString()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String snackMessage;

                String result = String.valueOf(response.body());
                String status = JsonPath.read(result, "$['status']");

                if (status.compareTo("failure") == 0) {
                    snackMessage = "Registration failed!";
                } else {
                    snackMessage = newUser + " registered!";
                }

                Snackbar.make(view, snackMessage, Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }
        });
    }

    public RegisterFragment() {
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        view =  inflater.inflate(R.layout.fragment_register, container, false);

        userName = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        passwordConfirm = view.findViewById(R.id.password_confirm);

        register = view.findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                registerUser();

                register.onEditorAction(EditorInfo.IME_ACTION_DONE);
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
