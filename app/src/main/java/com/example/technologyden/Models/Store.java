package com.example.technologyden.Models;

public class Store {

    String storeId;
    String storeName;
    String storeLocation;
    String storeNumber;
    String storeDelivery;
    String storeImage;

    public Store() {
    }

    public Store(String storeId, String storeName, String storeLocation, String storeNumber, String storeDelivery, String storeImage) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.storeLocation = storeLocation;
        this.storeNumber = storeNumber;
        this.storeDelivery = storeDelivery;
        this.storeImage = storeImage;
    }

    public Store(String storeName, String storeLocation, String storeNumber, String storeDelivery, String storeImage) {
        this.storeName = storeName;
        this.storeLocation = storeLocation;
        this.storeNumber = storeNumber;
        this.storeDelivery = storeDelivery;
        this.storeImage = storeImage;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(String storeLocation) {
        this.storeLocation = storeLocation;
    }

    public String getStoreNumber() {
        return storeNumber;
    }

    public void setStoreNumber(String storeNumber) {
        this.storeNumber = storeNumber;
    }

    public String getStoreDelivery() {
        return storeDelivery;
    }

    public void setStoreDelivery(String storeDelivery) {
        this.storeDelivery = storeDelivery;
    }

    public String getStoreImage() {
        return storeImage;
    }

    public void setStoreImage(String storeImage) {
        this.storeImage = storeImage;
    }
}
