package com.example.mvvm_crud.di;

import com.example.mvvm_crud.data.remote.ApiService;
import com.example.mvvm_crud.util.Constants;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    @Provides
    public static Retrofit retrofit() {
        return new Retrofit.Builder()
                .baseUrl(Constants.END_PONT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public ApiService apiService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }
}
