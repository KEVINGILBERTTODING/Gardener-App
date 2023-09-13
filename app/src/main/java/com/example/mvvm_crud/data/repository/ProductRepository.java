package com.example.mvvm_crud.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mvvm_crud.data.remote.ApiService;
import com.example.mvvm_crud.model.ProductsModel;
import com.example.mvvm_crud.model.ResponseModel;
import com.example.mvvm_crud.util.Constants;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {
    private ApiService apiService;

    @Inject
    public ProductRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<ResponseModel<List<ProductsModel>>> getProduct() {
        MutableLiveData<ResponseModel<List<ProductsModel>>> responseModelMutableLiveData = new MutableLiveData<>();
        apiService.getAllProduct(Constants.API_KEY).enqueue(new Callback<ResponseModel<List<ProductsModel>>>() {
            @Override
            public void onResponse(Call<ResponseModel<List<ProductsModel>>> call, Response<ResponseModel<List<ProductsModel>>> response) {
                if (response.isSuccessful()) {
                    responseModelMutableLiveData.setValue(response.body());
                }else{
                    responseModelMutableLiveData.setValue(new ResponseModel<>(Constants.FAILED_RESPONSE, null, Constants.SOMETHING_WENT_WRONG));
                }
            }

            @Override
            public void onFailure(Call<ResponseModel<List<ProductsModel>>> call, Throwable t) {
                responseModelMutableLiveData.setValue(new ResponseModel<>(Constants.FAILED_RESPONSE, null, t.getMessage().toString()));
            }
        });

        return responseModelMutableLiveData;
    }
}
