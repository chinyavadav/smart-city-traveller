package com.smartcitytraveller.mobile.ui.product;

import static com.smartcitytraveller.mobile.common.Util.handleHttpException;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.smartcitytraveller.mobile.api.APIService;
import com.smartcitytraveller.mobile.api.RestClients;
import com.smartcitytraveller.mobile.api.dto.CreateProductDto;
import com.smartcitytraveller.mobile.api.dto.ProductDto;
import com.smartcitytraveller.mobile.api.dto.ResponseDTO;
import com.smartcitytraveller.mobile.database.DbHandler;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductViewModel extends ViewModel {

    private static final String TAG = com.smartcitytraveller.mobile.ui.product.ProductViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDTO> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDTO> hitSaveProductApi(String authentication, CreateProductDto createProductDto) {
        responseLiveData = new MutableLiveData<>();
        Call<ProductDto> ul = apiService.createProduct(authentication, createProductDto);
        try {
            ul.enqueue(new Callback<ProductDto>() {
                @Override
                public void onResponse(Call<ProductDto> call, Response<ProductDto> response) {
                    if (response.code() == 200) {
                        ProductDto new_productsDto = response.body();
                        responseLiveData.setValue(new ResponseDTO<>("success", "Successfully added new_products!", new_productsDto));
                    } else {
                        String responseMessage = handleHttpException(response);
                        responseLiveData.setValue(new ResponseDTO("failed", responseMessage, null));
                    }
                }

                @Override
                public void onFailure(Call<ProductDto> call, Throwable t) {
                    Log.d("error", t.toString());
                    responseLiveData.setValue(new ResponseDTO<>("error", "Successfully added new_products!", null));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return responseLiveData;
        }
    }

    public MutableLiveData<ResponseDTO> getProducts(Context context, String authentication) {
        responseLiveData = new MutableLiveData<>();
        Call<List<ProductDto>> ul = apiService.getProducts(authentication);
        try {
            ul.enqueue(new Callback<List<ProductDto>>() {
                @Override
                public void onResponse(Call<List<ProductDto>> call, Response<List<ProductDto>> response) {
                    if (response.code() == 200) {
                        List<ProductDto> products = response.body();
                        if (products != null) {
                            DbHandler dbHandler = new DbHandler(context);
                            dbHandler.deleteAllProducts();
                            for (ProductDto new_products : products) {
                                dbHandler.insertProduct(new_products);
                            }
                        }
                        responseLiveData.setValue(new ResponseDTO<>("success", "Sync Complete", products));
                    } else {
                        String responseMessage = handleHttpException(response);
                        responseLiveData.setValue(new ResponseDTO("failed", responseMessage, null));
                    }
                }

                @Override
                public void onFailure(Call<List<ProductDto>> call, Throwable t) {
                    Log.d("error", t.toString());
                    responseLiveData.setValue(new ResponseDTO<>("error", "Connectivity Error!", null));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return responseLiveData;
        }
    }
}

