package com.smartcitytraveller.mobile.ui.profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartcitytraveller.mobile.R;
import com.smartcitytraveller.mobile.common.Util;
import com.smartcitytraveller.mobile.database.SharedPreferencesManager;
import com.smartcitytraveller.mobile.api.dto.UserDto;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProfileDetailsFragment extends Fragment {
    private static final String TAG = ProfileDetailsFragment.class.getSimpleName();

    private static final int IMAGE_PICKER_REQUEST = 100;

    ImageView imageViewBack, imageViewPlus, imageViewProfileAvatar;
    ProgressDialog pd;
    TextView textViewFullName, textViewPhoneNumber, textViewEmail;
    Button buttonEditProfile;

    UserDto userDTO;
    String authentication;

    SharedPreferencesManager sharedPreferencesManager;
    private ProfileDetailsViewModel profileDetailsViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileDetailsViewModel = new ViewModelProvider(this).get(ProfileDetailsViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pd = new ProgressDialog(getActivity());

        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        userDTO = sharedPreferencesManager.getUser();
        authentication = sharedPreferencesManager.getAuthenticationToken();

        long lastSync = sharedPreferencesManager.getLastSync();
        long now = new Date().getTime();
        if (now - lastSync >= 300000) {
            pd.setMessage("Syncing Profile ...");
            pd.show();
            profileDetailsViewModel.hitGetUserApi(getActivity(), authentication, userDTO.getId()).observe(getViewLifecycleOwner(), responseDTO -> {
                pd.dismiss();
                switch (responseDTO.getStatus()) {
                    case "success":
                        Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                        UserDto userDTO1 = sharedPreferencesManager.getUser();
                        populateFields(userDTO1);
                        break;
                    case "failed":
                    case "error":
                        Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                        break;
                }
            });
        }

        imageViewProfileAvatar = view.findViewById(R.id.adf_image_view_profile_avatar);
        imageViewPlus = view.findViewById(R.id.adf_image_view_plus);
        imageViewPlus.setOnClickListener(v -> pickImage());

        Util.loadAvatar(userDTO, imageViewProfileAvatar);

        textViewFullName = view.findViewById(R.id.text_view_full_name);
        textViewPhoneNumber = view.findViewById(R.id.text_view_phone_value);
        textViewEmail = view.findViewById(R.id.text_view_email_value);

        populateFields(userDTO);

        buttonEditProfile = view.findViewById(R.id.button_edit_profile);
        buttonEditProfile.setOnClickListener(v -> {
            EditProfileFragment editProfileFragment = new EditProfileFragment();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, editProfileFragment, EditProfileFragment.class.getSimpleName());
            transaction.addToBackStack(TAG);
            transaction.commit();
        });

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(v -> getActivity().onBackPressed());

    }

    @Override
    public void onResume() {
        super.onResume();
        UserDto userDTO = sharedPreferencesManager.getUser();
        populateFields(userDTO);
        Util.loadAvatar(userDTO, imageViewProfileAvatar);
    }

    public void populateFields(UserDto userDTO) {
        String firstName = userDTO.getFirstName();
        String fullName = firstName + " " + userDTO.getLastName();
        String msisdn = userDTO.getMsisdn();
        String email = userDTO.getEmail();
        textViewFullName.setText(fullName);
        textViewPhoneNumber.setText(msisdn);
        textViewEmail.setText(email);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri sourceUri = data.getData();
            if (sourceUri != null) {
                try {
                    final InputStream imageStream = getContext().getContentResolver().openInputStream(sourceUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    userDTO.setAvatar(encodeImage(selectedImage));
                    imageViewProfileAvatar.setImageURI(sourceUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    public void pickImage() {
        ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start(IMAGE_PICKER_REQUEST);
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] b = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public void uploadAvatar(UserDto userDto) {
        pd.setMessage("Uploading Avatar ...");
        pd.show();
        profileDetailsViewModel.hitUploadProfilePictureApi(getActivity(), authentication, userDto).observe(getViewLifecycleOwner(), responseDTO -> {
            pd.dismiss();
            switch (responseDTO.getStatus()) {
                case "success":
                    UserDto userDTO = sharedPreferencesManager.getUser();
                    Util.loadAvatar(userDTO, imageViewProfileAvatar);
                    Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                    break;
                case "failed":
                case "error":
                    Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                    break;
            }
        });
    }
}