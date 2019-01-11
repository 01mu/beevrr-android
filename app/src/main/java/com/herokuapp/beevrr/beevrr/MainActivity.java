/*
    beevrr-android
    github.com/01mu
 */

package com.herokuapp.beevrr.beevrr;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.herokuapp.beevrr.beevrr.Fragments.HomeFragment;
import com.jayway.jsonpath.JsonPath;

public class MainActivity extends AppCompatActivity implements
    HomeFragment.OnFragmentInteractionListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HomeFragment homeFragment = new HomeFragment();
        setFragment(homeFragment);
    }

    public void setFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();

        for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStack();
        }

        if (!fragment.isAdded()) {
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();

            fragmentTransaction.replace(R.id.main_frame, fragment).commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri){

    }
}
