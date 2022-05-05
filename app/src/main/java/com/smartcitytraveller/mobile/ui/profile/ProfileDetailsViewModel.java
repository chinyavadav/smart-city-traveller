package com.smartcitytraveller.mobile.ui.profile;

import static com.smartcitytraveller.mobile.utils.Utils.handleHttpException;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.smartcitytraveller.mobile.api.APIService;
import com.smartcitytraveller.mobile.api.RestClients;
import com.smartcitytraveller.mobile.api.dto.Location;
import com.smartcitytraveller.mobile.database.SharedPreferencesManager;
import com.smartcitytraveller.mobile.api.dto.UserDto;
import com.smartcitytraveller.mobile.api.dto.ResponseDto;

<<<<<<< HEAD
=======
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
<<<<<<< HEAD
>>>>>>> dc54f52 (fix commits)
=======
>>>>>>> 9444f1d (fix commits)
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileDetailsViewModel extends ViewModel {

  private static final String TAG = ProfileDetailsViewModel.class.getSimpleName();

<<<<<<< HEAD
<<<<<<< HEAD
  private MutableLiveData<ResponseDto> responseLiveData;
  private final APIService apiService = new RestClients().get();

  public MutableLiveData<ResponseDto> hitGetUserApi(
=======
=======
>>>>>>> 9444f1d (fix commits)
  private MutableLiveData<ResponseDTO> responseLiveData;
  private final APIService apiService = new RestClients().get();

  public MutableLiveData<ResponseDTO> hitGetUserApi(
<<<<<<< HEAD
>>>>>>> dc54f52 (fix commits)
=======
>>>>>>> 9444f1d (fix commits)
      final Context context, String authentication, UUID userId) {
    responseLiveData = new MutableLiveData<>();
    Call<UserDto> ul = apiService.getUser(authentication, userId);
    try {
      ul.enqueue(
          new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
              if (response.code() == 200) {
                SharedPreferencesManager sharedPreferencesManager =
                    new SharedPreferencesManager(context);
<<<<<<< HEAD
<<<<<<< HEAD
                UserDto userDto = response.body();
                sharedPreferencesManager.setUser(userDto);
                responseLiveData.setValue(
                    new ResponseDto("success", "Profile Syncing Complete!", null));
              } else {
                String responseMessage = handleHttpException(response);
                responseLiveData.setValue(new ResponseDto("failed", responseMessage, null));
=======
=======
>>>>>>> 9444f1d (fix commits)
                UserDto userDTO = response.body();
                sharedPreferencesManager.setUser(userDTO);
                responseLiveData.setValue(
                    new ResponseDTO("success", "Profile Syncing Complete!", null));
              } else {
                String responseMessage = handleHttpException(response);
                responseLiveData.setValue(new ResponseDTO("failed", responseMessage, null));
<<<<<<< HEAD
>>>>>>> dc54f52 (fix commits)
=======
>>>>>>> 9444f1d (fix commits)
              }
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
              Log.d("error", t.toString());
<<<<<<< HEAD
<<<<<<< HEAD
              responseLiveData.setValue(new ResponseDto("error", "Connectivity Issues!", null));
=======
              responseLiveData.setValue(new ResponseDTO("error", "Connectivity Issues!", null));
>>>>>>> dc54f52 (fix commits)
=======
              responseLiveData.setValue(new ResponseDTO("error", "Connectivity Issues!", null));
>>>>>>> 9444f1d (fix commits)
            }
          });
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      return responseLiveData;
    }
  }

