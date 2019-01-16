/*
    beevrr-android
    github.com/01mu
 */

package com.herokuapp.beevrr.beevrr;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.v7.widget.Toolbar;

import com.herokuapp.beevrr.beevrr.Retrofit.APIClient;
import com.herokuapp.beevrr.beevrr.Retrofit.APIInterface;
import com.jayway.jsonpath.JsonPath;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Methods {
    public static void setCookies(Response<String> response, Preferences preferences) {
        List<String> cookies = response.headers().values("Set-Cookie");

        for (String cookie : cookies) {
            if (cookie.trim().contains("laravel_session")) {
                preferences.setLaravelSession(cookie.substring(cookie.indexOf("=") + 1));
            } else if (cookie.trim().contains("XSRF-TOKEN")) {
                preferences.setXSRFToken(cookie.substring(cookie.indexOf("=") + 1));
            }
        }
    }

    public static void setFragment(Fragment fragment, FragmentManager fm) {
        for (Fragment frag :fm.getFragments()) {
            fm.beginTransaction().remove(frag).commit();
        }

        if (!fragment.isAdded()) {
            fm.beginTransaction().replace(R.id.main_frame, fragment).commit();
        }
    }

    public static void addFragment(Fragment fragment, FragmentManager fm) {
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        if (!fragment.isAdded()) {
            fragmentTransaction.add(R.id.main_frame, fragment);
            fragmentTransaction.addToBackStack(fragment.toString());
            fragmentTransaction.commit();
        }
    }

    public static void snackbar(View view, Activity activity, String message) {
        if(view != null && activity != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    public static void setToolbarTitle(Activity activity, Toolbar toolbar, String message) {
        toolbar = activity.findViewById(R.id.toolbar);
        toolbar.setTitle(message);
    }

    public static void checkLoggedIn(final Preferences preferences, final View view,
                                     final Activity activity, APIInterface apiService,
                                     final Fragment fragment, final FragmentManager fm) {
        apiService.checkLoggedIn().enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String result = String.valueOf(response.body());
                String status = JsonPath.read(result, "$['status']");

                boolean loggedIn = false;

                if (status.compareTo("success") == 0) {
                    loggedIn = JsonPath.read(result, "$['logged_in']");
                }

                if(loggedIn) {
                    Methods.addFragment(fragment, fm);
                } else {
                    Methods.snackbar(view, activity, "Not logged in!");
                }

                Methods.setCookies(response, preferences);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Methods.snackbar(view, activity, "Failed to connect!");
            }
        });
    }
}
