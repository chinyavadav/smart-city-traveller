package com.smartcitytraveller.mobile.ui.dashboard;

import static com.smartcitytraveller.mobile.common.Constants.CORE_BASE_URL;
import static com.smartcitytraveller.mobile.utils.Utils.requestLocationPermission;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.smartcitytraveller.mobile.R;
import com.smartcitytraveller.mobile.api.dto.Location;
import com.smartcitytraveller.mobile.api.dto.UserDto;
import com.smartcitytraveller.mobile.database.SharedPreferencesManager;
import com.smartcitytraveller.mobile.ui.panic.NextOfKinFragment;
import com.smartcitytraveller.mobile.ui.panic.PanicButtonFragment;
import com.smartcitytraveller.mobile.ui.profile.ProfileDetailsFragment;
import com.smartcitytraveller.mobile.ui.initial.check.CheckFragment;
import com.smartcitytraveller.mobile.ui.profile.ProfileDetailsViewModel;
import com.smartcitytraveller.mobile.ui.settings.SettingsFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.smartcitytraveller.mobile.utils.Utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/** A simple {@link Fragment} subclass. create an instance of this fragment. */
public class DashboardFragment extends Fragment
    implements NavigationView.OnNavigationItemSelectedListener {
  private static final String TAG = DashboardFragment.class.getSimpleName();

  DrawerLayout drawerLayout;

  TextView textViewFullName, textViewNavHeaderFullName, textViewMsisdn, textViewNavHeaderMsisdn;
  ImageView imageViewProfileAvatar, imageViewNavHeaderAvatar, imageViewMenu;
  ProgressDialog pd;
  WebView webViewMap;
  ImageView buttonMapOptions;

  FragmentManager fragmentManager;
  SharedPreferencesManager sharedPreferencesManager;

  Location location = new Location(0, 0);
  private FusedLocationProviderClient fusedLocationClient;

  UserDto userDto;
  String authentication, link;
  MapOptions viewName = MapOptions.placeMap;
  boolean mapOptionsActive = false;
  private Map<String, String> headers = new HashMap<>();

  private ProfileDetailsViewModel profileDetailsViewModel;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    profileDetailsViewModel = new ViewModelProvider(this).get(ProfileDetailsViewModel.class);
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_dashboard, container, false);
  }

  @SuppressLint({"DefaultLocale", "SetJavaScriptEnabled"})
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    fragmentManager = getActivity().getSupportFragmentManager();
    pd = new ProgressDialog(getActivity());
    sharedPreferencesManager = new SharedPreferencesManager(getContext());
    userDto = sharedPreferencesManager.getUser();

    requestLocationPermission(getActivity());
    getLastLocation();

    drawerLayout = view.findViewById(R.id.drawer_layout);
    NavigationView navigationView = getView().findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
    View navHeaderView = navigationView.getHeaderView(0);

    imageViewNavHeaderAvatar = navHeaderView.findViewById(R.id.image_view_nav_header_avatar);
    textViewNavHeaderFullName = navHeaderView.findViewById(R.id.text_view_nav_header_full_name);
    textViewNavHeaderMsisdn = navHeaderView.findViewById(R.id.text_view_nav_header_msisdn);

    imageViewProfileAvatar = view.findViewById(R.id.image_view_profile_avatar);
    textViewFullName = view.findViewById(R.id.text_view_full_name);
    textViewMsisdn = view.findViewById(R.id.text_view_msisdn);

    long lastSync = sharedPreferencesManager.getLastSync();
    long now = new Date().getTime();
    if (now - lastSync >= 300000) {
      syncProfileAndDisplay();
    }

    imageViewMenu = view.findViewById(R.id.image_view_menu);
    imageViewMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

    imageViewProfileAvatar.setOnClickListener(v -> showProfileDetailsFragment());

    buttonMapOptions = view.findViewById(R.id.image_view_button_map_options);
    buttonMapOptions.setOnClickListener(
        view1 -> {
          if (!mapOptionsActive) {
            mapOptionsActive = true;
            MapOptions[] mapOptions = MapOptions.values();
            CharSequence[] options = new CharSequence[mapOptions.length];
            int index = 0;
            for (MapOptions option : mapOptions) {
              options[index] = option.getDisplay();
              index++;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getString(R.string.map_options));
            builder.setNegativeButton(
                getString(android.R.string.cancel),
                (dialog, which) -> {
                  mapOptionsActive = true;
                  dialog.dismiss();
                });
            builder.setItems(
                options,
                (dialog, item) -> {
                  String option = (String) options[item];
                  mapOptionsActive = false;
                  if (MapOptions.getView(option).equals(MapOptions.refresh)) {
                    loadMap(viewName);
                  } else {
                    viewName = MapOptions.getView(option);
                    loadMap(viewName);
                  }
                });
            builder.show();
          }
        });

    webViewMap = getView().findViewById(R.id.web_view_map);
    WebSettings webSettings = webViewMap.getSettings();
    webSettings.setJavaScriptEnabled(true);
    loadMap(viewName);
  }

  public void loadMap(MapOptions viewName) {
    getLastLocation();
    link = CORE_BASE_URL + "/api/v1/navigation?viewName=" + viewName.name();
    webViewMap.setWebViewClient(new NavigationWebViewClient());
    webViewMap.loadUrl(link, headers);
  }

  @Override
  public void onStart() {
    super.onStart();
    Log.d(TAG, "onStart");
    syncDisplay();
    getLastLocation();
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume");
    syncDisplay();
    getLastLocation();
  }

  public void showProfileDetailsFragment() {
    ProfileDetailsFragment profileDetailsFragment = new ProfileDetailsFragment();
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.add(
        R.id.container, profileDetailsFragment, ProfileDetailsFragment.class.getSimpleName());
    transaction.addToBackStack(TAG);
    transaction.commit();
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    switch (item.getItemId()) {
      case R.id.nav_home:
        drawerLayout.closeDrawer(GravityCompat.START);
        break;
      case R.id.nav_profile:
        showProfileDetailsFragment();
        break;
      case R.id.nav_panic_button:
        if (userDto.getNextOfKin() != null) {
          PanicButtonFragment panicButtonFragment = new PanicButtonFragment();
          transaction.add(
              R.id.container, panicButtonFragment, PanicButtonFragment.class.getSimpleName());
          transaction.addToBackStack(TAG);
        } else {
          Bundle bundle = new Bundle();
          bundle.putBoolean("initiatePanicButton", true);
          NextOfKinFragment nextOfKinFragment = new NextOfKinFragment();
          nextOfKinFragment.setArguments(bundle);
          transaction.add(
              R.id.container, nextOfKinFragment, NextOfKinFragment.class.getSimpleName());
          transaction.addToBackStack(TAG);
        }
        break;
      case R.id.nav_next_of_kin:
        NextOfKinFragment nextOfKinFragment = new NextOfKinFragment();
        transaction.add(R.id.container, nextOfKinFragment, NextOfKinFragment.class.getSimpleName());
        transaction.addToBackStack(TAG);
        break;
      case R.id.nav_settings:
        SettingsFragment settingsFragment = new SettingsFragment();
        transaction.add(R.id.container, settingsFragment, SettingsFragment.class.getSimpleName());
        transaction.addToBackStack(TAG);
        break;
      case R.id.nav_logout:
        logout();
        break;
      default:
        break;
    }
    transaction.commit();
    drawerLayout.closeDrawer(GravityCompat.START);
    return false;
  }

  public void syncProfileAndDisplay() {
    pd.setMessage("Please Wait ...");
    pd.show();

    profileDetailsViewModel
        .hitGetUserApi(getActivity(), authentication, userDto.getId())
        .observe(
            getViewLifecycleOwner(),
            responseDto -> {
              pd.dismiss();
              switch (responseDto.getStatus()) {
                case "success":
                case "failed":
                case "error":
                  Snackbar.make(getView(), responseDto.getMessage(), Snackbar.LENGTH_LONG).show();
                  break;
              }
            });
  }

  public void logout() {
    pd.setMessage("Signing Out ...");
    pd.show();
    Utils.clearSessionData(sharedPreferencesManager, getContext());
    CheckFragment checkFragment = new CheckFragment();
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.replace(R.id.container, checkFragment, CheckFragment.class.getSimpleName());
    transaction.commit();
    pd.dismiss();
  }

  public void syncDisplay() {
    userDto = sharedPreferencesManager.getUser();
    String fullName = userDto.getFirstName() + " " + userDto.getLastName();
    String msisdn = userDto.getMsisdn();
    Utils.loadAvatar(userDto, imageViewProfileAvatar);
    Utils.loadAvatar(userDto, imageViewNavHeaderAvatar);
    textViewFullName.setText(fullName);
    textViewNavHeaderFullName.setText(fullName);
    textViewMsisdn.setText(msisdn);
    textViewNavHeaderMsisdn.setText(msisdn);
    getLastLocation();
  }

  private class NavigationWebViewClient extends WebViewClient {
    ProgressDialog pd = new ProgressDialog(getContext());

    public NavigationWebViewClient() {
      pd.setTitle("Loading...");
      pd.setMessage("Please Wait ...");
      pd.show();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
      String url = request.getUrl().toString();
      handleUrl(view, url, headers);
      return true;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      handleUrl(view, url, headers);
      return true;
    }

    @Override
    public void onPageCommitVisible(WebView view, String url) {
      super.onPageCommitVisible(view, url);
      if (pd != null) {
        pd.dismiss();
      }
    }
  }

  private void handleUrl(WebView webView, String url, Map<String, String> headers) {}

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
}
