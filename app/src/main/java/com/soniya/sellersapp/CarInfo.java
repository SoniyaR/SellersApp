package com.soniya.sellersapp;

import java.util.ArrayList;

public class CarInfo {

    //vehicle_no	model_name	availability description	location	sellingprice
    private String vehicle_no;
    private String model_name;
    private String availability;
    private String description;
    private String location;
    private String sellingprice;
    private String imgUrl;
    private ArrayList<String> image_uri_list;

    public  CarInfo() {}

    public CarInfo(String vehicle_no, String model_name, String availability, String location, String sellingprice, String description){
        this.availability = availability;
        this.description = description;
        this.location = location;
        this.model_name = model_name;
        this.sellingprice = sellingprice;
        this.vehicle_no = vehicle_no;
    }

    public CarInfo(String vehicle_no, String model_name, String availability, String location, String sellingprice){
        this.availability = availability;
        this.location = location;
        this.model_name = model_name;
        this.sellingprice = sellingprice;
        this.vehicle_no = vehicle_no;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }


    public ArrayList<String> getImage_uri_list() {
        return image_uri_list;
    }

    public void setImage_uri_list(ArrayList<String> image_uri_list) {
        this.image_uri_list = image_uri_list;
    }

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
}
