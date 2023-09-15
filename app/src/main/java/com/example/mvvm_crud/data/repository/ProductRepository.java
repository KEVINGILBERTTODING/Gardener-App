package com.example.mvvm_crud.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mvvm_crud.data.remote.ApiService;
import com.example.mvvm_crud.model.ProductsModel;
import com.example.mvvm_crud.model.ResponseModel;
import com.example.mvvm_crud.util.Constants;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.MultipartBody;
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

    public LiveData<ResponseModel> deleteProduct(String id){
        MutableLiveData<ResponseModel> responseModelMutableLiveData = new MutableLiveData<>();
        apiService.deleteProduct(Constants.API_KEY, id).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful()) {
                    responseModelMutableLiveData.setValue(response.body());

                }else {
                    Gson gson = new Gson();
                    ResponseModel responseModel = gson.fromJson(response.errorBody().charStream(), ResponseModel.class);
                    responseModelMutableLiveData.setValue(new ResponseModel(Constants.FAILED_RESPONSE, null, responseModel.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {

                responseModelMutableLiveData.setValue(new ResponseModel(Constants.FAILED_RESPONSE, null, Constants.SOMETHING_WENT_WRONG));

            }
        });

        return  responseModelMutableLiveData;
    }

    public LiveData<ResponseModel> insertData(Map map, MultipartBody.Part file) {
        MutableLiveData<ResponseModel> responseModelMutableLiveData = new MutableLiveData<>();
        apiService.insertData(Constants.API_KEY,  map, file).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful()) {
                    responseModelMutableLiveData.setValue(new ResponseModel(Constants.SUCCESS_RESPONSE, null, "Yeay berhasiil menambahkan produk baru"));
                }else {

                    responseModelMutableLiveData.setValue(new ResponseModel(Constants.FAILED_RESPONSE, null, response.body().getMessage()));
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                responseModelMutableLiveData.setValue(new ResponseModel(Constants.FAILED_RESPONSE, null, Constants.SOMETHING_WENT_WRONG));


            }
        });

        return  responseModelMutableLiveData;
    }

    public LiveData<ResponseModel> updateDataimage(Map map, MultipartBody.Part file) {
        MutableLiveData<ResponseModel> responseModelMutableLiveData = new MutableLiveData<>();
        apiService.updateDataImage(Constants.API_KEY,  map, file).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful()) {
                    responseModelMutableLiveData.setValue(new ResponseModel(Constants.SUCCESS_RESPONSE, null, "Yeay berhasiil menambahkan produk baru"));
                }else {
                    Gson gson = new Gson();
                    ResponseModel responseModel = gson.fromJson(response.errorBody().charStream(), ResponseModel.class);
                    responseModelMutableLiveData.setValue(new ResponseModel(Constants.FAILED_RESPONSE, null, responseModel.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                responseModelMutableLiveData.setValue(new ResponseModel(Constants.FAILED_RESPONSE, null, Constants.SOMETHING_WENT_WRONG));


            }
        });

        return  responseModelMutableLiveData;
    }

    public LiveData<ResponseModel> updateData(Map map) {
        MutableLiveData<ResponseModel> responseModelMutableLiveData = new MutableLiveData<>();
        apiService.updateData(Constants.API_KEY,  map).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful()) {
                    responseModelMutableLiveData.setValue(new ResponseModel(Constants.SUCCESS_RESPONSE, null, "Yeay berhasiil menambahkan produk baru"));
                }else {

                    Gson gson = new Gson();
                    ResponseModel responseModel = gson.fromJson(response.errorBody().charStream(), ResponseModel.class);
                    responseModelMutableLiveData.setValue(new ResponseModel(Constants.FAILED_RESPONSE, null, responseModel.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                responseModelMutableLiveData.setValue(new ResponseModel(Constants.FAILED_RESPONSE, null, Constants.SOMETHING_WENT_WRONG));


            }
        });

        return  responseModelMutableLiveData;
    }

    public LiveData<ResponseModel<List<ProductsModel>>> filterProduct(String query) {
        MutableLiveData<ResponseModel<List<ProductsModel>>> responseModelMutableLiveData = new MutableLiveData<>();
        apiService.filterData(Constants.API_KEY, query).enqueue(new Callback<ResponseModel<List<ProductsModel>>>() {
            @Override
            public void onResponse(Call<ResponseModel<List<ProductsModel>>> call, Response<ResponseModel<List<ProductsModel>>> response) {
                if (response.isSuccessful()) {
                    responseModelMutableLiveData.setValue(response.body());
                }else {
                    Gson gson = new Gson();
                    ResponseModel responseModel = gson.fromJson(response.errorBody().charStream(), ResponseModel.class);
                    responseModelMutableLiveData.setValue(new ResponseModel<>(Constants.FAILED_RESPONSE, null, responseModel.getMessage()));

                }
            }

            @Override
            public void onFailure(Call<ResponseModel<List<ProductsModel>>> call, Throwable t) {

                responseModelMutableLiveData.setValue(new ResponseModel<>(Constants.FAILED_RESPONSE, null, Constants.SOMETHING_WENT_WRONG));
            }
        });
        return  responseModelMutableLiveData;

    }
}
