package com.smartcitytraveller.mobile;

import static com.smartcitytraveller.mobile.utils.Utils.handleHttpException;
import static com.smartcitytraveller.mobile.utils.Utils.requestLocationPermission;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.smartcitytraveller.mobile.api.APIService;
import com.smartcitytraveller.mobile.api.RestClients;
import com.smartcitytraveller.mobile.api.dto.Location;
import com.smartcitytraveller.mobile.api.dto.ResponseDto;
import com.smartcitytraveller.mobile.api.dto.UserDto;
import com.smartcitytraveller.mobile.database.SharedPreferencesManager;
import com.smartcitytraveller.mobile.ui.dashboard.DashboardFragment;
import com.smartcitytraveller.mobile.ui.initial.splashscreen.SplashScreenFragment;
import com.smartcitytraveller.mobile.ui.panic.NextOfKinFragment;
import com.smartcitytraveller.mobile.ui.profile.ProfileDetailsViewModel;
import com.smartcitytraveller.mobile.utils.Utils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = MainActivity.class.getSimpleName();

  ProgressDialog pd;
  SharedPreferencesManager sharedPreferencesManager;
  FragmentManager fragmentManager;

  private UserDto userDto;
  private Location location = new Location(0, 0);
  private FusedLocationProviderClient fusedLocationClient;
  private final APIService apiService = new RestClients().get();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    fragmentManager = getSupportFragmentManager();
    pd = new ProgressDialog(this);

    sharedPreferencesManager = new SharedPreferencesManager(this);
    userDto = sharedPreferencesManager.getUser();
    new Timer()
        .schedule(
            new TimerTask() {
              @Override
              public void run() {
                userDto = sharedPreferencesManager.getUser();
                if (userDto != null) {
                  Log.d(TAG, "Syncing location...");
                  syncLocation();
                }
              }
            },
            0,
            sharedPreferencesManager.getSyncSeconds() * 1000);
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    if (Utils.isSessionValid(sharedPreferencesManager)) {
      if (userDto.getNextOfKin() == null) {
        NextOfKinFragment nextOfKinFragment = new NextOfKinFragment();
        transaction.add(R.id.container, nextOfKinFragment, NextOfKinFragment.class.getSimpleName());
      } else {
        DashboardFragment dashboardFragment = new DashboardFragment();
        transaction.add(R.id.container, dashboardFragment, DashboardFragment.class.getSimpleName());
      }
    } else {
      SplashScreenFragment splashScreenFragment = new SplashScreenFragment();
      transaction.add(
          R.id.container, splashScreenFragment, SplashScreenFragment.class.getSimpleName());
    }
    transaction.commit();
  }

  @Nullable
  @Override
  public View onCreateView(
      @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
    return super.onCreateView(name, context, attrs);
  }

  @Override
  public void onStart() {
    super.onStart();
  }

  public void getLastLocation() {
    fusedLocationClient
        .getLastLocation()
        .addOnSuccessListener(
            location -> {
              if (location != null) {
                this.location = new Location(location.getLatitude(), location.getLongitude());
              }
            });
  }

  public void syncLocation() {
    requestLocationPermission(this);
    getLastLocation();
    Call<ResponseDto> ul = apiService.syncLocation(userDto.getId(), location);
    try {
      ul.enqueue(
          new Callback<ResponseDto>() {
            @Override
            public void onResponse(Call<ResponseDto> call, Response<ResponseDto> response) {
              if (response.code() == 200) {
                ResponseDto responseDto = response.body();
                Log.d(TAG, "Successfully synced location");
              } else {
                String errorMsg = handleHttpException(response);
                Log.d(TAG, "Failed to sync location: " + errorMsg);
              }
            }

            @Override
            public void onFailure(Call<ResponseDto> call, Throwable t) {
              Log.d(TAG, "Failed to sync location: " + t.toString());
            }
          });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
