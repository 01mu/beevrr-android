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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.RadioGroup;
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

public class DiscussionAddFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Activity activity;
    private Preferences preferences;
    private APIInterface apiService;
    private View view;

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
                submitDiscussion.setEnabled(false);
                postDiscussionSubmit();
                submitDiscussion.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });
    }

    private void postDiscussionSubmit() {
        apiService.submitDiscussion(proposition.getText().toString(), argument.getText().toString(),
                preArgLength, argLength, postArgLength).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String result = String.valueOf(response.body());
                String snackMessage;

                try {
                    String status = JsonPath.read(result, "$['status']");

                    if (status.compareTo("not_logged_in") == 0) {
                        snackMessage = "Not logged in!";
                        submitDiscussion.setEnabled(true);
                    } else if (status.compareTo("failure") == 0) {
                        snackMessage = "Discussion submission failed!";
                        submitDiscussion.setEnabled(true);
                    } else {
                        snackMessage = "Discussion submitted!";
                        Methods.setFragment(new DiscussionsFragment(), getFragmentManager());
                    }
                } catch (Exception e) {
                    snackMessage = "Please wait before submitting a discussion again!";
                    submitDiscussion.setEnabled(true);
                }

                Methods.snackbar(view, activity, snackMessage);
                Methods.setCookies(response, preferences);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Methods.snackbar(view, activity, "Failed to connect!");
                submitDiscussion.setEnabled(false);
            }
        });
    }

    public DiscussionAddFragment() {
    }

    public static DiscussionAddFragment newInstance(String param1, String param2) {
        DiscussionAddFragment fragment = new DiscussionAddFragment();
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

        setHasOptionsMenu(true);

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.toolbar_empty, menu);
    }
}
