package com.example.mvvm_crud.model;

import com.example.mvvm_crud.util.Constants;

public class ProductsModel {
    private String id;
    private String product_name;
    private String price;
    private String description;
    private String image;

    public ProductsModel(String id, String product_name, String price, String description, String image) {
        this.product_name = product_name;
        this.price = price;
        this.description = description;
        this.image = image;
        this.id = id;
    }


    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return Constants.STORAGE_IMAGE + image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
