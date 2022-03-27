package com.smartcitytraveller.mobile.ui.profile;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.smartcitytraveller.mobile.R;
import com.smartcitytraveller.mobile.api.dto.ResponseDTO;
import com.smartcitytraveller.mobile.common.Util;
import com.smartcitytraveller.mobile.database.SharedPreferencesManager;
import com.smartcitytraveller.mobile.api.dto.UserDto;
import com.google.android.material.snackbar.Snackbar;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {

    private static final String TAG = EditProfileFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar;

    EditText editTextFirstName, editTextLastName, editTextEmail;
    Button buttonSaveProfile;

    FragmentManager fragmentManager;
    private ProfileDetailsViewModel profileDetailsViewModel;
    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileDetailsViewModel = new ViewModelProvider(this).get(ProfileDetailsViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        String authentication = sharedPreferencesManager.getAuthenticationToken();

        UserDto userDTO = sharedPreferencesManager.getUser();
        String firstName = userDTO.getFirstName();
        String lastName = userDTO.getLastName();
        String email = userDTO.getEmail();

        editTextFirstName = view.findViewById(R.id.edit_text_first_name);
        editTextLastName = view.findViewById(R.id.edit_text_last_name);
        editTextEmail = view.findViewById(R.id.edit_text_email);

        editTextFirstName.setText(firstName);
        editTextLastName.setText(lastName);
        editTextEmail.setText(email);

        imageViewProfileAvatar = view.findViewById(R.id.circular_image_view_avatar);
        Util.loadAvatar(userDTO, imageViewProfileAvatar);

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(v -> getActivity().onBackPressed());


        buttonSaveProfile = view.findViewById(R.id.button_save_profile);
        buttonSaveProfile.setOnClickListener(v -> {
            String firstName1 = editTextFirstName.getText().toString();
            String lastName1 = editTextLastName.getText().toString();
            String email1 = editTextEmail.getText().toString();

            if (firstName1.length() > 1 && lastName1.length() > 1 && email1.length() > 1) {
                if (firstName1.equals(userDTO.getFirstName()) && lastName1.equals(userDTO.getLastName()) && email1.equals(userDTO.getEmail())) {
                    Snackbar.make(view, "No changes detected!", Snackbar.LENGTH_LONG).show();
                } else {
                    userDTO.setEmail(email1);
                    userDTO.setFirstName(firstName1);
                    userDTO.setLastName(lastName1);
                    pd.setMessage("Updating ...");
                    pd.show();
                    profileDetailsViewModel.hitUpdateUserApi(getActivity(), authentication, userDTO).observe(getViewLifecycleOwner(), new Observer<ResponseDTO>() {
                        @Override
                        public void onChanged(ResponseDTO responseDTO) {
                            pd.dismiss();
                            switch (responseDTO.getStatus()) {
                                case "success":
                                    Snackbar.make(view, "Successfully updated profile!", Snackbar.LENGTH_LONG).show();
                                    break;
                                case "failed":
                                case "error":
                                    Snackbar.make(view, responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    });
                }
            } else {
                if (firstName1.length() < 2) {
                    Snackbar.make(view, "Enter valid First Name!", Snackbar.LENGTH_LONG).show();
                } else if (lastName1.length() < 2) {
                    Snackbar.make(view, "Enter valid Last Name!", Snackbar.LENGTH_LONG).show();
                } else if (email1.length() < 5) {
                    Snackbar.make(view, "Enter valid Email!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
}