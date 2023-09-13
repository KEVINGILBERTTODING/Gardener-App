package com.example.mvvm_crud.ui.adapters.products;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mvvm_crud.R;
import com.example.mvvm_crud.model.ProductsModel;
import com.example.mvvm_crud.model.UsersModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private Context context;
    private List<ProductsModel> productsModelList;

    public ProductAdapter(Context context, List<ProductsModel> productsModelList) {
        this.context = context;
        this.productsModelList = productsModelList;
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_producy, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        holder.tvProductName.setText(productsModelList.get(holder.getAdapterPosition()).getProduct_name());
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        holder.tvPrice.setText("Rp. " + decimalFormat.format( Double.parseDouble(productsModelList.get(holder.getAdapterPosition()).getPrice())));

        Glide.with(context).load(productsModelList.get(holder.getAdapterPosition()).getImage())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(holder.ivProduct);

    }

    @Override
    public int getItemCount() {
        return productsModelList.size() ;
    }

    public void filter(ArrayList<ProductsModel> filteredList) {
        productsModelList = filteredList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPrice, tvProductName;
        private ImageView ivProduct;
        private Button btnDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            btnDetail = itemView.findViewById(R.id.btnDetail);
        }
    }
}
