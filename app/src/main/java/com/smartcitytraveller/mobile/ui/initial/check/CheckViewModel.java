package com.smartcitytraveller.mobile.ui.initial.check;

import static com.smartcitytraveller.mobile.utils.Utils.handleHttpException;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.smartcitytraveller.mobile.api.APIService;
import com.smartcitytraveller.mobile.api.RestClients;
import com.smartcitytraveller.mobile.api.dto.CheckResponseDto;
import com.smartcitytraveller.mobile.database.SharedPreferencesManager;
import com.smartcitytraveller.mobile.api.dto.ResponseDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckViewModel extends ViewModel {
    private static final String TAG = CheckViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDto> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDto> hitCheckApi(final Context context, String phoneNumber) {
        responseLiveData = new MutableLiveData<>();
        Call<CheckResponseDto> ul = apiService.check(phoneNumber);
        try {
            ul.enqueue(new Callback<CheckResponseDto>() {
                @Override
                public void onResponse(Call<CheckResponseDto> call, Response<CheckResponseDto> response) {
                    if (response.code() == 200) {
                        CheckResponseDto checkResponseDto = response.body();

                        // TODO save
                        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
                        sharedPreferencesManager.setAuthorization(checkResponseDto);

                        responseLiveData.setValue(new ResponseDto("success", null, null));
                    } else {
                        String responseMessage = handleHttpException(response);
                        responseLiveData.setValue(new ResponseDto("failed", responseMessage, null));
                    }
                }

                @Override
                public void onFailure(Call<CheckResponseDto> call, Throwable t) {
                    Log.d("error", t.toString());
                    responseLiveData.setValue(new ResponseDto("error", "Connectivity Issues!"+t.toString(), null));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return responseLiveData;
        }
    }
}

