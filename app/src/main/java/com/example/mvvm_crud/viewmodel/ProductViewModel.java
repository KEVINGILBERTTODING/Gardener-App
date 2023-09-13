package com.example.mvvm_crud.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mvvm_crud.data.repository.ProductRepository;
import com.example.mvvm_crud.model.ProductsModel;
import com.example.mvvm_crud.model.ResponseModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

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


}
