package com.smartcitytraveller.mobile.ui.initial.signin;

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
import com.smartcitytraveller.mobile.api.dto.SignInRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInViewModel extends ViewModel {
    private static final String TAG = SignInViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDTO> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDTO> hitSignInApi(final Context context, SignInRequest signInRequest) {
        responseLiveData = new MutableLiveData<>();
        Call<AuthResponseDto> ul = apiService.signIn(signInRequest);
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

                        responseLiveData.setValue(new ResponseDTO("success", null, userDTO));
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

