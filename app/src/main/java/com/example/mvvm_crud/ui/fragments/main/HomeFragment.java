package com.example.mvvm_crud.ui.fragments.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mvvm_crud.R;
import com.example.mvvm_crud.databinding.FragmentHomeBinding;
import com.example.mvvm_crud.util.Constants;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SharedPreferences sharedPreferences;
    private String username, userId, email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        init();

        return binding.getRoot();
    }

    private void init(){
        sharedPreferences = getContext().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.USER_ID, null);
        email = sharedPreferences.getString(Constants.EMAIL, null);
        username = sharedPreferences.getString(Constants.NAME, null);

        // textview
        binding.tvUsername.setText("Hai, " + username);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listener();

    }

    private void listener() {

    }
}