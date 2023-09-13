package com.example.mvvm_crud.ui.fragments.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mvvm_crud.R;
import com.example.mvvm_crud.databinding.FragmentHomeBinding;
import com.example.mvvm_crud.model.ProductsModel;
import com.example.mvvm_crud.model.ResponseModel;
import com.example.mvvm_crud.ui.adapters.products.ProductAdapter;
import com.example.mvvm_crud.util.Constants;
import com.example.mvvm_crud.viewmodel.ProductViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.Binds;
import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.HiltAndroidApp;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SharedPreferences sharedPreferences;
    private String username, userId, email;
    private ProductViewModel productViewModel;
    private ProductAdapter productAdapter;
    private GridLayoutManager gridLayoutManager;
    private List<ProductsModel> productsModelList;


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
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);


        // textview
        binding.tvUsername.setText("Hai, " + username);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listener();
        getProduct();
    }

    private void getProduct() {
        binding.swipeRefresh.setRefreshing(true);
        showShimmer();
        productViewModel.getAllProducts().observe(getViewLifecycleOwner(), new Observer<ResponseModel<List<ProductsModel>>>() {
            @Override
            public void onChanged(ResponseModel<List<ProductsModel>> listResponseModel) {
                binding.swipeRefresh.setRefreshing(false);
                if (listResponseModel.getStatus().equals(Constants.SUCCESS_RESPONSE)) {
                    if (listResponseModel.getData().size() > 0){
                        productsModelList = listResponseModel.getData();
                        hideShimmer(false, "");
                        productAdapter = new ProductAdapter(getContext(), productsModelList);
                        gridLayoutManager = new GridLayoutManager(getContext(), 2);
                        binding.rvProduct.setAdapter(productAdapter);
                        binding.rvProduct.setLayoutManager(gridLayoutManager);
                        binding.rvProduct.setHasFixedSize(true);

                    }else {
                        hideShimmer(true, "Tidak ada produk");

                    }
                }else {
                    hideShimmer(true, listResponseModel.getMessage());
                    showToast(listResponseModel.getMessage());
                }
            }
        });
    }

    private void hideShimmer(Boolean isEmpty, String message) {

        final Handler handler = new Handler();

        if (isEmpty == true) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.shimmerLayout.stopShimmer();
                    binding.shimmerLayout.setVisibility(View.GONE);
                    binding.rvProduct.setVisibility(View.VISIBLE);
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                    binding.tvEmpty.setText(message);
                }
            }, 1500);
        }else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.shimmerLayout.stopShimmer();
                    binding.shimmerLayout.setVisibility(View.GONE);
                    binding.rvProduct.setVisibility(View.VISIBLE);
                    binding.tvEmpty.setVisibility(View.GONE);
                    binding.tvEmpty.setText(message);
                }
            }, 1500);
        }
    }

    private void showShimmer() {

        binding.shimmerLayout.startShimmer();
        binding.shimmerLayout.setVisibility(View.VISIBLE);
        binding.rvProduct.setVisibility(View.GONE);
        binding.tvEmpty.setVisibility(View.GONE);
    }

    private void listener() {

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getProduct();
            }
        });

        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    public void filter(String query) {
        ArrayList<ProductsModel> filteredlist = new ArrayList<>();
        for (ProductsModel item : productsModelList) {
            if (productsModelList != null && item.getProduct_name().toLowerCase().contains(query.toLowerCase())) {
                filteredlist.add(item);
            }
        }

        productAdapter.filter(filteredlist);

        if (filteredlist.isEmpty()) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
            binding.tvEmpty.setText("Tidak ada hasil");
        }else {
            binding.tvEmpty.setVisibility(View.GONE);
            productAdapter.filter(filteredlist);
        }
    }
}