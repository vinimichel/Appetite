package com.example.appetite;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.appetite.dataModels.NearbyRestaurants;

public class RestaurantDescription extends AppCompatActivity {

    NearbyRestaurants restaurantData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_description);
        restaurantData = (NearbyRestaurants)getIntent().getSerializableExtra("restaurantData");
        setRestaurantText();
    }

    private void setRestaurantText() {
        ((TextView)findViewById(R.id.restaurant_name_in_description_activity)).setText(restaurantData.getRestaurantName());


    }
}