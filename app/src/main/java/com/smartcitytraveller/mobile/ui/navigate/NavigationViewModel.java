package com.smartcitytraveller.mobile.ui.navigate;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.smartcitytraveller.mobile.api.APIService;
import com.smartcitytraveller.mobile.api.RestClients;
import com.smartcitytraveller.mobile.api.dto.ResponseDTO;

public class NavigationViewModel extends ViewModel {

    private static final String TAG = NavigationViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDTO> responseLiveData;
    private final APIService apiService = new RestClients().get();

}

