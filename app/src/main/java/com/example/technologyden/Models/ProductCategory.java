package com.example.technologyden.Models;

public class ProductCategory {

    private String productCategoryId;
    private String productCategoryImage;
    private String productCategoryName;
    private String storeProdCatID;

    public ProductCategory() {
    }

    public ProductCategory(String productCategoryId, String productCategoryImage, String productCategoryName, String storeProdCatID) {
        this.productCategoryId = productCategoryId;
        this.productCategoryImage = productCategoryImage;
        this.productCategoryName = productCategoryName;
        this.storeProdCatID = storeProdCatID;
    }

    public ProductCategory(String productCategoryImage, String productCategoryName, String storeProdCatID) {
        this.productCategoryImage = productCategoryImage;
        this.productCategoryName = productCategoryName;
        this.storeProdCatID = storeProdCatID;
    }

    public String getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(String productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public String getProductCategoryImage() {
        return productCategoryImage;
    }

    public void setProductCategoryImage(String productCategoryImage) {
        this.productCategoryImage = productCategoryImage;
    }

    public String getProductCategoryName() {
        return productCategoryName;
    }

    public void setProductCategoryName(String productCategoryName) {
        this.productCategoryName = productCategoryName;
    }

    public String getStoreProdCatID() {
        return storeProdCatID;
    }

    public void setStoreProdCatID(String storeProdCatID) {
        this.storeProdCatID = storeProdCatID;
    }
}
