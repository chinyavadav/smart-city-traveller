package com.smartcitytraveller.mobile.ui.initial.forgotpassword;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.smartcitytraveller.mobile.api.APIService;
import com.smartcitytraveller.mobile.api.RestClients;
import com.smartcitytraveller.mobile.api.dto.ResponseDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordViewModel extends ViewModel {
    private static final String TAG = ForgotPasswordViewModel.class.getSimpleName();
    private MutableLiveData<ResponseDTO> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDTO> hitResetPasswordApi(String msisdn) {
        responseLiveData = new MutableLiveData<>();
        Call<String> ul = apiService.resetPassword(msisdn);
        try {
            ul.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    String message = response.body();
                    responseLiveData.setValue(new ResponseDTO("success", message, null));
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    responseLiveData.setValue(new ResponseDTO("error", t.toString(), null));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return responseLiveData;
        }
    }
}
