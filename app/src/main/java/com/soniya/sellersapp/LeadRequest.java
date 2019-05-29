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
}
