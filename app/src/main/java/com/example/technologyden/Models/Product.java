package com.example.technologyden.Models;

public class Product {

    private String productId;
    private String prodCatId;
    private String productDescription;
    private String productDiscountPrice;
    private String productImageURL;
    private String productName;
    private String productPrice;
    private String productStatus;

    public Product() {
    }

    public Product(String productId, String prodCatId, String productDescription, String productDiscountPrice, String productImageURL, String productName, String productPrice, String productStatus) {
        this.productId = productId;
        this.prodCatId = prodCatId;
        this.productDescription = productDescription;
        this.productDiscountPrice = productDiscountPrice;
        this.productImageURL = productImageURL;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productStatus = productStatus;
    }

    public Product(String prodCatId, String productDescription, String productDiscountPrice, String productImageURL, String productName, String productPrice, String productStatus) {
        this.prodCatId = prodCatId;
        this.productDescription = productDescription;
        this.productDiscountPrice = productDiscountPrice;
        this.productImageURL = productImageURL;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productStatus = productStatus;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProdCatId() {
        return prodCatId;
    }

    public void setProdCatId(String prodCatId) {
        this.prodCatId = prodCatId;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductDiscountPrice() {
        return productDiscountPrice;
    }

    public void setProductDiscountPrice(String productDiscountPrice) {
        this.productDiscountPrice = productDiscountPrice;
    }

    public String getProductImageURL() {
        return productImageURL;
    }

    public void setProductImageURL(String productImageURL) {
        this.productImageURL = productImageURL;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }
}
