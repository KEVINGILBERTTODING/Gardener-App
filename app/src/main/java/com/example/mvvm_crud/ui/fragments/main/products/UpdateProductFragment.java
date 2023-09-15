package com.example.mvvm_crud.ui.fragments.main.products;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mvvm_crud.R;
import com.example.mvvm_crud.databinding.FragmentUpdateProductBinding;
import com.example.mvvm_crud.model.ResponseModel;
import com.example.mvvm_crud.util.Constants;
import com.example.mvvm_crud.viewmodel.ProductViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@AndroidEntryPoint
public class UpdateProductFragment extends Fragment {

   private FragmentUpdateProductBinding binding;
   private ProductViewModel productViewModel;
   private Integer isUpdate = 0;
   private File file;
   private String productId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUpdateProductBinding.inflate(inflater, container, false);

        init();
        return binding.getRoot();
    }

    private void init() {
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        productId = getArguments().getString("id", null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listener();
        binding.etProductName.setText(getArguments().getString("product_name"));
        binding.etPrice.setText(getArguments().getString("price"));
        binding.etDesc.setText(getArguments().getString("desc"));

        Glide.with(getContext()).load(getArguments().getString("image"))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivProduct);

        if (productId == null) {
            binding.btnSave.setEnabled(false);
            showToast(Constants.SOMETHING_WENT_WRONG);
        }
    }

    private void updateData() {
        HashMap map = new HashMap();
        map.put("product_id", RequestBody.create(MediaType.parse("text/plain"), productId));
        map.put("product_name", RequestBody.create(MediaType.parse("text/plain"), binding.etProductName.getText().toString()));
        map.put("price", RequestBody.create(MediaType.parse("text/plain"), binding.etPrice.getText().toString()));
        map.put("description", RequestBody.create(MediaType.parse("text/plain"), binding.etDesc.getText().toString()));
        if (isUpdate == 1) { // IMAGE UPDATE
            map.put("is_image", RequestBody.create(MediaType.parse("text/plain"), "1"));
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part photoProduct = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
            productViewModel.update(map, photoProduct).observe(getViewLifecycleOwner(), new Observer<ResponseModel>() {
                @Override
                public void onChanged(ResponseModel responseModel) {
                    if (responseModel.getStatus().equals(Constants.SUCCESS_RESPONSE)) {
                        showToast(responseModel.getMessage());
                        getActivity().onBackPressed();

                    }else {
                        showToast(responseModel.getMessage());
                    }
                }
            });

        }else { // UPDATE WITHOUT IMAGE
            map.put("is_image", RequestBody.create(MediaType.parse("text/plain"), "0"));
           productViewModel.updateData(map).observe(getViewLifecycleOwner(), new Observer<ResponseModel>() {
               @Override
               public void onChanged(ResponseModel responseModel) {
                   if (responseModel.getStatus().equals(Constants.SUCCESS_RESPONSE)) {
                       showToast(responseModel.getMessage());
                       getActivity().onBackPressed();

                   }else {
                       showToast(responseModel.getMessage());
                   }
               }
           });
        }



    }

    private void listener() {
        binding.btnBack.setOnClickListener(view -> {
            getActivity().onBackPressed();
        });

        binding.btnSave.setOnClickListener(view -> {
            formValidate();
        });
        binding.ivProduct.setOnClickListener(view -> {
            checkPermissionExternalStorage();
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

    private void formValidate() {
        if (productId == null) {
            showToast(Constants.SOMETHING_WENT_WRONG);
        }else if (binding.etProductName.getText().toString().isEmpty()) {
            showToast("Product name cannot be empty");
        }else if (binding.etPrice.getText().toString().isEmpty()) {
            showToast("Price cannot be empty");
        }else if (binding.etDesc.getText().toString().isEmpty()) {
            showToast("Description cannot be empty");

        }else {
            updateData();
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    private ActivityResultLauncher<String> getContentLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            result -> {
                if (result != null) {
                    Uri uri = result;
                    String pdfPath = getRealPathFromUri(uri);
                    file = new File(pdfPath);
                    isUpdate = 1;
                    binding.ivProduct.setImageURI(uri);

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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        binding = null;
        super.onDestroy();
    }
}