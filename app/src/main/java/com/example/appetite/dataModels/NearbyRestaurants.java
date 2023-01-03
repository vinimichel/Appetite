package com.example.appetite.dataModels;

import static com.mapbox.turf.TurfConstants.UNIT_KILOMETERS;
import static java.lang.Math.round;

import com.example.appetite.R;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.turf.TurfMeasurement;
import java.io.Serializable;

public class NearbyRestaurants implements Serializable {
    String restaurantName;
    String cityName;
    String foodCategory;
    double distance;
    Integer imageUrl;
    //public Feature restaurantFeature;


    public NearbyRestaurants(Feature restaurantFeature, Point deviceLocation) {
        //this.restaurantFeature = restaurantFeature;
        this.restaurantName = restaurantFeature.getProperty("title").getAsString();
        this.cityName = restaurantFeature.getProperty("city").getAsString();
        this.foodCategory = restaurantFeature.getProperty("category").getAsString();
        Point restaurantLngLat = (Point)restaurantFeature.geometry();
        double distanceBetweenDeviceAndTarget = TurfMeasurement.distance(deviceLocation,
                Point.fromLngLat(restaurantLngLat.longitude(), restaurantLngLat.latitude()), UNIT_KILOMETERS);
        this.distance = round(distanceBetweenDeviceAndTarget*100.0)/100.0;
        this.imageUrl = R.drawable.placeholder_img1;
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
