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
    String foodCultureCategory;
    String address;
    double distance;
    Integer imageUrl;
    int plz;
    String aboutUsText;

    public NearbyRestaurants(Feature restaurantFeature, Point deviceLocation) {
        this.restaurantName = restaurantFeature.getProperty("title").getAsString();
        this.cityName = restaurantFeature.getProperty("city").getAsString();
        this.foodCultureCategory = restaurantFeature.getProperty("category").getAsString();
        this.address = restaurantFeature.getProperty("address").getAsString();
        Point restaurantLngLat = (Point)restaurantFeature.geometry();
        double distanceBetweenDeviceAndTarget = TurfMeasurement.distance(deviceLocation,
            Point.fromLngLat(restaurantLngLat.longitude(), restaurantLngLat.latitude()), UNIT_KILOMETERS);
        this.distance = round(distanceBetweenDeviceAndTarget*100.0)/100.0;
        this.imageUrl = R.drawable.placeholder_img1;
        this.plz = restaurantFeature.getProperty("PLZ").getAsInt();
        this.aboutUsText = restaurantFeature.getProperty("description").getAsString();
    }

    public String getAddress() {
        return address;
    }

    public String getFoodCultureCategory() {
        return foodCultureCategory;
    }

    public void setFoodCultureCategory(String foodCultureCategory) {
        this.foodCultureCategory = foodCultureCategory;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public int getPlz() {
        return plz;
    }

    public void setPlz(int plz) {
        this.plz = plz;
    }

    public String getAboutUsText() {
        return aboutUsText;
    }

    public void setAboutUsText(String aboutUsText) {
        this.aboutUsText = aboutUsText;
    }

}
