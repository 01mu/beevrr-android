/*
    beevrr-android
    github.com/01mu
 */

package com.herokuapp.beevrr.beevrr;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.herokuapp.beevrr.beevrr.Fragments.DashboardFragment;
import com.herokuapp.beevrr.beevrr.Fragments.DiscussionsFragment;
import com.herokuapp.beevrr.beevrr.Fragments.LoginFragment;
import com.herokuapp.beevrr.beevrr.Fragments.LogoutFragment;
import com.herokuapp.beevrr.beevrr.Fragments.RegisterFragment;
import com.jayway.jsonpath.JsonPath;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
    DiscussionsFragment.OnFragmentInteractionListener,
    LoginFragment.OnFragmentInteractionListener,
    LogoutFragment.OnFragmentInteractionListener,
    DashboardFragment.OnFragmentInteractionListener,
    RegisterFragment.OnFragmentInteractionListener{

    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;

    DiscussionsFragment discussionsFragment;
    LoginFragment loginFragment;
    LogoutFragment logoutFragment;
    DashboardFragment dashboardFragment;
    RegisterFragment registerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNavigationViews();
        setNavigationSelector();

        discussionsFragment = new DiscussionsFragment();
        loginFragment = new LoginFragment();
        logoutFragment = new LogoutFragment();
        dashboardFragment = new DashboardFragment();
        registerFragment = new RegisterFragment();

        setFragment(discussionsFragment);
    }

    private void setFragment(Fragment fragment) {
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

    private void setNavigationViews() {
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        }

        navigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
    }

    private void setNavigationSelector() {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();

                        switch (menuItem.getItemId()) {
                            case R.id.nav_discussions:
                                setFragment(discussionsFragment);
                                break;
                            case R.id.nav_login:
                                setFragment(loginFragment);
                                break;
                            case R.id.nav_logout:
                                setFragment(logoutFragment);
                                break;
                            case R.id.nav_dashboard:
                                setFragment(dashboardFragment);
                                break;
                            case R.id.nav_register:
                                setFragment(registerFragment);
                                break;
                            default:
                                break;
                        }

                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri){

    }
}
