package com.smartcitytraveller.mobile.ui.initial.signup;

import static com.smartcitytraveller.mobile.common.Util.handleHttpException;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.smartcitytraveller.mobile.api.APIService;
import com.smartcitytraveller.mobile.api.RestClients;
import com.smartcitytraveller.mobile.database.SharedPreferencesManager;
import com.smartcitytraveller.mobile.api.dto.UserDto;
import com.smartcitytraveller.mobile.api.dto.AuthResponseDto;
import com.smartcitytraveller.mobile.api.dto.JWT;
import com.smartcitytraveller.mobile.api.dto.ResponseDTO;
import com.smartcitytraveller.mobile.api.dto.SignUpRequest;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpViewModel extends ViewModel {
    private static final String TAG = SignUpViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDTO> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDTO> hitSignUpApi(final Context context, SignUpRequest signUpRequest) {
        responseLiveData = new MutableLiveData<>();
        Call<AuthResponseDto> ul = apiService.signUp(signUpRequest);
        try {
            ul.enqueue(new Callback<AuthResponseDto>() {
                @Override
                public void onResponse(Call<AuthResponseDto> call, Response<AuthResponseDto> response) {
                    if (response.code() == 200) {
                        AuthResponseDto authResponseDto = response.body();

                        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
                        JWT jwt = authResponseDto.getJwt();
                        sharedPreferencesManager.setJWT(jwt);

                        UserDto userDTO = authResponseDto.getUser();
                        sharedPreferencesManager.setUser(userDTO);

                        responseLiveData.setValue(new ResponseDTO("success", null, null));
                    } else {
                        String responseMessage = handleHttpException(response);
                        responseLiveData.setValue(new ResponseDTO("failed", responseMessage, null));
                    }
                }

                @Override
                public void onFailure(Call<AuthResponseDto> call, Throwable t) {
                    Log.d("error", t.toString());
                    responseLiveData.setValue(new ResponseDTO("error", "Connectivity Issues!", null));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return responseLiveData;
        }
    }
}

