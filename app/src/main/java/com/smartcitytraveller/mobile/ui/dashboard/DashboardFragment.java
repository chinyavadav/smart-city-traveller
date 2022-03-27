package com.smartcitytraveller.mobile.ui.dashboard;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.smartcitytraveller.mobile.R;
import com.smartcitytraveller.mobile.api.dto.UserDto;
import com.smartcitytraveller.mobile.common.Util;
import com.smartcitytraveller.mobile.database.SharedPreferencesManager;
import com.smartcitytraveller.mobile.api.dto.ProductDto;
import com.smartcitytraveller.mobile.ui.panic.NextOfKinFragment;
import com.smartcitytraveller.mobile.ui.panic.PanicButtonFragment;
import com.smartcitytraveller.mobile.ui.product.ProductFragment;
import com.smartcitytraveller.mobile.ui.product.ProductViewModel;
import com.smartcitytraveller.mobile.ui.profile.ProfileDetailsFragment;
import com.smartcitytraveller.mobile.ui.initial.check.CheckFragment;
import com.smartcitytraveller.mobile.ui.settings.SettingsFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = DashboardFragment.class.getSimpleName();

    DrawerLayout drawerLayout;

    TextView textViewFullName, textViewNavHeaderFullName, textViewMsisdn, textViewNavHeaderMsisdn;
    ImageView imageViewProfileAvatar, imageViewNavHeaderAvatar, imageViewMenu;
    ProgressDialog pd;

    FragmentManager fragmentManager;
    SharedPreferencesManager sharedPreferencesManager;
    UserDto userDTO;
    String authentication;
    private ProductViewModel productViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        authentication = sharedPreferencesManager.getAuthenticationToken();
        userDTO = sharedPreferencesManager.getUser();

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
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        syncDisplay();
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        syncDisplay();
    }

    public void showProfileDetailsFragment() {
        ProfileDetailsFragment profileDetailsFragment = new ProfileDetailsFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, profileDetailsFragment, ProfileDetailsFragment.class.getSimpleName());
        transaction.addToBackStack(TAG);
        transaction.commit();
    }

    public void navigateToProvider(ProductDto product) {

        Bundle bundle = new Bundle();
        bundle.putString("product", new Gson().toJson(product));
        ProductFragment productFragment = new ProductFragment();
        productFragment.setArguments(bundle);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, productFragment, FragmentTransaction.class.getSimpleName());
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
                PanicButtonFragment panicButtonFragment = new PanicButtonFragment();
                transaction.add(R.id.container, panicButtonFragment, PanicButtonFragment.class.getSimpleName());
                transaction.addToBackStack(TAG);
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

        productViewModel.getProducts(getActivity(), authentication).observe(getViewLifecycleOwner(), responseDTO -> {
            pd.dismiss();
            switch (responseDTO.getStatus()) {
                case "success":
                    Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                    userDTO = sharedPreferencesManager.getUser();
                    break;
                case "failed":
                case "error":
                    Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                    break;
            }
        });
    }

    public void logout() {
        pd.setMessage("Signing Out ...");
        pd.show();
        Util.clearSessionData(sharedPreferencesManager, getContext());
        CheckFragment checkFragment = new CheckFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, checkFragment, CheckFragment.class.getSimpleName());
        transaction.commit();
        pd.dismiss();
    }

    public void syncDisplay() {
        userDTO = sharedPreferencesManager.getUser();
        String fullName = userDTO.getFirstName() + " " + userDTO.getLastName();
        String msisdn = userDTO.getMsisdn();
        Util.loadAvatar(userDTO, imageViewProfileAvatar);
        Util.loadAvatar(userDTO, imageViewNavHeaderAvatar);
        textViewFullName.setText(fullName);
        textViewNavHeaderFullName.setText(fullName);
        textViewMsisdn.setText(msisdn);
        textViewNavHeaderMsisdn.setText(msisdn);
    }
}
    