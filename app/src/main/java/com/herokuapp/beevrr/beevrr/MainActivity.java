/*
    beevrr-android
    github.com/01mu
 */

package com.herokuapp.beevrr.beevrr;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.herokuapp.beevrr.beevrr.Fragments.ChangeBioFragment;
import com.herokuapp.beevrr.beevrr.Fragments.ChangePasswordFragment;
import com.herokuapp.beevrr.beevrr.Fragments.DashboardFragment;
import com.herokuapp.beevrr.beevrr.Fragments.DiscussionsFragment;
import com.herokuapp.beevrr.beevrr.Fragments.LoginFragment;
import com.herokuapp.beevrr.beevrr.Fragments.LogoutFragment;
import com.herokuapp.beevrr.beevrr.Fragments.RegisterFragment;
import com.herokuapp.beevrr.beevrr.Fragments.UserActivityFragment;

import static com.herokuapp.beevrr.beevrr.R.drawable.ic_menu_black_24dp;

public class MainActivity extends AppCompatActivity implements
        DiscussionsFragment.OnFragmentInteractionListener,
        LoginFragment.OnFragmentInteractionListener,
        LogoutFragment.OnFragmentInteractionListener,
        DashboardFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener,
        ChangeBioFragment.OnFragmentInteractionListener,
        ChangePasswordFragment.OnFragmentInteractionListener,
        UserActivityFragment.OnFragmentInteractionListener {
    FragmentManager fm;

    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fm = getSupportFragmentManager();

        setNavigationViews();
        setNavigationSelector();

        Methods.setFragment(new DiscussionsFragment(), fm);
    }

    private void setNavigationViews() {
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(ic_menu_black_24dp);
        }

        navigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
    }

    private void setNavigationSelector() {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    Fragment set;

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();

                        switch (menuItem.getItemId()) {
                            case R.id.nav_discussions:
                                set = new DiscussionsFragment();
                                break;
                            case R.id.nav_login:
                                set = new LoginFragment();
                                break;
                            case R.id.nav_logout:
                                set = new LogoutFragment();
                                break;
                            case R.id.nav_dashboard:
                                set = new DashboardFragment();
                                break;
                            case R.id.nav_register:
                                set = new RegisterFragment();
                                break;
                            default:
                                set = new DiscussionsFragment();
                                break;
                        }

                        Methods.setFragment(set, fm);

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
    public void onFragmentInteraction(Uri uri) {

    }
}
