package com.smartcitytraveller.mobile.ui.initial.forgotpassword;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.smartcitytraveller.mobile.R;
import com.smartcitytraveller.mobile.api.dto.ResponseDTO;
import com.smartcitytraveller.mobile.common.Common;
import com.google.android.material.snackbar.Snackbar;
import com.hbb20.CountryCodePicker;
import com.smartcitytraveller.mobile.ui.initial.signin.SignInFragment;

import static com.smartcitytraveller.mobile.common.Validate.isValidMobileNumber;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ForgotPasswordFragment extends Fragment {

    private static final String TAG = ForgotPasswordFragment.class.getSimpleName();

    ProgressDialog pd;
    CountryCodePicker ccp;
    EditText editTextPhoneNumber;
    Button buttonRequestOTP;
    TextView textViewSignIn;

    String countryCode;
    FragmentManager fragmentManager;
    ForgotPasswordViewModel resetPasswordViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        resetPasswordViewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());

        ccp = view.findViewById(R.id.ccp);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCode = ccp.getSelectedCountryCode();
                Log.d(TAG, countryCode);
            }
        });
        editTextPhoneNumber = view.findViewById(R.id.edit_text_phone_number);

        buttonRequestOTP = view.findViewById(R.id.button_request_otp);
        buttonRequestOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryCode = (countryCode == null || countryCode.isEmpty()) ? ccp.getSelectedCountryCode() : countryCode;
                long phoneNumber = 0;
                try {
                    phoneNumber = Long.parseLong(editTextPhoneNumber.getText().toString().trim());
                } catch (Exception ignored) {

                }
                String msisdn = String.format("+%s%s", countryCode, phoneNumber);
                boolean isPhoneNumberValid = isValidMobileNumber(msisdn);
                if (isPhoneNumberValid) {
                    pd.setMessage("Resetting Password...");
                    pd.show();
                    resetPasswordViewModel.hitResetPasswordApi(msisdn).observe(getViewLifecycleOwner(), new Observer<ResponseDTO>() {
                        @Override
                        public void onChanged(ResponseDTO responseDTO) {
                            Common.hideSoftKeyboard(getActivity());
                            pd.dismiss();
                            switch (responseDTO.getStatus()) {
                                case "success":
                                    Snackbar.make(view, responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                                    SignInFragment signInFragment = new SignInFragment();
                                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                                    transaction.add(R.id.container, signInFragment, SignInFragment.class.getSimpleName());
                                    transaction.addToBackStack(TAG);
                                    transaction.commit();
                                    break;
                                case "failed":
                                case "error":
                                    Snackbar.make(view, responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    });
                } else {
                    Snackbar.make(view, "Enter valid Phone Number!", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        textViewSignIn = getView().findViewById(R.id.text_view_sign_up_title);
        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.popBackStack();
            }
        });
    }
}