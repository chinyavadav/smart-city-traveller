package com.smartcitytraveller.mobile.ui.profile;

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
                        String errorMsg;
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            errorMsg = jObjError.getString("message");
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            errorMsg = response.code() == 403 ? "Authentication Failed!" : "Error Occurred!";
                        }
                        responseLiveData.setValue(new ResponseDTO("failed", errorMsg, null));
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
                        responseLiveData.setValue(new ResponseDTO("failed", response.errorBody().toString(), null));
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

    public MutableLiveData<ResponseDTO> hitUploadProfilePictureApi(final Context context, String authorization, MultipartBody.Part requestFile) {
        responseLiveData = new MutableLiveData<>();
        Call<ResponseDTO> ul = apiService.uploadProfilePicture(authorization, requestFile);
        try {
            ul.enqueue(new Callback<ResponseDTO>() {
                @Override
                public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {
                    if (response.code() == 200) {
                        ResponseDTO responseDTO = response.body();
                        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
                        sharedPreferencesManager.setAvatarAvailable(true);
                        responseLiveData.setValue(new ResponseDTO("success", responseDTO.getMessage(), null));
                    } else {
                        String errorMsg;
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            errorMsg = jObjError.getString("message");
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            errorMsg = response.code() == 403 ? "Authentication Failed!" : "Error Occurred!";
                        }
                        responseLiveData.setValue(new ResponseDTO("failed", errorMsg, null));
                    }
                }

                @Override
                public void onFailure(Call<ResponseDTO> call, Throwable t) {
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

