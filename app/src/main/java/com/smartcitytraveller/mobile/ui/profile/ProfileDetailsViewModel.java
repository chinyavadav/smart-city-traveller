package com.smartcitytraveller.mobile.ui.profile;

import static com.smartcitytraveller.mobile.common.Util.handleHttpException;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.smartcitytraveller.mobile.api.APIService;
import com.smartcitytraveller.mobile.api.RestClients;
import com.smartcitytraveller.mobile.database.SharedPreferencesManager;
import com.smartcitytraveller.mobile.api.dto.UserDto;
import com.smartcitytraveller.mobile.api.dto.ResponseDTO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileDetailsViewModel extends ViewModel {

    private static final String TAG = ProfileDetailsViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDTO> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDTO> hitGetUserApi(final Context context, String authentication, UUID userId) {
        responseLiveData = new MutableLiveData<>();
        Call<UserDto> ul = apiService.getUser(authentication, userId);
        try {
            ul.enqueue(new Callback<UserDto>() {
                @Override
                public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                    if (response.code() == 200) {
                        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
                        UserDto userDTO = response.body();
                        sharedPreferencesManager.setUser(userDTO);
                        responseLiveData.setValue(new ResponseDTO("success", "Profile Syncing Complete!", null));
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

    public MutableLiveData<ResponseDTO> hitUpdateUserApi(final Context context, String authentication, UserDto userDto) {
        responseLiveData = new MutableLiveData<>();
        Call<UserDto> ul = apiService.updateUser(authentication, userDto);
        try {
            ul.enqueue(new Callback<UserDto>() {
                @Override
                public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                    if (response.code() == 200) {
                        UserDto userDTO = response.body();
                        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
                        sharedPreferencesManager.setUser(userDTO);
                        responseLiveData.setValue(new ResponseDTO("success", "Successfully Updated!", null));
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

    public MutableLiveData<ResponseDTO> hitUploadProfilePictureApi(final Context context, String authorization, UserDto userDto) {
        responseLiveData = new MutableLiveData<>();
        Call<UserDto> ul = apiService.updateUser(authorization, userDto);
        try {
            ul.enqueue(new Callback<UserDto>() {
                @Override
                public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                    if (response.code() == 200) {
                        UserDto userDto = response.body();
                        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
                        sharedPreferencesManager.setUser(userDto);
                        responseLiveData.setValue(new ResponseDTO("success", "Avatar successfully updated!", null));
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

