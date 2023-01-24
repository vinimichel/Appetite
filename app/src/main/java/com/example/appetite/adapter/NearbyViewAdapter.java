package com.example.appetite.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appetite.R;
import com.example.appetite.RestaurantDescription;
import com.example.appetite.dataModels.NearbyRestaurants;

import java.util.List;

public class NearbyViewAdapter extends RecyclerView.Adapter<NearbyViewAdapter.NearbyViewHolder> {

    Context context;
    List<NearbyRestaurants> nearbyRestaurantList;

    public NearbyViewAdapter(Context context, List<NearbyRestaurants> nearbyRestaurantList) {
        this.context = context;
        this.nearbyRestaurantList = nearbyRestaurantList;
    }

    // ViewHolder defines the attributes of items in the adapter
    public static final class NearbyViewHolder extends RecyclerView.ViewHolder {
        ImageView restaurantImage;
        TextView restaurantName, cityName, distanceField;
        public NearbyViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantImage = itemView.findViewById(R.id.restaurant_image);
            restaurantName = itemView.findViewById(R.id.restaurant_name);
            cityName = itemView.findViewById(R.id.restaurant_address);
            distanceField = itemView.findViewById(R.id.distance_field);
        }
    }

    @NonNull
    @Override
    public NearbyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.nearby_restaurants_item, parent, false);
        return new NearbyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyViewHolder holder, int position) {
        // define expressions of values in ViewHolder
        holder.restaurantName.setText(nearbyRestaurantList.get(position).getRestaurantName());
        holder.cityName.setText(nearbyRestaurantList.get(position).getCityName());
        if (nearbyRestaurantList.get(position).getDistance() >= 0.0) {
            holder.distanceField.setText(String.valueOf(nearbyRestaurantList.get(position).getDistance()));
        } else {
            holder.distanceField.setText("");
        }

        holder.restaurantImage.setImageResource(nearbyRestaurantList.get(position).getImageUrl());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent descriptionIntent = new Intent(context, RestaurantDescription.class);
                descriptionIntent.putExtra("restaurantData", nearbyRestaurantList.get(position));
                context.startActivity(descriptionIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return nearbyRestaurantList.size();
    }

}
