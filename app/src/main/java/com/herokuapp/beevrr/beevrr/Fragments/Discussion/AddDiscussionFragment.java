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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.herokuapp.beevrr.beevrr.Fragments.Dashboard.ChangePasswordFragment;
import com.herokuapp.beevrr.beevrr.Methods;
import com.herokuapp.beevrr.beevrr.Preferences;
import com.herokuapp.beevrr.beevrr.R;
import com.herokuapp.beevrr.beevrr.Retrofit.APIClient;
import com.herokuapp.beevrr.beevrr.Retrofit.APIInterface;
import com.jayway.jsonpath.JsonPath;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddDiscussionFragment extends Fragment {
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

    private TextView proposition;
    private TextView argument;
    private RadioGroup preArgumentRadioGroup;
    private RadioGroup argumentRadioGroup;
    private RadioGroup postArgumentRadioGroup;
    private Button submitDiscussion;

    private String preArgLength;
    private String argLength;
    private String postArgLength;

    private void initViews() {
        proposition = view.findViewById(R.id.add_proposition);
        argument = view.findViewById(R.id.add_argument);
        preArgumentRadioGroup = view.findViewById(R.id.pa_group);
        argumentRadioGroup = view.findViewById(R.id.a_group);
        postArgumentRadioGroup = view.findViewById(R.id.v_group);
        submitDiscussion = view.findViewById(R.id.submit_discussion_button);
    }

    private void initRadioListeners() {
        preArgumentRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.hours1pa:
                        preArgLength = "1hour";
                        break;
                    case R.id.hours6pa:
                        preArgLength = "6hours";
                        break;
                    case R.id.hours24pa:
                        preArgLength = "1day";
                        break;
                    default:
                        preArgLength = "3days";
                        break;
                }
            }
        });

        argumentRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.hours1a:
                        argLength = "1hour";
                        break;
                    case R.id.hours6a:
                        argLength = "6hours";
                        break;
                    case R.id.hours24a:
                        argLength = "1day";
                        break;
                    default:
                        argLength = "3days";
                        break;
                }
            }
        });

        postArgumentRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.hours1v:
                        postArgLength = "1hour";
                        break;
                    case R.id.hours6v:
                        postArgLength = "6hours";
                        break;
                    case R.id.hours24v:
                        postArgLength = "1day";
                        break;
                    default:
                        postArgLength = "3days";
                        break;
                }
            }
        });
    }

    private void initButtonListeners() {
        submitDiscussion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                postDiscussionSubmit();
            }
        });
    }

    private void postDiscussionSubmit() {
        String prop = proposition.getText().toString();
        String arg = argument.getText().toString();

        apiService.submitDiscussion(prop, arg, preArgLength, argLength,
                postArgLength).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String result = String.valueOf(response.body());

                String snackMessage;

                try {
                    String status = JsonPath.read(result, "$['status']");

                    if (status.compareTo("not_logged_in") == 0) {
                        snackMessage = "Not logged in!";
                        preferences.setLoginStatus(false);
                    } else if (status.compareTo("failure") == 0) {
                        snackMessage = "Discussion submission failed!";
                    } else {
                        snackMessage = "Discussion submitted!";
                    }
                } catch (Exception e) {
                    snackMessage = "Discussion submission failed!";
                }

                Methods.snackbar(view, activity, snackMessage);
                Methods.setCookies(response, preferences);

                Methods.setFragment(new DiscussionsFragment(), getFragmentManager());
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Methods.snackbar(view, activity, "Failed to connect!");
            }
        });
    }

    public AddDiscussionFragment() {
    }

    public static AddDiscussionFragment newInstance(String param1, String param2) {
        AddDiscussionFragment fragment = new AddDiscussionFragment();
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
        view = inflater.inflate(R.layout.fragment_add_discussion, container, false);

        initViews();
        initRadioListeners();
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
        Methods.setToolbarTitle(activity, toolbar, "Beevrr");
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
