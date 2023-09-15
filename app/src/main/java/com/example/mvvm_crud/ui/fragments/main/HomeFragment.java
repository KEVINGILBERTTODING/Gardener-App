package com.example.mvvm_crud.ui.fragments.main;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mvvm_crud.R;
import com.example.mvvm_crud.databinding.FragmentHomeBinding;
import com.example.mvvm_crud.model.ProductsModel;
import com.example.mvvm_crud.model.ResponseModel;
import com.example.mvvm_crud.ui.ItemClickListener;
import com.example.mvvm_crud.ui.adapters.products.ProductAdapter;
import com.example.mvvm_crud.ui.fragments.main.products.UpdateProductFragment;
import com.example.mvvm_crud.util.Constants;
import com.example.mvvm_crud.viewmodel.ProductViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dagger.Binds;
import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.HiltAndroidApp;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements ItemClickListener {

    private FragmentHomeBinding binding;
    private SharedPreferences sharedPreferences;
    private String username, userId, email, price;
    private ProductViewModel productViewModel;
    private ProductAdapter productAdapter;
    private GridLayoutManager gridLayoutManager;
    private List<ProductsModel> productsModelList;
    private BottomSheetBehavior bottomSheetBehavior;
    private String productId = null, image;

    private File file;

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
        setUpBottomSheet();
        getProduct();
    }

    private void getProduct() {
        binding.rvProduct.setAdapter(null);
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
                        productAdapter.notifyDataSetChanged();
                        productAdapter.setItemClickListener(HomeFragment.this);

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

        binding.overlay.setOnClickListener(view -> {
            hideBottomSheet();
        });

        binding.btnDelete.setOnClickListener(view -> {
            deleteProduct();

        });

        binding.fabInsert.setOnClickListener(view -> {
            showBottomSheet();
            binding.rlInsert.setVisibility(View.VISIBLE);
            binding.rlDetail.setVisibility(View.GONE);
        });

        binding.rlImagePicker.setOnClickListener(view -> {
            checkPermissionExternalStorage();
        });

        binding.btnSubmit.setOnClickListener(view -> {
            validateInsertData();
        });

        binding.btnEdit.setOnClickListener(view -> {
            Fragment fragment = new UpdateProductFragment();
            Bundle arg = new Bundle();
            arg.putString("id", productId);
            arg.putString("product_name", binding.tvProductName.getText().toString());
            arg.putString("price", price);
            arg.putString("desc", binding.tvDesc.getText().toString());
            arg.putString("image", image);
            fragment.setArguments(arg);
            getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.mainFrame, fragment)
                    .commit();
        });

    }

    private void checkPermissionExternalStorage() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            getContentLauncher.launch("image/*");
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showToast( "Yahh, izin ini dibutuhkan untuk mengakses file kamu");
            }
            // Meminta izin READ_EXTERNAL_STORAGE.
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
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


    private void insertData() {
        HashMap map = new HashMap();
        map.put("product_name", RequestBody.create(MediaType.parse("text/plain"), binding.etProductName.getText().toString()));
        map.put("price", RequestBody.create(MediaType.parse("text/plain"), binding.etProductPrice.getText().toString()));
        map.put("description", RequestBody.create(MediaType.parse("text/plain"), binding.etDesc.getText().toString()));

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part imageFlower = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
        productViewModel.insert(map, imageFlower).observeForever(new Observer<ResponseModel>() {
            @Override
            public void onChanged(ResponseModel responseModel) {
                if (responseModel.getStatus().equals(Constants.SUCCESS_RESPONSE)) {

                    showToast(responseModel.getMessage());
                    hideBottomSheet();
                    binding.etDesc.setText("");
                    binding.etProductPrice.setText("");
                    binding.etProductName.setText("");
                    file = null;
                    getProduct();
                }else {
                    showToast(responseModel.getMessage());
                }
            }
        });
    }


    private void validateInsertData() {
        if (binding.etProductName.getText().toString().isEmpty()) {
            binding.etProductName.setError("Tidak boleh kosong");
        }else if (binding.etProductPrice.getText().toString().isEmpty()) {
            binding.etProductPrice.setError("Tidak boleh kosong");
        }else if (binding.etDesc.getText().toString().isEmpty()) {
            binding.etDesc.setError("Tidak boleh kosong");
        }else if (file == null){
            showToast(Constants.SOMETHING_WENT_WRONG);
        }else {
            insertData();
        }
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

    private void showBottomSheet() {
        binding.overlay.setVisibility(View.VISIBLE);
        bottomSheetBehavior.setPeekHeight(600);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void hideBottomSheet() {
        binding.overlay.setVisibility(View.GONE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        binding.rlDetail.setVisibility(View.GONE);
        binding.rlInsert.setVisibility(View.GONE);
    }

    private void deleteProduct() {
        productViewModel.deleteProduct(productId).observe(getViewLifecycleOwner(), new Observer<ResponseModel>() {
            @Override
            public void onChanged(ResponseModel responseModel) {
                if (responseModel.getStatus().equals(Constants.SUCCESS_RESPONSE)) {
                    showToast(responseModel.getMessage());
                    getProduct();
                    hideBottomSheet();
                }else {
                    showToast(responseModel.getMessage());
                }
            }
        });
    }


    @Override
    public void onItemClick(Integer potion, Object model) {
        ProductsModel productsModel = (ProductsModel) model;
        showBottomSheet();

        // settext bottom sheet detail product
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        binding.rlDetail.setVisibility(View.VISIBLE);
        binding.tvProductName.setText(productsModel.getProduct_name());
        binding.tvPrice.setText("Rp. " + decimalFormat.format(Double.parseDouble(productsModel.getPrice())));
        binding.tvDesc.setText(productsModel.getDescription());
        Glide.with(getContext()).load(productsModel.getImage()).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivFlwer);

        // set value
        productId = productsModel.getId();
        image = productsModel.getImage();
        price = productsModel.getPrice();

        // validate null data
        if (potion == null && productsModel.getId() == null) {
            binding.btnEdit.setEnabled(false);
            binding.btnDelete.setEnabled(false);
        }




    }

    private ActivityResultLauncher<String> getContentLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            result -> {
                if (result != null) {
                    Uri uri = result;
                    String pdfPath = getRealPathFromUri(uri);
                    file = new File(pdfPath);
                    binding.ivProductSample.setVisibility(View.VISIBLE);
                    binding.ivProductSample.setImageURI(uri);
                    binding.tvImagePicker.setVisibility(View.GONE);

                } else {
                    showToast("Something went wrong");
                }
            }
    );

    public String getRealPathFromUri(Uri uri) {
        String filePath = "";
        if (getContext().getContentResolver() != null) {
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
                File file = new File(getContext().getCacheDir(), getFileName(uri));
                writeFile(inputStream, file);
                filePath = file.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (displayNameIndex != -1) {
                        result = cursor.getString(displayNameIndex);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void writeFile(InputStream inputStream, File file) throws IOException {
        OutputStream outputStream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    @Override
    public void onDestroy() {
        binding = null;
        super.onDestroy();
    }
}