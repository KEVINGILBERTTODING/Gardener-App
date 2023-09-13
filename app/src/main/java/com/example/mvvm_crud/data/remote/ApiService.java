package com.example.mvvm_crud.data.remote;

import com.example.mvvm_crud.model.ResponseModel;
import com.example.mvvm_crud.model.UsersModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @FormUrlEncoded
    @POST("register")
    Call<ResponseModel<UsersModel>> register(
            @Header("API-KEY") String apiKey,
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("login")
    Call<ResponseModel<UsersModel>> login(
            @Header("API-KEY") String apiKey,
            @Field("email") String email,
            @Field("password") String password
    );




}
