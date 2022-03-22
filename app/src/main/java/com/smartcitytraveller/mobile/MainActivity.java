package com.smartcitytraveller.mobile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.amazon.geo.mapsv2.util.AmazonMapsRuntimeUtil;
import com.amazon.geo.mapsv2.util.ConnectionResult;
import com.smartcitytraveller.mobile.api.dto.ProfileDto;
import com.smartcitytraveller.mobile.common.Common;
import com.smartcitytraveller.mobile.database.SharedPreferencesManager;
import com.smartcitytraveller.mobile.ui.dashboard.DashboardFragment;
import com.smartcitytraveller.mobile.ui.initial.splashscreen.SplashScreenFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    ProgressDialog pd;
    SharedPreferencesManager sharedPreferencesManager;
    ProfileDto profileDTO;

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        pd = new ProgressDialog(this);

        if (checkForAmazonMaps()) {
            // Maps should be available. Code
            // to get a reference to the map and proceed
            // normally goes here...
        } else {
            int cr = AmazonMapsRuntimeUtil.isAmazonMapsRuntimeAvailable(this);
            String msg = getString(R.string.map_not_available)
                    + " ConnectionResult = " + cr;

            Log.w(TAG, msg);
        }

        sharedPreferencesManager = new SharedPreferencesManager(this);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (Common.isSessionValid(sharedPreferencesManager)) {
            profileDTO = sharedPreferencesManager.getProfile();
            DashboardFragment dashboardFragment = new DashboardFragment();
            transaction.add(R.id.container, dashboardFragment, DashboardFragment.class.getSimpleName());
        } else {
            SplashScreenFragment splashScreenFragment = new SplashScreenFragment();
            transaction.add(R.id.container, splashScreenFragment, SplashScreenFragment.class.getSimpleName());
        }
        transaction.commit();


    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private boolean checkForAmazonMaps() {
        return AmazonMapsRuntimeUtil
                .isAmazonMapsRuntimeAvailable(this) == ConnectionResult.SUCCESS;
    }
}