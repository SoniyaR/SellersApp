package com.soniya.sellersapp;

import java.util.ArrayList;

public class CarInfo {

    private String vehicle_no;
    private String model_name;
    private String availability;
    private String description;

    public String getVehicle_no() {
        return vehicle_no;
    }

    public void setVehicle_no(String vehicle_no) {
        this.vehicle_no = vehicle_no;
    }

    public String getModel_name() {
        return model_name;
    }

    public void setModel_name(String model_name) {
        this.model_name = model_name;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSellingprice() {
        return sellingprice;
    }

    public void setSellingprice(String sellingprice) {
        this.sellingprice = sellingprice;
    }

    public ArrayList<String> getImage_uri_list() {
        return image_uri_list;
    }

    public void setImage_uri_list(ArrayList<String> image_uri_list) {
        this.image_uri_list = image_uri_list;
    }

    private String location;
    private String sellingprice;
    private ArrayList<String> image_uri_list;

}
