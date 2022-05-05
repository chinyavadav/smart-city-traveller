package com.smartcitytraveller.mobile.ui.panic;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartcitytraveller.mobile.R;
import com.smartcitytraveller.mobile.api.dto.Location;
import com.smartcitytraveller.mobile.api.dto.NextOfKin;
import com.smartcitytraveller.mobile.api.dto.ResponseDTO;
import com.smartcitytraveller.mobile.api.dto.UserDto;
import com.smartcitytraveller.mobile.common.Util;
import com.smartcitytraveller.mobile.database.SharedPreferencesManager;
import com.smartcitytraveller.mobile.ui.dashboard.DashboardFragment;
import com.smartcitytraveller.mobile.ui.profile.ProfileDetailsViewModel;

import java.math.BigDecimal;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class PanicButtonFragment extends Fragment {

    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar, imageViewPanicButton;

    TextView textViewPanicDescription;

    FragmentManager fragmentManager;
    private SharedPreferencesManager sharedPreferencesManager;
    private ProfileDetailsViewModel profileDetailsViewModel;
    private NextOfKin nextOfKin = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        profileDetailsViewModel = new ViewModelProvider(this).get(ProfileDetailsViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_panic_button, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        String authentication = sharedPreferencesManager.getAuthenticationToken();

        UserDto userDTO = sharedPreferencesManager.getUser();
        nextOfKin = userDTO.getNextOfKin();
        if (nextOfKin == null) {
            DashboardFragment dashboardFragment = (DashboardFragment) fragmentManager.findFragmentByTag(DashboardFragment.class.getSimpleName());
            if (dashboardFragment == null) {
                dashboardFragment = new DashboardFragment();
            }
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.container, dashboardFragment, DashboardFragment.class.getSimpleName());
            transaction.commit();
        }

        textViewPanicDescription = view.findViewById(R.id.text_view_panic_description);

        imageViewProfileAvatar = view.findViewById(R.id.circular_image_view_avatar);
        Util.loadAvatar(userDTO, imageViewProfileAvatar);

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(v -> getActivity().onBackPressed());

        String panicDescription = String.format(getText(R.string.panic_description).toString(), nextOfKin.getRelationship(), nextOfKin.getFirstName(), nextOfKin.getLastName(), nextOfKin.getMsisdn());
        textViewPanicDescription.setText(panicDescription);

        imageViewPanicButton = view.findViewById(R.id.button_panic);
        imageViewPanicButton.setOnClickListener(view1 -> {
            profileDetailsViewModel.hitPanicButtonApi(getContext(),authentication,userDTO.getId(),new Location(BigDecimal.ONE,BigDecimal.ONE)).observe(getViewLifecycleOwner(), responseDTO -> {

            });
        });
    }
}