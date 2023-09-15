package com.example.mvvm_crud.ui.fragments.main.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mvvm_crud.R;
import com.example.mvvm_crud.databinding.FragmentSearchBinding;
import com.example.mvvm_crud.model.ProductsModel;
import com.example.mvvm_crud.model.ResponseModel;
import com.example.mvvm_crud.ui.ItemClickListener;
import com.example.mvvm_crud.ui.adapters.products.ProductAdapter;
import com.example.mvvm_crud.util.Constants;
import com.example.mvvm_crud.viewmodel.ProductViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.DecimalFormat;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchFragment extends Fragment implements ItemClickListener {

    private FragmentSearchBinding binding;
    private ProductViewModel productViewModel;
    private GridLayoutManager gridLayoutManager;
    private ProductAdapter productAdapter;
    private BottomSheetBehavior bottomSheetBehavior;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       binding = FragmentSearchBinding.inflate(inflater, container, false);
       init();
       return binding.getRoot();
    }

    private void init() {
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        binding.tvEmpty.setText("Temukan bunga pilihan anda");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpBottomSheet();
        listener();
    }


    private void setUpBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.rlBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setHideable(true);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    hideBottomSheet();
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }


    private void listener() {
        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterData(query);
                binding.tvEmpty.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                binding.rvProduct.setAdapter(null);
                return false;
            }
        });

        binding.overlay.setOnClickListener(view -> {
            hideBottomSheet();
        });
    }

    private void filterData(String query) {
        showShimmer();
        binding.rvProduct.setAdapter(null);
        productViewModel.filterData(query).observe(getViewLifecycleOwner(), new Observer<ResponseModel<List<ProductsModel>>>() {
            @Override
            public void onChanged(ResponseModel<List<ProductsModel>> productsModelResponseModel) {
                hideShimmer();
                if(productsModelResponseModel.getStatus().equals(Constants.SUCCESS_RESPONSE)) {
                    if (productsModelResponseModel.getData().size() > 0) {
                        productAdapter = new ProductAdapter(getContext(), productsModelResponseModel.getData());
                        gridLayoutManager = new GridLayoutManager(getContext(), 2);
                        binding.rvProduct.setAdapter(productAdapter);
                        binding.rvProduct.setLayoutManager(gridLayoutManager);
                        binding.rvProduct.setHasFixedSize(true);
                        productAdapter.setItemClickListener(SearchFragment.this);

                    }else {
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                        binding.tvEmpty.setText("Yahh bunga tidak ditemukan :(");
                    }
                }else {
                    hideShimmer();
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                    binding.tvEmpty.setText(productsModelResponseModel.getMessage());
                }
            }
        });

    }

    private void showShimmer() {
        binding.shimmerLayouth.startShimmer();
        binding.shimmerLayouth.setVisibility(View.VISIBLE);
        binding.rvProduct.setVisibility(View.GONE);
    }
    private void hideShimmer() {
        binding.shimmerLayouth.stopShimmer();
        binding.shimmerLayouth.setVisibility(View.GONE);
        binding.rvProduct.setVisibility(View.VISIBLE);
    }

    private void showBottomSheet() {
        binding.overlay.setVisibility(View.VISIBLE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(600);
    }

    private void hideBottomSheet(){
        binding.overlay.setVisibility(View.GONE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }




    @Override
    public void onDestroy() {
        binding = null;
        super.onDestroy();
    }

    private String decimalFormat(String total) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return decimalFormat.format(Double.parseDouble(total));
    }

    @Override
    public void onItemClick(Integer position, Object model) {
        ProductsModel productsModel = (ProductsModel) model;
        binding.tvProductName.setText(productsModel.getProduct_name());
        binding.tvPrice.setText("Rp. " + decimalFormat(productsModel.getPrice()));
        binding.tvDesc.setText(productsModel.getDescription());
        Glide.with(getContext()).load(productsModel.getImage()).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate().into(binding.ivFlwer);
        showBottomSheet();


    }
}