<<<<<<< HEAD
<<<<<<< HEAD
  public MutableLiveData<ResponseDto> hitUpdateUserApi(final Context context, UserDto userDto) {
    responseLiveData = new MutableLiveData<>();
    Call<UserDto> ul = apiService.updateUser(userDto);
=======
=======
>>>>>>> 9444f1d (fix commits)
  public MutableLiveData<ResponseDTO> hitUpdateUserApi(
      final Context context, String authentication, UserDto userDto) {
    responseLiveData = new MutableLiveData<>();
    Call<UserDto> ul = apiService.updateUser(authentication, userDto);
<<<<<<< HEAD
>>>>>>> dc54f52 (fix commits)
=======
>>>>>>> 9444f1d (fix commits)
    try {
      ul.enqueue(
          new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
              if (response.code() == 200) {
<<<<<<< HEAD
<<<<<<< HEAD
                UserDto userDto = response.body();
                SharedPreferencesManager sharedPreferencesManager =
                    new SharedPreferencesManager(context);
                sharedPreferencesManager.setUser(userDto);
                responseLiveData.setValue(
                    new ResponseDto("success", "Successfully Updated!", null));
              } else {
                String responseMessage = handleHttpException(response);
                responseLiveData.setValue(new ResponseDto("failed", responseMessage, null));
=======
=======
>>>>>>> 9444f1d (fix commits)
                UserDto userDTO = response.body();
                SharedPreferencesManager sharedPreferencesManager =
                    new SharedPreferencesManager(context);
                sharedPreferencesManager.setUser(userDTO);
                responseLiveData.setValue(
                    new ResponseDTO("success", "Successfully Updated!", null));
              } else {
                String responseMessage = handleHttpException(response);
                responseLiveData.setValue(new ResponseDTO("failed", responseMessage, null));
<<<<<<< HEAD
>>>>>>> dc54f52 (fix commits)
=======
>>>>>>> 9444f1d (fix commits)
              }
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
              Log.d("error", t.toString());
<<<<<<< HEAD
<<<<<<< HEAD
              responseLiveData.setValue(new ResponseDto("error", "Connectivity Issues!", null));
=======
              responseLiveData.setValue(new ResponseDTO("error", "Connectivity Issues!", null));
>>>>>>> dc54f52 (fix commits)
=======
              responseLiveData.setValue(new ResponseDTO("error", "Connectivity Issues!", null));
>>>>>>> 9444f1d (fix commits)
            }
          });
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      return responseLiveData;
    }
  }

<<<<<<< HEAD
<<<<<<< HEAD
  public MutableLiveData<ResponseDto> hitUploadProfilePictureApi(
      final Context context, UserDto userDto) {
    responseLiveData = new MutableLiveData<>();
    Call<UserDto> ul = apiService.updateUser(userDto);
=======
=======
>>>>>>> 9444f1d (fix commits)
  public MutableLiveData<ResponseDTO> hitUploadProfilePictureApi(
      final Context context, String authorization, UserDto userDto) {
    responseLiveData = new MutableLiveData<>();
    Call<UserDto> ul = apiService.updateUser(authorization, userDto);
<<<<<<< HEAD
>>>>>>> dc54f52 (fix commits)
=======
>>>>>>> 9444f1d (fix commits)
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
                responseLiveData.setValue(
<<<<<<< HEAD
<<<<<<< HEAD
                    new ResponseDto("success", "Avatar successfully updated!", null));
              } else {
                String responseMessage = handleHttpException(response);
                responseLiveData.setValue(new ResponseDto("failed", responseMessage, null));
=======
=======
>>>>>>> 9444f1d (fix commits)
                    new ResponseDTO("success", "Avatar successfully updated!", null));
              } else {
                String responseMessage = handleHttpException(response);
                responseLiveData.setValue(new ResponseDTO("failed", responseMessage, null));
<<<<<<< HEAD
>>>>>>> dc54f52 (fix commits)
=======
>>>>>>> 9444f1d (fix commits)
              }
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
              Log.d("error", t.toString());
<<<<<<< HEAD
<<<<<<< HEAD
              responseLiveData.setValue(new ResponseDto("error", "Connectivity Issues!", null));
=======
              responseLiveData.setValue(new ResponseDTO("error", "Connectivity Issues!", null));
>>>>>>> dc54f52 (fix commits)
=======
              responseLiveData.setValue(new ResponseDTO("error", "Connectivity Issues!", null));
>>>>>>> 9444f1d (fix commits)
            }
          });
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      return responseLiveData;
    }
  }
<<<<<<< HEAD
<<<<<<< HEAD
=======
=======
>>>>>>> 9444f1d (fix commits)

  public MutableLiveData<ResponseDTO> hitPanicButtonApi(
      final Context context, String authorization, UUID userId, Location location) {
    responseLiveData = new MutableLiveData<>();
    Call<UserDto> ul = apiService.panicButton(authorization, userId, location);
    try {
      ul.enqueue(
          new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
              if (response.code() == 200) {
                responseLiveData.setValue(
                    new ResponseDTO("success", "Avatar successfully updated!", null));
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
<<<<<<< HEAD
>>>>>>> dc54f52 (fix commits)
=======
>>>>>>> 9444f1d (fix commits)
}
