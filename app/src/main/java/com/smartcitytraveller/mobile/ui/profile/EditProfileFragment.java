package com.smartcitytraveller.mobile.ui.profile;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.smartcitytraveller.mobile.R;
import com.smartcitytraveller.mobile.database.SharedPreferencesManager;
import com.smartcitytraveller.mobile.api.dto.UserDto;
import com.google.android.material.snackbar.Snackbar;
import com.smartcitytraveller.mobile.utils.Utils;

/** A simple {@link Fragment} subclass. create an instance of this fragment. */
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
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
    UserDto userDto = sharedPreferencesManager.getUser();
    String firstName = userDto.getFirstName();
    String lastName = userDto.getLastName();
    String email = userDto.getEmail();

    editTextFirstName = view.findViewById(R.id.edit_text_first_name);
    editTextLastName = view.findViewById(R.id.edit_text_last_name);
    editTextEmail = view.findViewById(R.id.edit_text_email);

    editTextFirstName.setText(firstName);
    editTextLastName.setText(lastName);
    editTextEmail.setText(email);

    imageViewProfileAvatar = view.findViewById(R.id.circular_image_view_avatar);
    Utils.loadAvatar(userDto, imageViewProfileAvatar);

    imageViewBack = view.findViewById(R.id.image_view_back);
    imageViewBack.setOnClickListener(v -> getActivity().onBackPressed());

    buttonSaveProfile = view.findViewById(R.id.button_save_profile);
    buttonSaveProfile.setOnClickListener(
        v -> {
          String firstName1 = editTextFirstName.getText().toString().trim();
          String lastName1 = editTextLastName.getText().toString().trim();
          String email1 = editTextEmail.getText().toString().trim();

          if (firstName1.length() > 1 && lastName1.length() > 1 && email1.length() > 1) {
            if (firstName1.equals(userDto.getFirstName())
                && lastName1.equals(userDto.getLastName())
                && email1.equals(userDto.getEmail())) {
              Snackbar.make(view, "No changes detected!", Snackbar.LENGTH_LONG).show();
            } else {
              userDto.setEmail(email1);
              userDto.setFirstName(firstName1);
              userDto.setLastName(lastName1);
              pd.setMessage("Updating ...");
              pd.show();
              profileDetailsViewModel
                  .hitUpdateUserApi(getActivity(), userDto)
                  .observe(
                      getViewLifecycleOwner(),
                      responseDto -> {
                        pd.dismiss();
                        switch (responseDto.getStatus()) {
                          case "success":
                            Snackbar.make(
                                    view, "Successfully updated profile!", Snackbar.LENGTH_LONG)
                                .show();
                            break;
                          case "failed":
                          case "error":
                            Snackbar.make(view, responseDto.getMessage(), Snackbar.LENGTH_LONG)
                                .show();
                            break;
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
