package com.soniya.sellersapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class UserInformation {

    private String emailId;
    private String mobileNo;
    private String accountType;
    private String location;
    private ArrayList<String> activeOrders;
    private HashMap<String, Date> soldOrders;
    private String paymentStatus;
//    private ArrayList<String> paidforUsers;
    private ArrayList<String> paidforCarNumbers;

    public UserInformation(){
        this.emailId = "";
        this.mobileNo= "";
        this.accountType = "";
        this.location = "";
        this.activeOrders = new ArrayList<>();
        this.soldOrders = new HashMap<>();
        this.paymentStatus = "";
//        this.paidforUsers = new ArrayList<>();
        this.paidforCarNumbers = new ArrayList<>();
    }

    public UserInformation(String emailId, String location, String mobileNo)   {
        this.emailId = emailId;
        this.mobileNo = mobileNo;
        this.location = location;
        this.accountType = "";
        this.activeOrders = new ArrayList<>();
        this.soldOrders = new HashMap<>();
        this.paymentStatus = "";
//        this.paidforUsers = new ArrayList<>();
        this.paidforCarNumbers = new ArrayList<>();
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<String> getActiveOrders() {
        return activeOrders;
    }

    public void setActiveOrders(ArrayList<String> activeOrders) {
        this.activeOrders = activeOrders;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

//    public ArrayList<String> getPaidforUsers() {
//        return paidforUsers;
//    }
//
//    public void setPaidforUsers(ArrayList<String> paidforUsers) {
//        this.paidforUsers = paidforUsers;
//    }

    public ArrayList<String> getPaidforCarNumbers() {
        return paidforCarNumbers;
    }

    public void setPaidforCarNumbers(ArrayList<String> paidforCarNumbers) {
        this.paidforCarNumbers = paidforCarNumbers;
    }

    public HashMap<String, Date> getSoldOrders() {
        return soldOrders;
    }

    public void setSoldOrders(HashMap<String, Date> soldOrders) {
        this.soldOrders = soldOrders;
    }
}
