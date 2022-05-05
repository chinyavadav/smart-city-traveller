package com.smartcitytraveller.mobile.ui.settings;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.smartcitytraveller.mobile.R;
import com.smartcitytraveller.mobile.api.dto.NextOfKinConstants;
import com.smartcitytraveller.mobile.api.dto.UserDto;
import com.smartcitytraveller.mobile.database.SharedPreferencesManager;
import com.google.android.material.snackbar.Snackbar;
import com.smartcitytraveller.mobile.utils.Utils;

public class SettingsFragment extends Fragment {

  ProgressDialog pd;
  ImageView imageViewBack;
  ImageView imageViewProfileAvatar;
  EditText editTextCurrentPassword, editTextNewPassword, editTextSyncFreq;
  Button buttonChangePassword;

  boolean passwordShow = false;
  boolean verifyPasswordShow = false;
  boolean syncFreqDialogOpen = false;

  SharedPreferencesManager sharedPreferencesManager;
  private SettingsViewModel settingsViewModel;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_settings, container, false);
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    pd = new ProgressDialog(getActivity());
    sharedPreferencesManager = new SharedPreferencesManager(getContext());
    UserDto userDto = sharedPreferencesManager.getUser();

    imageViewProfileAvatar = view.findViewById(R.id.ctf_image_view_profile_avatar);
    Utils.loadAvatar(userDto, imageViewProfileAvatar);

    imageViewBack = view.findViewById(R.id.image_view_back);
    imageViewBack.setOnClickListener(v -> getActivity().onBackPressed());

    editTextCurrentPassword = view.findViewById(R.id.edit_text_current_password);
    editTextCurrentPassword.setOnTouchListener(
        (v, event) -> {
          final int DRAWABLE_RIGHT = 2;
          if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX()
                >= (editTextCurrentPassword.getRight()
                    - editTextCurrentPassword
                        .getCompoundDrawables()[DRAWABLE_RIGHT]
                        .getBounds()
                        .width())) {
              if (!passwordShow) {
                editTextCurrentPassword.setTransformationMethod(null);
                editTextCurrentPassword.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.visibility_off, 0);
                passwordShow = true;
              } else {
                editTextCurrentPassword.setTransformationMethod(new PasswordTransformationMethod());
                editTextCurrentPassword.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.visibility, 0);
                passwordShow = false;
              }
              return passwordShow;
            }
          }
          return false;
        });

    editTextNewPassword = view.findViewById(R.id.edit_text_new_password);
    editTextNewPassword.setOnTouchListener(
        (v, event) -> {
          final int DRAWABLE_RIGHT = 2;
          if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX()
                >= (editTextNewPassword.getRight()
                    - editTextNewPassword
                        .getCompoundDrawables()[DRAWABLE_RIGHT]
                        .getBounds()
                        .width())) {
              if (!verifyPasswordShow) {
                editTextNewPassword.setTransformationMethod(null);
                editTextNewPassword.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.visibility_off, 0);
                verifyPasswordShow = true;
              } else {
                editTextNewPassword.setTransformationMethod(new PasswordTransformationMethod());
                editTextNewPassword.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.visibility, 0);
                verifyPasswordShow = false;
              }
              return verifyPasswordShow;
            }
          }
          return false;
        });

    editTextSyncFreq = view.findViewById(R.id.edit_text_sync_frequency);
    String secondsString = sharedPreferencesManager.getSyncSeconds() + " Seconds";
    editTextSyncFreq.setText(secondsString);
    editTextSyncFreq.setInputType(InputType.TYPE_NULL);
    editTextSyncFreq.setOnTouchListener(
        (v, event) -> {
          if (!syncFreqDialogOpen) {
            syncFreqDialogOpen = true;
            String[] seconds = {"30 Seconds", "60 Seconds", "120 Seconds", "300 Seconds"};
            CharSequence[] options = new CharSequence[seconds.length];
            for (int i = 0; i < seconds.length; i++) {
              options[i] = seconds[i];
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getString(R.string.relationship));
            builder.setNegativeButton(
                getString(android.R.string.cancel),
                (dialog, which) -> {
                  syncFreqDialogOpen = true;
                  dialog.dismiss();
                });
            builder.setItems(
                options,
                (dialog, item) -> {
                  String option = (String) options[item];
                  int syncSeconds = Integer.parseInt(option.replace(" Seconds", "").trim());
                  sharedPreferencesManager.setSyncSeconds(syncSeconds);
                  editTextSyncFreq.setText(option);
                  syncFreqDialogOpen = false;
                });
            builder.show();
          }
          return false;
        });

    buttonChangePassword = view.findViewById(R.id.button_change_password);
    buttonChangePassword.setOnClickListener(
        v -> {
          String currentPassword = editTextCurrentPassword.getText().toString().trim();
          String newPassword = editTextNewPassword.getText().toString().trim();
          if (currentPassword.equals(userDto.getPassword()) && newPassword.length() >= 8) {
            pd.setMessage("Please Wait...");
            pd.show();

            settingsViewModel
                .hitChangePasswordApi(getContext(), userDto)
                .observe(
                    getViewLifecycleOwner(),
                    responseDto -> {
                      pd.dismiss();
                      switch (responseDto.getStatus()) {
                        case "success":
                          editTextCurrentPassword.setText(null);
                          editTextNewPassword.setText(null);
                          AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                          builder
                              .setTitle("SmartCityTraveller Alert")
                              .setMessage(responseDto.getMessage())
                              .setPositiveButton(android.R.string.yes, null)
                              .show();
                          break;
                        case "failed":
                        case "error":
                          Snackbar.make(view, responseDto.getMessage(), Snackbar.LENGTH_LONG)
                              .show();
                          break;
                      }
                    });
          } else {
            if (!currentPassword.equals(userDto.getPassword())) {
              Snackbar.make(view, "Current password not matching!", Snackbar.LENGTH_LONG).show();
            } else {
              Snackbar.make(
                      view, "Password should be longer than 8 characters!", Snackbar.LENGTH_LONG)
                  .show();
            }
          }
        });
  }
}
