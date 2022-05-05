package com.smartcitytraveller.mobile.ui.initial.forgotpassword;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.smartcitytraveller.mobile.api.APIService;
import com.smartcitytraveller.mobile.api.RestClients;
import com.smartcitytraveller.mobile.api.dto.ResponseDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordViewModel extends ViewModel {
  private static final String TAG = ForgotPasswordViewModel.class.getSimpleName();
  private MutableLiveData<ResponseDto> responseLiveData;
  private final APIService apiService = new RestClients().get();

  public MutableLiveData<ResponseDto> hitResetPasswordApi(String msisdn) {
    responseLiveData = new MutableLiveData<>();
    Call<ResponseDto> ul = apiService.resetPassword(msisdn);
    try {
      ul.enqueue(
          new Callback<ResponseDto>() {
            @Override
            public void onResponse(Call<ResponseDto> call, Response<ResponseDto> response) {
              ResponseDto responseDto = response.body();
              responseLiveData.setValue(
                  new ResponseDto("success", responseDto.getMessage(), responseDto.getData()));
            }

            @Override
            public void onFailure(Call<ResponseDto> call, Throwable t) {
              responseLiveData.setValue(new ResponseDto("error", t.toString(), null));
            }
          });
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      return responseLiveData;
    }
  }
}
