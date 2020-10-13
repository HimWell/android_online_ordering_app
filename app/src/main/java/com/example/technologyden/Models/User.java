package com.example.technologyden.Models;

public class User {

    private int id;
    private String email;
    private String password;
    private String name;
    private String phoneNumber;
    private String isAdmin;
    private String secureCode;

    public User() {
    }

    public User(String phoneNumber, String email, String isAdmin, String name, String password, String secureCode) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.isAdmin = isAdmin;
        this.name = name;
        this.password = password;
        this.secureCode = secureCode;
    }

    public User(String email, String isAdmin, String name, String password, String secureCode) {
        this.email = email;
        this.isAdmin = isAdmin;
        this.name = name;
        this.password = password;
        this.secureCode = secureCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }
}
