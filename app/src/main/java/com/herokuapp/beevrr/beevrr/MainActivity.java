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
import android.view.Menu;
import android.view.MenuItem;

import com.herokuapp.beevrr.beevrr.Fragments.Auth.LoginFragment;
import com.herokuapp.beevrr.beevrr.Fragments.Auth.LogoutFragment;
import com.herokuapp.beevrr.beevrr.Fragments.Auth.RegisterFragment;
import com.herokuapp.beevrr.beevrr.Fragments.Dashboard.ChangeBioFragment;
import com.herokuapp.beevrr.beevrr.Fragments.Dashboard.ChangePasswordFragment;
import com.herokuapp.beevrr.beevrr.Fragments.Dashboard.DashboardFragment;
import com.herokuapp.beevrr.beevrr.Fragments.Dashboard.UserActivityFragment;
import com.herokuapp.beevrr.beevrr.Fragments.Discussion.DiscussionAddFragment;
import com.herokuapp.beevrr.beevrr.Fragments.Discussion.DiscussionRespondFragment;
import com.herokuapp.beevrr.beevrr.Fragments.Discussion.DiscussionResponsesFragment;
import com.herokuapp.beevrr.beevrr.Fragments.Discussion.DiscussionViewFragment;
import com.herokuapp.beevrr.beevrr.Fragments.Discussion.DiscussionVoteFragment;
import com.herokuapp.beevrr.beevrr.Fragments.Discussion.DiscussionsFragment;

import static com.herokuapp.beevrr.beevrr.R.drawable.ic_menu_black_24dp;

public class MainActivity extends AppCompatActivity implements
        DiscussionsFragment.OnFragmentInteractionListener,
        LoginFragment.OnFragmentInteractionListener,
        LogoutFragment.OnFragmentInteractionListener,
        DashboardFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener,
        ChangeBioFragment.OnFragmentInteractionListener,
        ChangePasswordFragment.OnFragmentInteractionListener,
        UserActivityFragment.OnFragmentInteractionListener,
        DiscussionViewFragment.OnFragmentInteractionListener,
        DiscussionAddFragment.OnFragmentInteractionListener,
        DiscussionRespondFragment.OnFragmentInteractionListener,
        DiscussionVoteFragment.OnFragmentInteractionListener,
        DiscussionResponsesFragment.OnFragmentInteractionListener {
    private static FragmentManager fm;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_empty, menu);
        return true;
    }

    private void setNavigationViews() {
        Toolbar toolbar;
        ActionBar actionBar;

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        actionBar.setTitle("Discussions");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(ic_menu_black_24dp);

        navigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
    }

    private void setNavigationSelector() {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        Fragment set;

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
