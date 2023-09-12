package com.example.mvvm_crud.model;

import java.util.List;

public class ResponseModel<T> {
    private String status;
    private T data;
    private String message;

    public ResponseModel(String status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
