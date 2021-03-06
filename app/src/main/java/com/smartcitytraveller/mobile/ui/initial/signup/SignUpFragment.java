package com.smartcitytraveller.mobile.ui.initial.signup;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.smartcitytraveller.mobile.R;
import com.smartcitytraveller.mobile.api.dto.CheckResponseDto;
import com.smartcitytraveller.mobile.api.dto.SignUpRequest;
import com.smartcitytraveller.mobile.api.dto.UserDto;
import com.smartcitytraveller.mobile.database.SharedPreferencesManager;
import com.smartcitytraveller.mobile.ui.dashboard.DashboardFragment;
import com.smartcitytraveller.mobile.ui.initial.check.CheckFragment;
import com.google.android.material.snackbar.Snackbar;
import com.smartcitytraveller.mobile.ui.panic.NextOfKinFragment;
import com.smartcitytraveller.mobile.utils.Utils;

/** A simple {@link Fragment} subclass. create an instance of this fragment. */
public class SignUpFragment extends Fragment {
  private static final String TAG = SignUpFragment.class.getSimpleName();

  ProgressDialog pd;
  TextView textViewPhoneNumber, textViewChangePhone;
  EditText editTextFirstName, editTextLastName, editTextEmail, editTextPassword;
  Button buttonSignUp;

  String msisdn;
  boolean passwordShow = false;

  SharedPreferencesManager sharedPreferencesManager;
  FragmentManager fragmentManager;
  private SignUpViewModel signUpViewModel;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_sign_up, container, false);
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    fragmentManager = getActivity().getSupportFragmentManager();
    pd = new ProgressDialog(getActivity());
    sharedPreferencesManager = new SharedPreferencesManager(getContext());
    CheckResponseDto checkResponseDto = sharedPreferencesManager.getAuthorization();
    msisdn = checkResponseDto.getMsisdn();

    textViewPhoneNumber = view.findViewById(R.id.text_view_phone_number);
    textViewPhoneNumber.setText(msisdn);
    editTextFirstName = view.findViewById(R.id.edit_text_first_name);
    editTextLastName = view.findViewById(R.id.edit_text_last_name);
    editTextEmail = view.findViewById(R.id.edit_text_email);

    editTextPassword = view.findViewById(R.id.edit_text_password);
    editTextPassword.setOnTouchListener(
        (v, event) -> {
          final int DRAWABLE_RIGHT = 2;
          if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX()
                >= (editTextPassword.getRight()
                    - editTextPassword
                        .getCompoundDrawables()[DRAWABLE_RIGHT]
                        .getBounds()
                        .width())) {
              if (!passwordShow) {
                editTextPassword.setTransformationMethod(null);
                editTextPassword.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.visibility_off, 0);
                passwordShow = true;
              } else {
                editTextPassword.setTransformationMethod(new PasswordTransformationMethod());
                editTextPassword.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.visibility, 0);
                passwordShow = false;
              }
              return passwordShow;
            }
          }
          return false;
        });

    buttonSignUp = view.findViewById(R.id.button_save_profile);
    buttonSignUp.setOnClickListener(
        v -> {
          String firstName = editTextFirstName.getText().toString().trim();
          String lastName = editTextLastName.getText().toString().trim();
          String email = editTextEmail.getText().toString().trim();
          String password = editTextPassword.getText().toString().trim();
          if (firstName.length() > 2
              && lastName.length() > 2
              && email.length() != 0
              && password.length() >= 8) {
            pd.setMessage("Please Wait ...");
            pd.show();
            signUpViewModel
                .hitSignUpApi(
                    getActivity(), new SignUpRequest(msisdn, email, firstName, lastName, password))
                .observe(
                    getViewLifecycleOwner(),
                    responseDto -> {
                      pd.dismiss();
                      switch (responseDto.getStatus()) {
                        case "success":
                          UserDto userDto = (UserDto) responseDto.getData();
                          if (userDto.getNextOfKin() == null) {
                            NextOfKinFragment nextOfKinFragment = new NextOfKinFragment();
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.replace(
                                R.id.container,
                                nextOfKinFragment,
                                NextOfKinFragment.class.getSimpleName());
                            transaction.commit();
                          } else {
                            DashboardFragment dashboardFragment = new DashboardFragment();
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.replace(
                                R.id.container,
                                dashboardFragment,
                                DashboardFragment.class.getSimpleName());
                            transaction.commit();
                          }
                          break;
                        case "failed":
                        case "error":
                          Snackbar.make(getView(), responseDto.getMessage(), Snackbar.LENGTH_LONG)
                              .show();
                          break;
                      }
                    });
          } else {
            if (firstName.length() == 0) {
              Snackbar.make(view, "Enter First Name!", Snackbar.LENGTH_LONG).show();
            } else if (lastName.length() == 0) {
              Snackbar.make(view, "Enter Last Name!", Snackbar.LENGTH_LONG).show();
            } else if (email.length() == 0) {
              Snackbar.make(view, "Enter valid Email!", Snackbar.LENGTH_LONG).show();
            } else if (password.length() < 8) {
              Snackbar.make(
                      view, "Password should have at least 8 characters!", Snackbar.LENGTH_LONG)
                  .show();
            }
          }
        });

    textViewChangePhone = view.findViewById(R.id.text_view_change_phone);
    textViewChangePhone.setOnClickListener(
        v -> {
          Utils.clearSessionData(sharedPreferencesManager, getContext());
          fragmentManager.popBackStack();
          Fragment authorizeFragment =
              fragmentManager.findFragmentByTag(CheckFragment.class.getSimpleName());
          if (authorizeFragment == null) {
            authorizeFragment = new CheckFragment();
          }
          FragmentTransaction transaction = fragmentManager.beginTransaction();
          transaction.replace(
              R.id.container, authorizeFragment, CheckFragment.class.getSimpleName());
          transaction.commit();
        });
  }
}
