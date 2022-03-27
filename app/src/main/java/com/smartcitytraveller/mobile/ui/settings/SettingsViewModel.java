package com.smartcitytraveller.mobile.ui.settings;

import static com.smartcitytraveller.mobile.common.Util.handleHttpException;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.smartcitytraveller.mobile.api.APIService;
import com.smartcitytraveller.mobile.api.RestClients;
import com.smartcitytraveller.mobile.api.dto.ResponseDTO;
import com.smartcitytraveller.mobile.api.dto.UserDto;
import com.smartcitytraveller.mobile.database.SharedPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsViewModel extends ViewModel {
    private static final String TAG = SettingsViewModel.class.getSimpleName();
    private MutableLiveData<ResponseDTO> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDTO> hitChangePasswordApi(Context context, String authentication, UserDto userDto) {
        responseLiveData = new MutableLiveData<>();
        Call<UserDto> ul = apiService.updateUser(authentication, userDto);
        try {
            ul.enqueue(new Callback<UserDto>() {
                @Override
                public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                    if (response.code() == 200) {
                        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
                        UserDto userDTO = response.body();
                        sharedPreferencesManager.setUser(userDTO);
                        responseLiveData.setValue(new ResponseDTO("success", "Successfully updated password", null));
                    } else {
                        String responseMessage = handleHttpException(response);
                        responseLiveData.setValue(new ResponseDTO("failed", responseMessage, null));
                    }
                }

                @Override
                public void onFailure(Call<UserDto> call, Throwable t) {
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
