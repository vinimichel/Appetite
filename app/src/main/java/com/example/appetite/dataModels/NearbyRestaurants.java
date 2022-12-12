package com.example.appetite.dataModels;

public class NearbyRestaurants {
    String restaurantName;
    String cityName;
    double distance;
    Integer imageUrl;



    public NearbyRestaurants(String restaurantName, String cityName, double distance, Integer imageUrl) {
        this.restaurantName = restaurantName;
        this.cityName = cityName;
        this.distance = distance;
        this.imageUrl = imageUrl;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Integer getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(Integer imageUrl) {
        this.imageUrl = imageUrl;
    }
}
