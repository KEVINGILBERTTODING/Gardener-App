package com.example.mvvm_crud.ui;

public interface ItemClickListener<T> {
    void onItemClick(Integer position, T model);
}
