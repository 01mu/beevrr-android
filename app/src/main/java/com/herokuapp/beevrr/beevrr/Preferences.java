package com.herokuapp.beevrr.beevrr;

import android.app.Activity;
import android.content.SharedPreferences;

import java.io.Serializable;

import static android.content.Context.MODE_PRIVATE;

public class Preferences implements Serializable {
    private SharedPreferences pref;
    private SharedPreferences.Editor edit;

    public Preferences(Activity activity) {
        pref = activity.getSharedPreferences("beevrr", MODE_PRIVATE);
        edit = pref.edit();
    }

    public String getXSRFToken() {
        return pref.getString("xsrfToken", "0");
    }

    public String getLaravelSession() {
        return pref.getString("laravelSession", "0");
    }

    public boolean getLoginStatus() {
        return pref.getBoolean("loginStatus", false);
    }

    public void setXSRFToken(String xsrfToken) {
        edit.putString("xsrfToken", xsrfToken);
        edit.commit();
    }

    public void setLaravelSession(String laravelSession) {
        edit.putString("laravelSession", laravelSession);
        edit.commit();
    }

    public void setLoginStatus(boolean status) {
        edit.putBoolean("loginStatus", status);
        edit.commit();
    }
}