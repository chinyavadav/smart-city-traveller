package com.smartcitytraveller.mobile.ui.initial.signin;

import static com.smartcitytraveller.mobile.utils.Utils.handleHttpException;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.smartcitytraveller.mobile.api.APIService;
import com.smartcitytraveller.mobile.api.RestClients;
import com.smartcitytraveller.mobile.database.SharedPreferencesManager;
import com.smartcitytraveller.mobile.api.dto.UserDto;
import com.smartcitytraveller.mobile.api.dto.ResponseDto;
import com.smartcitytraveller.mobile.api.dto.SignInRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInViewModel extends ViewModel {
  private static final String TAG = SignInViewModel.class.getSimpleName();

  private MutableLiveData<ResponseDto> responseLiveData;
  private final APIService apiService = new RestClients().get();

  public MutableLiveData<ResponseDto> hitSignInApi(
      final Context context, SignInRequest signInRequest) {
    responseLiveData = new MutableLiveData<>();
    Call<UserDto> ul = apiService.signIn(signInRequest);
    try {
      ul.enqueue(
          new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
              if (response.code() == 200) {
                UserDto userDto = response.body();
                SharedPreferencesManager sharedPreferencesManager =
                    new SharedPreferencesManager(context);
                sharedPreferencesManager.setUser(userDto);

                responseLiveData.setValue(new ResponseDto("success", null, userDto));
              } else {
                String responseMessage = handleHttpException(response);
                responseLiveData.setValue(new ResponseDto("failed", responseMessage, null));
              }
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
              Log.d("error", t.toString());
              responseLiveData.setValue(new ResponseDto("error", "Connectivity Issues!", null));
            }
          });
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      return responseLiveData;
    }
  }
}
