package com.example.mvvm_crud.ui.activities.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.mvvm_crud.databinding.ActivityLoginBinding;
import com.example.mvvm_crud.model.ResponseModel;
import com.example.mvvm_crud.model.UsersModel;
import com.example.mvvm_crud.ui.activities.main.MainActivity;
import com.example.mvvm_crud.util.Constants;
import com.example.mvvm_crud.viewmodel.AuthViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        listener();

        validateLogin();
    }

    private void init(){
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        sharedPreferences = this.getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }

    private void listener(){
        binding.btnLogin.setOnClickListener(view -> {
            formValidate();
        });
        binding.tvRegister.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }
    private void login() {
        authViewModel.login(binding.etEmail.getText().toString(), binding.etPassword.getText().toString())
                .observe(this, new Observer<ResponseModel<UsersModel>>() {
                    @Override
                    public void onChanged(ResponseModel<UsersModel> usersModelResponseModel) {
                        if (usersModelResponseModel.getStatus().equals(Constants.SUCCESS_RESPONSE)) {
                            editor.putBoolean(Constants.LOGIN_SESSION, true);
                            editor.putString(Constants.EMAIL, usersModelResponseModel.getData().getEmail());
                            editor.putString(Constants.USER_ID, usersModelResponseModel.getData().getId());
                            editor.putString(Constants.NAME, usersModelResponseModel.getData().getName());
                            editor.apply();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();

                        }else {
                            showToast(usersModelResponseModel.getMessage());
                        }
                    }
                });
    }

    private void formValidate() {
        if (binding.etEmail.getText().toString().isEmpty()) {
            showToast("Email tidak boleh kosong");
        } else if (binding.etEmail.getText().length() < 5) {
            showToast("Email tidak valid");
        } else if (binding.etPassword.getText().toString().isEmpty()) {
            showToast("Password tidak boleh kosong");
        }else {
            login();
        }
    }


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void validateLogin() {
        if (sharedPreferences.getBoolean(Constants.LOGIN_SESSION, false) == true) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }




}