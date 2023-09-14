package com.example.mvvm_crud.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.mvvm_crud.data.repository.ProductRepository;
import com.example.mvvm_crud.model.ProductsModel;
import com.example.mvvm_crud.model.ResponseModel;
import com.example.mvvm_crud.util.Constants;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;

@HiltViewModel
public class ProductViewModel extends ViewModel {
    private final ProductRepository productRepository;

    @Inject
    public ProductViewModel (ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public LiveData<ResponseModel<List<ProductsModel>>> getAllProducts(){
        return productRepository.getProduct();
    }


    public LiveData<ResponseModel> deleteProduct(String id) {
        MutableLiveData<ResponseModel> responseModelMutableLiveData = new MutableLiveData<>();
        if (id == null && id.isEmpty()) {
            responseModelMutableLiveData.setValue(new ResponseModel(Constants.FAILED_RESPONSE, null, Constants.SOMETHING_WENT_WRONG));
        }else {
            LiveData<ResponseModel>responseModelLiveData = productRepository.deleteProduct(id);
            responseModelLiveData.observeForever(new Observer<ResponseModel>() {
                @Override
                public void onChanged(ResponseModel responseModel) {
                    responseModelMutableLiveData.setValue(responseModel);
                    responseModelLiveData.removeObserver(this);
                }
            });
        }
        return responseModelMutableLiveData;
    }

    public LiveData<ResponseModel> insert(Map map, MultipartBody.Part file) {
        MutableLiveData<ResponseModel> responseModelMutableLiveData = new MutableLiveData<>();
        if (map == null && file == null) {
            responseModelMutableLiveData.setValue(new ResponseModel(Constants.FAILED_RESPONSE, null, Constants.SOMETHING_WENT_WRONG));
        }else {
            LiveData<ResponseModel> responseModelLiveData = productRepository.insertData(map, file);
            responseModelLiveData.observeForever(new Observer<ResponseModel>() {
                @Override
                public void onChanged(ResponseModel responseModel) {
                    responseModelMutableLiveData.setValue(responseModel);
                    responseModelLiveData.removeObserver(this);
                }
            });
        }
        return responseModelMutableLiveData;
    }

}
