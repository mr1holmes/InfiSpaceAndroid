package com.example.infispace;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.infispace.ui.FeedFragment;
import com.example.infispace.ui.ProfileFragment;
import com.example.infispace.util.AccountsUtil;

public class HomeActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private HomePagerAdapter mHomePagerAdapter;
    private String[] mTabNames = {"Feed", "Profile"};
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // setup toolbar
        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mHomePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mHomePagerAdapter);


        mTabLayout = (TabLayout) findViewById(R.id.home_sliding_tabs);
        mTabLayout.setupWithViewPager(mViewPager);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Server URL");
            alert.setMessage("Change Server URL");

            final EditText input = new EditText(this);
            alert.setView(input);
            input.setText(AccountsUtil.getServerUrl(this));

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = input.getText().toString();
                    AccountsUtil.setServerUrl(value, HomeActivity.this);
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class HomePagerAdapter extends FragmentPagerAdapter {

        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                FeedFragment feedFragment = new FeedFragment();
                return feedFragment;
            } else {
                ProfileFragment profileFragment = new ProfileFragment();
                return profileFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mTabNames[position];
        }
    }

}
