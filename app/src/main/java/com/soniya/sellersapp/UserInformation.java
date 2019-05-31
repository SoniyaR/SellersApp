package com.soniya.sellersapp;

public class UserInformation {

    private String emailId;
    private String mobileNo;
    private String accountType;
    private String location;

    public UserInformation(){

    }

    public UserInformation(String emailId, String mobileNo, String accountType, String location)   {
        this.emailId = emailId;
        this.mobileNo = mobileNo;
        this.accountType = accountType;
        this.location = location;
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
}
