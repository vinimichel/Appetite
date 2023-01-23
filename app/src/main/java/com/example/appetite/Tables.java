package com.example.appetite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.appetite.adapter.Adapter_Table;
import com.example.appetite.dataModels.Table;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class Tables extends AppCompatActivity {

    ArrayList<Table> tables = new ArrayList<Table>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tables);
        setBottomNavigationItem();

        RecyclerView recyclerView = findViewById(R.id.myRecyclerView);

        Button addBtn = findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTable(7);
                updateView(recyclerView);
            }
        });
        Button deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTable();
                updateView(recyclerView);
            }
        });

        // setup Table Data HERE!!!!!!
        updateView(recyclerView);
        tables.add(new Table(5));
        tables.add(new Table(2));
        tables.add(new Table(6));
        updateView(recyclerView);

        //

       /* Adapter_Table adapter = new Adapter_Table(this, tables);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        */
    }

    public void updateView(RecyclerView recyclerView) {
        Adapter_Table adapter = new Adapter_Table(this, tables);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    public void addTable(int seatCount) {
        tables.add(new Table(seatCount));
    }

    public void deleteTable() {
        if (tables.size() > 0) {
            tables.get(0).deleteTable();
            tables.remove(tables.size() - 1);
        }
    }

    /*private void setupTables() {
        // setup Tables after launching the app from internal storage
    }*/

    private void setBottomNavigationItem() {
        NavigationBarView bottomNavigationView = (NavigationBarView)findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.tables);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeBtn:
                        startActivity(new Intent(getApplicationContext(), MainOwner.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.my_restaurant:
                        startActivity(new Intent(getApplicationContext(), MyRestaurant.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.tables:
                        return true;
                    case R.id.settingsBtn:
                        //startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

}