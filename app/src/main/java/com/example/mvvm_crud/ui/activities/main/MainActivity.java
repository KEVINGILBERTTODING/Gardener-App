package com.example.mvvm_crud.ui.activities.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.mvvm_crud.R;
import com.example.mvvm_crud.databinding.ActivityMainBinding;
import com.example.mvvm_crud.ui.fragments.main.HomeFragment;
import com.example.mvvm_crud.ui.fragments.main.ProfileFragment;
import com.example.mvvm_crud.ui.fragments.main.SearchFragment;
import com.google.android.material.navigation.NavigationBarView;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        listener();
        moveFragment(new HomeFragment());
    }

    private void listener(){
        binding.bottomBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.mnuHome) {
                    moveFragment(new HomeFragment());
                    return true;
                }else if (item.getItemId() == R.id.mnuSearch) {
                    moveFragment(new SearchFragment());
                    return true;
                }else if (item.getItemId() == R.id.mnuProfile) {
                    moveFragment(new ProfileFragment());
                    return true;
                }
                return false;
            }
        });
    }

    private void moveFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, fragment).commit();
    }
}