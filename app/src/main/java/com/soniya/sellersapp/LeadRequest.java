package com.soniya.sellersapp;

public class LeadRequest {

    private String lead_model;

    public String getLead_model() {
        return lead_model;
    }

    public void setLead_model(String lead_model) {
        this.lead_model = lead_model;
    }

    public String getLead_location() {
        return lead_location;
    }

    public void setLead_location(String lead_location) {
        this.lead_location = lead_location;
    }

    public String getLead_price() {
        return lead_price;
    }

    public void setLead_price(String lead_price) {
        this.lead_price = lead_price;
    }

    private String lead_location;
    private String lead_price;

    private String lead_brand;
    private String customer_name;

    public String getLead_brand() {
        return lead_brand;
    }

    public void setLead_brand(String lead_brand) {
        this.lead_brand = lead_brand;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    private String mobile_no;
    private String emailId;
}
