/*
    beevrr-android
    github.com/01mu
 */

package com.herokuapp.beevrr.beevrr;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

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

    public static void setToolbarTitle(Activity activity, String message) {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        toolbar.setTitle(message);
    }
}
