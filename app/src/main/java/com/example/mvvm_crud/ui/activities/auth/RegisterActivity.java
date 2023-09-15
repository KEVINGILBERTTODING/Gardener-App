package com.example.mvvm_crud.ui.activities.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mvvm_crud.R;
import com.example.mvvm_crud.databinding.ActivityRegisterBinding;
import com.example.mvvm_crud.model.ResponseModel;
import com.example.mvvm_crud.model.UsersModel;
import com.example.mvvm_crud.util.Constants;
import com.example.mvvm_crud.viewmodel.AuthViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterActivity extends AppCompatActivity {
    protected ActivityRegisterBinding binding;
    private AuthViewModel authViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        listener();

    }


    private void listener() {

        binding.btnRegister.setOnClickListener(view -> {
            validateInput();
        });

        binding.tvLogin.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });

    }

    private void init() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void register(){
        authViewModel.register(
                binding.etName.getText().toString(),
                binding.etEmail.getText().toString(),
                binding.etPassword.getText().toString()
        ).observe(this, new Observer<ResponseModel<UsersModel>>() {
            @Override
            public void onChanged(ResponseModel<UsersModel> usersModelResponseModel) {
                if (usersModelResponseModel.getStatus().equals(Constants.SUCCESS_RESPONSE)) {
                    showToast(usersModelResponseModel.getMessage());
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }else {
                    showToast(usersModelResponseModel.getMessage());
                }
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void validateInput() {
        if (binding.etName.getText().toString().isEmpty()) {
            binding.etName.setError("Tidak boleh kosong");
        }else if (binding.etEmail.getText().toString().isEmpty()) {
            binding.etEmail.setError("Tidak boleh kosong");
        }else if (binding.etEmail.getText().toString().length() > 50) {
            binding.etEmail.setError("Tidak boleh lebih dari 50");
        }else if (binding.etEmail.getText().toString().contains(" ")) {
            binding.etEmail.setError("Tidak boleh mengandung spasi");
        }else if (binding.etPassword.getText().toString().isEmpty()) {
            binding.etPassword.setError("Tidak boleh kosong");
        }else if (binding.etPassword.getText().toString().length() > 150) {
            binding.etEmail.setError("Tidak boleh lebih dari 150");
        }else if (binding.etName.getText().length() < 5) {
            binding.etName.setError("Tidak boleh kurang dari 5 karakter");
        }
        else {
            register();
        }

    }

    @Override
    protected void onDestroy() {
        binding = null;

        super.onDestroy();
    }
}