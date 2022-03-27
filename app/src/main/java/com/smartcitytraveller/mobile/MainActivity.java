package com.smartcitytraveller.mobile;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.smartcitytraveller.mobile.api.dto.UserDto;
import com.smartcitytraveller.mobile.common.Common;
import com.smartcitytraveller.mobile.database.SharedPreferencesManager;
import com.smartcitytraveller.mobile.ui.dashboard.DashboardFragment;
import com.smartcitytraveller.mobile.ui.initial.splashscreen.SplashScreenFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    ProgressDialog pd;
    SharedPreferencesManager sharedPreferencesManager;
    UserDto userDTO;

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        pd = new ProgressDialog(this);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (Common.isSessionValid(sharedPreferencesManager)) {
            userDTO = sharedPreferencesManager.getUser();
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
}