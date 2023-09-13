package com.example.mvvm_crud.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.mvvm_crud.data.repository.AuthRepository;
import com.example.mvvm_crud.model.ResponseModel;
import com.example.mvvm_crud.model.UsersModel;
import com.example.mvvm_crud.util.Constants;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AuthViewModel extends ViewModel {
    private final AuthRepository authRepository;

    @Inject
    public AuthViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public LiveData<ResponseModel<UsersModel>> register(String name, String email, String password) {
        MutableLiveData<ResponseModel<UsersModel>> responseModelMutableLiveData = new MutableLiveData<>();
        if (name == null || email == null || password == null) {
            responseModelMutableLiveData.setValue(new ResponseModel<>(Constants.FAILED_RESPONSE, null, Constants.SOMETHING_WENT_WRONG));
        }else {
            LiveData<ResponseModel<UsersModel>> responseModelLiveData = authRepository.register(name, email, password);
            responseModelLiveData.observeForever(new Observer<ResponseModel<UsersModel>>() {
                @Override
                public void onChanged(ResponseModel<UsersModel> usersModelResponseModel) {
                    responseModelMutableLiveData.setValue(usersModelResponseModel);
                    responseModelLiveData.removeObserver(this);
                }
            });
        }
        return responseModelMutableLiveData;
    }

    public LiveData<ResponseModel<UsersModel>> login (String email, String password) {
        MutableLiveData<ResponseModel<UsersModel>> responseModelMutableLiveData = new MutableLiveData<>();
        if (email == null || password == null && email.isEmpty() && password.isEmpty()){
            responseModelMutableLiveData.setValue(new ResponseModel<>(Constants.FAILED_RESPONSE, null, Constants.SOMETHING_WENT_WRONG));
        }else {
            LiveData<ResponseModel<UsersModel>> responseModelLiveData = authRepository.login(email, password);
            responseModelLiveData.observeForever(new Observer<ResponseModel<UsersModel>>() {
                @Override
                public void onChanged(ResponseModel<UsersModel> usersModelResponseModel) {
                    responseModelMutableLiveData.setValue(usersModelResponseModel);
                    responseModelLiveData.removeObserver(this);
                }
            });
        }

        return responseModelMutableLiveData;
    }

}
