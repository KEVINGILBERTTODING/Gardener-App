package com.example.mvvm_crud.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mvvm_crud.data.remote.ApiService;
import com.example.mvvm_crud.model.ResponseModel;
import com.example.mvvm_crud.model.UsersModel;
import com.example.mvvm_crud.util.Constants;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final ApiService apiService;

    public AuthRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    private LiveData<ResponseModel<UsersModel>> register(String name, String email, String password) {
        MutableLiveData<ResponseModel<UsersModel>> responseModelMutableLiveData = new MutableLiveData<>();
        apiService.register(Constants.API_KEY, name, email, password).enqueue(new Callback<ResponseModel<UsersModel>>() {
            @Override
            public void onResponse(Call<ResponseModel<UsersModel>> call, Response<ResponseModel<UsersModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        responseModelMutableLiveData.setValue(response);
                    }else {
                        responseModelMutableLiveData.setValue(new ResponseModel<>(Constants.FAILED_RESPONSE, null, Constants.SOMETHING_WENT_WRONG));
                    }

                }else {
                    Gson gson = new Gson();
                    ResponseModel<UsersModel> responseModel = gson.fromJson(response.errorBody(), ResponseModel.class);
                    responseModelMutableLiveData.setValue(new ResponseModel<>(Constants.FAILED_RESPONSE, null, responseModel.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<ResponseModel<UsersModel>> call, Throwable t) {
                responseModelMutableLiveData.setValue(new ResponseModel<>(Constants.FAILED_RESPONSE, null, Constants.SOMETHING_WENT_WRONG));

            }
        });
        return responseModelMutableLiveData;
    }
}
