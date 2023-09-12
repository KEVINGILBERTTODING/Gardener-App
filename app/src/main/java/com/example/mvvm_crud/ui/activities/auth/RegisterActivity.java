package com.example.mvvm_crud.ui.activities.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.example.mvvm_crud.R;
import com.example.mvvm_crud.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {
    protected ActivityRegisterBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}