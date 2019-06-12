package com.soniya.sellersapp;

import java.io.Serializable;
import java.util.ArrayList;

public class UserInfoSerial implements Serializable {

    private String emailId;
    private String mobileNo;
    private String accountType;
    private String location;
    private ArrayList<String> activeOrders;
    private String paymentStatus;
    //    private ArrayList<String> paidforUsers;
    private ArrayList<String> paidforCarNumbers;

    public UserInfoSerial(UserInformation info) {
        this.emailId = info.getEmailId();
        this.mobileNo= info.getMobileNo();
        this.accountType = info.getAccountType();
        this.location = info.getLocation();
        this.activeOrders = info.getActiveOrders();
        this.paymentStatus = info.getPaymentStatus();
//        this.paidforUsers = new ArrayList<>();
        this.paidforCarNumbers = info.getPaidforCarNumbers();
    }
    public UserInfoSerial(){
        this.emailId = "";
        this.mobileNo= "";
        this.accountType = "";
        this.location = "";
        this.activeOrders = new ArrayList<>();
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

    public ArrayList<String> getPaidforCarNumbers() {
        return paidforCarNumbers;
    }

    public void setPaidforCarNumbers(ArrayList<String> paidforCarNumbers) {
        this.paidforCarNumbers = paidforCarNumbers;
    }
}
