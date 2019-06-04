package com.soniya.sellersapp;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class CarInfo {

    private String brand_name;
    private String vehicle_no;
    private String model_name;
    private String availability;
    private String description;
    private String location;
    private String sellingprice;
    private ArrayList<String> image_uri_list;
    private String fuelType;
    private String color;
    private String year;
    private String owner;
    private String thumbnailUriString;
    //
    private String kmsDriven;
    private String transmission;
    private String insurance;

    public String getKmsDriven() {
        return kmsDriven;
    }

    public void setKmsDriven(String kmsDriven) {
        this.kmsDriven = kmsDriven;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
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

    public ArrayList<String> getImage_uri_list() {
        return image_uri_list;
    }

    public void setImage_uri_list(ArrayList<String> image_uri_list) {
        this.image_uri_list = image_uri_list;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getThumbnailUriString() {
        return thumbnailUriString;
    }

    public void setThumbnailUriString(String thumbnailUriString) {
        this.thumbnailUriString = thumbnailUriString;
    }

    public CarInfo(String brand, String vehicleNum, String modelName, String availability, String location, String sellingprice,
                   ArrayList<String> imgUriList)    {
        this.brand_name = brand;
        this.vehicle_no = vehicleNum;
        this.model_name = modelName;
        this.availability = availability;
        this.sellingprice = sellingprice;
        this.image_uri_list = imgUriList;
        this.location = location;
    }

    //below code is custom listener code written for upload and retrieval of data (CarInfo List format)

    //retrieval data into arraylist

    public CarInfo(){

    }


}
