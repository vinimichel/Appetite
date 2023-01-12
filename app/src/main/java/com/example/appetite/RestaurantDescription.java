package com.example.appetite;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
        String fullAddress = restaurantData.getAddress() + ",\n" + restaurantData.getCityName() +  ", " + restaurantData.getPlz();
        ((TextView)findViewById(R.id.restaurant_address)).setText(fullAddress);
        ((TextView)findViewById(R.id.about_us)).setText(restaurantData.getAboutUsText());
        ((TextView)findViewById(R.id.category)).setText(restaurantData.getFoodCultureCategory());
    }

    public void goBack(View view) {
        finish();
    }

}
