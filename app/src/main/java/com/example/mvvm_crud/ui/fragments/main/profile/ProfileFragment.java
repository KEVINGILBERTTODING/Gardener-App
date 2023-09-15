package com.example.mvvm_crud.ui.fragments.main.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mvvm_crud.R;
import com.example.mvvm_crud.databinding.FragmentProfileBinding;
import com.example.mvvm_crud.ui.activities.auth.LoginActivity;
import com.example.mvvm_crud.util.Constants;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        init();

        return  binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listener();
    }

    private void listener() {
        binding.btnLogout.setOnClickListener(view -> {
            logOut();
        });
    }

    private void logOut() {
        editor.clear();
        editor.apply();
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

    private void init() {
        sharedPreferences = getContext().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        binding.tvEmail.setText(sharedPreferences.getString(Constants.EMAIL, null));
        binding.tvUsername.setText(sharedPreferences.getString(Constants.NAME, null));
    }
}